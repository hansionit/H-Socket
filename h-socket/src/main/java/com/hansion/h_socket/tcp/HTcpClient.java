package com.hansion.h_socket.tcp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.hansion.h_socket.tcp.bean.AddressInfo;
import com.hansion.h_socket.tcp.bean.SocketMessage;
import com.hansion.h_socket.tcp.conn.ClientManager;
import com.hansion.h_socket.tcp.conn.ConnConfig;
import com.hansion.h_socket.tcp.listener.SocketListener;
import com.hansion.h_socket.tcp.utils.CharsetUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static com.hansion.h_socket.tcp.utils.DataTransUtils.bytesToHexString;


/**
 * Description：TCP客户端
 * Author: Hansion
 * Time: 2016/11/29 15:08
 *
 * 使用方法：
 *  1.获取单一实例
 *  2.配置连接信息
 *  3.设置监听
 *  4.连接Socket
 *  5.进行收发操作
 *  6.Activity销毁时移除监听
 *  7.根据实际情况关闭Socket
 *
 */
public class HTcpClient {
    private static final String TAG = "HTcpClient --> ";

    private static HTcpClient hTcpClient;
    private Socket mSocket;
    //连接相关配置
    private ConnConfig mConnConfig;
    //监听者集合
    private List<SocketListener> mSocketListeners;
    //线程安全的阻塞消息队列
    private LinkedBlockingQueue<SocketMessage> msgQueue;
    //为了运行在主线程
    private Handler mUIHandler;

    //IP、端口号相关
    private AddressInfo addressInfo;

    //连接、发送、接收的线程
    private ConnectionThread mConnectionThread;
    private SendThread mSendThread;
    private ReceiveThread mReceiveThread;

    //当前连接的状态
    private ClientState mClientState;
    public enum ClientState {
        Disconnected, Connecting, Connected
    }

    //----------------------------对外提供的方法--------------------------------

    /**
     * 获取单一实例
     *
     * @param addressInfo
     * @return
     */
    public synchronized static HTcpClient getInstance(AddressInfo addressInfo) {
        if (null == hTcpClient) {
            hTcpClient = getTcpClient(addressInfo);
        }
        return hTcpClient;
    }

    /**
     * 配置连接信息
     * @param connConfig
     */
    public void config(ConnConfig connConfig) {
        mConnConfig = connConfig;
    }

    /**
     * 设置监听
     * @param listener
     */
    public void addTcpClientListener(SocketListener listener) {
        if (mSocketListeners.contains(listener)) {
            return;
        }
        mSocketListeners.add(listener);
    }

    /**
     * 连接Socket
     */
    public synchronized void connect() {
        if (!isDisconnected()) {
            Log.d(TAG,"已经连接或正在连接");
            return;
        }
        Log.d(TAG,"正在连接");
        //正在连接
        setClientState(ClientState.Connecting);
        getConnectionThread().start();
    }

    /**
     * 发送字符串
     *
     * @param message
     * @return
     */
    public synchronized SocketMessage sendMsg(String message) {
        SocketMessage msg = new SocketMessage(message, addressInfo);
        return sendMsg(msg);
    }

    /**
     * 发送byte数组
     * @param message
     * @return
     */
    public synchronized SocketMessage sendMsg(byte[] message) {
        SocketMessage msg = new SocketMessage(message, addressInfo);
        return sendMsg(msg);
    }

    /**
     * 移除监听
     * @param listener
     */
    public void removeTcpClientListener(SocketListener listener) {
        mSocketListeners.remove(listener);
    }

    /**
     * 手动关闭Socket连接
     */
    public synchronized void disconnect() {
        disconnect("手动关闭Socket连接", null);
    }

    public AddressInfo getTargetInfo() {
        return this.addressInfo;
    }


    //---------------------------------各种状态的通知---------------------------------------------

