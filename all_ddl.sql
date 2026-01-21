-- MySQL dump 10.13  Distrib 9.4.0, for macos14.7 (arm64)
--
-- Host: localhost    Database: account_life
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account_balance_history`
--

DROP TABLE IF EXISTS `account_balance_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_balance_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '余额历史ID',
  `account_id` bigint DEFAULT NULL COMMENT '账户ID',
  `user_id` bigint DEFAULT NULL COMMENT '所属用户',
  `balance` decimal(18,2) DEFAULT NULL COMMENT '账户余额',
  `currency` varchar(10) DEFAULT 'CNY' COMMENT '币种',
  `record_date` date DEFAULT NULL COMMENT '记录日期',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='账户余额历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_config`
--

DROP TABLE IF EXISTS `account_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户名称（如 招商银行、支付宝）',
  `type` tinyint NOT NULL COMMENT '账户类型：1-bank，2-platform，3-credit_wallet，4-wallet，5-insurance，6-securities',
  `platform_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '平台标识（如 ALIPAY、MEITUAN）',
  `website_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '官网登录地址',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述信息',
  `logo_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Logo图片地址',
  `sort_order` int DEFAULT '0' COMMENT '排序权重',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `bill_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账单邮箱地址',
  `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`),
  KEY `idx_type_active` (`type`,`is_active`,`sort_order`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户配置表（系统级）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_transaction`
--

