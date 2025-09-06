package com.siro.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.product.entity.CategoryEntity;
import com.siro.gulimall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-02 21:40:52
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询所有分类以及子分类，以树形结构组装起来
     * @return
     */
    List<CategoryEntity> listWithTree();

    /**
     * 删除菜单
     * @param asList
     */
    void removeMenuByIds(List<Long> asList);

    /**
     * 根据所属分类id查询完整路径
     * 格式：[父/子/孙]
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    /**
     * 查询所有一级分类
     * @return
     */
    List<CategoryEntity> getLevel1Categorys();

    /**
     * 查询二级分类和三级分类
     * @return
     */
    Map<String, List<Catelog2Vo>> getCatalogJson();
}

