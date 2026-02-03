/*
Navicat MySQL Data Transfer

Source Server         : courseDesign
Source Server Version : 80028
Source Host           : localhost:3306
Source Database       : isdp-db

Target Server Type    : MYSQL
Target Server Version : 80028
File Encoding         : 65001

Date: 2026-02-03 23:25:17
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
-- Records of oms_cart_item
-- ----------------------------
INSERT INTO `oms_cart_item` VALUES ('18', '5', null, '2', '1', '39.00', '甄沐海岸松无硅油氨基酸控油蓬松洗发水300ml', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/0a1918d9-a297-4e4f-b998-d7e58021d7df.jpeg', '2026-01-31 20:22:07', '2026-01-31 20:23:18', '0');

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
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_sn` (`order_sn`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

-- ----------------------------
-- Records of oms_order
-- ----------------------------
INSERT INTO `oms_order` VALUES ('1', '2', '202601160018357546624', '2026-01-16 00:18:36', 'customer', '66.00', '66.00', '4', '请尽快发货', '0.00');
INSERT INTO `oms_order` VALUES ('2', '2', '202601160038285239579', '2026-01-16 00:38:29', 'customer', '66.00', '66.00', '4', '请尽快发货', '0.00');
INSERT INTO `oms_order` VALUES ('3', '2', '202601160042510469788', '2026-01-16 00:42:51', 'customer', '66.00', '66.00', '4', '请尽快发货', '0.00');
INSERT INTO `oms_order` VALUES ('4', '2', '202601160050057286824', '2026-01-16 00:50:06', 'customer', '66.00', '66.00', '4', '请尽快发货', '0.00');
INSERT INTO `oms_order` VALUES ('5', '2', '202601160055395744940', '2026-01-16 00:55:40', 'customer', '66.00', '66.00', '4', '请尽快发货', '0.00');
INSERT INTO `oms_order` VALUES ('6', '2', '202601161210452368058', '2026-01-16 12:10:45', 'customer', '66.00', '66.00', '4', '请尽快发货', '0.00');
INSERT INTO `oms_order` VALUES ('7', '2', '202601161216139559879', '2026-01-16 12:16:14', 'customer', '66.00', '66.00', '4', '请尽快发货', '0.00');
INSERT INTO `oms_order` VALUES ('8', '2', '202601161708441553563', '2026-01-16 17:08:44', 'customer', '66.00', '66.00', '4', '请尽快发货', '0.00');
INSERT INTO `oms_order` VALUES ('9', '2', '202601172033483779168', '2026-01-17 20:33:48', 'customer', '132.00', '132.00', '4', '', '0.00');
INSERT INTO `oms_order` VALUES ('10', '2', '202601172127288728687', '2026-01-17 21:27:29', 'customer', '33542.00', '33542.00', '4', '666', '0.00');
INSERT INTO `oms_order` VALUES ('11', '2', '202601222033594335894', '2026-01-22 20:33:59', 'customer', '13555.00', '13555.00', '4', '麻溜的', '0.00');
INSERT INTO `oms_order` VALUES ('12', '2', '202601222040416515370', '2026-01-22 20:40:42', 'customer', '13555.00', '13555.00', '4', '1', '0.00');
INSERT INTO `oms_order` VALUES ('14', '2', '202601222107462969347', '2026-01-22 21:07:46', 'customer', '13575.00', '13575.00', '4', '', '0.00');
INSERT INTO `oms_order` VALUES ('15', '2', '202601222145157279669', '2026-01-22 21:45:16', 'customer', '13575.00', '13575.00', '0', '', '0.00');
INSERT INTO `oms_order` VALUES ('16', '2', '202601222232034691437', '2026-01-22 22:32:03', 'customer', '13575.00', '13575.00', '4', '11', '0.00');
INSERT INTO `oms_order` VALUES ('17', '2', '202601222242324608979', '2026-01-22 22:42:32', 'customer', '65.00', '65.00', '4', '', '0.00');
INSERT INTO `oms_order` VALUES ('18', '2', '202601222251020772382', '2026-01-22 22:51:02', 'customer', '66.00', '66.00', '1', '', '0.00');
INSERT INTO `oms_order` VALUES ('19', '2', '202601271804276527959', '2026-01-27 18:04:28', 'customer', '13554.00', '13454.00', '4', '', '100.00');

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
-- Records of oms_order_item
-- ----------------------------
INSERT INTO `oms_order_item` VALUES ('1', '1', '202601160018357546624', '1', null, '钢笔', '66.00', '1');
INSERT INTO `oms_order_item` VALUES ('2', '2', '202601160038285239579', '1', null, '钢笔', '66.00', '1');
INSERT INTO `oms_order_item` VALUES ('3', '3', '202601160042510469788', '1', null, '钢笔', '66.00', '1');
INSERT INTO `oms_order_item` VALUES ('4', '4', '202601160050057286824', '1', null, '钢笔', '66.00', '1');
INSERT INTO `oms_order_item` VALUES ('5', '5', '202601160055395744940', '1', null, '钢笔', '66.00', '1');
INSERT INTO `oms_order_item` VALUES ('6', '6', '202601161210452368058', '1', null, '钢笔', '66.00', '1');
INSERT INTO `oms_order_item` VALUES ('7', '7', '202601161216139559879', '1', null, '钢笔', '66.00', '1');
INSERT INTO `oms_order_item` VALUES ('8', '8', '202601161708441553563', '1', null, '钢笔', '66.00', '1');
INSERT INTO `oms_order_item` VALUES ('9', '9', '202601172033483779168', '1', null, '钢笔', '66.00', '2');
INSERT INTO `oms_order_item` VALUES ('10', '10', '202601172127288728687', '7', 'https://res6.vmallres.com/pimages//uomcdn/CN/pms/202309/gbom/6942103107320/800_800_959526DD397D0C873FCE80CE67C9A0BFmp.png', 'Mate X5', '13499.00', '2');
INSERT INTO `oms_order_item` VALUES ('11', '10', '202601172127288728687', '3', null, '铅笔盒', '45.00', '1');
INSERT INTO `oms_order_item` VALUES ('12', '10', '202601172127288728687', '8', 'https://res2.vmallres.com/pimages//uomcdn/CN/pms/202404/gbom/6942103119071/800_800_AE94E48F4A6370D6E956B4E722588A5Amp.png', 'Pura 70 Pro', '6499.00', '1');
INSERT INTO `oms_order_item` VALUES ('13', '11', '202601222033594335894', '18', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/0d9f2632-671b-4bb1-87de-630a72a400ec.png', '11', '11.00', '1');
INSERT INTO `oms_order_item` VALUES ('14', '11', '202601222033594335894', '3', null, '铅笔盒', '45.00', '1');
INSERT INTO `oms_order_item` VALUES ('15', '11', '202601222033594335894', '7', 'https://res6.vmallres.com/pimages//uomcdn/CN/pms/202309/gbom/6942103107320/800_800_959526DD397D0C873FCE80CE67C9A0BFmp.png', 'Mate X5', '13499.00', '1');
INSERT INTO `oms_order_item` VALUES ('16', '12', '202601222040416515370', '18', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/0d9f2632-671b-4bb1-87de-630a72a400ec.png', '11', '11.00', '1');
INSERT INTO `oms_order_item` VALUES ('17', '12', '202601222040416515370', '3', null, '铅笔盒', '45.00', '1');
INSERT INTO `oms_order_item` VALUES ('18', '12', '202601222040416515370', '7', 'https://res6.vmallres.com/pimages//uomcdn/CN/pms/202309/gbom/6942103107320/800_800_959526DD397D0C873FCE80CE67C9A0BFmp.png', 'Mate X5', '13499.00', '1');
INSERT INTO `oms_order_item` VALUES ('23', '14', '202601222107462969347', '18', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/0d9f2632-671b-4bb1-87de-630a72a400ec.png', '11', '11.00', '1');
INSERT INTO `oms_order_item` VALUES ('24', '14', '202601222107462969347', '2', null, '日记本', '20.00', '1');
INSERT INTO `oms_order_item` VALUES ('25', '14', '202601222107462969347', '3', null, '铅笔盒', '45.00', '1');
INSERT INTO `oms_order_item` VALUES ('26', '14', '202601222107462969347', '7', 'https://res6.vmallres.com/pimages//uomcdn/CN/pms/202309/gbom/6942103107320/800_800_959526DD397D0C873FCE80CE67C9A0BFmp.png', 'Mate X5', '13499.00', '1');
INSERT INTO `oms_order_item` VALUES ('27', '15', '202601222145157279669', '18', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/0d9f2632-671b-4bb1-87de-630a72a400ec.png', '11', '11.00', '1');
INSERT INTO `oms_order_item` VALUES ('28', '15', '202601222145157279669', '2', null, '日记本', '20.00', '1');
INSERT INTO `oms_order_item` VALUES ('29', '15', '202601222145157279669', '3', null, '铅笔盒', '45.00', '1');
INSERT INTO `oms_order_item` VALUES ('30', '15', '202601222145157279669', '7', 'https://res6.vmallres.com/pimages//uomcdn/CN/pms/202309/gbom/6942103107320/800_800_959526DD397D0C873FCE80CE67C9A0BFmp.png', 'Mate X5', '13499.00', '1');
INSERT INTO `oms_order_item` VALUES ('31', '16', '202601222232034691437', '18', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/0d9f2632-671b-4bb1-87de-630a72a400ec.png', '11', '11.00', '1');
INSERT INTO `oms_order_item` VALUES ('32', '16', '202601222232034691437', '2', null, '日记本', '20.00', '1');
INSERT INTO `oms_order_item` VALUES ('33', '16', '202601222232034691437', '3', null, '铅笔盒', '45.00', '1');
INSERT INTO `oms_order_item` VALUES ('34', '16', '202601222232034691437', '7', 'https://res6.vmallres.com/pimages//uomcdn/CN/pms/202309/gbom/6942103107320/800_800_959526DD397D0C873FCE80CE67C9A0BFmp.png', 'Mate X5', '13499.00', '1');
INSERT INTO `oms_order_item` VALUES ('35', '17', '202601222242324608979', '3', null, '铅笔盒', '45.00', '1');
INSERT INTO `oms_order_item` VALUES ('36', '17', '202601222242324608979', '2', null, '日记本', '20.00', '1');
INSERT INTO `oms_order_item` VALUES ('37', '18', '202601222251020772382', '1', null, '钢笔', '66.00', '1');
INSERT INTO `oms_order_item` VALUES ('38', '19', '202601271804276527959', '3', null, '铅笔盒', '45.00', '1');
INSERT INTO `oms_order_item` VALUES ('39', '19', '202601271804276527959', '7', 'https://res6.vmallres.com/pimages//uomcdn/CN/pms/202309/gbom/6942103107320/800_800_959526DD397D0C873FCE80CE67C9A0BFmp.png', 'Mate X5', '13499.00', '1');
INSERT INTO `oms_order_item` VALUES ('40', '19', '202601271804276527959', '4', null, '毛巾', '10.00', '1');

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
-- Records of oms_payment_info
-- ----------------------------
INSERT INTO `oms_payment_info` VALUES ('1', '202601222251020772382', '18', null, '66.00', 'SCAU商城-订单202601222251020772382', 'PENDING', '2026-01-22 22:51:10', null);

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
-- Records of pos_category
-- ----------------------------
INSERT INTO `pos_category` VALUES ('1', '0', '手机数码', null, null, null, null);
INSERT INTO `pos_category` VALUES ('2', '0', '电脑办公', null, null, null, null);
INSERT INTO `pos_category` VALUES ('3', '0', '生活百货', null, null, null, null);
INSERT INTO `pos_category` VALUES ('4', '0', '图书文具', null, null, null, null);
INSERT INTO `pos_category` VALUES ('11', '1', '手机通讯', null, null, null, null);
INSERT INTO `pos_category` VALUES ('12', '1', '手机配件', null, null, null, null);
INSERT INTO `pos_category` VALUES ('21', '2', '电脑整机', null, null, null, null);
INSERT INTO `pos_category` VALUES ('22', '2', '电脑外设', null, null, null, null);
INSERT INTO `pos_category` VALUES ('31', '3', '休闲零食', null, null, null, null);
INSERT INTO `pos_category` VALUES ('32', '3', '个人护理', null, null, null, null);
INSERT INTO `pos_category` VALUES ('41', '4', '教材教辅', null, null, null, null);
INSERT INTO `pos_category` VALUES ('42', '4', '办公文具', null, null, null, null);
INSERT INTO `pos_category` VALUES ('111', '11', '5G手机', null, null, null, null);
INSERT INTO `pos_category` VALUES ('112', '11', '游戏手机', null, null, null, null);
INSERT INTO `pos_category` VALUES ('113', '11', '拍照手机', null, null, null, null);
INSERT INTO `pos_category` VALUES ('121', '12', '手机壳', null, null, null, null);
INSERT INTO `pos_category` VALUES ('122', '12', '数据线/充电器', null, null, null, null);
INSERT INTO `pos_category` VALUES ('123', '12', '移动电源', null, null, null, null);
INSERT INTO `pos_category` VALUES ('211', '21', '轻薄本', null, null, null, null);
INSERT INTO `pos_category` VALUES ('212', '21', '游戏本', null, null, null, null);
INSERT INTO `pos_category` VALUES ('213', '21', '平板电脑', null, null, null, null);
INSERT INTO `pos_category` VALUES ('221', '22', '鼠标', null, null, null, null);
INSERT INTO `pos_category` VALUES ('222', '22', '键盘', null, null, null, null);
INSERT INTO `pos_category` VALUES ('223', '22', '显示器', null, null, null, null);
INSERT INTO `pos_category` VALUES ('311', '31', '坚果炒货', null, null, null, null);
INSERT INTO `pos_category` VALUES ('312', '31', '饼干蛋糕', null, null, null, null);
INSERT INTO `pos_category` VALUES ('321', '32', '洗发护发', null, null, null, null);
INSERT INTO `pos_category` VALUES ('322', '32', '纸品湿巾', null, null, null, null);
INSERT INTO `pos_category` VALUES ('411', '41', '考研复习', null, null, null, null);
INSERT INTO `pos_category` VALUES ('412', '41', '英语四六级', null, null, null, null);
INSERT INTO `pos_category` VALUES ('421', '42', '笔类', null, null, null, null);
INSERT INTO `pos_category` VALUES ('422', '42', '笔记本/手账', null, null, null, null);

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
-- Records of pos_payment
-- ----------------------------
INSERT INTO `pos_payment` VALUES ('1', 'PAY-1764346853495', '1', '2025-11-29 00:20:53', '135.00', 'CASH', null, null, null, null);
INSERT INTO `pos_payment` VALUES ('2', 'PAY-1764488909423', '13', '2025-11-30 15:48:29', '13499.00', 'ALIPAY', null, null, null, null);
INSERT INTO `pos_payment` VALUES ('3', 'PAY-1764491373940', '14', '2025-11-30 16:29:34', '6499.00', 'WECHAT_PAY', null, null, null, null);
INSERT INTO `pos_payment` VALUES ('4', 'PAY-1764491776641', '12', '2025-11-30 16:36:17', '66.00', 'CASH', null, null, null, null);
INSERT INTO `pos_payment` VALUES ('5', 'PAY-1764491957412', '15', '2025-11-30 16:39:17', '66.00', 'CASH', null, null, null, null);
INSERT INTO `pos_payment` VALUES ('6', 'PAY-1764492032265', '16', '2025-11-30 16:40:32', '66.00', 'CASH', null, null, null, null);
INSERT INTO `pos_payment` VALUES ('7', 'PAY-1764492544514', '17', '2025-11-30 16:49:05', '66.00', 'CASH', null, null, null, null);
INSERT INTO `pos_payment` VALUES ('8', 'PAY-1764492841269', '18', '2025-11-30 16:54:01', '30.00', 'ALIPAY', null, '2025-11-30 16:54:01', null, null);
INSERT INTO `pos_payment` VALUES ('9', 'PAY-1764492894573', '19', '2025-11-30 16:54:55', '45.00', 'CASH', null, null, null, null);
INSERT INTO `pos_payment` VALUES ('10', 'PAY-1764516171733', '20', '2025-11-30 23:22:52', '13499.00', 'CASH', null, null, null, null);
INSERT INTO `pos_payment` VALUES ('11', 'PAY-1764516229375', '21', '2025-11-30 23:23:49', '6499.00', 'CASH', null, '2025-11-30 23:23:49', null, null);
INSERT INTO `pos_payment` VALUES ('12', 'PAY-1764516743820', '22', '2025-11-30 23:32:24', '10.00', 'WECHAT_PAY', null, '2025-11-30 23:32:24', null, null);
INSERT INTO `pos_payment` VALUES ('22', 'PAY-1764583855744', '33', '2025-12-01 18:10:56', '20.00', 'CASH', 'admin_test', '2025-12-01 18:10:56', 'admin_test', '2025-12-01 18:10:56');
INSERT INTO `pos_payment` VALUES ('23', 'PAY-1764584135096', '34', '2025-12-01 18:15:35', '20.00', 'CASH', 'admin_test', '2025-12-01 18:15:35', 'admin_test', '2025-12-01 18:15:35');
INSERT INTO `pos_payment` VALUES ('24', 'PAY-1764689712417', '35', '2025-12-02 23:35:12', '30.00', 'ALIPAY', 'admin_test', '2025-12-02 23:35:12', 'admin_test', '2025-12-02 23:35:12');
INSERT INTO `pos_payment` VALUES ('25', 'PAY-1764693933446', '36', '2025-12-03 00:45:33', '80.00', 'CASH', 'admin_test', '2025-12-03 00:45:33', 'admin_test', '2025-12-03 00:45:33');
INSERT INTO `pos_payment` VALUES ('26', 'PAY-1764694466646', '37', '2025-12-03 00:54:27', '66.00', 'CASH', 'admin_test', '2025-12-03 00:54:27', 'admin_test', '2025-12-03 00:54:27');
INSERT INTO `pos_payment` VALUES ('27', 'PAY-1764694952568', '38', '2025-12-03 01:02:33', '66.00', 'ALIPAY', null, null, null, null);
INSERT INTO `pos_payment` VALUES ('28', 'PAY-1764739137439', '40', '2025-12-03 13:18:57', '66.00', 'CASH', 'admin_test', '2025-12-03 13:18:57', 'admin_test', '2025-12-03 13:18:57');
INSERT INTO `pos_payment` VALUES ('29', 'PAY-15114582620110849', '49', '2025-12-08 17:32:18', '152.00', 'CASH', 'admin_test', '2025-12-08 17:32:18', 'admin_test', '2025-12-08 17:32:18');
INSERT INTO `pos_payment` VALUES ('30', 'PAY-15115793800888322', '50', '2025-12-08 17:37:00', '66.00', 'CASH', 'admin_test', '2025-12-08 17:37:00', 'admin_test', '2025-12-08 17:37:00');
INSERT INTO `pos_payment` VALUES ('31', 'PAY-15116846067875843', '51', '2025-12-08 17:41:05', '66.00', 'CASH', 'admin_test', '2025-12-08 17:41:05', 'admin_test', '2025-12-08 17:41:05');
INSERT INTO `pos_payment` VALUES ('32', 'PAY-15116966326960132', '52', '2025-12-08 17:41:33', '66.00', 'CASH', 'admin_test', '2025-12-08 17:41:33', 'admin_test', '2025-12-08 17:41:33');
INSERT INTO `pos_payment` VALUES ('33', 'PAY-15118164622835717', '54', '2025-12-08 17:46:13', '132.00', 'CASH', 'admin_test', '2025-12-08 17:46:13', 'admin_test', '2025-12-08 17:46:13');
INSERT INTO `pos_payment` VALUES ('34', 'PAY-15118868997472262', '55', '2025-12-08 17:48:57', '66.00', 'CASH', 'admin_test', '2025-12-08 17:48:57', 'admin_test', '2025-12-08 17:48:57');
INSERT INTO `pos_payment` VALUES ('35', 'PAY-15120123127922695', '56', '2025-12-08 17:53:49', '66.00', 'CASH', 'admin_test', '2025-12-08 17:53:49', 'admin_test', '2025-12-08 17:53:49');
INSERT INTO `pos_payment` VALUES ('36', 'PAY-15120161782628360', '57', '2025-12-08 17:53:58', '66.00', 'CASH', 'admin_test', '2025-12-08 17:53:58', 'admin_test', '2025-12-08 17:53:58');
INSERT INTO `pos_payment` VALUES ('37', 'PAY-15122150352486409', '58', '2025-12-08 18:01:41', '66.00', 'CASH', 'admin_test', '2025-12-08 18:01:41', 'admin_test', '2025-12-08 18:01:41');
INSERT INTO `pos_payment` VALUES ('38', 'PAY-15213465652166666', '59', '2025-12-08 23:56:01', '66.00', 'CASH', 'admin_test', '2025-12-08 23:56:01', 'admin_test', '2025-12-08 23:56:01');
INSERT INTO `pos_payment` VALUES ('39', 'PAY-15214324645625867', '60', '2025-12-08 23:59:22', '45.00', 'ALIPAY', 'admin_test', '2025-12-08 23:59:22', 'admin_test', '2025-12-08 23:59:22');
INSERT INTO `pos_payment` VALUES ('41', 'PAY-15215595955945474', '62', '2025-12-09 00:04:17', '20.00', 'CASH', 'admin_test', '2025-12-09 00:04:17', 'admin_test', '2025-12-09 00:04:17');
INSERT INTO `pos_payment` VALUES ('47', 'PAY-15222545213030408', '61', '2025-12-09 00:31:15', '66.00', 'CASH', 'admin_test', '2025-12-09 00:31:15', 'admin_test', '2025-12-09 00:31:15');
INSERT INTO `pos_payment` VALUES ('48', 'PAY-15225156553146377', '65', '2025-12-09 00:41:24', '10.00', 'WECHAT_PAY', 'admin_test', '2025-12-09 00:41:24', 'admin_test', '2025-12-09 00:41:24');
INSERT INTO `pos_payment` VALUES ('49', 'PAY-15229206707306506', '66', '2025-12-09 00:57:06', '66.00', 'ALIPAY', 'admin_test', '2025-12-09 00:57:06', 'admin_test', '2025-12-09 00:57:06');
INSERT INTO `pos_payment` VALUES ('50', 'PAY-15229271131815947', '67', '2025-12-09 00:57:21', '45.00', 'ALIPAY', 'admin_test', '2025-12-09 00:57:21', 'admin_test', '2025-12-09 00:57:21');
INSERT INTO `pos_payment` VALUES ('51', 'PAY-17063780512956417', '3', '2025-12-13 23:36:12', '110.00', 'CASH', 'admin_test', '2025-12-13 23:36:12', 'admin_test', '2025-12-13 23:36:12');
INSERT INTO `pos_payment` VALUES ('52', 'PAY-32545110390472705', '68', '2026-01-24 16:51:39', '45.00', 'CASH', 'admin_test', '2026-01-24 16:51:39', 'admin_test', '2026-01-24 16:51:39');

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
  PRIMARY KEY (`product_id`) USING BTREE,
  UNIQUE KEY `product_sn` (`product_sn`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='商品表';

-- ----------------------------
-- Records of pos_product
-- ----------------------------
INSERT INTO `pos_product` VALUES ('1', '1001', '黑耀系列钢笔礼盒', '商品限购50件，超出限购数量不可购买', '169.00', '421', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/62389fff-7a64-444a-aa66-78b1fd583e32.png', '', 'admin', '2025-10-01 23:58:40', 'admin', '2026-01-30 16:26:19', '100');
INSERT INTO `pos_product` VALUES ('2', '1002', '广博A5/96张皮面记事本 雅典黑 1本', '耐磨防水，精选PU皮', '12.90', '422', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/440602d9-ba5f-42f8-9d9d-ba3300ff9aba.jpeg', '', 'admin', '2025-10-01 23:58:40', 'admin', '2026-01-30 16:43:04', '100');
INSERT INTO `pos_product` VALUES ('5', '1005', '甄沐海岸松无硅油氨基酸控油蓬松洗发水300ml', '控油清爽，洗出轻盈秀发', '38.00', '321', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/0a1918d9-a297-4e4f-b998-d7e58021d7df.jpeg', '', 'admin', '2025-10-01 23:58:40', 'admin', '2026-01-31 20:22:38', '100');
INSERT INTO `pos_product` VALUES ('7', '1007', 'Mate X5', '超轻薄四曲折叠，超高分辨率临境双屏', '13499.00', '111', 'https://res6.vmallres.com/pimages//uomcdn/CN/pms/202309/gbom/6942103107320/800_800_959526DD397D0C873FCE80CE67C9A0BFmp.png', 'https://www.vmall.com/product/comdetail/index.html?prdId=10086281788718&sbomCode=2601010457506', 'admin', '2025-10-01 23:58:40', 'admin', '2026-01-30 16:50:01', '99');
INSERT INTO `pos_product` VALUES ('8', '1008', 'Pura 70 Pro', '超聚光微距长焦，超高速风驰闪拍', '6499.00', '113', 'https://res2.vmallres.com/pimages//uomcdn/CN/pms/202404/gbom/6942103119071/800_800_AE94E48F4A6370D6E956B4E722588A5Amp.png', 'https://www.vmall.com/product/comdetail/index.html?prdId=10086821546239&sbomCode=2601010486504', 'admin', '2025-10-01 23:58:40', 'admin', '2026-01-30 16:50:22', '100');
INSERT INTO `pos_product` VALUES ('19', 'P111001', '小米14 Pro 钛金属版', '骁龙8Gen3 徕卡光学镜头 120W秒充', '4999.00', '111', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/5959b1f8-3133-4ba4-b5ad-98f03065973f.jpg', '', null, null, 'admin', '2026-01-30 23:22:35', '100');
INSERT INTO `pos_product` VALUES ('20', 'P111002', 'iPhone 15 Pro Max', '钛金属边框 A17 Pro芯片', '9999.00', '111', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/d071f425-0c39-430a-a734-ee65a65c4c3f.jpg', '', null, null, 'admin', '2026-01-30 23:21:58', '50');
INSERT INTO `pos_product` VALUES ('21', 'P111003', '华为 Mate 60 Pro', '卫星通话 昆仑玻璃 玄武架构', '6999.00', '111', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/2301f0bf-b2d9-4e1e-ba15-4387fd1cec0c.jpg', '', null, null, 'admin', '2026-01-30 23:22:51', '30');
INSERT INTO `pos_product` VALUES ('23', 'P112001', '红魔9 Pro', '第三代骁龙8 ICE 13.0魔冷散热', '4399.00', '112', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/7c4aad19-33a4-469b-9765-2dc82065a302.png', '', null, null, 'admin', '2026-01-30 23:25:40', '100');
INSERT INTO `pos_product` VALUES ('24', 'P112002', 'ROG 8 Pro', '专业电竞手机 165Hz高刷', '5299.00', '112', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/9297d125-7707-43d7-90d8-8f882250a222.jpg', '', null, null, 'admin', '2026-01-30 23:25:50', '60');
INSERT INTO `pos_product` VALUES ('25', 'P113001', 'vivo X100 Pro', '蔡司APO超级长焦 蓝晶芯片技术栈', '4999.00', '113', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/ae7c0ba1-8c1b-4335-99f7-26f32a8eff27.png', '', null, null, 'admin', '2026-01-30 23:26:11', '100');
INSERT INTO `pos_product` VALUES ('26', 'P113002', 'OPPO Find X7 Ultra', '双潜望四主摄 哈苏大师影像', '5999.00', '113', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/d7e6da45-0911-49f5-9833-5e364d28aaf0.jpeg', '', null, null, 'admin', '2026-01-30 23:26:21', '80');
INSERT INTO `pos_product` VALUES ('27', 'P121001', 'iPhone 15 液态硅胶壳', '官方同款 手感亲肤 全包防摔', '29.90', '121', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/3b4ba240-524a-4812-8ff5-89f5dd6bba87.jpeg', '', null, null, 'admin', '2026-01-30 23:26:28', '500');
INSERT INTO `pos_product` VALUES ('28', 'P121002', '华为 Mate60 素皮壳', '商务风格 还原裸机手感', '39.90', '121', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/aa61eeb6-8ccb-479f-a30b-281b5e716fdb.jpeg', '', null, null, 'admin', '2026-01-30 23:26:42', '300');
INSERT INTO `pos_product` VALUES ('29', 'P122001', 'Anker 苹果PD快充线', 'MFi认证 亲肤材质 不弹窗', '49.00', '122', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/2381a35b-387d-4942-9676-27cb9ce4d9ba.jpeg', '', null, null, 'admin', '2026-01-30 23:26:49', '200');
INSERT INTO `pos_product` VALUES ('30', 'P122002', '倍思 Type-C 数据线', '100W快充 编织线身 耐用不断裂', '19.90', '122', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/da4514d1-c227-4817-aeea-b84bc3c4c930.jpeg', '', null, null, 'admin', '2026-01-30 23:27:00', '500');
INSERT INTO `pos_product` VALUES ('31', 'P123001', '小米移动电源3', '20000mAh 大容量 双向快充', '79.00', '123', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/f6afd2d3-d0be-48e8-9e89-9e7da87a09da.jpeg', '', null, null, 'admin', '2026-01-30 23:27:14', '200');
INSERT INTO `pos_product` VALUES ('32', 'P123002', '罗马仕 充电宝', '20000mAh 小巧便携 自带线', '59.00', '123', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/02b34e38-5dc6-4c69-8a62-ce13eb263501.jpeg', '', null, null, 'admin', '2026-01-30 23:27:39', '300');
INSERT INTO `pos_product` VALUES ('33', 'P211001', 'MacBook Air M2', 'M2芯片 极致轻薄 午夜色 8G+256G', '8999.00', '211', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/02e6bdfa-9cf9-4954-92a4-bddb464e0545.jpeg', '', null, null, 'admin', '2026-01-30 23:27:49', '20');
INSERT INTO `pos_product` VALUES ('34', 'P211002', '联想小新Pro 14', 'i5-13500H 2.8K 120Hz高刷屏', '5499.00', '211', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/f8d4fc5d-9d19-4e4c-9979-669304b15173.jpeg', '', null, null, 'admin', '2026-01-30 23:28:07', '100');
INSERT INTO `pos_product` VALUES ('35', 'P211003', 'ThinkBook 14+', '全能轻薄本 丰富接口 商务首选', '5299.00', '211', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/8cb228cd-6f4e-432b-b866-cb18eb881e24.jpeg', '', null, null, 'admin', '2026-01-30 23:30:21', '80');
INSERT INTO `pos_product` VALUES ('36', 'P212001', '联想拯救者 R9000P', 'RTX4060 满血显卡 2.5K电竞屏', '8499.00', '212', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/a1220952-9944-4af2-9d5f-575774c32575.jpeg', '', null, null, 'admin', '2026-01-30 23:32:09', '50');
INSERT INTO `pos_product` VALUES ('37', 'P212002', '惠普暗影精灵9', 'i9-13900HX 强悍性能 狂暴模式', '7999.00', '212', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/824be602-a1a5-457a-9f7f-abe50447943d.jpeg', '', null, null, 'admin', '2026-01-30 23:31:55', '60');
INSERT INTO `pos_product` VALUES ('38', 'P213001', 'iPad Air 5', 'M1芯片 全面屏设计 64G', '4399.00', '213', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/b884dbcb-0346-48db-b906-96c6dff04c0a.jpeg', '', null, null, 'admin', '2026-01-30 23:32:19', '100');
INSERT INTO `pos_product` VALUES ('39', 'P213002', '小米平板 6 Pro', '骁龙8+ 11英寸2.8K护眼屏', '2499.00', '213', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/be9ec805-35e3-460b-96a2-847a44a689d2.jpeg', '', null, null, 'admin', '2026-01-30 23:32:27', '150');
INSERT INTO `pos_product` VALUES ('40', 'P221001', '罗技 MX Master 3S', '人体工学 静音滚轮 双模无线', '799.00', '221', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/c326237e-4dd5-477e-914b-5d528a0fb297.jpeg', '', null, null, 'admin', '2026-01-30 23:32:36', '50');
INSERT INTO `pos_product` VALUES ('41', 'P221002', '雷蛇 毒蝰 V3', '轻量化设计 30K光学传感器', '399.00', '221', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/b389efed-4d5e-4229-ae0f-c2b5d0d4930d.jpeg', '', null, null, 'admin', '2026-01-30 23:32:49', '100');
INSERT INTO `pos_product` VALUES ('42', 'P222001', 'Keychron K2 机械键盘', '蓝牙双模 适配Mac/Win 红轴', '498.00', '222', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/04f9db81-d125-4898-a1e9-256bfd8cdb75.jpeg', '', null, null, 'admin', '2026-01-30 23:32:56', '80');
INSERT INTO `pos_product` VALUES ('43', 'P222002', '罗技 K380 蓝牙键盘', '多设备切换 小巧便携 办公神器', '149.00', '222', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/6627ef48-9bdb-4b82-a0b8-ccb6abcee7a5.jpeg', '', null, null, 'admin', '2026-01-30 23:33:24', '200');
INSERT INTO `pos_product` VALUES ('44', 'P223001', '戴尔 U2723QE', '4K显示器 IPS Black面板 Type-C接口', '3499.00', '223', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/4abded32-3f9c-448a-8256-42a32ed1732a.jpeg', '', null, null, 'admin', '2026-01-30 23:33:31', '30');
INSERT INTO `pos_product` VALUES ('45', 'P223002', 'AOC 27英寸 2K', '高性价比 75Hz刷新率 护眼屏', '999.00', '223', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/0b4f49de-246c-449b-a026-841bfda056a0.jpeg', '', null, null, 'admin', '2026-01-30 23:33:48', '100');
INSERT INTO `pos_product` VALUES ('46', 'P311001', '三只松鼠 每日坚果', '30袋装 孕妇零食大礼包', '139.00', '311', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/9d29f843-c9ba-4f44-b7cc-5219691ac4b1.jpeg', '', null, null, 'admin', '2026-01-30 23:33:56', '200');
INSERT INTO `pos_product` VALUES ('47', 'P311002', '百草味 夏威夷果', '奶油味 200g/袋 颗粒饱满', '29.90', '311', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/2d739828-98ea-4c75-a337-8df05c09661c.jpeg', '', null, null, 'admin', '2026-01-30 23:34:02', '300');
INSERT INTO `pos_product` VALUES ('48', 'P312001', '奥利奥 夹心饼干', '原味+草莓味 独立小包装', '12.50', '312', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/93d67066-1bac-4c31-91c1-23cdff177ae2.jpeg', '', null, null, 'admin', '2026-01-30 23:34:10', '500');
INSERT INTO `pos_product` VALUES ('49', 'P312002', '好丽友 派', '巧克力味 12枚入 经典口味', '19.90', '312', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/cc8aa772-b6e2-43ae-9072-672ac17a5362.jpeg', '', null, null, 'admin', '2026-01-30 23:34:18', '200');
INSERT INTO `pos_product` VALUES ('50', 'P321001', '海飞丝 去屑洗发水', '柠檬清爽型 750ml 家庭装', '39.90', '321', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/3121ea0f-9993-4391-91ab-07ffa22ee04f.jpeg', '', null, null, 'admin', '2026-01-30 23:34:25', '200');
INSERT INTO `pos_product` VALUES ('51', 'P321002', '潘婷 护发素', '3分钟奇迹奢护 修复受损发质', '29.90', '321', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/386aa5bb-a810-4dab-ae59-9f89cd9f4e7f.jpeg', '', null, null, 'admin', '2026-01-30 23:34:37', '150');
INSERT INTO `pos_product` VALUES ('52', 'P322001', '维达 抽纸', '4层140抽*24包 整箱装 超韧系列', '59.90', '322', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/cd59d768-615b-4f52-b06f-ad7e914a4434.jpeg', '', null, null, 'admin', '2026-01-30 23:34:45', '200');
INSERT INTO `pos_product` VALUES ('53', 'P322002', '洁柔 Face面巾纸', '古龙水香 4层加厚 湿水不易破', '19.90', '322', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/3e9ec6ca-7fd9-4f2d-a9e5-fcea90c60488.jpeg', '', null, null, 'admin', '2026-01-30 23:35:11', '300');
INSERT INTO `pos_product` VALUES ('54', 'P411001', '肖秀荣 考研政治1000题', '2026考研必备 刷题神器 全新正版', '45.00', '411', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/1349d839-f9de-464d-aa03-3faa3bf0749d.jpeg', '', null, null, 'admin', '2026-01-30 23:35:18', '300');
INSERT INTO `pos_product` VALUES ('55', 'P411002', '张宇 高数18讲', '基础强化一本通 数学一二三通用', '58.00', '411', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/0e0968a0-a87b-444c-888b-93bce25050c3.jpeg', '', null, null, 'admin', '2026-01-30 23:35:26', '200');
INSERT INTO `pos_product` VALUES ('56', 'P412001', '星火英语 四级真题', '备考2026年6月 包含听力+详解', '39.00', '412', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/33054b13-55ed-4d00-ab2d-9c0d476f2cab.jpeg', '', null, null, 'admin', '2026-01-30 23:35:33', '500');
INSERT INTO `pos_product` VALUES ('57', 'P412002', '新东方 恋练有词', '考研英语词汇识记与应用大全', '42.00', '412', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/a1bc1fa5-b28f-4b20-b968-dcb4b476c8a0.jpeg', '', null, null, 'admin', '2026-01-30 23:35:42', '300');
INSERT INTO `pos_product` VALUES ('58', 'P421001', '得力 按动中性笔', '0.5mm 黑色 12支/盒 顺滑好写', '9.90', '421', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/e36305c2-ca57-43d0-86b2-67eb089fa294.jpeg', '', null, null, 'admin', '2026-01-30 23:35:53', '1000');
INSERT INTO `pos_product` VALUES ('59', 'P421002', '百乐 P500 签字笔', '考试专用 0.5mm 黑色 单支装', '7.50', '421', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/740628df-2d14-4d1a-8f7c-a646f41b0cce.jpeg', '', null, null, 'admin', '2026-01-30 23:35:57', '500');
INSERT INTO `pos_product` VALUES ('60', 'P422001', '广博 错题本', 'B5大号 康奈尔笔记法 加厚纸张', '5.00', '422', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/71bc0060-4c98-4ac4-9a2f-7429aaf3a724.jpeg', '', null, null, 'admin', '2026-01-30 23:36:10', '200');
INSERT INTO `pos_product` VALUES ('61', 'P422002', '国誉活页本', 'B5 26孔 超薄便携 40张替芯', '15.00', '422', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/5240d356-d03d-462d-b338-f050b6c21128.jpeg', '', null, null, 'admin', '2026-01-30 23:36:14', '150');

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
-- Records of pos_sale
-- ----------------------------
INSERT INTO `pos_sale` VALUES ('1', 'so-1542881137129459712', '135.00', '2022-06-25 14:42:06', '1', 'paid', '0', null, '2022-06-25 14:42:28', null, '2025-11-29 00:20:54');
INSERT INTO `pos_sale` VALUES ('2', 'so-1542881495386001408', '270.00', '2022-06-30 17:55:25', null, 'paid', '0', null, '2022-06-25 18:20:50', null, '2022-06-26 18:20:50');
INSERT INTO `pos_sale` VALUES ('3', 'so-1542881571277635584', '110.00', '2022-06-30 18:18:29', '51', 'paid', '0', null, '2022-06-25 18:20:56', 'anonymousUser', '2025-12-13 23:36:12');
INSERT INTO `pos_sale` VALUES ('4', 'so-1993652284996898816', '65.00', '2025-11-26 20:05:20', null, 'paid', '0', null, '2025-11-26 20:05:25', null, '2025-11-26 20:05:34');
INSERT INTO `pos_sale` VALUES ('5', 'so-1994077426801086464', '66.00', '2025-11-28 00:14:41', null, 'paid', '0', null, '2025-11-28 00:14:49', null, '2025-11-28 00:14:56');
INSERT INTO `pos_sale` VALUES ('6', 'so-1994080072404471808', '10.00', '2025-11-28 00:25:12', null, 'paid', '0', null, '2025-11-28 00:25:31', null, '2025-11-28 00:25:32');
INSERT INTO `pos_sale` VALUES ('7', 'so-1994080300700438528', '146.00', '2025-11-28 00:26:07', null, 'paid', '0', null, '2025-11-28 00:26:24', null, '2025-11-28 00:26:30');
INSERT INTO `pos_sale` VALUES ('8', 'so-1994080982488748032', '66.00', '2025-11-28 00:28:49', null, 'paid', '0', null, '2025-11-28 00:28:52', null, '2025-11-28 00:28:59');
INSERT INTO `pos_sale` VALUES ('9', 'so-1994313451952082944', '66.00', '2025-11-28 15:52:34', null, 'paid', '0', null, '2025-11-28 15:52:43', null, '2025-11-28 15:53:05');
INSERT INTO `pos_sale` VALUES ('10', 'so-1994316041515339776', '40.00', '2025-11-28 16:02:51', null, 'paid', '0', null, '2025-11-28 16:03:09', null, '2025-11-28 16:03:12');
INSERT INTO `pos_sale` VALUES ('11', 'so-1994445806108893184', '20.00', '2025-11-29 00:38:30', null, 'paid', '0', null, '2025-11-29 00:38:36', null, '2025-11-29 00:39:08');
INSERT INTO `pos_sale` VALUES ('12', 'so-1995034300422221824', '66.00', '2025-11-30 15:36:58', '4', 'paid', '0', null, '2025-11-30 15:37:11', null, '2025-11-30 16:36:17');
INSERT INTO `pos_sale` VALUES ('13', 'SALE-1764488887282', '13499.00', null, '2', 'paid', '0', null, '2025-11-30 15:48:29', null, null);
INSERT INTO `pos_sale` VALUES ('14', 'so-1995044427464380416', '6499.00', '2025-11-30 16:17:12', '3', 'paid', '0', null, '2025-11-30 16:17:12', null, '2025-11-30 16:29:34');
INSERT INTO `pos_sale` VALUES ('15', 'so-1995049946547707904', '66.00', '2025-11-30 16:39:08', '5', 'paid', '0', null, '2025-11-30 16:39:17', null, null);
INSERT INTO `pos_sale` VALUES ('16', 'so-1995050110993784832', '66.00', '2025-11-30 16:39:47', '6', 'paid', '0', null, '2025-11-30 16:39:47', null, '2025-11-30 16:40:32');
INSERT INTO `pos_sale` VALUES ('17', 'so-1995050130799288320', '66.00', '2025-11-30 16:39:52', '7', 'paid', '0', null, '2025-11-30 16:39:52', null, null);
INSERT INTO `pos_sale` VALUES ('18', 'so-1995053624717414400', '30.00', '2025-11-30 16:53:45', '8', 'paid', '0', null, '2025-11-30 16:53:45', null, null);
INSERT INTO `pos_sale` VALUES ('19', 'so-1995053839881015296', '45.00', '2025-11-30 16:54:36', '9', 'paid', '0', null, '2025-11-30 16:54:36', null, '2025-11-30 16:54:55');
INSERT INTO `pos_sale` VALUES ('20', 'so-1995150702827212800', '13499.00', '2025-11-30 23:19:30', '10', 'paid', '0', null, '2025-11-30 23:19:30', null, '2025-11-30 23:22:52');
INSERT INTO `pos_sale` VALUES ('21', 'so-1995151741387214848', '6499.00', '2025-11-30 23:23:38', '11', 'paid', '0', null, '2025-11-30 23:23:38', null, null);
INSERT INTO `pos_sale` VALUES ('22', 'so-1995153812563890176', '10.00', '2025-11-30 23:31:52', '12', 'paid', '0', null, '2025-11-30 23:31:52', null, '2025-11-30 23:32:24');
INSERT INTO `pos_sale` VALUES ('23', 'so-1995155626545803264', '40.00', '2025-11-30 23:39:04', '13', 'paid', '0', null, '2025-11-30 23:39:04', null, '2025-11-30 23:40:14');
INSERT INTO `pos_sale` VALUES ('24', 'so-1995176423406149632', '20.00', '2025-12-01 01:01:43', '14', 'paid', '0', null, '2025-12-01 01:01:43', null, null);
INSERT INTO `pos_sale` VALUES ('25', 'so-1995397746460372992', '66.00', '2025-12-01 15:41:10', '15', 'paid', '0', null, '2025-12-01 15:41:10', null, null);
INSERT INTO `pos_sale` VALUES ('26', 'so-1995399728231268352', '66.00', '2025-12-01 15:49:03', '16', 'paid', '0', null, '2025-12-01 15:49:03', null, null);
INSERT INTO `pos_sale` VALUES ('27', 'so-1995402794909220864', '66.00', '2025-12-01 16:01:14', '17', 'paid', '0', null, '2025-12-01 16:01:14', null, null);
INSERT INTO `pos_sale` VALUES ('28', 'so-1995410458166870016', '66.00', '2025-12-01 16:31:41', '18', 'paid', '0', null, '2025-12-01 16:31:41', null, null);
INSERT INTO `pos_sale` VALUES ('29', 'so-1995429533584715776', '66.00', '2025-12-01 17:47:29', '19', 'paid', '0', null, '2025-12-01 17:47:29', null, null);
INSERT INTO `pos_sale` VALUES ('30', 'so-1995430784879788032', '20.00', '2025-12-01 17:52:27', '20', 'paid', '0', null, '2025-12-01 17:52:27', null, null);
INSERT INTO `pos_sale` VALUES ('33', 'so-1995435423305965568', '20.00', '2025-12-01 18:10:53', null, 'paid', '0', null, '2025-12-01 18:10:53', null, '2025-12-01 18:10:56');
INSERT INTO `pos_sale` VALUES ('34', 'so-1995436586935918592', '20.00', '2025-12-01 18:15:30', '23', 'paid', '0', null, '2025-12-01 18:15:30', null, '2025-12-01 18:15:35');
INSERT INTO `pos_sale` VALUES ('35', 'so-1995879248524673024', '30.00', '2025-12-02 23:34:29', '24', 'paid', '0', null, '2025-12-02 23:34:29', null, '2025-12-02 23:35:12');
INSERT INTO `pos_sale` VALUES ('36', 'so-1995892051691671552', '80.00', '2025-12-03 00:25:22', '25', 'paid', '0', null, '2025-12-03 00:25:22', null, '2025-12-03 00:45:33');
INSERT INTO `pos_sale` VALUES ('37', 'so-1995899351240216576', '66.00', '2025-12-03 00:54:22', '26', 'paid', '0', null, '2025-12-03 00:54:22', null, '2025-12-03 00:54:27');
INSERT INTO `pos_sale` VALUES ('38', 'so-1995899726177406976', '66.00', '2025-12-03 00:55:51', '27', 'paid', '0', null, '2025-12-03 00:55:51', null, '2025-12-03 01:02:33');
INSERT INTO `pos_sale` VALUES ('40', 'so-1996086688482508800', '66.00', '2025-12-03 13:18:47', '28', 'paid', '0', null, '2025-12-03 13:18:47', null, '2025-12-03 13:18:57');
INSERT INTO `pos_sale` VALUES ('41', 'SO-14857975504044034', '0.00', '2025-12-08 00:56:33', null, 'unpaid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('42', 'SO-14859233929461763', '0.00', '2025-12-08 01:01:25', null, 'unpaid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('43', 'so-14863567551463428', '0.00', '2025-12-08 01:18:14', null, 'unpaid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('44', 'so-14864705717796869', '0.00', '2025-12-08 01:22:40', null, 'unpaid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('45', 'so-14867557576081414', '0.00', '2025-12-08 01:33:44', null, 'unpaid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('46', 'so-14867725079805959', '0.00', '2025-12-08 01:34:22', null, 'unpaid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('47', 'so-14867918353334280', '0.00', '2025-12-08 01:35:07', null, 'unpaid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('48', null, null, null, null, 'reserved', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('49', null, '152.00', null, '29', 'paid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('50', null, '66.00', null, '30', 'paid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('51', null, '66.00', null, '31', 'paid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('52', null, '66.00', null, '32', 'paid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('53', null, '66.00', null, null, 'reserved', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('54', null, '132.00', null, '33', 'paid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('55', null, '66.00', null, '34', 'paid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('56', null, '66.00', null, '35', 'paid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('57', null, '66.00', null, '36', 'paid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('58', 'so-15122094517911588', '66.00', '2025-12-08 18:01:28', '37', 'paid', '0', null, null, null, null);
INSERT INTO `pos_sale` VALUES ('59', 'so-15213405522624549', '66.00', '2025-12-08 23:55:48', '38', 'paid', '0', 'anonymousUser', '2025-12-08 23:56:01', 'anonymousUser', '2025-12-08 23:56:01');
INSERT INTO `pos_sale` VALUES ('60', 'so-15214294580854822', '45.00', '2025-12-08 23:59:14', '39', 'paid', '0', 'anonymousUser', '2025-12-08 23:59:22', 'anonymousUser', '2025-12-08 23:59:22');
INSERT INTO `pos_sale` VALUES ('61', 'so-15214749847388161', '66.00', '2025-12-09 00:01:00', '47', 'paid', '0', 'anonymousUser', '2025-12-09 00:01:05', 'anonymousUser', '2025-12-09 00:01:05');
INSERT INTO `pos_sale` VALUES ('62', 'so-15215578776076291', '20.00', '2025-12-09 00:04:14', '41', 'paid', '0', 'anonymousUser', '2025-12-09 00:04:17', 'anonymousUser', '2025-12-09 00:04:17');
INSERT INTO `pos_sale` VALUES ('65', 'so-15223176573222916', '10.00', '2025-12-09 00:33:43', '48', 'paid', '0', 'anonymousUser', '2025-12-09 00:39:01', 'anonymousUser', '2025-12-09 00:39:01');
INSERT INTO `pos_sale` VALUES ('66', 'so-15229107923058694', '66.00', '2025-12-09 00:56:44', '49', 'paid', '0', 'anonymousUser', '2025-12-09 00:56:52', 'anonymousUser', '2025-12-09 00:56:52');
INSERT INTO `pos_sale` VALUES ('67', 'so-15229142282797063', '45.00', '2025-12-09 00:56:52', '50', 'paid', '0', 'anonymousUser', '2025-12-09 00:57:21', 'anonymousUser', '2025-12-09 00:57:21');
INSERT INTO `pos_sale` VALUES ('68', 'so-32545088915636225', '45.00', '2026-01-24 16:51:34', '52', 'paid', '0', 'admin', '2026-01-24 16:51:39', 'admin', '2026-01-24 16:51:39');

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
-- Records of pos_sale_item
-- ----------------------------
INSERT INTO `pos_sale_item` VALUES ('1', '1', '1', null, null, '65.00', '1', 'paid', '0', null, '2022-06-25 14:43:26', null, '2022-06-26 14:43:26');
INSERT INTO `pos_sale_item` VALUES ('2', '1', '2', null, null, '20.00', '2', 'paid', '0', null, '2022-06-25 14:44:34', null, '2022-06-26 14:44:34');
INSERT INTO `pos_sale_item` VALUES ('3', '1', '4', null, null, '10.00', '3', 'paid', '0', null, '2022-06-25 14:45:22', null, '2022-06-26 14:45:22');
INSERT INTO `pos_sale_item` VALUES ('4', '2', '2', null, null, '20.00', '1', 'paid', '0', null, '2022-06-25 18:21:06', null, '2022-06-26 18:21:06');
INSERT INTO `pos_sale_item` VALUES ('5', '2', '5', null, null, '80.00', '2', 'paid', '0', null, '2022-06-25 18:21:10', null, '2022-06-26 18:21:10');
INSERT INTO `pos_sale_item` VALUES ('6', '2', '6', null, null, '30.00', '3', 'paid', '0', null, '2022-06-25 18:21:18', null, '2022-06-26 18:21:18');
INSERT INTO `pos_sale_item` VALUES ('7', '3', '5', null, null, '80.00', '1', 'paid', '0', null, '2022-06-25 18:21:22', 'anonymousUser', '2025-12-13 23:36:12');
INSERT INTO `pos_sale_item` VALUES ('8', '3', '6', null, null, '30.00', '1', 'paid', '0', null, '2022-06-25 18:21:27', 'anonymousUser', '2025-12-13 23:36:12');
INSERT INTO `pos_sale_item` VALUES ('9', '4', '1', null, null, '65.00', '1', 'paid', '0', null, '2025-11-26 20:05:25', null, '2025-11-26 20:05:34');
INSERT INTO `pos_sale_item` VALUES ('10', '5', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-11-28 00:14:49', null, '2025-11-28 00:14:56');
INSERT INTO `pos_sale_item` VALUES ('11', '6', '4', null, null, '10.00', '1', 'paid', '0', null, '2025-11-28 00:25:31', null, '2025-11-28 00:25:32');
INSERT INTO `pos_sale_item` VALUES ('12', '7', '5', null, null, '80.00', '1', 'paid', '0', null, '2025-11-28 00:26:24', null, '2025-11-28 00:26:30');
INSERT INTO `pos_sale_item` VALUES ('13', '7', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-11-28 00:26:24', null, '2025-11-28 00:26:30');
INSERT INTO `pos_sale_item` VALUES ('14', '8', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-11-28 00:28:52', null, '2025-11-28 00:28:59');
INSERT INTO `pos_sale_item` VALUES ('15', '9', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-11-28 15:52:43', null, '2025-11-28 15:53:05');
INSERT INTO `pos_sale_item` VALUES ('16', '10', '2', null, null, '20.00', '2', 'paid', '0', null, '2025-11-28 16:03:09', null, '2025-11-28 16:03:12');
INSERT INTO `pos_sale_item` VALUES ('17', '11', '2', null, null, '20.00', '1', 'paid', '0', null, '2025-11-29 00:38:36', null, '2025-11-29 00:39:08');
INSERT INTO `pos_sale_item` VALUES ('18', '12', '1', null, null, '66.00', '1', 'reserved', '0', null, '2025-11-30 15:37:11', null, null);
INSERT INTO `pos_sale_item` VALUES ('19', '13', '7', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('20', '14', '8', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('21', '15', '1', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('22', '16', '1', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('23', '17', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-11-30 16:49:05', null, null);
INSERT INTO `pos_sale_item` VALUES ('24', '18', '6', null, null, '30.00', '1', 'paid', '0', null, '2025-11-30 16:54:01', null, null);
INSERT INTO `pos_sale_item` VALUES ('25', '19', '3', null, null, '45.00', '1', 'reserved', '0', null, '2025-11-30 16:54:43', null, null);
INSERT INTO `pos_sale_item` VALUES ('26', '20', '7', null, null, '13499.00', '1', 'paid', '0', null, '2025-11-30 23:19:46', null, '2025-11-30 23:22:52');
INSERT INTO `pos_sale_item` VALUES ('27', '21', '8', null, null, '6499.00', '1', 'paid', '0', null, '2025-11-30 23:23:49', null, null);
INSERT INTO `pos_sale_item` VALUES ('28', '22', '4', null, null, '10.00', '1', 'paid', '0', null, '2025-11-30 23:32:02', null, '2025-11-30 23:32:24');
INSERT INTO `pos_sale_item` VALUES ('29', '23', '4', null, null, '10.00', '4', 'paid', '0', null, '2025-11-30 23:39:41', null, '2025-11-30 23:40:14');
INSERT INTO `pos_sale_item` VALUES ('30', '24', '2', null, null, '20.00', '1', 'paid', '0', null, '2025-12-01 01:01:49', null, null);
INSERT INTO `pos_sale_item` VALUES ('31', '25', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-12-01 15:41:16', null, null);
INSERT INTO `pos_sale_item` VALUES ('32', '26', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-12-01 15:49:09', null, null);
INSERT INTO `pos_sale_item` VALUES ('33', '27', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-12-01 16:01:18', null, null);
INSERT INTO `pos_sale_item` VALUES ('34', '28', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-12-01 16:31:45', null, null);
INSERT INTO `pos_sale_item` VALUES ('35', '29', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-12-01 17:47:32', null, null);
INSERT INTO `pos_sale_item` VALUES ('36', '30', '2', null, null, '20.00', '1', 'paid', '0', null, '2025-12-01 17:52:37', null, null);
INSERT INTO `pos_sale_item` VALUES ('39', '33', '2', null, null, '20.00', '1', 'paid', '0', null, '2025-12-01 18:10:56', null, null);
INSERT INTO `pos_sale_item` VALUES ('40', '34', '2', null, null, '20.00', '1', 'paid', '0', null, '2025-12-01 18:15:35', null, null);
INSERT INTO `pos_sale_item` VALUES ('41', '35', '4', null, null, '10.00', '3', 'paid', '0', null, '2025-12-02 23:35:12', null, null);
INSERT INTO `pos_sale_item` VALUES ('42', '36', '2', null, null, '20.00', '4', 'paid', '0', null, '2025-12-03 00:45:33', null, null);
INSERT INTO `pos_sale_item` VALUES ('43', '37', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-12-03 00:54:27', null, null);
INSERT INTO `pos_sale_item` VALUES ('44', '38', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-12-03 00:55:55', null, '2025-12-03 01:02:33');
INSERT INTO `pos_sale_item` VALUES ('45', '40', '1', null, null, '66.00', '1', 'paid', '0', null, '2025-12-03 13:18:52', null, '2025-12-03 13:18:57');
INSERT INTO `pos_sale_item` VALUES ('46', '48', '1', null, null, '66.00', '1', 'reserved', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('47', '48', '2', null, null, '20.00', '1', 'reserved', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('48', '49', '1', null, null, '66.00', '2', 'paid', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('49', '49', '2', null, null, '20.00', '1', 'paid', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('50', '50', '1', null, null, '66.00', '1', 'paid', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('51', '51', '1', null, null, '66.00', '1', 'paid', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('52', '52', '1', null, null, '66.00', '1', 'paid', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('53', '53', '1', null, null, '66.00', '1', 'reserved', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('54', '54', '1', null, null, '66.00', '2', 'paid', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('55', '55', '1', null, null, '66.00', '1', 'paid', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('56', '56', '1', null, null, '66.00', '1', 'paid', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('57', '57', '1', null, null, '66.00', '1', 'paid', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('58', '58', '1', null, null, '66.00', '1', 'paid', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('59', '59', '1', null, null, '66.00', '1', 'paid', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('60', '60', '3', null, null, '45.00', '1', 'paid', null, null, null, null, null);
INSERT INTO `pos_sale_item` VALUES ('61', '61', '1', null, null, '66.00', '1', 'paid', null, null, null, 'anonymousUser', '2025-12-09 00:31:15');
INSERT INTO `pos_sale_item` VALUES ('62', '62', '2', null, null, '20.00', '1', 'paid', '0', 'anonymousUser', '2025-12-09 00:04:17', null, null);
INSERT INTO `pos_sale_item` VALUES ('63', '65', '4', '1004', '毛巾', '10.00', '1', 'paid', '0', 'anonymousUser', '2025-12-09 00:39:01', 'anonymousUser', '2025-12-09 00:41:24');
INSERT INTO `pos_sale_item` VALUES ('64', '66', '1', '1001', '钢笔', '66.00', '1', 'paid', '0', 'anonymousUser', '2025-12-09 00:56:52', 'anonymousUser', '2025-12-09 00:57:06');
INSERT INTO `pos_sale_item` VALUES ('65', '67', '3', '1003', '铅笔盒', '45.00', '1', 'paid', '0', 'anonymousUser', '2025-12-09 00:57:21', 'anonymousUser', '2025-12-09 00:57:21');
INSERT INTO `pos_sale_item` VALUES ('66', '68', '3', '1003', '铅笔盒', '45.00', '1', 'paid', '0', 'admin', '2026-01-24 16:51:40', 'admin', '2026-01-24 16:51:40');

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='优惠券表';

-- ----------------------------
-- Records of sms_coupon
-- ----------------------------
INSERT INTO `sms_coupon` VALUES ('1', '测试满减券', '99', '100.00', '1', null, '2026-01-31 22:25:36', '1', '0.00');
INSERT INTO `sms_coupon` VALUES ('2', 'xujialiNB666', '99', '10.00', '1', '2026-01-27 00:00:00', '2026-02-19 00:00:00', '1', '0.00');

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
-- Records of sms_coupon_history
-- ----------------------------
INSERT INTO `sms_coupon_history` VALUES ('1', '1', '2', 'customer', '1', '2026-01-24 22:26:00', '1', '2026-01-27 18:04:28', '19', null);
INSERT INTO `sms_coupon_history` VALUES ('2', '2', '2', 'customer', '1', '2026-01-27 18:11:41', '0', null, null, null);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(64) NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码（加密存储）',
  `role` varchar(20) DEFAULT 'CUSTOMER' COMMENT '角色: ADMIN-管理员, CUSTOMER-顾客',
  `status` char(1) DEFAULT '0' COMMENT '状态: 0-正常, 1-禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像URL',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=2000587179186851842 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'admin', '$2a$10$GRKVyrThMGdzt8CnRkE.n.BWB1izTyOZds3RJhFDpTW6Ybry2Js5K', 'ADMIN', '0', '2025-12-12 10:38:48', '13445678900', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/eb2f98d3-dee9-48e5-9b90-eded6ef889b8.jpg');
INSERT INTO `sys_user` VALUES ('2', 'customer', '$2a$10$pAdiceDkK09AF5a42P97kOH1lPEDnPqBa1hPDLcE5INhfP/VTGw/y', 'CUSTOMER', '0', '2025-12-12 10:38:50', '13456789000', 'https://scau-mis-images.oss-cn-shenzhen.aliyuncs.com/f38f943e-9213-4a58-bdc1-e859e03f214f.jpg');
INSERT INTO `sys_user` VALUES ('1999528856879591425', 'new_user', '$2a$10$GpCOYlIY5bcfulmzkpf.XuITzpEV4kxRRCih4.r2gJmUdx7e6p8gS', 'CUSTOMER', '0', null, null, null);
INSERT INTO `sys_user` VALUES ('2000587179186851841', '13800138000', '$2a$10$jPJJkI6C4xiuboOT.8hVxuiMT1HOEOFvKJ10n/VzOVoB1Wp8R3qYu', 'CUSTOMER', '0', '2025-12-15 23:22:07', '13800138000', null);
