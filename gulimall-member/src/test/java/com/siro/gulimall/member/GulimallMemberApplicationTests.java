package com.siro.gulimall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GulimallMemberApplicationTests {

    @Test
    public void contextLoads() {
        // md5不能直接进行密码加密存储
        // 因为抗修改性：彩虹表
        String s = DigestUtils.md5Hex("123456");
        System.out.println(s);//e10adc3949ba59abbe56e057f20f883e

        //盐值加密  加盐，$1$+8位字符
        String s1 = Md5Crypt.md5Crypt("123456".getBytes(),"$1$qqqqqqqq");
        System.out.println(s1);
        //$1$BQKdq/Ej$XTa1tUD4SrV36SBT7uxwo.
        //$1$qqqqqqqq$AZofg3QwurbxV3KEOzwuI1

        //Spring家族的密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");//$2a$10$PcKPL7mWIHAlMsfyuPjp8OYYrAdrzPcc2OO0I2gF9RhH1sOLY1JXS
        boolean matches = passwordEncoder.matches("123456", "$2a$10$PcKPL7mWIHAlMsfyuPjp8OYYrAdrzPcc2OO0I2gF9RhH1sOLY1JXS");
        System.out.println(encode+ "-->" + matches);
    }

}
