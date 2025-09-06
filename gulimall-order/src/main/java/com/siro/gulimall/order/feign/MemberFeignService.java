package com.siro.gulimall.order.feign;

import com.siro.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author starsea
 * @date 2022-05-08
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    /**
     * 查询会员的所有收货地址列表
     * @param memberId
     * @return
     */
    @GetMapping("/member/memberreceiveaddress/{memberId}/addresses")
    public List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);
}
