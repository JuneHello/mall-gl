package com.siro.gulimall.ware.feign;

import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author starsea
 * @date 2022-05-08
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    /**
     * 查询收货地址详细信息
     * @param id
     * @return
     */
    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    public R addrInfo(@PathVariable("id") Long id);
}
