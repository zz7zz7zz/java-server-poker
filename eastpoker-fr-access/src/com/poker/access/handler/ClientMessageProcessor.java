package com.poker.access.handler;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import com.open.net.client.message.Message;
import com.open.net.client.message.MessageBuffer;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.open.util.log.Logger;
import com.poker.cmd.DispatchCmd;
import com.poker.data.DataPacket;
import com.poker.access.Main;

public class ClientMessageProcessor extends AbstractClientMessageProcessor {

	private ClientMessageHandler mHandler;
	
	public ClientMessageProcessor(ClientMessageHandler mHandler) {
		super();
		this.mHandler = mHandler;
	}


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
			int msg_length = msg.length ;
			int base_header_length = DataPacket.getBaseHeaderLength();
			
			while(true){
				
				if(msg_length == 0){
					code = 10;
					break;
				}
				
				//1.说明不足基本包头，继续读取
    			int header_start  = msg.offset+ msg_offset;
				if(msg_length <= base_header_length){
        			int next_pkg_length = 16384;//16KB
        			if(msg_length >= DataPacket.Header.HEADER_OFFSET_SEQUENCEID){//可以读出包体的长度,尽量传递真实的长度
        				next_pkg_length = DataPacket.getLength(msg.data,header_start);
        			}
    				client.mReceivingMsg = MessageBuffer.getInstance().buildWithCapacity(next_pkg_length,msg.data,msg.offset+msg_offset,msg_length);

    				code = -101;//不足基本包头
    				half_packet_count++;
    				break;
    			}
				
				//2.说明足基本包头，不足扩展包头
				int header_length = DataPacket.getHeaderLength(msg.data, header_start);
				if(msg_length <= header_length){
					int next_pkg_length = 16384;//16KB
        			if(msg_length >= DataPacket.Header.HEADER_OFFSET_SEQUENCEID){//可以读出包体的长度,尽量传递真实的长度
        				next_pkg_length = DataPacket.getLength(msg.data,header_start);
        			}
    				client.mReceivingMsg = MessageBuffer.getInstance().buildWithCapacity(next_pkg_length,msg.data,msg.offset+msg_offset,msg_length);

    				code = -102;//不足扩展包头
    				half_packet_count++;
    				break;
				}
				
				//3.说明包头,扩展包头读完了，接着读取包体
				if(msg_length > header_length){
    				int packet_length = DataPacket.getLength(msg.data,header_start);
            		if(msg_length >= packet_length){//说明可以凑成一个包
            			
            			int body_start 		= header_start  + header_length;
            			int body_length     = packet_length - header_length;
            			
            			dispatchMessage(client,msg.data,header_start,header_length,body_start,body_length);
            			
            			msg_offset += packet_length;
            			msg_length -= packet_length;
            			
            			full_packet_count++;
            			continue;
            		}else{//如果不足一个包(足包头，不足包体)
            			int next_pkg_length = packet_length;
	    				client.mReceivingMsg = MessageBuffer.getInstance().buildWithCapacity(next_pkg_length,msg.data,msg.offset+msg_offset,msg_length);
        				
        				code = -103;//足包头，不足包体-->不足整包
        				half_packet_count++;
        				break;
            		}
    			}
			}
    	}else{//说明有分包现象，只接收了部分包，未收到整包
			
    		int msg_offset = 0;
			int msg_length = msg.length ;
			int base_header_length = DataPacket.getBaseHeaderLength();
			
			while(true){

				if(msg_length == 0){
					code = 20;
					break;
				}
				
				//1.说明还没有读取完完整的一个包头，继续读取
				if(client.mReceivingMsg.length < base_header_length){
    				int remain_header_base_length = base_header_length - client.mReceivingMsg.length;
    				int rr_header_base_length = Math.min(remain_header_base_length, msg_length);//真实读取的数据长度real-read short for rr
    				if(rr_header_base_length > 0){
        				System.arraycopy(msg.data,msg.offset+msg_offset,client.mReceivingMsg.data,client.mReceivingMsg.offset+client.mReceivingMsg.length,rr_header_base_length);
        				
        				client.mReceivingMsg.length += rr_header_base_length;
        				
        				msg_offset += rr_header_base_length;
        				msg_length -= rr_header_base_length;
    				}
    				
    				if(rr_header_base_length < remain_header_base_length){//实际读取的数据量 少于 需要的数据量，说明包读取完了
        				code = -201;//不足包头
        				half_packet_count++;
    					break;
    				}else if(msg_length == 0){
        				code = -202;//足包头,但数据读取完了
        				half_packet_count++;
    					break;
    				}
    			}
				
				//2.说明包头读完了，接着读取扩展包头
				int header_start 	= client.mReceivingMsg.offset;
				int header_length   = DataPacket.getHeaderLength(client.mReceivingMsg.data, header_start);
				if(client.mReceivingMsg.length < header_length){
    				//读取扩展包头
    				int remain_header_extend_length = header_length - client.mReceivingMsg.length;
    				int rr_header_extend_length = Math.min(remain_header_extend_length, msg_length);//真实读取的数据长度real-read short for rr
    				if(rr_header_extend_length > 0){
        				System.arraycopy(msg.data,msg.offset+msg_offset,client.mReceivingMsg.data,client.mReceivingMsg.offset+client.mReceivingMsg.length,rr_header_extend_length);
        				
    					client.mReceivingMsg.length += rr_header_extend_length;
    					
    					msg_offset += rr_header_extend_length;
        				msg_length -= rr_header_extend_length;
    				}
    				
    				if(rr_header_extend_length < remain_header_extend_length){//实际读取的数据量 少于 需要的数据量，说明包读取完了
    					code = -203;//不足扩展包头
        				half_packet_count++;
    					break;
    				}else if(msg_length == 0){
        				code = -204;//足扩展包头,但数据读取完了
        				half_packet_count++;
    					break;
    				}
				}
				
				//3.说明包头,扩展包头读完了，接着读取包体
    			if(client.mReceivingMsg.length >= header_length){
    				
    				int packet_length  	= DataPacket.getLength(client.mReceivingMsg.data,header_start);
    				//读取包体
    				int remain_body_length = packet_length - client.mReceivingMsg.length;
    				int rr_body_length = Math.min(remain_body_length, msg_length);//真实读取的数据长度real-read short for rr
    				if(rr_body_length >0){
        				System.arraycopy(msg.data,msg.offset+msg_offset,client.mReceivingMsg.data,client.mReceivingMsg.offset+client.mReceivingMsg.length,rr_body_length);
        				
        				client.mReceivingMsg.length += rr_body_length;
        				
    					msg_offset += rr_body_length;
        				msg_length -= rr_body_length;
    				}
    				if(rr_body_length < remain_body_length){//实际读取的数据量 少于 需要的数据量，说明包读取完了
    					code = -205;//不足包体
        				half_packet_count++;
    					break;
    				}
    				
    				if(client.mReceivingMsg.length == packet_length){//说明包完整了

            			int body_start 		= header_start 	+ header_length;
            			int body_length     = packet_length - header_length;
            			
            			dispatchMessage(client,client.mReceivingMsg.data,header_start,header_length,body_start,body_length);
            			
    					full_packet_count++;
    					
    					client.mReceivingMsg.length = 0;
    					
            			if(msg_length > 0){//剩下还有数据,说明还有粘包现象
            				
            				//先看看client.mReceivingMsg是否可以复用
            				if(msg_length >= DataPacket.Header.HEADER_OFFSET_SEQUENCEID){
            					int next_pkg_length = DataPacket.getLength(msg.data,msg.offset+msg_offset);
            					if(next_pkg_length > client.mReceivingMsg.capacity){//说明不可以复用
                    				MessageBuffer.getInstance().release(client.mReceivingMsg);
                    				client.mReceivingMsg = MessageBuffer.getInstance().buildWithCapacity(next_pkg_length,msg.data,msg.offset+msg_offset,0);//说明不可以复用，以实际容量为准
            					}else{
            						continue;//说明可以复用
            					}
            				}else{
            					int next_pkg_length = 16384;
            					client.mReceivingMsg = MessageBuffer.getInstance().buildWithCapacity(next_pkg_length,msg.data,msg.offset+msg_offset,0);//说明不可以复用，以最大为准
            				}
            				
            				continue;
            			}else{//说明可以回收
            				code = 21;
            				MessageBuffer.getInstance().release(client.mReceivingMsg);
            				client.mReceivingMsg = null;
            				break;
            			}
    				}else if(client.mReceivingMsg.length < packet_length){//说明包还未完整 ,//Error------如果逻辑正常，是不会走到这里来的
        				code = -206;
        				half_packet_count++;
    					break;
    				}else {//说明异常了，需要重连,//Error------如果逻辑正常，是不会走到这里来的
    					code = -207;
    					break;
    				}
    			}else{
    				code = -208;//不足包头,//Error------如果逻辑正常，是不会走到这里来的
    				half_packet_count++;
    				break;
    			}
			}
		}
		
		Logger.v("code "+ code +" full_packet_count " + full_packet_count + " half_packet_count " + half_packet_count + System.getProperty("line.separator"));
    }
	
	@Override
	public void send(AbstractClient mClient, byte[] src, int offset, int length) {
		super.send(mClient, src, offset, length);
		Logger.v("output_packet_broadcast cmd 0x" + Integer.toHexString(DataPacket.getCmd(src, offset)) + " length " + length);
	}
		
	private static ByteBuffer mWriteBuffer  = ByteBuffer.allocate(16*1024);
	public void dispatchMessage(AbstractClient client ,byte[] data,int header_start,int header_length,int body_start,int body_length){
    		int cmd   = DataPacket.getCmd(data, header_start);
		 	Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + DispatchCmd.getCmdString(cmd) + " length " + DataPacket.getLength(data,header_start));
//        	mServerMessageProcessor.unicast(client, src, offset, length);
        	mWriteBuffer.clear();
            mWriteBuffer.put(data,header_start,header_length + body_length);
            mWriteBuffer.flip();
//        unicast(client,mWriteBuffer.array(),0,response.length);
            Main.mServerMessageProcessor.broadcast(mWriteBuffer.array(),0,mWriteBuffer.remaining());
            mWriteBuffer.clear();
	}

}
