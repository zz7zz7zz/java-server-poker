package com.poker.games;

import com.poker.common.config.Config;

public class Room {
	
	public Table mTables[];
	
	public Room(Config mConfig) {
		mTables = new Table[mConfig.table_count];
		for(int i=0;i<mConfig.table_count;i++){
			mTables[i] = new Table(mConfig.table_max_user);
		}
	}

}
