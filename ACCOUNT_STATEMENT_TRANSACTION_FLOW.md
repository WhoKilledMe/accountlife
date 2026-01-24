# 账户、账单、交易流程说明

## 一、场景分析

以用户提供的实际对账场景为例：

### 场景描述
用户在京东购买商品，通过微信支付，微信支付又通过宁波银行信用卡扣款。

### 涉及的三个平台账单

1. **京东账单**
   - 交易时间：2026-01-23 10:59:04
   - 商户名称：京东外卖
   - 交易说明：老手艺肉酱米线 标准 米线 等多件
   - 金额：15.3元
   - 收/付款方式：微信支付
   - 交易状态：交易成功
   - 收/支：支出
   - 交易分类：食品酒饮
   - 交易订单号：3387235006482426
   - 商家订单号：6181972601231058520308800374

2. **宁波银行账单**
   - 交易日期：2026/1/23
   - 记账日期：2026/1/23
   - 交易摘要：财付通-京东商城平台商户
   - 交易金额：15.2元

3. **微信账单**
   - 交易时间：2026-01-23 10:59:04
   - 交易类型：商户消费
   - 交易对方：京东
   - 商品：京东-订单编号3387235006482426
   - 收/支：支出
   - 金额：¥15.20
   - 支付方式：宁波银行信用卡(4573)
   - 当前状态：支付成功
   - 交易单号：4200002923202601237288113096
   - 商户单号：6181972601231058520308800374
   - 备注：已优惠¥0.10

### 关键匹配字段

- **订单号匹配**：
  - 京东交易订单号：3387235006482426
  - 微信商户单号：6181972601231058520308800374（与京东商家订单号一致）
  - 微信商品描述中包含京东订单号：3387235006482426

- **金额匹配**：
  - 京东：15.3元（含优惠）
  - 微信：15.20元（实际支付）
  - 宁波银行：15.2元（银行扣款）
  - 差异：0.1元（优惠金额）

- **时间匹配**：
  - 所有账单时间都在 2026-01-23 10:59:04 附近

## 二、数据流转流程

### 阶段1：账单导入与解析

```
用户上传账单文件
    ↓
系统识别平台类型（京东/微信/银行）
    ↓
调用对应的解析器（JingdongFinStatementParser/WechatFinStatementParser/NingboFinStatementParser）
    ↓
解析CSV文件，提取关键字段
    ↓
创建 FinStatement 记录，保存到 fin_statement 表
    ↓
状态：NEW → PARSED
```

**关键字段提取**：
- `out_trade_no`：订单号（用于匹配）
- `stmt_time`：交易时间
- `amount`：金额
- `direction`：方向（IN/OUT）
- `counterparty`：对方/商户
- `account_ref`：账户引用（如卡号尾号）
- `raw_data`：原始JSON数据（完整证据）

### 阶段2：账单到账户映射

```
查询待映射的账单（status = PARSED）
    ↓
根据 account_ref 自动匹配账户
    ↓
创建 fin_statement_account_map 记录
    ↓
状态：PARSED → MAPPED
```

**映射逻辑**：
- 从 `account_ref` 中提取账户信息（如卡号尾号"4573"）
- 在 `fin_account` 表中查找匹配的账户
- 创建映射关系，记录置信度

### 阶段3：账单到交易归并（核心对账逻辑）

```
查询已映射的账单（status = MAPPED）
    ↓
智能匹配算法：
  1. 通过订单号匹配（out_trade_no）
  2. 通过金额+时间匹配（容差：0.1元，时间窗口：±5分钟）
  3. 通过商户订单号匹配（从 raw_data 中提取）
    ↓
找到匹配的账单
    ↓
检查是否已有交易：
  - 如果已有交易 → 关联当前账单到该交易
  - 如果没有交易 → 创建新交易并关联所有匹配的账单
    ↓
创建 fin_transaction_statement_map 记录
    ↓
创建或更新 fin_transaction 记录
```

**匹配策略**（优先级从高到低）：

1. **精确订单号匹配**：
   - 直接通过 `out_trade_no` 字段匹配
   - 支持部分匹配（如商户订单号包含在交易订单号中）

2. **金额+时间匹配**：
   - 金额差异 ≤ 0.1元
   - 时间差异 ≤ 5分钟
   - 排除同一平台的账单（避免重复）

3. **商户订单号匹配**：
   - 从 `raw_data` JSON 中提取商户订单号
   - 跨平台匹配（如京东商家订单号 = 微信商户单号）

### 阶段4：交易创建

```
收集所有匹配的账单
    ↓
计算总金额（如果多条账单）
    ↓
创建 fin_transaction 记录：
  - biz_type：业务类型（PAY/INCOME）
  - trade_time：交易时间（取第一条账单时间）
  - amount：交易金额
  - counterparty：对方/商户
  - platform_code：主平台代码
  - category_id：分类ID
  - status：MATCHED（已对账）
    ↓
创建 fin_transaction_statement_map 记录（多条）
    ↓
状态：MAPPED → 完成
```

## 三、实际场景数据流转示例

### 步骤1：导入京东账单

```sql
-- fin_statement 表
INSERT INTO fin_statement (
    user_id, platform_code, out_trade_no, stmt_time, amount, 
    direction, counterparty, description, status
) VALUES (
    1, 'JINGDONG', '3387235006482426', '2026-01-23 10:59:04', 
    15.30, 'OUT', '京东外卖', '老手艺肉酱米线...', 'PARSED'
);
```

### 步骤2：导入微信账单

```sql
-- fin_statement 表
INSERT INTO fin_statement (
    user_id, platform_code, out_trade_no, stmt_time, amount, 
    direction, counterparty, account_ref, description, status
) VALUES (
    1, 'WECHAT', '4200002923202601237288113096', '2026-01-23 10:59:04', 
    15.20, 'OUT', '京东', '4573', '京东-订单编号3387235006482426', 'PARSED'
);
```

