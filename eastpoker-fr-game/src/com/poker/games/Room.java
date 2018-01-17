package com.poker.games;

import com.poker.common.config.Config;

public class Room {
	
	public Table mTables[];
	
	public Room(Config mConfig) {
		
		mTables = new Table[mConfig.table_count];
		for(int i=0;i<mConfig.table_count;i++){
			mTables[i] = new Table(mConfig.table_max_user);
		}
		
		//预先分配1/4桌子数目的用户，每次增长1/4桌子数目的用户
		int user_init_size = (int)(0.25*mConfig.table_count) * mConfig.table_max_user;
		UserPool.init(user_init_size,user_init_size);
	}

}
