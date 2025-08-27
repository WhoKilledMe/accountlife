-- 完整的数据库初始化脚本（破坏性重建）
-- 会删除并重建相关表结构，然后插入基础数据与系统分类（含父子层级）
-- 请在非生产库执行
-- ========================================
-- 第一部分：删除并创建必要的表结构
-- ========================================

-- 先按外键顺序删除（若存在）
DROP TABLE IF EXISTS `account_transaction`;
DROP TABLE IF EXISTS `transaction_category`;
DROP TABLE IF EXISTS `asset_account`;
DROP TABLE IF EXISTS `user_group_member`;
DROP TABLE IF EXISTS `user_group`;
DROP TABLE IF EXISTS `user`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `password_hash` VARCHAR(255) COMMENT '密码哈希',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `created_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` VARCHAR(50) DEFAULT 'system' COMMENT '修改人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）'
) COMMENT='用户表';

-- 用户组表
CREATE TABLE IF NOT EXISTS `user_group` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户组ID',
    `name` VARCHAR(100) NOT NULL COMMENT '组名称',
    `description` VARCHAR(255) COMMENT '组描述',
    `created_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` VARCHAR(50) DEFAULT 'system' COMMENT '修改人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）'
) COMMENT='用户组表';

-- 用户组成员表
CREATE TABLE IF NOT EXISTS `user_group_member` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    `group_id` BIGINT NOT NULL COMMENT '用户组ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` TINYINT NOT NULL COMMENT '角色：1-owner组长，2-member成员',
    `joined_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `created_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` VARCHAR(50) DEFAULT 'system' COMMENT '修改人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    FOREIGN KEY (`group_id`) REFERENCES `user_group`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) COMMENT='用户组成员关系表';

-- 资产账户表
CREATE TABLE IF NOT EXISTS `asset_account` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账户ID',
    `user_id` BIGINT NOT NULL COMMENT '所属用户',
    `name` VARCHAR(100) COMMENT '账户名称（如 招商银行、花呗）',
    `type` TINYINT NOT NULL COMMENT '账户类型：1-bank，2-platform，3-credit_wallet，4-wallet',
    `platform_code` VARCHAR(50) COMMENT '平台标识（如 ALIPAY、MEITUAN）',
    `account_number` VARCHAR(100) COMMENT '银行卡号/平台账号',
    `balance` DECIMAL(18, 2) DEFAULT 0.00 COMMENT '账户余额',
    `is_virtual` BOOLEAN DEFAULT FALSE COMMENT '是否为虚拟账户',
    `credit_limit` DECIMAL(18, 2) DEFAULT NULL COMMENT '信用额度，仅信用钱包用',
    `currency` VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    `description` VARCHAR(255) COMMENT '账户描述',
    `created_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` VARCHAR(50) DEFAULT 'system' COMMENT '修改人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) COMMENT='资产账户表';

-- 交易分类表
CREATE TABLE IF NOT EXISTS `transaction_category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称（如 餐饮）',
    `type` TINYINT NOT NULL COMMENT '分类类型：1-income，2-expense，3-transfer_out，4-transfer_in',
    `parent_id` BIGINT COMMENT '父分类ID',
    `icon` VARCHAR(50) COMMENT '图标',
    `user_id` BIGINT COMMENT '所属用户，null 表示系统分类',
    `sort_order` INT DEFAULT 0 COMMENT '排序值',
    `created_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` VARCHAR(50) DEFAULT 'system' COMMENT '修改人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    FOREIGN KEY (`parent_id`) REFERENCES `transaction_category`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) COMMENT='交易分类表';

