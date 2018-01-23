package com.poker.games;

import java.util.HashMap;

import com.open.util.log.Logger;
import com.poker.cmd.GameCmd;
import com.poker.common.config.Config;
import com.poker.games.impl.GTable;


public class Room {
	
	private final String TAG = "Room";
	
	public HashMap<Integer,User> userMap = new HashMap<>();
	public Table mTables[];
	
	public Room(Config mConfig) {
		
		mTables = new Table[mConfig.table_count];
		for(int i=0;i<mConfig.table_count;i++){
			int tableId = (mConfig.server_id << 16) + i;
			mTables[i] = new GTable(tableId,mConfig.table_max_user);
		}
		
		//预先分配1/4桌子数目的用户，每次增长1/4桌子数目的用户
		int user_init_size = (int)(0.25*mConfig.table_count) * mConfig.table_max_user;
		UserPool.init(user_init_size,user_init_size);
	}

	//---------------------------------------------------------------
	public void dispatchRoomMessage(int cmd , byte[] data,int header_start,int header_length,int body_start,int body_length){
		int uid = 0;
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
				//更新桌子人数
			}else{
				UserPool.release(mUser);
			}
    	}else{
    		
    		if(null == mUser){
    			Logger.v(TAG+ "cmd be unable to handle uid " + uid + " cmd "+cmd);
				return;
			}
    		
    		if(cmd == GameCmd.CMD_USER_EXIT){
    			int ret = mTable.onUserExit(mUser);
    			if(ret == 1){
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
	
	public static void main(String arg[]){
		int sid = 1;
		int tid = (sid<<16) + 5;
		System.out.println(tid);
		
		int index = tid & 0xff;
		int sidout= (tid>>16);
		System.out.println(sidout);
		System.out.println(index);
	}
}
