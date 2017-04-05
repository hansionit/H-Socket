package com.hansion.h_socket.tcp.bean;

import com.hansion.h_socket.tcp.utils.CharsetUtil;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hansion.h_socket.tcp.utils.DataTransUtils.bytesToHexString;

/**
 * Description：发送或接收的数据信息
 * Author: Hansion
 * Time: 2016/11/29 15:47
 */
public class SocketMessage {

    //线程安全的Integer加减操作接口
    private static final AtomicInteger IDAtomic = new AtomicInteger();
    private int id;
    //数据源：byte数组
    private byte[] sourceDataBytes;
    //数据源：字符串
    private String sourceDataString;
    //发送、接受消息的IP、端口信息
    private AddressInfo addressInfo;
    //发送、接受消息的时间戳
    private long time;
    //发送数据的类型：是否是byte数组
    private boolean isBytes = true;

    public SocketMessage(int id) {
        this.id = id;
    }

    public SocketMessage(byte[] data, AddressInfo addressInfo) {
        isBytes = true;
        this.sourceDataBytes = data;
        this.addressInfo = addressInfo;
        init();
    }

    public SocketMessage(String data, AddressInfo addressInfo) {
        isBytes = false;
        this.sourceDataString = data;
        this.addressInfo = addressInfo;
        init();
    }

    public boolean isBytes() {
        return isBytes;
    }

    private void init() {
        id = IDAtomic.getAndIncrement();
    }

    public void setTime() {
        time = System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public byte[] getSourceDataBytes() {
        return sourceDataBytes;
    }

    public String getSourceDataString() {
        return sourceDataString;
    }

    public String getSourceDataHexString() {
        if(sourceDataBytes != null) {
            return bytesToHexString(sourceDataBytes);
        }else {
            try {
                return bytesToHexString( sourceDataString.getBytes(CharsetUtil.mCharsetName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void setSourceDataString(String sourceDataString) {
        this.sourceDataString = sourceDataString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public AddressInfo getTarget() {
        return addressInfo;
    }

    public void setTarget(AddressInfo addressInfo) {
        this.addressInfo = addressInfo;
    }


}
