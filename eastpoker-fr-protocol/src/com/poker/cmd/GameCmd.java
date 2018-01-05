package com.poker.cmd;

public final class GameCmd {
	
	//-----------------------------------------------------
	public int CMD_USER_ENTER 				= 0x1;//用户进入
	public int CMD_BROADCAST_USER_ENTER  	= 0x2;
	
	public int CMD_USER_EXIT             	= 0x3;//用户退出
	public int CMD_BROADCAST_USER_EXIT   	= 0x4;
	
	public int CMD_USER_READY 				= 0x5;//用户准备
	public int CMD_BROADCAST_USER_READY  	= 0x6;
	
	public int CMD_USER_OFFLINE     		= 0x7;//用户掉线
	public int CMD_BROADCAST_USER_OFFLINE 	= 0x8;
	
	//-----------------------------------------------------
	public int CMD_KICK_USER 	    		= 0x9;//用户踢人
	public int CMD_BROADCAST_KICK_USER		= 0xA;
	
	public int CMD_ONLOOKERS     		    = 0xB;//用户申请围观
	public int CMD_BROADCAST_ONLOOKERS 	    = 0xC;
	
	//-----------------------------------------------------
	
	
}
