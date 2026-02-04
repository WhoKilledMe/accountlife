

-- ============================================
-- 测试数据：fin_account 账户数据
-- 参考 user_account 表数据创建
-- ============================================
INSERT INTO `fin_account` (
    `id`, `user_id`, `account_code`, `account_name`, `account_category`, `account_type`,
    `owner_type`, `platform_code`, `currency`, `balance`, `balance_updated_at`,
    `external_account_ref`, `is_virtual`, `status`, `remark`,
    `created_by`, `created_at`, `updated_by`, `updated_at`, `is_deleted`
) VALUES
      -- 工商银行储蓄卡
      (1, 3, 'ACCT-3-1', '工商银行储蓄卡', 'CASH', 'BANK_CARD', 'USER', 'ICBC', 'CNY', 10000.00,
       '2025-09-03 10:09:47', NULL, 0, 'ACTIVE', '主要储蓄账户',
       'system', '2025-08-12 17:20:51', 'system', '2025-09-03 10:09:47', 0),

      -- 支付宝余额
      (2, 3, 'ACCT-3-2', '支付宝余额', 'CASH', 'E_WALLET', 'USER', 'ALIPAY', 'CNY', 5000.00,
       '2025-09-03 10:09:47', NULL, 0, 'ACTIVE', '日常消费账户',
       'system', '2025-08-12 17:20:51', 'system', '2025-09-03 10:09:47', 0),

      -- 微信钱包
      (3, 3, 'ACCT-3-3', '微信钱包', 'CASH', 'E_WALLET', 'USER', 'WECHAT', 'CNY', 2000.00,
       '2025-09-03 10:09:47', NULL, 0, 'ACTIVE', '零钱账户',
       'system', '2025-08-12 17:20:51', 'system', '2025-09-03 10:09:47', 0),

      -- 建设银行储蓄卡
      (4, 3, 'ACCT-3-4', '建设银行储蓄卡', 'CASH', 'BANK_CARD', 'USER', 'CCB', 'CNY', 8000.00,
       '2025-09-03 10:09:47', NULL, 0, 'ACTIVE', '储蓄账户',
       'system', '2025-08-12 17:20:51', 'system', '2025-09-03 10:09:47', 0),

      -- 宁波信用卡
      (5, 3, 'ACCT-3-5', '宁波信用卡', 'CASH', 'CREDIT', 'USER', 'NINGBO', 'CNY', 0.00,
       '2025-08-12 19:53:09', NULL, 0, 'ACTIVE', '信用卡',
       'system', '2025-08-12 19:53:09', 'system', '2025-08-12 19:53:09', 0);


-- ============================================
-- 测试数据：fin_platform 平台字典数据
-- ============================================
INSERT INTO `fin_platform` (
    `id`, `platform_code`, `platform_name`, `platform_type`, `logo_url`, `website_url`,
    `description`, `support_bill`, `bill_import_format`, `bill_email_domain`,
    `api_config`, `sort_order`, `status`,
    `created_by`, `created_at`, `updated_by`, `updated_at`, `is_deleted`
) VALUES
      -- 微信
      (1, 'WECHAT', '微信', 'E_WALLET', NULL, 'https://weixin.qq.com',
       '微信支付平台，支持微信钱包、微信支付', 1, 'CSV', NULL,
       NULL, 10, 'ACTIVE',
       'system', NOW(), 'system', NOW(), 0),

      -- 支付宝
      (2, 'ALIPAY', '支付宝', 'E_WALLET', NULL, 'https://www.alipay.com',
       '支付宝平台，支持支付宝余额、花呗等', 1, 'CSV', NULL,
       NULL, 9, 'ACTIVE',
       'system', NOW(), 'system', NOW(), 0),

      -- 美团
      (3, 'MEITUAN', '美团', 'PLATFORM', NULL, 'https://www.meituan.com',
       '美团平台，支持美团支付、美团月付等', 1, 'CSV', NULL,
       NULL, 8, 'ACTIVE',
       'system', NOW(), 'system', NOW(), 0),

      -- 工商银行
      (4, 'ICBC', '工商银行', 'BANK', NULL, 'https://www.icbc.com.cn',
       '中国工商银行', 1, 'CSV', NULL,
       NULL, 7, 'ACTIVE',
       'system', NOW(), 'system', NOW(), 0),

      -- 建设银行
      (5, 'CCB', '建设银行', 'BANK', NULL, 'https://www.ccb.com',
       '中国建设银行', 1, 'CSV', NULL,
       NULL, 6, 'ACTIVE',
       'system', NOW(), 'system', NOW(), 0),

      -- 宁波银行
      (6, 'NINGBO', '宁波银行', 'BANK', NULL, 'https://www.nbcb.com.cn',
       '宁波银行，支持信用卡账单导入', 1, 'CSV', NULL,
       NULL, 5, 'ACTIVE',
       'system', NOW(), 'system', NOW(), 0),

      -- 招商银行
      (7, 'CMB', '招商银行', 'BANK', NULL, 'https://www.cmbchina.com',
       '招商银行', 1, 'CSV', NULL,
       NULL, 4, 'ACTIVE',
       'system', NOW(), 'system', NOW(), 0),

      -- 抖音
      (8, 'TIKTOK', '抖音', 'PLATFORM', NULL, 'https://www.douyin.com',
       '抖音平台，支持抖音支付', 1, 'CSV', NULL,
       NULL, 3, 'ACTIVE',
       'system', NOW(), 'system', NOW(), 0),

      -- 同花顺
      (9, 'TONGHUASHUN', '同花顺', 'INVESTMENT', NULL, 'https://www.10jqka.com.cn',
       '同花顺投资平台', 1, 'CSV', NULL,
       NULL, 2, 'ACTIVE',
       'system', NOW(), 'system', NOW(), 0),

      -- 其他
      (10, 'OTHER', '其他', 'OTHER', NULL, NULL,
       '其他未分类平台', 1, 'CSV', NULL,
       NULL, 1, 'ACTIVE',
       'system', NOW(), 'system', NOW(), 0);


