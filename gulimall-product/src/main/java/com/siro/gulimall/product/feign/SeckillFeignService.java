package com.siro.gulimall.product.feign;

import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author starsea
 * @date 2022-05-14
 */
@FeignClient("gulimall-seckill")
public interface SeckillFeignService {

    /**
     * 根据skuId查询商品是否参加秒杀活动
     * @param skuId
     * @return
     */
    @GetMapping(value = "/sku/seckill/{skuId}")
    public R getSkuSeckilInfo(@PathVariable("skuId") Long skuId);
}
