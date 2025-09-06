package com.siro.gulimall.ware.controller;

import com.siro.common.utils.PageUtils;
import com.siro.common.utils.R;
import com.siro.gulimall.ware.entity.PurchaseEntity;
import com.siro.gulimall.ware.service.PurchaseService;
import com.siro.gulimall.ware.vo.MergeVo;
import com.siro.gulimall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;



/**
 * 采购信息
 *
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-03 12:02:52
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @Value("${config.info}")
    private String configInfo;

    @GetMapping("/getconfig")
    public String getConfigInfo(){
        return configInfo;
    }
    /**
     * 完成采购
     *
     * 由于这个不属于后台管理系统，所以我们使用postman模拟采购人员app：
     * http://localhost:88/api/ware/purchase/done
     * 发送post请求的JSON数据：
     *  {
     *    "id": 2,
     *    "items": [
     *        {"itemId":1,"status":3,"reason":""},
     *        {"itemId":2,"status":4,"reason":"无货"}
     *     ]
     * }
     * @param purchaseDoneVo
     * @return
     */
    @PostMapping("/done")
    public R done(@RequestBody PurchaseDoneVo purchaseDoneVo){
        purchaseService.done(purchaseDoneVo);
        return R.ok();
    }

    /**
     * 领取采购单
     *
     * 由于这个不属于后台管理系统，所以我们使用postman模拟采购人员app：
     * http://localhost:88/api/ware/purchase/received
     * 发送post请求的JSON数据：
     * [2]
     * @param ids 采购单id
     * @return
     */
    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids){
        purchaseService.received(ids);
        return R.ok();
    }

    /**
     * 合并采购需求
     * @param mergeVo
     * @return
     */
    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo){
        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }

    /**
     * 查询未领取的采购单
     * @param params
     * @return
     */
    @RequestMapping("/unreceive/list")
    //@RequiresPermissions("ware:purchase:list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceive(params);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
