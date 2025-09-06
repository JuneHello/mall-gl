package com.siro.gulimall.order.config;

import com.rabbitmq.client.Channel;
import com.siro.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 容器中的 交换机，队列，以及绑定关系 都会自动创建（RabbitMQ没有的情况）
 * RabbitMQ 只要有。@Bean声明的属性发生变化也不会覆盖。
 *
 * @author starsea
 * @date 2022-05-12
 */
@Configuration
public class MyMQConfig {

    /**
     * 死信队列
     * @return
     */
    @Bean
    public Queue orderDelayOrderQueue() {
        Map<String, Object> arguments = new HashMap<>();
        /**
         * x-dead-letter-exchange: order-event-exchange
         * x-dead-letter-routing-key: order.release.order
         * x-message-ttl: 60000
         */
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 120000);//两分钟
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Queue queue = new Queue("order.delay.queue", true, false, false, arguments);
        return queue;
    }

    /**
     * 队列
     * @return
     */
    @Bean
    public Queue orderReleaseOrderQueue() {
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }

    @Bean
    public Queue helloJavaQueue() {
        return new Queue("hello.java.queue", true); // 持久化队列
    }

    /**
     * 交换机
     * @return
     */
    @Bean
    public Exchange orderEventExchange() {
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("order-event-exchange", true, false);
    }

    /**
     * 绑定关系
     * @return
     */
    @Bean
    public Binding orderCreateOrderBinding() {
        //String destination, DestinationType destinationType, String exchange, String routingKey,Map<String, Object> arguments
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }

    /**
     * 绑定关系
     * @return
     */
    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }

    /**
     * 订单释放直接和库存释放进行绑定
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBingding() {
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }

    /**
     * 商品秒杀队列
     * @return
     */
    @Bean
    public Queue orderSecKillOrderQueue() {
        Queue queue = new Queue("order.seckill.order.queue", true, false, false);
        return queue;
    }

    /**
     * 订单释放直接和库存释放进行绑定
     * @return
     */
    @Bean
    public Binding orderSecKillOrderQueueBinding() {
        return new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.seckill.order",
                null);
    }
}
