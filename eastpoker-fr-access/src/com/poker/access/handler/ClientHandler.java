package com.poker.access.handler;


import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.object.AbstractClient;
import com.open.net.server.GServer;
import com.open.net.server.object.AbstractServerClient;
import com.open.util.log.Logger;
import com.poker.access.Main;
import com.poker.access.object.User;
import com.poker.access.object.UserPool;
import com.poker.cmd.LoginCmd;
import com.poker.packet.BasePacket;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.packet.PacketInfo;
import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;

public class ClientHandler extends AbsClientHandler{
	
	public ClientHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
	}

	public void dispatchMessage(AbstractClient client ,byte[] data,int header_start,int header_length,int body_start,int body_length){
		
		try {	
			DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
			int  cmd = mDispatchPacket.getDispatchChainList(0).getCmd();

			if(cmd == LoginCmd.CMD_LOGIN_RESPONSE){
            	onClinetLogin(client, mDispatchPacket);
            }else {
            	sendDataToClient(client, mDispatchPacket);
            }
            
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	
	public void onClinetLogin(AbstractClient mClient , DispatchPacket mDispatchPacket){
		long uid = mDispatchPacket.getDispatchChainList(0).getUid();
		mInPacket.copyFrom(mDispatchPacket.getData());
		
		long socketId 	= mInPacket.readLong();
		uid 		= mInPacket.readLong();
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
	        Main.mServerHandler.unicast(mConnection, mSubPacket.buff,mSubPacket.header_start,mSubPacket.length);
	        User attachUser = (User)mConnection.getAttachment();
	        if(null == attachUser){//1.说明是新的连接，新的登录
	        	
	        	attachUser = UserPool.get(uid);
	        	attachUser.socketId = socketId;
		        mConnection.attach(attachUser);
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
	
	public void sendDataToClient(AbstractClient mClient , DispatchPacket mDispatchPacket){
		long uid = mDispatchPacket.getDispatchChainList(0).getUid();
		mInPacket.copyFrom(mDispatchPacket.getData());
		
		int ret = 0;
		
		User user = Main.userMap.get(uid);
		if(null == user){//说明之前已经连接上了,断掉老的链接
			ret = 1;
		}else{
			AbstractServerClient mConnection = GServer.getClient(user.socketId);
			if(null != mConnection){
				int  cmd = mDispatchPacket.getDispatchChainList(0).getCmd();
				int sequenceId 	= mDispatchPacket.getSequenceId();
				
				//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
				byte[] mTempBuff = mOutPacket.getPacket();
				
				int length 		= BasePacket.buildClientPacekt(mTempBuff, sequenceId, cmd, (byte)0,mInPacket.getPacket(),0,mInPacket.getLength());
		        Main.mServerHandler.unicast(mConnection, mTempBuff,0,length);
			}else {//这里有可能在登录的瞬间又掉线了
				ret = 2;
			}
		}

		Logger.v("sendDataToClient socketId uid "+ uid + " ret " + ret);
	}
}
