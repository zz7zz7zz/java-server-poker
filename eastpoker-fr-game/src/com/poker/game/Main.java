package com.poker.game;

import java.util.Arrays;
import java.util.LinkedList;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.GClient;
import com.open.net.client.impl.tcp.nio.NioClient;
import com.open.net.client.message.Message;
import com.open.net.client.message.MessageBuffer;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.open.net.client.object.ClientConfig;
import com.open.net.client.object.IConnectListener;
import com.open.net.client.object.TcpAddress;
import com.open.net.server.object.ArgsConfig;
import com.open.net.server.object.ServerLog;
import com.open.net.server.object.ServerLog.LogListener;
import com.open.net.server.utils.NetUtil;
import com.open.util.log.Logger;
import com.open.util.log.base.LogConfig;
import com.poker.base.ServerIds;
import com.poker.cmd.AllocatorCmd;
import com.poker.cmd.DispatchCmd;
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.games.Room;
import com.poker.handler.MessageHandler;
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
    	libArgsConfig.server_type = ServerIds.SERVER_GAME;
        
        //1.3 服务器配置初始化:作为客户端配置
        ClientConfig libClientConfig = new ClientConfig();
        libClientConfig.initFileConfig("./conf/lib.client.config");
        GClient.init(libClientConfig);
        
        //1.3 日志配置初始化
        LogConfig libLogConfig = Logger.init("./conf/lib.log.config",libArgsConfig.id);
        Logger.addFilterTraceElement(ServerLog.class.getName());
        Logger.addFilterTraceElement(mLogListener.getClass().getName());
        
        //1.4 业务配置初始化
        mConfig = new Config();
        mConfig.initFileConfig("./conf/server.config");
        
        mRoom = new Room(mConfig);
        
        Logger.v("libArgsConfig: "+ libArgsConfig.toString()+"\r\n");
        Logger.v("libClientConfig: "+ libClientConfig.toString()+"\r\n");
        Logger.v("libLogConfig: "+ libLogConfig.toString()+"\r\n");
        Logger.v("mConfig: "+ mConfig.toString()+"\r\n");
        
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

    //---------------------------------------Global----------------------------------------------------
    public static ArgsConfig libArgsConfig;
    public static Config mConfig;
    public static NioClient [] dispatcher;
    public static byte[] write_buf = new byte[16*1024];
    public static byte[] write_buff_dispatcher = new byte[16*1024];
    public static MessageHandler mHandler = new MessageHandler();
    public static Room mRoom;
    //---------------------------------------Monitor----------------------------------------------------
    public static void register_monitor(Config mConfig){
        Monitor.register2Monitor(write_buf,libArgsConfig.server_type,libArgsConfig.name, libArgsConfig.id,libArgsConfig.host, libArgsConfig.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,write_buf,0,DataPacket.getLength(write_buf));
    		}
    	}
    }
    
    public static void unregister_monitor(Config mConfig){
    	Monitor.unregister2Monitor(write_buf,libArgsConfig.server_type,libArgsConfig.name, libArgsConfig.id,libArgsConfig.host, libArgsConfig.port);
        int monitorSize = (null != mConfig.monitor_net_udp) ? mConfig.monitor_net_udp.length : 0;
    	if(monitorSize > 0){
    		for(int i=0; i< monitorSize ; i++){
    			NetUtil.send_data_by_udp_nio(mConfig.monitor_net_udp[i].ip, mConfig.monitor_net_udp[i].port,write_buf,0,DataPacket.getLength(write_buf));
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
    
	private static IConnectListener mDisPatcherConnectResultListener = new IConnectListener() {
		@Override
		public void onConnectionSuccess(AbstractClient client) {
			Logger.v("-------dispatcher onConnectionSuccess---------" +Arrays.toString(((NioClient)client).getConnectAddress()));
			//register to dispatchServer
			int length = Dispatcher.register2Dispatcher(write_buf,libArgsConfig.server_type,libArgsConfig.name, libArgsConfig.id,libArgsConfig.host, libArgsConfig.port,mConfig.game_id,-1);
			mDisPatcherMessageProcessor.send(client,write_buf,0,length);
			
			//上报桌子信息
			mHandler.report_roominfo(client,write_buff_dispatcher, write_buf, 1, mDisPatcherMessageProcessor, mConfig);
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

    //-------------------------------------------------------------------------------------------
    public static LogListener mLogListener = new LogListener(){

		@Override
		public void onLog(String tag, String msg) {
			Logger.v(msg);
		}
    };
    
    //-------------------------------------------------------------------------------------------
	private static AbstractClientMessageProcessor mDisPatcherMessageProcessor =new AbstractClientMessageProcessor() {

		@Override
		public void onReceiveMessages(AbstractClient mClient, LinkedList<Message> list) {
			for(int i = 0 ;i<list.size();i++){
				Message msg = list.get(i);
				onReceiveMessage(mClient,msg);
			}
		}
		
		 protected void onReceiveMessage(AbstractClient client, Message msg){

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
	            			onHandleCmd(client,cmd,msg,header_start,header_length,body_start,body_length);
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
	            			onHandleCmd(client,cmd,client.mReceivingMsg,header_start,header_length,body_start,body_length);
	            			
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
		
		
		 public void onHandleCmd(AbstractClient client, int cmd ,Message msg,int header_start,int header_length,int body_start,int body_length){
	        	try {
	        		
	        		Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + DispatchCmd.getCmdString(cmd) + " length " + DataPacket.getLength(msg.data,header_start));
	        		
		        	if(cmd == AllocatorCmd.CMD_ALLOCATOR_BROADCAST_GET_ROOMINFO){
		        		mHandler.on_get_roominfo(client,write_buff_dispatcher,write_buf,1,mDisPatcherMessageProcessor,mConfig,mRoom);
		        	}
				} catch (InvalidProtocolBufferException e) {
					e.printStackTrace();
				}
			}
		 
	};
}
