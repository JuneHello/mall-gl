package com.siro.gulimall.product.controller;

import com.siro.common.utils.PageUtils;
import com.siro.common.utils.R;
import com.siro.common.valid.AddGroup;
import com.siro.common.valid.UpdateGroup;
import com.siro.common.valid.UpdateStatusGroup;
import com.siro.gulimall.product.entity.BrandEntity;
import com.siro.gulimall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 品牌
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-02 21:40:52
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 根据品牌id查询所有品牌
     * @param brandIds
     * @return
     */
    @GetMapping("/infos")
    public R info(@RequestParam("brandIds") List<Long> brandIds){
        List<BrandEntity> brand = brandService.getBrandsByIds(brandIds);
        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand/*, BindingResult result*/){
        //if (result.hasErrors()) {
        //    Map<String,String> map = new HashMap<>();
        //    result.getFieldErrors().forEach((item) -> {
        //        // 获取错误提示
        //        String message = item.getDefaultMessage();
        //        // 获取错误属性的字段
        //        String field = item.getField();
        //        map.put(message,field);
        //    });
        //    return R.error(400,"提交的数据不合法").put("data",map);
        //} else {
        //    brandService.save(brand);
        //}
        brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
		brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
