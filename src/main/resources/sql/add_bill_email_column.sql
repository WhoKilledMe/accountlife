-- 为 account_config 表添加账单邮箱字段
-- 执行时间: 2025-01-XX

ALTER TABLE `account_config` 
ADD COLUMN `bill_email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账单邮箱地址' 
AFTER `is_active`;

-- 为常用平台设置官方账单邮箱地址
UPDATE `account_config` SET `bill_email` = 'it_paybill@meituan.com' WHERE `platform_code` = 'MEITUAN';
UPDATE `account_config` SET `bill_email` = 'service@mail.alipay.com' WHERE `platform_code` = 'ALIPAY';
UPDATE `account_config` SET `bill_email` = 'personalservice@bank-of-china.com' WHERE `platform_code` = 'BOC';
UPDATE `account_config` SET `bill_email` = 'creditcardcenter@cardmail.psbcltd.cn' WHERE `platform_code` = 'PSBC';

-- 其他平台暂无公开的官方账单邮箱，可根据实际邮件来源补充
-- 微信支付：通常通过微信客户端查看，无专门邮箱
-- 京东：账单邮件由官方邮箱发送，具体地址需查看实际邮件
-- 滴滴、携程、去哪儿等：账单邮件由官方邮箱发送，具体地址需查看实际邮件

select * from account_config;

ALTER TABLE business_transaction
    ADD COLUMN discount_amount DECIMAL(18,2)  DEFAULT 0 COMMENT '业务折扣/优惠金额' after  amount;


ALTER TABLE account_transaction
    ADD COLUMN discount_amount DECIMAL(18,2)  DEFAULT 0 COMMENT '业务折扣/优惠金额' after  amount;


ALTER TABLE account_transaction
    ADD COLUMN external_ DECIMAL(18,2)  DEFAULT 0 COMMENT '业务折扣/优惠金额' after  amount;

