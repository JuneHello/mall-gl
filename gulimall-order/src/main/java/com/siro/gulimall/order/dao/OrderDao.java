package com.siro.gulimall.order.dao;

import com.siro.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 11:59:07
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    /**
     * 修改订单状态
     * @param outTradeNo
     * @param code
     */
    void updateOrderStatus(@Param("outTradeNo") String outTradeNo, @Param("code") Integer code);
}
