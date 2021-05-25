SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `lms_item`;
CREATE TABLE `lms_item` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `locator` varchar(64) DEFAULT NULL,
                            `delivery_sn` varchar(64) DEFAULT NULL COMMENT '运单号',
                            `user_sn` varchar(64) DEFAULT NULL COMMENT '用户识别码',
                            `location` varchar(64) DEFAULT NULL COMMENT '货物地点',
                            `note` varchar(500) DEFAULT NULL COMMENT '备注信息',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `sku` varchar(64) DEFAULT NULL COMMENT 'SKU',
                            `size` varchar(64) DEFAULT NULL COMMENT '尺寸',
                            `status` int(1) DEFAULT '1' COMMENT '货物状态：0->未入库；1->已入库',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='货物表';

INSERT INTO `lms_item` VALUES ('1', 'US000001', '5438264791723471237021730281', 'HFEUHF', 'US', '测试商品1','2021-05-13 13:55:30', '12345', 'large', '0');
INSERT INTO `lms_item` VALUES ('2', 'US000002', '5438264791723471237021730282', 'HFEUHF', 'US', '测试商品2','2021-05-14 13:55:30', '12346', 'small', '1');



DROP TABLE IF EXISTS `lms_order_item_relation`;
CREATE TABLE `lms_order_item_relation` (
                                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                           `item_id` bigint(20) DEFAULT NULL,
                                           `order_id` bigint(20) DEFAULT NULL,
                                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8 COMMENT='货物和订单关系表';

INSERT INTO `lms_order_item_relation` VALUES ('27', '1', '1');
INSERT INTO `lms_order_item_relation` VALUES ('28', '2', '2');

DROP TABLE IF EXISTS `lms_order`;
CREATE TABLE `lms_order` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `action` varchar(64) DEFAULT NULL COMMENT '订单操作',
                            `weight` decimal(19,2) DEFAULT NULL COMMENT '重 量',
                            `weight_unit` int(1) DEFAULT NULL COMMENT '重量单位：0->lbs；1->kg',
                            `amount` int(1) DEFAULT '1' COMMENT '货物数量',
                            `delivery_sn` varchar(64) DEFAULT NULL COMMENT '运单号',
                            `user_sn` varchar(64) DEFAULT NULL COMMENT '用户识别码',
                            `origin` varchar(64) DEFAULT NULL COMMENT '发货地点',
                            `destination` varchar(64) DEFAULT NULL COMMENT '到货地点',
                            `note` varchar(500) DEFAULT NULL COMMENT '备注信息',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `status` int(1) DEFAULT '1' COMMENT '订单状态',
                            `payment_status` int(1) DEFAULT '1' COMMENT '支付状态：0->未定价；1->待支付 2->未支付 3->支付失败 4->支付取消',
                            `payment_time` datetime DEFAULT NULL COMMENT '支付成功时间',
                            `price` decimal(19,2) DEFAULT NULL COMMENT '价格',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='订单表';

INSERT INTO `lms_order` VALUES ('1', '发往国内仓', '2', '0', '1', '372917498739847', 'HFEUHF', 'US', 'CN', '测试订单1', '2021-05-14 13:55:30', '1', '0', null, '20.00');
INSERT INTO `lms_order` VALUES ('2', '发往国内仓', '2.5', '0', '1', '372917498739847', 'HFEUHF', 'US', 'CN', '测试订单2', '2021-05-14 13:55:30', '1', '0', null, '25.00');
