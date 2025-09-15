package com.acco.life.enums;

import java.util.Arrays;

/**
 * 枚举接口，定义通用方法
 */
public interface EnumInterface {
    
    /**
     * 获取枚举代码
     * @return 枚举代码
     */
    String getCode();
    
    /**
     * 获取枚举名称
     * @return 枚举名称
     */
    String getName();
    
    /**
     * 根据代码获取枚举实例（默认实现）
     * @param code 枚举代码
     * @return 枚举实例，未找到返回null
     */
    @SuppressWarnings("unchecked")
    default <T extends Enum<T> & EnumInterface> T getEnumByCode(String code) {
        if (code == null) {
            return null;
        }
        Class<?> enumClass = this.getClass();
        if (!enumClass.isEnum()) {
            return null;
        }
        return (T) Arrays.stream(enumClass.getEnumConstants())
                .filter(enumItem -> ((EnumInterface) enumItem).getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
