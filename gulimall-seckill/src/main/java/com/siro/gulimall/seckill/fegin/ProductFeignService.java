package com.siro.gulimall.seckill.fegin;

import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author starsea
 * @date 2022-05-14
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     * 根据skuId获取详情
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R getSkuInfo(@PathVariable("skuId") Long skuId);
}
