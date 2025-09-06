package com.siro.gulimall.order.to;

import com.siro.gulimall.order.entity.OrderEntity;
import com.siro.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单创建成功后返回的数据
 * @author starsea
 * @date 2022-05-09
 */
@Data
public class OrderCreateTo {

    /** 订单信息 **/
    private OrderEntity order;

    /** 订单项信息 **/
    private List<OrderItemEntity> orderItems;

    /** 订单计算的应付价格 **/
    private BigDecimal payPrice;

    /** 运费 **/
    private BigDecimal fare;
}
