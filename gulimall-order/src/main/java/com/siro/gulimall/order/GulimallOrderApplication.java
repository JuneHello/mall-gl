package com.siro.gulimall.order;

import com.alibaba.cloud.seata.GlobalTransactionAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * windows解决端口占用：【cmd中】
 * 1. 查看是那个进程在占用：netstat -ano|findstr 9000
 * 2. 查看是那个程序在占用：tasklist|findstr 19760
 */
@EnableAspectJAutoProxy(exposeProxy = true)//对外暴露代理对象
@EnableFeignClients
@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableRabbit
@SpringBootApplication(exclude = GlobalTransactionAutoConfiguration.class)//排除seata的全局配置，否则影响mq的库存数量
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
