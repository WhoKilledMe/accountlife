# 账单文件导入指南

## 一、API接口说明

### 导入账单文件接口

**接口地址**: `POST /api/fin/statement/upload`

**请求参数**:
- `file`: 文件（multipart/form-data）
- `type`: 交易来源类型（TransactionSourceType枚举值）
- `sourceChannel`: 导入渠道（可选，默认：FILE_UPLOAD）

**支持的交易来源类型**:
- `PLATFORM` - 美团账单
- `ALIPAY` - 支付宝账单
- `LABEL_DETAIL` - LabelDetail账单
- `NING_BO_CREDIT` - 宁波银行信用卡账单

**响应示例**:
```json
{
  "id": 1,
  "fileName": "美团账单(20250823-20250923).csv",
  "status": "COMPLETED",
  "totalRows": 51,
  "successCount": 51,
  "failureCount": 0
}
```

## 二、使用curl命令导入文件

### 1. 导入美团账单
```bash
curl -X POST "http://localhost:8080/api/fin/statement/upload" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/美团账单(20250823-20250923).csv" \
  -F "type=PLATFORM" \
  -F "sourceChannel=FILE_UPLOAD"
```

### 2. 导入支付宝账单
```bash
curl -X POST "http://localhost:8080/api/fin/statement/upload" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/支付宝交易明细(20240904-20250903)_副本.csv" \
  -F "type=ALIPAY" \
  -F "sourceChannel=FILE_UPLOAD"
```

### 3. 导入LabelDetail账单
```bash
curl -X POST "http://localhost:8080/api/fin/statement/upload" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/labelDetail.csv" \
  -F "type=LABEL_DETAIL" \
  -F "sourceChannel=FILE_UPLOAD"
```

## 三、使用Postman导入

1. 选择 `POST` 方法
2. URL: `http://localhost:8080/api/fin/statement/upload`
3. Headers: 添加 `Authorization: Bearer YOUR_TOKEN`
4. Body: 选择 `form-data`
   - 添加 `file` 字段（类型：File），选择要上传的文件
   - 添加 `type` 字段（类型：Text），输入对应的类型值
   - 添加 `sourceChannel` 字段（类型：Text，可选），输入 `FILE_UPLOAD`

## 四、查询导入结果

### 1. 查询账单文件列表
```bash
curl -X POST "http://localhost:8080/api/fin/statement/file/page?page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. 根据文件ID查询账单行
```bash
curl -X GET "http://localhost:8080/api/fin/statement/file/1/statements" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. 分页查询账单行
```bash
curl -X POST "http://localhost:8080/api/fin/statement/page?page=0&size=20" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 4. 根据时间范围查询账单行
```bash
curl -X GET "http://localhost:8080/api/fin/statement/range?startTime=2025-08-01%2000:00:00&endTime=2025-09-30%2023:59:59" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 五、文件格式要求

### 美团账单
- 格式：CSV
- 编码：UTF-8
- 元数据行数：19行（自动跳过）
- 表头行：第20行
- 数据起始：第21行

### 支付宝账单
- 格式：CSV
- 编码：UTF-8
- 元数据行数：24行（自动跳过）
- 表头行：第25行
- 数据起始：第26行
- 特殊处理：自动过滤"不计收支"类型交易

### LabelDetail账单
- 格式：CSV
- 编码：UTF-8
- 表头行：第1行
- 数据起始：第2行

## 六、导入流程说明

1. **文件上传**: 用户上传账单文件
2. **MD5校验**: 系统计算文件MD5，检查是否已导入（幂等性）
3. **文件记录**: 创建 `fin_statement_file` 记录
4. **解析文件**: 根据类型选择对应的解析器
5. **生成账单行**: 将解析结果转换为 `fin_statement` 记录
6. **保存数据**: 批量保存账单行到数据库
7. **更新状态**: 更新文件记录状态为 `COMPLETED`

## 七、注意事项

1. **幂等性**: 相同MD5的文件不会重复导入
2. **不计收支**: 支付宝账单中的"不计收支"类型交易会被自动过滤
3. **错误处理**: 如果解析失败，文件状态会更新为 `FAILED`，错误信息保存在 `error_log` 字段
4. **账户引用**: 系统会尝试从支付方式中提取账户信息（如卡号尾号）

## 八、常见问题

### Q1: 文件已导入错误
**A**: 系统通过MD5校验防止重复导入。如果需要重新导入，需要修改文件内容（如添加一个空格）以改变MD5值。

### Q2: 解析失败
**A**: 检查文件格式是否正确，确保：
- 文件编码为UTF-8
- CSV格式正确
- 表头行存在且格式正确

### Q3: 部分数据未导入
**A**: 查看文件记录的 `failureCount` 字段，检查失败原因。常见原因：
- 金额格式错误
- 时间格式错误
- 必填字段缺失

## 九、批量导入脚本示例

```bash
#!/bin/bash

# 配置
API_URL="http://localhost:8080/api/fin/statement/upload"
TOKEN="YOUR_TOKEN"

# 导入美团账单
curl -X POST "$API_URL" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@美团账单(20250823-20250923).csv" \
  -F "type=PLATFORM"

# 导入支付宝账单
curl -X POST "$API_URL" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@支付宝交易明细(20240904-20250903)_副本.csv" \
  -F "type=ALIPAY"

# 导入LabelDetail账单
curl -X POST "$API_URL" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@labelDetail.csv" \
  -F "type=LABEL_DETAIL"
```
