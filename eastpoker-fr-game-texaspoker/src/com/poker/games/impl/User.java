package com.poker.games.impl;

import java.util.Arrays;

import com.poker.games.AbsUser;
import com.poker.games.impl.define.TexasDefine.UserStatus;
import com.poker.games.impl.define.TexasDefine.Result;
import com.poker.protocols.texaspoker.TexasGameBroadcastUserActionProto.TexasGameBroadcastUserAction.Operate;

public class User extends AbsUser {
	
	public byte[] handCard=new byte[2];
	public UserStatus play_status;
	public Operate operate;
	
	public long round_chip = 0;
	public boolean isFold;
	public boolean isAllIn;
	public Result result = new Result();
	public long win_chip=0;
	
	public void reset(){
		super.reset();
		stopGame();
	}
	
	public boolean isPlaying(){
		return play_status == UserStatus.PLAY;
	}
	
	public void startGame(){
		super.startGame();
		play_status = UserStatus.PLAY;
	}
	
	public void stopGame(){
		super.stopGame();
		operate = Operate.FOLD;
		play_status = UserStatus.NOT_PLAY_SITDOWN;
		Arrays.fill(handCard, (byte)0);
		
		isFold = false;
		isAllIn = false;
		result.clear();
		win_chip = 0;
	}
}
