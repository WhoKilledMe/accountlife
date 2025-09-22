-- 为 asset_account 表添加账单邮箱字段
-- 执行时间: 2025-01-XX

ALTER TABLE user_account
ADD COLUMN `bill_email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账单邮箱地址' 
AFTER `currency`;
ALTER TABLE user_account
    ADD COLUMN `parent_id` bigint DEFAULT NULL COMMENT '父节点'
        AFTER `user_id`;
-- 为现有账户设置默认账单邮箱（根据平台代码匹配）
UPDATE user_account aa
JOIN `account_config` ac ON aa.platform_code = ac.platform_code 
SET aa.bill_email = ac.bill_email 
WHERE ac.bill_email IS NOT NULL;

-- 查看更新结果
SELECT aa.id, aa.name, aa.platform_code, aa.bill_email, ac.name as config_name, ac.bill_email as config_email
FROM user_account aa
LEFT JOIN `account_config` ac ON aa.platform_code = ac.platform_code;
