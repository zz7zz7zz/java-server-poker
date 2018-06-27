package com.poker.base.cmd;

import com.poker.base.type.TServer;

public class UserCmd {

	public static int CMD_LOGIN_GAME 				= (TServer.SERVER_USER <<16) + 0x1001;//0x50001登录游戏
	public static int CMD_CHECK_GAME_STATUS 		= (TServer.SERVER_USER <<16) + 0x1002;//0x50002用户登录账号后后，检查游戏状态
	
	public static int CMD_ENTER_ROOM 				= (TServer.SERVER_USER <<16) + 0x1003;//0x50003进入房间
	public static int CMD_LEAVE_ROOM 				= (TServer.SERVER_USER <<16) + 0x1004;//0x50004退出房间

	public static String getCmdString(int cmd){
		if(cmd == CMD_LOGIN_GAME){
			return "cmd_login_game";
		}else if(cmd == CMD_CHECK_GAME_STATUS){
			return "cmd_check_game_status";
		}else if(cmd == CMD_ENTER_ROOM){
			return "cmd_enter_room";
		}else if(cmd == CMD_LEAVE_ROOM){
			return "cmd_leave_room";
		}
		return "unknown_user_cmd 0x"+Integer.toHexString(cmd);
	}
}
