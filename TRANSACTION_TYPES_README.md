# 交易类型系统扩展说明

## 概述

本次更新大幅扩展了交易类型系统，从原来的4种基础类型扩展到70种详细类型，提供了更精确的交易分类和更智能的AI推断功能。

## 新增交易类型

### 1. 基础类型 (1-4)
- `INCOME(1, "收入")` - 基础收入类型
- `EXPENSE(2, "支出")` - 基础支出类型  
- `TRANSFER_OUT(3, "转出")` - 基础转出类型
- `TRANSFER_IN(4, "转入")` - 基础转入类型

### 2. 详细收入类型 (11-25)
- `SALARY_INCOME(11, "工资收入")` - 工资、薪水等
- `BONUS_INCOME(12, "奖金收入")` - 奖金、绩效、提成等
- `INVESTMENT_INCOME(13, "投资收益")` - 股票、基金、理财收益
- `INTEREST_INCOME(14, "利息收入")` - 存款利息、债券利息等
- `REFUND_INCOME(15, "退款收入")` - 退款、返现等
- `COMPENSATION_INCOME(17, "赔偿收入")` - 赔偿款等
- `DONATION_INCOME(18, "捐赠收入")` - 接受的捐赠
- `GIFT_INCOME(19, "礼金收入")` - 红包、礼金等
- `LOTTERY_INCOME(20, "中奖收入")` - 彩票中奖等
- `PART_TIME_INCOME(21, "兼职收入")` - 兼职、副业收入
- `BUSINESS_INCOME(22, "经营收入")` - 经营、生意收入
- `RENTAL_INCOME(23, "租金收入")` - 房屋、设备租金
- `ROYALTY_INCOME(24, "版税收入")` - 版权、专利收入
- `COMMISSION_INCOME(25, "佣金收入")` - 销售佣金等

### 3. 详细支出类型 (31-47)
- `FOOD_EXPENSE(31, "餐饮支出")` - 餐饮、美食、外卖等
- `TRANSPORT_EXPENSE(32, "交通支出")` - 地铁、公交、出租车等
- `SHOPPING_EXPENSE(33, "购物支出")` - 超市、商场、网购等
- `MEDICAL_EXPENSE(34, "医疗支出")` - 医院、药店、体检等
- `EDUCATION_EXPENSE(35, "教育支出")` - 学校、培训、课程等
- `ENTERTAINMENT_EXPENSE(36, "娱乐支出")` - 电影、游戏、旅游等
- `HOUSING_EXPENSE(37, "住房支出")` - 房租、房贷、物业费等
- `INVESTMENT_EXPENSE(38, "投资支出")` - 股票、基金、理财等
- `INSURANCE_EXPENSE(39, "保险支出")` - 各类保险费用
- `TAX_EXPENSE(40, "税费支出")` - 个人所得税、增值税等
- `CHARITY_EXPENSE(41, "慈善支出")` - 慈善捐赠等
- `TRAVEL_EXPENSE(42, "旅游支出")` - 旅游、度假等
- `BEAUTY_EXPENSE(43, "美容支出")` - 美容、美发、美甲等
- `FITNESS_EXPENSE(44, "健身支出")` - 健身、运动、瑜伽等
- `PET_EXPENSE(45, "宠物支出")` - 宠物护理、宠物用品等
- `CHILD_EXPENSE(46, "子女支出")` - 子女教育、子女用品等
- `ELDERLY_EXPENSE(47, "赡养支出")` - 赡养老人等

### 4. 转账类型 (51-55)
- `ACCOUNT_TRANSFER(51, "账户转账")` - 账户间转账
- `CARD_TRANSFER(52, "卡片转账")` - 卡片间转账
- `PLATFORM_TRANSFER(53, "平台转账")` - 平台间转账
- `CROSS_BANK_TRANSFER(54, "跨行转账")` - 跨银行转账
- `INTERNATIONAL_TRANSFER(55, "国际转账")` - 国际转账

### 5. 特殊类型 (61-70)
- `LOAN_REPAYMENT(61, "贷款还款")` - 贷款还款
- `CREDIT_CARD_REPAYMENT(62, "信用卡还款")` - 信用卡还款
- `BILL_PAYMENT(63, "账单支付")` - 各类账单支付
- `RECHARGE(64, "充值")` - 账户充值
- `WITHDRAWAL(65, "提现")` - 账户提现
- `EXCHANGE(66, "货币兑换")` - 货币兑换
- `FEE_CHARGE(67, "手续费")` - 各类手续费
- `PENALTY(68, "罚金")` - 各类罚金
- `ADJUSTMENT(69, "调整")` - 账户调整
- `CORRECTION(70, "冲正")` - 交易冲正

## 新增分类类型

