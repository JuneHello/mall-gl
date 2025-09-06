package com.siro.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 11:59:08
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

