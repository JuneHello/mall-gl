/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1-虚拟机
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 127.0.0.1:3307
 Source Schema         : gulimall_oms

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 21/05/2022 17:31:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mq_message
-- ----------------------------
DROP TABLE IF EXISTS `mq_message`;
CREATE TABLE `mq_message`  (
  `message_id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息内容json格式',
  `to_exchane` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发送的交换机',
  `routing_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由键',
  `class_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型',
  `message_status` int(1) NULL DEFAULT 0 COMMENT '0-新建 1-已发送 2-错误抵达 3-已抵达',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mq_message
-- ----------------------------

-- ----------------------------
-- Table structure for oms_order
-- ----------------------------
DROP TABLE IF EXISTS `oms_order`;
CREATE TABLE `oms_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `member_id` bigint(20) NULL DEFAULT NULL COMMENT 'member_id',
  `order_sn` char(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `coupon_id` bigint(20) NULL DEFAULT NULL COMMENT '使用的优惠券',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'create_time',
  `member_username` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `total_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '订单总额',
  `pay_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '应付总额',
  `freight_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '运费金额',
  `promotion_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '促销优化金额（促销价、满减、阶梯价）',
  `integration_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '积分抵扣金额',
  `coupon_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '优惠券抵扣金额',
  `discount_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '后台调整订单使用的折扣金额',
  `pay_type` tinyint(4) NULL DEFAULT NULL COMMENT '支付方式【1->支付宝；2->微信；3->银联； 4->货到付款；】',
  `source_type` tinyint(4) NULL DEFAULT NULL COMMENT '订单来源[0->PC订单；1->app订单]',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】',
  `delivery_company` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物流公司(配送方式)',
  `delivery_sn` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物流单号',
  `auto_confirm_day` int(11) NULL DEFAULT NULL COMMENT '自动确认时间（天）',
  `integration` int(11) NULL DEFAULT NULL COMMENT '可以获得的积分',
  `growth` int(11) NULL DEFAULT NULL COMMENT '可以获得的成长值',
  `bill_type` tinyint(4) NULL DEFAULT NULL COMMENT '发票类型[0->不开发票；1->电子发票；2->纸质发票]',
  `bill_header` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发票抬头',
  `bill_content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发票内容',
  `bill_receiver_phone` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收票人电话',
  `bill_receiver_email` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收票人邮箱',
  `receiver_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货人电话',
  `receiver_post_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货人邮编',
  `receiver_province` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '省份/直辖市',
  `receiver_city` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '城市',
  `receiver_region` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区',
  `receiver_detail_address` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '详细地址',
  `note` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单备注',
  `confirm_status` tinyint(4) NULL DEFAULT NULL COMMENT '确认收货状态[0->未确认；1->已确认]',
  `delete_status` tinyint(4) NULL DEFAULT NULL COMMENT '删除状态【0->未删除；1->已删除】',
  `use_integration` int(11) NULL DEFAULT NULL COMMENT '下单时使用的积分',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime NULL DEFAULT NULL COMMENT '确认收货时间',
  `comment_time` datetime NULL DEFAULT NULL COMMENT '评价时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oms_order
-- ----------------------------
INSERT INTO `oms_order` VALUES (1, 1, '202205151254423811525701181073006593', NULL, '2022-05-15 12:54:42', 'starsea', 5789.0000, 5795.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 5789, 5789, NULL, NULL, NULL, NULL, NULL, '迪丽热巴', '15666666666', '102218', '上海', '上海市', '黄浦区', '上城路138号五区六单元', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, '2022-05-15 12:54:42');
INSERT INTO `oms_order` VALUES (2, 1, '202205151332362571525710718404415489', NULL, '2022-05-15 13:32:36', 'starsea', 11578.0000, 11586.0000, 8.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 11578, 11578, NULL, NULL, NULL, NULL, NULL, '黄眉大王', '13888888888', '102218', '北京', '北京市', '昌平区', '大望路118号三区四单元', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, '2022-05-15 13:32:36');
INSERT INTO `oms_order` VALUES (4, 1, '202205151352287851525715720225202178', NULL, '2022-05-15 13:52:29', 'starsea', 11578.0000, 11586.0000, 8.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 11578, 11578, NULL, NULL, NULL, NULL, NULL, '黄眉大王', '13888888888', '102218', '北京', '北京市', '昌平区', '大望路118号三区四单元', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, '2022-05-15 13:52:29');
INSERT INTO `oms_order` VALUES (5, 1, '202205151450059601525730220668112898', NULL, '2022-05-15 14:50:06', 'starsea', 11578.0000, 11586.0000, 8.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 1, NULL, NULL, 7, 11578, 11578, NULL, NULL, NULL, NULL, NULL, '黄眉大王', '13888888888', '102218', '北京', '北京市', '昌平区', '大望路118号三区四单元', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, '2022-05-15 14:50:06');
INSERT INTO `oms_order` VALUES (6, 1, '202205151509182441525735053701693441', NULL, '2022-05-15 15:09:18', 'starsea', 5789.0000, 5795.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 1, NULL, NULL, 7, 5789, 5789, NULL, NULL, NULL, NULL, NULL, '迪丽热巴', '15666666666', '102218', '上海', '上海市', '黄浦区', '上城路138号五区六单元', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, '2022-05-15 15:09:18');

-- ----------------------------
-- Table structure for oms_order_item
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_item`;
CREATE TABLE `oms_order_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT 'order_id',
  `order_sn` char(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'order_sn',
  `spu_id` bigint(20) NULL DEFAULT NULL COMMENT 'spu_id',
  `spu_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'spu_name',
  `spu_pic` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'spu_pic',
  `spu_brand` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '品牌',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '商品分类id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT '商品sku编号',
  `sku_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品sku名字',
  `sku_pic` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品sku图片',
  `sku_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '商品sku价格',
  `sku_quantity` int(11) NULL DEFAULT NULL COMMENT '商品购买的数量',
  `sku_attrs_vals` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品销售属性组合（JSON）',
  `promotion_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '商品促销分解金额',
  `coupon_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '优惠券优惠分解金额',
  `integration_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '积分优惠分解金额',
  `real_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '该商品经过优惠后的分解金额',
  `gift_integration` int(11) NULL DEFAULT NULL COMMENT '赠送积分',
  `gift_growth` int(11) NULL DEFAULT NULL COMMENT '赠送成长值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单项信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oms_order_item
-- ----------------------------
INSERT INTO `oms_order_item` VALUES (14, NULL, '202205151254423811525701181073006593', 3, '华为 HUAWEI Mate 40 Pro', NULL, '华为', 225, 1, '华为 HUAWEI Mate 40 Pro 亮黑色  8GB+128GB 4G 全网通 麒麟9000旗舰芯片', 'https://gulimall-hello-2021.oss-cn-beijing.aliyuncs.com/2021-12-04/079716da-439b-4e88-8800-916ad5b8af18_0d40c24b264aa511.jpg', 5789.0000, 1, '颜色：亮黑色;版本： 8GB+128GB', 0.0000, 0.0000, 0.0000, 5789.0000, 5789, 5789);
INSERT INTO `oms_order_item` VALUES (17, NULL, '202205151332362571525710718404415489', 3, '华为 HUAWEI Mate 40 Pro', NULL, '华为', 225, 3, '华为 HUAWEI Mate 40 Pro 罗兰紫  8GB+128GB 4G 全网通 麒麟9000旗舰芯片', 'https://gulimall-hello-2021.oss-cn-beijing.aliyuncs.com/2021-12-04/079716da-439b-4e88-8800-916ad5b8af18_0d40c24b264aa511.jpg', 5789.0000, 1, '颜色：罗兰紫;版本： 8GB+128GB', 0.0000, 0.0000, 0.0000, 5789.0000, 5789, 5789);
INSERT INTO `oms_order_item` VALUES (18, NULL, '202205151332362571525710718404415489', 3, '华为 HUAWEI Mate 40 Pro', NULL, '华为', 225, 1, '华为 HUAWEI Mate 40 Pro 亮黑色  8GB+128GB 4G 全网通 麒麟9000旗舰芯片', 'https://gulimall-hello-2021.oss-cn-beijing.aliyuncs.com/2021-12-04/079716da-439b-4e88-8800-916ad5b8af18_0d40c24b264aa511.jpg', 5789.0000, 1, '颜色：亮黑色;版本： 8GB+128GB', 0.0000, 0.0000, 0.0000, 5789.0000, 5789, 5789);
INSERT INTO `oms_order_item` VALUES (22, NULL, '202205151352287851525715720225202178', 3, '华为 HUAWEI Mate 40 Pro', NULL, '华为', 225, 3, '华为 HUAWEI Mate 40 Pro 罗兰紫  8GB+128GB 4G 全网通 麒麟9000旗舰芯片', 'https://gulimall-hello-2021.oss-cn-beijing.aliyuncs.com/2021-12-04/079716da-439b-4e88-8800-916ad5b8af18_0d40c24b264aa511.jpg', 5789.0000, 1, '颜色：罗兰紫;版本： 8GB+128GB', 0.0000, 0.0000, 0.0000, 5789.0000, 5789, 5789);
INSERT INTO `oms_order_item` VALUES (23, NULL, '202205151352287851525715720225202178', 3, '华为 HUAWEI Mate 40 Pro', NULL, '华为', 225, 1, '华为 HUAWEI Mate 40 Pro 亮黑色  8GB+128GB 4G 全网通 麒麟9000旗舰芯片', 'https://gulimall-hello-2021.oss-cn-beijing.aliyuncs.com/2021-12-04/079716da-439b-4e88-8800-916ad5b8af18_0d40c24b264aa511.jpg', 5789.0000, 1, '颜色：亮黑色;版本： 8GB+128GB', 0.0000, 0.0000, 0.0000, 5789.0000, 5789, 5789);
INSERT INTO `oms_order_item` VALUES (24, NULL, '202205151410544911525720357892263937', 3, '华为 HUAWEI Mate 40 Pro', NULL, '华为', 225, 3, '华为 HUAWEI Mate 40 Pro 罗兰紫  8GB+128GB 4G 全网通 麒麟9000旗舰芯片', 'https://gulimall-hello-2021.oss-cn-beijing.aliyuncs.com/2021-12-04/079716da-439b-4e88-8800-916ad5b8af18_0d40c24b264aa511.jpg', 5789.0000, 1, '颜色：罗兰紫;版本： 8GB+128GB', 0.0000, 0.0000, 0.0000, 5789.0000, 5789, 5789);
INSERT INTO `oms_order_item` VALUES (25, NULL, '202205151410544911525720357892263937', 3, '华为 HUAWEI Mate 40 Pro', NULL, '华为', 225, 1, '华为 HUAWEI Mate 40 Pro 亮黑色  8GB+128GB 4G 全网通 麒麟9000旗舰芯片', 'https://gulimall-hello-2021.oss-cn-beijing.aliyuncs.com/2021-12-04/079716da-439b-4e88-8800-916ad5b8af18_0d40c24b264aa511.jpg', 5789.0000, 1, '颜色：亮黑色;版本： 8GB+128GB', 0.0000, 0.0000, 0.0000, 5789.0000, 5789, 5789);
INSERT INTO `oms_order_item` VALUES (26, NULL, '202205151450059601525730220668112898', 3, '华为 HUAWEI Mate 40 Pro', NULL, '华为', 225, 3, '华为 HUAWEI Mate 40 Pro 罗兰紫  8GB+128GB 4G 全网通 麒麟9000旗舰芯片', 'https://gulimall-hello-2021.oss-cn-beijing.aliyuncs.com/2021-12-04/079716da-439b-4e88-8800-916ad5b8af18_0d40c24b264aa511.jpg', 5789.0000, 1, '颜色：罗兰紫;版本： 8GB+128GB', 0.0000, 0.0000, 0.0000, 5789.0000, 5789, 5789);
INSERT INTO `oms_order_item` VALUES (27, NULL, '202205151450059601525730220668112898', 3, '华为 HUAWEI Mate 40 Pro', NULL, '华为', 225, 1, '华为 HUAWEI Mate 40 Pro 亮黑色  8GB+128GB 4G 全网通 麒麟9000旗舰芯片', 'https://gulimall-hello-2021.oss-cn-beijing.aliyuncs.com/2021-12-04/079716da-439b-4e88-8800-916ad5b8af18_0d40c24b264aa511.jpg', 5789.0000, 1, '颜色：亮黑色;版本： 8GB+128GB', 0.0000, 0.0000, 0.0000, 5789.0000, 5789, 5789);
INSERT INTO `oms_order_item` VALUES (28, NULL, '202205151509182441525735053701693441', 3, '华为 HUAWEI Mate 40 Pro', NULL, '华为', 225, 5, '华为 HUAWEI Mate 40 Pro 星河银  8GB+128GB 4G 全网通 麒麟9000旗舰芯片', 'https://gulimall-hello-2021.oss-cn-beijing.aliyuncs.com/2021-12-04/079716da-439b-4e88-8800-916ad5b8af18_0d40c24b264aa511.jpg', 5789.0000, 1, '颜色：星河银;版本： 8GB+128GB', 0.0000, 0.0000, 0.0000, 5789.0000, 5789, 5789);

-- ----------------------------
-- Table structure for oms_order_operate_history
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_operate_history`;
CREATE TABLE `oms_order_operate_history`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '订单id',
  `operate_man` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人[用户；系统；后台管理员]',
  `create_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `order_status` tinyint(4) NULL DEFAULT NULL COMMENT '订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】',
  `note` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单操作历史记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oms_order_operate_history
-- ----------------------------

-- ----------------------------
-- Table structure for oms_order_return_apply
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_return_apply`;
CREATE TABLE `oms_order_return_apply`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT 'order_id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT '退货商品id',
  `order_sn` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '申请时间',
  `member_username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员用户名',
  `return_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '退款金额',
  `return_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退货人姓名',
  `return_phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退货人电话',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '申请状态[0->待处理；1->退货中；2->已完成；3->已拒绝]',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `sku_img` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品图片',
  `sku_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `sku_brand` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品品牌',
  `sku_attrs_vals` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品销售属性(JSON)',
  `sku_count` int(11) NULL DEFAULT NULL COMMENT '退货数量',
  `sku_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '商品单价',
  `sku_real_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '商品实际支付单价',
  `reason` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原因',
  `description述` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `desc_pics` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '凭证图片，以逗号隔开',
  `handle_note` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '处理备注',
  `handle_man` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '处理人员',
  `receive_man` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货人',
  `receive_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
  `receive_note` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货备注',
  `receive_phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货电话',
  `company_address` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司收货地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单退货申请' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oms_order_return_apply
-- ----------------------------

-- ----------------------------
-- Table structure for oms_order_return_reason
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_return_reason`;
CREATE TABLE `oms_order_return_reason`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退货原因名',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '启用状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'create_time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '退货原因' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oms_order_return_reason
-- ----------------------------

-- ----------------------------
-- Table structure for oms_order_setting
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_setting`;
CREATE TABLE `oms_order_setting`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `flash_order_overtime` int(11) NULL DEFAULT NULL COMMENT '秒杀订单超时关闭时间(分)',
  `normal_order_overtime` int(11) NULL DEFAULT NULL COMMENT '正常订单超时时间(分)',
  `confirm_overtime` int(11) NULL DEFAULT NULL COMMENT '发货后自动确认收货时间（天）',
  `finish_overtime` int(11) NULL DEFAULT NULL COMMENT '自动完成交易时间，不能申请退货（天）',
  `comment_overtime` int(11) NULL DEFAULT NULL COMMENT '订单完成后自动好评时间（天）',
  `member_level` tinyint(2) NULL DEFAULT NULL COMMENT '会员等级【0-不限会员等级，全部通用；其他-对应的其他会员等级】',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单配置信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oms_order_setting
-- ----------------------------

-- ----------------------------
-- Table structure for oms_payment_info
-- ----------------------------
DROP TABLE IF EXISTS `oms_payment_info`;
CREATE TABLE `oms_payment_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_sn` char(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单号（对外业务号）',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '订单id',
  `alipay_trade_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付宝交易流水号',
  `total_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '支付总金额',
  `subject` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '交易内容',
  `payment_status` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `confirm_time` datetime NULL DEFAULT NULL COMMENT '确认时间',
  `callback_content` varchar(4000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回调内容',
  `callback_time` datetime NULL DEFAULT NULL COMMENT '回调时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order_sn`(`order_sn`) USING BTREE,
  INDEX `alipay_trade_no`(`alipay_trade_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '支付信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oms_payment_info
-- ----------------------------
INSERT INTO `oms_payment_info` VALUES (2, '202205151450059601525730220668112898', NULL, '2022051522001489070501752510', NULL, NULL, 'TRADE_SUCCESS', NULL, NULL, NULL, '2022-05-15 14:50:42');
INSERT INTO `oms_payment_info` VALUES (3, '202205151509182441525735053701693441', NULL, '2022051522001489070501752357', NULL, NULL, 'TRADE_SUCCESS', NULL, NULL, NULL, '2022-05-15 15:09:43');

-- ----------------------------
-- Table structure for oms_refund_info
-- ----------------------------
DROP TABLE IF EXISTS `oms_refund_info`;
CREATE TABLE `oms_refund_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_return_id` bigint(20) NULL DEFAULT NULL COMMENT '退款的订单',
  `refund` decimal(18, 4) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_sn` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退款交易流水号',
  `refund_status` tinyint(1) NULL DEFAULT NULL COMMENT '退款状态',
  `refund_channel` tinyint(4) NULL DEFAULT NULL COMMENT '退款渠道[1-支付宝，2-微信，3-银联，4-汇款]',
  `refund_content` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '退款信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oms_refund_info
-- ----------------------------

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `context` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ux_undo_log`(`xid`, `branch_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of undo_log
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
