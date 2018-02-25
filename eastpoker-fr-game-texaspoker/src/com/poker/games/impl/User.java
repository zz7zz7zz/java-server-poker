package com.poker.games.impl;

import java.util.Arrays;

import com.poker.games.AbsUser;
import com.poker.games.impl.define.TexasDefine.UserStatus;
import com.poker.games.impl.define.TexasDefine.Operate;
import com.poker.games.impl.define.TexasDefine.Result;

public class User extends AbsUser {
	
	public byte[] handCard=new byte[2];
	public UserStatus play_status;
	public Operate operate;
	
	public boolean isFold;
	public boolean isAllIn;
	public Result result = new Result();
	
	public long round_chip = 0;//每一轮下注金币
	public long win_pot_chip=0;//赢下Pot中的金币
	public long win_chip=0;//最终输赢的金币
	public long originalChip;//最原始的金币
	
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
		
		originalChip = chip;
	}
	
	public void stopGame(){
		super.stopGame();
		
		Arrays.fill(handCard, (byte)0);
		play_status = UserStatus.NOT_PLAY_SITDOWN;
		operate = Operate.UNRECOGNIZED;
		
		isFold = false;
		isAllIn = false;
		result.clear();
		
		round_chip = 0;
		win_pot_chip = 0;
		win_chip = 0;
		originalChip = 0;
	}
}
