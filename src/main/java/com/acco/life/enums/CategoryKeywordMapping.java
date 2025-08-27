package com.acco.life.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description: 分类关键词映射枚举，建立关键词与分类的映射关系
 * 注意：此枚举已迁移到数据库表 category_keyword_mapping，保留用于向后兼容
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Deprecated
public enum CategoryKeywordMapping {
    // 收入分类关键词映射
    SALARY_KEYWORDS("工资薪金", CategoryType.SALARY, Arrays.asList(
        "工资", "薪水", "月薪", "年薪", "基本工资", "岗位工资", "绩效工资",  "薪资"
    )),
    
    BONUS_KEYWORDS("奖金补贴", CategoryType.BONUS, Arrays.asList(
        "奖金", "绩效", "提成", "补贴", "津贴", "年终奖", "季度奖", "月度奖", "销售提成", "业务提成"
    )),
    
    INVESTMENT_INCOME_KEYWORDS("投资收益", CategoryType.INVESTMENT_INCOME, Arrays.asList(
        "投资", "股票", "基金", "理财", "债券", "期货", "外汇", "黄金", "白银", "原油", "收益", "分红"
    )),
    
    INTEREST_KEYWORDS("利息收入", CategoryType.INTEREST, Arrays.asList(
        "利息", "存款利息", "定期利息", "活期利息", "债券利息", "贷款利息", "复利"
    )),
    
    BUSINESS_KEYWORDS("经营收入", CategoryType.BUSINESS, Arrays.asList(
        "经营", "生意", "买卖", "销售", "营业额", "营业收入", "经营利润", "商业收入"
    )),
    
    RENTAL_KEYWORDS("租金收入", CategoryType.RENTAL, Arrays.asList(
        "租金", "房租", "地租", "设备租金", "房屋出租", "土地出租", "租赁收入"
    )),
    
    ROYALTY_KEYWORDS("版税收入", CategoryType.ROYALTY, Arrays.asList(
        "版税", "版权", "专利", "知识产权", "著作权", "专利权", "商标权"
    )),
    
    COMMISSION_KEYWORDS("佣金收入", CategoryType.COMMISSION, Arrays.asList(
        "佣金", "中介费", "介绍费", "代理费", "手续费", "服务费"
    )),
    
    REFUND_KEYWORDS("退款返现", CategoryType.REFUND, Arrays.asList(
        "退款", "返现", "返利", "返点", "返券", "退费", "退钱", "返还"
    )),
    
    COMPENSATION_KEYWORDS("赔偿收入", CategoryType.COMPENSATION, Arrays.asList(
        "赔偿", "补偿", "赔款", "补偿款", "赔偿金", "补偿金"
    )),
    
    DONATION_KEYWORDS("捐赠收入", CategoryType.DONATION, Arrays.asList(
        "捐赠", "捐助", "捐款", "资助", "赞助", "慈善", "公益"
    )),
    
    GIFT_KEYWORDS("礼金红包", CategoryType.GIFT, Arrays.asList(
        "红包", "礼金", "礼钱", "压岁钱", "贺礼", "礼金", "红包", "微信红包", "支付宝红包"
    )),
    
    LOTTERY_KEYWORDS("中奖收入", CategoryType.LOTTERY, Arrays.asList(
        "中奖", "彩票", "双色球", "大乐透", "福彩", "体彩", "刮刮乐", "中奖"
    )),
    
    PART_TIME_KEYWORDS("兼职收入", CategoryType.PART_TIME, Arrays.asList(
        "兼职", "副业", "外快", "零工", "临时工", "小时工", "周末工"
    )),
    
    // 支出分类关键词映射
    FOOD_KEYWORDS("餐饮美食", CategoryType.FOOD, Arrays.asList(
        "餐饮", "美食", "餐厅", "外卖", "咖啡", "奶茶", "小吃", "火锅", "烧烤", "面食", "米线", "馄饨", "包子", "饼", "粥", "汤", "菜", "饭", "酒", "茶"
    )),
    
