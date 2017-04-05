package com.hansion.h_socket.tcp.utils;

/**
 * Description：
 * Author: Hansion
 * Time: 2017/4/5 17:10
 */
public class DataTransUtils {

    /**
     * 将byte数组转为16进制字符串
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv.toUpperCase()).append(" ");
        }
        return stringBuilder.toString();
    }

}
