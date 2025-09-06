package com.siro.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author starsea
 * @date 2022-05-08
 */
@Data
public class FareVo {
    //地址信息
    private MemberAddressVo address;
    private BigDecimal fare;
}
