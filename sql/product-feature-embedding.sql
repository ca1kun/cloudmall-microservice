DROP TABLE IF EXISTS `pos_product_feature_embedding`;
CREATE TABLE `pos_product_feature_embedding` (
  `embedding_id` bigint NOT NULL AUTO_INCREMENT COMMENT '向量ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `dimensions` int DEFAULT NULL COMMENT '向量维度',
  `embedding_text` text COMMENT '用于生成向量的文本',
  `embedding_vector` mediumtext COMMENT '向量JSON',
  `model_name` varchar(128) DEFAULT NULL COMMENT '模型名',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`embedding_id`),
  UNIQUE KEY `uk_product_feature_embedding_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品特征向量表';
