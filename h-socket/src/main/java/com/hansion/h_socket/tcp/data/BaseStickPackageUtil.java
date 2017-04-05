package com.hansion.h_socket.tcp.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * 不对粘包进行处理，直接将读取到的数据返回；默认最大长度：256
 */
public class BaseStickPackageUtil implements StickPackageUtil {
    private int maxLen = 256;//最大长度256

    public BaseStickPackageUtil() {
    }

    public BaseStickPackageUtil(int maxLen) {
        if (maxLen > 0) {
            this.maxLen = maxLen;
        }
    }

    @Override
    public byte[] execute(InputStream is) {
        byte[] bytes = new byte[maxLen];
        int len;
        try {
            if ((len = is.read(bytes)) != -1) {
                return Arrays.copyOf(bytes, len);
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return null;
    }
}
