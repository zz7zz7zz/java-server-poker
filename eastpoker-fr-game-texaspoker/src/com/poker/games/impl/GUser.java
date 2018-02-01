package com.poker.games.impl;

import java.util.Arrays;

import com.poker.games.User;
import com.poker.games.impl.GDefine.GStatus;
import com.poker.protocols.texaspoker.TexasGameBroadcastActionProto.TexasGameBroadcastAction.Operate;

public class GUser extends User {
	
	public byte[] handCard=new byte[2];
	public GStatus play_status;
	public Operate action_type;
	
	public long round_chip = 0;
	
	public void reset(){
		super.reset();
		stopGame();
	}
	
	public void startGame(){
		super.startGame();
		play_status = GStatus.PLAY;
	}
	
	public void stopGame(){
		super.stopGame();
		action_type = Operate.FOLD;
		play_status = GStatus.NOT_PLAY_SITDOWN;
		Arrays.fill(handCard, (byte)0);
	}
}
