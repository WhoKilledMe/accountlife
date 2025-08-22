-- 快速插入交易分类数据
-- 基于CategoryType枚举的57种分类
-- 执行前确保transaction_category表已存在
truncate table  transaction_category;

-- 插入收入类交易分类 (type = 1)
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `icon`, `sort_order`, `created_by`, `updated_by`) VALUES
('工资薪金', 1, '💰', 1, 'system', 'system'),
('奖金补贴', 1, '🎁', 2, 'system', 'system'),
('投资收益', 1, '📈', 3, 'system', 'system'),
('利息收入', 1, '💹', 4, 'system', 'system'),
('经营收入', 1, '🏢', 5, 'system', 'system'),
('租金收入', 1, '🏠', 6, 'system', 'system'),
('版税收入', 1, '📚', 7, 'system', 'system'),
('佣金收入', 1, '🤝', 8, 'system', 'system'),
('退款返现', 1, '↩️', 9, 'system', 'system'),
('赔偿收入', 1, '⚖️', 10, 'system', 'system'),
('捐赠收入', 1, '🙏', 11, 'system', 'system'),
('礼金红包', 1, '🧧', 12, 'system', 'system'),
('中奖收入', 1, '🎰', 13, 'system', 'system'),
('兼职收入', 1, '💼', 14, 'system', 'system'),
('其他收入', 1, '💵', 15, 'system', 'system');

-- 插入支出类交易分类 (type = 2)
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `icon`, `sort_order`, `created_by`, `updated_by`) VALUES
('餐饮美食', 2, '🍽️', 16, 'system', 'system'),
('交通出行', 2, '🚗', 17, 'system', 'system'),
('购物消费', 2, '🛒', 18, 'system', 'system'),
('医疗健康', 2, '🏥', 19, 'system', 'system'),
('教育培训', 2, '📚', 20, 'system', 'system'),
('娱乐休闲', 2, '🎮', 21, 'system', 'system'),
('住房生活', 2, '🏠', 22, 'system', 'system'),
('投资理财', 2, '📊', 23, 'system', 'system'),
('保险保障', 2, '🛡️', 24, 'system', 'system'),
('税费支出', 2, '🧾', 25, 'system', 'system'),
('慈善捐赠', 2, '❤️', 26, 'system', 'system'),
('旅游度假', 2, '✈️', 27, 'system', 'system'),
('美容美体', 2, '💄', 28, 'system', 'system'),
('健身运动', 2, '🏃', 29, 'system', 'system'),
('宠物护理', 2, '🐕', 30, 'system', 'system'),
('子女教育', 2, '👶', 31, 'system', 'system'),
('赡养老人', 2, '👴', 32, 'system', 'system'),
('数码科技', 2, '💻', 33, 'system', 'system'),
('家具家居', 2, '🪑', 34, 'system', 'system'),
('服装配饰', 2, '👕', 35, 'system', 'system'),
('化妆品', 2, '💋', 36, 'system', 'system'),
('图书文具', 2, '📖', 37, 'system', 'system'),
('运动户外', 2, '⛷️', 38, 'system', 'system'),
('游戏娱乐', 2, '🎲', 39, 'system', 'system'),
('订阅服务', 2, '📱', 40, 'system', 'system'),
('水电煤气', 2, '💡', 41, 'system', 'system'),
('维修保养', 2, '🔧', 42, 'system', 'system'),
('贷款还款', 2, '🏦', 43, 'system', 'system'),
('信用卡还款', 2, '💳', 44, 'system', 'system'),
('账单支付', 2, '📄', 45, 'system', 'system'),
('充值', 2, '➕', 46, 'system', 'system'),
('提现', 2, '➖', 47, 'system', 'system'),
('货币兑换', 2, '💱', 48, 'system', 'system'),
('手续费', 2, '💸', 49, 'system', 'system'),
('罚金', 2, '⚠️', 50, 'system', 'system'),
('其他支出', 2, '💸', 51, 'system', 'system');

-- 插入转账类交易分类 (type = 3 和 4)
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `icon`, `sort_order`, `created_by`, `updated_by`) VALUES
('账户转账', 3, '🔄', 52, 'system', 'system'),
('卡片转账', 3, '💳', 53, 'system', 'system'),
('平台转账', 3, '🌐', 54, 'system', 'system'),
('跨行转账', 3, '🏛️', 55, 'system', 'system'),
('国际转账', 3, '🌍', 56, 'system', 'system'),
('转入', 4, '⬇️', 57, 'system', 'system');

-- 验证插入结果
SELECT 
    '收入分类' as category_group,
    COUNT(*) as count