-- 账户交易明细表
CREATE TABLE IF NOT EXISTS `account_transaction` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '交易ID',
    `user_id` BIGINT NOT NULL COMMENT '所属用户',
    `account_id` BIGINT NOT NULL COMMENT '发生账户',
    `type` TINYINT NOT NULL COMMENT '交易类型：1-income，2-expense，3-transfer_out，4-transfer_in',
    `amount` DECIMAL(18, 2) NOT NULL COMMENT '交易金额',
    `category_id` BIGINT COMMENT '分类ID',
    `related_transaction_id` BIGINT COMMENT '关联交易ID',
    `description` VARCHAR(255) COMMENT '摘要说明',
    `transaction_time` DATETIME COMMENT '实际发生时间',
    `source_type` TINYINT COMMENT '来源类型：0-宁波银行信用卡，1-LabelDetail账单，2-银行，3-平台，4-信用钱包',
    `source_ref` VARCHAR(100) COMMENT '原始账单唯一标识',
    `statement_id` BIGINT COMMENT '账单归属ID',
    `created_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` VARCHAR(50) DEFAULT 'system' COMMENT '修改人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`account_id`) REFERENCES `asset_account`(`id`),
    FOREIGN KEY (`category_id`) REFERENCES `transaction_category`(`id`),
    FOREIGN KEY (`related_transaction_id`) REFERENCES `account_transaction`(`id`)
) COMMENT='账户交易明细表';

-- ========================================
-- 第二部分：插入基础用户数据
-- ========================================

-- 插入测试用户
INSERT INTO `user` (`username`, `email`, `phone`, `status`, `created_by`, `updated_by`) VALUES
('admin', 'admin@accountlife.com', '13800138000', 1, 'system', 'system'),
('test_user', 'test@accountlife.com', '13800138001', 1, 'system', 'system')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

INSERT INTO `user` (`username`, `email`, `phone`, `status`, `created_by`, `updated_by`) VALUES
('wensen.zhang', '772965502@qq.com', '18336968029', 1, 'system', 'system')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;
-- 插入用户组
INSERT INTO `user_group` (`name`, `description`, `created_by`, `updated_by`) VALUES
('张文森家庭组', '张文森家庭组', 'system', 'system')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

-- 插入用户组成员关系
INSERT INTO `user_group_member` (`user_id`, `group_id`, `role`, `created_by`, `updated_by`) VALUES
(1, 1, 1, 'system', 'system'),
(2, 2, 1, 'system', 'system'),
(3, 3, 1, 'system', 'system')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

-- ========================================
-- 第三部分：插入交易分类数据
-- ========================================

-- 清空现有系统分类数据（可选）
-- DELETE FROM transaction_category WHERE user_id IS NULL;

-- 插入收入类交易分类 (type = 1)
INSERT INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `user_id`, `sort_order`, `created_by`, `updated_by`) VALUES
('工资薪金', 1, NULL, '💰', NULL, 1, 'system', 'system'),
('奖金补贴', 1, NULL, '🎁', NULL, 2, 'system', 'system'),
('投资收益', 1, NULL, '📈', NULL, 3, 'system', 'system'),
('利息收入', 1, NULL, '💹', NULL, 4, 'system', 'system'),
('经营收入', 1, NULL, '🏢', NULL, 5, 'system', 'system'),
('租金收入', 1, NULL, '🏠', NULL, 6, 'system', 'system'),
('版税收入', 1, NULL, '📚', NULL, 7, 'system', 'system'),
('佣金收入', 1, NULL, '🤝', NULL, 8, 'system', 'system'),
('退款返现', 1, NULL, '↩️', NULL, 9, 'system', 'system'),
('赔偿收入', 1, NULL, '⚖️', NULL, 10, 'system', 'system'),
('捐赠收入', 1, NULL, '🙏', NULL, 11, 'system', 'system'),
('礼金红包', 1, NULL, '🧧', NULL, 12, 'system', 'system'),
('中奖收入', 1, NULL, '🎰', NULL, 13, 'system', 'system'),
('兼职收入', 1, NULL, '💼', NULL, 14, 'system', 'system'),
('其他收入', 1, NULL, '💵', NULL, 15, 'system', 'system')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

