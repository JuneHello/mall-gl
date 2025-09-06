package com.siro.gulimall.product.feign;

import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Starsea
 * @date 2022-03-28 21:43
 */
@FeignClient("gulimall-ware")
public interface WareFeginService {

    /**
     * 解决返回值问题：
     * 1. R设计的时候可以加上泛型
     * 2. 直接返回我们想要的结果
     * 3. 自己封装解析结果
     *
     * 查询sku是否有库存
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hosStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);
}
