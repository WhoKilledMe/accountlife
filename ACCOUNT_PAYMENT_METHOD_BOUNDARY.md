# 账户与支付方式的边界定义

## 一、核心概念

### 1. 账户（Account）- `fin_account`

**定义**：**资金容器**，是用户拥有的具体账户实例，有余额、有状态。

**特征**：
- ✅ 有余额（`balance`）
- ✅ 有所有者（`user_id`）
- ✅ 有状态（`ACTIVE/INACTIVE/FROZEN`）
- ✅ 是具体实例（如：张三的宁波银行信用卡尾号4573）
- ✅ 可以记录资金变动（`fin_account_flow`）

**示例**：
```
账户1：宁波银行信用卡(尾号4573)
  - user_id: 1
  - account_name: "宁波银行信用卡(尾号4573)"
  - platform_code: "NINGBO"
  - balance: 5000.00
  - account_type: "CREDIT"

账户2：微信零钱
  - user_id: 1
  - account_name: "微信零钱"
  - platform_code: "WECHAT"
  - balance: 100.50
  - account_type: "E_WALLET"

账户3：支付宝余额
  - user_id: 1
  - account_name: "支付宝余额"
  - platform_code: "ALIPAY"
  - balance: 200.00
  - account_type: "E_WALLET"
```

### 2. 支付方式（Payment Method）- `fin_payment_method`

**定义**：**支付能力/通道**，是抽象的支付类型定义，不是具体实例。

**特征**：
- ❌ 没有余额
- ❌ 没有所有者
- ✅ 是能力定义（如：微信支付、信用卡支付）
- ✅ 可以绑定到多个账户
- ✅ 描述支付类型（`PLATFORM/BALANCE/CREDIT/BANK`）

**示例**：
```
支付方式1：微信支付（聚合）
  - payment_code: "WECHAT_PAY"
  - payment_name: "微信支付"
  - payment_type: "PLATFORM"
  - platform_code: "WECHAT"

支付方式2：零钱支付
  - payment_code: "WECHAT_BALANCE"
  - payment_name: "零钱"
  - payment_type: "BALANCE"
  - platform_code: "WECHAT"

支付方式3：信用卡支付
  - payment_code: "CREDIT_PAY"
  - payment_name: "信用卡支付"
  - payment_type: "CREDIT"
  - platform_code: "OTHER"
```

## 二、边界划分

### 边界原则

| 维度 | 账户（Account） | 支付方式（Payment Method） |
|------|----------------|---------------------------|
| **本质** | 资金容器（具体实例） | 支付能力（抽象定义） |
| **所有者** | 属于用户 | 属于系统/平台 |
| **余额** | ✅ 有余额 | ❌ 无余额 |
| **状态** | ✅ 有状态（激活/冻结） | ✅ 有状态（启用/禁用） |
| **唯一性** | 每个用户有多个账户 | 系统全局唯一 |
| **生命周期** | 随用户账户创建/注销 | 系统级配置，长期存在 |
| **变动记录** | ✅ 记录资金流水 | ❌ 不记录资金变动 |

### 关键区别

#### 1. **实例 vs 类型**

- **账户**：是实例
  - 每个用户都有自己的账户实例
  - 例如：用户A的"宁波银行信用卡(尾号4573)" ≠ 用户B的"宁波银行信用卡(尾号4573)"

- **支付方式**：是类型
  - 所有用户共享同一套支付方式定义
  - 例如：所有用户的"微信支付"都是同一个支付方式定义

#### 2. **余额 vs 能力**

- **账户**：有余额，是资金的实际存放地
  ```sql
  SELECT balance FROM fin_account WHERE id = 1;
  -- 返回：100.50（实际余额）
  ```

- **支付方式**：无余额，只是支付能力的描述
  ```sql
  SELECT * FROM fin_payment_method WHERE payment_code = 'WECHAT_PAY';
  -- 返回：支付方式定义（无余额字段）
  ```

#### 3. **用户维度 vs 系统维度**

- **账户**：用户维度
  - 每个用户有自己独立的账户列表
  - 查询：`SELECT * FROM fin_account WHERE user_id = 1`

- **支付方式**：系统维度
  - 所有用户共享同一套支付方式
  - 查询：`SELECT * FROM fin_payment_method WHERE status = 'ACTIVE'`

## 三、关系模型

### 1. 支付方式与账户的绑定关系

通过 `fin_payment_binding` 表建立多对多关系：

```
支付方式 ←→ 账户绑定表 ←→ 账户
```

**示例**：
```sql
-- 支付方式：微信支付
payment_method_id = 1 (WECHAT_PAY)

-- 可以绑定到多个账户：
binding1: payment_method_id=1, account_id=2 (微信零钱)
binding2: payment_method_id=1, account_id=3 (微信零钱通)
```

**含义**：
- "微信支付"这个支付方式可以使用"微信零钱"或"微信零钱通"账户
- 用户选择"微信支付"时，系统会从绑定的账户中选择一个进行扣款

### 2. 交易中的支付路由

通过 `fin_payment_route` 表记录交易实际使用的支付方式和账户：

```
交易 → 支付路由 → 支付方式 + 账户
```

**示例**：
```sql
-- 交易：在京东购买商品，金额15.20元
transaction_id = 1

-- 支付路由：通过微信支付，从微信零钱扣款
payment_route:
  transaction_id = 1
  payment_method_id = 1 (微信支付)
  from_account_id = 2 (微信零钱)
  amount = 15.20
```

## 四、实际场景分析

### 场景：用户在京东购买商品，通过微信支付，从微信零钱扣款

