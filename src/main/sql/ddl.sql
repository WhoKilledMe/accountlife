-- ✅ 用户表
DROP TABLE IF EXISTS user_account;
CREATE TABLE user_account (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(100) COMMENT '用户名',
  email VARCHAR(100) COMMENT '邮箱',
  phone VARCHAR(20) COMMENT '手机号',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- ✅ 用户组表（支持多人数据聚合分析）
DROP TABLE IF EXISTS user_group;
CREATE TABLE user_group (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户组ID',
  name VARCHAR(100) COMMENT '组名称（如 家庭、公司账本）',
  description VARCHAR(255) COMMENT '组描述',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- ✅ 用户组关系表（多用户关联到组）
DROP TABLE IF EXISTS user_group_member;
CREATE TABLE user_group_member (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
  group_id BIGINT NOT NULL COMMENT '所属用户组ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role TINYINT DEFAULT 1 COMMENT '角色权限：1-组长(owner)，2-成员(member)',
  joined_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间'
);

-- ✅ 账户表（银行、平台、信用钱包）
DROP TABLE IF EXISTS asset_account;
CREATE TABLE asset_account (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账户ID',
  user_id BIGINT NOT NULL COMMENT '所属用户',
  name VARCHAR(100) COMMENT '账户名称（如 招商银行、花呗）',
  type TINYINT NOT NULL COMMENT '账户类型：1-bank(银行)，2-platform(平台)，3-credit_wallet(信用钱包)，4-wallet(虚拟余额)',
  platform_code VARCHAR(50) COMMENT '平台标识（如 ALIPAY、MEITUAN）',
  account_number VARCHAR(100) COMMENT '银行卡号/平台账号',
  is_virtual BOOLEAN DEFAULT FALSE COMMENT '是否为虚拟账户（如信用钱包）',
  credit_limit DECIMAL(18,2) DEFAULT NULL COMMENT '信用额度，仅信用钱包用',
  currency VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- ✅ 分类表
DROP TABLE IF EXISTS transaction_category;
CREATE TABLE transaction_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  name VARCHAR(100) COMMENT '分类名称（如 餐饮）',
  type TINYINT COMMENT '分类类型：1-income(收入)，2-expense(支出)',
  parent_id BIGINT COMMENT '父分类ID',
  icon VARCHAR(50) COMMENT '图标',
  user_id BIGINT COMMENT '所属用户，null 表示系统分类',
  sort_order INT COMMENT '排序值'
);

-- ✅ 交易明细表
DROP TABLE IF EXISTS account_transaction;
CREATE TABLE account_transaction (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '交易ID',
  user_id BIGINT COMMENT '所属用户',
  account_id BIGINT COMMENT '发生账户',
  type TINYINT COMMENT '交易类型：1-income(收入)，2-expense(支出)，3-transfer_out(转出)，4-transfer_in(转入)',
  amount DECIMAL(18,2) NOT NULL COMMENT '交易金额',
  category_id BIGINT COMMENT '分类ID',
  related_transaction_id BIGINT COMMENT '关联交易ID（如转账双边）',
  description VARCHAR(255) COMMENT '摘要说明',
  transaction_time DATETIME COMMENT '实际发生时间',
  source_type TINYINT COMMENT '来源类型：1-bank，2-platform，3-credit_wallet',
  source_ref VARCHAR(100) COMMENT '原始账单唯一标识/流水号',
  statement_id BIGINT COMMENT '账单归属ID（如花呗账单）',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入账时间'
);

-- ✅ 平台账单记录表
DROP TABLE IF EXISTS platform_transaction;
CREATE TABLE platform_transaction (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '平台账单ID',
  user_id BIGINT COMMENT '所属用户',
  platform_code VARCHAR(50) COMMENT '平台编码（如 ALIPAY）',
  raw_json TEXT COMMENT '原始账单JSON数据',
  mapped_transaction_id BIGINT COMMENT '映射到业务交易ID',
  remark VARCHAR(255) COMMENT '备注',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '导入时间'
);

-- ✅ 投资资产表
DROP TABLE IF EXISTS investment_asset;
CREATE TABLE investment_asset (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '投资资产ID',
  user_id BIGINT COMMENT '所属用户',
  account_id BIGINT COMMENT '关联账户',
  code VARCHAR(50) COMMENT '股票/基金代码',
  name VARCHAR(100) COMMENT '名称',
  type TINYINT COMMENT '类型：1-stock(股票)，2-fund(基金)，3-bond(债券)',
  quantity DECIMAL(18,4) COMMENT '持仓数量',
  cost_price DECIMAL(18,4) COMMENT '成本价',
  market_price DECIMAL(18,4) COMMENT '市价',
  currency VARCHAR(10) COMMENT '币种',
  last_updated DATETIME COMMENT '更新时间'
);

-- ✅ 固定资产表
DROP TABLE IF EXISTS fixed_asset;
CREATE TABLE fixed_asset (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '固定资产ID',
  user_id BIGINT COMMENT '所属用户',
  name VARCHAR(100) COMMENT '资产名称',
  type TINYINT COMMENT '资产类型：1-property(房产)，2-vehicle(车辆)，3-equipment(设备)，4-other(其他)',
  value DECIMAL(18,2) COMMENT '评估/购入金额',
  purchase_date DATE COMMENT '购买日期',
  location VARCHAR(200) COMMENT '地址或位置',
  note TEXT COMMENT '备注',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- ✅ 信用钱包账单表（花呗账单周期归集）
DROP TABLE IF EXISTS credit_wallet_statement;
CREATE TABLE credit_wallet_statement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账单ID',
  account_id BIGINT COMMENT '信用钱包账户ID',
  user_id BIGINT COMMENT '所属用户',
  billing_period_start DATE COMMENT '账单期开始日期',
  billing_period_end DATE COMMENT '账单期结束日期',
  total_amount DECIMAL(18,2) COMMENT '应还金额',
  repay_due_date DATE COMMENT '还款到期日',
  repay_date DATE COMMENT '实际还款日',
  status TINYINT COMMENT '账单状态：1-open(未还)，2-paid(已还)，3-overdue(逾期)',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间'
);
