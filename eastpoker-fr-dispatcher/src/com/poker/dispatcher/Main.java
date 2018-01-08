package com.poker.dispatcher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.server.GServer;
import com.open.net.server.impl.tcp.nio.NioServer;
import com.open.net.server.message.Message;
import com.open.net.server.object.AbstractServerClient;
import com.open.net.server.object.AbstractServerMessageProcessor;
import com.open.net.server.object.ArgsConfig;
import com.open.net.server.object.ServerConfig;
import com.open.net.server.object.ServerLog;
import com.open.net.server.object.ServerLog.LogListener;
import com.open.net.server.utils.NetUtil;
import com.open.net.server.utils.TextUtils;
import com.open.util.log.Logger;
import com.open.util.log.base.LogConfig;
import com.poker.base.ServerIds;
import com.poker.cmd.DispatchCmd;
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.protocols.Monitor;
import com.poker.protocols.server.DispatchChainProto.DispatchChain;
import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;
import com.poker.protocols.server.ServerProto.Server;

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
    	libArgsConfig.server_type = ServerIds.SERVER_DIAPATCHER;
    	
    	//1.2 服务器配置初始化:解析文件配置
        ServerConfig libServerConfig = new ServerConfig();
        libServerConfig.initArgsConfig(libArgsConfig);
        libServerConfig.initFileConfig("./conf/lib.server.config");
        
        //1.3 日志配置初始化
        LogConfig libLogConfig = Logger.init("./conf/lib.log.config",libArgsConfig.id);
        Logger.addFilterTraceElement(ServerLog.class.getName());
        Logger.addFilterTraceElement(mLogListener.getClass().getName());
        
        //1.4 业务配置初始化
        Config mConfig = new Config();
        mConfig.initFileConfig("./conf/server.config");
        
        Logger.v("libArgsConfig: "+ libArgsConfig.toString()+"\r\n");
        Logger.v("libServerConfig: "+ libServerConfig.toString()+"\r\n");
        Logger.v("libLogConfig: "+ libLogConfig.toString()+"\r\n");
        
        //----------------------------------------- 二、注册到关联服务器 ---------------------------------------
        register_monitor(mConfig);//注册到服务监听器
    	
        //----------------------------------------- 三、服务器初始化 ------------------------------------------
    	Logger.v("-------Server------start---------");
        try {
            //3.1 数据初始化
            GServer.init(libServerConfig, com.open.net.server.impl.tcp.nio.NioClient.class);
            
            //3.2 服务器初始化
            NioServer mNioServer = new NioServer(libServerConfig,mServerMessageProcessor,mLogListener);
            mNioServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        Logger.v("-------Server------end---------");
        
        //----------------------------------------- 四、反注册关联服务器 ---------------------------------------
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
    
    //-------------------------------------------------------------------------------------------
    public static HashMap<Integer, ArrayList<AbstractServerClient>> serverList = new HashMap<Integer, ArrayList<AbstractServerClient>>();
    public static HashMap<Integer, ArrayList<AbstractServerClient>> gameGroupList = new HashMap<Integer, ArrayList<AbstractServerClient>>();
    public static HashMap<Integer, ArrayList<AbstractServerClient>> matchGroupList = new HashMap<Integer, ArrayList<AbstractServerClient>>();
    
    public static AbstractServerMessageProcessor mServerMessageProcessor = new AbstractServerMessageProcessor() {

        private ByteBuffer mWriteBuffer  = ByteBuffer.allocate(16*1024);
        private long oldTime = System.currentTimeMillis();
        private long nowTime  = oldTime;
        
        protected void onReceiveMessage(AbstractServerClient client, Message msg){

        	try {
        		
        		Logger.v(System.getProperty("line.separator"));
        		Logger.v("onReceiveMessage 0x" + Integer.toHexString(DataPacket.getCmd(msg.data, msg.offset)));
        		
        		int cmd = DataPacket.getCmd(msg.data, msg.offset);
        		if(cmd == DispatchCmd.CMD_REGISTER){

        			Server mServer = Server.parseFrom(msg.data,msg.offset + DataPacket.getHeaderLength(),msg.length - DataPacket.getHeaderLength());
        			
        			//-------------------------------------------serverType----------------------------------------------
        			boolean add = true;
            		ArrayList<AbstractServerClient> mServerList = serverList.get(mServer.getType());
            		if(null == mServerList){
            			mServerList = new ArrayList<AbstractServerClient>(10);
            			serverList.put(mServer.getType(), mServerList);
            		}else{
                		for(AbstractServerClient ser:mServerList){
                			if((ser == client)){
                				add = false;
                				break;
                			}else{ 
                				Server attachObj = (Server) ser.getAttachment();
                				if(null != attachObj && attachObj.getId() == mServer.getId()){
                    				mServerList.remove(ser);
                    				break;
                    			}
                			}
                		}
            		}
            		client.attach(mServer);
            		if(add){
            			mServerList.add(client);
            		}
            		
            		//-------------------------------------------gameGroup----------------------------------------------
            		int gameGroup = mServer.getGameGroup();
            		if(gameGroup >0){
            			ArrayList<AbstractServerClient> gameGroupArray = gameGroupList.get(mServer.getType());
                		if(null == gameGroupArray){
                			gameGroupArray = new ArrayList<AbstractServerClient>(10);
                			gameGroupList.put(gameGroup, gameGroupArray);
                		}else{
                    		for(AbstractServerClient ser:gameGroupArray){
                    			if((ser == client)){
                    				add = false;
                    				break;
                    			}else{ 
                    				Server attachObj = (Server) ser.getAttachment();
                    				if(null != attachObj && attachObj.getId() == mServer.getId()){
                    					gameGroupArray.remove(ser);
                        				break;
                        			}
                    			}
                    		}
                		}
                		
                		if(add){
                			gameGroupArray.add(client);
                		}
            		}
            		
            		//-------------------------------------------matchGroup----------------------------------------------
            		int matchGroup = mServer.getGameGroup();
            		if(matchGroup >0){
            			ArrayList<AbstractServerClient> matchGroupArray = matchGroupList.get(mServer.getType());
                		if(null == matchGroupArray){
                			matchGroupArray = new ArrayList<AbstractServerClient>(10);
                			matchGroupList.put(matchGroup, matchGroupArray);
                		}else{
                    		for(AbstractServerClient ser:matchGroupArray){
                    			if((ser == client)){
                    				add = false;
                    				break;
                    			}else{ 
                    				Server attachObj = (Server) ser.getAttachment();
                    				if(null != attachObj && attachObj.getId() == mServer.getId()){
                    					matchGroupArray.remove(ser);
                        				break;
                        			}
                    			}
                    		}
                		}
                		
                		if(add){
                			matchGroupArray.add(client);
                		}
            		}
            		
            		logServer();
            		
        		}else if(cmd == DispatchCmd.CMD_DISPATCH){
        			
        			DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(msg.data,msg.offset + DataPacket.getHeaderLength(),msg.length - DataPacket.getHeaderLength());
        			int count = mDispatchPacket.getDispatchChainListCount();
        			if(count>0){
        				DispatchChain chain = mDispatchPacket.getDispatchChainList(count-1);
        				int dstServerType = chain.getDstServerType();
        				int dstServerId = chain.getDstServerId();
        				
        				AbstractServerClient server = null;
        				ArrayList<AbstractServerClient> serverArray = serverList.get(dstServerType);
        				if(null != serverArray){
        					for(AbstractServerClient ser:serverArray){
        						Server serInfo = (Server) ser.getAttachment();
        						if(serInfo.getId() == dstServerId){
        							server = ser;
        							break;
        						}
        					}
        				}
        				
        				Logger.v(String.format("dispatch %d %d %d %d %d", chain.getSrcServerType(),chain.getSrcServerId(),chain.getDstServerType(),chain.getDstServerId(),(null != server) ? 1 : 0));
        				
        				if(null != server){
        					mWriteBuffer.clear();
        					mDispatchPacket.getData().copyTo(mWriteBuffer);
        					mWriteBuffer.flip();
        					unicast(server, mWriteBuffer.array(),0,mWriteBuffer.remaining());
        				}
        			}
    				
        			System.out.println("DispatchPacket "+mDispatchPacket.toString());
        		}else{
        			
        		}
        		
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}

        }
        
		@Override
		public void onTimeTick() {
			nowTime = System.currentTimeMillis();
			if(nowTime - oldTime > 1000){
				oldTime = nowTime;
				
			}
		}

		@Override
		public void onClientEnter(AbstractServerClient client) {
			Logger.v("onClientEnter " + client.mClientId);
		}

		@Override
		public void onClientExit(AbstractServerClient client) {
			Logger.v("onClientExit " + client.mClientId);
		}
    };
    
    public static void logServer(){
		//打印所有的服务
    	logServer(serverList);
    	
    	logServer(gameGroupList);
    	
    	logServer(matchGroupList);
    }
    
    public static void logServer(HashMap<Integer, ArrayList<AbstractServerClient>> serverList){
		//打印所有的服务
		Iterator<Entry<Integer, ArrayList<AbstractServerClient>>> iter = serverList.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, ArrayList<AbstractServerClient>> entry = iter.next();
			Integer key = entry.getKey();
			ArrayList<AbstractServerClient> val = entry.getValue();
			
	        Logger.v("------- "+key+" size " + val.size() + " -------");
	        for(AbstractServerClient ser:val){
	        	Server serInfo = (Server) ser.getAttachment();
	        	Logger.v(String.format("------- name %s id %d bindHost %s bindPort %d host %s port %d", serInfo.getName(),serInfo.getId(),!TextUtils.isEmpty(serInfo.getHost())? serInfo.getHost() : "null",serInfo.getPort(),ser.mHost,ser.mPort));
	        }
		}
    }
    
    
    public static LogListener mLogListener = new LogListener(){

		@Override
		public void onLog(String tag, String msg) {
			Logger.v(msg);
		}
    };
}
