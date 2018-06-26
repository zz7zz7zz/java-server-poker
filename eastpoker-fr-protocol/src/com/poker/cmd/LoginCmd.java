package com.poker.cmd;

import com.poker.base.ServerIds;

public final class LoginCmd {
	
	public static final int CMD_LOGIN_REQUEST  = (ServerIds.SERVER_LOGIN <<16) + 1;//0x40001
	public static final int CMD_LOGIN_RESPONSE = (ServerIds.SERVER_LOGIN <<16) + 2;//0x40001
	
	//------------------------------------------------------------------------------------------
	public static String getCmdString(int cmd){
		if(cmd == CMD_LOGIN_REQUEST){
			return "cmd_login_request";
		}else if(cmd == CMD_LOGIN_RESPONSE){
			return "cmd_login_response";
		}
		return "unknown_login_cmd";
	}
}
