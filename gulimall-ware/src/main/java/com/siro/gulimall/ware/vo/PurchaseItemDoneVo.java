package com.siro.gulimall.ware.vo;

import lombok.Data;

/**
 * @author starsea
 * @date 2021-12-04 16:49
 */
@Data
public class PurchaseItemDoneVo {
    // {itemId:1,status:4,reason:""}
    private Long itemId;
    private Integer status;
    private String reason;
}
