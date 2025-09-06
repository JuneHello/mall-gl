package com.siro.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.siro.common.constant.CartConstant;
import com.siro.common.utils.R;
import com.siro.gulimall.cart.feign.ProductFeignService;
import com.siro.gulimall.cart.interceptor.CartInterceptor;
import com.siro.gulimall.cart.service.CartService;
import com.siro.gulimall.cart.to.UserInfoTo;
import com.siro.gulimall.cart.vo.CartItemVo;
import com.siro.gulimall.cart.vo.CartVo;
import com.siro.gulimall.cart.vo.SkuInfoVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author starsea
 * @date 2022-05-05
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    /**
     * 获取购物车商品信息
     * @return
     */
    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        if (userInfoTo.getUserId() != null) {
            //用户已登录
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            //临时购物车的键
            String temptCartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();

            //如果临时购物车的数据还未进行合并
            List<CartItemVo> tempCartItems = getCartItems(temptCartKey);
            if (tempCartItems != null) {
                //临时购物车有数据需要进行合并操作
                for (CartItemVo item : tempCartItems) {
                    addToCart(item.getSkuId(), item.getCount());
                }
                //清除临时购物车的数据
                clearCartInfo(temptCartKey);
            }

            //获取登录后的购物车数据【包含合并过来的临时购物车的数据和登录后购物车的数据】
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);

            //设置选中商品数量
            if (cartItems != null && cartItems.size() > 0) {
                int count = cartItems.stream().filter(obj -> {
                    return obj.getCheck() == true;
                }).mapToInt(CartItemVo::getCount).sum();
                cartVo.setCheckedNum(count);
            }
        } else {
            //用户未登录
            String userKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            //获取临时购物车的所有购物项
            List<CartItemVo> cartItems = getCartItems(userKey);
            cartVo.setItems(cartItems);
        }

        return cartVo;
    }

    /**
     * 清空临时购物车数据
     * @param cartKey
     */
    @Override
    public void clearCartInfo(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    /**
     * 勾选购物项
     * @param skuId
     * @param check
     * @return
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1 ?true:false);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), s);
    }

    /**
     * 修改购物项数量
     * @param skuId
     * @param num
     */
    @Override
    public void countItem(Long skuId, Integer num) {
        //TODO 页面数量减少到0，应该删除该商品
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    /**
     * 删除某件商品
     * @param skuId
     * @return
     */
    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    /**
     * 获取购物车中所有选中的数据
     * @return
     */
    @Override
    public List<CartItemVo> getCurrentUserCartItem() {
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            String userKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            List<CartItemVo> cartItems = getCartItems(userKey);
            //获取所有被选中的购物项
            List<CartItemVo> collect = cartItems.stream().filter(item -> {
                return item.getCheck();
            }).map(item -> {
                //更新为最新的价格
                R price = productFeignService.getPrice(item.getSkuId());
                String data = (String) price.get("data");
                item.setPrice(new BigDecimal(data));
                return item;
            }).collect(Collectors.toList());
            return collect;
        }
    }

    /**
     * 获取购物车里面的数据
     * @param cartKey
     * @return
     */
    private List<CartItemVo> getCartItems(String cartKey) {
        //获取购物车里面的所有商品
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            List<CartItemVo> collect = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItemVo cartItem = JSON.parseObject(str, CartItemVo.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        //查询redis是否有当前商品
        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
            //购物车无此商品，进行添加

            //2.添加新商品到购物车
            CartItemVo cartItemVo = new CartItemVo();

            //1.远程查询当前要添加的商品的信息
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R skuInfo = productFeignService.getSkuInfo(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });

                cartItemVo.setCheck(true);
                cartItemVo.setCount(num);
                cartItemVo.setImage(data.getSkuDefaultImg());
                cartItemVo.setPrice(data.getPrice());
                cartItemVo.setTitle(data.getSkuTitle());
                cartItemVo.setSkuId(skuId);
            }, executor);

            //3.远程查询sku的组合信息
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVo.setSkuAttrValues(skuSaleAttrValues);
            }, executor);

            //等待异步都完成在执行后续
            CompletableFuture.allOf(getSkuInfoTask,getSkuSaleAttrValues).get();
            //4.保存到redis中
            String s = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(),s);

            return cartItemVo;
        } else {
            //购物车有此商品，修改数量
            // TODO 个人觉得有问题：如果后台修改了价格呢，或者删除了这件商品呢。
            CartItemVo cartItemVo = JSON.parseObject(res, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount() + num);
            cartOps.put(skuId.toString(),JSON.toJSONString(cartItemVo));
            return cartItemVo;
        }
    }

    /**
     * 获取购物车中某个购物项
     * @param skuId
     * @return
     */
    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String str = (String) cartOps.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(str, CartItemVo.class);
        return cartItemVo;
    }

    /**
     * 确认用户是否登录来指定是临时购物车还是真是购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        //指定是临时购物车还是真实购物车
        String cartKey = "";//redis中存储的key
        if (userInfoTo.getUserId() != null) {
            //用户登录了
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        } else {
            //用户没登录
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
        }

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }
}
