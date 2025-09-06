package com.siro.gulimall.order.feign;

import com.siro.common.utils.R;
import com.siro.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author starsea
 * @date 2022-05-08
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {

    /**
     * 查询sku是否有库存
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hosStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);

    /**
     * 根据用户收货地址计算运费
     * @param addrId
     * @return
     */
    @GetMapping("/ware/wareinfo/fare")
    public R getFare(@RequestParam("addrId") Long addrId);

    /**
     * 为某个订单锁定库存锁定库存
     * @param wareSkuLockVo
     * @return
     */
    @PostMapping("/ware/waresku/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo);
}
