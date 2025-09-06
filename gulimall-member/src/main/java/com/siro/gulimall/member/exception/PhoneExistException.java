package com.siro.gulimall.member.exception;

/**
 * @author starsea
 * @date 2022-05-01
 */
public class PhoneExistException extends RuntimeException {

    public PhoneExistException() {
        super("手机号存在");
    }

}
