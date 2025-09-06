package com.siro.gulimall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author starsea
 * @date 2022-05-01
 */
@Data
public class MemberRegisterVo {
    private String userName;
    private String password;
    private String phone;
}
