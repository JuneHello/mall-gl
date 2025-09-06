package com.siro.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * spu信息介绍
 * 
 * @author starsea
 * @email 1065510476@qq.com
 * @date 2021-10-02 21:40:52
 */
@Data
@TableName("pms_spu_info_desc")
public class SpuInfoDescEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	@TableId(type = IdType.INPUT)// 使用自己输入的主键id
	private Long spuId;
	/**
	 * 商品介绍
	 */
	private String decript;

}
