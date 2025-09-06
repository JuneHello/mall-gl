package com.siro.gulimall.member.exception;

/**
 * @author starsea
 * @date 2022-05-01
 */
public class UserNameExistExecption extends RuntimeException {

    public UserNameExistExecption() {
        super("用户名存在");
    }

}
