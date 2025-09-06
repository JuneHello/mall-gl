package com.siro.gulimall.auth.vo;

import lombok.Data;

/**
 * 社交登录信息
 * @author starsea
 * @date 2022-05-04
 */
@Data
public class SocialUser {

    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}
