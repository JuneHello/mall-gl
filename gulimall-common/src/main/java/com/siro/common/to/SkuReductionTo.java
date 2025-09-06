package com.siro.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author starsea
 * @date 2021-11-28 10:31
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
