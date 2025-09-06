package com.siro.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.siro.gulimall.order.config.AlipayTemplate;
import com.siro.gulimall.order.service.OrderService;
import com.siro.gulimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author starsea
 * @date 2022-05-13
 */
@Controller
public class PayWebController {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    /**
     * 用户下单:支付宝支付
     * 1、让支付页让浏览器展示
     * 2、支付成功以后，跳转到用户的订单列表页
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/aliPayOrder",produces = "text/html")
    public String aliPayOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        //获取当前订单的支付信息
        PayVo payVo = orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        return pay;
    }

}
