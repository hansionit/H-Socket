package com.hansion.h_socket.tcp.data;

import java.io.InputStream;

/**
 * 对数据粘包处理的工具
 */
public interface StickPackageUtil {
    //同步方法且反复调用，直到获取完整数据，不要做耗时操作
    byte[] execute(InputStream is);
}
