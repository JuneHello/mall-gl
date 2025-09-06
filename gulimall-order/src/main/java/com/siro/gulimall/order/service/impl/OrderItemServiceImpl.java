package com.siro.gulimall.order.service.impl;

import com.rabbitmq.client.Channel;
import com.siro.gulimall.order.entity.OrderEntity;
import com.siro.gulimall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siro.common.utils.PageUtils;
import com.siro.common.utils.Query;

import com.siro.gulimall.order.dao.OrderItemDao;
import com.siro.gulimall.order.entity.OrderItemEntity;
import com.siro.gulimall.order.service.OrderItemService;

@RabbitListener(queues = {"hello.java.queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 测试Rabbitmq消息接收
     *
     * org.springframework.amqp.core.Message
     *
     * 参数可以写的以下类型
     * 1. Message message：原生消息详细信息，头+体
     * 2. T<发送消息的类型> OrderReturnReasonEntity content
     * 3. Channel channel：当前传输数据的通道
     *
     * Queue：可以有很多人来监听。只要收到消息，队列删除消息，而且只能有一个能收到此消息
     *
     * 场景：
     *  1. 订单服务启动多个。同一个消息，只能有一个客户端收到
     *  2. 只有一个消息完全处理完，方运行结束，我们就可以接收下一个消息
     */
    @RabbitHandler
    public void recieveMessage(Message message, OrderReturnReasonEntity content,
                               Channel channel) throws InterruptedException {
        System.out.println("内容1..." + content);
        //消息体信息
        byte[] body = message.getBody();
        //消息头属性信息
        MessageProperties messageProperties = message.getMessageProperties();
//        Thread.sleep(3000);
        System.out.println("消息处理完==>" + content.getName());

        //deliveryTag 是 channel内按顺序自增的
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("deliveryTag ==> " + deliveryTag);

        //签收获取，非批量模式
        try {
            if (deliveryTag%2 == 0) {
                channel.basicAck(deliveryTag,false);
                System.out.println("签收了货物..." + deliveryTag);
            } else {
                // requeue = false 丢弃， = true 发回服务器，服务器重新入队
                //long deliveryTag, boolean multiple, boolean requeue
                channel.basicNack(deliveryTag,false,true);
                //long deliveryTag, boolean requeue
//                channel.basicReject();
                System.out.println("没有签收了货物..." + deliveryTag);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitHandler
    public void recieveMessage2(OrderEntity content) {
        System.out.println("内容2..." +content);
    }
}