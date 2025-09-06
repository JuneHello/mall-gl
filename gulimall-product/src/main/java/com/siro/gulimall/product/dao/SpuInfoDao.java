package com.siro.gulimall.product.dao;

import com.siro.gulimall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-02 21:40:52
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    /**
     * 修改spu中商品的状态
     * @param spuId
     * @param code
     */
    void updaSpuStatus(@Param("spuId") Long spuId, @Param("code") Integer code);
}
