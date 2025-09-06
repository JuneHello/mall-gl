package com.siro.gulimall.auth.feign;

import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author starsea
 * @date 2022-04-30
 */
@FeignClient("gulimall-third-party")
public interface ThirdFeignService {

    /**
     * 发送短信验证码
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
