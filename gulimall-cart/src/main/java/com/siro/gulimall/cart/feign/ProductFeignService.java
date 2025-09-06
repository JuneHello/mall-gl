package com.siro.gulimall.cart.feign;

import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author starsea
 * @date 2022-05-06
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     * 根据skuId查询商品信息
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId查询销售属性
     * @param skuId
     * @return
     */
    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    public List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

    /**
     * 获取商品的最新价格
     * @param skuId
     * @return
     */
    @GetMapping("/product/skuinfo/{skuId}/price")
    public R getPrice(@PathVariable("skuId") Long skuId);
}
