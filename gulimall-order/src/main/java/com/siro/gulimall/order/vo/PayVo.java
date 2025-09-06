package com.siro.gulimall.order.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 请求支付宝的参数信息
 * @author starsea
 * @date 2022-05-13
 */
@ToString
@Data
public class PayVo {

    private String out_trade_no; // 商户订单号 必填
    private String subject; // 订单名称 必填
    private String total_amount;  // 付款金额 必填
    private String body; // 商品描述 可空

}
