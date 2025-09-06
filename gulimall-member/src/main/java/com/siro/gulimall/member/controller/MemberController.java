package com.siro.gulimall.member.controller;

import com.siro.common.exception.BizCodeEnum;
import com.siro.common.utils.PageUtils;
import com.siro.common.utils.R;
import com.siro.gulimall.member.entity.MemberEntity;
import com.siro.gulimall.member.exception.PhoneExistException;
import com.siro.gulimall.member.exception.UserNameExistExecption;
import com.siro.gulimall.member.feign.CouponFeginService;
import com.siro.gulimall.member.service.MemberService;
import com.siro.gulimall.member.vo.MemberLoginVo;
import com.siro.gulimall.member.vo.MemberRegisterVo;
import com.siro.gulimall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 会员
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 11:42:24
 */
@RestController
@RequestMapping("/member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeginService couponFeginService;

    /**
     * 社交微博登录
     * @param socialUser
     * @return
     */
    @PostMapping("/oauthLogin")
    public R oauthLogin(@RequestBody SocialUser socialUser) {
        MemberEntity entity = memberService.login(socialUser);
        if (entity != null) {
            return R.ok().setData(entity);
        } else {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMessage());
        }
    }

    /**
     * 用户账号密码登录
     * @param memberLoginVo
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo) {
        MemberEntity entity = memberService.login(memberLoginVo);
        if (entity != null) {
            return R.ok().setData(entity);
        } else {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMessage());
        }
    }

    /**
     * 用户注册
     * @return
     */
    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegisterVo memberRegisterVo) {

        try {
            memberService.regist(memberRegisterVo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMessage());
        } catch (UserNameExistExecption e) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMessage());
        }

        return R.ok();
    }

    @RequestMapping("/coupons")
    public R test() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        R memberCoupons = couponFeginService.memberCoupons();
        return R.ok().put("member",memberEntity).put("coupons",memberCoupons.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
