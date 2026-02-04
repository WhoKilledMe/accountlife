# 数据表审查报告

## 审查范围
- `transaction_category.sql` - 交易分类表（59行）
- `category_keyword_mapping.sql` - 分类关键词映射表（4391行）

## 发现的问题

### 1. ⚠️ 字段名不一致（重要）

**问题描述：**
- `category_keyword_mapping.sql` 使用的字段名是 `created_time` 和 `updated_time`
- 但 `CategoryKeywordMapping` 实体类继承自 `BaseColumnEntity`，而 `BaseColumnEntity` 使用的是 `created_at` 和 `updated_at`
- 这会导致实体类无法正确映射数据库字段

**影响：**
- 实体类读取/写入时可能无法正确映射时间字段
- 可能导致数据不一致或查询失败

**建议修复：**
```sql
-- 方案1：修改SQL文件，统一使用 created_at/updated_at（推荐）
-- 将 category_keyword_mapping.sql 中的：
-- created_time -> created_at
-- updated_time -> updated_at

-- 方案2：如果数据库表已经是 created_time/updated_time，则修改实体类
-- 在 CategoryKeywordMapping 中覆盖字段映射
```

### 2. ⚠️ 数据库名硬编码

**问题描述：**
- SQL 文件中硬编码了数据库名 `account_life.`
- 这会导致在不同环境（开发/测试/生产）部署时需要手动修改

**影响：**
- 降低SQL的可移植性
- 增加部署复杂度

**建议修复：**
```sql
-- 移除数据库名前缀，让SQL更通用
-- 从：insert into account_life.transaction_category
-- 改为：insert into transaction_category
```

### 3. ✅ category_id 范围检查（正常）

**检查结果：**
- `transaction_category` 表中有 id: 1-58（包括测试分类58）
- `category_keyword_mapping` 中使用的 category_id 范围: 1-57
- **结论：** 所有映射的 category_id 都在分类表中存在，数据一致性良好
- 分类58（测试分类）没有关键词映射是合理的

### 4. ✅ 数据完整性检查（正常）

**检查结果：**
- 所有系统分类（1-57）都有对应的关键词映射
- 关键词覆盖全面，权重设置合理（1-3）
- 用户自定义分类（58）没有系统关键词映射，符合预期

### 5. ⚠️ 测试数据清理建议

**问题描述：**
- `transaction_category` 表中包含一条测试数据（id=58, name='测试分类'）
- 该分类属于用户3，但可能不应该出现在系统初始化SQL中

**建议：**
- 如果这是测试数据，建议从初始化SQL中移除
- 或者明确标注为示例数据，并添加注释说明

## 修复建议优先级

### 高优先级（必须修复）
1. **字段名不一致** - 会影响实体类映射，必须修复

### 中优先级（建议修复）
2. **数据库名硬编码** - 影响可移植性，建议修复

### 低优先级（可选）
3. **测试数据清理** - 根据实际需求决定是否保留

## 修复SQL示例

### 修复字段名不一致
```sql
-- 如果数据库表已经是 created_time/updated_time，需要修改实体类
-- 如果数据库表可以修改，建议统一改为 created_at/updated_at
```

### 移除数据库名硬编码
```sql
-- transaction_category.sql
-- 修改前：
insert into account_life.transaction_category (...)
-- 修改后：
insert into transaction_category (...)

-- category_keyword_mapping.sql
-- 修改前：
insert into account_life.category_keyword_mapping (...)
-- 修改后：
insert into category_keyword_mapping (...)
```

## 数据质量评估

### 优点
✅ 分类覆盖全面（收入15种、支出36种、转账5种、转入1种）  
✅ 关键词映射丰富（4390条映射记录）  
✅ category_id 外键引用完整，无孤立数据  
✅ 权重设置合理，便于匹配优先级控制  

### 待改进
⚠️ 字段命名需要与实体类保持一致  
⚠️ SQL可移植性需要提升（移除硬编码数据库名）  

## 总结

整体数据质量良好，主要问题集中在：
1. 字段名不一致（需要立即修复）
2. 数据库名硬编码（建议修复以提升可移植性）

修复这些问题后，数据表可以正常使用。
