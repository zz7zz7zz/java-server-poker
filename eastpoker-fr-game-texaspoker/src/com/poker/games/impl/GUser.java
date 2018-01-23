package com.poker.games.impl;

import java.util.Arrays;

import com.poker.games.User;

public class GUser extends User {
	
	enum GStatus{
		GStatus_PLAY(1),
		GStatus_UNPLAY_SITDOWN(2),
		GStatus_UNPLAY_ONLOOKERS(3),
		GStatus_UNPLAY_WAIT(4);
		
		int code;
        private GStatus(int code) {
            this.code = code;
        }
	}
	
	public byte[] handCard=new byte[2];
	public GStatus play_status;
	public int action_type;
	
	public void reset(){
		super.reset();
		clear();
	}
	
	public void clear(){
		play_status = GStatus.GStatus_UNPLAY_SITDOWN;
		Arrays.fill(handCard, (byte)0);
	}
}
