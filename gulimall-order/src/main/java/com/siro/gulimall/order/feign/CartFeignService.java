package com.siro.gulimall.order.feign;

import com.siro.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author starsea
 * @date 2022-05-08
 */
@FeignClient("gulimall-cart")
public interface CartFeignService {

    /**
     * 获取购物车中所有选中的数据
     * @return
     */
    @GetMapping("/currentUserCartItem")
    public List<OrderItemVo> getCurrentUserCartItem();
}
