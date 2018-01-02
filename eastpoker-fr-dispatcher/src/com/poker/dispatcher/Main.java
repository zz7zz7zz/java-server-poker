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
import com.open.net.server.utils.TextUtils;
import com.open.util.log.Logger;
import com.poker.base.Server;
import com.poker.cmd.DispatchCmd;
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.protocols.Monitor;
import com.poker.protocols.server.DispatchChainProto.DispatchChain;
import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;
import com.poker.protocols.server.LoginProto;
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
    public static byte[] write_buff = new byte[16*1024];
    
    public static void register_monitor(Config mConfig){
        Monitor.register2Monitor(write_buff,Server.SERVER_DIAPATCHER,GServer.mServerInfo.name, GServer.mServerInfo.id,GServer.mServerInfo.host, GServer.mServerInfo.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,write_buff,0,DataPacket.getLength(write_buff));
    		}
    	}
    }
    
    public static void unregister_monitor(Config mConfig){
    	Monitor.unregister2Monitor(write_buff,Server.SERVER_DIAPATCHER,GServer.mServerInfo.name, GServer.mServerInfo.id,GServer.mServerInfo.host, GServer.mServerInfo.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,write_buff,0,DataPacket.getLength(write_buff));
    		}
    	}
    }
    
    //-------------------------------------------------------------------------------------------
    public static HashMap<Integer, ArrayList<AbstractClient>> serverOnlineList = new HashMap<Integer, ArrayList<AbstractClient>>();
    
    public static AbstractMessageProcessor mMessageProcessor = new AbstractMessageProcessor() {

        private ByteBuffer mWriteBuffer  = ByteBuffer.allocate(16*1024);
        private long oldTime = System.currentTimeMillis();
        private long nowTime  = oldTime;
        
        protected void onReceiveMessage(AbstractClient client, Message msg){

        	try {
        		
        		Logger.v(System.getProperty("line.separator"));
        		Logger.v("onReceiveMessage 0x" + Integer.toHexString(DataPacket.getCmd(msg.data, msg.offset)));
        		
        		int cmd = DataPacket.getCmd(msg.data, msg.offset);
        		if(cmd == DispatchCmd.CMD_REGISTER){

        			ServerInfo enterServer = ServerInfo.parseFrom(msg.data,msg.offset + DataPacket.getHeaderLength(),msg.length - DataPacket.getHeaderLength());
        			
        			boolean add = true;
            		ArrayList<AbstractClient> clientArray = serverOnlineList.get(enterServer.getType());
            		if(null == clientArray){
            			clientArray = new ArrayList<AbstractClient>(10);
            			serverOnlineList.put(enterServer.getType(), clientArray);
            		}else{
                		for(AbstractClient ser:clientArray){
                			if((ser == client)){
                				add = false;
                				break;
                			}else{ 
                				ServerInfo attachObj = (ServerInfo) ser.getAttachment();
                				if(null != attachObj && attachObj.getId() == enterServer.getId()){
                    				clientArray.remove(ser);
                    				break;
                    			}
                			}
                		}
            		}
            		
            		client.attach(enterServer);
            		if(add){
            			clientArray.add(client);
            		}
            		
            		//打印所有的服务
            		Iterator<Entry<Integer, ArrayList<AbstractClient>>> iter = serverOnlineList.entrySet().iterator();
            		while (iter.hasNext()) {
        				Entry<Integer, ArrayList<AbstractClient>> entry = iter.next();
        				Integer key = entry.getKey();
        				ArrayList<AbstractClient> val = entry.getValue();
        				
        		        Logger.v("------- "+key+" size " + val.size() + " -------");
        		        for(AbstractClient ser:val){
        		        	ServerInfo serInfo = (ServerInfo) ser.getAttachment();
        		        	Logger.v(String.format("------- name %s id %d host %s port %d ", serInfo.getName(),serInfo.getId(),!TextUtils.isEmpty(serInfo.getHost())? serInfo.getHost() : "null",serInfo.getPort()));
        		        }
            		}
            		
        		}else if(cmd == DispatchCmd.CMD_DISPATCH){
        			DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(msg.data,msg.offset + DataPacket.getHeaderLength(),msg.length - DataPacket.getHeaderLength());
        			int count = mDispatchPacket.getDispatchChainListCount();
        			if(count>0){
        				DispatchChain chain = mDispatchPacket.getDispatchChainList(count-1);
        				int dstServerType = chain.getDstServerType();
        				int dstServerId = chain.getDstServerId();
        				
        				AbstractClient server = null;
        				ArrayList<AbstractClient> serverArray = serverOnlineList.get(dstServerType);
        				if(null != serverArray){
        					for(AbstractClient ser:serverArray){
        						ServerInfo serInfo = (ServerInfo) ser.getAttachment();
        						if(serInfo.getId() == dstServerId){
        							server = ser;
        							break;
        						}
        					}
        				}
        				
        				Logger.v(String.format("dispatch %d %d %d %d %d", chain.getSrcServerType(),chain.getSrcServerId(),chain.getDstServerType(),chain.getDstServerId(),(null != server) ? 1 : 0));
        				
        				if(null != server){
        					mDispatchPacket.getData().copyTo(mWriteBuffer);
        					mWriteBuffer.flip();
        					mMessageProcessor.unicast(server, mWriteBuffer.array(),0,mWriteBuffer.remaining());
        				}
        			}
        			
//        			try {
//    					
//    					mDispatchPacket.getData().copyTo(mWriteBuffer);
//    					mWriteBuffer.flip();
//    					LoginProto.Login readObj = LoginProto.Login.parseFrom(mWriteBuffer.array(),DataPacket.getHeaderLength(),mWriteBuffer.remaining()-DataPacket.getHeaderLength());
//    					System.out.println("login "+readObj.toString());
//    				} catch (InvalidProtocolBufferException e) {
//    					// TODO Auto-generated catch block
//    					e.printStackTrace();
//    				}
    				
        			System.out.println("DispatchPacket "+mDispatchPacket.toString());
        		}else{
        			
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
			Logger.v("onClientEnter " + client.mClientId);
		}

		@Override
		public void onClientExit(AbstractClient client) {
			Logger.v("onClientExit " + client.mClientId);
		}
    };
    
    public static LogListener mLogListener = new LogListener(){

		@Override
		public void onLog(String tag, String msg) {
			Logger.v(msg);
		}
    };
}
