package com.poker.access.handler;

import com.open.net.client.impl.tcp.nio.NioClient;
import com.open.net.server.message.Message;
import com.open.net.server.message.MessageBuffer;
import com.open.net.server.object.AbstractServerClient;
import com.open.net.server.object.AbstractServerMessageProcessor;
import com.open.util.log.Logger;
import com.poker.base.ServerIds;
import com.poker.cmd.DispatchCmd;
import com.poker.data.DataPacket;
import com.poker.access.Main;

public class ServerMessageProcessor extends AbstractServerMessageProcessor{

	public ServerMessageHandler mHandler;
    public byte[] write_buff;
    
	public ServerMessageProcessor(ServerMessageHandler mHandler, byte[] writeBuffer) {
		super();
		this.mHandler = mHandler;
		this.write_buff = writeBuffer;
	}

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
        			int capacity = 16384;//16KB
        			if(the_rest_msg_length >= DataPacket.Header.OFFSET_SEQUENCEID){//可以读出包体的长度,尽量传递真实的长度
        				capacity = DataPacket.getLength(msg.data,msg.offset+msg_offset);
        			}
    				client.mReceivingMsg = MessageBuffer.getInstance().buildWithCapacity(capacity,msg.data,msg.offset+msg_offset,the_rest_msg_length);
    				
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
            			dispatchMessage(client,cmd,msg,header_start,header_length,body_start,body_length);
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
            			dispatchMessage(client,cmd,client.mReceivingMsg,header_start,header_length,body_start,body_length);
            			
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
	public void onClientEnter(AbstractServerClient arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClientExit(AbstractServerClient client) {

	}

	@Override
	public void onTimeTick() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void unicast(AbstractServerClient client, byte[] src, int offset, int length) {
		super.unicast(client, src, offset, length);
		Logger.v("output_packet_unicast cmd 0x" + Integer.toHexString(DataPacket.getCmd(src, offset)) + " length " + length);
	}

	@Override
	public void multicast(AbstractServerClient[] clients, byte[] src, int offset, int length) {
		super.multicast(clients, src, offset, length);
		Logger.v("output_packet_multicast cmd 0x" + Integer.toHexString(DataPacket.getCmd(src, offset)) + " length " + length);
	}

	@Override
	public void broadcast(byte[] src, int offset, int length) {
		super.broadcast(src, offset, length);
		Logger.v("output_packet_broadcast cmd 0x" + Integer.toHexString(DataPacket.getCmd(src, offset)) + " length " + length);
	}
	
	//-----------------------------------------------------------------------------------------------------------------------------------
	public void dispatchMessage(AbstractServerClient client, int cmd ,Message msg,int header_start,int header_length,int body_start,int body_length){
		Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + DispatchCmd.getCmdString(cmd) + " length " + DataPacket.getLength(msg.data,header_start));
		
		int server = cmd >> 16;
      	int squenceId = DataPacket.getSequenceId(msg.data,msg.offset);
      	
      	//如果Server大于0，则将数据转发至对应的server
      	if(server > 0){
      		int length = 0;
      		if(server == ServerIds.SERVER_LOGIN){
      			length = ImplDataTransfer.send2Login(write_buff, squenceId, msg.data,msg.offset,msg.length);
      		}else if(server == ServerIds.SERVER_USER){
      			length = ImplDataTransfer.send2User(write_buff, squenceId, msg.data,msg.offset,msg.length);
      		}
      		
      		Main.dispatchIndex = (Main.dispatchIndex+1) % Main.dispatcher.length;
      		NioClient mNioClient = Main.dispatcher[Main.dispatchIndex];
      		if(mNioClient.isConnected()){
      			Main.mClientMessageProcessor.send(mNioClient,write_buff,0,length);
      		}else{
      			for(int i = 0;i<Main.dispatcher.length;i++){
      				mNioClient = Main.dispatcher[Main.dispatchIndex];
              		if(mNioClient.isConnected()){
              			Main.mClientMessageProcessor.send(mNioClient,write_buff,0,length);
              			break;
              		}
      			}
      		}
      	}
	}
}
