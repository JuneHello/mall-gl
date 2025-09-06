package com.siro.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 11:42:24
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据memberId查询会员收货地址信息
     * @param memberId
     * @return
     */
    List<MemberReceiveAddressEntity> getAddress(Long memberId);
}

