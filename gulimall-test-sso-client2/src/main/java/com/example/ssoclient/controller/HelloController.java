package com.example.ssoclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author starsea
 * @date 2022-05-05
 */
@Controller
public class HelloController {

    @Value("${sso.server.url}")
    String ssoServerUrl;

    /**
     * 无需登录就可访问
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/hello")
    public String hello() {
        return "hello";
    }

    /**
     * 查看员工信息
     * @param model
     * @param session
     * @param token
     * @return
     */
    @GetMapping(value = "/boss")
    public String boss(Model model, HttpSession session, @RequestParam(value = "token", required = false) String token) {
        if (!StringUtils.isEmpty(token)) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://sso.com:8080/userinfo?token=" + token, String.class);
            String body = forEntity.getBody();
            session.setAttribute("loginUser", body);
        }
        Object loginUser = session.getAttribute("loginUser");

        if (loginUser == null) {
            //没登录，跳转到登录服务器进行登录
            return "redirect:" + ssoServerUrl + "?redirect_url=http://client2.com:8082/boss";
        } else {
            List<String> emps = new ArrayList<>();

            emps.add("张三");
            emps.add("李四");

            model.addAttribute("emps", emps);
            return "employees";
        }
    }
}
