package com.siro.gulimall.order;

import com.siro.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * Seata控制分布式事务【http://seata.io/zh-cn/docs/user/quickstart.html】
     * 1、每个服务先必须创建undo_log表
     * 2、安装事务协调器：seata-server。https://github.com/seata/seata/releases
     * 3、整合【参考官网和github】
     *      1）导入依赖 spring-cloud-starter-alibaba-seata
     *      2）解压并启动seata-server。【由于依赖seata-all的版本，所以我们下载0.7.1】
     *          registry.conf：修改registry type=nacos
     *      3）所有想要用到分布式事务的微服务使用seata DataSourceProxy代理自己的数据源
     *      4）使用分布式事务的微服务都必须导入：file.conf和registry.conf文件
     *      5）修改file.conf文件的 vgroup_mapping.${spring.application.name}-fescar-service-group
     *      6）启动测试分布式事务
     *      7）给分布式大事务上【业务方法上】标注 @GlobalTransactional 注解
     *      8）每一个远程的小事务用 @Transactional 注解
     */

    /**
     * 本地事务失效问题：
     * 同一个对象内事务方法互调默认失效，原因是绕过了代理对象。
     * 解决：使用代理对象来调用事务方法
     *  1）引入 aop-starter 依赖
     *  2）@EnableAspectJAutoProxy(exposeProxy=true)：开启aspectj动态代理功能。
     *      以后所有动态代理都是aspectj创建的（即使没有接口也可以创建动态代理）
     *  3）本类相互调用对象
     *      OrderServiceImpl orderService = (OrderServiceImpl) AopContext.currentProxy();
     *      orderService.b();
     *      orderService.c();
     */

    /**
     * 整合RabbitMQ
     * 1. 引入amqp-starter，RabbitAutoConfiguration就会自动生效
     * 2. 给容器种自动配置了
     *      CachingConnectionFactory、RabbitTemplate、AmqpAdmin、RabbitMessagingTemplate
     *      所有的属性都是在RabbitProperties类种绑定的
     *      @ConfigurationProperties(prefix = "spring.rabbitmq")
     *      public class RabbitProperties {
     * 3. 给配置文件中配置 以spring.rabbitmq开头的信息
     * 4. 使用@EnableRabbit开启功能
     * 5. 监听消息使用@RabbitListener注解，必须有@EnableRabbit注解
     *      @RabbitListener：标在类+方法上（监听哪些队列）
     *      @RabbitHandler：标在方法上（重载区分不同的消息）
     *
     * 1. 如何创建Exchange、Queue、Binding
     *      1）使用AmqpAdmin进行创建
     * 2. 如何收发消息
     */
    @Test
    public void createExchange() {
        //创建交换机
        DirectExchange directExchange = new DirectExchange("hello.java.exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}]创建成功", "hello.java.exchange");
    }

    @Test
    public void createQueue() {
        //创建队列
        Queue queue = new Queue("hello.java.queue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功", "hello.java.queue");
    }

    @Test
    public void createBinding() {
        //交换机和队列绑定
        /**
         * 参数说明：
         * String destination：目的地
         * Binding.DestinationType destinationType：目的地类型
         * String exchange：交换机
         * String routingKey：路由键
         * Map<String, Object> arguments：自定义参数
         */
        Binding binding = new Binding("hello.java.queue", Binding.DestinationType.QUEUE,
                "hello.java.exchange","hello.java",null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding[{}]创建成功", "hello.java.binding");
    }

    /**
     * 发送字符串消息
     */
    @Test
    public void sendMessageTest() {
        String msg = "hello world";
        rabbitTemplate.convertAndSend("hello.java.exchange","hello.java", msg);
        log.info("消息发送完成{}",msg);
    }

}
