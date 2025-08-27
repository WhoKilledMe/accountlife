#!/bin/bash

# 交易分类数据插入执行脚本
# 适用于Linux和Mac系统

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置变量
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="accountlife"
DB_USER="root"

echo -e "${BLUE}================================${NC}"
echo -e "${BLUE}  交易分类数据插入执行脚本${NC}"
echo -e "${BLUE}================================${NC}"
echo ""

# 检查MySQL客户端是否安装
if ! command -v mysql &> /dev/null; then
    echo -e "${RED}错误: MySQL客户端未安装${NC}"
    echo "请先安装MySQL客户端或确保mysql命令在PATH中"
    exit 1
fi

# 获取数据库连接信息
echo -e "${YELLOW}请输入数据库连接信息:${NC}"
read -p "数据库主机 [${DB_HOST}]: " input_host
DB_HOST=${input_host:-$DB_HOST}

read -p "数据库端口 [${DB_PORT}]: " input_port
DB_PORT=${input_port:-$DB_PORT}

read -p "数据库名称 [${DB_NAME}]: " input_name
DB_NAME=${input_name:-$DB_NAME}

read -p "数据库用户名 [${DB_USER}]: " input_user
DB_USER=${input_user:-$DB_USER}

read -s -p "数据库密码: " DB_PASS
echo ""

# 测试数据库连接
echo -e "${YELLOW}测试数据库连接...${NC}"
if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "USE $DB_NAME;" 2>/dev/null; then
    echo -e "${GREEN}✓ 数据库连接成功${NC}"
else
    echo -e "${RED}✗ 数据库连接失败${NC}"
    echo "请检查数据库连接信息"
    exit 1
fi

echo ""
echo -e "${YELLOW}选择执行模式:${NC}"
echo "1) 完整初始化 (包含表结构创建)"
echo "2) 仅插入交易分类数据"
echo "3) 仅验证数据"
echo "4) 退出"

read -p "请选择 [1-4]: " choice

case $choice in
    1)
        echo -e "${YELLOW}执行完整初始化...${NC}"
        echo "这将创建所有必要的表结构和数据"
        read -p "确认继续? (y/N): " confirm
        if [[ $confirm =~ ^[Yy]$ ]]; then
            echo "执行 complete-init.sql..."
            mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" < complete-init.sql
            if [ $? -eq 0 ]; then
                echo -e "${GREEN}✓ 完整初始化执行成功${NC}"
            else
                echo -e "${RED}✗ 完整初始化执行失败${NC}"
                exit 1
            fi
        else
            echo "操作已取消"
            exit 0
        fi
        ;;
    2)
        echo -e "${YELLOW}执行交易分类数据插入...${NC}"
        echo "执行 quick-insert-categories.sql..."
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" < quick-insert-categories.sql
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✓ 交易分类数据插入成功${NC}"
        else
            echo -e "${RED}✗ 交易分类数据插入失败${NC}"
            exit 1
        fi
        ;;
    3)
        echo -e "${YELLOW}执行数据验证...${NC}"
        echo "执行 test-connection.sql..."
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" < test-connection.sql
        ;;
    4)
        echo "退出程序"
        exit 0
        ;;
    *)
        echo -e "${RED}无效选择${NC}"
        exit 1
        ;;
esac

# 如果执行了数据插入，自动进行验证
if [ $choice -eq 1 ] || [ $choice -eq 2 ]; then
    echo ""
    echo -e "${YELLOW}自动执行数据验证...${NC}"
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" < test-connection.sql
fi

echo ""
echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}  执行完成！${NC}"
echo -e "${GREEN}================================${NC}"
echo ""
echo -e "${BLUE}预期结果:${NC}"
echo "- 收入分类: 15个"
echo "- 支出分类: 36个"
echo "- 转出分类: 5个"
echo "- 转入分类: 1个"
echo "- 总计: 57个交易分类"
echo ""
echo -e "${BLUE}如需帮助，请查看 EXECUTION_GUIDE.md${NC}" 