    TRANSPORT_KEYWORDS("交通出行", CategoryType.TRANSPORT, Arrays.asList(
        "交通", "地铁", "公交", "出租车", "网约车", "高铁", "火车", "飞机", "汽车", "停车", "加油", "维修", "保险", "高速", "收费站", "过路费", "车费", "路费"
    )),
    
    SHOPPING_KEYWORDS("购物消费", CategoryType.SHOPPING, Arrays.asList(
        "购物", "超市", "商场", "便利店", "网购", "淘宝", "京东", "拼多多", "天猫", "服装", "鞋子", "包包", "化妆品", "护肤品", "数码", "电器", "家具", "家居", "饰品", "珠宝", "手表"
    )),
    
    MEDICAL_KEYWORDS("医疗健康", CategoryType.MEDICAL, Arrays.asList(
        "医疗", "医院", "诊所", "药店", "体检", "疫苗", "挂号", "手术", "治疗", "康复", "保健", "健身", "瑜伽", "按摩", "美容", "美发", "美甲", "牙科", "眼科", "骨科"
    )),
    
    EDUCATION_KEYWORDS("教育培训", CategoryType.EDUCATION, Arrays.asList(
        "教育", "学校", "大学", "培训", "课程", "学费", "书本", "文具", "考试", "证书", "学习", "辅导", "补习", "培训班", "技能培训", "职业培训"
    )),
    
    ENTERTAINMENT_KEYWORDS("娱乐休闲", CategoryType.ENTERTAINMENT, Arrays.asList(
        "娱乐", "电影", "游戏", "KTV", "酒吧", "网吧", "游乐园", "景点", "旅游", "演唱会", "音乐会", "展览", "博物馆", "公园", "动物园", "水族馆", "电影院", "游戏厅"
    )),
    
    HOUSING_KEYWORDS("住房生活", CategoryType.HOUSING, Arrays.asList(
        "住房", "房租", "房贷", "物业费", "水电费", "燃气费", "网费", "电话费", "维修费", "装修", "家具", "家电", "物业管理", "房屋维修", "房屋装修"
    )),
    
    INVESTMENT_KEYWORDS("投资理财", CategoryType.INVESTMENT, Arrays.asList(
        "投资", "股票", "基金", "债券", "保险", "理财", "存款", "投资理财", "股票投资", "基金投资", "保险投资"
    )),
    
    INSURANCE_KEYWORDS("保险保障", CategoryType.INSURANCE, Arrays.asList(
        "保险", "人寿保险", "健康保险", "意外保险", "车险", "房屋保险", "医疗保险", "养老保险", "失业保险", "工伤保险"
    )),
    
    TAX_KEYWORDS("税费支出", CategoryType.TAX, Arrays.asList(
        "税费", "税", "个人所得税", "增值税", "营业税", "房产税", "车船税", "印花税", "契税", "关税", "消费税"
    )),
    
    CHARITY_KEYWORDS("慈善捐赠", CategoryType.CHARITY, Arrays.asList(
        "慈善", "捐赠", "捐款", "捐助", "资助", "赞助", "公益", "爱心", "扶贫", "助学"
    )),
    
    TRAVEL_KEYWORDS("旅游度假", CategoryType.TRAVEL, Arrays.asList(
        "旅游", "度假", "旅行", "观光", "游览", "度假村", "旅游景点", "旅游团", "自由行", "跟团游"
    )),
    
    BEAUTY_KEYWORDS("美容美体", CategoryType.BEAUTY, Arrays.asList(
        "美容", "美体", "美发", "美甲", "护肤", "化妆", "整形", "美容院", "美发店", "美甲店", "护肤中心"
    )),
    
    FITNESS_KEYWORDS("健身运动", CategoryType.FITNESS, Arrays.asList(
        "健身", "运动", "瑜伽", "跑步", "游泳", "篮球", "足球", "网球", "羽毛球", "健身房", "运动场", "游泳池"
    )),
    
