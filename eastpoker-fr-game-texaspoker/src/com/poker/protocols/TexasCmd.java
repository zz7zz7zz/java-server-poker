package com.poker.protocols;

public final class TexasCmd {
	
	//客户端发起命令字
	public static final int CMD_CLIENT_WHO_ACTION_WHAT				= 0x1006; //谁做了什么操作
	public static final int CMD_CLIENT_SHOW_HAND 					= 0x1007; //谁秀牌
	public static final int CMD_CLIENT_RECONNECT 					= 0x1009; //客户端数据不对，请求重连
	
	//客户端返回命令字
	public static final int CMD_SERVER_GAME_START   				= 0x2001; //游戏开始，下发相关配置
	
	public static final int CMD_SERVER_DEAL_PREFLOP   				= 0x2002; //发底牌
	public static final int CMD_SERVER_DEAL_FLOP  					= 0x2003; //发翻牌
	public static final int CMD_SERVER_DEAL_TURN   					= 0x2004; //发转牌
	public static final int CMD_SERVER_DEAL_RIVER   				= 0x2005; //发河牌

	public static final int CMD_SERVER_BROADCAST_WHO_ACTION_WAHT 	= 0x2006; //广播 谁做了什么操作,下一个操作者是谁
	public static final int CMD_SERVER_BROADCAST_SHOW_HAND 			= 0x2007; //广播 秀牌

	public static final int CMD_SERVER_RECONNECT 	    			= 0x2008; //客户端请求重连
	public static final int CMD_SERVER_GAME_END 	    			= 0x2009; //游戏结束，进行结算
}