-- 插入支出类交易分类 (type = 2)
INSERT INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `user_id`, `sort_order`, `created_by`, `updated_by`) VALUES
('餐饮美食', 2, NULL, '🍽️', NULL, 16, 'system', 'system'),
('交通出行', 2, NULL, '🚗', NULL, 17, 'system', 'system'),
('购物消费', 2, NULL, '🛒', NULL, 18, 'system', 'system'),
('医疗健康', 2, NULL, '🏥', NULL, 19, 'system', 'system'),
('教育培训', 2, NULL, '📚', NULL, 20, 'system', 'system'),
('娱乐休闲', 2, NULL, '🎮', NULL, 21, 'system', 'system'),
('住房生活', 2, NULL, '🏠', NULL, 22, 'system', 'system'),
('投资理财', 2, NULL, '📊', NULL, 23, 'system', 'system'),
('保险保障', 2, NULL, '🛡️', NULL, 24, 'system', 'system'),
('税费支出', 2, NULL, '🧾', NULL, 25, 'system', 'system'),
('慈善捐赠', 2, NULL, '❤️', NULL, 26, 'system', 'system'),
('旅游度假', 2, NULL, '✈️', NULL, 27, 'system', 'system'),
('美容美体', 2, NULL, '💄', NULL, 28, 'system', 'system'),
('健身运动', 2, NULL, '🏃', NULL, 29, 'system', 'system'),
('宠物护理', 2, NULL, '🐕', NULL, 30, 'system', 'system'),
('子女教育', 2, NULL, '👶', NULL, 31, 'system', 'system'),
('赡养老人', 2, NULL, '👴', NULL, 32, 'system', 'system'),
('数码科技', 2, NULL, '💻', NULL, 33, 'system', 'system'),
('家具家居', 2, NULL, '🪑', NULL, 34, 'system', 'system'),
('服装配饰', 2, NULL, '👕', NULL, 35, 'system', 'system'),
('化妆品', 2, NULL, '💋', NULL, 36, 'system', 'system'),
('图书文具', 2, NULL, '📖', NULL, 37, 'system', 'system'),
('运动户外', 2, NULL, '⛷️', NULL, 38, 'system', 'system'),
('游戏娱乐', 2, NULL, '🎲', NULL, 39, 'system', 'system'),
('订阅服务', 2, NULL, '📱', NULL, 40, 'system', 'system'),
('水电煤气', 2, NULL, '💡', NULL, 41, 'system', 'system'),
('维修保养', 2, NULL, '🔧', NULL, 42, 'system', 'system'),
('贷款还款', 2, NULL, '🏦', NULL, 43, 'system', 'system'),
('信用卡还款', 2, NULL, '💳', NULL, 44, 'system', 'system'),
('账单支付', 2, NULL, '📄', NULL, 45, 'system', 'system'),
('充值', 2, NULL, '➕', NULL, 46, 'system', 'system'),
('提现', 2, NULL, '➖', NULL, 47, 'system', 'system'),
('货币兑换', 2, NULL, '💱', NULL, 48, 'system', 'system'),
('手续费', 2, NULL, '💸', NULL, 49, 'system', 'system'),
('罚金', 2, NULL, '⚠️', NULL, 50, 'system', 'system'),
('其他支出', 2, NULL, '💸', NULL, 51, 'system', 'system')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

-- 插入转账类交易分类 (type = 3 和 4)
INSERT INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `user_id`, `sort_order`, `created_by`, `updated_by`) VALUES
('账户转账', 3, NULL, '🔄', NULL, 52, 'system', 'system'),
('卡片转账', 3, NULL, '💳', NULL, 53, 'system', 'system'),
('平台转账', 3, NULL, '🌐', NULL, 54, 'system', 'system'),
('跨行转账', 3, NULL, '🏛️', NULL, 55, 'system', 'system'),
('国际转账', 3, NULL, '🌍', NULL, 56, 'system', 'system'),
('转入', 4, NULL, '⬇️', NULL, 57, 'system', 'system')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

-- ========================================
-- 第四部分：插入测试账户数据
-- ========================================

