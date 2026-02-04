# 交易流程接口设计文档

## 一、业务流程分析

根据SQL脚本，完整的交易流程包括以下步骤：

### 1. 数据准备阶段
- **平台初始化**：创建平台字典（JD、WECHAT、NINGBO）
- **支付方式初始化**：创建支付方式（WECHAT_PAY、NINGBO_CREDIT）
- **账户创建**：创建各类账户（银行账户、钱包账户、商户账户、补贴账户）

### 2. 账单导入阶段
- **账单文件上传**：上传CSV/Excel文件
- **账单解析**：解析为 `fin_statement` 记录
- **分类识别**：使用AI服务识别交易分类

### 3. 对账映射阶段
- **账单到账户映射**：`fin_statement_account_map`
  - 自动映射：根据账户标识（account_ref）匹配
  - 手动映射：用户手动指定
- **账单到交易映射**：`fin_transaction_statement_map`
  - 自动归并：根据订单号、金额、时间窗口匹配
  - 手动归并：用户手动关联

### 4. 交易创建阶段
- **主交易创建**：`fin_transaction`
  - 从账单推断或手动创建
  - 包含业务金额（15.30元）

### 5. 支付路由阶段
- **支付拆分**：`fin_payment_route`
  - 描述资金流向：宁波信用卡 → 微信支付 → 京东商户（15.20元）
  - 补贴路由：微信补贴 → 京东商户（0.10元）

### 6. 清算阶段
- **清算流水**：`fin_clearing_flow`
  - 记录银行/渠道已清算的资金移动
  - 状态：INIT → CLEARING → SUCCESS/FAILED

### 7. 记账阶段
- **会计流水**：`fin_account_flow`
  - 复式记账：借京东商户 15.20 / 贷宁波信用卡 15.20
  - 补贴记账：借京东商户 0.10 / 贷微信补贴 0.10

### 8. 补贴处理阶段
- **补贴交易**：独立的 `fin_transaction`（biz_type = SUBSIDY）
  - 关联原交易（original_transaction_id）
  - 金额：0.10元

## 二、接口设计

### 2.1 完整交易流程接口

**接口**：`POST /api/fin/transaction-flow/complete`

**功能**：从账单创建交易并完成所有后续流程

**请求体**：
```json
{
  "statementIds": [1, 2, 3],
  "transactionDto": {
    "transactionNo": "122",
    "bizType": "PAY",
    "bizSubType": "JD_PAY",
    "amount": 15.30,
    "currency": "CNY",
    "counterparty": "京东外卖",
    "platformCode": "JD",
    "tradeTime": "2026-01-23T10:59:04"
  }
}
```

**流程**：
1. 创建主交易（如果transactionDto为null，则从账单推断）
2. 自动映射账单到账户（根据account_ref）
3. 自动映射账单到交易（根据订单号、金额、时间）
4. 创建支付路由（根据账单中的支付方式推断）
5. 创建清算流水（如果涉及银行清算）
6. 创建会计流水（复式记账）
7. 处理补贴（如果账单中有优惠信息）

### 2.2 确认交易并完成清算

**接口**：`POST /api/fin/transaction-flow/confirm-settle/{transactionId}`

**功能**：确认交易并完成清算和记账

**流程**：
1. 更新交易状态为 CONFIRMED
2. 创建清算流水（如果还未创建）
3. 创建会计流水（复式记账）
4. 更新账户余额（可选）

### 2.3 支付路由管理

**接口**：`POST /api/fin/transaction-flow/payment-routes/{transactionId}`

**功能**：批量创建支付路由

**请求体**：
```json
[
  {
    "paymentMethodId": 18,
    "fromAccountId": 2,
    "toAccountId": 5,
    "amount": 15.20,
    "routeOrder": 1,
    "routeType": "NORMAL"
  },
  {
    "paymentMethodId": 1,
    "fromAccountId": 5,
    "toAccountId": 4,
    "amount": 15.20,
    "routeOrder": 2,
    "routeType": "NORMAL"
  },
  {
    "paymentMethodId": 1,
    "fromAccountId": 3,
    "toAccountId": 4,
    "amount": 0.10,
    "routeOrder": 3,
    "routeType": "SUBSIDY"
  }
]
```

### 2.4 清算流水管理

**接口**：`POST /api/fin/transaction-flow/clearing-flow/{transactionId}`

**功能**：创建清算流水

**参数**：
- `fromAccountId`: 资金来源账户ID
- `toAccountId`: 资金去向账户ID
- `amount`: 清算金额
- `remark`: 备注

### 2.5 会计流水管理

**接口**：`POST /api/fin/transaction-flow/account-flow/{transactionId}`

**功能**：创建会计流水（复式记账）

**参数**：
- `debitAccountId`: 借方账户ID
- `creditAccountId`: 贷方账户ID
- `amount`: 金额
- `bizType`: 业务类型
- `remark`: 备注

### 2.6 补贴交易处理

**接口**：`POST /api/fin/transaction-flow/subsidy`

**功能**：创建补贴交易

**参数**：
- `originalTransactionId`: 原交易ID
- `subsidyAmount`: 补贴金额
- `fromAccountId`: 补贴来源账户
- `toAccountId`: 补贴去向账户
- `remark`: 备注

## 三、现有接口增强

### 3.1 对账归并接口（已存在）

**接口**：`POST /api/fin/reconciliation/auto-map-account`
- 自动映射账单到账户

**接口**：`POST /api/fin/reconciliation/auto-merge-transaction`
- 自动归并账单到交易

**接口**：`POST /api/fin/reconciliation/create-transaction-batch`
- 从多条账单创建交易（需要增强，支持完整流程）

### 3.2 交易管理接口（已存在）

**接口**：`POST /api/fin/transaction`
- 创建交易（需要增强，支持支付路由、清算、记账）

**接口**：`POST /api/fin/transaction/{id}/confirm`
- 确认交易（需要增强，支持清算和记账）

## 四、接口调用顺序建议

### 场景1：完整自动化流程
```
1. POST /api/fin/statement/upload (上传账单)
2. POST /api/fin/transaction-flow/complete (完整流程)
```

### 场景2：分步操作
```
1. POST /api/fin/statement/upload (上传账单)
2. POST /api/fin/reconciliation/auto-map-account (自动映射账户)
3. POST /api/fin/reconciliation/create-transaction-batch (创建交易)
4. POST /api/fin/transaction-flow/payment-routes/{id} (创建支付路由)
5. POST /api/fin/transaction-flow/confirm-settle/{id} (确认并清算)
```

### 场景3：手动处理补贴
```
1. ... (完成主交易流程)
2. POST /api/fin/transaction-flow/subsidy (创建补贴交易)
```

## 五、数据一致性保证

1. **事务控制**：完整流程接口使用事务，确保所有步骤要么全部成功，要么全部回滚
2. **幂等性**：使用订单号、交易号等唯一标识确保幂等
3. **状态管理**：交易状态流转：INIT → MATCHED → CONFIRMED
4. **余额更新**：会计流水创建后，可选择是否更新账户余额

## 六、错误处理

1. **账单未映射**：如果账单未映射到账户，不能创建交易
2. **金额不匹配**：如果账单金额总和与交易金额不一致，需要警告或拒绝
3. **账户不存在**：如果指定的账户不存在，返回错误
4. **重复创建**：如果交易已存在，返回错误或更新

## 七、性能优化

1. **批量操作**：支持批量创建支付路由、会计流水
2. **异步处理**：清算和记账可以异步处理
3. **缓存**：账户信息、支付方式信息可以缓存