FROM transaction_category 
WHERE type = 1 AND user_id IS NULL
UNION ALL
SELECT 
    '支出分类' as category_group,
    COUNT(*) as count
FROM transaction_category 
WHERE type = 2 AND user_id IS NULL
UNION ALL
SELECT 
    '转出分类' as category_group,
    COUNT(*) as count
FROM transaction_category 
WHERE type = 3 AND user_id IS NULL
UNION ALL
SELECT 
    '转入分类' as category_group,
    COUNT(*) as count
FROM transaction_category 
WHERE type = 4 AND user_id IS NULL; 

-- ================================
-- 追加：父子层级设置（幂等）
-- ================================

-- 新增父类（若不存在）
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `icon`, `sort_order`, `created_by`, `updated_by`)
VALUES
('超市日用', 2, '🧻', 58, 'system', 'system'),
('通讯网络', 2, '📶', 59, 'system', 'system'),
('车辆维护', 2, '🚗', 60, 'system', 'system'),
('金融支出', 2, '🏦', 61, 'system', 'system');

-- 将现有平铺分类归入上级父类（仅在尚未设置父类时执行）
-- 购物消费 → 子类：服装配饰、化妆品、数码科技、家具家居、图书文具
UPDATE transaction_category c
JOIN (SELECT id FROM transaction_category WHERE name='购物消费' AND type=2 AND user_id IS NULL LIMIT 1) p ON 1=1
SET c.parent_id = p.id
WHERE c.user_id IS NULL AND c.type=2 AND c.parent_id IS NULL AND c.name IN ('服装配饰','化妆品','数码科技','家具家居','图书文具');

-- 车辆维护 → 子类：维修保养
UPDATE transaction_category c
JOIN (SELECT id FROM transaction_category WHERE name='车辆维护' AND type=2 AND user_id IS NULL LIMIT 1) p ON 1=1
SET c.parent_id = p.id
WHERE c.user_id IS NULL AND c.type=2 AND c.parent_id IS NULL AND c.name IN ('维修保养');

-- 金融支出 → 子类：贷款还款、信用卡还款、手续费、罚金、账单支付、充值、提现、货币兑换
UPDATE transaction_category c
JOIN (SELECT id FROM transaction_category WHERE name='金融支出' AND type=2 AND user_id IS NULL LIMIT 1) p ON 1=1
SET c.parent_id = p.id
WHERE c.user_id IS NULL AND c.type=2 AND c.parent_id IS NULL AND c.name IN ('贷款还款','信用卡还款','手续费','罚金','账单支付','充值','提现','货币兑换');

-- 插入收入子类
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '工资', 1, p.id, '🧾', 101, 'system', 'system' FROM transaction_category p WHERE p.name='工资薪金' AND p.type=1 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '年终奖', 1, p.id, '🎉', 102, 'system', 'system' FROM transaction_category p WHERE p.name='工资薪金' AND p.type=1 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '加班费', 1, p.id, '⏱️', 103, 'system', 'system' FROM transaction_category p WHERE p.name='工资薪金' AND p.type=1 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '绩效', 1, p.id, '🏅', 104, 'system', 'system' FROM transaction_category p WHERE p.name='工资薪金' AND p.type=1 AND p.user_id IS NULL LIMIT 1;

INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '补贴', 1, p.id, '🧾', 121, 'system', 'system' FROM transaction_category p WHERE p.name='奖金补贴' AND p.type=1 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '津贴', 1, p.id, '🪙', 122, 'system', 'system' FROM transaction_category p WHERE p.name='奖金补贴' AND p.type=1 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '报销入账', 1, p.id, '📥', 123, 'system', 'system' FROM transaction_category p WHERE p.name='奖金补贴' AND p.type=1 AND p.user_id IS NULL LIMIT 1;

-- 插入支出子类：餐饮美食
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '三餐', 2, p.id, '🍚', 1601, 'system', 'system' FROM transaction_category p WHERE p.name='餐饮美食' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '外卖', 2, p.id, '🥡', 1602, 'system', 'system' FROM transaction_category p WHERE p.name='餐饮美食' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '饮品咖啡', 2, p.id, '☕', 1603, 'system', 'system' FROM transaction_category p WHERE p.name='餐饮美食' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '零食', 2, p.id, '🍪', 1604, 'system', 'system' FROM transaction_category p WHERE p.name='餐饮美食' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '夜宵', 2, p.id, '🌙', 1605, 'system', 'system' FROM transaction_category p WHERE p.name='餐饮美食' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '酒水', 2, p.id, '🍺', 1606, 'system', 'system' FROM transaction_category p WHERE p.name='餐饮美食' AND p.type=2 AND p.user_id IS NULL LIMIT 1;

