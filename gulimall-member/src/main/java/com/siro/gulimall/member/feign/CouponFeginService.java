package com.siro.gulimall.member.feign;

import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author starfish
 * @date 2021-10-03 15:28
 */
@FeignClient("gulimall-coupon")
public interface CouponFeginService {

    @RequestMapping("/coupon/coupon/member/list")
    public R memberCoupons();
}
