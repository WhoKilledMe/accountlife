@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM 交易分类数据插入执行脚本
REM 适用于Windows系统

echo ================================
echo   交易分类数据插入执行脚本
echo ================================
echo.

REM 配置变量
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=accountlife
set DB_USER=root

REM 检查MySQL客户端是否安装
where mysql >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: MySQL客户端未安装或不在PATH中
    echo 请先安装MySQL客户端或确保mysql.exe在PATH中
    pause
    exit /b 1
)

REM 获取数据库连接信息
echo 请输入数据库连接信息:
set /p input_host=数据库主机 [%DB_HOST%]: 
if not "%input_host%"=="" set DB_HOST=%input_host%

set /p input_port=数据库端口 [%DB_PORT%]: 
if not "%input_port%"=="" set DB_PORT=%input_port%

set /p input_name=数据库名称 [%DB_NAME%]: 
if not "%input_name%"=="" set DB_NAME=%input_name%

set /p input_user=数据库用户名 [%DB_USER%]: 
if not "%input_user%"=="" set DB_USER=%input_user%

set /p DB_PASS=数据库密码: 

REM 测试数据库连接
echo.
echo 测试数据库连接...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% -e "USE %DB_NAME%;" >nul 2>nul
if %errorlevel% equ 0 (
    echo ✓ 数据库连接成功
) else (
    echo ✗ 数据库连接失败
    echo 请检查数据库连接信息
    pause
    exit /b 1
)

echo.
echo 选择执行模式:
echo 1) 完整初始化 (包含表结构创建)
echo 2) 仅插入交易分类数据
echo 3) 仅验证数据
echo 4) 退出

set /p choice=请选择 [1-4]: 

if "%choice%"=="1" goto :complete_init
if "%choice%"=="2" goto :insert_categories
if "%choice%"=="3" goto :verify_data
if "%choice%"=="4" goto :exit
goto :invalid_choice

:complete_init
echo.
echo 执行完整初始化...
echo 这将创建所有必要的表结构和数据
set /p confirm=确认继续? (y/N): 
if /i "%confirm%"=="y" (
    echo 执行 complete-init.sql...
    mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% < complete-init.sql
    if %errorlevel% equ 0 (
        echo ✓ 完整初始化执行成功
    ) else (
        echo ✗ 完整初始化执行失败
        pause
        exit /b 1
    )
) else (
    echo 操作已取消
    goto :exit
)
goto :auto_verify

:insert_categories
echo.
echo 执行交易分类数据插入...
echo 执行 quick-insert-categories.sql...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% < quick-insert-categories.sql
if %errorlevel% equ 0 (
    echo ✓ 交易分类数据插入成功
) else (
    echo ✗ 交易分类数据插入失败
    pause
    exit /b 1
)
goto :auto_verify

:verify_data
echo.
echo 执行数据验证...
echo 执行 test-connection.sql...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% < test-connection.sql
goto :end

:auto_verify
echo.
echo 自动执行数据验证...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% < test-connection.sql
goto :end

:invalid_choice
echo 无效选择
pause
exit /b 1

:end
echo.
echo ================================
echo   执行完成！
echo ================================
echo.
echo 预期结果:
echo - 收入分类: 15个
echo - 支出分类: 36个
echo - 转出分类: 5个
echo - 转入分类: 1个
echo - 总计: 57个交易分类
echo.
echo 如需帮助，请查看 EXECUTION_GUIDE.md

:exit
pause 