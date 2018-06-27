package com.poker.game;

import java.util.Arrays;

import com.open.net.base.Looper;
import com.open.net.base.util.NetUtil;
import com.open.net.client.GClient;
import com.open.net.client.impl.tcp.nio.NioClient;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.ClientConfig;
import com.open.net.client.object.IConnectListener;
import com.open.net.client.object.TcpAddress;
import com.open.net.server.object.ArgsConfig;
import com.open.net.server.object.ServerLog;
import com.open.net.server.object.ServerLog.LogListener;

import com.open.util.log.Logger;
import com.open.util.log.base.LogConfig;
import com.poker.base.cmd.BaseGameCmd;
import com.poker.base.cmd.Cmd;
import com.poker.base.cmd.Cmd.ICmdRecognizer;
import com.poker.base.data.DataPacket;
import com.poker.base.packet.InPacket;
import com.poker.base.packet.OutPacket;
import com.poker.base.packet.PacketTransfer;
import com.poker.base.type.TServer;
import com.poker.common.config.Config;
import com.poker.game.handler.ClientHandler;
import com.poker.games.Room;
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
    	libArgsConfig.server_type = TServer.SERVER_GAME;
        
        //1.3 服务器配置初始化:作为客户端配置
        libClientConfig = new ClientConfig();
        libClientConfig.initFileConfig("./conf/lib.client.config");
        GClient.init(libClientConfig);
        
        //1.3 日志配置初始化
        LogConfig libLogConfig = Logger.init("./conf/lib.log.config",libArgsConfig.id);
        Logger.addFilterTraceElement(ServerLog.class.getName());
        Logger.addFilterTraceElement(mLogListener.getClass().getName());
        
        //1.4 业务配置初始化
        mServerConfig = new Config();
        mServerConfig.initFileConfig("./conf/server.config");
        mServerConfig.server_id = libArgsConfig.id;
        
        libArgsConfig.server_type = (mServerConfig.game_id<<16 | TServer.SERVER_GAME);
        //-----------------------------------------初始化全局属性-----------------------------------------------
        initGlobalFields(libClientConfig.packet_max_length_tcp);
        
        Logger.v("libArgsConfig: "+ libArgsConfig.toString()+"\r\n");
        Logger.v("libClientConfig: "+ libClientConfig.toString()+"\r\n");
        Logger.v("libLogConfig: "+ libLogConfig.toString()+"\r\n");
        Logger.v("mConfig: "+ mServerConfig.toString()+"\r\n");
        
        //----------------------------------------- 二、注册到关联服务器 ---------------------------------------
        byte[] mTempBuff = new byte[512];
        register_monitor(mServerConfig,mTempBuff);//注册到服务监听器
    	register_dispatcher(mServerConfig,mTempBuff);//注册到Dispatcher
    	
        //----------------------------------------- 三、服务器初始化 ------------------------------------------
        
    	Looper.loop();
        
        //----------------------------------------- 四、反注册关联服务器 ---------------------------------------
        unregister_dispatcher(mServerConfig);//反注册到服务监听器
        unregister_monitor(mServerConfig,new byte[512]);//反注册到服务监听器
        
        //----------------------------------------- 五、最终退出程序 ---------------------------------------
        System.exit(0);
    }

    //---------------------------------------Fields----------------------------------------------------
    public static ArgsConfig libArgsConfig;
    public static ClientConfig libClientConfig;
    public static Config mServerConfig;
    public static NioClient [] dispatcher;
    public static int 		   dispatchIndex = -1;
	
    public static Room mRoom;

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
        mRoom = new Room(mServerConfig);
        Cmd.AddCmdRecognizer(new ICmdRecognizer() {
			@Override
			public String getCmdString(int cmd) {
				return BaseGameCmd.getCmdString(cmd);
			}
		});
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
	
	public static void send2Dispatch(byte[] buff, int offset, int length){
  		Main.dispatchIndex = (Main.dispatchIndex+1) % Main.dispatcher.length;
  		NioClient mNioClient = Main.dispatcher[Main.dispatchIndex];
  		if(mNioClient.isConnected()){
  			mNioClient.getmMessageProcessor().send(mNioClient,buff,offset,length);
  		}else{
  			for(int i = 1;i<Main.dispatcher.length;i++){
  				mNioClient = Main.dispatcher[(Main.dispatchIndex+i)%Main.dispatcher.length];
          		if(mNioClient.isConnected()){
          			mNioClient.getmMessageProcessor().send(mNioClient,buff,offset,length);
          			break;
          		}
  			}
  		}
	}
}
