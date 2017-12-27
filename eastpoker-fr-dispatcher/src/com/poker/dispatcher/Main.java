package com.poker.dispatcher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.server.GServer;
import com.open.net.server.impl.tcp.nio.NioClient;
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
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.protocols.DataTransfer;
import com.poker.protocols.server.ServerInfoProto;
import com.poker.protocols.server.ServerInfoProto.ServerInfo;

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
        GServer.init(mServerInfo, NioClient.class);
        
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
        unregister_monitor(mConfig);
    }

    //---------------------------------------Monitor----------------------------------------------------
    public static void register_monitor(Config mConfig){
        byte[] buff = DataTransfer.register2Monitor(Server.SERVER_DIAPATCHER,GServer.mServerInfo.name, GServer.mServerInfo.id,GServer.mServerInfo.host, GServer.mServerInfo.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,buff,0,DataPacket.Header.getLength(buff));
    		}
    	}
    }
    
    public static void unregister_monitor(Config mConfig){
        byte[] buff = DataTransfer.unregister2Monitor(Server.SERVER_DIAPATCHER,GServer.mServerInfo.name, GServer.mServerInfo.id,GServer.mServerInfo.host, GServer.mServerInfo.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,buff,0,DataPacket.Header.getLength(buff));
    		}
    	}
    }
    
    //-------------------------------------------------------------------------------------------
    public static HashMap<Integer, ArrayList<ServerInfoProto.ServerInfo>> serverOnlineList = new HashMap<Integer, ArrayList<ServerInfoProto.ServerInfo>>();
    
    public static AbstractMessageProcessor mMessageProcessor = new AbstractMessageProcessor() {

        private ByteBuffer mWriteBuffer  = ByteBuffer.allocate(128*1024);
        private long oldTime = System.currentTimeMillis();
        private long nowTime  = oldTime;
        
        protected void onReceiveMessage(AbstractClient client, Message msg){

        	try {
        		ServerInfo enterServer = ServerInfo.parseFrom(msg.data,DataPacket.Header.HEADER_LENGTH+msg.offset,msg.length-DataPacket.Header.HEADER_LENGTH);
        		ArrayList<ServerInfo> serverArray = serverOnlineList.get(enterServer.getType());
        		if(null == serverArray){
        			serverArray = new ArrayList<ServerInfo>(10);
        			serverOnlineList.put(enterServer.getType(), serverArray);
        		}else{
            		for(ServerInfo obj:serverArray){
            			if(obj.getId() == enterServer.getId()){
            				serverArray.remove(obj);
            				break;
            			}
            		}
        		}
        		serverArray.add(enterServer);

        		//打印所有的服务
        		Iterator<Entry<Integer, ArrayList<ServerInfo>>> iter = serverOnlineList.entrySet().iterator();
        		while (iter.hasNext()) {
    				Entry<Integer, ArrayList<ServerInfo>> entry = iter.next();
    				Integer key = entry.getKey();
    				ArrayList<ServerInfo> val = entry.getValue();
    				
    				Logger.v(System.getProperty("line.separator"));
    		        Logger.v("------- "+key+" size " + val.size() + " -------");
    		        for(ServerInfo ser:val){
    		        	Logger.v(String.format("------- %s %d %s %d ", ser.getName(),ser.getId(),ser.getHost(),ser.getPort()));
    		        }
    		        
        		}
        		
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}
        	
//            Logger.v("--onReceiveMessage()- rece  "+new String(msg.data,msg.offset,msg.length));
//            String data ="MainNioServer--onReceiveMessage()--src_reuse_type "+msg.src_reuse_type
//                    + " dst_reuse_type " + msg.dst_reuse_type
//                    + " block_index " +msg.block_index
//                    + " offset " +msg.offset;
//            Logger.v("--onReceiveMessage()--reply "+data);
//            
//            byte[] response = data.getBytes();
//
//            mWriteBuffer.clear();
//            mWriteBuffer.put(response,0,response.length);
//            mWriteBuffer.flip();
////        unicast(client,mWriteBuffer.array(),0,response.length);
//            broadcast(mWriteBuffer.array(),0,response.length);
//            mWriteBuffer.clear();
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
			System.out.println("onClientEnter " + client.mClientId);
		}

		@Override
		public void onClientExit(AbstractClient client) {
			System.out.println("onClientExit " + client.mClientId);
		}
    };
    
    public static LogListener mLogListener = new LogListener(){

		@Override
		public void onLog(String tag, String msg) {
			Logger.v(msg);
		}
    };
}
