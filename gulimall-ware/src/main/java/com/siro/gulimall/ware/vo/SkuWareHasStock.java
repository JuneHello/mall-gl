package com.siro.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author starsea
 * @date 2022-05-11
 */
@Data
public class SkuWareHasStock {
    private Long skuId;
    private Integer num;
    private List<Long> wareId;
}
