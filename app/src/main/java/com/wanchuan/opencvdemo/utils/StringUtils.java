package com.wanchuan.opencvdemo.utils;

import android.text.TextUtils;

/**
 * 字符串工具类
 */
public class StringUtils {


    /**
     * byte 转 16进制字符串
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    /**
     * 16进制字符串转 byte
     * @param hexString
     * @return
     */
    private static byte[] hexStrToBinaryStr(String hexString) {

        if (TextUtils.isEmpty(hexString)) {
            return null;
        }
        hexString = hexString.replaceAll(" ", "");
        int len = hexString.length();
        int index = 0;
        byte[] bytes = new byte[len / 2];
        while (index < len) {
            String sub = hexString.substring(index, index + 2);
            bytes[index / 2] = (byte) Integer.parseInt(sub, 16);
            index += 2;
        }
        return bytes;
    }
}
