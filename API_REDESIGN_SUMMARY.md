# 接口重新梳理和调整总结

## 一、根据SQL脚本反向推导的业务流程

根据提供的SQL脚本，完整的交易流程包括以下13个步骤：

1. **平台初始化** - `fin_platform`
2. **支付方式初始化** - `fin_payment_method`
3. **账户创建** - `fin_account`
4. **账单导入** - `fin_statement`
5. **账单到账户映射** - `fin_statement_account_map`
6. **主交易创建** - `fin_transaction`
7. **交易与账单映射** - `fin_transaction_statement_map`
8. **支付绑定** - `fin_payment_binding`
9. **支付路由创建** - `fin_payment_route`
10. **清算流水** - `fin_clearing_flow`
11. **会计流水** - `fin_account_flow`
12. **补贴交易** - `fin_transaction` (biz_type = SUBSIDY)
13. **余额快照** - `fin_account_balance_snapshot`

## 二、新增接口

### 2.1 完整交易流程服务

**文件**：`FinTransactionFlowService.java` 和 `FinTransactionFlowController.java`

**核心接口**：
- `POST /api/fin/transaction-flow/complete` - 完整交易流程（一步到位）
- `POST /api/fin/transaction-flow/confirm-settle/{transactionId}` - 确认并清算
- `POST /api/fin/transaction-flow/payment-routes/{transactionId}` - 批量创建支付路由
- `POST /api/fin/transaction-flow/clearing-flow/{transactionId}` - 创建清算流水
- `POST /api/fin/transaction-flow/account-flow/{transactionId}` - 创建会计流水
- `POST /api/fin/transaction-flow/subsidy` - 创建补贴交易

### 2.2 清算流水服务

**文件**：`FinClearingFlowService.java`

**接口**：
- 创建清算流水
- 更新清算状态
- 查询清算流水（按交易ID、账户ID、时间范围等）

## 三、现有接口增强建议

### 3.1 对账归并接口（`FinReconciliationController`）

**现状**：
- ✅ `POST /api/fin/reconciliation/auto-map-account` - 自动映射账单到账户
- ✅ `POST /api/fin/reconciliation/auto-merge-transaction` - 自动归并账单到交易
- ✅ `POST /api/fin/reconciliation/create-transaction-batch` - 从账单创建交易

**建议增强**：
- 在 `createTransactionFromStatements` 方法中，增加可选参数 `completeFlow`，如果为true，则执行完整流程（包括支付路由、清算、记账）

### 3.2 交易管理接口（`FinTransactionController`）

**现状**：
- ✅ `POST /api/fin/transaction` - 创建交易
- ✅ `POST /api/fin/transaction/{id}/confirm` - 确认交易

**建议增强**：
- `confirmTransaction` 方法增强：增加可选参数 `settle`，如果为true，则同时完成清算和记账

### 3.3 会计流水接口（`FinAccountFlowController`）

**现状**：
- ✅ `POST /api/fin/account-flow/double-entry` - 创建复式记账分录

**无需调整**：接口已满足需求

## 四、接口调用场景

### 场景1：完整自动化流程（推荐）

```http
# 1. 上传账单
POST /api/fin/statement/upload
Content-Type: multipart/form-data
file: [CSV文件]
type: JD

# 2. 完整交易流程（一步到位）
POST /api/fin/transaction-flow/complete
Content-Type: application/json
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

### 场景2：分步操作（灵活控制）

```http
# 1. 上传账单
POST /api/fin/statement/upload

# 2. 自动映射账单到账户
POST /api/fin/reconciliation/auto-map-account

# 3. 从账单创建交易
POST /api/fin/reconciliation/create-transaction-batch
Content-Type: application/json
[1, 2, 3]

# 4. 创建支付路由
POST /api/fin/transaction-flow/payment-routes/123
Content-Type: application/json
[
  {
    "paymentMethodId": 18,
    "fromAccountId": 2,
    "toAccountId": 5,
    "amount": 15.20,
    "routeOrder": 1,
    "routeType": "NORMAL"
  }
]

