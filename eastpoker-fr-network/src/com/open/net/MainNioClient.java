package com.open.net;

import com.open.net.base.Looper;
import com.open.net.client.GClient;
import com.open.net.client.impl.tcp.nio.NioClient;
import com.open.net.client.message.Message;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.open.net.client.object.ClientConfig;
import com.open.net.client.object.IConnectListener;
import com.open.net.client.object.TcpAddress;

import java.io.IOException;
import java.util.LinkedList;


/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :  服务器入口
 */

public final class MainNioClient {

    public static void main_start(String [] args) throws IOException{
    	
    	//1.配置初始化
        ClientConfig libClientConfig = new ClientConfig();
        libClientConfig.initFileConfig("./conf/lib.client.config");
        GClient.init(libClientConfig);
        
        NioClient mNioClient = new NioClient(mAbstractClientMessageProcessor,mIConnectListener);
        mNioClient.setConnectAddress(new TcpAddress[]{new TcpAddress("192.168.9.141", 9995),new TcpAddress("192.168.9.141", 9996)});
        mNioClient.connect();
        
        Looper.loop();
    }

    static AbstractClientMessageProcessor mAbstractClientMessageProcessor = new AbstractClientMessageProcessor() {
		
		@Override
		public void onReceiveMessages(AbstractClient mClient, LinkedList<Message> mQueen) {
			System.out.println("onReceiveMessages");
			
		}
	};
	
	static IConnectListener mIConnectListener =  new IConnectListener() {
		
		@Override
		public void onConnectionSuccess(AbstractClient mClient) {
			System.out.println("onConnectionSuccess");
			
		}
		
		@Override
		public void onConnectionFailed(AbstractClient mClient) {
			System.out.println("onConnectionFailed");
			
			try {
				Thread.sleep(500);
				((NioClient)mClient).connect();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	};
}
