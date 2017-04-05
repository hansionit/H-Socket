package com.hansion.h_socket.tcp.listener;


import com.hansion.h_socket.tcp.HTcpClient;
import com.hansion.h_socket.tcp.bean.SocketMessage;

/**
 * Description：
 * Author: Hansion
 * Time: 2016/11/29 16:14
 */
public interface SocketListener {

    void onConnected(HTcpClient client);

    void onSended(HTcpClient client, SocketMessage socketMessage);

    void onDisconnected(HTcpClient client, String msg, Exception e);

    void onReceive(HTcpClient client, SocketMessage socketMessage);
}
