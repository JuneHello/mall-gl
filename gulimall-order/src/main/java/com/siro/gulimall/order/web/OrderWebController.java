package com.siro.gulimall.order.web;

import com.siro.common.exception.NoStockException;
import com.siro.gulimall.order.service.OrderService;
import com.siro.gulimall.order.vo.OrderConfirmVo;
import com.siro.gulimall.order.vo.OrderSubmitVo;
import com.siro.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * @author starsea
 * @date 2022-05-08
 */
@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    /**
     * 点击去结算去订单确认页
     * @return
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        //展示订单确认的数据
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }

    /**
     * 提交订单
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("订单提交的数据：" + orderSubmitVo);
        try {
            //下单操作
            SubmitOrderResponseVo responseVo = orderService.submitOrder(orderSubmitVo);
            if (responseVo.getCode() == 0) {
                //下单成功来到支付选择页面
                model.addAttribute("submitOrderResp", responseVo);
                return "pay";
            } else {
                String msg = "下单失败，";
                switch (responseVo.getCode()) {
                    case 1: msg += "令牌订单信息过期，请刷新再次提交"; break;
                    case 2: msg += "订单商品价格发生变化，请确认后再次提交"; break;
                    case 3: msg += "库存锁定失败，商品库存不足"; break;
                }
                redirectAttributes.addFlashAttribute("msg", msg);
                //下单失败回到订单确认页重新确认订单信息
                return "redirect:http://order.gulimall.com/toTrade";
            }
        } catch (Exception e) {
            if (e instanceof NoStockException) {
                String message = ((NoStockException) e).getMessage();
                redirectAttributes.addFlashAttribute("msg", message);
            }
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }

}
