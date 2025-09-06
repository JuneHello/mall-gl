package com.siro.gulimall.product.vo;

import lombok.Data;

/**
 * @author starsea
 * @date 2021-11-21 15:07
 */
@Data
public class AttrRespVo extends AttrVo {
    // 所属分类名字
    private String catelogName;
    // 所属分组名字
    private String groupName;

    // 分类完整路径
    private Long[] catelogPath;
}
