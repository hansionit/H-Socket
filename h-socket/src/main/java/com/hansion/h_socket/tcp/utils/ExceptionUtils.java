package com.hansion.h_socket.tcp.utils;

/**
 */
public class ExceptionUtils {
    private static final String TAG = "RFException";

    public static void throwException(String message) {
        throw new IllegalStateException(TAG + " : " + message);
    }
}
