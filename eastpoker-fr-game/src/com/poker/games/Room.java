package com.poker.games;

import java.util.HashMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.util.log.Logger;
import com.poker.cmd.AllocatorCmd;
import com.poker.cmd.GameCmd;
import com.poker.cmd.UserCmd;
import com.poker.common.config.Config;
import com.poker.data.DistapchType;
import com.poker.game.Main;
import com.poker.games.impl.GTable;
import com.poker.games.impl.config.CardConfig;
import com.poker.games.impl.config.GameConfig;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.packet.PacketTransfer;
import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;


public class Room {
	
	private final String TAG = "Room";
	
	static InPacket  mInPacket;
	static OutPacket mOutPacket;
	
	public HashMap<Long,User> userMap = new HashMap<>();
	public Table mTables[];
	public short gameId;
	public short gameSid;
	
	public Room(Config mConfig) {
		
		GameConfig mGameConfig = new GameConfig();
		mGameConfig.initFileConfig("./conf-game/game.config");
		
		CardConfig mCardConfig = new CardConfig();
		mCardConfig.initFileConfig("./conf-game/card.config");
		
		gameId = mConfig.game_id;
		gameSid= mConfig.server_id;
		
		mTables = new Table[mConfig.table_count];
		for(int i=0;i<mConfig.table_count;i++){
			int tableId = (mConfig.server_id << 16) + i;
			mTables[i] = new GTable(tableId,mConfig,mGameConfig,mCardConfig);
		}
		
		//预先分配1/4桌子数目的用户，每次增长1/4桌子数目的用户
		int user_init_size = (int)(0.25*mConfig.table_count) * mConfig.table_max_user;
		UserPool.init(user_init_size,user_init_size);
		
		mInPacket = new InPacket(Main.libClientConfig.packet_max_length_tcp);
		mOutPacket= new OutPacket(Main.libClientConfig.packet_max_length_tcp);
	}

	//---------------------------------------------------------------
	public void dispatchRoomMessage(int cmd , byte[] data,int header_start,int header_length,int body_start,int body_length) throws InvalidProtocolBufferException{
		
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		long uid = mDispatchPacket.getDispatchChainList(0).getUid();
		
		int tid = 0;
		
		int tidIndex = tid & 0xff;
		if(tidIndex <0 || tidIndex >= mTables.length){
			return;
		}
		
		Table mTable = mTables[tidIndex];
		if(null == mTable){
			return;
		}
		
		User mUser = userMap.get(uid);
		if(null != mUser){//说明之前在桌子上,替换上真实的桌子
			if(mUser.tid != tid){
				Logger.v(TAG+ "tid.diff tid " + tid + " realtid "+mUser.tid);
				Table realTable = mTables[tidIndex];
				mTable = realTable;
			}
		}
		
		if(cmd == GameCmd.CMD_LOGIN_GAME){
			if(null == mUser){
				mUser = UserPool.get(uid);
			}
			
			int ret = mTable.onUserLogin(mUser);
			
			if(ret == 1){
				userMap.put(uid, mUser);
				enterRoom(uid, mTable);
				//更新桌子人数
			}else{
				UserPool.release(mUser);
				leaveRoom(uid);
			}
    	}else{
    		
    		if(null == mUser){
    			Logger.v(TAG+ "cmd be unable to handle uid " + uid + " cmd "+cmd);
				return;
			}
    		
    		if(cmd == GameCmd.CMD_USER_EXIT){
    			int ret = mTable.onUserExit(mUser);
    			if(ret == 1){
    				leaveRoom(uid);
    				UserPool.release(mUser);
    				//更新桌子人数
    			}
        	}else if(cmd == GameCmd.CMD_USER_READY){
    			mTable.onUserReady(mUser);
        	}else if(cmd == GameCmd.CMD_USER_OFFLINE){
        		mTable.onUserOffline(mUser);
        	}else if(cmd == GameCmd.CMD_KICK_USER){
        		mTable.onKickUser(mUser, null);;
        	}else{
        		mTable.dispatchTableMessage(mUser,cmd, data, header_start, header_length, body_start, body_length);
        	}
    	}
	}
	
	public void enterRoom(long uid,Table table){
		
		mOutPacket.begin(0, AllocatorCmd.CMD_LOGIN_GAME);
		mOutPacket.writeInt(table.tableId);
		mOutPacket.writeShort(gameId);
		mOutPacket.writeShort(gameSid);
		mOutPacket.end();
		
		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
		byte[] mTempBuff = mInPacket.getPacket();
		int length = PacketTransfer.send2User(0, mTempBuff, 0, uid, UserCmd.CMD_ENTER_ROOM, DistapchType.TYPE_P2P, mOutPacket.getPacket(),0,  mOutPacket.getLength());
		Main.send2Dispatch(mTempBuff,0,length);	
	}
	
	public void leaveRoom(long uid){
		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
		byte[] mTempBuff = mInPacket.getPacket();
		
		int length = PacketTransfer.send2User(0, mTempBuff, 0, uid, UserCmd.CMD_LEAVE_ROOM, DistapchType.TYPE_P2P, mOutPacket.getPacket(),0,  0);
		Main.send2Dispatch(mTempBuff,0,length);	
	}
	
//	public static void main(String arg[]){
//		int sid = 1;
//		int tid = (sid<<16) + 5;
//		System.out.println(tid);
//		
//		int index = tid & 0xff;
//		int sidout= (tid>>16);
//		System.out.println(sidout);
//		System.out.println(index);
//	}
}