DROP TABLE IF EXISTS `account_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '交易ID',
  `user_id` bigint NOT NULL COMMENT '所属用户',
  `account_id` bigint NOT NULL COMMENT '发生账户',
  `type` tinyint NOT NULL COMMENT '交易类型：1-income，2-expense，3-transfer_out，4-transfer_in',
  `amount` decimal(18,2) NOT NULL COMMENT '交易金额',
  `discount_amount` decimal(18,2) DEFAULT '0.00' COMMENT '业务折扣/优惠金额',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `related_transaction_id` bigint DEFAULT NULL COMMENT '关联交易ID',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要说明',
  `transaction_time` datetime DEFAULT NULL COMMENT '实际发生时间',
  `source_type` tinyint DEFAULT NULL COMMENT '来源类型：0-宁波银行信用卡，1-LabelDetail账单，2-银行，3-平台，4-信用钱包',
  `source_ref` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原始账单唯一标识',
  `statement_id` bigint DEFAULT NULL COMMENT '账单归属ID',
  `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `account_id` (`account_id`),
  KEY `category_id` (`category_id`),
  KEY `related_transaction_id` (`related_transaction_id`)
) ENGINE=InnoDB AUTO_INCREMENT=847 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户交易明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_transaction_upload_file_log`
--

DROP TABLE IF EXISTS `account_transaction_upload_file_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_transaction_upload_file_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联用户表）',
  `account_id` bigint NOT NULL COMMENT '账户ID（关联账户表）',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名称',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `zip_password` int DEFAULT NULL COMMENT '压缩文件密码（如果有）',
  `transaction_start_date` datetime DEFAULT NULL COMMENT '交易开始日期',
  `transaction_end_date` datetime DEFAULT NULL COMMENT '交易结束日期',
  `md5_checksum` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件MD5校验码',
  `status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '处理状态：PENDING, PROCESSING, COMPLETED, FAILED',
  `total_records` int DEFAULT '0' COMMENT '总记录数',
  `success_count` int DEFAULT '0' COMMENT '成功导入记录数',
  `failure_count` int DEFAULT '0' COMMENT '失败记录数',
  `error_message` text COLLATE utf8mb4_unicode_ci COMMENT '错误信息（如果有）',
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易导入文件日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `budget`
--

DROP TABLE IF EXISTS `budget`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `budget` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `name` varchar(255) NOT NULL COMMENT '预算名称',
  `period_type` int NOT NULL COMMENT '预算类型：1-月度预算，2-年度预算，3-分类预算',
  `category_id` int DEFAULT NULL COMMENT '分类ID（分类预算时使用）',
  `amount` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '预算金额',
  `used_amount` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '已使用金额',
  `start_date` date NOT NULL COMMENT '预算开始日期',
  `end_date` date NOT NULL COMMENT '预算结束日期',
  `status` int NOT NULL DEFAULT '1' COMMENT '预算状态：1-进行中，2-已完成，3-已超支',
  `alert_threshold` decimal(5,2) DEFAULT NULL COMMENT '提醒阈值（百分比）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`),
  KEY `idx_budget_user` (`user_id`),
  KEY `idx_budget_category` (`category_id`),
  KEY `idx_budget_period` (`start_date`,`end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预算表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `business_transaction`
--

DROP TABLE IF EXISTS `business_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `business_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主交易ID',
  `category_id` bigint NOT NULL COMMENT '交易分类ID',
  `user_id` bigint NOT NULL COMMENT '所属用户',
  `amount` decimal(18,2) NOT NULL COMMENT '交易金额',
  `discount_amount` decimal(18,2) DEFAULT '0.00' COMMENT '业务折扣/优惠金额',
  `transaction_time` datetime NOT NULL COMMENT '交易发生时间',
  `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业务交易表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `business_transaction_flow`
--

DROP TABLE IF EXISTS `business_transaction_flow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `business_transaction_flow` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `business_id` bigint NOT NULL COMMENT '业务ID',
  `from_transaction_id` bigint NOT NULL COMMENT '资金来源交易ID',
  `to_transaction_id` bigint NOT NULL COMMENT '资金去向交易ID',
  `amount` decimal(18,2) NOT NULL COMMENT '流动金额',
  `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`),
  KEY `idx_business_id` (`business_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业务交易流';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category_keyword_mapping`
--

DROP TABLE IF EXISTS `category_keyword_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category_keyword_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` int NOT NULL COMMENT '关联transaction_category.id',
  `keyword` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关键词',
  `weight` int DEFAULT '1' COMMENT '权重：1-普通，2-重要，3-核心',
  `user_id` int DEFAULT NULL COMMENT '所属用户，null表示系统默认',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_keyword` (`keyword`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4391 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类关键词映射表：支持用户自定义关键词与分类ID映射';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `credit_wallet_statement`
--

DROP TABLE IF EXISTS `credit_wallet_statement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `credit_wallet_statement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '账单ID',
  `account_id` bigint DEFAULT NULL COMMENT '信用钱包账户ID',
  `user_id` bigint DEFAULT NULL COMMENT '所属用户',
  `billing_period_start` date DEFAULT NULL COMMENT '账单期开始日期',
  `billing_period_end` date DEFAULT NULL COMMENT '账单期结束日期',
  `total_amount` decimal(18,2) DEFAULT NULL COMMENT '应还金额',
  `repay_due_date` date DEFAULT NULL COMMENT '还款到期日',
  `repay_date` date DEFAULT NULL COMMENT '实际还款日',
  `status` tinyint DEFAULT NULL COMMENT '账单状态：1-open，2-paid，3-overdue',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='信用钱包账单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fixed_asset`
--

DROP TABLE IF EXISTS `fixed_asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fixed_asset` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '固定资产ID',
  `user_id` bigint DEFAULT NULL COMMENT '所属用户',
  `name` varchar(100) DEFAULT NULL COMMENT '资产名称',
  `type` tinyint DEFAULT NULL COMMENT '资产类型：1-property，2-vehicle，3-equipment，4-other',
  `value` decimal(18,2) DEFAULT NULL COMMENT '评估/购入金额',
  `purchase_date` date DEFAULT NULL COMMENT '购买日期',
  `location` varchar(200) DEFAULT NULL COMMENT '地址或位置',
  `note` text COMMENT '备注',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='固定资产表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `investment_asset`
--

DROP TABLE IF EXISTS `investment_asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `investment_asset` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '投资资产ID',
  `user_id` bigint DEFAULT NULL COMMENT '所属用户',
  `account_id` bigint DEFAULT NULL COMMENT '关联账户',
  `code` varchar(50) DEFAULT NULL COMMENT '股票/基金代码',
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `type` tinyint DEFAULT NULL COMMENT '类型：1-stock，2-fund，3-bond',
  `quantity` decimal(18,4) DEFAULT NULL COMMENT '持仓数量',
  `cost_price` decimal(18,4) DEFAULT NULL COMMENT '成本价',
  `market_price` decimal(18,4) DEFAULT NULL COMMENT '市价',
  `currency` varchar(10) DEFAULT NULL COMMENT '币种',
  `last_updated` datetime DEFAULT NULL COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='投资资产表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `platform_transaction`
--

DROP TABLE IF EXISTS `platform_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `platform_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '平台账单ID',
  `user_id` bigint DEFAULT NULL COMMENT '所属用户',
  `account_id` bigint DEFAULT NULL COMMENT '账户编码',
  `raw_json` text COMMENT '原始账单JSON数据',
  `transaction_id` bigint DEFAULT NULL COMMENT '映射到业务交易ID',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '导入时间',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='平台交易原始账单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_config`
--

DROP TABLE IF EXISTS `report_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报表配置ID',
  `user_id` bigint DEFAULT NULL COMMENT '所属用户',
  `name` varchar(100) DEFAULT NULL COMMENT '报表名称',
  `type` tinyint DEFAULT NULL COMMENT '报表类型：1-category_summary, 2-trend_analysis',
  `config` text COMMENT '报表配置JSON',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报表配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `user_id` bigint DEFAULT NULL COMMENT '所属用户',
  `name` varchar(50) DEFAULT NULL COMMENT '标签名称',
  `color` varchar(20) DEFAULT NULL COMMENT '标签颜色',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_category`
--

DROP TABLE IF EXISTS `transaction_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称（如 餐饮）',
  `type` tinyint NOT NULL COMMENT '分类类型：1-income，2-expense，3-transfer_out，4-transfer_in',
  `parent_id` bigint DEFAULT NULL COMMENT '父分类ID',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `user_id` bigint DEFAULT NULL COMMENT '所属用户，null 表示系统分类',
  `sort_order` int DEFAULT '0' COMMENT '排序值',
  `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`),
  KEY `parent_id` (`parent_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_tag`
--

DROP TABLE IF EXISTS `transaction_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `transaction_id` bigint DEFAULT NULL COMMENT '交易ID',
  `tag_id` bigint DEFAULT NULL COMMENT '标签ID',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码哈希',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-正常，0-禁用',
  `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_account`
--

DROP TABLE IF EXISTS `user_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `user_id` bigint NOT NULL COMMENT '所属用户',
  `parent_id` bigint DEFAULT NULL COMMENT '父节点',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账户名称（如 招商银行、花呗）',
  `type` tinyint NOT NULL COMMENT '账户类型：1-bank，2-platform，3-credit_wallet，4-wallet',
  `platform_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '平台标识（如 ALIPAY、MEITUAN）',
  `account_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '银行卡号/平台账号',
  `balance` decimal(18,2) DEFAULT '0.00' COMMENT '账户余额',
  `is_virtual` tinyint(1) DEFAULT '0' COMMENT '是否为虚拟账户',
  `credit_limit` decimal(18,2) DEFAULT NULL COMMENT '信用额度，仅信用钱包用',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'CNY' COMMENT '币种',
  `bill_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账单邮箱地址',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账户描述',
  `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_account_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户组ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组描述',
  `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户组表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_group_member`
--

DROP TABLE IF EXISTS `user_group_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_group_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `group_id` bigint NOT NULL COMMENT '用户组ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` tinyint NOT NULL COMMENT '角色：1-owner组长，2-member成员',
  `joined_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`),
  KEY `group_id` (`group_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_group_member_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `user_group` (`id`),
  CONSTRAINT `user_group_member_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户组成员关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_mail_config`
--

DROP TABLE IF EXISTS `user_mail_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_mail_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联用户表）',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名称',
  `host` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SMTP服务器地址',
  `port` int NOT NULL COMMENT 'SMTP端口',
  `email_address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱账号',
  `auth_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱授权码',
  `enable_ssl` tinyint(1) DEFAULT '0' COMMENT '是否启用SSL',
  `enable_tls` tinyint(1) DEFAULT '1' COMMENT '是否启用TLS',
  `connection_timeout` int DEFAULT '30000' COMMENT '连接超时时间（毫秒）',
  `read_timeout` int DEFAULT '30000' COMMENT '读取超时时间（毫秒）',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述信息',
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_email_address` (`email_address`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-19 20:23:12
