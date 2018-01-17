package com.poker.games.impl;

import com.poker.games.AbstractGame;
import com.poker.games.User;

/**
 * 具体游戏的实现类
 * @author Administrator
 * 2018年1月15日
 */
public class GImpl extends AbstractGame {

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
		// TODO Auto-generated method stub
		
	}
	
}
