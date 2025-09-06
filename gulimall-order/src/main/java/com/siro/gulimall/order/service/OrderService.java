package com.siro.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.to.mq.SeckillOrderTo;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.order.entity.OrderEntity;
import com.siro.gulimall.order.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 11:59:07
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认页返回需要用的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 下单操作
     * @param orderSubmitVo
     * @return
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);

    /**
     * 根据订单号查询订单数据
     * @return
     */
    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * MQ关闭订单
     * @param entity
     */
    void closeOrder(OrderEntity entity);

    /**
     * 获取当前订单的支付信息
     * @param orderSn
     * @return
     */
    PayVo getOrderPay(String orderSn);

    /**
     * 查询当前登录用户的所有订单信息
     * @param params
     * @return
     */
    PageUtils queryPageWithItem(Map<String, Object> params);

    /**
     * 处理支付宝支付的结果
     * @param payAsyncVo
     * @return
     */
    String handlePayResult(PayAsyncVo payAsyncVo);

    /**
     * 处理秒杀
     * @param orderTo
     */
    void createSeckillOrder(SeckillOrderTo orderTo);
}

