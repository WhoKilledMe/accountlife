# 账单导入系统修复总结

## 已完成的修复

### 1. ✅ 创建CSV工具类
**文件**: `src/main/java/com/acco/life/util/CsvUtil.java`

**功能**: 
- 提供 `skipLines()` 方法，支持跳过CSV文件头部指定行数的元数据
- 使用ByteArrayInputStream将剩余内容转换为新的输入流

### 2. ✅ 修复美团账单解析器
**文件**: `src/main/java/com/acco/life/service/csv/MeituanFinStatementParser.java`

**修复内容**:
- 在解析前跳过前19行元数据
- 从第20行（表头）开始解析数据

### 3. ✅ 创建支付宝账单DTO
**文件**: `src/main/java/com/acco/life/dto/FileTransactionAlipay.java`

**字段映射**:
- 交易时间 -> tradeTime
- 交易分类 -> tradeCategory
- 交易对方 -> counterparty
- 对方账号 -> counterpartyAccount
- 商品说明 -> productDescription
- 收/支 -> incomeOrExpense
- 金额 -> amount
- 收/付款方式 -> paymentMethod
- 交易状态 -> tradeStatus
- 交易订单号 -> tradeOrderNo
- 商家订单号 -> merchantOrderNo
- 备注 -> remark

### 4. ✅ 创建支付宝账单解析器
**文件**: `src/main/java/com/acco/life/service/csv/AlipayFinStatementParser.java`

**功能**:
- 跳过前24行元数据
- 解析支付宝账单字段
- 过滤"不计收支"类型的交易（如余额宝收益、自动转入等）
- 正确解析金额和方向（收入/支出）
- 从支付方式中提取账户引用（如卡号尾号）

### 5. ✅ 更新TransactionSourceType枚举
**文件**: `src/main/java/com/acco/life/enums/TransactionSourceType.java`

**新增**:
```java
ALIPAY(5, "支付宝");
```

### 6. ✅ 更新FileTransactionFactory
**文件**: `src/main/java/com/acco/life/factory/FileTransactionFactory.java`

**新增映射**:
```java
fileTransactionMap.put(TransactionSourceType.ALIPAY, FileTransactionAlipay.class);
```

### 7. ✅ 修复LabelDetail方向判断逻辑
**文件**: `src/main/java/com/acco/life/service/csv/LabelDetailFinStatementParser.java`

**修复内容**:
- 修正方向判断逻辑：正数（+）表示收入（IN），负数（-）表示支出（OUT）

## 账单文件格式说明

### 美团账单
- **元数据行数**: 19行
- **表头行**: 第20行
- **数据起始**: 第21行
- **状态**: ✅ 已修复

### 支付宝账单
- **元数据行数**: 24行
- **表头行**: 第25行
- **数据起始**: 第26行
- **特殊处理**: 过滤"不计收支"类型交易
- **状态**: ✅ 已支持

### LabelDetail账单
- **元数据行数**: 0行
- **表头行**: 第1行
- **数据起始**: 第2行
- **状态**: ✅ 已支持（无需调整）

### 微信支付账单（XLSX）
- **格式**: Excel (.xlsx)
- **状态**: ⚠️ 暂不支持（需要添加Excel解析库）

## 使用说明

### 导入美团账单
```java
// 使用 TransactionSourceType.PLATFORM
// 系统会自动使用 MeituanFinStatementParser
```

### 导入支付宝账单
```java
// 使用 TransactionSourceType.ALIPAY
// 系统会自动使用 AlipayFinStatementParser
```

### 导入LabelDetail账单
```java
// 使用 TransactionSourceType.LABEL_DETAIL
// 系统会自动使用 LabelDetailFinStatementParser
```

## 注意事项

1. **不计收支交易**: 支付宝账单中的"不计收支"类型交易（如余额宝收益、自动转入等）会被自动过滤，不会导入到系统中。

2. **账户引用提取**: 支付宝解析器会尝试从支付方式中提取账户信息，如"宁波银行信用卡(4573)"会提取出"4573"作为账户引用。

3. **方向判断**:
   - 美团账单：默认都是支出（OUT）
   - 支付宝账单：根据"收/支"字段判断
   - LabelDetail账单：根据金额正负判断（正数=收入，负数=支出）

4. **时间格式**: 所有解析器都支持 `yyyy-MM-dd HH:mm:ss` 和 `yyyy-MM-dd` 格式。

## 后续优化建议

1. **微信支付支持**: 如需支持微信支付XLSX格式，需要：
   - 添加Apache POI或EasyExcel依赖
   - 创建WechatXlsxParser
   - 处理密码保护的ZIP文件解压

2. **错误处理**: 可以增强错误处理，记录跳过的不计收支交易数量。

3. **性能优化**: 对于大文件，可以考虑流式处理而不是一次性读取所有内容。
