package com.hansion.h_socket.tcp.bean;


import com.hansion.h_socket.tcp.utils.ExceptionUtils;
import com.hansion.h_socket.tcp.utils.StringValidationUtils;

/**
 * Description：
 * Author: Hansion
 * Time: 2016/11/29 15:26
 */
public class AddressInfo {

    private String ip;
    private int port;

    public AddressInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
        check();
    }

    /**
     * 检测IP、端口号是否符合规范
     */
    private void check() {
        if (!StringValidationUtils.validateRegex(port + "", StringValidationUtils.RegexPort)) {
            ExceptionUtils.throwException("port 格式不合法");
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
