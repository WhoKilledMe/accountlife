package com.acco.life.util;

import cn.hutool.crypto.SecureUtil;
import java.security.SecureRandom;

public final class PasswordUtil {

    private static final String HEX = "0123456789abcdef";
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    public static String generateSalt(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(HEX.charAt(RANDOM.nextInt(HEX.length())));
        }
        return sb.toString();
    }

    public static String md5WithSalt(String plainPassword, String salt) {
        String md5 = SecureUtil.md5(plainPassword + salt);
        return salt + ":" + md5;
    }

    public static boolean verify(String plainPassword, String stored) {
        if (stored == null || !stored.contains(":")) {
            return false;
        }
        String[] parts = stored.split(":", 2);
        String salt = parts[0];
        String expect = md5WithSalt(plainPassword, salt);
        return expect.equals(stored);
    }
}