**注意**：微信账单的 `raw_data` 中包含：
```json
{
  "merchantNo": "6181972601231058520308800374",
  "product": "京东-订单编号3387235006482426"
}
```

### 步骤3：导入宁波银行账单

```sql
-- fin_statement 表
INSERT INTO fin_statement (
    user_id, platform_code, stmt_time, amount, 
    direction, counterparty, description, status
) VALUES (
    1, 'NINGBO_BANK', '2026-01-23 10:59:04', 
    15.20, 'OUT', '财付通-京东商城平台商户', '财付通-京东商城平台商户', 'PARSED'
);
```

### 步骤4：自动映射到账户

```sql
-- fin_statement_account_map 表
-- 微信账单映射到宁波银行信用卡账户（通过 account_ref = '4573'）
INSERT INTO fin_statement_account_map (
    statement_id, account_id, map_type, confidence, mapped_by
) VALUES (
    2, 123, 'AUTO', 80.00, 'SYSTEM'
);
```

### 步骤5：智能对账归并

**匹配过程**：

1. 处理京东账单（ID=1）：
   - 查找匹配：通过订单号 "3387235006482426" 找到微信账单（商品描述中包含该订单号）
   - 金额匹配：15.30 vs 15.20（差异0.1元，在容差范围内）
   - 时间匹配：相同时间
   - 创建交易，关联京东账单和微信账单

2. 处理微信账单（ID=2）：
   - 已关联到交易，跳过

3. 处理宁波银行账单（ID=3）：
   - 查找匹配：通过金额15.20和时间匹配到微信账单
   - 关联到同一笔交易

**最终结果**：

```sql
-- fin_transaction 表
INSERT INTO fin_transaction (
    user_id, biz_type, trade_time, amount, currency, 
    counterparty, platform_code, category_id, status
) VALUES (
    1, 'PAY', '2026-01-23 10:59:04', 15.20, 'CNY',
    '京东', 'JINGDONG', 123, 'MATCHED'
);

-- fin_transaction_statement_map 表（三条记录）
INSERT INTO fin_transaction_statement_map (
    transaction_id, statement_id, map_type, confidence
) VALUES 
    (1, 1, 'MANY_TO_ONE', 95.00),  -- 京东账单
    (1, 2, 'MANY_TO_ONE', 100.00), -- 微信账单
    (1, 3, 'MANY_TO_ONE', 90.00);  -- 宁波银行账单
```

## 四、API 使用示例

### 1. 导入账单

```bash
# 导入京东账单
POST /api/fin/statement/upload
Content-Type: multipart/form-data
{
  "file": "京东账单.csv",
  "sourceType": "JINGDONG"
}

# 导入微信账单
POST /api/fin/statement/upload
Content-Type: multipart/form-data
{
  "file": "微信账单.csv",
  "sourceType": "WECHAT"
}

# 导入宁波银行账单
POST /api/fin/statement/upload
Content-Type: multipart/form-data
{
  "file": "宁波银行账单.csv",
  "sourceType": "NING_BO_CREDIT"
}
```

### 2. 自动映射到账户

```bash
POST /api/fin/reconciliation/auto-map-account
```

### 3. 自动对账归并

```bash
POST /api/fin/reconciliation/auto-merge-transaction
```

### 4. 手动归并账单到交易

```bash
POST /api/fin/reconciliation/merge-transaction?statementId=1&transactionId=1&mapType=MANY_TO_ONE
```

### 5. 从多条账单创建交易（合单）

```bash
POST /api/fin/reconciliation/create-transaction-batch
Content-Type: application/json
[1, 2, 3]  # statementIds
```

## 五、关键设计点

### 1. 幂等性保证

- 使用 `raw_row_hash`（file_id + row_raw 的 MD5）确保同一行不会重复导入
- 使用 `UNIQUE KEY uk_file_row (file_id, raw_row_hash)` 约束

### 2. 原始数据保存

- `raw_data` 字段保存完整的原始JSON数据
- 便于后续重新解析或审计

### 3. 匹配置信度

- 自动匹配：置信度 80-95%
- 手动匹配：置信度 100%
- 支持后续人工审核和调整

### 4. 多对一映射

- 支持多条账单归并到一笔交易（`MANY_TO_ONE`）
- 支持一条账单对应一笔交易（`ONE_TO_ONE`）
- 支持一笔交易对应多条账单（`ONE_TO_MANY`）

### 5. 金额容差处理

- 考虑优惠、手续费等因素
- 默认容差：0.1元
- 可通过配置调整

## 六、扩展功能

### 1. 智能匹配增强

- 支持模糊匹配（如订单号部分匹配）
- 支持时间窗口调整
- 支持金额容差配置

### 2. 对账报告

- 生成对账报告，显示匹配情况
- 标记未匹配的账单
- 提供人工审核界面

### 3. 异常处理

- 金额差异过大告警
- 时间差异过大告警
- 重复匹配检测

### 4. 批量处理

- 支持批量导入账单
- 支持批量对账
- 支持批量创建交易

## 七、总结

整个流程实现了：

1. **多平台账单统一管理**：通过解析器将不同格式的账单统一为 `FinStatement`
2. **智能账户映射**：自动识别账户信息并建立映射关系
3. **跨平台对账**：通过订单号、金额、时间等字段智能匹配账单
4. **交易归并**：将多条相关账单归并到一笔交易，形成完整的资金流转记录
5. **数据可追溯**：保存原始数据，支持审计和重新解析

这样的设计能够有效处理复杂的跨平台支付场景，实现自动化的对账归并。
