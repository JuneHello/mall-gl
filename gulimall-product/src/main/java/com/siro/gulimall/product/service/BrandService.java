package com.siro.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-02 21:40:52
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);

    List<BrandEntity> getBrandsByIds(List<Long> brandIds);
}

