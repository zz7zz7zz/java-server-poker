package com.poker.access.handler;

import java.util.HashMap;

import com.open.net.client.object.AbstractClient;
import com.open.net.server.GServer;
import com.open.net.server.object.AbstractServerClient;
import com.poker.access.Main;
import com.poker.access.object.User;
import com.poker.access.object.UserPool;
import com.poker.data.DataPacket;
import com.poker.packet.InPacket;

public class ClientMessageHandler {
	

	
	public void onClinetLogin(AbstractClient mClient , InPacket mInPacket){
		
		long socketId 	= mInPacket.readLong();
		long uid 		= mInPacket.readLong();
		
		//方法二：使用下面的方法代替，避免创建多余的数组，浪费内存
		int[] offset_length_array = mInPacket.readBytesOffsetAndLenth();
		int packet_start = offset_length_array[0];
		int packet_length = offset_length_array[1];
		
		int packet_header_length = DataPacket.getHeaderLength(mInPacket.getPacket(), packet_start);
		
		int packet_body_start = packet_start + packet_header_length;
		int packet_body_length = packet_length - packet_header_length;
		
		int cmd = DataPacket.getCmd(Main.mInPacket.getPacket(), packet_start);
		int sequenceId = DataPacket.getSequenceId(Main.mInPacket.getPacket(), packet_start);
		
		User user = Main.userMap.get(uid);
		if(null != user){//说明之前已经连接上了
			if(user.socketId != socketId){
				AbstractServerClient client_server_connection = GServer.getClient(user.socketId);
				if(null != client_server_connection){
					client_server_connection.onClose();
				}
			}
		}
		
		AbstractServerClient client_server_connection = GServer.getClient(socketId);
		if(null != client_server_connection){
			int length = DataPacket.write(Main.write_buff_dispatcher, sequenceId, cmd, (byte)0,Main.mInPacket.getPacket(),packet_body_start, packet_body_length);
	        Main.mServerMessageProcessor.unicast(client_server_connection, Main.write_buff_dispatcher,0,length);
	        
	        User attachUser = (User)client_server_connection.getAttachment();
	        if(null == attachUser){
	        	
	        	attachUser = UserPool.get(uid);
	        	attachUser.socketId = socketId;
		        client_server_connection.attach(user);
		        
		        Main.userMap.put(uid, attachUser);
		        
	        }else if(attachUser.socketId != socketId){//会跑到这里来吗？？？
	        	attachUser.socketId = socketId;
	        }
		}

        
		System.out.println("onClinetLogin socketId " + socketId + " uid "+ uid + " success " + (null != client_server_connection));
	}
}
