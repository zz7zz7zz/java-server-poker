package com.poker.access.handler;


import com.open.net.base.util.ExceptionUtil;
import com.open.net.client.object.AbstractClient;
import com.open.net.server.GServer;
import com.open.net.server.object.AbstractServerClient;
import com.open.util.log.Logger;
import com.poker.access.Main;
import com.poker.access.object.User;
import com.poker.access.object.UserPool;
import com.poker.base.cmd.AccessCmd;
import com.poker.base.cmd.Cmd;
import com.poker.base.cmd.LoginCmd;
import com.poker.base.data.DataCryptor;
import com.poker.base.packet.BasePacket;
import com.poker.base.packet.InPacket;
import com.poker.base.packet.OutPacket;
import com.poker.base.packet.PacketInfo;
import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;

public class ClientHandler extends AbsClientHandler{
	
	public ClientHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
	}

	public void dispatchMessage(AbstractClient client ,byte[] data,int header_start,int header_length,int body_start,int body_length){
		
		try {	
			DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
			int  cmd = mDispatchPacket.getDispatchChainList(0).getCmd();
			
    		Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + Cmd.getCmdString(cmd) + " length " + BasePacket.getLength(data,header_start));
    		
			if(cmd == LoginCmd.CMD_LOGIN_RESPONSE){
            	onClinetLogin(client, mDispatchPacket);
            }else if(cmd == AccessCmd.CMD_LOGIN_GAME){
            	onLoginGame(client, mDispatchPacket);
            }else {
            	sendDataToClient(client, mDispatchPacket);
            }
            
		} catch (Exception e) {
			e.printStackTrace();
			Logger.v(ExceptionUtil.getStackTraceString(e));
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
	
	public void onLoginGame(AbstractClient mClient , DispatchPacket mDispatchPacket){
		long uid = mDispatchPacket.getDispatchChainList(0).getUid();
		mInPacket.copyFrom(mDispatchPacket.getData());
		
		int tid       = mInPacket.readInt();
		short gameId  = mInPacket.readShort();
		short gameSid = mInPacket.readShort();


		User user = Main.userMap.get(uid);
		if(null != user){//说明之前已经连接上了,断掉老的链接
			user.gameId = gameId;
			user.gameSid= gameSid;
			user.tid    = tid;
			
			Logger.v("onLoginGame socketId  uid "+ uid);
		}

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
				
				//对包体数据进行加密
				DataCryptor.encrypt(mInPacket.getPacket(),0,mDispatchPacket.getData().size());
				
				int length 		= BasePacket.buildClientPacekt(mTempBuff, sequenceId, cmd, (byte)0,mInPacket.getPacket(),0,mDispatchPacket.getData().size());
		        Main.mServerHandler.unicast(mConnection, mTempBuff,0,length);
			}else {
				ret = 2;
			}
		}

		Logger.v("sendDataToClient socketId uid "+ uid + " ret " + ret);
	}
}
