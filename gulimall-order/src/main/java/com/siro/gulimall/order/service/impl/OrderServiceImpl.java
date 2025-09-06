package com.siro.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.siro.common.constant.CartConstant;
import com.siro.common.constant.OrderConstant;
import com.siro.common.constant.OrderStatusEnum;
import com.siro.common.exception.NoStockException;
import com.siro.common.to.mq.OrderTo;
import com.siro.common.to.mq.SeckillOrderTo;
import com.siro.common.utils.R;
import com.siro.common.vo.MemberRespVo;
import com.siro.gulimall.order.entity.OrderItemEntity;
import com.siro.gulimall.order.entity.PaymentInfoEntity;
import com.siro.gulimall.order.feign.CartFeignService;
import com.siro.gulimall.order.feign.MemberFeignService;
import com.siro.gulimall.order.feign.ProductFeignService;
import com.siro.gulimall.order.feign.WareFeignService;
import com.siro.gulimall.order.interceptor.LoginUserInterceptor;
import com.siro.gulimall.order.service.OrderItemService;
import com.siro.gulimall.order.service.PaymentInfoService;
import com.siro.gulimall.order.to.OrderCreateTo;
import com.siro.gulimall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siro.common.utils.PageUtils;
import com.siro.common.utils.Query;

import com.siro.gulimall.order.dao.OrderDao;
import com.siro.gulimall.order.entity.OrderEntity;
import com.siro.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberFeignService memberFeignService;
    
    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PaymentInfoService paymentInfoService;

    /**
     * 在同一个类里面，编写两个方法，内部调用的时候，会导致事务设置失效。原因是没有用到代理对象的缘故。
     * 事务设置失效，同一个对象内事务方法互相调用，原因是绕过了代理对象，事务是使用代理对象来控制的。
     */
    @Transactional(timeout = 30)
    public void a() {
        OrderServiceImpl orderService = (OrderServiceImpl) AopContext.currentProxy();
        orderService.a();
        orderService.b();

//        aService.b(); // 和a用同一个事务，如果a事务出现异常回滚，b事务也回滚，超时时间以a为主
//        cService.c(); // 新事务，如果a事务回滚，c事务并不会回滚
    }
    @Transactional(propagation = Propagation.REQUIRED,timeout = 2)
    public void b() {

    }
    @Transactional(propagation = Propagation.REQUIRES_NEW,timeout = 20)
    public void c() {

    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 订单确认页返回需要用的数据
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();

        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        // TODO p268 异步情况下保证主线程的 ThreadLocal 和附线程的用同一个线程
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();

        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //1、远程查询所有会员的收货地址信息
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setMemberAddressVos(address);
        }, executor);

        /**
         * 存在问题：Feign远程调用丢失请求头问题
         * feign在远程调用之前要构造请求，调用了很多的拦截器
         *
         * 1.浏览器发送请求，请求头自动带来了cookie 到order服务
         * 2.order服务通过feign远程调用 cart服务
         *  feign会创建一个新的request【这个请求没有带任何请求头】
         *  没有带请求头，cart服务就认为没登录
         *
         * 解决：加上feign远程调用的请求拦截器
         */
        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //2、远程查询购物车所有选中的购物项信息
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItem();
            confirmVo.setItems(items);
        }, executor).thenRunAsync(() -> {
            //获取全部商品的id
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> skuIds = items.stream().map(item -> {
                return item.getSkuId();
            }).collect(Collectors.toList());

            //5、远程查询商品库存信息
            R skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            List<SkuStockVo> skuStockVos = skuHasStock.getData("data", new TypeReference<List<SkuStockVo>>() {});
            if (skuStockVos != null && skuStockVos.size() > 0) {
                //将skuStockVos集合转换为map
                Map<Long, Boolean> skuHasStockMap = skuStockVos.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(skuHasStockMap);
            }
        }, executor);

        //3、查询用户积分信息
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);

        //4、价格数据自动计算

        //TODO 6、防重令牌(防止表单重复提交)
        //为用户设置一个token，三十分钟过期时间（存在redis）
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId(),token,30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);

        CompletableFuture.allOf(addressFuture, cartFuture).get();
        return confirmVo;
    }

    /**
     * TODO 下单操作
     *
     * 如果使用异常机制和本地事务，存在的问题：
     *  1. 远程服务其实成功了，由于网络等故障没有返回。导致：订单回滚，库存却扣减。
     *  2. 远程服务执行完成，下面的其他方法出现了问题。导致：已执行的远程请求【库存】肯定不会回滚。
     *
     * @Transactional：本地事务。在分布式系统，只能控制住自己的回滚，控制不了其他服务的回滚。
     * 所以使用 分布式事务。最大原因：网络问题+分布式机器。
     *
     * 柔性事务 - 可靠消息 + 最终一致性方案（异步确保型）
     *
     * @param orderSubmitVo
     * @return
     */
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        //去创建订单，验令牌，验价格，锁库存....
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setCode(0);

        //保证同一线程数据是一致的
        confirmVoThreadLocal.set(orderSubmitVo);

        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        //1、验证令牌【令牌的对比和删除必须保证原子性】
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = orderSubmitVo.getOrderToken();
        //通过lure脚本原子验证令牌和删除令牌
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if (result == 0L) {
            //验证令牌失败
            responseVo.setCode(1);
            return responseVo;
        } else {
            //验证令牌成功
            //2、创建订单、订单项等信息
            OrderCreateTo order = createOrder();

            //3、验证价格
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = orderSubmitVo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //金额对比
                //4、保存订单
                saveOrder(order);

                //5、库存锁定，只要有异常，回滚订单数据
                //订单号、所有订单项信息(skuId,skuNum,skuName)
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());

                //获取出要锁定的商品数据信息
                List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map((item) -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(orderItemVos);

                //TODO 调用远程锁定库存的方法【利用最终一致性】
                //出现的问题：扣减库存成功了，但是由于网络原因超时，出现异常，导致订单事务回滚，库存事务不回滚(解决方案：seata)
                //为了保证高并发，不推荐使用seata，因为是加锁，并行化，提升不了效率,可以发消息给库存服务
                R r = wareFeignService.orderLockStock(lockVo);
                if (r.getCode() == 0) {
                    //锁定成功
                    responseVo.setOrder(order.getOrder());

                    // TODO 远程扣减积分
//                     int i = 10/0; //订单回滚，库存不回滚

                    //订单创建成功，发送消息给MQ
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order.getOrder());

                    //删除购物车里的数据
                    redisTemplate.delete(CartConstant.CART_PREFIX + memberRespVo.getId());
                    return responseVo;
                } else {
                    //锁定失败
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                    //responseVo.setCode(3);
                    //return responseVo;
                }
            } else {
                //验价失败
                responseVo.setCode(2);
                return responseVo;
            }
        }
    }

    /**
     * 根据订单号查询订单数据
     * @return
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    /**
     * MQ关闭订单
     * @param entity
     */
    @Override
    public void closeOrder(OrderEntity entity) {
        //查询当前这个订单的最新状态
        OrderEntity orderEntity = getById(entity.getId());
        if(orderEntity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()){
            OrderEntity update = new OrderEntity();
            update.setId(entity.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(update);

            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity, orderTo);
            //如果订单创建成功后由于机器卡顿，消息延迟等原因，订单还没解锁但是先执行了，然后这个消息消费完了
            //这是订单解锁了，但是库存永远得不到释放。所以要发送给MQ
            try {
                //TODO 保证消息一定发送出去，每一个消息都可以做好日志记录（给数据库保存每一个消息的详细信息）
                //TODO 定期扫描数据库将失败的消息再发送一遍。
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
            } catch (Exception e) {
                //TODO 将没法送成功的消息进行重试发送。
            }
        }
    }

    /**
     * 获取当前订单的支付信息
     * @param orderSn
     * @return
     */
    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity orderEntity = this.getOrderByOrderSn(orderSn);

        BigDecimal bigDecimal = orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(bigDecimal.toString());
        payVo.setOut_trade_no(orderEntity.getOrderSn());

        List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity entity = order_sn.get(0);
        payVo.setSubject(entity.getSkuName());
        payVo.setBody(entity.getSkuAttrsVals());

        return payVo;
    }

    /**
     * 查询当前登录用户的所有订单信息
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",memberRespVo.getId()).orderByDesc("id")
        );

        List<OrderEntity> order_sn = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> itemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setOrderItemEntityList(itemEntities);
            return order;
        }).collect(Collectors.toList());

        page.setRecords(order_sn);

        return new PageUtils(page);
    }

    /**
     * 处理支付宝支付的结果
     * @param payAsyncVo
     * @return
     */
    @Override
    public String handlePayResult(PayAsyncVo payAsyncVo) {
        //1、保存交易流水信息
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setOrderSn(payAsyncVo.getOut_trade_no());
        paymentInfoEntity.setAlipayTradeNo(payAsyncVo.getTrade_no());
        paymentInfoEntity.setPaymentStatus(payAsyncVo.getTrade_status());
        paymentInfoEntity.setCallbackTime(payAsyncVo.getNotify_time());
        paymentInfoService.save(paymentInfoEntity);

        //2、修改订单状态
        if (payAsyncVo.getTrade_status().equals("TRADE_SUCCESS") || payAsyncVo.getTrade_status().equals("TRADE_FINISHED")) {
            //支付成功状态
            String outTradeNo = payAsyncVo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(outTradeNo, OrderStatusEnum.PAYED.getCode());
        }

        return "success";
    }

    /**
     * 处理秒杀
     * @param orderTo
     */
    @Override
    public void createSeckillOrder(SeckillOrderTo orderTo) {
        //TODO 保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderTo.getOrderSn());
        orderEntity.setMemberId(orderTo.getMemberId());
        orderEntity.setCreateTime(new Date());
        BigDecimal totalPrice = orderTo.getSeckillPrice().multiply(BigDecimal.valueOf(orderTo.getNum()));
        orderEntity.setPayAmount(totalPrice);
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());

        //保存订单
        this.save(orderEntity);

        //保存订单项信息
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrderSn(orderTo.getOrderSn());
        orderItem.setRealAmount(totalPrice);

        orderItem.setSkuQuantity(orderTo.getNum());

        //保存商品的spu信息
        R spuInfo = productFeignService.getSpuInfoBySkuId(orderTo.getSkuId());
        SpuInfoVo spuInfoData = spuInfo.getData("data", new TypeReference<SpuInfoVo>() {
        });
        orderItem.setSpuId(spuInfoData.getId());
        orderItem.setSpuName(spuInfoData.getSpuName());
        orderItem.setSpuBrand(spuInfoData.getBrandName());
        orderItem.setCategoryId(spuInfoData.getCatalogId());

        //保存订单项数据
        orderItemService.save(orderItem);
    }

    /**
     * 创建订单
     * @return
     */
    private OrderCreateTo createOrder() {
        OrderCreateTo createTo = new OrderCreateTo();

        //生成订单号
        String orderSn = IdWorker.getTimeId();

        //1、构建订单数据
        OrderEntity orderEntity = builderOrder(orderSn);

        //2、获取到所有的订单项
        List<OrderItemEntity> orderItemEntities = builderOrderItems(orderSn);

        //3、验价(计算价格、积分等信息)
        computePrice(orderEntity, orderItemEntities);

        createTo.setOrder(orderEntity);
        createTo.setOrderItems(orderItemEntities);

        return createTo;
    }

    /**
     * 构建订单数据
     * @param orderSn 订单号
     * @return
     */
    private OrderEntity builderOrder(String orderSn) {
        //获取当前用户登录信息
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(memberRespVo.getId());
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberUsername(memberRespVo.getUsername());

        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();

        //远程获取收货地址和运费信息
        R fareAddressVo = wareFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareResp = fareAddressVo.getData("data", new TypeReference<FareVo>() {});

        //获取到运费信息
        orderEntity.setFreightAmount(fareResp.getFare());

        //获取到收货地址信息
        MemberAddressVo address = fareResp.getAddress();
        //设置收货人信息
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());

        //设置订单相关的状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setConfirmStatus(0);

        return orderEntity;
    }

    /**
     * 构建所有订单项数据
     * @return
     */
    public List<OrderItemEntity> builderOrderItems(String orderSn) {
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();

        //最后确定每个购物项的价格
        List<OrderItemVo> currentCartItems = cartFeignService.getCurrentUserCartItem();
        if (currentCartItems != null && currentCartItems.size() > 0) {
            orderItemEntityList = currentCartItems.stream().map((items) -> {
                //构建订单项数据
                OrderItemEntity orderItemEntity = builderOrderItem(items);
                orderItemEntity.setOrderSn(orderSn);//设置订单号

                return orderItemEntity;
            }).collect(Collectors.toList());
        }

        return orderItemEntityList;
    }

    /**
     * 构建某一个订单项的数据
     * @param items
     * @return
     */
    private OrderItemEntity builderOrderItem(OrderItemVo items) {

        OrderItemEntity orderItemEntity = new OrderItemEntity();

        //1、商品的spu信息
        Long skuId = items.getSkuId();
        //获取spu的信息
        R spuInfo = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo spuInfoData = spuInfo.getData("data", new TypeReference<SpuInfoVo>() {
        });
        orderItemEntity.setSpuId(spuInfoData.getId());
        orderItemEntity.setSpuName(spuInfoData.getSpuName());
        orderItemEntity.setSpuBrand(spuInfoData.getBrandName());
        orderItemEntity.setCategoryId(spuInfoData.getCatalogId());

        //2、商品的sku信息
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(items.getTitle());
        orderItemEntity.setSkuPic(items.getImage());
        orderItemEntity.setSkuPrice(items.getPrice());
        orderItemEntity.setSkuQuantity(items.getCount());
        //使用StringUtils.collectionToDelimitedString将list集合转换为String
        String skuAttrValues = StringUtils.collectionToDelimitedString(items.getSkuAttrValues(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttrValues);

        //3、商品的优惠信息

        //4、商品的积分信息
        orderItemEntity.setGiftGrowth(items.getPrice().multiply(new BigDecimal(items.getCount())).intValue());
        orderItemEntity.setGiftIntegration(items.getPrice().multiply(new BigDecimal(items.getCount())).intValue());

        //5、订单项的价格信息
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);

        //当前订单项的实际金额.总额 - 各种优惠价格
        //原来的价格
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        //原价减去优惠价得到最终的价格
        BigDecimal subtract = origin.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(subtract);

        return orderItemEntity;
    }

    /**
     * 计算价格的方法
     * @param orderEntity
     * @param orderItemEntities
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        //总价
        BigDecimal total = new BigDecimal("0.0");
        //优惠价
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        //积分、成长值
        Integer integrationTotal = 0;
        Integer growthTotal = 0;

        //订单总额，叠加每一个订单项的总额信息
        for (OrderItemEntity orderItem : orderItemEntities) {
            //优惠价格信息
            coupon = coupon.add(orderItem.getCouponAmount());
            promotion = promotion.add(orderItem.getPromotionAmount());
            intergration = intergration.add(orderItem.getIntegrationAmount());

            //总价
            total = total.add(orderItem.getRealAmount());

            //积分信息和成长值信息
            integrationTotal += orderItem.getGiftIntegration();
            growthTotal += orderItem.getGiftGrowth();
        }

        //1、订单价格相关的
        orderEntity.setTotalAmount(total);
        //设置应付总额(总额+运费)
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(intergration);

        //设置积分成长值信息
        orderEntity.setIntegration(integrationTotal);
        orderEntity.setGrowth(growthTotal);

        //设置删除状态(0-未删除，1-已删除)
        orderEntity.setDeleteStatus(0);
    }

    /**
     * 保存订单所有数据
     * @param orderCreateTo
     */
    private void saveOrder(OrderCreateTo orderCreateTo) {
        //获取订单信息
        OrderEntity order = orderCreateTo.getOrder();
        order.setModifyTime(new Date());
        order.setCreateTime(new Date());
        //保存订单
        this.baseMapper.insert(order);

        //获取订单项信息
        List<OrderItemEntity> orderItems = orderCreateTo.getOrderItems();
        //批量保存订单项数据
        orderItemService.saveBatch(orderItems);
    }

}