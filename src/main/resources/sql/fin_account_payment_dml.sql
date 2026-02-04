-- ============================================
-- 账户和支付方式 DML 语句
-- 生成日期: 2026-01-21
-- 说明: 插入京东、招商银行、农业银行相关账户和支付方式
-- ============================================

-- ============================================
-- 1. 插入账户数据 (fin_account)
-- ============================================
-- 注意: 请根据实际情况修改 user_id 和 external_account_ref 字段值

INSERT INTO `fin_account` (
    `user_id`, `account_code`, `account_name`, `account_category`, `account_type`,
    `owner_type`, `platform_code`, `currency`, `balance`, `balance_updated_at`,
    `external_account_ref`, `is_virtual`, `status`, `remark`,
    `created_by`, `created_at`, `updated_by`, `updated_at`, `is_deleted`
) VALUES
    -- 京东白条（信用账户）
    (3, 'JD-3-1', '京东白条', 'CASH', 'CREDIT', 'USER', 'JD', 'CNY', 0.00,
     NULL, 'jd_baitiao_001', 0, 'ACTIVE', '京东白条信用账户',
     'system', NOW(), 'system', NOW(), 0),

    -- 京东余额（电子钱包）
    (3, 'JD-3-2', '京东余额', 'CASH', 'E_WALLET', 'USER', 'JD', 'CNY', 0.00,
     NULL, 'jd_balance_001', 0, 'ACTIVE', '京东钱包余额账户',
     'system', NOW(), 'system', NOW(), 0),

    -- 京东支付（平台账户，用于记账）
    (3, 'JD-3-3', '京东支付', 'CASH', 'E_WALLET', 'PLATFORM', 'JD', 'CNY', 0.00,
     NULL, 'jd_pay_001', 0, 'ACTIVE', '京东支付平台账户',
     'system', NOW(), 'system', NOW(), 0),

    -- 招商银行账户（借记卡）
    (3, 'CMB-3-1', '招商银行账户', 'CASH', 'BANK_CARD', 'USER', 'CMB', 'CNY', 0.00,
     NULL, 'cmb_debit_001', 0, 'ACTIVE', '招商银行借记卡账户',
     'system', NOW(), 'system', NOW(), 0),

    -- 招商银行信用卡账户
    (3, 'CMB-3-2', '招商银行信用卡', 'CASH', 'CREDIT', 'USER', 'CMB', 'CNY', 0.00,
     NULL, 'cmb_credit_001', 0, 'ACTIVE', '招商银行信用卡账户',
     'system', NOW(), 'system', NOW(), 0),

    -- 农业银行账户（借记卡）
    (3, 'ABC-3-1', '农业银行账户', 'CASH', 'BANK_CARD', 'USER', 'ABC', 'CNY', 0.00,
     NULL, 'abc_debit_001', 0, 'ACTIVE', '农业银行借记卡账户',
     'system', NOW(), 'system', NOW(), 0);


-- ============================================
-- 2. 插入支付方式数据 (fin_payment_method)
-- ============================================

INSERT INTO `fin_payment_method` (
    `payment_code`, `payment_name`, `payment_type`, `platform_code`,
    `status`, `remark`, `created_by`, `created_at`, `updated_by`, `updated_at`, `is_deleted`
) VALUES
    -- 京东白条（信用支付）
    ('JD_BAITIAO', '京东白条', 'CREDIT', 'JD',
     'ACTIVE', '京东白条信用支付', 'system', NOW(), 'system', NOW(), 0),

    -- 京东余额（余额支付）
    ('JD_BALANCE', '京东余额', 'BALANCE', 'JD',
     'ACTIVE', '京东钱包余额支付', 'system', NOW(), 'system', NOW(), 0),

    -- 京东支付（平台支付）
    ('JD_PAY', '京东支付', 'PLATFORM', 'JD',
     'ACTIVE', '京东聚合支付，支持多种支付方式', 'system', NOW(), 'system', NOW(), 0),

    -- 招商银行信用卡支付
    ('CMB_CREDIT', '招商银行信用卡支付', 'CREDIT', 'CMB',
     'ACTIVE', '招商银行信用卡支付', 'system', NOW(), 'system', NOW(), 0),

    -- 招商银行支付（借记卡）
    ('CMB_DEBIT', '招商银行支付', 'BANK', 'CMB',
     'ACTIVE', '招商银行借记卡支付', 'system', NOW(), 'system', NOW(), 0),

    -- 农业银行支付（借记卡）
    ('ABC_DEBIT', '农业银行支付', 'BANK', 'ABC',
     'ACTIVE', '农业银行借记卡支付', 'system', NOW(), 'system', NOW(), 0);


