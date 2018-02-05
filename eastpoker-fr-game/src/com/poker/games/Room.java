package com.poker.games;

import java.util.HashMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.util.log.Logger;
import com.poker.cmd.GameCmd;
import com.poker.common.config.Config;
import com.poker.games.define.UserPool;
import com.poker.games.define.GameDefine.LoginResult;
import com.poker.games.impl.GTable;
import com.poker.games.impl.config.CardConfig;
import com.poker.games.impl.config.GameConfig;

import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;


public class Room {
	
	private final String TAG = "Room";
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
	}

	//---------------------------------------------------------------
	public void dispatchRoomMessage(int cmd , byte[] data,int header_start,int header_length,int body_start,int body_length) throws InvalidProtocolBufferException{
		
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		long uid = mDispatchPacket.getDispatchChainList(0).getUid();
		
		if(uid <=0){
			Logger.vt(TAG, "error uid " + uid);
			return;
		}
		
		Table mTable = null;
		User mUser = userMap.get(uid);
		if(null != mUser){//说明之前在桌子上,替换上真实的桌子
			if(mUser.tid >= 0){
				mTable = mTables[mUser.tid & 0xff];
			}
		}
		
		if(cmd == GameCmd.CMD_LOGIN_GAME){
			
			Table.mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());	
			int accessId = Table.mInPacket.readInt();
			int tableId  = Table.mInPacket.readInt();
			
			int tableIdIndex = tableId & 0xff;
			if(tableIdIndex <0 || tableIdIndex >= mTables.length){
				Logger.v(TAG+ "error uid  tid.diff tid " + tableId + " index "+ tableIdIndex);
				return;
			}
			
			Table mRequestTable = mTables[tableIdIndex];
			if(null == mTable){
				mTable = mRequestTable;
			}else{
				Logger.v(TAG+ "error uid  req.tid " + tableId + " req.tid.index "+ tableIdIndex+ " realtid "+mUser.tid + " realIndex" + (mUser.tid & 0xff));
			}
			
			
    		if(null == mUser){
    			mUser = UserPool.get(uid);
    		}
    		mUser.accessId = accessId;
    		
    		loginGame(mUser,mTable);
    		
    	}else{

    		if(null == mUser){
    			Logger.v(TAG+ "cmd be unable to handle user null " + uid + " cmd "+cmd);
				return;
			}
    		
    		if(null == mTable){
    			Logger.v(TAG+ "cmd be unable to handle mTable null ");
				return;
			}
    		
    		if(cmd == GameCmd.CMD_CHECK_GAME_STATUS){
    			
    			Table.mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());	
    			int accessId = Table.mInPacket.readInt();
    			mUser.accessId =  accessId;
    			checkGameStatus(mUser,mTable);
    			
        	}else if(cmd == GameCmd.CMD_USER_EXIT){
        		
        		logoutGame(mUser,mTable);
        		
        	}else if(cmd == GameCmd.CMD_USER_READY){
        		
    			mTable.onUserReady(mUser);
    			
        	}else if(cmd == GameCmd.CMD_USER_OFFLINE){
        		
        		mTable.onUserOffline(mUser);
        		
        	}else if(cmd == GameCmd.CMD_KICK_USER){
        		
        		mTable.onKickUser(mUser, null);
        		
        	}else{
        		mTable.dispatchTableMessage(mUser,cmd, data, header_start, header_length, body_start, body_length);
        	}
    	}
	}
	
	private void checkGameStatus(User mUser , Table mTable){
		if(mTable.isUserInTable(mUser)){
			loginGame(mUser,mTable);
		}else{
			mTable.leaveRoom(mUser.uid);
			UserPool.release(mUser);
		}
	}
	
	private void loginGame(User mUser , Table mTable){
		LoginResult ret = mTable.onUserLogin(mUser);
		if(ret != LoginResult.LOGIN_FAILED_FULL){
			mUser.tid = mTable.tableId;
			userMap.put(mUser.uid, mUser);
			mTable.enterRoom(mUser.uid, mTable);
			//更新桌子人数
		}else{
			mTable.leaveRoom(mUser.uid);
			UserPool.release(mUser);
		}
	}
	
	public void logoutGame(User mUser , Table mTable){
		mTable.onUserExit(mUser);
		
		mTable.leaveRoom(mUser.uid);
		UserPool.release(mUser);
		//更新桌子人数
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
