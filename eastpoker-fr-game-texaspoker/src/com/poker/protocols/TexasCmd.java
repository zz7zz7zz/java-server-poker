package com.poker.protocols;

public final class TexasCmd {
	
	//客户端发起   （游戏业务命令字）,范围是0x3001~0x3FFF
	public static final int CMD_CLIENT_ACTION						= 0x3001; //谁做了什么操作
	public static final int CMD_CLIENT_SHOW_HAND 					= 0x3002; //谁秀牌
	public static final int CMD_CLIENT_RECONNECT 					= 0x3003; //客户端数据不对，请求重连
	
	//客户端返回   （游戏业务命令字）,范围是0x4001~0x4FFF
	public static final int CMD_SERVER_GAME_START   				= 0x4001; //游戏开始
	
	public static final int CMD_SERVER_DEAL_PREFLOP   				= 0x4002; //发底牌
	public static final int CMD_SERVER_DEAL_FLOP  					= 0x4003; //发翻牌
	public static final int CMD_SERVER_DEAL_TURN   					= 0x4004; //发转牌
	public static final int CMD_SERVER_DEAL_RIVER   				= 0x4005; //发河牌

	public static final int CMD_SERVER_BROADCAST_USER_ACTION 		= 0x4006; //广播 谁做了什么操作
	public static final int CMD_SERVER_BROADCAST_NEXT_OPERATE 		= 0x4007; //下一个操作者是谁
	public static final int CMD_SERVER_BROADCAST_POTS 		        = 0x4008; //广播一轮结束后，有几个Pot
	public static final int CMD_SERVER_BROADCAST_SHOW_HAND 			= 0x4009; //广播集体秀牌
	
	public static final int CMD_SERVER_RECONNECT 	    			= 0x400A; //客户端请求重连
	public static final int CMD_SERVER_GAME_OVER 	    			= 0x4FFF; //游戏结束，进行结算
	
	public static String getCmdString(int cmd){
		if(cmd == CMD_CLIENT_ACTION){
			return "cmd_client_action";
		}else if(cmd == CMD_CLIENT_SHOW_HAND){
			return "cmd_client_show_hand";
		}else if(cmd == CMD_CLIENT_RECONNECT){
			return "cmd_client_reconnect";
		}else if(cmd == CMD_SERVER_GAME_START){
			return "cmd_server_game_start";
		}else if(cmd == CMD_SERVER_DEAL_PREFLOP){
			return "cmd_server_deal_preflop";
		}else if(cmd == CMD_SERVER_DEAL_FLOP){
			return "cmd_server_deal_flop";
		}else if(cmd == CMD_SERVER_DEAL_TURN){
			return "cmd_server_deal_turn";
		}else if(cmd == CMD_SERVER_DEAL_RIVER){
			return "cmd_server_deal_river";
		}else if(cmd == CMD_SERVER_BROADCAST_USER_ACTION){
			return "cmd_server_broadcast_user_action";
		}else if(cmd == CMD_SERVER_BROADCAST_NEXT_OPERATE){
			return "cmd_server_broadcast_next_operate";
		}else if(cmd == CMD_SERVER_BROADCAST_POTS){
			return "cmd_server_broadcast_pots";
		}else if(cmd == CMD_SERVER_BROADCAST_SHOW_HAND){
			return "cmd_server_broadcast_show_hand";
		}else if(cmd == CMD_SERVER_RECONNECT){
			return "cmd_server_reconnect";
		}else if(cmd == CMD_SERVER_GAME_OVER){
			return "cmd_server_game_over";
		}
		return "";
	}
}