-- ============================================
-- 测试数据：fin_payment_method 支付方式数据
-- 按平台维度插入
-- ============================================
INSERT INTO `fin_payment_method` (
    `id`, `payment_code`, `payment_name`, `payment_type`, `platform_code`,
    `status`, `remark`, `created_at`, `updated_at`
) VALUES
      -- ========== 微信平台 ==========
      -- 微信支付（聚合支付）
      (1, 'WECHAT_PAY', '微信支付', 'PLATFORM', 'WECHAT',
       'ACTIVE', '微信聚合支付，支持多种支付方式', NOW(), NOW()),

      -- 零钱
      (2, 'WECHAT_BALANCE', '零钱', 'BALANCE', 'WECHAT',
       'ACTIVE', '微信零钱余额支付', NOW(), NOW()),

      -- 零钱通
      (3, 'WECHAT_LINGQIAN_TONG', '零钱通', 'BALANCE', 'WECHAT',
       'ACTIVE', '微信零钱通理财产品', NOW(), NOW()),

      -- ========== 支付宝平台 ==========
      -- 支付宝支付（聚合支付）
      (4, 'ALIPAY_PAY', '支付宝支付', 'PLATFORM', 'ALIPAY',
       'ACTIVE', '支付宝聚合支付，支持多种支付方式', NOW(), NOW()),

      -- 余额
      (5, 'ALIPAY_BALANCE', '余额', 'BALANCE', 'ALIPAY',
       'ACTIVE', '支付宝余额支付', NOW(), NOW()),

      -- 余额宝
      (6, 'ALIPAY_YUEBAO', '余额宝', 'BALANCE', 'ALIPAY',
       'ACTIVE', '支付宝余额宝理财产品', NOW(), NOW()),

      -- 花呗
      (7, 'ALIPAY_HUABEI', '花呗', 'CREDIT', 'ALIPAY',
       'ACTIVE', '支付宝花呗信用支付', NOW(), NOW()),

      -- ========== 美团平台 ==========
      -- 美团支付
      (8, 'MEITUAN_PAY', '美团支付', 'PLATFORM', 'MEITUAN',
       'ACTIVE', '美团聚合支付', NOW(), NOW()),

      -- 美团月付
      (9, 'MEITUAN_MONTH', '美团月付', 'CREDIT', 'MEITUAN',
       'ACTIVE', '美团月付信用支付', NOW(), NOW()),

      -- ========== 抖音平台 ==========
      -- 抖音支付
      (10, 'TIKTOK_PAY', '抖音支付', 'PLATFORM', 'TIKTOK',
       'ACTIVE', '抖音聚合支付', NOW(), NOW()),

      -- ========== 银行类 - 通用支付方式 ==========
      -- 银行卡支付（借记卡）
      (11, 'BANK_DEBIT', '银行卡支付', 'BANK', NULL,
       'ACTIVE', '银行卡借记支付，适用于所有银行', NOW(), NOW()),

      -- 信用卡支付
      (12, 'CREDIT_PAY', '信用卡支付', 'CREDIT', NULL,
       'ACTIVE', '信用卡支付，适用于所有银行信用卡', NOW(), NOW()),

      -- ========== 工商银行 ==========
      -- 工商银行借记卡
      (13, 'ICBC_DEBIT', '工商银行借记卡', 'BANK', 'ICBC',
       'ACTIVE', '工商银行储蓄卡支付', NOW(), NOW()),

      -- 工商银行信用卡
      (14, 'ICBC_CREDIT', '工商银行信用卡', 'CREDIT', 'ICBC',
       'ACTIVE', '工商银行信用卡支付', NOW(), NOW()),

      -- ========== 建设银行 ==========
      -- 建设银行借记卡
      (15, 'CCB_DEBIT', '建设银行借记卡', 'BANK', 'CCB',
       'ACTIVE', '建设银行储蓄卡支付', NOW(), NOW()),

      -- 建设银行信用卡
      (16, 'CCB_CREDIT', '建设银行信用卡', 'CREDIT', 'CCB',
       'ACTIVE', '建设银行信用卡支付', NOW(), NOW()),

      -- ========== 宁波银行 ==========
      -- 宁波银行借记卡
      (17, 'NINGBO_DEBIT', '宁波银行借记卡', 'BANK', 'NINGBO',
       'ACTIVE', '宁波银行储蓄卡支付', NOW(), NOW()),

      -- 宁波银行信用卡
      (18, 'NINGBO_CREDIT', '宁波银行信用卡', 'CREDIT', 'NINGBO',
       'ACTIVE', '宁波银行信用卡支付', NOW(), NOW()),

      -- ========== 招商银行 ==========
      -- 招商银行借记卡
      (19, 'CMB_DEBIT', '招商银行借记卡', 'BANK', 'CMB',
       'ACTIVE', '招商银行储蓄卡支付', NOW(), NOW()),

      -- 招商银行信用卡
      (20, 'CMB_CREDIT', '招商银行信用卡', 'CREDIT', 'CMB',
       'ACTIVE', '招商银行信用卡支付', NOW(), NOW());


