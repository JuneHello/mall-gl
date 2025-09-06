package com.siro.common.constant;

/**
 * @author starsea
 * @date 2022-05-14
 */
public class SeckillConstant {
    //秒杀活动用的前缀
    public final static String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    //商品秒杀的前缀
    public final static String SKUKILL_CACHE_PREFIX = "seckill:skus:";
    //商品库存信号量
    public final static String SKU_STOCK_SEMAPHORE = "seckill:stock:";
    //秒杀商品上架功能的锁
    public final static String UPLOAD_LOCK = "seckill:upload:lock";
}
