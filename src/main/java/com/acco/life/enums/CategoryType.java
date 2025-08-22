package com.acco.life.enums;

import com.acco.life.enums.TransactionType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description: 分类类型枚举，定义了各种详细的分类类型
 * 基于quick-insert-categories.sql文件生成
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public enum CategoryType {
    // ==================== 收入分类 (type = 1) ====================
    // 基础收入分类
    SALARY(1, "工资薪金", TransactionType.INCOME, null),
    BONUS(2, "奖金补贴", TransactionType.INCOME, null),
    INVESTMENT_INCOME(3, "投资收益", TransactionType.INCOME, null),
    INTEREST(4, "利息收入", TransactionType.INCOME, null),
    BUSINESS(5, "经营收入", TransactionType.INCOME, null),
    RENTAL(6, "租金收入", TransactionType.INCOME, null),
    ROYALTY(7, "版税收入", TransactionType.INCOME, null),
    COMMISSION(8, "佣金收入", TransactionType.INCOME, null),
    REFUND(9, "退款返现", TransactionType.INCOME, null),
    COMPENSATION(10, "赔偿收入", TransactionType.INCOME, null),
    DONATION(11, "捐赠收入", TransactionType.INCOME, null),
    GIFT(12, "礼金红包", TransactionType.INCOME, null),
    LOTTERY(13, "中奖收入", TransactionType.INCOME, null),
    PART_TIME(14, "兼职收入", TransactionType.INCOME, null),
    OTHER_INCOME(15, "其他收入", TransactionType.INCOME, null),
    
    // 工资薪金子类
    SALARY_BASE(101, "工资", TransactionType.INCOME, SALARY),
    SALARY_BONUS(102, "年终奖", TransactionType.INCOME, SALARY),
    SALARY_OVERTIME(103, "加班费", TransactionType.INCOME, SALARY),
    SALARY_PERFORMANCE(104, "绩效", TransactionType.INCOME, SALARY),
    
    // 奖金补贴子类
    BONUS_SUBSIDY(121, "补贴", TransactionType.INCOME, BONUS),
    BONUS_ALLOWANCE(122, "津贴", TransactionType.INCOME, BONUS),
    BONUS_REIMBURSEMENT(123, "报销入账", TransactionType.INCOME, BONUS),
    
    // ==================== 支出分类 (type = 2) ====================
    // 基础支出分类
    FOOD(16, "餐饮美食", TransactionType.EXPENSE, null),
    TRANSPORT(17, "交通出行", TransactionType.EXPENSE, null),
    SHOPPING(18, "购物消费", TransactionType.EXPENSE, null),
    MEDICAL(19, "医疗健康", TransactionType.EXPENSE, null),
    EDUCATION(20, "教育培训", TransactionType.EXPENSE, null),
    ENTERTAINMENT(21, "娱乐休闲", TransactionType.EXPENSE, null),
    HOUSING(22, "住房生活", TransactionType.EXPENSE, null),
    INVESTMENT(23, "投资理财", TransactionType.EXPENSE, null),
    INSURANCE(24, "保险保障", TransactionType.EXPENSE, null),
    TAX(25, "税费支出", TransactionType.EXPENSE, null),
    CHARITY(26, "慈善捐赠", TransactionType.EXPENSE, null),
    TRAVEL(27, "旅游度假", TransactionType.EXPENSE, null),
    BEAUTY(28, "美容美体", TransactionType.EXPENSE, null),
    FITNESS(29, "健身运动", TransactionType.EXPENSE, null),
    PET(30, "宠物护理", TransactionType.EXPENSE, null),
    CHILD(31, "子女教育", TransactionType.EXPENSE, null),
    ELDERLY(32, "赡养老人", TransactionType.EXPENSE, null),
    DIGITAL(33, "数码科技", TransactionType.EXPENSE, null),
    FURNITURE(34, "家具家居", TransactionType.EXPENSE, null),
    CLOTHING(35, "服装配饰", TransactionType.EXPENSE, null),
    COSMETICS(36, "化妆品", TransactionType.EXPENSE, null),
    BOOKS(37, "图书文具", TransactionType.EXPENSE, null),
    SPORTS(38, "运动户外", TransactionType.EXPENSE, null),
    GAMING(39, "游戏娱乐", TransactionType.EXPENSE, null),
    SUBSCRIPTION(40, "订阅服务", TransactionType.EXPENSE, null),
    UTILITIES(41, "水电煤气", TransactionType.EXPENSE, null),
    MAINTENANCE(42, "维修保养", TransactionType.EXPENSE, null),
    LOAN_REPAYMENT(43, "贷款还款", TransactionType.EXPENSE, null),
    CREDIT_CARD_REPAYMENT(44, "信用卡还款", TransactionType.EXPENSE, null),
    BILL_PAYMENT(45, "账单支付", TransactionType.EXPENSE, null),
    RECHARGE(46, "充值", TransactionType.EXPENSE, null),
    WITHDRAWAL(47, "提现", TransactionType.EXPENSE, null),
    EXCHANGE(48, "货币兑换", TransactionType.EXPENSE, null),
    FEE_CHARGE(49, "手续费", TransactionType.EXPENSE, null),
    PENALTY(50, "罚金", TransactionType.EXPENSE, null),
    OTHER_EXPENSE(51, "其他支出", TransactionType.EXPENSE, null),
    
    // 新增基础支出分类
    SUPERMARKET(58, "超市日用", TransactionType.EXPENSE, null),
    COMMUNICATION(59, "通讯网络", TransactionType.EXPENSE, null),
    VEHICLE_MAINTENANCE(60, "车辆维护", TransactionType.EXPENSE, null),
    FINANCIAL_EXPENSE(61, "金融支出", TransactionType.EXPENSE, null),
    
    // 餐饮美食子类
    FOOD_MEALS(1601, "三餐", TransactionType.EXPENSE, FOOD),
    FOOD_TAKEOUT(1602, "外卖", TransactionType.EXPENSE, FOOD),
    FOOD_DRINKS(1603, "饮品咖啡", TransactionType.EXPENSE, FOOD),
    FOOD_SNACKS(1604, "零食", TransactionType.EXPENSE, FOOD),
    FOOD_LATE_NIGHT(1605, "夜宵", TransactionType.EXPENSE, FOOD),
    FOOD_ALCOHOL(1606, "酒水", TransactionType.EXPENSE, FOOD),
    
    // 交通出行子类
    TRANSPORT_PUBLIC(2301, "公交地铁", TransactionType.EXPENSE, TRANSPORT),
    TRANSPORT_TAXI(2302, "打车网约车", TransactionType.EXPENSE, TRANSPORT),
    TRANSPORT_FUEL(2303, "加油充电", TransactionType.EXPENSE, TRANSPORT),
    TRANSPORT_PARKING(2304, "停车", TransactionType.EXPENSE, TRANSPORT),
    TRANSPORT_TOLL(2305, "过路费", TransactionType.EXPENSE, TRANSPORT),
    TRANSPORT_SHARING(2306, "共享出行", TransactionType.EXPENSE, TRANSPORT),
    
    // 住房生活子类
    HOUSING_RENT(2201, "房租", TransactionType.EXPENSE, HOUSING),
    HOUSING_PROPERTY(2203, "物业费", TransactionType.EXPENSE, HOUSING),
    HOUSING_CLEANING(2204, "家政保洁", TransactionType.EXPENSE, HOUSING),
    HOUSING_REPAIR(2205, "房屋维修", TransactionType.EXPENSE, HOUSING),
    
    // 通讯网络子类
    COMMUNICATION_PHONE(3301, "话费", TransactionType.EXPENSE, COMMUNICATION),
    COMMUNICATION_DATA(3302, "流量包", TransactionType.EXPENSE, COMMUNICATION),
    COMMUNICATION_BROADBAND(3303, "宽带", TransactionType.EXPENSE, COMMUNICATION),
    
    // 订阅服务子类
    SUBSCRIPTION_MEDIA(3401, "音乐视频", TransactionType.EXPENSE, SUBSCRIPTION),
    SUBSCRIPTION_READING(3402, "阅读", TransactionType.EXPENSE, SUBSCRIPTION),
    
    // ==================== 转账分类 ====================
    // 转出分类 (type = 3)
    ACCOUNT_TRANSFER(52, "账户转账", TransactionType.TRANSFER_OUT, null),
    CARD_TRANSFER(53, "卡片转账", TransactionType.TRANSFER_OUT, null),
    PLATFORM_TRANSFER(54, "平台转账", TransactionType.TRANSFER_OUT, null),
    CROSS_BANK_TRANSFER(55, "跨行转账", TransactionType.TRANSFER_OUT, null),
    INTERNATIONAL_TRANSFER(56, "国际转账", TransactionType.TRANSFER_OUT, null),
    
    // 转入分类 (type = 4)
    TRANSFER_IN(57, "转入", TransactionType.TRANSFER_IN, null),
    
    // 账户转账子类
    ACCOUNT_TRANSFER_SAVINGS(5201, "活期⇄理财", TransactionType.TRANSFER_OUT, ACCOUNT_TRANSFER),
    ACCOUNT_TRANSFER_CASH(5202, "现金⇄银行卡", TransactionType.TRANSFER_OUT, ACCOUNT_TRANSFER),
    ACCOUNT_TRANSFER_PLATFORM(5203, "储蓄卡⇄支付平台", TransactionType.TRANSFER_OUT, ACCOUNT_TRANSFER),
    
    // 转入子类
    TRANSFER_IN_RED_PACKET(5701, "收红包", TransactionType.TRANSFER_IN, TRANSFER_IN),
    TRANSFER_IN_REFUND(5702, "退款入账", TransactionType.TRANSFER_IN, TRANSFER_IN);

    public final int code;
    public final String name;
    public final TransactionType transactionType;
    public final CategoryType parent;

    CategoryType(int code, String name, TransactionType transactionType, CategoryType parent) {
        this.code = code;
        this.name = name;
        this.transactionType = transactionType;
        this.parent = parent;
    }
    
    /**
     * 根据代码获取分类类型
     */
    public static CategoryType getByCode(int code) {
        for (CategoryType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 根据数据库值获取分类类型（兼容数据库存储的整数值）
     */
    public static CategoryType fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        return getByCode(value);
    }
    
    /**
     * 获取分类类型的数据库值
     */
    public Integer getValue() {
        return this.code;
    }
    
    /**
     * 根据名称获取分类类型
     */
    public static CategoryType getByName(String name) {
        for (CategoryType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 判断是否为收入分类
     */
    public boolean isIncome() {
        return this.transactionType == TransactionType.INCOME;
    }
    
    /**
     * 判断是否为支出分类
     */
    public boolean isExpense() {
        return this.transactionType == TransactionType.EXPENSE;
    }
    
    /**
     * 判断是否为转账分类
     */
    public boolean isTransfer() {
        return this.transactionType == TransactionType.TRANSFER_OUT || this.transactionType == TransactionType.TRANSFER_IN;
    }
    
    /**
     * 判断是否为父类分类
     */
    public boolean isParent() {
        return this.parent == null;
    }
    
    /**
     * 判断是否为子类分类
     */
    public boolean isChild() {
        return this.parent != null;
    }
    
    /**
     * 获取父类分类
     */
    public CategoryType getParent() {
        return this.parent;
    }
    
    /**
     * 获取所有子类分类
     */
    public List<CategoryType> getChildren() {
        return Arrays.stream(values())
                .filter(type -> this.equals(type.parent))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有收入分类
     */
    public static List<CategoryType> getIncomeCategories() {
        return Arrays.stream(values())
                .filter(CategoryType::isIncome)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有支出分类
     */
    public static List<CategoryType> getExpenseCategories() {
        return Arrays.stream(values())
                .filter(CategoryType::isExpense)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有转账分类
     */
    public static List<CategoryType> getTransferCategories() {
        return Arrays.stream(values())
                .filter(CategoryType::isTransfer)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有父类分类
     */
    public static List<CategoryType> getParentCategories() {
        return Arrays.stream(values())
                .filter(CategoryType::isParent)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有子类分类
     */
    public static List<CategoryType> getChildCategories() {
        return Arrays.stream(values())
                .filter(CategoryType::isChild)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据交易类型获取对应的分类类型
     */
    public static List<CategoryType> getByTransactionType(TransactionType transactionType) {
        return Arrays.stream(values())
                .filter(type -> type.transactionType == transactionType)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据父类获取所有子类
     */
    public static List<CategoryType> getChildrenByParent(CategoryType parent) {
        return Arrays.stream(values())
                .filter(type -> parent.equals(type.parent))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取分类的完整路径（父类 -> 子类）
     */
    public String getFullPath() {
        if (this.parent == null) {
            return this.name;
        } else {
            return this.parent.name + " -> " + this.name;
        }
    }
}
