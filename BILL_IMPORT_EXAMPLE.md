# 账单导入使用示例

## 一、使用API接口导入（推荐）

### 1. 使用curl命令

```bash
# 导入美团账单
curl -X POST "http://localhost:8080/api/fin/statement/upload" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@src/main/resources/csv/美团账单(20250823-20250923).csv" \
  -F "type=PLATFORM"

# 导入支付宝账单
curl -X POST "http://localhost:8080/api/fin/statement/upload" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@src/main/resources/csv/支付宝交易明细(20240904-20250903)_副本.csv" \
  -F "type=ALIPAY"

# 导入LabelDetail账单
curl -X POST "http://localhost:8080/api/fin/statement/upload" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@src/main/resources/csv/labelDetail.csv" \
  -F "type=LABEL_DETAIL"
```

### 2. 使用Postman

1. 创建新的POST请求
2. URL: `http://localhost:8080/api/fin/statement/upload`
3. Headers: 添加 `Authorization: Bearer YOUR_TOKEN`
4. Body: 选择 `form-data`
   - `file`: File类型，选择要上传的文件
   - `type`: Text类型，输入 `PLATFORM`、`ALIPAY` 或 `LABEL_DETAIL`
   - `sourceChannel`: Text类型（可选），输入 `FILE_UPLOAD`

## 二、使用Java工具类导入

### 1. 单个文件导入

```java
@Autowired
private BillImportUtil billImportUtil;

public void importSingleFile() {
    String filePath = "src/main/resources/csv/美团账单(20250823-20250923).csv";
    Long userId = 1L;
    
    Mono<BillImportUtil.ImportResult> result = billImportUtil.importBillFile(
        filePath, 
        TransactionSourceType.PLATFORM, 
        userId
    );
    
    result.subscribe(
        importResult -> {
            System.out.println("导入成功: " + importResult);
            System.out.println("总行数: " + importResult.getTotalRows());
            System.out.println("成功: " + importResult.getSuccessCount());
            System.out.println("失败: " + importResult.getFailureCount());
        },
        error -> {
            System.err.println("导入失败: " + error.getMessage());
        }
    );
}
```

### 2. 批量文件导入

```java
@Autowired
private BillImportUtil billImportUtil;

public void importMultipleFiles() {
    List<String> filePaths = Arrays.asList(
        "src/main/resources/csv/美团账单(20250823-20250923).csv",
        "src/main/resources/csv/支付宝交易明细(20240904-20250903)_副本.csv",
        "src/main/resources/csv/labelDetail.csv"
    );
    Long userId = 1L;
    
    // 导入美团账单
    billImportUtil.importBillFile(
        filePaths.get(0), 
        TransactionSourceType.PLATFORM, 
        userId
    ).subscribe(result -> System.out.println("美团: " + result));
    
    // 导入支付宝账单
    billImportUtil.importBillFile(
        filePaths.get(1), 
        TransactionSourceType.ALIPAY, 
        userId
    ).subscribe(result -> System.out.println("支付宝: " + result));
    
    // 导入LabelDetail账单
    billImportUtil.importBillFile(
        filePaths.get(2), 
        TransactionSourceType.LABEL_DETAIL, 
        userId
    ).subscribe(result -> System.out.println("LabelDetail: " + result));
}
```

## 三、查询导入结果

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

### 4. 根据时间范围查询

```bash
curl -X GET "http://localhost:8080/api/fin/statement/range?startTime=2025-08-01%2000:00:00&endTime=2025-09-30%2023:59:59" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 四、完整导入流程示例

```java
@Service
public class BillImportService {
    
    @Autowired
    private BillImportUtil billImportUtil;
    
    /**
     * 导入所有账单文件
     */
    public void importAllBills(Long userId) {
        // 1. 导入美团账单
        billImportUtil.importBillFile(
            "src/main/resources/csv/美团账单(20250823-20250923).csv",
            TransactionSourceType.PLATFORM,
            userId
        ).subscribe(
            result -> {
                if (result.isSuccess()) {
                    log.info("美团账单导入成功: {}", result);
                } else {
                    log.error("美团账单导入失败: {}", result.getMessage());
                }
            }
        );
        
        // 2. 导入支付宝账单
        billImportUtil.importBillFile(
            "src/main/resources/csv/支付宝交易明细(20240904-20250903)_副本.csv",
            TransactionSourceType.ALIPAY,
            userId
        ).subscribe(
            result -> {
                if (result.isSuccess()) {
                    log.info("支付宝账单导入成功: {}", result);
                } else {
                    log.error("支付宝账单导入失败: {}", result.getMessage());
                }
            }
        );
        
        // 3. 导入LabelDetail账单
        billImportUtil.importBillFile(
            "src/main/resources/csv/labelDetail.csv",
            TransactionSourceType.LABEL_DETAIL,
            userId
        ).subscribe(
            result -> {
                if (result.isSuccess()) {
                    log.info("LabelDetail账单导入成功: {}", result);
                } else {
                    log.error("LabelDetail账单导入失败: {}", result.getMessage());
                }
            }
        );
    }
}
```

## 五、注意事项

1. **文件路径**: 确保文件路径正确，可以使用绝对路径或相对路径
2. **用户ID**: 确保用户ID有效，导入的账单会关联到该用户
3. **幂等性**: 相同MD5的文件不会重复导入，如需重新导入需要修改文件内容
4. **错误处理**: 导入失败时，检查文件格式和编码是否正确
5. **异步处理**: 使用Mono时，记得调用subscribe()来触发执行

## 六、导入后的数据

导入成功后，数据会保存在以下表中：

1. **fin_statement_file**: 文件导入记录
   - 记录文件信息、导入状态、统计信息等

2. **fin_statement**: 账单行记录
   - 每条交易记录对应一条账单行
   - 包含金额、时间、方向、对手方等信息

3. **后续处理**: 
   - 账单行可以映射到账户（fin_statement_account_map）
   - 账单行可以关联到交易（fin_transaction_statement_map）
   - 可以生成清算流水（fin_clearing_flow）
   - 可以生成总账流水（fin_account_flow）
