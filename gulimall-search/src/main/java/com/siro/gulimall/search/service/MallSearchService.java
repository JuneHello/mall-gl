package com.siro.gulimall.search.service;

import com.siro.gulimall.search.vo.SearchParam;
import com.siro.gulimall.search.vo.SearchResult;

/**
 * @author Starsea
 * @date 2022-04-11 20:53
 */
public interface MallSearchService {
    /**
     * 根据传递过来的页面查询参数，去es中检索商品
     * @param param 检索所有参数
     * @return 返回检索的所有结果
     */
    SearchResult search(SearchParam param);
}
