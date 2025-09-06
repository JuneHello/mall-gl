package com.siro.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @author starsea
 * @date 2022-05-12
 */
@Data
public class StockLockedTo {

    //库存工作单id
    private Long id;
    //工作单详情
    private StockDetailTo detail;
}
