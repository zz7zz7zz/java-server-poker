package com.poker.user.handler;


import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.object.AbstractClient;
import com.open.util.log.Logger;
import com.poker.access.object.User;
import com.poker.access.object.UserPool;
import com.poker.cmd.AllocatorCmd;
import com.poker.cmd.GameCmd;
import com.poker.cmd.LoginCmd;
import com.poker.cmd.UserCmd;
import com.poker.data.DistapchType;
import com.poker.packet.BasePacket;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.packet.PacketInfo;
import com.poker.packet.PacketTransfer;
import com.poker.protocols.game.LoginGameProto.LoginGame;
import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;
import com.poker.user.Main;

public class ClientHandler extends AbsClientHandler{


	public ClientHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
	}
	
	@Override
	public void dispatchMessage(AbstractClient client, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {
		
		try {
    		int cmd   = BasePacket.getCmd(data, header_start);
    		int squenceId = BasePacket.getSequenceId(data, header_start);
    		Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + LoginCmd.getCmdString(cmd) + " length " + BasePacket.getLength(data,header_start));
        	
    		if(cmd == UserCmd.CMD_LOGIN_GAME){
        		loginGame(squenceId,data, header_start,header_length,body_start, body_length);
        	}else if(cmd == UserCmd.CMD_ENTER_ROOM){
        		enterRoom(squenceId,data, header_start,header_length,body_start, body_length);
        	}else if(cmd == UserCmd.CMD_LEAVE_ROOM){
        		leaveRoom(squenceId,data, header_start,header_length,body_start, body_length);
        	}
    		
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	
	public void loginGame(int squenceId ,byte[] data, int header_start, int header_length, int body_start, int body_length) throws InvalidProtocolBufferException{
		
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		long uid = mDispatchPacket.getDispatchChainList(0).getUid();
		
		mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());
		int accessId = mInPacket.readInt();
		PacketInfo mSubPacket = mInPacket.readBytesToSubPacket();
		LoginGame loginGameRequest = LoginGame.parseFrom(mSubPacket.buff,mSubPacket.body_start, mSubPacket.body_length);
		
		int request_gameid = loginGameRequest.getGameid();
		int request_gamelevel = loginGameRequest.getLevel();
		
		int tableId = 0;
		short gameId = 0;
		short gameSid = 0;
		short matchId = 0;
		short matchSid = 0;
		User user = Main.userMap.get(uid);
		if(null != user){
			tableId = user.tableId;
			gameId = user.gameId;
			gameSid = user.gameSid;
			matchId = user.matchId;
			matchSid = user.matchSid;
		}
		
		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
		byte[] mTempBuff = mInPacket.getPacket();
		if(tableId > 0){//说明没有在游戏中，去Alloc中寻找桌子再进入游戏
			int length = PacketTransfer.send2Game(gameSid, mTempBuff, squenceId, uid, GameCmd.CMD_LOGIN_GAME, DistapchType.TYPE_P2P, mOutPacket.getPacket(),0,  0);
			send2Dispatch(mTempBuff,0,length);	
		}else{
			
			mOutPacket.begin(squenceId, AllocatorCmd.CMD_LOGIN_GAME);
			mOutPacket.writeInt(accessId);//AccessId
			mOutPacket.writeBytes(mSubPacket.buff, mSubPacket.body_start, mSubPacket.body_length);
			mOutPacket.end();
			
			int length = PacketTransfer.send2Alloc(request_gameid,mTempBuff, squenceId, uid,AllocatorCmd.CMD_LOGIN_GAME,DistapchType.TYPE_P2P,mOutPacket.getPacket(),0,  mOutPacket.getLength());
	  		send2Dispatch(mTempBuff,0,length);	
		}
	}
	
	public void enterRoom(int squenceId ,byte[] data, int header_start, int header_length, int body_start, int body_length) throws InvalidProtocolBufferException{
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		long uid = mDispatchPacket.getDispatchChainList(0).getUid();
		
		mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());
		int tid = mInPacket.readInt();
		short gameId = mInPacket.readShort();
		short gameSid = mInPacket.readShort();
		
		User user = Main.userMap.get(uid);
		if(null != user){
			user = UserPool.get(uid);
		}
		
		user.tableId = tid;
		user.gameId = gameId;
		user.gameSid = gameSid;
	}
	
	public void leaveRoom(int squenceId ,byte[] data, int header_start, int header_length, int body_start, int body_length) throws InvalidProtocolBufferException{
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		long uid = mDispatchPacket.getDispatchChainList(0).getUid();
		User user = Main.userMap.remove(uid);
		UserPool.release(user);
	}
}
