-- ============================================
-- AccountLife 财务系统重构 - DDL
-- 版本: 2.0
-- 日期: 2026-01-21
-- 描述: 多层分离架构，支撑采账单→建账户→对账归并→资金图谱
-- ============================================

-- --------------------------------------------
-- 1. 账户主表：描述资金容器
-- --------------------------------------------
DROP TABLE IF EXISTS fin_account;
CREATE TABLE IF NOT EXISTS fin_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账户ID（主键）',
    user_id BIGINT NOT NULL COMMENT '所属用户ID（系统用户表 fk）',
    account_code VARCHAR(64) NOT NULL UNIQUE COMMENT '系统内部唯一编码（格式：ACCT-{userId}-{seq}）',
    account_name VARCHAR(128) NOT NULL COMMENT '账户可读名，如：宁波信用卡(尾号1234)、微信余额',
    account_category VARCHAR(32) NOT NULL COMMENT '账户大类：CASH/CLEARING/INVEST/BENEFIT/TRANSIT/LEDGER',
    account_type VARCHAR(32) NOT NULL COMMENT '细分类：BANK_CARD/E_WALLET/CREDIT/BROKER_FUND/FUND_TA/STOCK_POSITION/COUPON/SUBSIDY',
    owner_type VARCHAR(16) DEFAULT 'USER' COMMENT '账户所有者类型：USER/PLATFORM/MERCHANT/SYSTEM',
    platform_code VARCHAR(64) COMMENT '外部机构/平台代码：WECHAT/ALIPAY/MEITUAN/CMB/TONGHUASHUN 等',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    balance DECIMAL(18,2) DEFAULT 0.00 COMMENT '当前余额（冗余字段，由流水同步）',
    balance_updated_at DATETIME COMMENT '余额最后更新时间',
    external_account_ref VARCHAR(128) COMMENT '外部账户标识（卡号尾号/账号/OpenID等，用于去重）',
    is_virtual TINYINT(1) DEFAULT 0 COMMENT '是否虚拟账户（1=虚拟，如补贴池；0=真实银行/券商）',
    status VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE/FROZEN',
    remark VARCHAR(255) COMMENT '备注字段',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    INDEX idx_user_id (user_id),
    INDEX idx_platform_code (platform_code),
    INDEX idx_account_category (account_category),
    UNIQUE KEY uk_user_platform_ref (user_id, platform_code, external_account_ref)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户主表：描述资金容器（银行/钱包/券商/补贴等）';


