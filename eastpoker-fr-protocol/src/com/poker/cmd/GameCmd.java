package com.poker.cmd;

import com.poker.base.ServerIds;

public final class GameCmd {
	
	//-----------------------------------------------------
	
	public static int CMD_LOGIN_GAME 				= (ServerIds.SERVER_GAME <<16) + 0x1001;//0x71001登录游戏
	
	public static int CMD_USER_ENTER 				= (ServerIds.SERVER_GAME <<16) + 0x1002;//0x71001用户进入
	public static int CMD_BROADCAST_USER_ENTER  	= (ServerIds.SERVER_GAME <<16) + 0x1003;//0x71002
	
	public static int CMD_USER_EXIT             	= (ServerIds.SERVER_GAME <<16) + 0x1004;//0x71003用户退出
	public static int CMD_BROADCAST_USER_EXIT   	= (ServerIds.SERVER_GAME <<16) + 0x1005;//0x71004
	
	public static int CMD_USER_READY 				= (ServerIds.SERVER_GAME <<16) + 0x1006;//0x71005用户准备
	public static int CMD_BROADCAST_USER_READY  	= (ServerIds.SERVER_GAME <<16) + 0x1007;//0x71006
	
	public static int CMD_USER_OFFLINE     			= (ServerIds.SERVER_GAME <<16) + 0x1008;//0x71007用户掉线
	public static int CMD_BROADCAST_USER_OFFLINE 	= (ServerIds.SERVER_GAME <<16) + 0x1009;//0x71008
	
	//-----------------------------------------------------
	public static int CMD_KICK_USER 	    		= (ServerIds.SERVER_GAME <<16) + 0x100A;//0x71009用户踢人
	public static int CMD_BROADCAST_KICK_USER		= (ServerIds.SERVER_GAME <<16) + 0x100B;//0x7100A
	
	public static int CMD_ONLOOKERS     		    = (ServerIds.SERVER_GAME <<16) + 0x100C;//0x7100B用户申请围观
	public static int CMD_BROADCAST_ONLOOKERS 	    = (ServerIds.SERVER_GAME <<16) + 0x100D;//0x7100C
	
	//-----------------------------------------------------
	
	
}
