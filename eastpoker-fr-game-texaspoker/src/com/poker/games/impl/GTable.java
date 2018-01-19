package com.poker.games.impl;

import com.poker.games.Table;
import com.poker.games.User;
import com.poker.games.impl.handler.GCmd;

public class GTable extends Table {

	public GTable(int table_max_user) {
		super(table_max_user);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onUserEnter(User mUser) {
		super.onUserEnter(mUser);
	}

	@Override
	public void onUserExit(User mUser) {
		super.onUserExit(mUser);
	}

	@Override
	public void onUserReady(User mUser) {
		super.onUserReady(mUser);
	}

	@Override
	public void onUserOffline(User mUser) {
		super.onUserOffline(mUser);
	}

	@Override
	public void onKickUser(User mUser, User kickedUser) {
		super.onKickUser(mUser, kickedUser);
	}

	@Override
	protected void dispatchTableMessage(int cmd, byte[] data, int header_start, int header_length, int body_start,int body_length) {
		if(cmd == GCmd.CMD_SERVER_BROADCAST_SHOW_HAND){
			
		}
	}

}
