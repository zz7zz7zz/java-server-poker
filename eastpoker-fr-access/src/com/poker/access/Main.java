package com.poker.access;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.impl.tcp.nio.NioClient;
import com.open.net.client.structures.BaseClient;
import com.open.net.client.structures.BaseMessageProcessor;
import com.open.net.client.structures.IConnectListener;
import com.open.net.client.structures.TcpAddress;
import com.open.net.server.GServer;
import com.open.net.server.impl.tcp.nio.NioServer;
import com.open.net.server.structures.AbstractClient;
import com.open.net.server.structures.AbstractMessageProcessor;
import com.open.net.server.structures.ServerConfig;
import com.open.net.server.structures.ServerLog;
import com.open.net.server.structures.ServerLog.LogListener;
import com.open.net.server.structures.message.Message;
import com.open.net.server.utils.NetUtil;
import com.open.util.log.Logger;
import com.poker.base.Server;
import com.poker.cmd.LoginCmd;
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.data.DataTransfer;
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
    	
        //-------------------------------------------------------------------------------------------
    	//1.1 配置初始化
        ServerConfig mServerInfo = new ServerConfig();
        mServerInfo.initArgsConfig(args);
        mServerInfo.initFileConfig("./conf/lib.server.config");
        
        //1.2 数据初始化
        GServer.init(mServerInfo, com.open.net.server.impl.tcp.nio.NioClient.class);
        
        //1.3 日志初始化
        Logger.init("./conf/lib.log.config",mServerInfo.id);
        Logger.addFilterTraceElement(ServerLog.class.getName());
        Logger.addFilterTraceElement(mLogListener.getClass().getName());
        Logger.v("-------Server------"+ mServerInfo.toString());
        
        //1.4 业务配置初始化
        Config mConfig = new Config();
        mConfig.initFileConfig("./conf/server.config");
        
        //-------------------------------------------------------------------------------------------
        //2.1 发送给服务监听器
        register_monitor(mConfig);
    	
    	//2.2 连接到Dispatcher
    	register_dispatcher(mConfig);
    	
        //-------------------------------------------------------------------------------------------
        //3.0 连接初始化
        Logger.v("-------Server------start---------");
        try {
            NioServer mNioServer = new NioServer(mServerInfo,mMessageProcessor,mLogListener);
            mNioServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        Logger.v("-------Server------end---------");
        
        //-------------------------------------------------------------------------------------------
        //4.0 连接初始化
        unregister_dispatcher(mConfig);
        unregister_monitor(mConfig);
    }

    //---------------------------------------Monitor----------------------------------------------------
    public static byte[] writeBuff = new byte[16*1024];
    
    public static void register_monitor(Config mConfig){
    	Monitor.register2Monitor(writeBuff,Server.SERVER_ACCESS,GServer.mServerInfo.name, GServer.mServerInfo.id,GServer.mServerInfo.host, GServer.mServerInfo.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,writeBuff,0,DataPacket.getLength(writeBuff));
    		}
    	}
    }
    
    public static void unregister_monitor(Config mConfig){
        Monitor.unregister2Monitor(writeBuff,Server.SERVER_ACCESS,GServer.mServerInfo.name, GServer.mServerInfo.id,GServer.mServerInfo.host, GServer.mServerInfo.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,writeBuff,0,DataPacket.getLength(writeBuff));
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
    
    public static int dispatchIndex = -1;
    public static NioClient [] dispatcher;
	private static IConnectListener mDisPatcherConnectResultListener = new IConnectListener() {
		@Override
		public void onConnectionSuccess(BaseClient client) {
			Logger.v("-------dispatcher onConnectionSuccess---------" +Arrays.toString(((NioClient)client).getConnectAddress()));
			//register to dispatchServer
			int length = Dispatcher.register2Dispatcher(writeBuff,Server.SERVER_ACCESS,GServer.mServerInfo.name, GServer.mServerInfo.id,GServer.mServerInfo.host, GServer.mServerInfo.port);
			mDisPatcherMessageProcessor.send(client,writeBuff,0,length);
		}

		@Override
		public void onConnectionFailed(BaseClient client) {

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

	private static BaseMessageProcessor mDisPatcherMessageProcessor =new BaseMessageProcessor() {

		@Override
		public void onReceiveMessages(BaseClient arg0,
				LinkedList<com.open.net.client.structures.message.Message> arg1) {
			// TODO Auto-generated method stub
			
		}
	};
	
    //-------------------------------------------------------------------------------------------
    public static AbstractMessageProcessor mMessageProcessor = new AbstractMessageProcessor() {

        private ByteBuffer mWriteBuffer  = ByteBuffer.allocate(16*1024);
        private long oldTime = System.currentTimeMillis();
        private long nowTime  = oldTime;
        
        protected void onReceiveMessage(AbstractClient client, Message msg){

        	int cmd = DataPacket.getCmd(msg.data, msg.offset);
        	String sCmd = Integer.toHexString(cmd);
        	
        	int server = cmd >> 16;
        	String sServer = Integer.toHexString(cmd >> 16);
        	
        	int squenceId = DataPacket.getSequenceId(msg.data,msg.offset);
        	
        	System.out.println(String.format("onReceiveMessage 0x%s serverType 0x%s squenceId %s",sCmd,sServer,squenceId));
        	Logger.v(String.format("onReceiveMessage 0x%s serverType 0x%s squenceId %s",sCmd,sServer,squenceId));
        	
        	if(cmd == LoginCmd.CMD_LOGIN){
        		LoginProto.Login readObj;
				try {
					readObj = LoginProto.Login.parseFrom(msg.data,msg.offset + DataPacket.getHeaderLength(),msg.length-DataPacket.getHeaderLength());
					System.out.println("login "+ sCmd + " "+readObj.toString());
				} catch (InvalidProtocolBufferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	
        	//如果Server大于0，则将数据转发至对应的server
        	if(server > 0){
        		int length = 0;
        		if(server == Server.SERVER_LOGIN){
        			length = ImplDataTransfer.send2Login(writeBuff, squenceId, cmd, msg.data,msg.offset,msg.length);
        		}else if(server == Server.SERVER_USER){
        			length = ImplDataTransfer.send2User(writeBuff, squenceId, cmd, msg.data,msg.offset,msg.length);
        		}
        		
        		dispatchIndex = (dispatchIndex+1) % dispatcher.length;
        		NioClient mNioClient = dispatcher[dispatchIndex];
        		if(mNioClient.isConnected()){
        			mDisPatcherMessageProcessor.send(mNioClient,writeBuff,0,length);
        		}else{
        			for(int i = 0;i<dispatcher.length;i++){
        				mNioClient = dispatcher[dispatchIndex];
                		if(mNioClient.isConnected()){
                			mDisPatcherMessageProcessor.send(mNioClient,writeBuff,0,length);
                			break;
                		}
        			}
        		}
        	}
        	
            Logger.v("--onReceiveMessage()- rece  "+new String(msg.data,msg.offset,msg.length));
            String data ="MainNioServer--onReceiveMessage()--src_reuse_type "+msg.src_reuse_type
                    + " dst_reuse_type " + msg.dst_reuse_type
                    + " block_index " +msg.block_index
                    + " offset " +msg.offset;
            Logger.v("--onReceiveMessage()--reply "+data);
            
            byte[] response = data.getBytes();

            mWriteBuffer.clear();
            mWriteBuffer.put(response,0,response.length);
            mWriteBuffer.flip();
//        unicast(client,mWriteBuffer.array(),0,response.length);
            broadcast(mWriteBuffer.array(),0,response.length);
            mWriteBuffer.clear();
        }
        
		@Override
		public void onTimeTick() {
			nowTime = System.currentTimeMillis();
			if(nowTime - oldTime > 1000){
				oldTime = nowTime;
				
			}
		}

		@Override
		public void onClientEnter(AbstractClient client) {
			Logger.v("onClientEnter " + client.mClientId);
		}

		@Override
		public void onClientExit(AbstractClient client) {
			Logger.v("onClientExit " + client.mClientId);
		}
    };
    
    
    public static class ImplDataTransfer{
    	
    	public static int send2Access(byte[] writeBuff,int squenceId, int cmd , byte[] data, int offset ,int length){
    		int dst_server_id = GServer.mServerInfo.id;
    		return DataTransfer.send2Dispatcher(writeBuff,squenceId, cmd, data,offset,length, Server.SERVER_ACCESS, GServer.mServerInfo.id, Server.SERVER_ACCESS, dst_server_id);
    	}
    	
    	public static int send2Login(byte[] writeBuff,int squenceId, int cmd , byte[] data, int offset ,int length){
    		int dst_server_id = GServer.mServerInfo.id;
    		return DataTransfer.send2Dispatcher(writeBuff,squenceId, cmd, data,offset,length, Server.SERVER_ACCESS, GServer.mServerInfo.id, Server.SERVER_LOGIN, dst_server_id);
    	}
    	
    	public static int send2User(byte[] writeBuff,int squenceId, int cmd , byte[] data, int offset ,int length){
    		int dst_server_id = GServer.mServerInfo.id;
    		return DataTransfer.send2Dispatcher(writeBuff,squenceId, cmd, data,offset,length, Server.SERVER_ACCESS, GServer.mServerInfo.id, Server.SERVER_USER, dst_server_id);
    	}
    	
    	public static int send2Allocator(byte[] writeBuff,int squenceId, int cmd , byte[] data, int offset ,int length){
    		int dst_server_id = GServer.mServerInfo.id;
    		return DataTransfer.send2Dispatcher(writeBuff,squenceId, cmd, data,offset,length, Server.SERVER_ACCESS, GServer.mServerInfo.id, Server.SERVER_ALLOCATOR, dst_server_id);
    	}
    	
    	public static int send2Gamer(byte[] writeBuff,int squenceId, int cmd , byte[] data, int offset ,int length){
    		int dst_server_id = GServer.mServerInfo.id;
    		DataTransfer.send2Dispatcher(writeBuff,squenceId, cmd, data,offset,length, Server.SERVER_ACCESS, GServer.mServerInfo.id, Server.SERVER_GAME, dst_server_id);
    		return 1;
    	}
    	
    	public static int send2GoldCoin(byte[] writeBuff,int squenceId, int cmd , byte[] data, int offset ,int length){
    		int dst_server_id = GServer.mServerInfo.id;
    		return DataTransfer.send2Dispatcher(writeBuff,squenceId, cmd, data,offset,length, Server.SERVER_ACCESS, GServer.mServerInfo.id, Server.SERVER_GOLDCOIN, dst_server_id);
    	}
    }
    
    public static LogListener mLogListener = new LogListener(){

		@Override
		public void onLog(String tag, String msg) {
			Logger.v(msg);
		}
    };
}