-- ============================================
-- 测试数据：fin_statement_mapping_rule 账单映射规则
-- 说明：用于配置各平台账单字段到内部模型的映射
-- ============================================
truncate  table fin_statement_mapping_rule;
INSERT INTO `fin_statement_mapping_rule` (
    `platform_code`, `source_type`, `source_pattern`,
    `target_type`, `target_value`, `priority`,
    `enabled`, `remark`, `created_by`, `created_at`, `updated_by`, `updated_at`, `is_deleted`
) VALUES
      -- ========== 京东平台：支付方式 -> account_ref ==========
      -- 微信支付 -> WECHAT_PAY
      ('JD', 'PAYMENT_METHOD', '微信支付',
       'ACCOUNT_REF', 'WECHAT_PAY', 100,
       1, '京东账单中支付方式为微信支付时，account_ref 归一为 WECHAT_PAY', 'system', NOW(), 'system', NOW(), 0),

      -- 余额 -> JD_BALANCE
      ('JD', 'PAYMENT_METHOD', '余额',
       'ACCOUNT_REF', 'JD_BALANCE', 90,
       1, '京东账单中支付方式为余额时，account_ref 归一为 JD_BALANCE', 'system', NOW(), 'system', NOW(), 0),

      -- 京东白条 -> JD_BAITIAO
      ('JD', 'PAYMENT_METHOD', '京东白条',
       'ACCOUNT_REF', 'JD_BAITIAO', 90,
       1, '京东账单中支付方式为京东白条时，account_ref 归一为 JD_BAITIAO', 'system', NOW(), 'system', NOW(), 0),

      -- 京东小金库 / 京东支付 -> JD_PAY
      ('JD', 'PAYMENT_METHOD', '京东小金库',
       'ACCOUNT_REF', 'JD_PAY', 80,
       1, '京东账单中支付方式为京东小金库时，account_ref 归一为 JD_PAY', 'system', NOW(), 'system', NOW(), 0);

select * from fin_statement_mapping_rule;

SELECT *
FROM fin_statement_mapping_rule
WHERE platform_code = 'JD'
  AND source_type = 'PAYMENT_METHOD'
  AND enabled = 1
  AND '京东白条' LIKE CONCAT('%', source_pattern, '%');