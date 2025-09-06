package com.siro.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.siro.common.utils.HttpUtils;
import com.siro.gulimall.member.dao.MemberLevelDao;
import com.siro.gulimall.member.entity.MemberLevelEntity;
import com.siro.gulimall.member.exception.PhoneExistException;
import com.siro.gulimall.member.exception.UserNameExistExecption;
import com.siro.gulimall.member.vo.MemberLoginVo;
import com.siro.gulimall.member.vo.MemberRegisterVo;
import com.siro.gulimall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siro.common.utils.PageUtils;
import com.siro.common.utils.Query;

import com.siro.gulimall.member.dao.MemberDao;
import com.siro.gulimall.member.entity.MemberEntity;
import com.siro.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 用户注册
     * @param memberRegisterVo
     */
    @Override
    public void regist(MemberRegisterVo memberRegisterVo) {
        MemberEntity memberEntity = new MemberEntity();

        //设置会员默认等级
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());

        //检查用户名和手机号是否唯一，为了让controller能感知到异常，使用异常机制
        checkPhoneUnique(memberRegisterVo.getPhone());
        checkUserNameUnique(memberRegisterVo.getUserName());

        //设置手机号
        memberEntity.setMobile(memberRegisterVo.getPhone());
        //设置用户名
        memberEntity.setUsername(memberRegisterVo.getUserName());
        //设置昵称
        memberEntity.setNickname(memberRegisterVo.getUserName());
        //设置创建时间
        memberEntity.setCreateTime(new Date());

        //设置密码【加密加盐存储】
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(memberRegisterVo.getPassword());
        memberEntity.setPassword(encode);

        //其他的默认信息

        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException{
        Integer mobile = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (mobile > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUserNameUnique(String userName) throws UserNameExistExecption{
        Integer username = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (username > 0){
            throw new UserNameExistExecption();
        }
    }

    /**
     * 用户登录
     * @param memberLoginVo
     * @return
     */
    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) {
        String loginacct = memberLoginVo.getLoginacct();
        String password = memberLoginVo.getPassword();
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("mobile", loginacct));
        if (entity == null) {
            //登录失败
            return null;
        } else {
            //校验密码
            String passwordDb = entity.getPassword();//数据库的密码
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(password, passwordDb);//进行密码匹配
            if (matches) {
                return entity;
            } else {
                return null;
            }
        }
    }

    /**
     * 社交微博登录
     * @param socialUser
     * @return
     */
    @Override
    public MemberEntity login(SocialUser socialUser) {
        //注册和登录合并逻辑
        String uid = socialUser.getUid();
        //1、判断当前社交用户是否已经登录过系统
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (memberEntity != null) {
            //这个用户已经注册
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());

            this.baseMapper.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        }else{
            //2、没有查到当前社交用户对应的记录我们就需要注册一个
            MemberEntity regist = new MemberEntity();
            try{
                //3、查询当前社交用户的社交账号信息（昵称，性别等）
                Map<String,String> query = new HashMap<>();
                query.put("access_token",socialUser.getAccess_token());
                query.put("uid",socialUser.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);
                if(response.getStatusLine().getStatusCode() == 200){
                    //查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    //昵称
                    String name = jsonObject.getString("name");
                    regist.setNickname(name);
                    //性别
                    String gender = jsonObject.getString("gender");
                    regist.setGender("m".equals(gender)?1:0);
                    //头像
                    String profileImageUrl = jsonObject.getString("profile_image_url");
                    regist.setHeader(profileImageUrl);
                    //........
                }
            }catch (Exception e){}

            regist.setSocialUid(socialUser.getUid());
            regist.setAccessToken(socialUser.getAccess_token());
            regist.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.insert(regist);

            return regist;
        }
    }

}