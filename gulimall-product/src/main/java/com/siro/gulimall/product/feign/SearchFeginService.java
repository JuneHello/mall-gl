package com.siro.gulimall.product.feign;

import com.siro.common.to.es.SkuEsModel;
import com.siro.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Starsea
 * @date 2022-03-29 20:46
 */
@FeignClient("gulimall-search")
public interface SearchFeginService {

    @PostMapping("/search/save/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
