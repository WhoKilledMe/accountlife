# AccountLife API 接口思维导图

## 认证模块
- 基础路径: `/api/auth`
- POST /login
  - 用户登录
  - 输入: username, password
  - 输出: token
- POST /logout
  - 用户登出
  - 清理Token缓存

## 用户管理
- 基础路径: `/api/user`
- GET /
  - 查询所有用户
- GET /page
  - 分页查询用户
  - 参数: page, size
- GET /{id}
  - 根据ID查询
- POST /
  - 创建用户
  - 字段: username, email, phone, password, groupId, role
- PUT /
  - 更新用户
- DELETE /{id}
  - 删除用户

## 用户组管理
- 基础路径: `/api/usergroup`
- GET /
  - 查询所有用户组
- GET /page
  - 分页查询
- GET /{id}
  - 根据ID查询
- POST /
  - 创建用户组
- PUT /
  - 更新用户组
- DELETE /{id}
  - 删除用户组

## 用户组成员
- 基础路径: `/api/usergroupmember`
- GET /
  - 查询所有组成员
- GET /page
  - 分页查询
- GET /{id}
  - 根据ID查询
- POST /
  - 添加组成员
- PUT /
  - 更新组成员
- DELETE /{id}
  - 移除组成员

## 用户账户
- 基础路径: `/api/useraccount`
- GET /
  - 查询所有账户
- POST /page
  - 分页查询(带筛选)
  - 支持名称模糊搜索
- GET /select
  - 下拉选择
  - 参数: accountName
- GET /{id}
  - 根据ID查询
- POST /
  - 创建账户
- POST /batch
  - 批量创建
- PUT /
  - 更新账户
- DELETE /{id}
  - 删除账户

## 系统账户配置
- 基础路径: `/api/accountconfig`
- GET /
  - 查询启用的配置
- GET /type/{type}
  - 按类型查询
- GET /search
  - 按名称搜索
- POST /page
  - 分页查询
- GET /{id}
  - 根据ID查询
- POST /
  - 创建配置
- PUT /
  - 更新配置
- DELETE /{id}
  - 删除配置(软删除)

## 交易管理

### 账户交易
- 基础路径: `/api/accounttransaction`
- GET /
  - 查询所有交易
- POST /page
  - 分页查询(带筛选)
  - 筛选条件
    - accountId
    - categoryId
    - type
    - description
- GET /{id}
  - 根据ID查询
- POST /
  - 创建交易
  - 交易类型
    - 1-收入
    - 2-支出
    - 3-转出
    - 4-转入
- PUT /
  - 更新交易
- DELETE /{id}
  - 删除交易

### 业务交易
- 基础路径: `/api/businesstransaction`
- GET /
  - 查询所有
- POST /page
  - 分页查询
- GET /{id}
  - 根据ID查询
- POST /
  - 创建
- PUT /
  - 更新
- DELETE /{id}
  - 删除

### 平台交易
- 基础路径: `/api/platformtransaction`
- GET /
  - 查询所有
- GET /page
  - 分页查询
- GET /{id}
  - 根据ID查询
- POST /
  - 创建
- PUT /
  - 更新
- DELETE /{id}
  - 删除

## 分类管理

### 交易分类
- 基础路径: `/api/transactioncategory`
- GET /
  - 查询所有分类
- POST /page
  - 分页查询
- GET /select
  - 下拉选择
- GET /tree
  - 树形结构查询
- GET /{id}
  - 根据ID查询
- POST /
  - 创建分类
- PUT /
  - 更新分类
- DELETE /{id}
  - 删除分类

### 分类关键词映射
- 基础路径: `/api/category-keywords`
- GET /match
  - 智能匹配分类
  - 参数: keyword, userId
- GET /search
  - 搜索映射
- GET /by-category-id
  - 按分类查询
- GET /by-user-id
  - 按用户查询
- GET /keywords
  - 获取所有关键词
- POST /
  - 添加映射
- PUT /{id}
  - 更新映射
- DELETE /{id}
  - 删除映射

## 预算管理
- 基础路径: `/api/budget`

### 基础操作
- GET /user/{userId}
  - 查询用户所有预算
- GET /user/{userId}/page
  - 分页查询
- GET /{id}
  - 根据ID查询
- POST /
  - 创建预算
  - 预算类型
    - 1-月度
    - 2-年度
    - 3-分类
- PUT /
  - 更新预算
- DELETE /{id}
  - 删除预算

### 查询筛选
- GET /user/{userId}/month
  - 按月份查询
- GET /user/{userId}/year
  - 按年份查询
- GET /user/{userId}/category/{categoryId}
  - 按分类查询

### 预算监控
- PUT /{id}/used-amount
  - 更新使用金额
- PUT /{id}/check-status
  - 检查预算状态
  - 状态值
    - 1-进行中
    - 2-已完成
    - 3-已超支
- GET /user/{userId}/alerts
  - 获取预算提醒

### 使用情况
- GET /usage/current-month
  - 当月预算使用
- GET /usage/month
  - 指定月份使用情况

## 统计分析
- 基础路径: `/api/statistics`

### 资产统计
- GET /total-assets
  - 总资产统计
- GET /account-balance
  - 账户余额统计
- GET /investment
  - 投资资产统计
- GET /fixed-asset
  - 固定资产统计

