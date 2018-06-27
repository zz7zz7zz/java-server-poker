package com.poker.base.cmd;

import com.poker.base.type.TServer;

public final class GameCmd {
	
	//-----------------------------------------------------
	public static int CMD_LOGIN_GAME 				= (TServer.SERVER_GAME <<16) + 0x1001;//0x71001登录游戏
	public static int CMD_CHECK_GAME_STATUS 		= (TServer.SERVER_GAME <<16) + 0x1002;//0x71002检查游戏状态
	
	public static String getCmdString(int cmd){
		if(cmd == CMD_LOGIN_GAME){
			return "cmd_login_game";
		}else if(cmd == CMD_CHECK_GAME_STATUS){
			return "cmd_check_game_status";
		}
		return "unknown_game_cmd 0x"+Integer.toHexString(cmd);
	}
}
