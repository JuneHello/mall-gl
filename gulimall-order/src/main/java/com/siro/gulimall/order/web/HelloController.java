package com.siro.gulimall.order.web;

import com.siro.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;

/**
 * @author starsea
 * @date 2022-05-07
 */
@Controller
public class HelloController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 测试mq配置类的功能：http://order.gulimall.com/test/createOrder
     * @return
     */
    @ResponseBody
    @GetMapping("/test/createOrder")
    public String createOrderTest() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        orderEntity.setModifyTime(new Date());
        //MQ发送消息
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",orderEntity);
        return "ok";
    }

    /**
     * 测试引入的页面跳转
     * @param page
     * @return
     */
    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page) {

        return page;
    }
}
