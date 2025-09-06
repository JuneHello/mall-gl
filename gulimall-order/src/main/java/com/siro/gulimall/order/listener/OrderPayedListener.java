package com.siro.gulimall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.siro.gulimall.order.config.AlipayTemplate;
import com.siro.gulimall.order.service.OrderService;
import com.siro.gulimall.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付成功后更新状态
 * @author starsea
 * @date 2022-05-13
 */
@RestController
public class OrderPayedListener {

    @Autowired
    OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;

    /**
     * P307 异步通知内网穿透
     * 使用postman测试是否成功：http://kgfecg.natappfree.cc/payed/notify
     * 参数：name = hello
     *
     * 在虚拟机的 /mydata/nginx/logs下，通过 cat error.log |grep 'payed' 查看错误日志，
     * 来编gulimall.conf 文件进行修改配置 第三行
     *
     * @return
     */
    @PostMapping("/payed/notify")
    public String handleAlipayed(PayAsyncVo payAsyncVo, HttpServletRequest request) throws AlipayApiException, UnsupportedEncodingException {
        //只要我们收到了支付宝给我们的异步通知，告诉我们订单支付成功，返回success，支付宝就再也不通知
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        //验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(),
                alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名

        if (signVerified) {
            System.out.println("签名验证成功...");
            //去修改订单状态
            String result = orderService.handlePayResult(payAsyncVo);
            return result;
        } else {
            System.out.println("签名验证失败...");
            return "error";
        }
    }
}
