package com.poker.games.impl.handler;

public class GCmd {
	
	public int CMD_DEAL   			= 1001; //发牌，意味着牌局开始
	
	public int CMD_ACTION 			= 1002; //操作
	
	public int CMD_BROADCAST_ACTION = 1003; //广播操作
	
	public int CMD_SHOW_HAND 		= 1004; //秀牌
	
	public int CMD_BROADCAST_SHOW_HAND = 1005; //广播秀牌
	
	public int CMD_ALL_SHOW_HAND 	= 1006; //集体秀牌
	
	public int CMD_SETTLEMENT 	    = 1007; //结算，意味着牌局结束
}
