package com.siro.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author starsea
 * @date 2021-11-27 21:48
 */
@Data
public class MemberPrice {
    private Long id;
    private String name;
    private BigDecimal price;
}