# 5. 确认并清算
POST /api/fin/transaction-flow/confirm-settle/123
```

### 场景3：手动处理补贴

```http
# 1. ... (完成主交易流程)

# 2. 创建补贴交易
POST /api/fin/transaction-flow/subsidy
Content-Type: application/x-www-form-urlencoded
originalTransactionId=123
subsidyAmount=0.10
fromAccountId=3
toAccountId=4
remark=微信备注: 已优惠¥0.10（平台补贴）
```

## 五、需要实现的Service层

### 5.1 FinTransactionFlowServiceImpl

**需要实现的方法**：
1. `createCompleteTransactionFlow` - 完整流程的核心方法
2. `confirmAndSettleTransaction` - 确认并清算
3. `createPaymentRoutes` - 批量创建支付路由
4. `createClearingFlow` - 创建清算流水
5. `createAccountFlow` - 创建会计流水
6. `createSubsidyTransaction` - 创建补贴交易

**依赖服务**：
- `FinTransactionService` - 交易管理
- `FinReconciliationService` - 对账归并
- `FinPaymentRouteRepository` - 支付路由
- `FinClearingFlowService` - 清算流水
- `FinAccountFlowService` - 会计流水
- `FinAccountService` - 账户管理

### 5.2 FinClearingFlowServiceImpl

**需要实现的方法**：
1. `createClearingFlow` - 创建清算流水
2. `updateClearingStatus` - 更新清算状态
3. 各种查询方法

**依赖**：
- `FinClearingFlowRepository`
- `FinAccountRepository`（用于查询账户名称）

## 六、数据流转图

```
账单上传 (fin_statement)
    ↓
自动映射到账户 (fin_statement_account_map)
    ↓
创建主交易 (fin_transaction)
    ↓
映射账单到交易 (fin_transaction_statement_map)
    ↓
创建支付路由 (fin_payment_route)
    ↓
创建清算流水 (fin_clearing_flow)
    ↓
创建会计流水 (fin_account_flow) - 复式记账
    ↓
处理补贴 (fin_transaction + fin_account_flow)
    ↓
余额快照 (fin_account_balance_snapshot)
```

## 七、关键设计点

### 7.1 事务控制

完整流程接口应该使用 `@Transactional` 确保原子性：
- 如果任何一步失败，整个流程回滚
- 使用 Reactive 事务管理（R2DBC）

### 7.2 幂等性保证

- 使用 `transaction_no` 确保交易唯一性
- 使用 `raw_row_hash` 确保账单唯一性
- 使用 `flow_no` 确保清算流水唯一性

### 7.3 金额匹配验证

- 主交易金额 = 账单金额总和（允许容差）
- 支付路由金额总和 = 主交易金额
- 会计流水借贷平衡

### 7.4 状态流转

```
交易状态：
INIT → MATCHED → CONFIRMED → CANCELLED

清算状态：
INIT → CLEARING → SUCCESS / FAILED
```

## 八、下一步工作

1. ✅ 创建 `FinTransactionFlowService` 接口
2. ✅ 创建 `FinClearingFlowService` 接口
3. ✅ 创建 `FinTransactionFlowController`
4. ⏳ 实现 `FinTransactionFlowServiceImpl`
5. ⏳ 实现 `FinClearingFlowServiceImpl`
6. ⏳ 增强 `FinReconciliationServiceImpl.createTransactionFromStatements`
7. ⏳ 增强 `FinTransactionServiceImpl.confirmTransaction`
8. ⏳ 创建清算流水 Controller
9. ⏳ 编写单元测试

## 九、接口文档位置

- **完整接口设计**：`TRANSACTION_FLOW_API_DESIGN.md`
- **接口梳理总结**：`API_REDESIGN_SUMMARY.md`（本文档）
