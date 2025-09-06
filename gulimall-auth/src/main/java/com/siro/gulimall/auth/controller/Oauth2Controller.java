package com.siro.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.siro.common.constant.AuthConstant;
import com.siro.common.utils.HttpUtils;
import com.siro.common.utils.R;
import com.siro.gulimall.auth.feign.MemberFeignService;
import com.siro.common.vo.MemberRespVo;
import com.siro.gulimall.auth.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求
 * 官网地址：https://open.weibo.com/
 * @author starsea
 * @date 2022-05-04
 */
@Controller
public class Oauth2Controller {

    @Autowired
    private MemberFeignService memberFeignService;

    /**
     * 微博社交登录
     * @param code
     * @return
     * @throws Exception
     */
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        Map<String,String> header = new HashMap<>();
        Map<String,String> query = new HashMap<>();

        Map<String,String> map = new HashMap<>();
        map.put("client_id", "");
        map.put("client_secret", "");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code", code);
        //1、根据code换取accessToken
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "POST", header, query, map);
        //2、处理
        if(response.getStatusLine().getStatusCode() == 200){
            //获取到了 accessToken
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
            //当前用户如果是第一次进入网站，自动注册进来（为当前社交用户生成一个会员信息账号）
            //登录或者注册这个社交用户
            R oauthlogin = memberFeignService.oauthLogin(socialUser);
            if(oauthlogin.getCode() == 0){
                MemberRespVo data = oauthlogin.getData("data", new TypeReference<MemberRespVo>(){});
                System.out.println("社交微博登录信息:" + data.toString());
                /**
                 * 第一次使用session,命令浏览器保存卡号。JSESSIONID这个cookie
                 * 1. 默认发的令牌.session=asdfasdfa。作用域:当前域。(解决子域session共享问题)
                 * 2. 使用JSON的序列化方式来序列化对象数据到redis中。
                 */
                session.setAttribute(AuthConstant.LOGIN_USER, data);
                //登录成功就跳回首页
                return "redirect:http://gulimall.com";
            }else {
                return "redirect:http://auth.gulimall.com/login.html";
            }
        }else {
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
