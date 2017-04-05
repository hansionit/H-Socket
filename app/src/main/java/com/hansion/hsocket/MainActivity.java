package com.hansion.hsocket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hansion.h_socket.tcp.HTcpClient;
import com.hansion.h_socket.tcp.bean.AddressInfo;
import com.hansion.h_socket.tcp.bean.SocketMessage;
import com.hansion.h_socket.tcp.conn.ConnConfig;
import com.hansion.h_socket.tcp.data.SpecifiedStickPackageUtil;
import com.hansion.h_socket.tcp.data.StickPackageUtil;
import com.hansion.h_socket.tcp.listener.SocketListener;


public class MainActivity extends AppCompatActivity implements SocketListener {

    private String ip="192.168.1.58";
    private int  port = 2000 ;

    private StickPackageUtil stickHelper;
    private HTcpClient hTcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hTcpClient = HTcpClient.getInstance(new AddressInfo(ip, port));
        hTcpClient.addTcpClientListener(this);

        //粘包处理：添加包头0xAA 、 包尾0xBB
        byte[] headBytes = new byte[1];
        headBytes[0] = (byte) 0xAA;
        byte[] footBytes = new byte[1];
        footBytes[0] = (byte) 0xBB;
        stickHelper = new SpecifiedStickPackageUtil(headBytes, footBytes);

        connTcpServer();

    }


    public void connTcpServer() {
        hTcpClient.config(new ConnConfig.Builder()
//                .setStickPackageHelper(stickHelper)//粘包
//                    .setIsReconnect(true)   //自动重连
                .create());
        hTcpClient.connect();
    }

    @Override
    public void onConnected(HTcpClient hTcpClient) {

        hTcpClient.sendMsg("连接成功");

        LogUtil.e("Tcp连接成功");
    }

    @Override
    public void onSended(HTcpClient hTcpClient, SocketMessage socketMessage) {
        LogUtil.e("Tcp发送数据---十六进制形式："+socketMessage.getSourceDataHexString());
        LogUtil.e("Tcp发送数据---字符串形式："+socketMessage.getSourceDataString());
    }

    @Override
    public void onDisconnected(HTcpClient hTcpClient, String s, Exception e) {
        LogUtil.e("Tcp断开连接或连接失败");
    }

    @Override
    public void onReceive(HTcpClient hTcpClient, SocketMessage socketMessage) {
        LogUtil.e("Tcp收到数据---十六进制形式："+socketMessage.getSourceDataHexString());
        LogUtil.e("Tcp收到数据---字符串形式："+socketMessage.getSourceDataString());
    }
}
