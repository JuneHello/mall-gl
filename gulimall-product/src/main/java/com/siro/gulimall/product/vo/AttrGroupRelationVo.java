package com.siro.gulimall.product.vo;

import lombok.Data;

/**
 * @author starsea
 * @date 2021-11-21 17:19
 */
@Data
public class AttrGroupRelationVo {
    /*
        [{"attrId":1,"attrGroupId":2}]
     */
    private Long attrId;
    private Long attrGroupId;
}
