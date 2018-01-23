package com.poker.cmd;

public class SystemCmd {
	
	public static final int CMD_HEART_BEATE 	= 0x0001;
	public static final int CMD_UPDATE_CONFIG 	= 0x0002;
	
	public static final int CMD_ERR 			= 0x000A;
	public static final int CMD_USER_OFFLINE    = 0x000B;
	
	//错误类型
	public static final int ERR_CODE_LOGIN_FAILED_TABLE_FULL = 100;//登录游戏错误
}
