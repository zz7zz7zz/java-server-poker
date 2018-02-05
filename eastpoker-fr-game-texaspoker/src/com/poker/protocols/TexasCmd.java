package com.poker.protocols;

public final class TexasCmd {
	
	//客户端发起命令字,范围是0x2001~0x2FFF
	public static final int CMD_CLIENT_ACTION						= 0x2001; //谁做了什么操作
	public static final int CMD_CLIENT_SHOW_HAND 					= 0x2002; //谁秀牌
	public static final int CMD_CLIENT_RECONNECT 					= 0x2003; //客户端数据不对，请求重连
	
	//客户端返回命令字,范围是0x3001~0x3FFF
	public static final int CMD_SERVER_GAME_START   				= 0x3001; //游戏开始
	
	public static final int CMD_SERVER_DEAL_PREFLOP   				= 0x3002; //发底牌
	public static final int CMD_SERVER_DEAL_FLOP  					= 0x3003; //发翻牌
	public static final int CMD_SERVER_DEAL_TURN   					= 0x3004; //发转牌
	public static final int CMD_SERVER_DEAL_RIVER   				= 0x3005; //发河牌

	public static final int CMD_SERVER_BROADCAST_USER_ACTION 		= 0x3006; //广播 谁做了什么操作
	public static final int CMD_SERVER_BROADCAST_NEXT_OPERATE 		= 0x3007; //下一个操作者是谁
	public static final int CMD_SERVER_BROADCAST_POTS 		        = 0x3008; //广播一局结束后，有几个Pot
	public static final int CMD_SERVER_BROADCAST_SHOW_HAND 			= 0x3009; //广播集体秀牌
	
	public static final int CMD_SERVER_RECONNECT 	    			= 0x300A; //客户端请求重连
	public static final int CMD_SERVER_GAME_OVER 	    			= 0x3FFF; //游戏结束，进行结算
}
