package com.siro.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 12:02:52
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<PurchaseDetailEntity> listDetailByPurchaseId(Long id);
}

