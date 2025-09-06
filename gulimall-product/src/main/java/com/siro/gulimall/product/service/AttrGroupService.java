package com.siro.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siro.common.utils.PageUtils;
import com.siro.gulimall.product.entity.AttrGroupEntity;
import com.siro.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.siro.gulimall.product.vo.SkuItemVo;
import com.siro.gulimall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-02 21:40:52
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据属树形节点id查询对应属性分类数据
     * @param params
     * @param catelogId
     * @return
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    /**
     * 获取分类下所有分组&关联属性
     * @param catelogId
     * @return
     */
    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    /**
     * 查出当前spu对应的所有属性的分组信息以及分组下所有属性对应的值
     * @param spuId
     * @param catalogId
     * @return
     */
    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

