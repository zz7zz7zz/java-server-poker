package com.poker.login;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.GClient;
import com.open.net.client.impl.tcp.nio.NioClient;
import com.open.net.client.message.Message;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.open.net.client.object.ClientConfig;
import com.open.net.client.object.IConnectListener;
import com.open.net.client.object.TcpAddress;
import com.open.net.server.GServer;
import com.open.net.server.impl.tcp.nio.NioServer;
import com.open.net.server.object.ArgsConfig;
import com.open.net.server.object.ServerConfig;
import com.open.net.server.object.ServerLog;
import com.open.net.server.object.ServerLog.LogListener;
import com.open.net.server.utils.NetUtil;
import com.open.util.log.Logger;
import com.open.util.log.base.LogConfig;
import com.poker.base.Server;
import com.poker.cmd.LoginCmd;
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.protocols.Dispatcher;
import com.poker.protocols.Monitor;
import com.poker.protocols.server.LoginProto;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :  服务器入口
 */

public class Main {

    public static void main(String [] args){
    	
        //----------------------------------------- 一、配置初始化 ------------------------------------------
    	//1.1 服务器配置初始化:解析命令行参数
    	libArgsConfig = new ArgsConfig();
    	libArgsConfig.initArgsConfig(args);
    	libArgsConfig.server_type = Server.SERVER_ACCESS;
    	
    	//1.2 服务器配置初始化:解析文件配置
        ServerConfig libServerConfig = new ServerConfig();
        libServerConfig.initArgsConfig(libArgsConfig);
        libServerConfig.initFileConfig("./conf/lib.server.config");
        
        //1.3 服务器配置初始化:作为客户端配置
        ClientConfig libClientConfig = new ClientConfig();
        libClientConfig.initFileConfig("./conf/lib.client.config");
        GClient.init(libClientConfig);
        
        //1.3 日志配置初始化
        LogConfig libLogConfig = Logger.init("./conf/lib.log.config",libArgsConfig.id);
        Logger.addFilterTraceElement(ServerLog.class.getName());
        Logger.addFilterTraceElement(mLogListener.getClass().getName());
        
        //1.4 业务配置初始化
        Config mConfig = new Config();
        mConfig.initFileConfig("./conf/server.config");
        
        Logger.v("libArgsConfig: "+ libArgsConfig.toString()+"\r\n");
        Logger.v("libServerConfig: "+ libServerConfig.toString()+"\r\n");
        Logger.v("libClientConfig: "+ libClientConfig.toString()+"\r\n");
        Logger.v("libLogConfig: "+ libLogConfig.toString()+"\r\n");
        
        //----------------------------------------- 二、注册到关联服务器 ---------------------------------------
        register_monitor(mConfig);//注册到服务监听器
    	register_dispatcher(mConfig);//注册到Dispatcher
    	
        //----------------------------------------- 三、服务器初始化 ------------------------------------------
    	while(true){
    		try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
    	}
        //----------------------------------------- 四、反注册关联服务器 ---------------------------------------
        unregister_dispatcher(mConfig);//反注册到服务监听器
        unregister_monitor(mConfig);//反注册到服务监听器
    }

    //---------------------------------------Monitor----------------------------------------------------
    public static ArgsConfig libArgsConfig;
    public static byte[] write_buff = new byte[16*1024];
    
    public static void register_monitor(Config mConfig){
        Monitor.register2Monitor(write_buff,libArgsConfig.server_type,libArgsConfig.name, libArgsConfig.id,libArgsConfig.host, libArgsConfig.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,write_buff,0,DataPacket.getLength(write_buff));
    		}
    	}
    }
    
    public static void unregister_monitor(Config mConfig){
    	Monitor.unregister2Monitor(write_buff,libArgsConfig.server_type,libArgsConfig.name, libArgsConfig.id,libArgsConfig.host, libArgsConfig.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,write_buff,0,DataPacket.getLength(write_buff));
    		}
    	}
    }
    
    //---------------------------------------Dispatcher----------------------------------------------------
    public static void register_dispatcher(Config mConfig){
    	int dispatcherSize = (null != mConfig.dispatcher_net_tcp) ? mConfig.dispatcher_net_tcp.length : 0;
    	if(dispatcherSize > 0){
    		dispatcher = new NioClient[dispatcherSize];
    		for(int i=0; i< dispatcherSize ; i++){
    			dispatcher[i] = new NioClient(mDisPatcherMessageProcessor,mDisPatcherConnectResultListener); 
    			dispatcher[i].setConnectAddress(new TcpAddress[]{mConfig.dispatcher_net_tcp[i]});
    			dispatcher[i].connect();
    		}
    	}
    }
    
    public static void unregister_dispatcher(Config mConfig){
    	int dispatcherSize = (null != dispatcher) ? dispatcher.length : 0;
    	if(dispatcherSize > 0){
    		for(int i=0; i< dispatcherSize ; i++){
    			dispatcher[i].disconnect();
    		}
    	}
    }
    
    public static NioClient [] dispatcher;
    
	private static IConnectListener mDisPatcherConnectResultListener = new IConnectListener() {
		@Override
		public void onConnectionSuccess(AbstractClient client) {
			Logger.v("-------dispatcher onConnectionSuccess---------" +Arrays.toString(((NioClient)client).getConnectAddress()));
			//register to dispatchServer
			int length = Dispatcher.register2Dispatcher(write_buff,libArgsConfig.server_type,libArgsConfig.name, libArgsConfig.id,libArgsConfig.host, libArgsConfig.port);
			mDisPatcherMessageProcessor.send(client,write_buff,0,length);
		}

		@Override
		public void onConnectionFailed(AbstractClient client) {

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for(NioClient mClient: dispatcher){
				if(mClient == client){
					Logger.v("-------dispatcher onConnectionFailed---------" +Arrays.toString(((NioClient)client).getConnectAddress()));
					mClient.connect();
					break;
				}
			}
		}
	};

	private static AbstractClientMessageProcessor mDisPatcherMessageProcessor =new AbstractClientMessageProcessor() {

		@Override
		public void onReceiveMessages(AbstractClient arg0, LinkedList<Message> list) {
			for(int i = 0 ;i<list.size();i++){
				Message msg = list.get(i);
				int cmd = DataPacket.getCmd(msg.data, msg.offset);
	        	Logger.v("onReceiveMessage mDisPatcherMessageProcessor 0x" + Integer.toHexString(DataPacket.getCmd(msg.data, msg.offset)));
	        	if(cmd == LoginCmd.CMD_LOGIN){
	        		LoginProto.Login readObj;
					try {
						readObj = LoginProto.Login.parseFrom(msg.data,msg.offset + DataPacket.getHeaderLength(),msg.length-DataPacket.getHeaderLength());
						System.out.println("login "+readObj.toString());
					} catch (InvalidProtocolBufferException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
			}
		}
	};
    
    public static LogListener mLogListener = new LogListener(){

		@Override
		public void onLog(String tag, String msg) {
			Logger.v(msg);
		}
    };
}
