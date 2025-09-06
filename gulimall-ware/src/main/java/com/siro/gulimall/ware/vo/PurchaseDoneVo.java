package com.siro.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author starsea
 * @date 2021-12-04 16:49
 */
@Data
public class PurchaseDoneVo {
    /*
        {
           id: 123,//采购单id
           items: [{itemId:1,status:4,reason:""}]//完成/失败的需求详情
        }
     */

    @NotNull
    private Long id;// 采购单id
    private List<PurchaseItemDoneVo> items;
}
