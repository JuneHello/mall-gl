package com.siro.gulimall.seckill.service;

import com.siro.gulimall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @author starsea
 * @date 2022-05-14
 */
public interface SeckillService {
    /**
     * 定时上架秒杀商品
     */
    void uploadSeckillSkuLatest3Days();

    /**
     * 当前时间可以参与秒杀的商品信息
     * @return
     */
    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    /**
     * 根据skuId查询商品是否参加秒杀活动
     * @param skuId
     * @return
     */
    SeckillSkuRedisTo getSkuSeckilInfo(Long skuId);

    String kill(String killId, String key, Integer num) throws InterruptedException;
}