-- 插入支出子类：交通出行
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '公交地铁', 2, p.id, '🚉', 2301, 'system', 'system' FROM transaction_category p WHERE p.name='交通出行' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '打车网约车', 2, p.id, '🚕', 2302, 'system', 'system' FROM transaction_category p WHERE p.name='交通出行' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '加油充电', 2, p.id, '⛽', 2303, 'system', 'system' FROM transaction_category p WHERE p.name='交通出行' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '停车', 2, p.id, '🅿️', 2304, 'system', 'system' FROM transaction_category p WHERE p.name='交通出行' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '过路费', 2, p.id, '🛣️', 2305, 'system', 'system' FROM transaction_category p WHERE p.name='交通出行' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '共享出行', 2, p.id, '🚲', 2306, 'system', 'system' FROM transaction_category p WHERE p.name='交通出行' AND p.type=2 AND p.user_id IS NULL LIMIT 1;

-- 插入支出子类：住房生活
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '房租', 2, p.id, '🏠', 2201, 'system', 'system' FROM transaction_category p WHERE p.name='住房生活' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '物业费', 2, p.id, '🧾', 2203, 'system', 'system' FROM transaction_category p WHERE p.name='住房生活' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '家政保洁', 2, p.id, '🧹', 2204, 'system', 'system' FROM transaction_category p WHERE p.name='住房生活' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '房屋维修', 2, p.id, '🛠️', 2205, 'system', 'system' FROM transaction_category p WHERE p.name='住房生活' AND p.type=2 AND p.user_id IS NULL LIMIT 1;

-- 插入支出子类：通讯网络、订阅服务
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '话费', 2, p.id, '📱', 3301, 'system', 'system' FROM transaction_category p WHERE p.name='通讯网络' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '流量包', 2, p.id, '📶', 3302, 'system', 'system' FROM transaction_category p WHERE p.name='通讯网络' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '宽带', 2, p.id, '🖧', 3303, 'system', 'system' FROM transaction_category p WHERE p.name='通讯网络' AND p.type=2 AND p.user_id IS NULL LIMIT 1;

INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '音乐视频', 2, p.id, '🎵', 3401, 'system', 'system' FROM transaction_category p WHERE p.name='订阅服务' AND p.type=2 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '阅读', 2, p.id, '📖', 3402, 'system', 'system' FROM transaction_category p WHERE p.name='订阅服务' AND p.type=2 AND p.user_id IS NULL LIMIT 1;

-- 插入转账子类：账户转账、转入
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '活期⇄理财', 3, p.id, '🔁', 5201, 'system', 'system' FROM transaction_category p WHERE p.name='账户转账' AND p.type=3 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '现金⇄银行卡', 3, p.id, '🏧', 5202, 'system', 'system' FROM transaction_category p WHERE p.name='账户转账' AND p.type=3 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '储蓄卡⇄支付平台', 3, p.id, '💳', 5203, 'system', 'system' FROM transaction_category p WHERE p.name='账户转账' AND p.type=3 AND p.user_id IS NULL LIMIT 1;

INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '收红包', 4, p.id, '🧧', 5701, 'system', 'system' FROM transaction_category p WHERE p.name='转入' AND p.type=4 AND p.user_id IS NULL LIMIT 1;
INSERT IGNORE INTO `transaction_category` (`name`, `type`, `parent_id`, `icon`, `sort_order`, `created_by`, `updated_by`)
SELECT '退款入账', 4, p.id, '↩️', 5702, 'system', 'system' FROM transaction_category p WHERE p.name='转入' AND p.type=4 AND p.user_id IS NULL LIMIT 1;

-- 简要校验（父类/子类计数）
SELECT '收入父类' AS group_name, COUNT(*) AS cnt FROM transaction_category WHERE user_id IS NULL AND type=1 AND parent_id IS NULL
UNION ALL
SELECT '收入子类', COUNT(*) FROM transaction_category WHERE user_id IS NULL AND type=1 AND parent_id IS NOT NULL
UNION ALL
SELECT '支出父类', COUNT(*) FROM transaction_category WHERE user_id IS NULL AND type=2 AND parent_id IS NULL
UNION ALL
SELECT '支出子类', COUNT(*) FROM transaction_category WHERE user_id IS NULL AND type=2 AND parent_id IS NOT NULL
UNION ALL
SELECT '转账父类', COUNT(*) FROM transaction_category WHERE user_id IS NULL AND type IN (3,4) AND parent_id IS NULL
UNION ALL
SELECT '转账子类', COUNT(*) FROM transaction_category WHERE user_id IS NULL AND type IN (3,4) AND parent_id IS NOT NULL;