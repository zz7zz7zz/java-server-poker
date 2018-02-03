package com.poker.games.impl;

import java.util.ArrayList;

public class TexasDefine {
	
	public static final byte[] POKER_ARRAY={
		
			0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,   //黑桃 2 - A spades
		
			0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x1a,0x1b,0x1c,0x1d,0x1e,	//红花 2 - A hearts
			
		    0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29,0x2a,0x2b,0x2c,0x2d,0x2e,	//梅桃 2 - A clubs 
		    
		    0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x3a,0x3b,0x3c,0x3d,0x3e,	//方块 2 - A diamonds
		    
	};
	
	public static enum GStep{
		START(1),
		PREFLOP(2),
		FLOP(3),
		TRUN(4),
		RIVER(5),
		SHOWHAND(6),
		STOP(7);
		
		int code;
        private GStep(int code) {
            this.code = code;
        }
	}
	
	
	public static enum GStatus{
		PLAY(1),
		NOT_PLAY_SITDOWN(2),
		NOT_PLAY_ONLOOKERS(3),
		NOT_PLAY_WAIT(4);
		
		int code;
        private GStatus(int code) {
            this.code = code;
        }
	}
	
	public static class Pot{
		String name;//Pot名字
		long pot_chips;//Pot下注额
		ArrayList<Byte> seatIds = new ArrayList<>();
		
//		byte seatIds;//哪些座位上的人参与分成，按位运算
	}
}
