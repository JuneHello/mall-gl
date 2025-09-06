package com.siro.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 * @author Starsea
 * @date 2022-04-11 20:52
 */
@Data
public class SearchParam {

    /**
     * keyword=小米&sort=saleCount_desc/asc&hasStock=0/1&skuPrice=400_1900&brandId=1&catalog3Id=1&attrs=1_3G:4G:5G&attrs=2_骁龙845&attrs=4_高清屏
     */

    private String keyword;//页面传递过来的全文匹配关键字
    private Long catalog3Id;//三级分类id

    /**
     * 销量：sort=saleCount_asc/desc
     * 价格：sort=skuPrice_asc/desc
     * 综合排序（热度评分）：sort=hotScore_asc/desc
     */
    private String sort;//排序条件

    /**
     * hasStock=0/1
     * skuPrice=1_500/_500/500_
     * brandId=1
     * attrs=2_5寸:6寸
     */
    private Integer hasStock;//是否只显示有货（0-无库存、1-有库存）
    private String skuPrice;//价格区间
    private List<Long> brandId;//按照品牌id筛选，可以多选
    private List<String> attrs;//按照属性筛选，可以多选

    private Integer pageNum = 1;//页码

    private String _queryString;//地址栏中拼接的原生查询条件
}
