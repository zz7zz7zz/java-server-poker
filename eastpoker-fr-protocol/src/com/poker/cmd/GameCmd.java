package com.poker.cmd;

import com.poker.base.ServerIds;

public final class GameCmd {
	
	//-----------------------------------------------------
	public static int CMD_CHECK_GAME_STATUS 		= (ServerIds.SERVER_GAME <<16) + 0x1100;//0x71001登录游戏
	public static int CMD_LOGIN_GAME 				= (ServerIds.SERVER_GAME <<16) + 0x1001;//0x71001登录游戏
}
