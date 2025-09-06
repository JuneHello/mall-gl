package com.siro.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式锁Redisson配置类
 * 官网地址：https://github.com/redisson/redisson/wiki/1.-%E6%A6%82%E8%BF%B0
 * 可以先学习一下 JUC
 *
 * @author starsea
 * @date 2022-04-10
 */
@Configuration
public class MyRedissonConfig {

    /**
     * 所有Redisson的使用都是通过RedissonClient对象
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        //1、创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6380");
        //2、根据Config创建出RedissonClient实例
        //Redis url should start with redis:// or rediss://
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
