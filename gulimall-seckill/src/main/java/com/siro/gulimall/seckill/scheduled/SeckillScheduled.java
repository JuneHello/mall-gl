package com.siro.gulimall.seckill.scheduled;

import com.siro.common.constant.SeckillConstant;
import com.siro.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品定时上架
 *  每天晚上3点，上架最近三天需要三天秒杀的商品
 *  当天00:00:00 - 23:59:59
 *  明天00:00:00 - 23:59:59
 *  后天00:00:00 - 23:59:59
 *
 * @author starsea
 * @date 2022-05-14
 */
@Slf4j
@Service
public class SeckillScheduled {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedissonClient redissonClient;

    //保证幂等性问题
//    @Scheduled(cron = "*/5 * * * * ?") //每5秒就执行一次上架功能
    @Scheduled(cron = "0 */5 * * * ?") //每5分钟就执行一次上架功能
//    @Scheduled(cron = "0 0 1/1 * * ?") //每一小时执行一次
    public void uploadSeckillSkuLatest3Days() {
        //1、重复上架无需处理
        log.info("上架秒杀的商品...");
        //分布式锁
        RLock lock = redissonClient.getLock(SeckillConstant.UPLOAD_LOCK);
        try {
            //加锁
            lock.lock(10, TimeUnit.SECONDS);
            seckillService.uploadSeckillSkuLatest3Days();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
