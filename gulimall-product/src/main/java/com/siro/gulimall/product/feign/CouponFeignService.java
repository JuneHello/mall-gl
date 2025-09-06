package com.siro.gulimall.product.feign;

import com.siro.common.to.SkuReductionTo;
import com.siro.common.to.SpuBoundTo;
import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author starsea
 * @date 2021-11-27 22:49
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    /**
     * 1、CouponFeignService.saveSpuBounds(spuBoundTo);
     *  1） @RequestBody 将这个对象转为json
     *  2） 找到gulimall-common服务，给/coupon/spubounds/save发送请求，将上一步转的json放在请求体位置，发送数据
     *  3）对方服务接收请求，请求体里有json数据
     *      (@RequestBody SpuBoundsEntity spuBounds)：将请求体的json转为SpuBoundsEntity。
     *
     * 总结：只要json数据模型是兼容的。双方服务无需使用同一个to对象
     * @param spuBoundTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
