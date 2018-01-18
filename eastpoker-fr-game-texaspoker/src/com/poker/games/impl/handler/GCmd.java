package com.poker.games.impl.handler;

public class GCmd {
	
	public int CMD_GAME_START   		= 0x1001; //游戏开始
	public int CMD_DEAL   				= 0x1002; //发牌，意味着牌局开始
	public int CMD_ACTION 				= 0x1003; //操作
	public int CMD_BROADCAST_ACTION 	= 0x1004; //广播操作
	public int CMD_SHOW_HAND 			= 0x1005; //秀牌
	public int CMD_BROADCAST_SHOW_HAND 	= 0x1006; //广播秀牌
	public int CMD_ALL_SHOW_HAND 		= 0x1007; //集体秀牌
	public int CMD_GAME_END 	    	= 0x1008; //游戏结束，进行结算
}
