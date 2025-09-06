package com.siro.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 12:02:52
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据订单号查询库存工作单
     * @param orderSn
     * @return
     */
    WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
}

