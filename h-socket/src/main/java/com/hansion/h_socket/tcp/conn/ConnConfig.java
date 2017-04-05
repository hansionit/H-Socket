package com.hansion.h_socket.tcp.conn;


import com.hansion.h_socket.tcp.data.BaseStickPackageUtil;
import com.hansion.h_socket.tcp.data.StickPackageUtil;
import com.hansion.h_socket.tcp.utils.CharsetUtil;
import com.hansion.h_socket.tcp.utils.StringValidationUtils;

import java.nio.ByteOrder;

/**
 * Description：连接相关配置
 * Author: Hansion
 * Time: 2016/11/29 15:37
 */
public class ConnConfig {

    private String charsetName = CharsetUtil.GBK;//默认编码
    private long connTimeout = 5000;//连接超时时间
    private long receiveTimeout = 0;//接受消息的超时时间,0为无限大
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;//大端还是小端
    private StickPackageUtil mStickPackageHelper = new BaseStickPackageUtil();//解决粘包
    private boolean isReconnect = false;//是否重连
    private int localPort = -1;

    private ConnConfig() {
    }

    public String getCharsetName() {
        return charsetName;
    }

    public long getConnTimeout() {
        return connTimeout;
    }

    public boolean isReconnect() {
        return isReconnect;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    public StickPackageUtil getStickPackageHelper() {
        return mStickPackageHelper;
    }

    public int getLocalPort() {
        return localPort;
    }

    public static class Builder {
        private ConnConfig mTcpConnConfig;

        public Builder() {
            mTcpConnConfig = new ConnConfig();
        }

        public ConnConfig create() {
            return mTcpConnConfig;
        }

        public Builder setCharsetName(String charsetName) {
            mTcpConnConfig.charsetName = charsetName;
            return this;
        }

        public Builder setByteOrder(ByteOrder byteOrder) {
            mTcpConnConfig.byteOrder = byteOrder;
            return this;
        }

        public Builder setConnTimeout(long timeout) {
            mTcpConnConfig.connTimeout = timeout;
            return this;
        }

        public Builder setIsReconnect(boolean b) {
            mTcpConnConfig.isReconnect = b;
            return this;
        }

        public Builder setStickPackageHelper(StickPackageUtil helper) {
            mTcpConnConfig.mStickPackageHelper = helper;
            return this;
        }

        //bug
        @Deprecated
        public Builder setLocalPort(int localPort) {
            if (localPort > 0 && StringValidationUtils.validateRegex
                    (localPort + "", StringValidationUtils.RegexPort)) {
                mTcpConnConfig.localPort = localPort;
            }
            return this;
        }
    }
}
