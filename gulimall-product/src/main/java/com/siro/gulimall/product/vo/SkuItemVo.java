package com.siro.gulimall.product.vo;

import com.siro.gulimall.product.entity.SkuImagesEntity;
import com.siro.gulimall.product.entity.SkuInfoEntity;
import com.siro.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @author Starsea
 * @date 2022-04-24 22:06
 */
@ToString
@Data
public class SkuItemVo {
    //sku基本信息
    private SkuInfoEntity info;

    //有货
    private boolean hasStock =true;
//    // 库存
//    Map<Long, Boolean> stocks;

    //sku的图片信息
    private List<SkuImagesEntity> images;

    //spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    //spu的介绍
    private SpuInfoDescEntity desc;

    //spu的规格参数信息
    private List<SpuItemAttrGroupVo> groupAttrs;

    //当前商品秒杀优惠信息
    private SeckillInfoVo seckillInfo;
}