-- ============================================
-- 3. 如果需要，先确保平台数据存在 (fin_platform)
-- ============================================
-- 如果平台表中还没有京东和农业银行的记录，请先执行以下语句

-- 插入京东平台（如果不存在）
INSERT INTO `fin_platform` (
    `platform_code`, `platform_name`, `platform_type`, `support_payment`, `support_credit`, 
    `support_balance`, `support_bill`, `bill_import_format`, `bill_email_domain`, 
    `logo_url`, `website_url`, `api_config`, `description`, `sort_order`, `status`,
    `created_by`, `created_at`, `updated_by`, `updated_at`, `is_deleted`
)
SELECT 'JD', '京东', 'PLATFORM', 1, 1, 1, 1, 'CSV', '', 
       NULL, 'https://www.jd.com', NULL, '京东平台，支持京东支付、京东白条等', 2, 'ACTIVE',
       'system', NOW(), 'system', NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `fin_platform` WHERE `platform_code` = 'JD');

-- 插入农业银行平台（如果不存在）
INSERT INTO `fin_platform` (
    `platform_code`, `platform_name`, `platform_type`, `support_payment`, `support_credit`, 
    `support_balance`, `support_bill`, `bill_import_format`, `bill_email_domain`, 
    `logo_url`, `website_url`, `api_config`, `description`, `sort_order`, `status`,
    `created_by`, `created_at`, `updated_by`, `updated_at`, `is_deleted`
)
SELECT 'ABC', '农业银行', 'BANK', 1, 1, 1, 1, 'CSV', '', 
       NULL, 'https://www.abchina.com', NULL, '中国农业银行', 3, 'ACTIVE',
       'system', NOW(), 'system', NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `fin_platform` WHERE `platform_code` = 'ABC');


-- ============================================
-- 使用说明:
-- ============================================
-- 1. user_id 已设置为 3
-- 2. 请根据实际情况修改 external_account_ref 字段值（当前为示例值）
-- 3. account_code 格式为 {PLATFORM}-{userId}-{seq}，请确保唯一性（如：JD-3-1, CMB-3-1）
-- 4. 如果账户已存在，请先删除或更新后再插入
-- 5. 支付方式的 payment_code 必须唯一


select * from fin_payment_method;
select * from fin_account;
select * from fin_platform;

INSERT INTO `fin_payment_method` (
    `payment_code`, `payment_name`, `payment_type`, `platform_code`,
    `status`, `remark`, `created_by`, `created_at`, `updated_by`, `updated_at`, `is_deleted`
) VALUES
-- 招商银行信用卡支付
('CMB_CREDIT', '招商银行信用卡支付', 'CREDIT', 'CMB',
    'ACTIVE', '招商银行信用卡支付', 'system', NOW(), 'system', NOW(), 0),

-- 招商银行支付（借记卡）
('CMB_DEBIT', '招商银行支付', 'BANK', 'CMB',
    'ACTIVE', '招商银行借记卡支付', 'system', NOW(), 'system', NOW(), 0),

-- 农业银行支付（借记卡）
('ABC_DEBIT', '农业银行支付', 'BANK', 'ABC',
    'ACTIVE', '农业银行借记卡支付', 'system', NOW(), 'system', NOW(), 0);

