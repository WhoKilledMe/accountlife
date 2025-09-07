-- =============================================
-- 邮件配置表 (mail_config) DML 脚本
-- 作者: wensen.zhang
-- 版本: V1.0.0
-- 创建时间: 2025-01-01
-- =============================================

-- 1. 创建邮件配置表
CREATE TABLE IF NOT EXISTS mail_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID（关联用户表）',
    name VARCHAR(100) NOT NULL COMMENT '配置名称',
    host VARCHAR(255) NOT NULL COMMENT 'SMTP服务器地址',
    port INT NOT NULL COMMENT 'SMTP端口',
    email_address VARCHAR(255) NOT NULL COMMENT '邮箱账号',
    auth_code VARCHAR(255) NOT NULL COMMENT '邮箱授权码',
    enable_ssl BOOLEAN DEFAULT FALSE COMMENT '是否启用SSL',
    enable_tls BOOLEAN DEFAULT TRUE COMMENT '是否启用TLS',
    connection_timeout INT DEFAULT 30000 COMMENT '连接超时时间（毫秒）',
    read_timeout INT DEFAULT 30000 COMMENT '读取超时时间（毫秒）',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    description TEXT COMMENT '描述信息',
    created_by VARCHAR(50) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) COMMENT '修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_email_address (email_address),
    INDEX idx_is_active (is_active),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件配置表';

-- 2. 插入示例数据
INSERT INTO mail_config (
    user_id, name, host, port, email_address, auth_code, 
    enable_ssl, enable_tls, connection_timeout, read_timeout, 
    is_active, description, created_by, created_at, updated_by, updated_at, is_deleted
) VALUES 
-- Gmail 配置示例
(1, 'Gmail配置', 'smtp.gmail.com', 587, 'user@gmail.com', 'your_app_password', 
 TRUE, TRUE, 30000, 30000, TRUE, 'Gmail SMTP配置', 'system', NOW(), 'system', NOW(), 0),

-- QQ邮箱配置示例
(1, 'QQ邮箱配置', 'smtp.qq.com', 587, 'user@qq.com', 'your_auth_code', 
 TRUE, TRUE, 30000, 30000, TRUE, 'QQ邮箱SMTP配置', 'system', NOW(), 'system', NOW(), 0),

-- 163邮箱配置示例
(1, '163邮箱配置', 'smtp.163.com', 25, 'user@163.com', 'your_auth_code', 
 FALSE, TRUE, 30000, 30000, TRUE, '163邮箱SMTP配置', 'system', NOW(), 'system', NOW(), 0),

-- 企业邮箱配置示例
(2, '企业邮箱配置', 'smtp.exmail.qq.com', 587, 'user@company.com', 'your_auth_code', 
 TRUE, TRUE, 30000, 30000, TRUE, '企业邮箱SMTP配置', 'system', NOW(), 'system', NOW(), 0);

-- 3. 查询语句示例
-- 查询用户的所有邮件配置
-- SELECT * FROM mail_config WHERE user_id = ? AND is_deleted = 0;

-- 查询启用的邮件配置
-- SELECT * FROM mail_config WHERE is_active = TRUE AND is_deleted = 0;

-- 根据邮箱地址查询配置
-- SELECT * FROM mail_config WHERE email_address = ? AND is_deleted = 0;

-- 4. 更新语句示例
-- 更新邮件配置
-- UPDATE mail_config SET 
--     name = ?, host = ?, port = ?, email_address = ?, auth_code = ?,
--     enable_ssl = ?, enable_tls = ?, connection_timeout = ?, read_timeout = ?,
--     is_active = ?, description = ?, updated_by = ?, updated_at = NOW()
-- WHERE id = ? AND is_deleted = 0;

-- 启用/禁用邮件配置
-- UPDATE mail_config SET is_active = ?, updated_by = ?, updated_at = NOW() 
-- WHERE id = ? AND is_deleted = 0;

-- 5. 删除语句示例（逻辑删除）
-- DELETE FROM mail_config WHERE id = ? AND is_deleted = 0;
-- UPDATE mail_config SET is_deleted = 1, updated_by = ?, updated_at = NOW() WHERE id = ?;

-- 6. 常用查询视图
CREATE OR REPLACE VIEW v_mail_config_active AS
SELECT 
    id,
    user_id,
    name,
    host,
    port,
    email_address,
    enable_ssl,
    enable_tls,
    connection_timeout,
    read_timeout,
    is_active,
    description,
    created_by,
    created_at,
    updated_by,
    updated_at
FROM mail_config 
WHERE is_deleted = 0 AND is_active = TRUE;

-- 7. 数据验证约束
-- 添加邮箱格式验证（MySQL 8.0+）
-- ALTER TABLE mail_config ADD CONSTRAINT chk_email_format 
-- CHECK (email_address REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$');

-- 添加端口范围验证
-- ALTER TABLE mail_config ADD CONSTRAINT chk_port_range 
-- CHECK (port > 0 AND port <= 65535);

-- 添加超时时间验证
-- ALTER TABLE mail_config ADD CONSTRAINT chk_timeout_positive 
-- CHECK (connection_timeout > 0 AND read_timeout > 0);

-- 8. 清理脚本
-- 物理删除已逻辑删除超过30天的记录
-- DELETE FROM mail_config WHERE is_deleted = 1 AND updated_at < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 9. 统计查询
-- 统计每个用户的邮件配置数量
-- SELECT user_id, COUNT(*) as config_count 
-- FROM mail_config 
-- WHERE is_deleted = 0 
-- GROUP BY user_id;

-- 统计启用的邮件配置数量
-- SELECT COUNT(*) as active_config_count 
-- FROM mail_config 
-- WHERE is_deleted = 0 AND is_active = TRUE;
