# 交易分类数据插入执行指南

## 概述

本指南将帮助您将57种交易分类数据插入到MySQL数据库中，这些分类基于`CategoryType`枚举和`TransactionType`枚举的关系设计。

## 文件说明

### 1. `complete-init.sql` - 完整初始化脚本
- **用途**: 完整的数据库初始化，包括表结构创建和所有数据插入
- **适用场景**: 全新环境部署，需要完整的数据库结构
- **包含内容**: 
  - 表结构创建（user, user_group, asset_account, transaction_category, account_transaction）
  - 基础用户数据
  - 57种交易分类数据
  - 测试账户数据

### 2. `quick-insert-categories.sql` - 快速分类插入脚本
- **用途**: 仅插入交易分类数据，不创建表结构
- **适用场景**: 已有数据库环境，只需要添加分类数据
- **包含内容**: 57种交易分类的INSERT语句

### 3. `transaction-category-data.sql` - 原始分类数据脚本
- **用途**: 原始的详细分类数据插入脚本
- **适用场景**: 需要查看详细的数据结构和注释
- **包含内容**: 详细的分类数据，包含完整注释

### 4. `test-connection.sql` - 连接测试脚本
- **用途**: 验证数据库连接和检查数据完整性
- **适用场景**: 执行数据插入后的验证
- **包含内容**: 10个测试查询，验证数据正确性

## 执行步骤

### 步骤1: 环境准备
确保您有以下环境：
- MySQL 5.7+ 或 MariaDB 10.2+
- 足够的数据库权限（CREATE, INSERT, SELECT）
- 目标数据库已创建

### 步骤2: 选择执行脚本

#### 选项A: 全新环境（推荐）
```bash
mysql -u your_username -p your_database < complete-init.sql
```

#### 选项B: 已有环境，仅添加分类
```bash
mysql -u your_username -p your_database < quick-insert-categories.sql
```

### 步骤3: 验证执行结果
```bash
mysql -u your_username -p your_database < test-connection.sql
```

## 预期结果

执行成功后，您应该看到：

### 分类数量统计
- 收入分类: 15个
- 支出分类: 36个  
- 转出分类: 5个
- 转入分类: 1个
- **总计: 57个**

### 分类类型映射
- `type = 1`: 收入类（工资薪金、奖金补贴、投资收益等）
- `type = 2`: 支出类（餐饮美食、交通出行、购物消费等）
- `type = 3`: 转出类（账户转账、卡片转账、平台转账等）
- `type = 4`: 转入类（转入）

## 常见问题解决

### 问题1: 权限不足
```
ERROR 1142 (42000): CREATE command denied to user
```
**解决方案**: 联系数据库管理员获取CREATE权限，或使用已有表结构

### 问题2: 表已存在
```
ERROR 1050 (42S01): Table 'transaction_category' already exists
```
**解决方案**: 使用`quick-insert-categories.sql`脚本，或先删除现有表

### 问题3: 外键约束失败
```
ERROR 1452 (23000): Cannot add or update a child row
```
**解决方案**: 确保先创建了user表，或修改外键约束

### 问题4: 重复键值
```
ERROR 1062 (23000): Duplicate entry for key
```
**解决方案**: 使用`INSERT IGNORE`语句，或先清空现有数据

## 数据验证查询

### 验证分类总数
```sql
SELECT COUNT(*) as total_categories FROM transaction_category WHERE user_id IS NULL;
```

### 验证分类类型分布
```sql
SELECT 
    CASE type 
        WHEN 1 THEN '收入'
        WHEN 2 THEN '支出'
        WHEN 3 THEN '转出'
        WHEN 4 THEN '转入'
    END as category_type,
    COUNT(*) as count
FROM transaction_category 
WHERE user_id IS NULL
GROUP BY type
ORDER BY type;
```

### 验证特定分类
```sql
SELECT * FROM transaction_category 
WHERE name LIKE '%餐饮%' OR name LIKE '%交通%'
ORDER BY type, sort_order;
```

## 回滚方案

如果需要回滚数据，可以使用以下SQL：

### 删除所有系统分类
```sql
DELETE FROM transaction_category WHERE user_id IS NULL;
```

### 删除特定类型分类
```sql
DELETE FROM transaction_category WHERE user_id IS NULL AND type = 2; -- 删除支出类
```

### 重置自增ID（谨慎使用）
```sql
ALTER TABLE transaction_category AUTO_INCREMENT = 1;
```

## 注意事项

1. **备份数据**: 执行前请备份现有数据
2. **测试环境**: 建议先在测试环境执行
3. **权限检查**: 确保有足够的数据库权限
4. **外键约束**: 注意表之间的依赖关系
5. **数据一致性**: 验证插入后的数据完整性

## 联系支持

如果在执行过程中遇到问题，请：
1. 检查错误日志
2. 验证数据库版本兼容性
3. 确认权限设置
4. 查看本文档的常见问题部分

---

**最后更新**: 2025-01-27  
**版本**: 1.0.0  
**作者**: AccountLife开发团队 