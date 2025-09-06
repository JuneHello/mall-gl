package com.siro.gulimall.cart.controller;

import com.siro.gulimall.cart.service.CartService;
import com.siro.gulimall.cart.vo.CartItemVo;
import com.siro.gulimall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author starsea
 * @date 2022-05-05
 */
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 获取购物车中所有选中的数据
     * @return
     */
    @ResponseBody
    @GetMapping("/currentUserCartItem")
    public List<CartItemVo> getCurrentUserCartItem() {
        return cartService.getCurrentUserCartItem();
    }

    /**
     * 跳转购物车页面
     *
     * 浏览器有一个cookie：user-key；标识用户身份，一个月后过期。
     * 如果第一次使用jd的购物车功能，都会给一个临时的用户身份；
     * 浏览器以后保存，每次访问都会带上这个cookie。
     *
     * 登录：session中。
     * 未登录：按照cookie里面带过来user-key来做。
     * 第一次，如果没有临时用户，帮忙创建一个临时用户。
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        //快速得到用户信息：id,user-key
        CartVo cartVo = cartService.getCart();
        model.addAttribute("cart",cartVo);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num,
                            RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId,num);
        //将数据放在session里面可以在页面取出，但是只能取一次
//        redirectAttributes.addFlashAttribute("skuId", skuId);
        //将数据放在url后面拼接
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    /**
     * 跳转到成功页：为了解决页面刷新后无限次添加商品
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId, Model model) {
        //重定向到成功页面，再次查询购物车数量
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("item", cartItemVo);
        return "success";
    }

    /**
     * 勾选购物项
     * @param skuId
     * @param check
     * @return
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check) {
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 修改购物项数量
     * @return
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
        cartService.countItem(skuId, num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 删除某件商品
     * @param skuId
     * @return
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
}
