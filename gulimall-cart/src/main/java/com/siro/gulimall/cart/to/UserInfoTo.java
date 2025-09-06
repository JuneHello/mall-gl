package com.siro.gulimall.cart.to;

import lombok.Data;
import lombok.ToString;

/**
 * @author starsea
 * @date 2022-05-05
 */
@ToString
@Data
public class UserInfoTo {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 临时用户，一定存在
     */
    private String userKey;

    /**
     * 是否临时用户
     */
    private Boolean tempUser = false;
}
