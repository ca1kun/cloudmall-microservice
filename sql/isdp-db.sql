/*
Navicat MySQL Data Transfer

Source Server         : courseDesign
Source Server Version : 80028
Source Host           : localhost:3306
Source Database       : isdp-db

Target Server Type    : MYSQL
Target Server Version : 80028
File Encoding         : 65001

Date: 2026-04-05 15:57:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for oms_cart_item
-- ----------------------------
DROP TABLE IF EXISTS `oms_cart_item`;
CREATE TABLE `oms_cart_item` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `product_id` bigint DEFAULT NULL,
                                 `product_sku_id` bigint DEFAULT NULL,
                                 `member_id` bigint DEFAULT NULL,
                                 `quantity` int DEFAULT NULL COMMENT '购买数量',
                                 `price` decimal(10,2) DEFAULT NULL COMMENT '添加时价格',
                                 `product_name` varchar(200) DEFAULT NULL,
                                 `product_pic` varchar(500) DEFAULT NULL COMMENT '商品图片',
                                 `create_date` datetime DEFAULT NULL,
                                 `modify_date` datetime DEFAULT NULL,
                                 `delete_status` int DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车表';

-- ----------------------------
-- Table structure for oms_order
-- ----------------------------
DROP TABLE IF EXISTS `oms_order`;
CREATE TABLE `oms_order` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `member_id` bigint NOT NULL,
                             `order_sn` varchar(64) DEFAULT NULL COMMENT '订单编号',
                             `create_time` datetime DEFAULT NULL,
                             `member_username` varchar(64) DEFAULT NULL,
                             `total_amount` decimal(10,2) DEFAULT NULL COMMENT '订单总金额',
                             `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '应付金额',
                             `status` int DEFAULT '0' COMMENT '0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭',
                             `note` varchar(500) DEFAULT NULL COMMENT '订单备注',
                             `coupon_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠券抵扣金额',
                             `merchant_id` bigint NOT NULL COMMENT '所属商家ID',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `idx_order_sn` (`order_sn`),
                             KEY `idx_order_merchant` (`merchant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

-- ----------------------------
-- Table structure for oms_order_item
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_item`;
CREATE TABLE `oms_order_item` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `order_id` bigint DEFAULT NULL COMMENT '订单ID',
                                  `order_sn` varchar(64) DEFAULT NULL COMMENT '订单编号',
                                  `product_id` bigint DEFAULT NULL,
                                  `product_pic` varchar(500) DEFAULT NULL,
                                  `product_name` varchar(200) DEFAULT NULL,
                                  `product_price` decimal(10,2) DEFAULT NULL COMMENT '销售价格',
                                  `product_quantity` int DEFAULT NULL COMMENT '购买数量',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单详情表';

-- ----------------------------
-- Table structure for oms_payment_info
-- ----------------------------
DROP TABLE IF EXISTS `oms_payment_info`;
CREATE TABLE `oms_payment_info` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `order_sn` varchar(64) NOT NULL COMMENT '订单编号',
                                    `order_id` bigint NOT NULL COMMENT '订单ID',
                                    `alipay_trade_no` varchar(50) DEFAULT NULL COMMENT '支付宝交易号',
                                    `total_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
                                    `subject` varchar(200) DEFAULT NULL COMMENT '交易内容',
                                    `payment_status` varchar(20) DEFAULT NULL COMMENT '支付状态',
                                    `create_time` datetime DEFAULT NULL,
                                    `callback_time` datetime DEFAULT NULL COMMENT '回调时间',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付流水表';

-- ----------------------------
-- Table structure for pos_category
-- ----------------------------
DROP TABLE IF EXISTS `pos_category`;
CREATE TABLE `pos_category` (
                                `category_id` bigint NOT NULL AUTO_INCREMENT COMMENT '类别id',
                                `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级id',
                                `category_name` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类别名称',
                                `create_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者',
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `update_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新者',
                                `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                PRIMARY KEY (`category_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=423 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='类别表';

-- ----------------------------
-- Table structure for pos_payment
-- ----------------------------
DROP TABLE IF EXISTS `pos_payment`;
CREATE TABLE `pos_payment` (
                               `payment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付id',
                               `payment_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付编号',
                               `sale_id` bigint DEFAULT NULL COMMENT '订单id',
                               `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
                               `amount` decimal(10,2) DEFAULT '0.00' COMMENT '支付金额',
                               `pay_method` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付方式',
                               `create_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者',
                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                               `update_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新者',
                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                               PRIMARY KEY (`payment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='支付表';

-- ----------------------------
-- Table structure for pos_product
-- ----------------------------
DROP TABLE IF EXISTS `pos_product`;
CREATE TABLE `pos_product` (
                               `product_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品id',
                               `product_sn` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品编号',
                               `product_name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
                               `product_description` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品描述',
                               `price` decimal(10,2) NOT NULL COMMENT '商品价格',
                               `product_category_id` bigint NOT NULL COMMENT '类别ID',
                               `image_url` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主图URL',
                               `detail_url` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详情URL',
                               `create_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者',
                               `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                               `update_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新者',
                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                               `stock` int DEFAULT '100' COMMENT '库存数量',
                               `merchant_id` bigint NOT NULL COMMENT '所属商家ID',
                               PRIMARY KEY (`product_id`) USING BTREE,
                               UNIQUE KEY `product_sn` (`product_sn`),
                               KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='商品表';

-- ----------------------------
-- Table structure for pos_sale
-- ----------------------------
DROP TABLE IF EXISTS `pos_sale`;
CREATE TABLE `pos_sale` (
                            `sale_id` bigint NOT NULL AUTO_INCREMENT COMMENT '销售ID',
                            `sale_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '销售单号',
                            `total` decimal(10,2) DEFAULT '0.00' COMMENT '总金额',
                            `sale_time` datetime DEFAULT NULL COMMENT '销售时间',
                            `payment_id` bigint DEFAULT NULL COMMENT '支付id',
                            `status` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单状态',
                            `del_flag` char(1) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '删除标识',
                            `create_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新者',
                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                            PRIMARY KEY (`sale_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for pos_sale_item
-- ----------------------------
DROP TABLE IF EXISTS `pos_sale_item`;
CREATE TABLE `pos_sale_item` (
                                 `sale_item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单明细id',
                                 `sale_id` bigint DEFAULT NULL COMMENT '订单id',
                                 `product_id` bigint DEFAULT NULL COMMENT '产品id',
                                 `product_sn` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品快照-编码',
                                 `product_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品快照-名称',
                                 `price` decimal(10,2) DEFAULT '0.00' COMMENT '销售价格',
                                 `quantity` int DEFAULT NULL COMMENT '购买数量',
                                 `status` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '明细状态',
                                 `del_flag` char(1) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '删除标识',
                                 `create_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新者',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`sale_item_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for sms_coupon
-- ----------------------------
DROP TABLE IF EXISTS `sms_coupon`;
CREATE TABLE `sms_coupon` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `name` varchar(100) DEFAULT NULL COMMENT '优惠券名称',
                              `count` int DEFAULT NULL COMMENT '发行数量',
                              `amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
                              `per_limit` int DEFAULT '1' COMMENT '每人限领张数',
                              `start_time` datetime DEFAULT NULL,
                              `end_time` datetime DEFAULT NULL,
                              `status` int DEFAULT '1' COMMENT '1正常 0过期',
                              `min_point` decimal(10,2) DEFAULT '0.00' COMMENT '使用门槛(0表示无门槛)',
                              `merchant_id` bigint DEFAULT '0' COMMENT '所属商家ID (0表示平台通用券)',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='优惠券表';

-- ----------------------------
-- Table structure for sms_coupon_history
-- ----------------------------
DROP TABLE IF EXISTS `sms_coupon_history`;
CREATE TABLE `sms_coupon_history` (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `coupon_id` bigint DEFAULT NULL,
                                      `member_id` bigint DEFAULT NULL,
                                      `member_name` varchar(64) DEFAULT NULL,
                                      `get_type` int DEFAULT '1' COMMENT '获取类型：0->后台赠送；1->主动获取',
                                      `create_time` datetime DEFAULT NULL,
                                      `use_status` int DEFAULT '0' COMMENT '使用状态：0->未使用；1->已使用',
                                      `use_time` datetime DEFAULT NULL COMMENT '使用时间',
                                      `order_id` bigint DEFAULT NULL COMMENT '订单ID',
                                      `order_sn` varchar(64) DEFAULT NULL COMMENT '订单编号',
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='优惠券领取历史';

-- ----------------------------
-- Table structure for sys_merchant
-- ----------------------------
DROP TABLE IF EXISTS `sys_merchant`;
CREATE TABLE `sys_merchant` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `user_id` bigint NOT NULL COMMENT '关联的用户ID(店长)',
                                `merchant_name` varchar(100) NOT NULL COMMENT '店铺名称',
                                `logo_url` varchar(255) DEFAULT NULL COMMENT '店铺Logo',
                                `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
                                `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
                                `status` tinyint(1) DEFAULT '0' COMMENT '状态：0-待审核 1-营业中 2-已关店',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='入驻商家表';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `username` varchar(64) NOT NULL DEFAULT '' COMMENT '用户名',
                            `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码（加密存储）',
                            `role` varchar(20) DEFAULT 'CUSTOMER' COMMENT '角色: SUPER_ADMIN, MERCHANT, CUSTOMER',
                            `status` char(1) DEFAULT '0' COMMENT '状态: 0-正常, 1-禁用',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
                            `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像URL',
                            `merchant_id` bigint DEFAULT NULL COMMENT '所属商家ID(仅商家有)',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_username` (`username`),
                            UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=2000587179186851842 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
