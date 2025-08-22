package com.acco.life.util;

import com.acco.life.common.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Currency;

/**
 * 金额处理工具类
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
public class MoneyUtil {
    
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#,##0.00%");
    
    /**
     * 格式化金额
     */
    public static String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return MONEY_FORMAT.format(amount);
    }
    
    /**
     * 格式化百分比
     */
    public static String formatPercent(BigDecimal percent) {
        if (percent == null) {
            return "0.00%";
        }
        return PERCENT_FORMAT.format(percent);
    }
    
    /**
     * 四舍五入到指定精度
     */
    public static BigDecimal round(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(Constants.MONEY_SCALE, RoundingMode.HALF_UP);
    }
    
    /**
     * 四舍五入到指定精度
     */
    public static BigDecimal round(BigDecimal amount, int scale) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(scale, RoundingMode.HALF_UP);
    }
    
    /**
     * 向上取整到指定精度
     */
    public static BigDecimal roundUp(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(Constants.MONEY_SCALE, RoundingMode.UP);
    }
    
    /**
     * 向下取整到指定精度
     */
    public static BigDecimal roundDown(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(Constants.MONEY_SCALE, RoundingMode.DOWN);
    }
    
    /**
     * 安全加法
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        BigDecimal safeA = a != null ? a : BigDecimal.ZERO;
        BigDecimal safeB = b != null ? b : BigDecimal.ZERO;
        return round(safeA.add(safeB));
    }
    
    /**
     * 安全减法
     */
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        BigDecimal safeA = a != null ? a : BigDecimal.ZERO;
        BigDecimal safeB = b != null ? b : BigDecimal.ZERO;
        return round(safeA.subtract(safeB));
    }
    
    /**
     * 安全乘法
     */
    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        BigDecimal safeA = a != null ? a : BigDecimal.ZERO;
        BigDecimal safeB = b != null ? b : BigDecimal.ZERO;
        return round(safeA.multiply(safeB));
    }
    
    /**
     * 安全除法
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b) {
        return divide(a, b, Constants.MONEY_SCALE);
    }
    
    /**
     * 安全除法（指定精度）
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b, int scale) {
        BigDecimal safeA = a != null ? a : BigDecimal.ZERO;
        BigDecimal safeB = b != null ? b : BigDecimal.ONE;
        
        if (safeB.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("除数不能为零");
        }
        
        return safeA.divide(safeB, scale, RoundingMode.HALF_UP);
    }
    
    /**
     * 计算百分比
     */
    public static BigDecimal calculatePercent(BigDecimal part, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return divide(part, total, 4);
    }
    
    /**
     * 判断金额是否为零
     */
    public static boolean isZero(BigDecimal amount) {
        return amount == null || amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * 判断金额是否为正数
     */
    public static boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 判断金额是否为负数
     */
    public static boolean isNegative(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }
    
    /**
     * 获取金额的绝对值
     */
    public static BigDecimal abs(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.abs();
    }
    
    /**
     * 比较两个金额
     */
    public static int compare(BigDecimal a, BigDecimal b) {
        BigDecimal safeA = a != null ? a : BigDecimal.ZERO;
        BigDecimal safeB = b != null ? b : BigDecimal.ZERO;
        return safeA.compareTo(safeB);
    }
} 