-- 插入测试账户
INSERT INTO `asset_account` (`user_id`, `name`, `type`, `platform_code`, `balance`, `currency`, `description`, `created_by`, `updated_by`) VALUES
(1, '工商银行储蓄卡', 1, 'ICBC', 10000.00, 'CNY', '主要储蓄账户', 'system', 'system'),
(1, '支付宝余额', 2, 'ALIPAY', 5000.00, 'CNY', '日常消费账户', 'system', 'system'),
(1, '微信钱包', 2, 'WECHAT', 2000.00, 'CNY', '零钱账户', 'system', 'system'),
(2, '建设银行储蓄卡', 1, 'CCB', 8000.00, 'CNY', '储蓄账户', 'system', 'system')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

INSERT INTO `asset_account` (`user_id`, `name`, `type`, `platform_code`, `balance`, `currency`, `description`, `created_by`, `updated_by`) VALUES
    (3, '宁波信用卡', 3, 'NINGBO', 0, 'CNY', '信用卡', 'system', 'system')

ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;
                                                                                                                                               -- ========================================
-- 第五部分：验证数据插入结果
-- ========================================

-- 显示插入结果统计
SELECT '数据插入完成！' as message;

-- 统计各类别数量
SELECT 
    CASE type 
        WHEN 1 THEN '收入分类'
        WHEN 2 THEN '支出分类'
        WHEN 3 THEN '转出分类'
        WHEN 4 THEN '转入分类'
    END as category_group,
    COUNT(*) as count
FROM transaction_category 
WHERE user_id IS NULL
GROUP BY type
ORDER BY type;

-- 显示所有分类数据
SELECT 
    id,
    name,
    CASE type 
        WHEN 1 THEN '收入'
        WHEN 2 THEN '支出'
        WHEN 3 THEN '转出'
        WHEN 4 THEN '转入'
        ELSE '未知'
    END as type_name,
    icon,
    sort_order,
    created_at
FROM transaction_category 
WHERE user_id IS NULL
ORDER BY type, sort_order;

-- 显示用户和账户信息
SELECT 
    u.username,
    u.email,
    u.phone,
    COUNT(a.id) as account_count
FROM `user` u
LEFT JOIN asset_account a ON u.id = a.user_id
WHERE u.is_deleted = 0
GROUP BY u.id, u.username, u.email, u.phone;

select * from transaction_category;

-- 在现有脚本末尾添加分类关键词映射表
-- 分类关键词映射表（修正版）
DROP TABLE IF EXISTS category_keyword_mapping;
CREATE TABLE IF NOT EXISTS category_keyword_mapping (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL COMMENT '关联transaction_category.id',
    keyword VARCHAR(200) NOT NULL COMMENT '关键词',
    weight INT DEFAULT 1 COMMENT '权重：1-普通，2-重要，3-核心',
    user_id INT NULL COMMENT '所属用户，null表示系统默认',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_category_id (category_id),
    INDEX idx_keyword (keyword),
    INDEX idx_weight (weight),
    INDEX idx_active (is_active),
    INDEX idx_user_id (user_id),
    UNIQUE KEY uk_user_category_keyword (COALESCE(user_id,0), category_id, keyword)
) COMMENT='分类关键词映射表：支持用户自定义关键词与分类ID映射';

-- 基础示例数据（完整500+将独立文件导入）
INSERT IGNORE INTO category_keyword_mapping (category_id, keyword, weight, user_id) VALUES
(16, '餐饮', 3, NULL), (16, '外卖', 3, NULL), (16, '奶茶', 2, NULL), (16, '咖啡', 2, NULL), (16, '星巴克', 2, NULL),
(17, '地铁', 3, NULL), (17, '公交', 3, NULL), (17, '打车', 3, NULL), (17, '网约车', 3, NULL), (17, '加油', 3, NULL),
(18, '购物', 3, NULL), (18, '超市', 3, NULL), (18, '京东', 3, NULL), (18, '淘宝', 3, NULL), (18, '拼多多', 3, NULL),
(1,  '工资', 3, NULL), (1,  '薪水', 3, NULL), (1,  '月薪', 3, NULL), (1,  '年终奖', 3, NULL);