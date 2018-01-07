package com.poker.games;

public interface IGame {
	
	void onReceiveCmd(int cmd, byte[] data, int offset, int length);
	
	void onSendCmd(int cmd, byte[] data, int offset, int length);
}
