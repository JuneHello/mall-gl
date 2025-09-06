package com.siro.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 二级分类vo
 * @author Starsea
 * @date 2022-03-30 21:50
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catelog2Vo {
    // 一级父分类id
    private String catalog1Id;
    // 三级子分类
    private List<catelog3Vo> catalog3List;
    private String id;
    private String name;

    /**
     * 三级分类vo
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class catelog3Vo {
        // 父分类id，二级分类id
        private String catalog2Id;
        private String id;
        private String name;
    }
}
