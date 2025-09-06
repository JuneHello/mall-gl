package com.siro.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.ware.entity.WareInfoEntity;
import com.siro.gulimall.ware.vo.FareVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 12:02:52
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据用户收货地址计算运费
     * @param addrId
     * @return
     */
    FareVo getFare(Long addrId);
}

