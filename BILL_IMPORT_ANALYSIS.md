# 账单导入系统分析报告

## 一、账单文件格式分析

### 1. 美团账单 (CSV)
**文件路径**: `src/main/resources/csv/美团账单(20250823-20250923).csv`

**格式特点**:
- 文件前19行为元数据（标题、统计信息、提示等）
- 第20行为表头：`交易创建时间,交易成功时间,交易类型,订单标题,收/支,支付方式,订单金额,实付金额,交易单号,商家单号,备注`
- 数据从第21行开始

**现有支持**:
- ✅ 已有 `MeituanCsvParser` (旧版，转换为AccountTransaction)
- ✅ 已有 `MeituanFinStatementParser` (新版，转换为FinStatement)
- ⚠️ **问题**: 解析器直接读取CSV，无法跳过文件头部的19行元数据

**需要调整**:
- 在解析前跳过前19行元数据

### 2. 支付宝账单 (CSV)
**文件路径**: `src/main/resources/csv/支付宝交易明细(20240904-20250903)_副本.csv`

**格式特点**:
- 文件前24行为元数据（导出信息、统计、提示等）
- 第25行为表头：`交易时间,交易分类,交易对方,对方账号,商品说明,收/支,金额,收/付款方式,交易状态,交易订单号,商家订单号,备注,`
- 数据从第26行开始
- 字段包含：交易时间、交易分类、交易对方、对方账号、商品说明、收/支、金额、收/付款方式、交易状态、交易订单号、商家订单号、备注
- 有"不计收支"类型的交易（如余额宝收益、自动转入等）

**现有支持**:
- ❌ **无支付宝专用解析器**
- ❌ `TransactionSourceType` 枚举中没有支付宝类型

**需要调整**:
- 创建支付宝账单解析器
- 跳过前24行元数据
- 处理"不计收支"类型的交易（建议过滤掉，不导入）

### 3. LabelDetail账单 (CSV)
**文件路径**: `src/main/resources/csv/labelDetail.csv`

**格式特点**:
- 第1行为表头：`交易日期,记账日期,交易摘要,交易金额`
- 数据从第2行开始
- 格式简洁，无元数据头部

**现有支持**:
- ✅ 已有 `LabelDetailCsvParser` (旧版，转换为AccountTransaction)
- ✅ 已有 `LabelDetailFinStatementParser` (新版，转换为FinStatement)
- ✅ 格式匹配，无需调整

### 4. 微信支付账单 (XLSX)
**文件路径**: `src/main/resources/xlsx/微信支付账单流水文件(20250101-20250401)——【解压密码可在微信支付公众号查看】.xlsx`

**格式特点**:
- Excel格式（.xlsx）
- 需要密码解压（从微信支付公众号获取）

**现有支持**:
- ❌ **系统目前只支持CSV格式**
- ❌ 无Excel解析器（需要Apache POI或EasyExcel）
- ❌ `TransactionSourceType` 枚举中没有微信支付类型
- ❌ `pom.xml` 中没有Excel解析库依赖

**建议**:
- 暂时不处理，等需要时再添加Excel解析支持

## 二、需要调整的内容

### 优先级1：必须修复

#### 1. 修复美团账单解析器（跳过元数据行）
**文件**: 
- `src/main/java/com/acco/life/service/csv/MeituanFinStatementParser.java`

**需要处理**:
- 在解析前跳过前19行元数据
- 使用BufferedReader跳过指定行数

#### 2. 创建支付宝账单解析器
**文件**: 
- `src/main/java/com/acco/life/dto/FileTransactionAlipay.java` (新建)
- `src/main/java/com/acco/life/service/csv/AlipayFinStatementParser.java` (新建)

**需要处理**:
- 跳过前24行元数据
- 解析字段：交易时间、交易分类、交易对方、商品说明、收/支、金额、收/付款方式、交易状态、交易订单号
- 处理"不计收支"类型的交易（过滤掉，不导入）

#### 3. 更新TransactionSourceType枚举
**文件**: `src/main/java/com/acco/life/enums/TransactionSourceType.java`

**需要添加**:
```java
ALIPAY(5, "支付宝");
```

#### 4. 更新FileTransactionFactory
**文件**: `src/main/java/com/acco/life/factory/FileTransactionFactory.java`

**需要添加**:
```java
fileTransactionMap.put(TransactionSourceType.ALIPAY, FileTransactionAlipay.class);
```

### 优先级2：功能增强（可选）

#### 5. 添加微信支付XLSX解析支持
**依赖**: 需要在 `pom.xml` 中添加Excel解析库
```xml
<!-- Apache POI for Excel -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

**文件**:
- `src/main/java/com/acco/life/dto/FileTransactionWechat.java` (新建)
- `src/main/java/com/acco/life/service/csv/WechatXlsxParser.java` (新建)
- `src/main/java/com/acco/life/service/csv/WechatFinStatementParser.java` (新建)

## 三、导入流程说明

### 当前导入流程（CSV）
1. 用户上传CSV文件 → `FinStatementController.upload()`
2. 根据 `TransactionSourceType` 选择对应的解析器（`FinStatementCsvParser`）
3. 解析器读取CSV文件，转换为 `FileTransactionDto`
4. 转换为 `FinStatement` 实体
5. 保存到数据库

### 新版导入流程（推荐使用FinStatement）
- 使用 `/api/fin/statement/upload` 接口
- 返回 `FinStatement` 对象，支持后续对账和映射

## 四、实施计划

1. ✅ **第一步**: 修复美团账单解析器（跳过元数据行）
2. ✅ **第二步**: 创建支付宝CSV解析器
3. ✅ **第三步**: 更新枚举和工厂类
4. ⚠️ **第四步**: 添加微信支付XLSX支持（可选，如果暂时不需要可以延后）

## 五、测试建议

每个解析器创建后，建议：
1. 使用实际账单文件进行测试
2. 验证元数据行是否正确跳过
3. 验证字段映射是否正确
4. 验证金额和时间的解析是否正确
5. 验证"不计收支"类型的处理逻辑
