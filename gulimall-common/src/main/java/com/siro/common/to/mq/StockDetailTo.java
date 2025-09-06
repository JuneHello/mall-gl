package com.siro.common.to.mq;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author starsea
 * @date 2022-05-12
 */
@Data
public class StockDetailTo {

    /**
     * id
     */
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 库存id
     */
    private Long wareId;
    /**
     * 锁定状态 1-锁定 2-解锁 3-扣减
     */
    private Integer lockStatus;
}
