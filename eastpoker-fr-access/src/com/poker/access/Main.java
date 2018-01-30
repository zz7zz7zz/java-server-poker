package com.poker.access;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import com.open.net.client.GClient;
import com.open.net.client.impl.tcp.nio.NioClient;
import com.open.net.client.object.AbstractClient;
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
import com.poker.access.handler.ClientHandler;
import com.poker.access.handler.ServerHandler;
import com.poker.access.object.User;
import com.poker.access.object.UserPool;
import com.poker.base.ServerIds;
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.packet.PacketTransfer;
import com.poker.protocols.Dispatcher;
import com.poker.protocols.Monitor;

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
    	libArgsConfig.server_type = ServerIds.SERVER_ACCESS;
    	
    	//1.2.1 服务器配置初始化:解析文件配置
        libServerConfig = new ServerConfig();
        libServerConfig.initArgsConfig(libArgsConfig);
        libServerConfig.initFileConfig("./conf/lib.server.config");
        
        //1.2.1 服务器配置初始化:作为客户端配置
        libClientConfig = new ClientConfig();
        libClientConfig.initFileConfig("./conf/lib.client.config");
        GClient.init(libClientConfig);
        
        //1.3 日志配置初始化
        LogConfig libLogConfig = Logger.init("./conf/lib.log.config",libArgsConfig.id);
        Logger.addFilterTraceElement(ServerLog.class.getName());
        Logger.addFilterTraceElement(mLogListener.getClass().getName());
        
        //1.4 业务配置初始化
        Config mServerConfig = new Config();
        mServerConfig.initFileConfig("./conf/server.config");
        
        //-----------------------------------------初始化全局属性-----------------------------------------------
        initGlobalFields(libServerConfig.packet_max_length_tcp);
        
        Logger.v("libArgsConfig: "+ libArgsConfig.toString()+"\r\n");
        Logger.v("libServerConfig: "+ libServerConfig.toString()+"\r\n");
        Logger.v("libClientConfig: "+ libClientConfig.toString()+"\r\n");
        Logger.v("libLogConfig: "+ libLogConfig.toString()+"\r\n");
        Logger.v("mServerConfig: "+ mServerConfig.toString()+"\r\n");
        
        //----------------------------------------- 二、注册到关联服务器 ---------------------------------------
        byte[] mTempBuff = new byte[512];
        register_monitor(mServerConfig,mTempBuff);//注册到服务监听器
    	register_dispatcher(mServerConfig,mTempBuff);//注册到Dispatcher
    	
        //----------------------------------------- 三、服务器初始化 ------------------------------------------
    	Logger.v("-------Server------start---------");
        try {
            //3.1 数据初始化
            GServer.init(libServerConfig, com.open.net.server.impl.tcp.nio.NioClient.class);
            
            //3.2 服务器初始化
            NioServer mNioServer = new NioServer(libServerConfig,mServerHandler,mLogListener);
            mNioServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        Logger.v("-------Server------end---------");
        
        //----------------------------------------- 四、反注册关联服务器 ---------------------------------------
        unregister_dispatcher(mServerConfig);//反注册到服务监听器
        unregister_monitor(mServerConfig,new byte[512]);//反注册到服务监听器
        
        //----------------------------------------- 五、最终退出程序 ---------------------------------------
        System.exit(0);
    }

    //---------------------------------------Fields----------------------------------------------------
    public static ArgsConfig   libArgsConfig;
    public static ClientConfig libClientConfig;
    public static ServerConfig libServerConfig;
    public static NioClient [] dispatcher;
    public static int 		   dispatchIndex = -1;
    public static ServerHandler mServerHandler ;
    
	public static HashMap<Long,User> userMap = new HashMap<Long,User>();
   //---------------------------------------Logger----------------------------------------------------
    public static LogListener mLogListener = new LogListener(){

		@Override
		public void onLog(String tag, String msg) {
			Logger.v(msg);
		}
    };
    
    //---------------------------------------初始化全局对象----------------------------------------------------
    private static void initGlobalFields(int packet_max_length_tcp){
    	PacketTransfer.init(libArgsConfig.server_type, libArgsConfig.id);
    	mServerHandler = new ServerHandler(new InPacket(packet_max_length_tcp), new OutPacket(packet_max_length_tcp));
   
		//预先分配1/4桌子数目的用户，每次增长1/4桌子数目的用户
		int user_init_size = (int)(0.25*libServerConfig.connect_max_count);
		UserPool.init(user_init_size,user_init_size);
    }
    
    //---------------------------------------Monitor----------------------------------------------------
    public static void register_monitor(Config mConfig,byte[] buff){
    	Monitor.register2Monitor(buff,libArgsConfig.server_type,libArgsConfig.name, libArgsConfig.id,libArgsConfig.host, libArgsConfig.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,buff,0,DataPacket.getLength(buff));
    		}
    	}
    }
    
    public static void unregister_monitor(Config mConfig,byte[] buff){
        Monitor.unregister2Monitor(buff,libArgsConfig.server_type,libArgsConfig.name, libArgsConfig.id,libArgsConfig.host, libArgsConfig.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,buff,0,DataPacket.getLength(buff));
    		}
    	}
    }
    
    //---------------------------------------Dispatcher----------------------------------------------------
    public static void register_dispatcher(Config mConfig,byte[] buff){
    	int dispatcherSize = (null != mConfig.dispatcher_net_tcp) ? mConfig.dispatcher_net_tcp.length : 0;
    	if(dispatcherSize > 0){
    		dispatcher = new NioClient[dispatcherSize];
    		for(int i=0; i< dispatcherSize ; i++){
    			dispatcher[i] = new NioClient(new ClientHandler(new InPacket(libClientConfig.packet_max_length_tcp),new OutPacket(libClientConfig.packet_max_length_tcp)),mClientConnectResultListener); 
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
    
	private static IConnectListener mClientConnectResultListener = new IConnectListener() {
		@Override
		public void onConnectionSuccess(AbstractClient client) {
			Logger.v("-------dispatcher onConnection Success---------" +Arrays.toString(((NioClient)client).getConnectAddress()));
			
			//register to dispatchServer 公用byte[] ，为了不必要的内存开销
			ClientHandler mClientHandler=((ClientHandler)client.getmMessageProcessor());
			byte[] buff = mClientHandler.getInPacket().getPacket();
			int length = Dispatcher.register2Dispatcher(buff,libArgsConfig.server_type,libArgsConfig.name, libArgsConfig.id,libArgsConfig.host, libArgsConfig.port);
			client.getmMessageProcessor().send(client,buff,0,length);
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
					Logger.v("-------dispatcher onConnection Failed---------" +Arrays.toString(((NioClient)client).getConnectAddress()));
					mClient.connect();
					break;
				}
			}
		}
	};
}
