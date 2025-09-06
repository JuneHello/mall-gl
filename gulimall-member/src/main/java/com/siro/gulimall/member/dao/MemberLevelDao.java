package com.siro.gulimall.member.dao;

import com.siro.gulimall.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 11:42:24
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

    /**
     * 查询默认等级
     * @return
     */
    MemberLevelEntity getDefaultLevel();
}
