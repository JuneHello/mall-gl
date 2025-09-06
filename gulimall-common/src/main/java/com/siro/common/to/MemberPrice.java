package com.siro.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 远程调用的实体类
 * @author starsea
 * @date 2021-11-28 10:30
 */
@Data
public class MemberPrice {
    private Long id;
    private String name;
    private BigDecimal price;
}
