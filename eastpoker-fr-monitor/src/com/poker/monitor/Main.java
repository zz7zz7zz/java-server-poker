package com.poker.monitor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.server.GServer;
import com.open.net.server.impl.udp.nio.UdpNioClient;
import com.open.net.server.impl.udp.nio.UdpNioServer;
import com.open.net.server.message.Message;
import com.open.net.server.object.AbstractServerClient;
import com.open.net.server.object.AbstractServerMessageProcessor;
import com.open.net.server.object.ArgsConfig;
import com.open.net.server.object.ServerConfig;
import com.open.net.server.object.ServerLog;
import com.open.net.server.object.ServerLog.LogListener;
import com.open.net.server.utils.TextUtils;
import com.open.util.log.Logger;
import com.open.util.log.base.LogConfig;
import com.poker.base.Server;
import com.poker.cmd.MonitorCmd;
import com.poker.data.DataPacket;
import com.poker.protocols.server.ServerInfoProto.ServerInfo;;
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
        
        //1.3 日志配置初始化
        LogConfig libLogConfig = Logger.init("./conf/lib.log.config",libArgsConfig.id);
        Logger.addFilterTraceElement(ServerLog.class.getName());
        Logger.addFilterTraceElement(mLogListener.getClass().getName());
        
        Logger.v("libArgsConfig: "+ libArgsConfig.toString()+"\r\n");
        Logger.v("libServerConfig: "+ libServerConfig.toString()+"\r\n");
        Logger.v("libLogConfig: "+ libLogConfig.toString()+"\r\n");
        
        //----------------------------------------- 三、服务器初始化 ------------------------------------------
        Logger.v("-------Server------start---------");
        try {
            //3.1 数据初始化
            GServer.init(libServerConfig, UdpNioClient.class);
            
            //3.2 服务器初始化
            UdpNioServer mBioServer = new UdpNioServer(libServerConfig,mMessageProcessor,mLogListener);
            mBioServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.v("-------Server------end---------");
    }

    //-------------------------------------------------------------------------------------------
    public static ArgsConfig libArgsConfig;
    public static HashMap<Integer, ArrayList<AbstractServerClient>> serverOnlineList = new HashMap<Integer, ArrayList<AbstractServerClient>>();
    
    public static AbstractServerMessageProcessor mMessageProcessor = new AbstractServerMessageProcessor() {

    	private ByteBuffer mWriteBuffer  = ByteBuffer.allocate(16*1024);
        private long oldTime = System.currentTimeMillis();
        private long nowTime  = oldTime;
        
        protected void onReceiveMessage(AbstractServerClient client, Message msg){
        	try {
        		
        		int cmd = DataPacket.getCmd(msg.data, msg.offset);
        		Logger.v(System.getProperty("line.separator"));
        		Logger.v("onReceiveMessage 0x" + Integer.toHexString(cmd));
        		
        		if(cmd == MonitorCmd.CMD_REGISTER){
        			ServerInfo enterServer = ServerInfo.parseFrom(msg.data,msg.offset + DataPacket.getHeaderLength(),msg.length - DataPacket.getHeaderLength());
        			
        			boolean add = true;
            		ArrayList<AbstractServerClient> clientArray = serverOnlineList.get(enterServer.getType());
            		if(null == clientArray){
            			clientArray = new ArrayList<AbstractServerClient>(10);
            			serverOnlineList.put(enterServer.getType(), clientArray);
            		}else{
                		for(AbstractServerClient ser:clientArray){
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
            		Iterator<Entry<Integer, ArrayList<AbstractServerClient>>> iter = serverOnlineList.entrySet().iterator();
            		while (iter.hasNext()) {
        				Entry<Integer, ArrayList<AbstractServerClient>> entry = iter.next();
        				Integer key = entry.getKey();
        				ArrayList<AbstractServerClient> val = entry.getValue();
        				
        		        Logger.v("------- "+key+" size " + val.size() + " -------");
        		        for(AbstractServerClient ser:val){
        		        	ServerInfo serInfo = (ServerInfo) ser.getAttachment();
        		        	Logger.v(String.format("------- name %s id %d bindHost %s bindPort %d host %s port %d", serInfo.getName(),serInfo.getId(),!TextUtils.isEmpty(serInfo.getHost())? serInfo.getHost() : "null",serInfo.getPort(),ser.mHost,ser.mPort));
        		        }
            		}
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

    public static LogListener mLogListener = new LogListener(){

		@Override
		public void onLog(String tag, String msg) {
			Logger.v(msg);
		}
    };
}
