package com.siro.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.to.SkuReductionTo;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 11:31:54
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

