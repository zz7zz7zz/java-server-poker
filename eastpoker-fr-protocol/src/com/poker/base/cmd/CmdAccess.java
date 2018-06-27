package com.poker.base.cmd;

import com.poker.base.type.TServer;

public final class CmdAccess {
	
	public static final int CMD_LOGIN_GAME 			= (TServer.SERVER_ACCESS <<16) + 1;//0x30001
	public static final int CMD_LOGINOUT_GAME 	    = (TServer.SERVER_ACCESS <<16) + 2;//0x30002
	
	//------------------------------------------------------------------------------------------
	public static String getCmdString(int cmd){
		if(cmd == CMD_LOGIN_GAME){
			return "cmd_login_game";
		}else if(cmd == CMD_LOGINOUT_GAME){
			return "cmd_loginout_game";
		}
		return "unknown_access_cmd 0x"+Integer.toHexString(cmd);
	}
	
}
