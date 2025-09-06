package com.siro.gulimall.order.controller;

import com.siro.gulimall.order.entity.OrderEntity;
import com.siro.gulimall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @author starsea
 * @date 2022-05-07
 */
@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 测试发送 实体类 消息
     * @param num
     * @return
     */
    @GetMapping("/sendMq")
    public String sendMq(@RequestParam(value = "num",defaultValue = "10") Integer num) {
        for (int i = 0; i < num; i++) {
            if (i % 2 == 0) {
                //如果发送的消息是个对象，我们会使用序列化机制，将对象写出去，对象必须实现Serializable接口
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setId(1L);
                reasonEntity.setName("退货原因"+i);

                //发送的对象类型的消息是一个json，需要重新定义消息转换器Jackson2JsonMessageConverter
                rabbitTemplate.convertAndSend("hello.java.exchange","hello.java", reasonEntity, new CorrelationData(UUID.randomUUID().toString()));
            } else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello.java.exchange","hello.java", orderEntity, new CorrelationData(UUID.randomUUID().toString()));
            }
        }
        return "ok";
    }
}
