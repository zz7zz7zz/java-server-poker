package com.poker.games;

import com.poker.cmd.GameCmd;
import com.poker.common.config.Config;
import com.poker.games.impl.GTable;


public class Room {
	
	public Table mTables[];
	
	public Room(Config mConfig) {
		
		mTables = new Table[mConfig.table_count];
		for(int i=0;i<mConfig.table_count;i++){
			mTables[i] = new GTable(mConfig.table_max_user);
		}
		
		//预先分配1/4桌子数目的用户，每次增长1/4桌子数目的用户
		int user_init_size = (int)(0.25*mConfig.table_count) * mConfig.table_max_user;
		UserPool.init(user_init_size,user_init_size);
	}

	//---------------------------------------------------------------
	public void dispatchRoomMessage(int cmd , byte[] data,int header_start,int header_length,int body_start,int body_length){
		Table mTable = null ;
		if(cmd == GameCmd.CMD_USER_ENTER){
			User mUser = UserPool.get();
			mTable.onUserEnter(mUser);
    	}else if(cmd == GameCmd.CMD_USER_EXIT){
			User mUser = UserPool.get();
			mTable.onUserExit(mUser);
    	}else if(cmd == GameCmd.CMD_USER_READY){
			User mUser = UserPool.get();
			mTable.onUserReady(mUser);
    	}else if(cmd == GameCmd.CMD_USER_OFFLINE){
    		User mUser = UserPool.get();
    		mTable.onUserOffline(mUser);
    	}else if(cmd == GameCmd.CMD_KICK_USER){
    		User mUser = UserPool.get();
    		mTable.onKickUser(mUser, null);;
    	}else{
    		mTable.dispatchTableMessage(cmd, data, header_start, header_length, body_start, body_length);
    	}
	}
}
