package com.siro.gulimall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * @author starsea
 * @date 2022-05-10
 */
@Data
public class WareSkuLockVo {

    //订单号
    private String orderSn;

    //需要锁住的所有库存信息
    private List<OrderItemVo> locks;
}
