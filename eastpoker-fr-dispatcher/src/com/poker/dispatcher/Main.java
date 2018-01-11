package com.poker.dispatcher;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.server.GServer;
import com.open.net.server.impl.tcp.nio.NioServer;
import com.open.net.server.message.Message;
import com.open.net.server.message.MessageBuffer;
import com.open.net.server.object.AbstractServerClient;
import com.open.net.server.object.AbstractServerMessageProcessor;
import com.open.net.server.object.ArgsConfig;
import com.open.net.server.object.ServerConfig;
import com.open.net.server.object.ServerLog;
import com.open.net.server.object.ServerLog.LogListener;
import com.open.net.server.utils.NetUtil;
import com.open.util.log.Logger;
import com.open.util.log.base.LogConfig;
import com.poker.base.ServerIds;
import com.poker.cmd.DispatchCmd;
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.handler.MessageHandler;
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

    
    //---------------------------------------Fields----------------------------------------------------
    public static ArgsConfig libArgsConfig;
    public static byte[] write_buff = new byte[16*1024];
    public static ByteBuffer mWriteBuffer  = ByteBuffer.allocate(16*1024);
    public static MessageHandler mHandler = new MessageHandler();
    
    //---------------------------------------Logger----------------------------------------------------
    public static LogListener mLogListener = new LogListener(){

		@Override
		public void onLog(String tag, String msg) {
			Logger.v(msg);
		}
    };
    
    //---------------------------------------Monitor----------------------------------------------------
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
    
    //---------------------------------------Server: receive msg from client ----------------------------------------------------
    public static AbstractServerMessageProcessor mServerMessageProcessor = new AbstractServerMessageProcessor() {

        private long oldTime = System.currentTimeMillis();
        private long nowTime  = oldTime;
        
        protected void onReceiveMessage(AbstractServerClient client, Message msg){

        	//过滤异常Message
        	if(null == client || msg.length<=0){
        		return;
        	}
        	
    		//对数据进行拆包/组包过程
        	int code = 0;
        	int full_packet_count = 0;
        	int half_packet_count = 0;
        	//packet struct like this : |--head--|-----body-----|
        	
        	if(null == client.mReceivingMsg){
				int msg_offset = 0;
				int header_length = DataPacket.getHeaderLength();
				while(true){
					int the_rest_msg_length = msg.length -msg_offset ;
					if(the_rest_msg_length == 0){
						code = 1;
						break;
					}else if(the_rest_msg_length <= header_length){//说明还没有接收完完整的一个包头，继续读取
	    				client.mReceivingMsg = new Message();
	    				System.arraycopy(msg.data,msg.offset+msg_offset,client.mReceivingMsg.data,client.mReceivingMsg.offset,the_rest_msg_length);
        				client.mReceivingMsg.length = the_rest_msg_length;
        				
        				code = -101;//不足包头
        				half_packet_count++;
        				break;
	    			}else if(the_rest_msg_length > header_length){
            			int header_start 	= msg.offset+ msg_offset;
	    				int packetLength = DataPacket.getLength(msg.data,header_start);
	            		if(the_rest_msg_length >= packetLength){//说明可以凑成一个包

	            			int body_start 		= header_start + header_length;
	            			int body_length     = DataPacket.getLength(msg.data, header_start)-header_length;
	            			
	            			int cmd = DataPacket.getCmd(msg.data, header_start);
	            			onHandleCmd(client,cmd,msg,body_start,body_length);
	            			msg_offset += packetLength;
	            			
	            			full_packet_count++;
	            			continue;
	            		}else{//如果不足一个包
	            			int capacity = 16384;//16KB
	            			if(the_rest_msg_length >= DataPacket.Header.OFFSET_SEQUENCEID){//可以读出包体的长度,尽量传递真实的长度
	            				capacity = DataPacket.getLength(msg.data,msg.offset+msg_offset);
	            			}
		    				client.mReceivingMsg = MessageBuffer.getInstance().buildWithCapacity(capacity,msg.data,msg.offset+msg_offset,the_rest_msg_length);
	        				
	        				code = -102;//足包头，不足包体-->不足整包
	        				half_packet_count++;
	        				break;
	            		}
	    			}
				}
        	}else{//说明有分包现象，只接收了部分包，未收到整包
				int msg_offset = 0;
				int header_length = DataPacket.getHeaderLength();
    			while(true){
    				int the_rest_msglength = msg.length -msg_offset ;
    				if(the_rest_msglength == 0){
    					code = 2;
    					break;
    				}
    				
    				if(client.mReceivingMsg.length < header_length){//说明还没有读取完完整的一个包头，继续读取
        				int remain_header_length = header_length - client.mReceivingMsg.length;
        				int real_read_remain_header_length = Math.min(remain_header_length, the_rest_msglength);
        				if(real_read_remain_header_length >0){
            				System.arraycopy(msg.data,msg.offset+msg_offset,client.mReceivingMsg.data,client.mReceivingMsg.offset+client.mReceivingMsg.length,real_read_remain_header_length);
            				client.mReceivingMsg.length += real_read_remain_header_length;
            				msg_offset += real_read_remain_header_length;
        				}
        			}
        			
        			if(client.mReceivingMsg.length >= header_length){//说明包头读完了，接着读取包体了
        				int packetLength = DataPacket.getLength(client.mReceivingMsg.data,client.mReceivingMsg.offset);
        				int remain_packet_length = packetLength - client.mReceivingMsg.length;
        				int real_read_remain_packet_length = Math.min(remain_packet_length, the_rest_msglength);
        				if(real_read_remain_packet_length >0){
            				System.arraycopy(msg.data,msg.offset+msg_offset,client.mReceivingMsg.data,client.mReceivingMsg.offset+client.mReceivingMsg.length,real_read_remain_packet_length);
            				client.mReceivingMsg.length += real_read_remain_packet_length;
            				msg_offset += real_read_remain_packet_length;
        				}
        				
        				if(client.mReceivingMsg.length == packetLength){//说明包完整了
        					
	            			int header_start 	= client.mReceivingMsg.offset;
	            			int body_start 		= header_start + header_length;
	            			int body_length     = DataPacket.getLength(client.mReceivingMsg.data, header_start)-header_length;
	            			
        					int cmd = DataPacket.getCmd(client.mReceivingMsg.data, header_start);
	            			onHandleCmd(client,cmd,client.mReceivingMsg,body_start,body_length);
	            			
        					full_packet_count++;
        					
        					client.mReceivingMsg.length = 0;
                			if(msg.length - msg_offset > 0){//说明需要继续接收,还有粘包现象
                				continue;
                			}else{//说明可以回收
                				code = 3;
                				MessageBuffer.getInstance().release(client.mReceivingMsg);
                				client.mReceivingMsg = null;
                				break;
                			}
        				}else if(client.mReceivingMsg.length < packetLength){//说明包还未完整
            				code = -202;
            				half_packet_count++;
        					break;
        				}else {//说明异常了，需要重连
        					code = -203;
        					break;
        				}
        			}else{
        				code = -201;//不足包头
        				half_packet_count++;
        				break;
        			}
    			}
    		}
    		
    		Logger.v("code "+ code +" full_packet_count " + full_packet_count + " half_packet_count " + half_packet_count + System.getProperty("line.separator"));
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

		}

		@Override
		public void onClientExit(AbstractServerClient client) {
			mHandler.exit(client);
		}
		
		public void onHandleCmd(AbstractServerClient client, int cmd ,Message msg,int body_start,int body_length){
        	try {
        		
        		Logger.v("onReceiveMessage 0x" + Integer.toHexString(cmd));
        		
        		if(cmd == DispatchCmd.CMD_REGISTER){
        			mHandler.register(client, msg, body_start,body_length);
        		}else if(cmd == DispatchCmd.CMD_DISPATCH){
        			mHandler.dispatch(client, msg, body_start, body_length,mWriteBuffer,mServerMessageProcessor);
        		}else if(cmd == DispatchCmd.CMD_DISPATCH_GAME_GROUP){
        			mHandler.dispatchGameGoup(client, msg, body_start, body_length, mWriteBuffer, mServerMessageProcessor);
        		}else if(cmd == DispatchCmd.CMD_DISPATCH_MATCH_GROUP){
        			mHandler.dispatchMatchGroup(client, msg, body_start, body_length, mWriteBuffer, mServerMessageProcessor);
        		}else{
        			
        		}
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}
		}
    };
   
}