#### 1. 账户层面（资金容器）

```sql
-- 用户的微信零钱账户
fin_account:
  id: 2
  user_id: 1
  account_name: "微信零钱"
  platform_code: "WECHAT"
  balance: 100.50  -- 实际余额
  account_type: "E_WALLET"
```

**特点**：
- 这是用户的具体账户实例
- 有实际余额：100.50元
- 扣款后余额会变动：100.50 → 85.30

#### 2. 支付方式层面（支付能力）

```sql
-- 微信支付方式定义
fin_payment_method:
  id: 1
  payment_code: "WECHAT_PAY"
  payment_name: "微信支付"
  payment_type: "PLATFORM"
  platform_code: "WECHAT"
```

**特点**：
- 这是支付能力的抽象定义
- 没有余额，只是描述"微信支付"这种支付方式
- 所有用户共享这个定义

#### 3. 绑定关系

```sql
-- 微信支付可以绑定到微信零钱账户
fin_payment_binding:
  payment_method_id: 1 (微信支付)
  account_id: 2 (微信零钱)
  priority: 10
  enabled: 1
```

**含义**：
- 当用户选择"微信支付"时，系统知道可以使用"微信零钱"账户
- 可以有多个绑定（如：微信支付也可以绑定到"微信零钱通"）

#### 4. 实际支付路由

```sql
-- 交易实际使用的支付方式和账户
fin_payment_route:
  transaction_id: 1
  payment_method_id: 1 (微信支付)
  from_account_id: 2 (微信零钱)
  amount: 15.20
```

**含义**：
- 记录了这笔交易实际使用了"微信支付"方式
- 实际从"微信零钱"账户扣款15.20元

## 五、设计原则

### 1. 账户设计原则

✅ **应该放在账户表**：
- 用户的具体账户实例（如：张三的宁波银行信用卡）
- 有余额的实体
- 需要记录资金变动的实体
- 用户拥有的、可管理的实体

❌ **不应该放在账户表**：
- 支付方式的抽象定义
- 没有余额的支付通道
- 系统级的配置信息

### 2. 支付方式设计原则

✅ **应该放在支付方式表**：
- 支付能力的抽象定义（如：微信支付、信用卡支付）
- 支付类型的分类（PLATFORM/BALANCE/CREDIT）
- 系统级的支付通道配置

❌ **不应该放在支付方式表**：
- 用户的具体账户实例
- 余额信息
- 用户维度的数据

### 3. 边界判断标准

**判断标准1：是否有余额**
- 有余额 → 账户
- 无余额 → 支付方式

**判断标准2：是否属于用户**
- 属于用户（每个用户有独立实例）→ 账户
- 属于系统（所有用户共享）→ 支付方式

**判断标准3：是否需要记录资金变动**
- 需要记录资金变动 → 账户
- 不需要记录资金变动 → 支付方式

## 六、常见混淆场景

### 混淆1：把支付方式当作账户

❌ **错误理解**：
```
"微信支付"是一个账户，余额是100元
```

✅ **正确理解**：
```
"微信零钱"是一个账户，余额是100元
"微信支付"是支付方式，可以绑定到"微信零钱"账户
```

### 混淆2：把账户当作支付方式

❌ **错误理解**：
```
"宁波银行信用卡(尾号4573)"是支付方式
```

✅ **正确理解**：
```
"宁波银行信用卡(尾号4573)"是账户
"信用卡支付"是支付方式，可以绑定到该账户
```

### 混淆3：支付方式有余额

❌ **错误理解**：
```
查询支付方式的余额：SELECT balance FROM fin_payment_method
```

✅ **正确理解**：
```
支付方式没有余额，需要查询绑定的账户：
SELECT a.balance 
FROM fin_account a
JOIN fin_payment_binding b ON a.id = b.account_id
WHERE b.payment_method_id = 1
```

## 七、数据流转示例

### 场景：用户使用微信支付购买商品

```
1. 用户选择支付方式
   ↓
   选择：微信支付（payment_method_id = 1）
   
2. 系统查找绑定的账户
   ↓
   查询：fin_payment_binding WHERE payment_method_id = 1
   找到：account_id = 2 (微信零钱)
   
3. 检查账户余额
   ↓
   查询：fin_account WHERE id = 2
   余额：100.50元（足够支付）
   
4. 创建交易和支付路由
   ↓
   fin_transaction: 创建交易记录
   fin_payment_route: 
     payment_method_id = 1 (微信支付)
     from_account_id = 2 (微信零钱)
     amount = 15.20
   
5. 更新账户余额
   ↓
   fin_account: balance = 100.50 - 15.20 = 85.30
   fin_account_flow: 记录资金变动流水
```

## 八、总结

### 账户（Account）
- **本质**：资金容器，具体实例
- **特征**：有余额、有所有者、有状态
- **维度**：用户维度
- **用途**：存储资金、记录余额变动

### 支付方式（Payment Method）
- **本质**：支付能力，抽象定义
- **特征**：无余额、系统级配置
- **维度**：系统维度
- **用途**：描述支付类型、路由到账户

### 关系
- **多对多**：一个支付方式可以绑定多个账户，一个账户可以支持多个支付方式
- **通过绑定表**：`fin_payment_binding` 建立关系
- **通过路由表**：`fin_payment_route` 记录实际使用情况

### 核心边界
**账户 = 资金容器（实例）**  
**支付方式 = 支付能力（类型）**

---

**记住**：账户有余额，支付方式没有余额。账户是用户的，支付方式是系统的。