### 收支统计
- GET /monthly
  - 月度收支统计
  - 参数: month (yyyy-MM)
- GET /yearly
  - 年度收支统计
  - 参数: year
- GET /category
  - 分类统计
  - 参数: startDate, endDate

### 分析报表
- GET /trend
  - 趋势统计
  - 参数: startDate, endDate
- GET /budget
  - 预算执行情况
  - 参数: month (yyyy-MM)

## 资产管理

### 固定资产
- 基础路径: `/api/fixedasset`
- GET /
  - 查询所有
- GET /page
  - 分页查询
- GET /{id}
  - 根据ID查询
- POST /
  - 创建
  - 类型: 房产/汽车/设备
- PUT /
  - 更新
- DELETE /{id}
  - 删除

### 投资资产
- 基础路径: `/api/investmentasset`
- GET /
  - 查询所有
- GET /page
  - 分页查询
- GET /{id}
  - 根据ID查询
- POST /
  - 创建
  - 类型: 股票/基金/理财
- PUT /
  - 更新
- DELETE /{id}
  - 删除

## 信用钱包账单
- 基础路径: `/api/creditwalletstatement`
- GET /
  - 查询所有账单
- GET /page
  - 分页查询
- GET /{id}
  - 根据ID查询
- POST /
  - 创建账单
- PUT /
  - 更新账单
- DELETE /{id}
  - 删除账单

## 邮件相关

### 邮箱配置
- 基础路径: `/api/usermailconfig`
- GET /
  - 查询所有配置
- POST /page
  - 分页查询
- POST /query
  - 条件查询
- GET /me
  - 当前用户配置
- GET /{id}
  - 根据ID查询
- POST /
  - 创建配置
  - 字段: emailAddress, host, port, authCode
- PUT /
  - 更新配置
- DELETE /{id}
  - 删除配置
- POST /test
  - 测试连通性

### 邮件同步
- 基础路径: `/api/mailsync`
- POST /email
  - 同步邮箱账单
  - 参数: mailSender, mailDate, accountId, zipPassword

## 文件处理

### 文件上传
- 基础路径: `/api/v1/file`
- POST /upload
  - 上传并解析CSV
  - 参数
    - file
    - type
    - accountName
  - type枚举
    - 0-宁波银行信用卡
    - 1-LabelDetail账单
    - 2-银行
    - 3-平台(美团等)
    - 4-信用钱包

### 上传日志
- 基础路径: `/api/accounttransaction/uploadlog`
- GET /
  - 查询所有日志
- POST /page
  - 分页查询
- GET /{id}
  - 根据ID查询
- POST /
  - 创建日志
- PUT /
  - 更新日志
- DELETE /{id}
  - 删除日志
- POST /{id}/sync-mail
  - 邮箱附件同步
  - 解压ZIP并解析CSV

## 测试接口
- 基础路径: `/api/test`
- 仅开发环境使用
- GET /current-user
  - 测试获取当前用户
- GET /user-id
  - 测试获取用户ID
- GET /user-id-header
  - 测试请求头用户ID
- GET /current-user-id
  - 测试getCurrentUserId

---

## 枚举速查

### 交易类型
- 1: 收入 (Income)
- 2: 支出 (Expense)
- 3: 转出 (Transfer Out)
- 4: 转入 (Transfer In)

### 预算类型
- 1: 月度预算 (Monthly)
- 2: 年度预算 (Yearly)
- 3: 分类预算 (Category)

### 预算状态
- 1: 进行中 (In Progress)
- 2: 已完成 (Completed)
- 3: 已超支 (Overspent)

### 交易来源
- 1: 文件上传 (File Upload)
- 2: 邮箱同步 (Mail Sync)
- 3: 手动录入 (Manual Entry)

### CSV来源类型
- 0: 宁波银行信用卡 (NING_BO_CREDIT)
- 1: LabelDetail账单 (LABEL_DETAIL)
- 2: 银行 (BANK)
- 3: 平台/美团 (PLATFORM)
- 4: 信用钱包 (CREDIT_WALLET)

---

## 接口统计

| 模块 | 接口数 | 说明 |
|-----|-------|-----|
| 认证模块 | 2 | 登录/登出 |
| 用户管理 | 6 | 用户CRUD |
| 用户组管理 | 6 | 组CRUD |
| 用户组成员 | 6 | 成员管理 |
| 用户账户 | 8 | 账户管理 |
| 系统账户配置 | 8 | 配置管理 |
| 账户交易 | 6 | 交易CRUD |
| 业务交易 | 6 | 业务层交易 |
| 平台交易 | 6 | 平台交易 |
| 交易分类 | 8 | 分类管理 |
| 分类关键词 | 8 | 智能匹配 |
| 预算管理 | 14 | 预算监控 |
| 统计分析 | 9 | 数据统计 |
| 固定资产 | 6 | 固资管理 |
| 投资资产 | 6 | 投资管理 |
| 信用钱包账单 | 6 | 账单管理 |
| 用户邮箱配置 | 9 | 邮箱配置 |
| 邮件同步 | 1 | 账单同步 |
| 文件上传 | 1 | CSV导入 |
| 上传日志 | 7 | 日志管理 |
| 测试接口 | 4 | 开发调试 |
| **合计** | **127** | - |
