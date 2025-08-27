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
    INDEX idx_user_id (user_id)
) COMMENT='分类关键词映射表：支持用户自定义关键词与分类ID映射';
