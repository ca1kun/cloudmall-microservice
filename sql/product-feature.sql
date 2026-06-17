DROP TABLE IF EXISTS `pos_product_feature`;
CREATE TABLE `pos_product_feature` (
  `feature_id` bigint NOT NULL AUTO_INCREMENT COMMENT '特征ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(128) DEFAULT NULL COMMENT '商品名称',
  `category_name` varchar(64) DEFAULT NULL COMMENT '类目名称',
  `brand` varchar(64) DEFAULT NULL COMMENT '品牌',
  `price_band` varchar(32) DEFAULT NULL COMMENT '价格带',
  `scene_tags` varchar(255) DEFAULT NULL COMMENT '场景标签，逗号分隔',
  `feature_tags` varchar(255) DEFAULT NULL COMMENT '特征标签，逗号分隔',
  `search_text` varchar(1000) DEFAULT NULL COMMENT '检索文本',
  `feature_summary` varchar(255) DEFAULT NULL COMMENT '特征摘要',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`feature_id`),
  UNIQUE KEY `uk_product_feature_product_id` (`product_id`),
  KEY `idx_product_feature_brand` (`brand`),
  KEY `idx_product_feature_price_band` (`price_band`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品特征表';
