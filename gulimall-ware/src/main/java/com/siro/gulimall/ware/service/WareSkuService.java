package com.siro.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.to.mq.OrderTo;
import com.siro.common.to.mq.StockLockedTo;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.ware.entity.WareSkuEntity;
import com.siro.gulimall.ware.vo.HasStockVo;
import com.siro.gulimall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 12:02:52
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据采购单的数据进行入库操作
     * @param skuId sku_id
     * @param wareId 仓库id
     * @param skuNum 库存数
     */
    void saveStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 查询sku是否有库存
     * @param skuIds
     * @return
     */
    List<HasStockVo> getSkuHasStock(List<Long> skuIds);

    /**
     * 为某个订单锁定库存锁定库存
     * @param wareSkuLockVo
     * @return
     */
    Boolean orderLockStock(WareSkuLockVo wareSkuLockVo);

    /**
     * 解锁库存
     * @param stockLockedTo
     */
    void unlockStock(StockLockedTo stockLockedTo);

    /**
     * 解锁订单
     * @param orderTo
     */
    void unlockStock(OrderTo orderTo);
}

