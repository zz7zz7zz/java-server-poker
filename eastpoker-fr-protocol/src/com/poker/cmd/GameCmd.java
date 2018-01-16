package com.poker.cmd;

public final class GameCmd {
	
	//-----------------------------------------------------
	public static int CMD_USER_ENTER 				= 0x1;//用户进入
	public static int CMD_BROADCAST_USER_ENTER  	= 0x2;
	
	public static int CMD_USER_EXIT             	= 0x3;//用户退出
	public static int CMD_BROADCAST_USER_EXIT   	= 0x4;
	
	public static int CMD_USER_READY 				= 0x5;//用户准备
	public static int CMD_BROADCAST_USER_READY  	= 0x6;
	
	public static int CMD_USER_OFFLINE     		= 0x7;//用户掉线
	public static int CMD_BROADCAST_USER_OFFLINE 	= 0x8;
	
	//-----------------------------------------------------
	public static int CMD_KICK_USER 	    		= 0x9;//用户踢人
	public static int CMD_BROADCAST_KICK_USER		= 0xA;
	
	public static int CMD_ONLOOKERS     		    = 0xB;//用户申请围观
	public static int CMD_BROADCAST_ONLOOKERS 	    = 0xC;
	
	//-----------------------------------------------------
	
	
}
