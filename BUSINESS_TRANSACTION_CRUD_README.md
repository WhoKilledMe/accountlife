# BusinessTransaction CRUD 功能说明

## 概述
为 `business_transaction` 表生成了完整的 CRUD 功能，包括后端 API 和前端页面组件。

## 数据库表结构
```sql
CREATE TABLE business_transaction
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主交易ID',
    category_id      BIGINT         NOT NULL COMMENT '交易分类ID',
    user_id          BIGINT         NOT NULL COMMENT '所属用户',
    amount           DECIMAL(18, 2) NOT NULL COMMENT '交易金额',
    transaction_time DATETIME       NOT NULL COMMENT '交易发生时间',
    `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '创建人',
    `created_at` datetime                               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'system' COMMENT '修改人',
    `updated_at` datetime                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted` tinyint                                DEFAULT '0' COMMENT '是否删除（0-否，1-是）',
    index `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='业务交易表';
```

## 后端文件结构

### 1. 实体类 (Entity)
- **文件**: `src/main/java/com/acco/life/entity/BusinessTransaction.java`
- **功能**: 对应数据库表 `business_transaction` 的实体类
- **继承**: `BaseColumnEntity` (包含基础字段：id, createdBy, createdAt, updatedBy, updatedAt, isDeleted)

### 2. 数据传输对象 (DTO)
- **文件**: `src/main/java/com/acco/life/dto/BusinessTransactionDto.java`
- **功能**: 用于 API 数据传输的对象
- **继承**: `BaseColumnDto`
- **特殊字段**: `categoryName` (通过关联查询获取分类名称)

### 3. 仓库接口 (Repository)
- **文件**: `src/main/java/com/acco/life/repository/BusinessTransactionRepository.java`
- **功能**: 提供数据库操作接口
- **继承**: `ReactiveCrudRepository<BusinessTransaction, Long>`
- **自定义查询**: 支持按 userId 和 categoryId 进行分页搜索

### 4. 映射器 (Mapper)
- **文件**: `src/main/java/com/acco/life/mapper/BusinessTransactionMapper.java`
- **功能**: 使用 MapStruct 进行 DTO 与实体之间的转换
- **注解**: `@Mapper(componentModel = "spring")`

### 5. 服务接口 (Service)
- **文件**: `src/main/java/com/acco/life/service/BusinessTransactionService.java`
- **功能**: 定义业务逻辑接口
- **方法**: findAll, findById, save, deleteById, page

### 6. 服务实现类 (ServiceImpl)
- **文件**: `src/main/java/com/acco/life/service/impl/BusinessTransactionServiceImpl.java`
- **功能**: 实现业务逻辑
- **特性**: 
  - 支持分页查询
  - 自动补充分类名称
  - 响应式编程 (Reactive)

### 7. 控制器 (Controller)
- **文件**: `src/main/java/com/acco/life/controller/BusinessTransactionController.java`
- **功能**: 提供 REST API 接口
- **路径**: `/api/businesstransaction`
- **接口**:
  - `GET /` - 查询所有业务交易
  - `POST /page` - 分页查询
  - `GET /{id}` - 根据ID查询
  - `POST /` - 创建业务交易
  - `PUT /` - 更新业务交易
  - `DELETE /{id}` - 删除业务交易

## 前端文件结构

### 1. 类型定义
- **文件**: `src/services/types.ts`
- **新增**: `BusinessTransactionDto` 接口定义

### 2. API 客户端
- **文件**: `src/api/businessTransactions.ts`
- **功能**: 封装后端 API 调用
- **方法**: list, page, get, create, update, delete

### 3. 页面组件
- **文件**: `src/pages/BusinessTransactions.tsx`
- **功能**: 业务交易管理页面
- **特性**:
  - 分页表格显示
  - 创建/编辑/删除功能
  - 分类选择器（支持搜索和分页加载）
  - 时间选择器
  - 模拟数据支持（后端不可用时）

## API 接口说明

### 1. 查询所有业务交易
```
GET /api/businesstransaction
```

### 2. 分页查询业务交易
```
POST /api/businesstransaction/page?page=0&size=10
Content-Type: application/json

{
  "userId": 1,
  "categoryId": 2
}
```

### 3. 根据ID查询业务交易
```
GET /api/businesstransaction/{id}
```

### 4. 创建业务交易
```
POST /api/businesstransaction
Content-Type: application/json

{
  "categoryId": 1,
  "userId": 1,
  "amount": 100.00,
  "transactionTime": "2025-01-15T10:30:00"
}
```

### 5. 更新业务交易
```
PUT /api/businesstransaction
Content-Type: application/json

{
  "id": 1,
  "categoryId": 1,
  "userId": 1,
  "amount": 150.00,
  "transactionTime": "2025-01-15T10:30:00"
}
```

### 6. 删除业务交易
```
DELETE /api/businesstransaction/{id}
```

## 技术特性

### 后端特性
- **响应式编程**: 使用 Spring WebFlux 和 R2DBC
- **自动映射**: 使用 MapStruct 进行对象转换
- **分页查询**: 支持数据库级别的分页和搜索
- **关联查询**: 自动补充分类名称
- **错误处理**: 统一的错误处理机制

### 前端特性
- **React Query**: 数据获取和缓存管理
- **Ant Design**: UI 组件库
- **TypeScript**: 类型安全
- **分页表格**: 支持大数据量展示
- **表单验证**: 完整的表单验证机制
- **模拟数据**: 后端不可用时的降级方案

## 使用说明

1. **启动后端服务**: 确保 Spring Boot 应用正常运行
2. **访问前端页面**: 导航到 BusinessTransactions 页面
3. **创建业务交易**: 点击"新建业务交易"按钮
4. **编辑业务交易**: 点击表格中的编辑按钮
5. **删除业务交易**: 点击表格中的删除按钮
6. **搜索功能**: 使用搜索框进行快速查找

## 注意事项

1. 所有时间字段使用 ISO 8601 格式
2. 金额字段使用 BigDecimal 类型确保精度
3. 分类名称通过关联查询动态获取
4. 支持软删除机制（is_deleted 字段）
5. 前端包含模拟数据，确保在后端不可用时仍能正常演示
