# AccountLife - 个人资产管理系统

## 项目简介

AccountLife 是一个基于 Spring Boot 3 + WebFlux + R2DBC 构建的现代化个人资产管理系统，采用响应式编程模型，提供高性能的资产管理和财务分析功能。

## 技术栈

- **后端框架**: Spring Boot 3.5.3
- **响应式编程**: Spring WebFlux
- **数据库**: MariaDB + R2DBC
- **缓存**: Redis
- **安全**: Spring Security
- **API文档**: OpenAPI 3 (Swagger)
- **对象映射**: MapStruct
- **工具库**: Hutool
- **构建工具**: Maven
- **Java版本**: 21

## 主要功能

### 1. 用户管理
- 用户注册、登录、信息管理
- 用户组和成员管理
- 角色权限控制

### 2. 资产管理
- 银行账户管理
- 投资资产管理
- 固定资产管理
- 账户余额跟踪

### 3. 交易管理
- 收入支出记录
- 转账交易处理
- 交易分类管理
- 交易历史查询

### 4. 预算管理
- 月度/年度预算设置
- 分类预算控制
- 预算执行监控
- 超支提醒

### 5. 统计分析
- 收支统计分析
- 资产分布分析
- 趋势分析
- 预算执行分析

### 6. 数据导入
- CSV文件导入
- 银行账单解析
- 平台交易同步

### 7. 通知系统
- 预算提醒
- 交易通知
- 系统通知

## 项目结构

```
src/main/java/com/acco/life/
├── AccountlifeApplication.java          # 主启动类
├── common/                              # 通用组件
│   ├── ApiResponse.java                 # 统一响应结果
│   ├── PageResponse.java                # 分页响应
│   └── Constants.java                   # 常量定义
├── config/                              # 配置类
│   ├── OpenApiConfig.java               # OpenAPI配置
│   ├── CacheConfig.java                 # 缓存配置
│   └── SecurityConfig.java              # 安全配置
├── controller/                          # 控制器层
│   ├── UserController.java              # 用户管理
│   ├── AccountTransactionController.java # 交易管理
│   ├── AssetAccountController.java      # 资产管理
│   ├── BudgetController.java            # 预算管理
│   └── StatisticsController.java        # 统计分析
├── service/                             # 服务层
│   ├── UserService.java                 # 用户服务
│   ├── AccountTransactionService.java   # 交易服务
│   ├── AssetAccountService.java         # 资产服务
│   ├── BudgetService.java               # 预算服务
│   ├── StatisticsService.java           # 统计服务
│   └── impl/                            # 服务实现
├── repository/                          # 数据访问层
├── entity/                              # 实体类
├── dto/                                 # 数据传输对象
├── mapper/                              # 对象映射器
├── enums/                               # 枚举类
├── exception/                           # 异常处理
├── util/                                # 工具类
└── filter/                              # 过滤器
```

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- MariaDB 10.5+
- Redis 6.0+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/your-username/accountlife.git
   cd accountlife
   ```

2. **配置数据库**
   - 创建数据库：`CREATE DATABASE account_life;`
   - 执行DDL脚本：`src/main/resources/sql/ddl.sql`
   - 执行初始化数据：`src/main/resources/sql/init-data.sql`

3. **配置应用**
   - 修改 `src/main/resources/application.yml` 中的数据库连接信息
   - 配置Redis连接信息
   - 配置邮件服务信息

4. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

5. **访问应用**
   - 应用地址：http://localhost:8081
   - API文档：http://localhost:8081/swagger-ui/index.html

## API文档

项目集成了OpenAPI 3，启动后可通过以下地址访问API文档：
- Swagger UI: http://localhost:8081/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs

## 主要API接口

### 用户管理
- `GET /api/user` - 查询所有用户
- `GET /api/user/{id}` - 根据ID查询用户
- `POST /api/user` - 创建用户
- `PUT /api/user` - 更新用户
- `DELETE /api/user/{id}` - 删除用户

### 交易管理
- `GET /api/transaction` - 查询交易记录
- `POST /api/transaction` - 创建交易
- `PUT /api/transaction` - 更新交易
- `DELETE /api/transaction/{id}` - 删除交易

### 资产管理
- `GET /api/account` - 查询资产账户
- `POST /api/account` - 创建资产账户
- `PUT /api/account` - 更新资产账户
- `DELETE /api/account/{id}` - 删除资产账户

### 预算管理
- `GET /api/budget/user/{userId}` - 查询用户预算
- `POST /api/budget` - 创建预算
- `PUT /api/budget` - 更新预算
- `DELETE /api/budget/{id}` - 删除预算

### 统计分析
- `GET /api/statistics/total-assets/{userId}` - 获取总资产统计
- `GET /api/statistics/monthly/{userId}` - 获取月度统计
- `GET /api/statistics/yearly/{userId}` - 获取年度统计
- `GET /api/statistics/category/{userId}` - 获取分类统计

## 开发指南

### 代码规范

1. **命名规范**
   - 类名：PascalCase
   - 方法名：camelCase
   - 常量：UPPER_SNAKE_CASE
   - 包名：lowercase

2. **注释规范**
   - 类和方法必须有JavaDoc注释
   - 重要业务逻辑需要行内注释

3. **异常处理**
   - 使用全局异常处理器
   - 自定义业务异常
   - 统一错误响应格式

### 数据库设计

- 使用R2DBC进行响应式数据库访问
- 实体类使用Spring Data R2DBC注解
- 支持软删除（is_deleted字段）

### 缓存策略

- 使用Redis作为缓存存储
- 配置合理的缓存过期时间
- 支持缓存预热和失效

## 部署说明

### Docker部署

1. **构建镜像**
   ```bash
   docker build -t accountlife:latest .
   ```

2. **运行容器**
   ```bash
   docker run -d -p 8081:8081 --name accountlife accountlife:latest
   ```

### 生产环境配置

1. **数据库配置**
   - 使用生产级数据库
   - 配置连接池参数
   - 启用数据库监控

2. **缓存配置**
   - 配置Redis集群
   - 设置合理的缓存策略
   - 监控缓存命中率

3. **安全配置**
   - 启用HTTPS
   - 配置CORS策略
   - 实现用户认证授权

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目维护者：wensen.zhang
- 邮箱：wensen.zhang@example.com
- 项目地址：https://github.com/your-username/accountlife

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现基础的用户管理功能
- 实现交易记录管理
- 实现资产管理功能
- 实现预算管理功能
- 实现统计分析功能 