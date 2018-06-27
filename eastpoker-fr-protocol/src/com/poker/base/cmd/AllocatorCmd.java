package com.poker.base.cmd;

import com.poker.base.type.TServer;

public final class AllocatorCmd {
	
	public static final int CMD_GAMESERVER_TO_ALLOCATOR_REPORT_ROOMINFO = (TServer.SERVER_ALLOCATOR <<16) + 1;//0x60001,上报桌子信息
	public static final int CMD_GAMESERVER_TO_ALLOCATOR_UPDATE_ROOMINFO = (TServer.SERVER_ALLOCATOR <<16) + 2;//0x60002,上报桌子信息
	
	public static final int CMD_ALLOCATOR_BROADCAST_GET_ROOMINFO = (TServer.SERVER_ALLOCATOR <<16) + 3;   //0x60002,上报桌子信息
	
	public static final int CMD_LOGIN_GAME = (TServer.SERVER_ALLOCATOR <<16) + 0x1001;   //0x61001登录游戏
	
	//------------------------------------------------------------------------------------------
	public static String getCmdString(int cmd){
		if(cmd == CMD_GAMESERVER_TO_ALLOCATOR_REPORT_ROOMINFO){
			return "cmd_gameserver_to_allocator_report_roominfo";
		}else if(cmd == CMD_GAMESERVER_TO_ALLOCATOR_UPDATE_ROOMINFO){
			return "cmd_gameserver_to_allocator_update_roominfo";
		}else if(cmd == CMD_ALLOCATOR_BROADCAST_GET_ROOMINFO){
			return "cmd_allocator_broadcast_get_roominfo";
		}else if(cmd == CMD_LOGIN_GAME){
			return "cmd_login_game";
		}
		return "unknown_allocator_cmd 0x"+Integer.toHexString(cmd);
	}
	
}
