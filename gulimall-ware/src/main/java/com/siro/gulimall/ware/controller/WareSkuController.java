package com.siro.gulimall.ware.controller;

import com.siro.common.exception.BizCodeEnum;
import com.siro.common.utils.PageUtils;
import com.siro.common.utils.R;
import com.siro.gulimall.ware.entity.WareSkuEntity;
import com.siro.common.exception.NoStockException;
import com.siro.gulimall.ware.service.WareSkuService;
import com.siro.gulimall.ware.vo.HasStockVo;
import com.siro.gulimall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品库存
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 12:02:52
 */
@RestController
@RequestMapping("/ware/waresku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 为某个订单锁定库存锁定库存
     * @param wareSkuLockVo
     * @return
     */
    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo) {
        try {
            Boolean stock = wareSkuService.orderLockStock(wareSkuLockVo);
            return R.ok();
        } catch (NoStockException e) {
            return R.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(), BizCodeEnum.NO_STOCK_EXCEPTION.getMessage());
        }
    }

    /**
     * 查询sku是否有库存
     * @param skuIds
     * @return
     */
    @PostMapping("/hosStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds) {
        List<HasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);
        // 疑问：为啥hashMap返回泛型没有用
        return R.ok().setData(vos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
