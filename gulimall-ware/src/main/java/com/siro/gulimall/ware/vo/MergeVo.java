package com.siro.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author starsea
 * @date 2021-12-04 15:06
 */
@Data
public class MergeVo {
    /*
        {
          purchaseId: 1, //整单id
          items:[1,2,3,4] //合并项集合
        }
    */
    // 采购单id
    private Long purchaseId;
    // 采购项集合
    private List<Long> items;
}
