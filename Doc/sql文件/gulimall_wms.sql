/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1-虚拟机
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 127.0.0.1:3307
 Source Schema         : gulimall_wms

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 21/05/2022 17:32:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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

-- ----------------------------
-- Table structure for wms_purchase
-- ----------------------------
DROP TABLE IF EXISTS `wms_purchase`;
CREATE TABLE `wms_purchase`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '采购单id',
  `assignee_id` bigint(20) NULL DEFAULT NULL COMMENT '采购人id',
  `assignee_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '采购人名',
  `phone` char(13) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `priority` int(4) NULL DEFAULT NULL COMMENT '优先级',
  `status` int(4) NULL DEFAULT NULL COMMENT '状态',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '总金额',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '采购信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wms_purchase
-- ----------------------------
INSERT INTO `wms_purchase` VALUES (1, 2, 'zhangsan', '13888888888', 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `wms_purchase` VALUES (2, 1, 'admin', '13612345678', NULL, 4, NULL, NULL, '2021-12-04 15:44:36', '2021-12-04 18:27:43');
INSERT INTO `wms_purchase` VALUES (3, 1, 'admin', '13612345678', NULL, 3, NULL, NULL, '2021-12-04 18:25:47', '2021-12-04 18:30:08');

-- ----------------------------
-- Table structure for wms_purchase_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_purchase_detail`;
CREATE TABLE `wms_purchase_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `purchase_id` bigint(20) NULL DEFAULT NULL COMMENT '采购单id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT '采购商品id',
  `sku_num` int(11) NULL DEFAULT NULL COMMENT '采购数量',
  `sku_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '采购金额',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态[0新建，1已分配，2正在采购，3已完成，4采购失败]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '采购需求信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wms_purchase_detail
-- ----------------------------
INSERT INTO `wms_purchase_detail` VALUES (1, 2, 1, 10, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (2, 2, 2, 5, NULL, 1, 4);
INSERT INTO `wms_purchase_detail` VALUES (3, 3, 3, 15, NULL, 2, 3);
INSERT INTO `wms_purchase_detail` VALUES (4, 3, 4, 10, NULL, 2, 3);
INSERT INTO `wms_purchase_detail` VALUES (5, 3, 5, 5, NULL, 2, 3);

-- ----------------------------
-- Table structure for wms_ware_info
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_info`;
CREATE TABLE `wms_ware_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '仓库名',
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '仓库地址',
  `areacode` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区域编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '仓库信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wms_ware_info
-- ----------------------------
INSERT INTO `wms_ware_info` VALUES (1, '1号仓库', '北京海淀区', '12345');
INSERT INTO `wms_ware_info` VALUES (2, '2号仓库', '北京朝阳区', '112233');

-- ----------------------------
-- Table structure for wms_ware_order_task
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_order_task`;
CREATE TABLE `wms_ware_order_task`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT 'order_id',
  `order_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'order_sn',
  `consignee` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货人',
  `consignee_tel` char(15) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货人电话',
  `delivery_address` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配送地址',
  `order_comment` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单备注',
  `payment_way` tinyint(1) NULL DEFAULT NULL COMMENT '付款方式【 1:在线付款 2:货到付款】',
  `task_status` tinyint(2) NULL DEFAULT NULL COMMENT '任务状态',
  `order_body` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单描述',
  `tracking_no` char(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物流单号',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'create_time',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `task_comment` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作单备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '库存工作单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wms_ware_order_task
-- ----------------------------
INSERT INTO `wms_ware_order_task` VALUES (1, NULL, '202205151254423811525701181073006593', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (2, NULL, '202205151332362571525710718404415489', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (4, NULL, '202205151352287851525715720225202178', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (6, NULL, '202205151450059601525730220668112898', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (7, NULL, '202205151509182441525735053701693441', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for wms_ware_order_task_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_order_task_detail`;
CREATE TABLE `wms_ware_order_task_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `sku_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'sku_name',
  `sku_num` int(11) NULL DEFAULT NULL COMMENT '购买个数',
  `task_id` bigint(20) NULL DEFAULT NULL COMMENT '工作单id',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '库存id',
  `lock_status` int(2) NULL DEFAULT NULL COMMENT '锁定状态 1-锁定 2-解锁 3-扣减',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '库存工作单详情' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wms_ware_order_task_detail
-- ----------------------------
INSERT INTO `wms_ware_order_task_detail` VALUES (1, 1, NULL, 1, 1, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (2, 3, NULL, 1, 2, 2, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (3, 1, NULL, 1, 2, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (4, 3, NULL, 1, 4, 2, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (5, 1, NULL, 1, 4, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (6, 3, NULL, 1, 6, 2, 1);
INSERT INTO `wms_ware_order_task_detail` VALUES (7, 1, NULL, 1, 6, 1, 1);
INSERT INTO `wms_ware_order_task_detail` VALUES (8, 5, NULL, 1, 7, 2, 1);

-- ----------------------------
-- Table structure for wms_ware_sku
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_sku`;
CREATE TABLE `wms_ware_sku`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `stock` int(11) NULL DEFAULT NULL COMMENT '库存数',
  `sku_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'sku_name',
  `stock_locked` int(11) NULL DEFAULT 0 COMMENT '锁定库存',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品库存' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wms_ware_sku
-- ----------------------------
INSERT INTO `wms_ware_sku` VALUES (1, 1, 1, 110, '华为', 1);
INSERT INTO `wms_ware_sku` VALUES (2, 3, 2, 50, NULL, 1);
INSERT INTO `wms_ware_sku` VALUES (3, 4, 2, 20, NULL, 0);
INSERT INTO `wms_ware_sku` VALUES (4, 5, 2, 5, NULL, 1);

SET FOREIGN_KEY_CHECKS = 1;
