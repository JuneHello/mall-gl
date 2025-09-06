package com.siro.gulimall.order.feign;

import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author starsea
 * @date 2022-05-09
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     * 根据skuId查询spu对象数据
     * @param skuId
     * @return
     */
    @GetMapping("/product/spuinfo/skuId/{id}")
    public R getSpuInfoBySkuId(@PathVariable("id") Long skuId);
}
