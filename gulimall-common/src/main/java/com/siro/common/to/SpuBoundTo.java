package com.siro.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠券服务 与 商品服务 共用的实体类
 * @author starsea
 * @date 2021-11-27 22:57
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
