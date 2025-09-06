package com.siro.gulimall.third.component;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * 使用短信服务
 * 官网地址：https://dysms.console.aliyun.com/overview
 *
 * @author starsea
 * @date 2022-04-30
 */
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Data
@Component
public class SmsComponent {
    private String regionId;
    private String accessKeyID;
    private String accessKeySecret;
    private String signName;//签名名称
    private String templateCode;//模板code

    public void sendSmsCode(String phone, String code) {
        //判断手机号是否为空
        if (StringUtils.isEmpty(phone)) {
            System.out.println("手机号为空");
        }else {
            //设置相关参数
            DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyID, accessKeySecret);
            IAcsClient client = new DefaultAcsClient(profile);

            //设置相关固定的参数
            CommonRequest request = new CommonRequest();
            request.setMethod(MethodType.POST); //提交方式
            request.setDomain("dysmsapi.aliyuncs.com");
            request.setVersion("2017-05-25");
            request.setAction("SendSms");

            //设置发送相关的参数
            request.putQueryParameter("PhoneNumbers", phone);
            request.putQueryParameter("SignName", signName);
            request.putQueryParameter("TemplateCode", templateCode);

            HashMap<String, Object> params = new HashMap<>();
            params.put("code", code);
            request.putQueryParameter("TemplateParam", JSONObject.toJSONString(params));

            try {
                //最终发送
                CommonResponse response = client.getCommonResponse(request);
                System.out.println("验证码发送成功" + response.getData());
            }catch (ServerException e) {
                e.printStackTrace();
                System.out.println("验证码发送失败1");
            } catch (ClientException e) {
                e.printStackTrace();
                System.out.println("验证码发送失败2");
            }
        }
    }
}
