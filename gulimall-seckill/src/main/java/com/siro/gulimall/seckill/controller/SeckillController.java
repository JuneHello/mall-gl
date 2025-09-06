package com.siro.gulimall.seckill.controller;

import com.siro.common.utils.R;
import com.siro.gulimall.seckill.service.SeckillService;
import com.siro.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author starsea
 * @date 2022-05-14
 */
@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 当前时间可以参与秒杀的商品信息
     * @return
     */
    @GetMapping(value = "/getCurrentSeckillSkus")
    @ResponseBody
    public R getCurrentSeckillSkus() {
        //获取到当前可以参加秒杀商品的信息
        List<SeckillSkuRedisTo> vos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(vos);
    }

    /**
     * 根据skuId查询商品是否参加秒杀活动
     * @param skuId
     * @return
     */
    @GetMapping(value = "/sku/seckill/{skuId}")
    @ResponseBody
    public R getSkuSeckilInfo(@PathVariable("skuId") Long skuId) {
        SeckillSkuRedisTo to = seckillService.getSkuSeckilInfo(skuId);
        return R.ok().setData(to);
    }

    /**
     * 商品进行秒杀(秒杀开始)
     * @param killId
     * @param key 随机码
     * @param num 购买数量
     * @return
     */
    @GetMapping(value = "/kill")
    public String seckill(@RequestParam("killId") String killId,
                          @RequestParam("key") String key,
                          @RequestParam("num") Integer num,
                          Model model) {
        String orderSn = null;
        try {
            //1、判断是否登录
            orderSn = seckillService.kill(killId,key,num);
            model.addAttribute("orderSn",orderSn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    /*
    页面加上这个 没有在redis的商品详情页有问题，有待解决
    <li style="color: red;" th:if="#{item.seckillInfo!=null}">
        <span th:if="${#dates.createNow().getTime() < item.seckillInfo.startTime}">
            商品将会在[[${#dates.format(new java.util.Date(item.seckillInfo.startTime),"yyyy-MM-dd HH:mm:ss")}]]进行秒杀
        </span>
        <span th:if="${#dates.createNow().getTime() >= item.seckillInfo.startTime && #dates.createNow().getTime() <= item.seckillInfo.endTime}">
            秒杀价格：￥[[${#numbers.formatDecimal(item.seckillInfo.seckillPrice,1,2)}]]
        </span>
    </li>
     */
}
