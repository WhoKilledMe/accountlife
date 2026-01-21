-- ============================================
-- AccountLife 财务系统重构 - DDL
-- 版本: 2.0
-- 日期: 2026-01-21
-- 描述: 多层分离架构，支撑采账单→建账户→对账归并→资金图谱
-- ============================================

-- --------------------------------------------
-- 1. 账户主表：描述资金容器
-- --------------------------------------------
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
-- 2. 账户扩展表：机构特有字段
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS fin_account_ext (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '扩展ID',
    account_id BIGINT NOT NULL COMMENT 'fin_account.id',
    json_ext JSON COMMENT '银行/券商等扩展信息（卡号掩码、账单日、开户行、授信额度等）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_account_id (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户扩展信息，按 account_id 一对一或一对多存放';


-- --------------------------------------------
-- 3. 账户余额快照表：用于历史/报表
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS fin_account_balance_snapshot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '快照ID',
    account_id BIGINT NOT NULL COMMENT 'fin_account.id',
    snapshot_date DATE NOT NULL COMMENT '快照日期（按日）',
    balance DECIMAL(18,2) NOT NULL COMMENT '当天结束时的余额快照',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    UNIQUE KEY uk_account_snapshot (account_id, snapshot_date),
    INDEX idx_snapshot_date (snapshot_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户日度余额快照表，用于报表/性能优化';


-- --------------------------------------------
-- 4. 支付方式表：抽象支付能力
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS fin_payment_method (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付方式ID',
    payment_code VARCHAR(64) NOT NULL UNIQUE COMMENT '支付方式代码（WECHAT_PAY/ALIPAY_PAY/BANK_DEBIT/CREDIT_PAY/MEITUAN_MONTH）',
    payment_name VARCHAR(128) NOT NULL COMMENT '人类可读名称，如：微信支付（聚合）',
    payment_type VARCHAR(32) NOT NULL COMMENT '支付类别：PLATFORM/BALANCE/CREDIT/BANK/MIXED/SUBSIDY',
    platform_code VARCHAR(64) COMMENT '归属平台：WECHAT/ALIPAY/MEITUAN/TIKTOK/NINGBO_BANK/xxx',
    status VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE',
    remark VARCHAR(255) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_platform_code (platform_code),
    INDEX idx_payment_type (payment_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付方式能力表（抽象支付通道/能力）';


-- --------------------------------------------
-- 5. 支付方式与账户绑定表
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS fin_payment_binding (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '绑定ID',
    payment_method_id BIGINT NOT NULL COMMENT 'fin_payment_method.id',
    account_id BIGINT NOT NULL COMMENT 'fin_account.id',
    priority INT DEFAULT 0 COMMENT '路由优先级，数值越大优先',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_payment_account (payment_method_id, account_id),
    INDEX idx_account_id (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付能力与具体账户的绑定关系（路由表）';


-- --------------------------------------------
-- 6. 交易中枢表：业务事实
-- --------------------------------------------
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
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_platform_code (platform_code),
    INDEX idx_status (status),
    INDEX idx_file_md5 (file_md5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单文件导入日志：记录文件来源及处理状态';


-- --------------------------------------------
-- 8. 账单行表：原始行证据
-- --------------------------------------------
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
    parser_version VARCHAR(32) COMMENT '解析器版本号（便于重解析）',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    parsed_at DATETIME COMMENT '解析完成时间',
    status VARCHAR(16) DEFAULT 'NEW' COMMENT 'NEW/PARSED/MAPPED/IGNORED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '导入时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_file_row (file_id, raw_row_hash),
    INDEX idx_user_platform_time (user_id, platform_code, stmt_time),
    INDEX idx_out_trade_no (out_trade_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单行表：保存每一行原始/解析后数据，作为证据';


-- --------------------------------------------
-- 9. 账单到账户映射表
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS fin_statement_account_map (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '映射ID',
    statement_id BIGINT NOT NULL COMMENT 'fin_statement.id',
    account_id BIGINT NOT NULL COMMENT 'fin_account.id',
    map_type VARCHAR(16) DEFAULT 'AUTO' COMMENT 'AUTO/MANUAL',
    confidence DECIMAL(5,2) DEFAULT 0.00 COMMENT '匹配置信度 0-100',
    mapped_by VARCHAR(64) COMMENT '映射操作人/系统标识',
    mapped_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '映射时间',
    remark VARCHAR(255) COMMENT '备注',
    UNIQUE KEY uk_stmt_account (statement_id, account_id),
    INDEX idx_account_id (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单到账户的映射表（用于确定哪条账单属于哪个账户）';


-- --------------------------------------------
-- 10. 交易与账单映射表
-- --------------------------------------------
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
    UNIQUE KEY uk_tx_stmt (transaction_id, statement_id),
    INDEX idx_statement_id (statement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易与账单的映射，支持分期/合单/人工修正';


-- --------------------------------------------
-- 11. 支付拆分表（支付路由）
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS fin_payment_route (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付路由ID',
    transaction_id BIGINT NOT NULL COMMENT 'fin_transaction.id',
    payment_method_id BIGINT NOT NULL COMMENT 'fin_payment_method.id',
    from_account_id BIGINT COMMENT '资金来源账户（可NULL 表示平台出资）',
    to_account_id BIGINT COMMENT '资金去向（可NULL 表示外部流出）',
    amount DECIMAL(18,2) NOT NULL COMMENT '拆分金额',
    route_order INT DEFAULT 1 COMMENT '拆分顺序（展示和执行顺序）',
    route_type VARCHAR(32) COMMENT 'NORMAL/SUBSIDY/FEE/SPLIT',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_from_account (from_account_id),
    INDEX idx_to_account (to_account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付拆分表：表达交易的支付如何由不同来源组成';


-- --------------------------------------------
-- 12. 清算流水表（金融级清算）
-- --------------------------------------------
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
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_from_account (from_account_id),
    INDEX idx_to_account (to_account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='金融清算流水：用于表达银行/券商等清算中/已清算/失败的资金移动';


-- --------------------------------------------
-- 13. 总账流水表（会计分录级别）
-- --------------------------------------------
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
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_account_time (account_id, trade_time),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_journal_id (journal_id),
    INDEX idx_trade_time (trade_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='总账流水（会计明细）：所有账户余额变动应通过该表记录（复式记账的边）';