-- --------------------------------------------
-- 1.1. 平台字典表：统一管理平台信息
-- --------------------------------------------
DROP TABLE IF EXISTS fin_platform;
CREATE TABLE IF NOT EXISTS fin_platform (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '平台ID（主键）',
    platform_code VARCHAR(64) NOT NULL UNIQUE COMMENT '平台唯一编码：WECHAT/ALIPAY/MEITUAN/CMB/ICBC/CCB/NINGBO/TIKTOK/TONGHUASHUN 等',
    platform_name VARCHAR(128) NOT NULL COMMENT '平台名称：微信、支付宝、美团、招商银行等',
    platform_type VARCHAR(32) NOT NULL COMMENT '平台类型：PAYMENT/MERCHANT/BANK/BROKER/E_WALLET/INVESTMENT/OTHER',
    support_payment TINYINT(1) DEFAULT 0 COMMENT '是否提供支付能力（1-支持，0-不支持）',
    support_credit TINYINT(1) DEFAULT 0 COMMENT '是否提供信用能力（花呗/月付/信用卡，1-支持，0-不支持）',
    support_balance TINYINT(1) DEFAULT 0 COMMENT '是否提供余额账户（1-支持，0-不支持）',
    support_bill TINYINT(1) DEFAULT 1 COMMENT '是否支持账单导出/导入（1-支持，0-不支持）',
    bill_import_format VARCHAR(32) COMMENT '支持的账单格式：CSV/JSON/PDF/EMAIL/XLSX',
    bill_email_domain VARCHAR(128) COMMENT '账单邮箱域名（用于自动识别，如 bill@alipay.com）',
    logo_url VARCHAR(255) COMMENT '平台Logo地址（用于UI展示）',
    website_url VARCHAR(255) COMMENT '平台官网地址',
    api_config JSON COMMENT 'API配置信息（JSON格式，如接口地址、认证方式等，备用）',
    description VARCHAR(500) COMMENT '平台描述信息',
    sort_order INT DEFAULT 0 COMMENT '排序权重（数值越大越靠前）',
    status VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    INDEX idx_platform_code (platform_code),
    INDEX idx_platform_type (platform_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台字典表：统一管理所有平台/机构的基础信息及能力';


-- --------------------------------------------
-- 2. 账户扩展表：机构特有字段
-- --------------------------------------------
DROP TABLE IF EXISTS fin_account_ext;
CREATE TABLE IF NOT EXISTS fin_account_ext (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '扩展ID',
    account_id BIGINT NOT NULL COMMENT 'fin_account.id',
    json_ext JSON COMMENT '银行/券商等扩展信息（卡号掩码、账单日、开户行、授信额度等）',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    INDEX idx_account_id (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户扩展信息，按 account_id 一对一或一对多存放';


-- --------------------------------------------
-- 3. 账户余额快照表：用于历史/报表
-- --------------------------------------------
DROP TABLE IF EXISTS fin_account_balance_snapshot;
CREATE TABLE IF NOT EXISTS fin_account_balance_snapshot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '快照ID',
    account_id BIGINT NOT NULL COMMENT 'fin_account.id',
    snapshot_date DATE NOT NULL COMMENT '快照日期（按日）',
    balance DECIMAL(18,2) NOT NULL COMMENT '当天结束时的余额快照',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    UNIQUE KEY uk_account_snapshot (account_id, snapshot_date),
    INDEX idx_snapshot_date (snapshot_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户日度余额快照表，用于报表/性能优化';


-- --------------------------------------------
-- 4. 支付方式表：抽象支付能力
-- --------------------------------------------
DROP TABLE IF EXISTS fin_payment_method;
CREATE TABLE IF NOT EXISTS fin_payment_method (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付方式ID',
    payment_code VARCHAR(64) NOT NULL UNIQUE COMMENT '支付方式代码（WECHAT_PAY/ALIPAY_PAY/BANK_DEBIT/CREDIT_PAY/MEITUAN_MONTH）',
    payment_name VARCHAR(128) NOT NULL COMMENT '人类可读名称，如：微信支付（聚合）',
    payment_type VARCHAR(32) NOT NULL COMMENT '支付类别：PLATFORM/BALANCE/CREDIT/BANK/MIXED/SUBSIDY',
    platform_code VARCHAR(64) COMMENT '归属平台：WECHAT/ALIPAY/MEITUAN/TIKTOK/NINGBO_BANK/xxx',
    status VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE',
    remark VARCHAR(255) COMMENT '备注',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    INDEX idx_platform_code (platform_code),
    INDEX idx_payment_type (payment_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付方式能力表（抽象支付通道/能力）';


-- --------------------------------------------
-- 5. 支付方式与账户绑定表
-- --------------------------------------------
DROP TABLE IF EXISTS fin_payment_binding;
CREATE TABLE IF NOT EXISTS fin_payment_binding (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '绑定ID',
    payment_method_id BIGINT NOT NULL COMMENT 'fin_payment_method.id',
    account_id BIGINT NOT NULL COMMENT 'fin_account.id',
    priority INT DEFAULT 0 COMMENT '路由优先级，数值越大优先',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    UNIQUE KEY uk_payment_account (payment_method_id, account_id),
    INDEX idx_account_id (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付能力与具体账户的绑定关系（路由表）';

-- --------------------------------------------
-- 6. 交易中枢表：业务事实
-- --------------------------------------------
DROP TABLE IF EXISTS fin_transaction;
CREATE TABLE IF NOT EXISTS fin_transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '交易ID（业务中枢）',
    user_id BIGINT NOT NULL COMMENT '所属用户',
    transaction_no VARCHAR(64) NOT NULL UNIQUE COMMENT '系统交易号/业务流水号',
    biz_type VARCHAR(32) NOT NULL COMMENT '业务类型：PAY/TRANSFER/INVEST/REDEEM/REPAY/REFUND',
    biz_sub_type VARCHAR(64) COMMENT '子类型，如：CREDIT_PAY/BALANCE_PAY/FUND_SUBSCRIBE/STOCK_BUY',
    category_id BIGINT COMMENT '交易分类ID（关联 transaction_category）',
    trade_time DATETIME NOT NULL COMMENT '交易发生时间（业务时间）',
    amount DECIMAL(18,2) NOT NULL COMMENT '业务金额（通常为总额）',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    status VARCHAR(32) DEFAULT 'INIT' COMMENT '业务状态：INIT/MATCHED/CONFIRMED/CANCELLED',
    counterparty VARCHAR(128) COMMENT '交易对手或商户名称（美团/拼多多/同花顺）',
    platform_code VARCHAR(64) COMMENT '来源平台：MEITUAN/WECHAT/ALIPAY/TIKTOK/NINGBO_BANK',
    original_transaction_id BIGINT COMMENT '原交易ID（退款时指向原单）',
    remark VARCHAR(255) COMMENT '备注/摘要',
    created_by VARCHAR(64) COMMENT '创建人（系统/解析器/人工）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    INDEX idx_user_id (user_id),
    INDEX idx_user_time (user_id, trade_time),
    INDEX idx_platform_code (platform_code),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易中枢表：表示一笔业务事实（消费/投资/还款等）';


-- --------------------------------------------
-- 7. 账单文件导入日志
-- --------------------------------------------
DROP TABLE IF EXISTS fin_statement_file;
CREATE TABLE IF NOT EXISTS fin_statement_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件导入记录ID',
    user_id BIGINT COMMENT '所属用户（可为空表示系统/公共文件）',
    platform_code VARCHAR(64) COMMENT '文件来源平台：WECHAT/ALIPAY/CMB/MEITUAN/TIKTOK/EXTERNAL',
    file_name VARCHAR(255) COMMENT '上传文件名',
    file_type VARCHAR(32) COMMENT '文件类型：CSV/ZIP/JSON/PDF/EMAIL_ATTACHMENT',
    file_md5 CHAR(32) COMMENT '文件 md5（用于幂等）',
    total_rows INT DEFAULT 0 COMMENT '总行数（估计）',
    success_count INT DEFAULT 0 COMMENT '成功解析行数',
    failure_count INT DEFAULT 0 COMMENT '失败行数',
    status VARCHAR(32) DEFAULT 'PENDING' COMMENT 'PENDING/PROCESSING/COMPLETED/FAILED',
    source_channel VARCHAR(32) COMMENT '导入渠道：FILE_UPLOAD/EMAIL/API/FTP/THIRD_PARTY',
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    processed_at DATETIME COMMENT '处理完成时间',
    error_log TEXT COMMENT '解析错误日志（如解析失败原因）',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    INDEX idx_user_id (user_id),
    INDEX idx_platform_code (platform_code),
    INDEX idx_status (status),
    INDEX idx_file_md5 (file_md5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单文件导入日志：记录文件来源及处理状态';


-- --------------------------------------------
-- 8. 账单行表：原始行证据
-- --------------------------------------------
DROP TABLE IF EXISTS fin_statement;
CREATE TABLE IF NOT EXISTS fin_statement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账单行ID',
    file_id BIGINT NOT NULL COMMENT 'fin_statement_file.id',
    user_id BIGINT NOT NULL COMMENT '所属用户',
    platform_code VARCHAR(64) NOT NULL COMMENT '来源平台：WECHAT/ALIPAY/CMB/MEITUAN/TIKTOK/OTHER',
    source_type VARCHAR(32) COMMENT '细分类：PLATFORM_ORDER/BANK_STATEMENT/CREDIT_CARD_STATEMENT/SECURITIES_TRADE',
    raw_row_hash CHAR(32) COMMENT '行级幂等Hash（md5(file_id + row_raw)）',
    raw_data JSON COMMENT '原始或解析后的行 JSON（完整）',
    out_trade_no VARCHAR(128) COMMENT '平台订单号/流水号（若有）',
    stmt_time DATETIME COMMENT '账单行时间（原始时间）',
    amount DECIMAL(18,2) COMMENT '金额（正入/负出或用 direction 字段）',
    direction VARCHAR(8) COMMENT 'IN/OUT',
    counterparty VARCHAR(128) COMMENT '对方/商户（美团/拼多多/招商银行）',
    account_ref VARCHAR(128) COMMENT '原始文本中指示的账户（用于初步映射，如 卡号尾号/支付宝账号）',
    description VARCHAR(255) COMMENT '交易摘要/备注',
    category_id BIGINT COMMENT '交易分类ID（关联 transaction_category）',
    parser_version VARCHAR(32) COMMENT '解析器版本号（便于重解析）',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    parsed_at DATETIME COMMENT '解析完成时间',
    status VARCHAR(16) DEFAULT 'NEW' COMMENT 'NEW/PARSED/MAPPED/IGNORED',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    UNIQUE KEY uk_file_row (file_id, raw_row_hash),
    INDEX idx_user_platform_time (user_id, platform_code, stmt_time),
    INDEX idx_out_trade_no (out_trade_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单行表：保存每一行原始/解析后数据，作为证据';


-- --------------------------------------------
-- 9. 账单到账户映射表
-- --------------------------------------------
DROP TABLE IF EXISTS fin_statement_account_map;
CREATE TABLE IF NOT EXISTS fin_statement_account_map (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '映射ID',
    statement_id BIGINT NOT NULL COMMENT 'fin_statement.id',
    account_id BIGINT NOT NULL COMMENT 'fin_account.id',
    map_type VARCHAR(16) DEFAULT 'AUTO' COMMENT 'AUTO/MANUAL',
    confidence DECIMAL(5,2) DEFAULT 0.00 COMMENT '匹配置信度 0-100',
    mapped_by VARCHAR(64) COMMENT '映射操作人/系统标识',
    mapped_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '映射时间',
    remark VARCHAR(255) COMMENT '备注',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    UNIQUE KEY uk_stmt_account (statement_id, account_id),
    INDEX idx_account_id (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单到账户的映射表（用于确定哪条账单属于哪个账户）';


-- --------------------------------------------
-- 10. 交易与账单映射表
-- --------------------------------------------
DROP TABLE IF EXISTS fin_transaction_statement_map;
CREATE TABLE IF NOT EXISTS fin_transaction_statement_map (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '交易-账单映射ID',
    transaction_id BIGINT NOT NULL COMMENT 'fin_transaction.id',
    statement_id BIGINT NOT NULL COMMENT 'fin_statement.id',
    map_type VARCHAR(16) DEFAULT 'ONE_TO_ONE' COMMENT 'ONE_TO_ONE/ONE_TO_MANY/MANY_TO_ONE',
    allocated_amount DECIMAL(18,2) DEFAULT NULL COMMENT '若是部分分配则记录该条账单映射到交易的金额',
    confirm_status VARCHAR(16) DEFAULT 'AUTO' COMMENT 'AUTO/MANUAL_CONFIRMED/MANUAL_FIXED',
    confidence DECIMAL(5,2) DEFAULT 0.00 COMMENT '匹配置信度',
    match_rule VARCHAR(64) COMMENT '匹配规则标识（便于问题排查）',
    match_detail JSON COMMENT '匹配详情（时间差、金额差等）',
    mapped_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '映射时间',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    UNIQUE KEY uk_tx_stmt (transaction_id, statement_id),
    INDEX idx_statement_id (statement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易与账单的映射，支持分期/合单/人工修正';


-- --------------------------------------------
-- 11. 支付拆分表（支付路由）
-- --------------------------------------------
DROP TABLE IF EXISTS fin_payment_route;
CREATE TABLE IF NOT EXISTS fin_payment_route (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付路由ID',
    transaction_id BIGINT NOT NULL COMMENT 'fin_transaction.id',
    payment_method_id BIGINT NOT NULL COMMENT 'fin_payment_method.id',
    from_account_id BIGINT COMMENT '资金来源账户（可NULL 表示平台出资）',
    to_account_id BIGINT COMMENT '资金去向（可NULL 表示外部流出）',
    amount DECIMAL(18,2) NOT NULL COMMENT '拆分金额',
    route_order INT DEFAULT 1 COMMENT '拆分顺序（展示和执行顺序）',
    route_type VARCHAR(32) COMMENT 'NORMAL/SUBSIDY/FEE/SPLIT',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_from_account (from_account_id),
    INDEX idx_to_account (to_account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付拆分表：表达交易的支付如何由不同来源组成';


-- --------------------------------------------
-- 12. 清算流水表（金融级清算）
-- --------------------------------------------
DROP TABLE IF EXISTS fin_clearing_flow;
CREATE TABLE IF NOT EXISTS fin_clearing_flow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '清算流水ID',
    user_id BIGINT NOT NULL COMMENT '所属用户',
    flow_no VARCHAR(64) NOT NULL UNIQUE COMMENT '清算流水号',
    from_account_id BIGINT COMMENT '资金来源账户（可NULL 表示平台或外部）',
    to_account_id BIGINT COMMENT '资金去向账户（可NULL）',
    transaction_id BIGINT COMMENT '关联的业务交易ID（若有关联）',
    amount DECIMAL(18,2) NOT NULL COMMENT '本次清算金额',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    status VARCHAR(32) DEFAULT 'INIT' COMMENT 'INIT/CLEARING/SUCCESS/FAILED',
    trade_time DATETIME NOT NULL COMMENT '清算时间或发生时间',
    remark VARCHAR(255) COMMENT '备注',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    INDEX idx_user_id (user_id),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_from_account (from_account_id),
    INDEX idx_to_account (to_account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='金融清算流水：用于表达银行/券商等清算中/已清算/失败的资金移动';


-- --------------------------------------------
-- 13. 总账流水表（会计分录级别）
-- --------------------------------------------
DROP TABLE IF EXISTS fin_account_flow;
CREATE TABLE IF NOT EXISTS fin_account_flow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会计流水ID',
    user_id BIGINT NOT NULL COMMENT '所属用户',
    flow_no VARCHAR(64) NOT NULL UNIQUE COMMENT '会计流水号（全局唯一）',
    journal_id BIGINT COMMENT '会计分录组ID（同一笔业务的借贷方共享，用于验证借贷平衡）',
    account_id BIGINT NOT NULL COMMENT '变动账户ID（fin_account.id）',
    transaction_id BIGINT COMMENT '关联业务交易（fin_transaction.id）',
    direction VARCHAR(8) NOT NULL COMMENT 'DEBIT/CREDIT（借/贷方向，便于复式记账）',
    amount DECIMAL(18,2) NOT NULL COMMENT '变动金额（正数）',
    balance_before DECIMAL(18,2) COMMENT '余额变更前（快照）',
    balance_after DECIMAL(18,2) COMMENT '余额变更后（快照）',
    biz_type VARCHAR(32) COMMENT '业务类型，复制自 fin_transaction.biz_type 便于查询',
    trade_time DATETIME NOT NULL COMMENT '发生时间（用于排序/回溯）',
    remark VARCHAR(255) COMMENT '备注',
    created_by VARCHAR(64) COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(64) COMMENT '最近修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
    INDEX idx_user_id (user_id),
    INDEX idx_account_time (account_id, trade_time),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_journal_id (journal_id),
    INDEX idx_trade_time (trade_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='总账流水（会计明细）：所有账户余额变动应通过该表记录（复式记账的边）';


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


select * from fin_account;
select * from fin_platform;
select * from fin_payment_method;
select * from fin_account where user_id = 3 and account_code in (
    'ACCT-3-5');

select * from fin_platform where platform_code = 'JD';

select * from fin_payment_method where payment_code in ('WECHAT_PAY', 'NINGBO_CREDIT');

-- 1、平台创建
delete from fin_platform where platform_code in ('NINGBO','JD','WECHAT');
insert into fin_platform ( platform_code, platform_name, platform_type, support_payment, support_credit, support_balance, support_bill, bill_import_format, bill_email_domain, logo_url, website_url, api_config, description, sort_order, status, created_by, created_at, updated_by, updated_at, is_deleted)
values ('NINGBO','宁波银行','BANK',1,1,1,1,'CSV','',NULL,NULL,NULL,NULL,1,'ACTIVE','system',NOW(),'system',NOW(),0),
       ('JD','京东','PLATFORM',1,1,1,1,'CSV','',NULL,NULL,NULL,NULL,2,'ACTIVE','system',NOW(),'system',NOW(),0),
       ('WECHAT','微信','E_WALLET',1,0,1,1,'CSV','',NULL,'https://weixin.qq.com',NULL,'微信支付平台，支持微信钱包、微信支付',10,'ACTIVE','system',NOW(),'system',NOW(),0);

-- 2、支付方式创建
delete from fin_payment_method where payment_code in ('NINGBO_CREDIT','WECHAT_PAY');
insert into fin_payment_method (payment_code, payment_name, payment_type, platform_code, status, remark, created_at, updated_at)
values ('NINGBO_CREDIT','宁波银行信用卡支付','CREDIT','NINGBO','ACTIVE',NULL,'system',NOW(),'system',NOW(),0),
       ('WECHAT_PAY','微信支付','PLATFORM','WECHAT','ACTIVE','微信聚合支付，支持多种支付方式','system',NOW(),'system',NOW(),0);

-- 3、账户创建
select * from fin_account;
delete from fin_account where user_id = 3 and account_code in ('NINGBO-3-1','WECHAT-3-3');
insert into fin_account (user_id, account_code, account_name, account_category, account_type, owner_type, platform_code, currency, balance, balance_updated_at, external_account_ref, is_virtual, status, remark, created_by, created_at, updated_by, updated_at, is_deleted)
values (3,'NINGBO-3-1','宁波银行借记卡','BANK','DEBIT','USER','NINGBO','CNY',0.00,NULL,'123456',0,'ACTIVE',NULL,'system',NOW(),'system',NOW(),0),
       (3,'WECHAT-3-3','微信补贴','E_WALLET','WALLET','PLATFORM','WECHAT','CNY',0.00,NULL,'wxid_abcdefg',1,'ACTIVE',NULL,'system',NOW(),'system',NOW(),0),
       (3,'WECHAT-3-4','微信商户','CLEARING','CLEARING_ACCOUNT','MERCHANT','WECHAT','CNY',0.00,NULL,'wxid_abcdefg',0,'ACTIVE',NULL,'system',NOW(),'system',NOW(),0),
       (3,'JD-3-4','京东商户','CLEARING','CLEARING_ACCOUNT','MERCHANT','JD','CNY',0.00,NULL,'wxid_abcdefg',0,'ACTIVE',NULL,'system',NOW(),'system',NOW(),0),
       (3,'WECHAT-3-5','微信支付','E_WALLET','WALLET','PLATFORM','WECHAT','CNY',0.00,NULL,'wxid_abcdefg',0,'ACTIVE',NULL,'system',NOW(),'system',NOW(),0);

-- 4、账单导入
delete from fin_statement where user_id= 3 ;
insert into fin_statement (user_id, file_id, platform_code, source_type, raw_row_hash, raw_data, out_trade_no, stmt_time, amount, direction, counterparty, account_ref, description, category_id, parser_version, retry_count, parsed_at, status, created_at, updated_at)
values (3, 1, 'NINGBO', 'PLATFORM_ORDER', '123456', '{"out_trade_no": "123456", "stmt_time": "2023-05-01", "amount": 100.00, "direction": "IN", "counterparty": "京东", "account_ref": "123456", "description": "京东订单"}', '123456', '2023-0');