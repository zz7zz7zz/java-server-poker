package com.poker.games.impl;

import java.util.Arrays;

import com.poker.games.User;
import com.poker.games.impl.GData.GStatus;
import com.poker.protocols.texaspoker.TexasGameActionProto.TexasGameAction.Operate;

public class GUser extends User {
	
	public byte[] handCard=new byte[2];
	public GStatus play_status;
	public Operate action_type;
	
	public int round_chip = 0;
	
	public void reset(){
		super.reset();
		clear();
	}
	
	public void clear(){
		action_type = Operate.FOLD;
		play_status = GStatus.GStatus_UNPLAY_SITDOWN;
		Arrays.fill(handCard, (byte)0);
	}
}
