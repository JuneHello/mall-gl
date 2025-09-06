package com.siro.gulimall.cart.service;

import com.siro.gulimall.cart.vo.CartItemVo;
import com.siro.gulimall.cart.vo.CartVo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author starsea
 * @date 2022-05-05
 */
public interface CartService {

    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @return
     */
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物车中某个购物项
     * @param skuId
     * @return
     */
    CartItemVo getCartItem(Long skuId);

    /**
     * 获取购物车商品信息
     * @return
     */
    CartVo getCart() throws ExecutionException, InterruptedException;

    /**
     * 清空临时购物车数据
     * @param cartKey
     */
    void clearCartInfo(String cartKey);

    /**
     * 勾选购物项
     * @param skuId
     * @param check
     * @return
     */
    void checkItem(Long skuId, Integer check);

    /**
     * 修改购物项数量
     * @param skuId
     * @param num
     */
    void countItem(Long skuId, Integer num);

    /**
     * 删除某件商品
     * @param skuId
     * @return
     */
    void deleteItem(Long skuId);

    /**
     * 获取购物车中所有选中的数据
     * @return
     */
    List<CartItemVo> getCurrentUserCartItem();
}
