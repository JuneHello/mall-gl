package com.siro.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.coupon.entity.HomeSubjectEntity;

import java.util.Map;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 11:31:54
 */
public interface HomeSubjectService extends IService<HomeSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

