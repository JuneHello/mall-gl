package com.siro.gulimall.order.vo;

import com.siro.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * 提交订单返回前端的数据
 * @author starsea
 * @date 2022-05-09
 */
@Data
public class SubmitOrderResponseVo {

    /** 订单信息 **/
    private OrderEntity order;

    /** 错误状态码 1-失败 **/
    private Integer code;
}
