package com.siro.gulimall.auth.feign;

import com.siro.common.utils.R;
import com.siro.gulimall.auth.vo.SocialUser;
import com.siro.gulimall.auth.vo.UserLoginVo;
import com.siro.gulimall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author starsea
 * @date 2022-05-03
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    /**
     * 会员注册
     * @param userRegisterVo
     * @return
     */
    @PostMapping("/member/member/regist")
    public R regist(@RequestBody UserRegisterVo userRegisterVo);

    /**
     * 用户账号密码登录
     * @param userLoginVo
     * @return
     */
    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginVo userLoginVo);

    /**
     * 社交微博登录
     * @param socialUser
     * @return
     */
    @PostMapping("/member/member/oauthLogin")
    public R oauthLogin(@RequestBody SocialUser socialUser);
}
