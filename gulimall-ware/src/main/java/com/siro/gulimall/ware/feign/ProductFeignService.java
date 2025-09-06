package com.siro.gulimall.ware.feign;

import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author starsea
 * @date 2021-12-04 17:40
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     * 注意：
     * 1） 让所有请求通过网关：
     *      1、@FeignClient("gulimall-gateway")
     *      2、@RequestMapping("/api/product/skuinfo/info/{skuId}")
     * 2） 直接让后台指定服务处理
     *      1、@FeignClient("gulimall-product")
     *      2、@RequestMapping("/product/skuinfo/info/{skuId}")
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

}
