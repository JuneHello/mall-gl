package com.siro.gulimall.product.controller;

import com.siro.common.utils.PageUtils;
import com.siro.common.utils.R;
import com.siro.gulimall.product.entity.ProductAttrValueEntity;
import com.siro.gulimall.product.service.AttrService;
import com.siro.gulimall.product.service.ProductAttrValueService;
import com.siro.gulimall.product.vo.AttrRespVo;
import com.siro.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品属性
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-02 21:40:52
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    /**
     * 修改商品规格
     * @param spuId
     * @param entities
     * @return
     */
    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable Long spuId,
                    @RequestBody List<ProductAttrValueEntity> entities){
        productAttrValueService.updateSpuAttr(spuId,entities);
        return R.ok();
    }

    /**
     * 获取spu规格
     * @return
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> list = productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data",list);
    }

    /**
     * 根据条件获取分页的 基本base（或销售sale）属性列表
     * @param params
     * @param catelogId
     * @param type
     * @return
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId,
                  @PathVariable("attrType") String type){
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, type);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		//AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