### 1. 收入分类 (11-25)
- `SALARY(11, "工资薪金")` - 工资、薪水等
- `BONUS(12, "奖金补贴")` - 奖金、补贴等
- `INVESTMENT_INCOME(13, "投资收益")` - 投资收益
- `INTEREST(14, "利息收入")` - 利息收入
- `BUSINESS(15, "经营收入")` - 经营收入
- `RENTAL(16, "租金收入")` - 租金收入
- `ROYALTY(17, "版税收入")` - 版税收入
- `COMMISSION(18, "佣金收入")` - 佣金收入
- `REFUND(19, "退款返现")` - 退款、返现
- `COMPENSATION(20, "赔偿收入")` - 赔偿收入
- `DONATION(21, "捐赠收入")` - 捐赠收入
- `GIFT(22, "礼金红包")` - 礼金、红包
- `LOTTERY(23, "中奖收入")` - 中奖收入
- `PART_TIME(24, "兼职收入")` - 兼职收入
- `OTHER_INCOME(25, "其他收入")` - 其他收入

### 2. 支出分类 (31-58)
- `FOOD(31, "餐饮美食")` - 餐饮、美食等
- `TRANSPORT(32, "交通出行")` - 交通出行等
- `SHOPPING(33, "购物消费")` - 购物消费等
- `MEDICAL(34, "医疗健康")` - 医疗健康等
- `EDUCATION(35, "教育培训")` - 教育培训等
- `ENTERTAINMENT(36, "娱乐休闲")` - 娱乐休闲等
- `HOUSING(37, "住房生活")` - 住房生活等
- `INVESTMENT(38, "投资理财")` - 投资理财等
- `INSURANCE(39, "保险保障")` - 保险保障等
- `TAX(40, "税费支出")` - 税费支出等
- `CHARITY(41, "慈善捐赠")` - 慈善捐赠等
- `TRAVEL(42, "旅游度假")` - 旅游度假等
- `BEAUTY(43, "美容美体")` - 美容美体等
- `FITNESS(44, "健身运动")` - 健身运动等
- `PET(45, "宠物护理")` - 宠物护理等
- `CHILD(46, "子女教育")` - 子女教育等
- `ELDERLY(47, "赡养老人")` - 赡养老人等
- `DIGITAL(48, "数码科技")` - 数码科技等
- `FURNITURE(49, "家具家居")` - 家具家居等
- `CLOTHING(50, "服装配饰")` - 服装配饰等
- `COSMETICS(51, "化妆品")` - 化妆品等
- `BOOKS(52, "图书文具")` - 图书文具等
- `SPORTS(53, "运动户外")` - 运动户外等
- `GAMING(54, "游戏娱乐")` - 游戏娱乐等
- `SUBSCRIPTION(55, "订阅服务")` - 订阅服务等
- `UTILITIES(56, "水电煤气")` - 水电煤气等
- `MAINTENANCE(57, "维修保养")` - 维修保养等
- `OTHER_EXPENSE(58, "其他支出")` - 其他支出

## 新增功能

### 1. 详细交易类型推断
- `inferDetailedTransactionType()` - 推断详细的交易类型枚举
- `inferDetailedCategoryType()` - 推断详细的分类类型枚举

### 2. 类型查询功能
- `getAllTransactionTypes()` - 获取所有交易类型
- `getAllCategoryTypes()` - 获取所有分类类型
- `getCategoryTypesByTransactionType()` - 根据交易类型获取对应分类

### 3. 智能推荐功能
- `recommendTransactionCategories()` - 智能推荐交易分类
- `batchInferTransactionCategories()` - 批量推断交易分类

### 4. 实用工具方法
- `isIncome()` - 判断是否为收入类型
- `isExpense()` - 判断是否为支出类型
- `isTransfer()` - 判断是否为转账类型
- `isSpecial()` - 判断是否为特殊类型

## 使用示例

```java
// 推断详细交易类型
TransactionType detailedType = aiService.inferDetailedTransactionType("工资发放", "5000.00");
// 返回: SALARY_INCOME

// 推断详细分类类型
CategoryType detailedCategory = aiService.inferDetailedCategoryType("餐饮消费", "-100.00");
// 返回: FOOD

// 获取所有收入分类
CategoryType[] incomeCategories = CategoryType.getIncomeCategories();

// 判断交易类型
if (detailedType.isIncome()) {
    // 处理收入逻辑
}
```

## 优势

1. **更精确的分类**: 从4种类型扩展到70种类型，提供更精确的交易分类
2. **智能推断**: 基于关键词和金额的智能推断，提高分类准确性
3. **批量处理**: 支持批量交易分类推断，提高处理效率
4. **灵活扩展**: 枚举设计支持未来进一步扩展
5. **向后兼容**: 保持原有接口的兼容性

## 注意事项

1. 新的枚举值使用了特定的编码范围，避免与原有值冲突
2. 所有新增方法都提供了默认实现，确保向后兼容
3. 建议在生产环境中逐步迁移到新的详细类型系统
4. 可以根据实际业务需求进一步定制关键词映射规则 