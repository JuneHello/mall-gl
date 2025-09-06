package com.siro.gulimall.third;

import com.aliyun.oss.OSSClient;
import com.siro.gulimall.third.component.SmsComponent;
import com.siro.common.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    SmsComponent smsComponent;

    /**
     * 测试阿里云短信服务
     */
    @Test
    public void sendSmsCode() {
        smsComponent.sendSmsCode("13888888888","3d4c5b");
    }

    /**
     * 使用视频中讲解的免费五次的【三网合一】短信接口 配置类
     * 官网地址：https://market.aliyun.com/products/57126001/cmapi025334.html
     * 需要认证企业用户【这里我们没有使用】
     */
    @Test
    public void sendSms() {
        String appcode = "your-appcode-here";
        String code = "3d4c5b";
        String phone = "13888888888";
        String skin = "1";
        String sign = "175622";
        String host = "https://fesms.market.alicloudapi.com";
        String path = "/sms/";

        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("code", code);
        querys.put("phone", phone);
        querys.put("skin", skin);
        querys.put("sign", sign);
        //JDK 1.8示例代码请在这里下载：  http://code.fegine.com/Tools.zip
        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 或者直接下载：
             * http://code.fegine.com/HttpUtils.zip
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             * 相关jar包（非pom）直接下载：
             * http://code.fegine.com/aliyun-jar.zip
             */
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            //System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
            //状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    OSSClient ossClient;

    /**
     * 测试阿里云oss存储
     *
     * 操作步骤：
     * 1. 引入oss-starter依赖
     * 2. 配置key，endpoint相关信息
     * 3. 使用 OSSClient 进行相关操作即可
     * @throws FileNotFoundException
     */
    @Test
    public void testUpload() throws FileNotFoundException {
        //// Endpoint以北京为例，其他Region请按实际情况填写
        //String endpoint = "oss-cn-beijing.aliyuncs.com";
        //// 云账号Accesskey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常操作
        //String accessKeyId = "your-access-key-here";
        //String accessKeySecret = "your-access-secret-here";
        //创建OSSClient实例
        //OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        // 上传文件流
        InputStream inputStream = new FileInputStream("C:\\Users\\GH\\Pictures\\Saved Pictures\\default.jpg");
        ossClient.putObject("gulimall-hello-2021","hahaha.jpg",inputStream);
        //关闭ossClient
        ossClient.shutdown();

        System.out.println("上传成功....");
    }

}
