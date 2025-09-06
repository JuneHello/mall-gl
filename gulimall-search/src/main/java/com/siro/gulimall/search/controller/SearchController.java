package com.siro.gulimall.search.controller;

import com.siro.gulimall.search.service.MallSearchService;
import com.siro.gulimall.search.vo.SearchParam;
import com.siro.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Starsea
 * @date 2022-04-11 20:44
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    /**
     * 跳转搜索页面
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {
        //拿到地址栏中拼接的数据
        String queryString = request.getQueryString();
        param.set_queryString(queryString);
        //根据传递过来的页面查询参数，去es中检索商品
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);
        return "list";
    }
}
