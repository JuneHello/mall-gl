package com.siro.common.to;

import lombok.Data;

/**
 * @author Starsea
 * @date 2022-03-28 21:31
 */
@Data
public class SkuHasStockVo {
    private Long skuId;
    private Boolean hasStock;//是否有库存
}
