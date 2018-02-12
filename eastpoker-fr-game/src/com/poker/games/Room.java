package com.poker.games;

import java.util.HashMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.util.log.Logger;
import com.poker.cmd.BaseGameCmd;
import com.poker.cmd.GameCmd;
import com.poker.common.config.Config;
import com.poker.games.define.UserPool;
import com.poker.games.define.GameDefine.LoginResult;
import com.poker.games.impl.Table;
import com.poker.games.impl.User;

import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;


public class Room {
	
	private final String TAG = "Room";
	
	public HashMap<Long,User> userMap = new HashMap<>();
	public Table mTables[];
	public short gameId;
	public short gameSid;
	
	public Room(Config mConfig) {
		gameId = mConfig.game_id;
		gameSid= mConfig.server_id;
		
		mTables = new Table[mConfig.table_count];
		for(int i=0;i<mConfig.table_count;i++){
			int tableId = (mConfig.server_id << 16) + i;
			mTables[i] = new Table(this,tableId,mConfig);
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
			
			AbsTable.mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());	
			int accessId = AbsTable.mInPacket.readInt();
			int tableId  = AbsTable.mInPacket.readInt();
			
			int tableIdIndex = tableId & 0xff;
			if(tableIdIndex <0 || tableIdIndex >= mTables.length){
				Logger.v(TAG+ "error uid  tid.diff tid " + tableId + " index "+ tableIdIndex);
				return;
			}
			
			Table mRequestTable = mTables[tableIdIndex];
			if(null == mTable){
				mTable = mRequestTable;
			}else{
				Logger.v(TAG+ "error uid  req.tid " + tableId + " req.tid.index "+ tableIdIndex+ " realtid "+mUser.tid + " realIndex " + (mUser.tid & 0xff));
			}
			
			
    		if(null == mUser){
    			mUser = UserPool.get(uid);
    		}
    		mUser.accessId = accessId;
    		mUser.setOnLine(true);
    		loginGame(mUser,mTable);
    		
    	}else{

    		if(null == mUser){
    			Logger.v(TAG+ " cmd can not be able to handle user null " + uid + " cmd 0x"+Integer.toHexString(cmd));
				return;
			}
    		
    		if(null == mTable){
    			Logger.v(TAG+ " cmd can not be able to handle mTable null ");
				return;
			}
    		
    		if(cmd == GameCmd.CMD_CHECK_GAME_STATUS){
    			
    			AbsTable.mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());	
    			int accessId = AbsTable.mInPacket.readInt();
    			mUser.accessId =  accessId;

    			if(mTable.isUserInTable(mUser)){
            		mTable.updateOnLineStatus(mUser,true);
    				loginGame(mUser,mTable);
    			}else{
    				logoutGame(mUser,mTable);
    			}
    			
        	}else if(cmd == BaseGameCmd.CMD_CLIENT_USER_EXIT){
        		
        		logoutGame(mUser,mTable);
        		
        	}else if(cmd == BaseGameCmd.CMD_CLIENT_USER_READY){
        		
        		mUser.accessId = mDispatchPacket.getDispatchChainList(0).getSrcServerId();
        		mTable.updateOnLineStatus(mUser,true);
        		
    			mTable.onUserReady(mUser);
    			
        	}else if(cmd == BaseGameCmd.CMD_CLIENT_OFFLINE){
        		
        		mTable.updateOnLineStatus(mUser,false);
        		
        	}else if(cmd == BaseGameCmd.CMD_CLIENT_KICK_USER){
        		
        		long kickedUid = 0;
        		User kickedUser = userMap.get(kickedUid);
        		int ret = mTable.onKickUser(mUser, kickedUser);
        		if(ret > 0){
        			logoutGame(kickedUser,mTable);
        		}
        		
        	}else{
        		
        		mUser.accessId = mDispatchPacket.getDispatchChainList(0).getSrcServerId();
        		mTable.updateOnLineStatus(mUser,true);
        		
        		mTable.dispatchTableMessage(mUser,cmd, data, header_start, header_length, body_start, body_length);
        	}
    	}
	}
	
	private void loginGame(User mUser , Table mTable){
		LoginResult ret = mTable.onUserLogin(mUser);
		if(ret != LoginResult.LOGIN_FAILED_FULL){
			mUser.tid = mTable.tableId;
			userMap.put(mUser.uid, mUser);
			
			//向Access更新用户游戏信息
			mTable.enterRoom2Access(mUser, mTable);
			
			//向Alloc更新桌子人数
			mTable.enterRoom2Alloc(mUser, mTable);
			
			//向User更新用户状态
			mTable.enterRoom2User(mUser, mTable);
			
		}else{
			//logoutGame(mUser,mTable);
			
			UserPool.release(mUser);
		}
	}
	
	public void logoutGame(User mUser , AbsTable mTable){
		mTable.onUserExit(mUser);
		
		//向Access更新用户游戏信息
		mTable.leaveRoom2Access(mUser);
		//向Alloc更新桌子人数
		mTable.leaveRoom2Alloc(mUser);
		//向User更新用户状态
		mTable.leaveRoom2User(mUser);
		
		UserPool.release(mUser);
	}
	
//	public static void main(String arg[]){
//		int sid = 0;
//		int index = 0;
//		int tid = (sid<<16) + index;
//		System.out.println("A " + tid);
//		
//		int outindex = tid & 0xff;
//		int outsid= (tid>>16);
//		System.out.println("B " + outsid);
//		System.out.println("B " + outindex);
//	}
}
