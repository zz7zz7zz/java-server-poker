package com.poker.access.handler;


import com.open.net.client.object.AbstractClient;
import com.open.net.server.GServer;
import com.open.net.server.object.AbstractServerClient;
import com.open.util.log.Logger;
import com.poker.access.Main;
import com.poker.access.object.User;
import com.poker.access.object.UserPool;
import com.poker.packet.BasePacket;
import com.poker.packet.InPacket;
import com.poker.packet.PacketInfo;

public class ClientMessageHandler {
	
	public void onClinetLogin(AbstractClient mClient , InPacket mInPacket){
		
		long socketId 	= mInPacket.readLong();
		long uid 		= mInPacket.readLong();
		PacketInfo mSubPacket = mInPacket.readBytesToSubPacket();

		User user = Main.userMap.get(uid);
		if(null != user){//说明之前已经连接上了,断掉老的链接
			if(user.socketId != socketId){
				AbstractServerClient client_server_connection = GServer.getClient(user.socketId);
				if(null != client_server_connection){
					client_server_connection.onClose();
					Logger.v("onClinetLogin disconnect oldSocketId " + user.socketId + " NewSocketId "+ socketId);
				}
			}
		}
		
		int ret = 0;
		AbstractServerClient mConnection = GServer.getClient(socketId);
		if(null != mConnection){
			int cmd 		= BasePacket.getCmd(mSubPacket.buff, mSubPacket.header_start);
			int sequenceId 	= BasePacket.getSequenceId(mSubPacket.buff, mSubPacket.header_start);
			int length 		= BasePacket.buildClientData(Main.write_buff_dispatcher, sequenceId, cmd, (byte)0,mSubPacket.buff,mSubPacket.body_start, mSubPacket.body_length);
	        Main.mServerMessageProcessor.unicast(mConnection, Main.write_buff_dispatcher,0,length);
	        User attachUser = (User)mConnection.getAttachment();
	        if(null == attachUser){//1.说明是新的连接，新的登录
	        	
	        	attachUser = UserPool.get(uid);
	        	attachUser.socketId = socketId;
		        mConnection.attach(user);
		        Main.userMap.put(uid, attachUser); 
		        
		        ret = 1;
	        }else if(attachUser.socketId == socketId){//2.老的连接，再次登录
	        	ret = 2;
	        }else if(attachUser.socketId != socketId){//3.老的连接，再次登录（但是老连接绑定的用户的连接与当前逻辑返回的连接不一致）：会跑到这里来吗？？？
	        	attachUser.socketId = socketId;
	        	ret = 3;
	        }
		}else {//这里有可能在登录的瞬间又掉线了
			ret = 4;
		}

		Logger.v("onClinetLogin socketId " + socketId + " uid "+ uid + " ret " + ret);
	}
}
