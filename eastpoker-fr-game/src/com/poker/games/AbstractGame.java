package com.poker.games;

public abstract class AbstractGame {
	
	protected abstract void onReceiveCmd(User mUser, int cmd, byte[] data, int offset, int length);
	
	public void onSendCmd(int cmd, byte[] data, int offset, int length){
		
	};
}
