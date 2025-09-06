package com.siro.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.siro.common.constant.AuthConstant;
import com.siro.common.exception.BizCodeEnum;
import com.siro.common.utils.R;
import com.siro.common.vo.MemberRespVo;
import com.siro.gulimall.auth.feign.MemberFeignService;
import com.siro.gulimall.auth.feign.ThirdFeignService;
import com.siro.gulimall.auth.vo.UserLoginVo;
import com.siro.gulimall.auth.vo.UserRegisterVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author starsea
 * @date 2022-04-28
 */
@Controller
public class LoginController {

    @Autowired
    private ThirdFeignService thirdFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    /**
     * 跳转登录页
     * @param session
     * @return
     */
    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthConstant.LOGIN_USER);
        if(attribute==null){
            // 没登陆
            return "login";
        }else{
            // 已登录，重定向到首页
            return "redirect:http://gulimall.com";
        }
    }

    /**
     * 发送短信获取验证码
     * @param phone
     * @return
     */
    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        //1. 接口防刷
        //防止同一个phone在60秒内再次发送验证码
        String redisCode = redisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(),BizCodeEnum.SMS_CODE_EXCEPTION.getMessage());
            }
        }

        //2. 验证码再次校验【存入redis】
        String code = UUID.randomUUID().toString().substring(0, 5);
        String subString = code + "_" + System.currentTimeMillis();
        //key，sms:code:13888888888    value，1234
        redisTemplate.opsForValue().set(AuthConstant.SMS_CODE_CACHE_PREFIX+phone, subString,10, TimeUnit.MINUTES);

        thirdFeignService.sendCode(phone,code);
        return R.ok();
    }


    /**
     * 用户注册
     * @param userRegisterVo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/regist")
    public String regist(@Valid UserRegisterVo userRegisterVo, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            //将错误消息返回给前端
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
//            model.addAttribute("errors",errors);
            redirectAttributes.addFlashAttribute("errors",errors);

            /**
             * 错误：Request method 'POST' not supported
             * 分析：用户注册 -> regist[post] -> 转发/reg.html（路径映射默认都是get方式访问的）
             * return "forward:/reg.html";
             * 解决直接渲染注册页
             * return "reg";
             *
             * 问题：刷新页面相当于表单重复提交
             * 解决：使用重定向到注册页面【要写完成路径】，但是转发 model里面的数据是在请求域中，重定向获取不到数据。
             *      使用RedirectAttributes 模拟重定向携带数据。
             * 分析：重定向携带数据，利用session原理。将数据放在session中，只要跳到下一个页面取出这个数据以后，session里面的数据就会删掉。
             * TODO 分布式下的session问题。
             */

            //校验出错，重定向到注册页
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        //校验验证码
        String code = userRegisterVo.getCode();
        String s = redisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + userRegisterVo.getPhone());
        if (!StringUtils.isEmpty(s)) {
            if (code.equals(s.split("_")[0])) {
                //验证码通过，删除验证码【令牌机制】
                redisTemplate.delete(AuthConstant.SMS_CODE_CACHE_PREFIX + userRegisterVo.getPhone());
                //真正注册，调用远程服务
                R r = memberFeignService.regist(userRegisterVo);
                if (r.getCode() == 0) {
                    //注册成功，回到登录页
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    //失败或异常
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", r.getData("msg", new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            } else {
                //验证码不通过
                Map<String,String> errors = new HashMap<>();
                errors.put("code", "验证码失效");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {
            Map<String,String> errors = new HashMap<>();
            errors.put("code", "验证码失效");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }

    /**
     * 用户账号密码登录
     * @param userLoginVo
     * @return
     */
    @PostMapping("/login")
    public String login(UserLoginVo userLoginVo, RedirectAttributes redirectAttributes, HttpSession session) {
        //远程登录
        R r = memberFeignService.login(userLoginVo);
        if (r.getCode() == 0) {
            MemberRespVo data = r.getData(new TypeReference<MemberRespVo>() {});
            session.setAttribute(AuthConstant.LOGIN_USER, data);
            return "redirect:http://gulimall.com";
        } else {
            Map<String,String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    /**
     * 用户退出
     * @param request
     * @return
     */
    @GetMapping(value = "/loguot.html")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute(AuthConstant.LOGIN_USER);
        request.getSession().invalidate();
        return "redirect:http://gulimall.com";
    }
}
