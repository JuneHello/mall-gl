package com.siro.gulimall.ware.feign;

import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author starsea
 * @date 2022-05-12
 */
@FeignClient("gulimall-order")
public interface OrderFeignService {

    /**
     * 根据订单号查询订单数据
     * @param orderSn
     * @return
     */
    @GetMapping("/order/order/status/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
