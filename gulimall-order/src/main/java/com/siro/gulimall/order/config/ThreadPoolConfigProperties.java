package com.siro.gulimall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 在配置文件中动态绑定线程池中参数
 * @author starsea
 * @date 2022-04-28
 */
@ConfigurationProperties(prefix = "gulimall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
    //核心线程数
    private Integer coreSize;
    //最大线程数
    private Integer maxSize;
    //休眠时长
    private Integer keepAliveTime;
}