    PET_KEYWORDS("宠物护理", CategoryType.PET, Arrays.asList(
        "宠物", "狗", "猫", "宠物医院", "宠物店", "宠物用品", "宠物食品", "宠物美容", "宠物寄养"
    )),
    
    CHILD_KEYWORDS("子女教育", CategoryType.CHILD, Arrays.asList(
        "子女", "孩子", "儿童", "婴儿", "奶粉", "尿布", "玩具", "童装", "儿童教育", "儿童医疗", "儿童娱乐"
    )),
    
    ELDERLY_KEYWORDS("赡养老人", CategoryType.ELDERLY, Arrays.asList(
        "老人", "赡养", "养老", "老年", "护理", "保健品", "老年用品", "养老院", "护理院"
    )),
    
    DIGITAL_KEYWORDS("数码科技", CategoryType.DIGITAL, Arrays.asList(
        "数码", "科技", "手机", "电脑", "平板", "相机", "耳机", "音箱", "智能设备", "电子产品", "数码产品"
    )),
    
    FURNITURE_KEYWORDS("家具家居", CategoryType.FURNITURE, Arrays.asList(
        "家具", "家居", "沙发", "床", "桌子", "椅子", "柜子", "装饰品", "家居用品", "家装", "装修材料"
    )),
    
    CLOTHING_KEYWORDS("服装配饰", CategoryType.CLOTHING, Arrays.asList(
        "服装", "配饰", "衣服", "裤子", "裙子", "外套", "内衣", "袜子", "帽子", "围巾", "手套", "皮带", "领带"
    )),
    
    COSMETICS_KEYWORDS("化妆品", CategoryType.COSMETICS, Arrays.asList(
        "化妆品", "护肤品", "彩妆", "口红", "粉底", "眼影", "睫毛膏", "腮红", "香水", "洗面奶", "面霜", "精华液"
    )),
    
    BOOKS_KEYWORDS("图书文具", CategoryType.BOOKS, Arrays.asList(
        "图书", "文具", "书籍", "杂志", "报纸", "笔记本", "笔", "纸", "书包", "文具盒", "尺子", "橡皮"
    )),
    
    SPORTS_KEYWORDS("运动户外", CategoryType.SPORTS, Arrays.asList(
        "运动", "户外", "登山", "徒步", "露营", "钓鱼", "滑雪", "滑冰", "攀岩", "自行车", "摩托车", "运动装备"
    )),
    
    GAMING_KEYWORDS("游戏娱乐", CategoryType.GAMING, Arrays.asList(
        "游戏", "娱乐", "手游", "网游", "单机游戏", "游戏机", "游戏手柄", "游戏卡", "游戏币", "游戏充值"
    )),
    
    SUBSCRIPTION_KEYWORDS("订阅服务", CategoryType.SUBSCRIPTION, Arrays.asList(
        "订阅", "会员", "VIP", "付费", "月费", "年费", "服务费", "平台费", "软件费", "应用费"
    )),
    
    UTILITIES_KEYWORDS("水电煤气", CategoryType.UTILITIES, Arrays.asList(
        "水电", "煤气", "网费", "电话费", "电费", "水费", "燃气费", "宽带费", "流量费", "话费"
    )),
    
    MAINTENANCE_KEYWORDS("维修保养", CategoryType.MAINTENANCE, Arrays.asList(
        "维修", "保养", "修理", "维护", "检修", "检测", "清洗", "护理", "保养费", "维修费"
    )),
    
    LOAN_REPAYMENT_KEYWORDS("贷款还款", CategoryType.LOAN_REPAYMENT, Arrays.asList(
        "贷款", "还款", "还贷", "还钱", "分期", "按揭", "房贷", "车贷", "消费贷", "信用贷"
    )),
    
    CREDIT_CARD_REPAYMENT_KEYWORDS("信用卡还款", CategoryType.CREDIT_CARD_REPAYMENT, Arrays.asList(
        "信用卡", "还款", "还卡", "还钱", "分期", "最低还款", "全额还款", "信用卡费"
    )),
    
