package com.siro.gulimall.member.feign;

import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author starsea
 * @date 2022-05-13
 */
@FeignClient("gulimall-order")
public interface OrderFeignService {

    /**
     * 查询当前登录用户的所有订单信息
     * @param params
     * @return
     */
    @PostMapping("/order/order/listWithItem")
    public R listWithItem(@RequestBody Map<String, Object> params);
}
