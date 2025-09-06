package com.siro.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.coupon.entity.SeckillSkuRelationEntity;

import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 11:31:54
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

