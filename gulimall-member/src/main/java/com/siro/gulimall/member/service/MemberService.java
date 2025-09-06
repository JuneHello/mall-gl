package com.siro.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.member.entity.MemberEntity;
import com.siro.gulimall.member.exception.PhoneExistException;
import com.siro.gulimall.member.exception.UserNameExistExecption;
import com.siro.gulimall.member.vo.MemberLoginVo;
import com.siro.gulimall.member.vo.MemberRegisterVo;
import com.siro.gulimall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 11:42:24
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 用户注册
     * @param memberRegisterVo
     */
    void regist(MemberRegisterVo memberRegisterVo);

    /**
     * 查询手机号是否存在
     * @param phone
     */
    void checkPhoneUnique(String phone) throws PhoneExistException;

    /**
     * 查询用户名是否存在
     * @param userName
     */
    void checkUserNameUnique(String userName) throws UserNameExistExecption;

    /**
     * 用户账号密码登录
     * @param memberLoginVo
     * @return
     */
    MemberEntity login(MemberLoginVo memberLoginVo);

    /**
     * 社交微博登录
     * @param socialUser
     * @return
     */
    MemberEntity login(SocialUser socialUser);
}

