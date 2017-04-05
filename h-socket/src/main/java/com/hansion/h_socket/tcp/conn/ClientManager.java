package com.hansion.h_socket.tcp.conn;


import com.hansion.h_socket.tcp.HTcpClient;
import com.hansion.h_socket.tcp.bean.AddressInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * Description：
 * Author: Hansion
 * Time: 2016/11/29 15:44
 */
public class ClientManager {
    private static Set<HTcpClient> sMXTcpClients = new HashSet<>();

    public static void putTcpClient(HTcpClient hTcpClient) {
        sMXTcpClients.add(hTcpClient);
    }

    public static HTcpClient getTcpClient(AddressInfo addressInfo) {
        for (HTcpClient hc : sMXTcpClients) {
            if (hc.getTargetInfo().equals(addressInfo)) {
                return hc;
            }
        }
        return null;
    }
}
