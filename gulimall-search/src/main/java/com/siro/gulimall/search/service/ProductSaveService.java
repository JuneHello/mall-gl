package com.siro.gulimall.search.service;

import com.siro.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author Starsea
 * @date 2022-03-29 20:30
 */
public interface ProductSaveService {
    /**
     * es中保存商品上架
     * @param skuEsModels
     * @return
     */
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