    BILL_PAYMENT_KEYWORDS("账单支付", CategoryType.BILL_PAYMENT, Arrays.asList(
        "账单", "缴费", "支付", "付款", "交费", "付费", "账单支付", "费用支付"
    )),
    
    RECHARGE_KEYWORDS("充值", CategoryType.RECHARGE, Arrays.asList(
        "充值", "充钱", "充费", "充卡", "充话费", "充流量", "充游戏币", "充会员"
    )),
    
    WITHDRAWAL_KEYWORDS("提现", CategoryType.WITHDRAWAL, Arrays.asList(
        "提现", "取钱", "取现", "提款", "取款", "提现费", "手续费"
    )),
    
    EXCHANGE_KEYWORDS("货币兑换", CategoryType.EXCHANGE, Arrays.asList(
        "兑换", "换钱", "换汇", "汇率", "外币", "美元", "欧元", "日元", "港币", "台币"
    )),
    
    FEE_CHARGE_KEYWORDS("手续费", CategoryType.FEE_CHARGE, Arrays.asList(
        "手续费", "服务费", "管理费", "年费", "月费", "平台费", "交易费", "转账费"
    )),
    
    PENALTY_KEYWORDS("罚金", CategoryType.PENALTY, Arrays.asList(
        "罚金", "罚款", "滞纳金", "违约金", "赔偿金", "罚单", "交通罚单", "违章罚款"
    )),
    
    // 转账分类关键词映射
    ACCOUNT_TRANSFER_KEYWORDS("账户转账", CategoryType.ACCOUNT_TRANSFER, Arrays.asList(
        "转账", "转出", "转入", "账户", "银行", "支付宝", "微信", "转账费"
    )),
    
    CARD_TRANSFER_KEYWORDS("卡片转账", CategoryType.CARD_TRANSFER, Arrays.asList(
        "卡片", "银行卡", "信用卡", "借记卡", "储蓄卡", "转账", "转卡"
    )),
    
    PLATFORM_TRANSFER_KEYWORDS("平台转账", CategoryType.PLATFORM_TRANSFER, Arrays.asList(
        "平台", "第三方", "支付平台", "金融平台", "投资平台", "转账", "平台间"
    )),
    
    CROSS_BANK_TRANSFER_KEYWORDS("跨行转账", CategoryType.CROSS_BANK_TRANSFER, Arrays.asList(
        "跨行", "跨银行", "不同银行", "银行间", "转账", "跨行费"
    )),
    
    INTERNATIONAL_TRANSFER_KEYWORDS("国际转账", CategoryType.INTERNATIONAL_TRANSFER, Arrays.asList(
        "国际", "跨境", "海外", "国外", "外币", "外汇", "转账", "国际费"
    ));

    public final String description;
    public final CategoryType categoryType;
    public final List<String> keywords;

    CategoryKeywordMapping(String description, CategoryType categoryType, List<String> keywords) {
        this.description = description;
        this.categoryType = categoryType;
        this.keywords = keywords;
    }
    
    /**
     * 根据关键词查找对应的分类（已废弃，建议使用数据库版本）
     */
    @Deprecated
    public static CategoryType findCategoryByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return CategoryType.OTHER_EXPENSE;
        }
        
        String lowerKeyword = keyword.toLowerCase().trim();
        
        for (CategoryKeywordMapping mapping : values()) {
            if (mapping.keywords.stream().anyMatch(lowerKeyword::contains)) {
                return mapping.categoryType;
            }
        }
        
        return null; // 没有找到匹配的分类
    }
    
    /**
     * 根据分类获取所有相关关键词
     */
    public static List<String> getKeywordsByCategory(CategoryType categoryType) {
        return Arrays.stream(values())
                .filter(mapping -> mapping.categoryType == categoryType)
                .flatMap(mapping -> mapping.keywords.stream())
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有分类描述
     */
    public static List<String> getAllDescriptions() {
        return Arrays.stream(values())
                .map(mapping -> mapping.description)
                .distinct()
                .collect(Collectors.toList());
    }
} 