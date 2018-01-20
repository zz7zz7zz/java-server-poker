package com.poker.cmd;

import com.poker.base.ServerIds;

public final class GameCmd {
	
	//-----------------------------------------------------
	
	public static int CMD_LOGIN_GAME 				= (ServerIds.SERVER_GAME <<16) + 0x1;//0x70001登录游戏
	
	public static int CMD_USER_ENTER 				= (ServerIds.SERVER_GAME <<16) + 0x2;//0x70001用户进入
	public static int CMD_BROADCAST_USER_ENTER  	= (ServerIds.SERVER_GAME <<16) + 0x3;//0x70002
	
	public static int CMD_USER_EXIT             	= (ServerIds.SERVER_GAME <<16) + 0x4;//0x70003用户退出
	public static int CMD_BROADCAST_USER_EXIT   	= (ServerIds.SERVER_GAME <<16) + 0x5;//0x70004
	
	public static int CMD_USER_READY 				= (ServerIds.SERVER_GAME <<16) + 0x6;//0x70005用户准备
	public static int CMD_BROADCAST_USER_READY  	= (ServerIds.SERVER_GAME <<16) + 0x7;//0x70006
	
	public static int CMD_USER_OFFLINE     			= (ServerIds.SERVER_GAME <<16) + 0x8;//0x70007用户掉线
	public static int CMD_BROADCAST_USER_OFFLINE 	= (ServerIds.SERVER_GAME <<16) + 0x9;//0x70008
	
	//-----------------------------------------------------
	public static int CMD_KICK_USER 	    		= (ServerIds.SERVER_GAME <<16) + 0xA;//0x70009用户踢人
	public static int CMD_BROADCAST_KICK_USER		= (ServerIds.SERVER_GAME <<16) + 0xB;//0x7000A
	
	public static int CMD_ONLOOKERS     		    = (ServerIds.SERVER_GAME <<16) + 0xC;//0x7000B用户申请围观
	public static int CMD_BROADCAST_ONLOOKERS 	    = (ServerIds.SERVER_GAME <<16) + 0xD;//0x7000C
	
	//-----------------------------------------------------
	
	
}