    /**
     * 断开连接的通知
     *
     * @param msg
     * @param e
     */
    private void notifyDisconnected(final String msg, final Exception e) {
        for (SocketListener socketListener : mSocketListeners) {
            final SocketListener finalL = socketListener;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onDisconnected(HTcpClient.this, msg, e);
                }
            });
        }
    }

    /**
     * 连接成功的通知
     */
    private void notifyConnected() {
        for (SocketListener wl : mSocketListeners) {
            final SocketListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onConnected(HTcpClient.this);
                }
            });
        }
    }


    /**
     * 发送成功的通知
     *
     * @param msg
     */
    private void notifySended(final SocketMessage msg) {
        for (SocketListener wl : mSocketListeners) {
            final SocketListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onSended(HTcpClient.this, msg);
                }
            });
        }
    }

    /**
     * 接收到数据的通知
     * @param msg
     */
    private void notifyReceive(final SocketMessage msg) {
        for (SocketListener wl : mSocketListeners) {
            final SocketListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onReceive(HTcpClient.this, msg);
                }
            });
        }
    }


    //----------------------------私有方法--------------------------------

    /**
     * 创建tcp连接
     *
     * @param addressInfo
     * @return
     */
    private static HTcpClient getTcpClient(AddressInfo addressInfo) {
        return getTcpClient(addressInfo, null);
    }

    private static HTcpClient getTcpClient(AddressInfo addressInfo, ConnConfig connConfig) {
        HTcpClient hTcpClient = ClientManager.getTcpClient(addressInfo);
        if (hTcpClient == null) {
            hTcpClient = new HTcpClient();
            hTcpClient.init(addressInfo, connConfig);
            ClientManager.putTcpClient(hTcpClient);
        }
        return hTcpClient;
    }

    private void init(AddressInfo addressInfo, ConnConfig connConfig) {
        this.addressInfo = addressInfo;
        mClientState = ClientState.Disconnected;
        mSocketListeners = new ArrayList<>();
        if (mConnConfig == null && connConfig == null) {
            mConnConfig = new ConnConfig.Builder().create();
        } else if (connConfig != null) {
            mConnConfig = connConfig;
        }
        mUIHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获取连接状态
     *
     * @return
     */
    public ClientState getClientState() {
        return mClientState;
    }

    /**
     * 设置连接状态
     * @param state
     */
    private void setClientState(ClientState state) {
        if (mClientState != state) {
            mClientState = state;
        }
    }

    /**
     * 发送消息
     * @param msg
     * @return
     */
    private synchronized SocketMessage sendMsg(SocketMessage msg) {
        if (isDisconnected()) {
            Log.d(TAG,"发送消息 " + msg + "，未连接Socket,先进行连接");
            connect();
        }
        boolean re = enqueueTcpMsg(msg);
        if (re) {
            return msg;
        }
        return null;
    }

    private boolean enqueueTcpMsg(final SocketMessage msg) {
        if (msg == null || getMsgQueue().contains(msg)) {
            return false;
        }
        try {
            getMsgQueue().put(msg);
            return true;
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
        return false;
    }

    /**
     * 当前是否断开连接
     *
     * @return
     */
    private boolean isDisconnected() {
        return getClientState() == ClientState.Disconnected;
    }

    private boolean isConnected() {
        return getClientState() == ClientState.Connected;
    }

    private LinkedBlockingQueue<SocketMessage> getMsgQueue() {
        if (msgQueue == null) {
            msgQueue = new LinkedBlockingQueue<>();
        }
        return msgQueue;
    }

    private void runOnUiThread(Runnable runnable) {
        mUIHandler.post(runnable);
    }

    //连接完成，创建发送和接受消息的线程
    private void onConnectSuccess() {
        Log.d(TAG,"Socket连接成功");
        //标记为已连接
        setClientState(ClientState.Connected);
        getSendThread().start();
        getReceiveThread().start();
    }

    private synchronized Socket getSocket() {
        if (mSocket == null || isDisconnected() || !mSocket.isConnected()) {
            mSocket = new Socket();
            try {
                mSocket.setSoTimeout((int) mConnConfig.getReceiveTimeout());
            } catch (SocketException e) {
//                e.printStackTrace();
            }
        }
        return mSocket;
    }

    private synchronized void onErrorDisConnect(String msg, Exception e) {
        if (isDisconnected()) {
            return;
        }
        disconnect(msg, e);
        if (mConnConfig.isReconnect()) {//重连
            connect();
        }
    }


    private synchronized void disconnect(String msg, Exception e) {
        if (isDisconnected()) {
            return;
        }
        closeSocket();
        getConnectionThread().interrupt();
        getSendThread().interrupt();
        getReceiveThread().interrupt();
        setClientState(ClientState.Disconnected);
        notifyDisconnected(msg, e);
        Log.d(TAG,"Socket断开连接 " + msg + e);
    }


    private synchronized boolean closeSocket() {
        if (mSocket != null) {
            try {
                mSocket.close();
                Log.d(TAG,"Socket已关闭");
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        return true;
    }


    //----------------------------连接、发送、接收的线程--------------------------------

    private ConnectionThread getConnectionThread() {
        if (mConnectionThread == null || !mConnectionThread.isAlive() || mConnectionThread.isInterrupted()) {
            mConnectionThread = new ConnectionThread();
        }
        return mConnectionThread;
    }

    private SendThread getSendThread() {
        if (mSendThread == null || !mSendThread.isAlive()) {
            mSendThread = new SendThread();
        }
        return mSendThread;
    }

    private ReceiveThread getReceiveThread() {
        if (mReceiveThread == null || !mReceiveThread.isAlive()) {
            mReceiveThread = new ReceiveThread();
        }
        return mReceiveThread;
    }

    /**
     * 连接线程
     */
    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            try {
                int localPort = mConnConfig.getLocalPort();
                if (localPort > 0) {
                    if (!getSocket().isBound()) {
                        getSocket().bind(new InetSocketAddress(localPort));
                    }
                }
                String ip = addressInfo.getIp();
                int port = addressInfo.getPort();
                getSocket().connect(new InetSocketAddress(ip, port), (int) mConnConfig.getConnTimeout());
                Log.d(TAG,"创建Socket连接成功,ip:" + ip + ",端口号:" + port);
            } catch (Exception e) {
                Log.e(TAG,"创建Socket连接失败:" + e);
                onErrorDisConnect("创建连接失败", e);
                return;
            }
            notifyConnected();
            Log.d(TAG,"socket连接成功");
            onConnectSuccess();
        }
    }

    /**
     * 发送线程
     */
    private class SendThread extends Thread {
        private SocketMessage sendingMsg;

        private SendThread setSendingTcpMsg(SocketMessage sendingMsg) {
            this.sendingMsg = sendingMsg;
            return this;
        }

        public SocketMessage getSendingTcpMsg() {
            return this.sendingMsg;
        }

        public boolean cancel(SocketMessage packet) {
            return getMsgQueue().remove(packet);
        }

        public boolean cancel(int tcpMsgID) {
            return getMsgQueue().remove(new SocketMessage(tcpMsgID));
        }

        @Override
        public void run() {
            SocketMessage msg;
            try {
                while (isConnected() && !Thread.interrupted() && (msg = getMsgQueue().take()) != null) {
                    setSendingTcpMsg(msg);//设置正在发送的
                    byte[] data = msg.getSourceDataBytes();
                    if (data == null) {//根据编码转换消息
                        data = CharsetUtil.stringToData(msg.getSourceDataString(), mConnConfig.getCharsetName());
                    }
                    if (data != null && data.length > 0) {
                        try {
                            getSocket().getOutputStream().write(data);
                            getSocket().getOutputStream().flush();
                            msg.setTime();
                            notifySended(msg);
                            if(msg.isBytes()) {
                                Log.e(TAG,"发送数组：" + bytesToHexString(msg.getSourceDataBytes()));
                            }else {
                                Log.e(TAG,"发送字符串：" + msg.getSourceDataString());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            onErrorDisConnect("发送消息失败", e);
                            return;
                        }
                    }
                }
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
        }
    }


    /**
     * 接收线程
     */
    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                InputStream is = getSocket().getInputStream();
                while (isConnected() && !Thread.interrupted()) {
                    byte[] result = mConnConfig.getStickPackageHelper().execute(is);//粘包处理
                    if (result == null) {//报错
                        Log.d(TAG,"粘包处理失败" + Arrays.toString(result));
                        onErrorDisConnect("粘包处理中发生错误", null);
                        break;
                    }
                    SocketMessage msg = new SocketMessage(result, addressInfo);
                    msg.setTime();
                    String msgstr = CharsetUtil.dataToString(result, mConnConfig.getCharsetName());
                    msg.setSourceDataString(msgstr);
                    notifyReceive(msg);
                    byte[] sourceDataBytes = msg.getSourceDataBytes();
                    Log.d(TAG,"收到：len= " + sourceDataBytes.length + ", bytes=" + bytesToHexString(sourceDataBytes));
                }
            } catch (Exception e) {
                Log.e(TAG,"接收消息错误:" + e);
                onErrorDisConnect("接受消息错误", e);
            }
        }
    }
}
