# H-Socket
非常方便的Tcp连接、数据收发库，可以直接使用jar包




* 获取HTcp实例

		HTcpClient hTcpClient = HTcpClient.getInstance(new AddressInfo(ip, port));
		
* 设置包头包尾过滤(根据个人需要)

        //粘包处理：添加包头0xAA 、 包尾0xBB
        byte[] headBytes = new byte[1];
        headBytes[0] = (byte) 0xAA;
        byte[] footBytes = new byte[1];
        footBytes[0] = (byte) 0xBB;
        stickHelper = new SpecifiedStickPackageUtil(headBytes, footBytes);

* 连接服务器

             hTcpClient.config(new ConnConfig.Builder()
                //.setStickPackageHelper(stickHelper)//粘包
                // .setIsReconnect(true)   //自动重连
                .setCharsetName("GBK")  //设置编码格式 或 UTF-8 默认为GBK
                .create());
        	 hTcpClient.connect();


* 添加监听

            hTcpClient.addTcpClientListener(new SocketListener() {
            @Override
            public void onConnected(HTcpClient hTcpClient) {
                //连接成功
            }

            @Override
            public void onSended(HTcpClient hTcpClient, SocketMessage socketMessage) {
                //发送数据成功
            }

            @Override
            public void onDisconnected(HTcpClient hTcpClient, String s, Exception e) {
                //断开连接或连接失败
            }

            @Override
            public void onReceive(HTcpClient hTcpClient, SocketMessage socketMessage) {
                //接收到数据
				socketMessage.getSourceDataString();
				socketMessage.getSourceDataBytes();
            }
        });


* 发送数据（支持字符串与字节数组）

     	hTcpClient.sendMsg("连接成功");
	 	hTcpClient.sendMsg(bytes);



     
		
