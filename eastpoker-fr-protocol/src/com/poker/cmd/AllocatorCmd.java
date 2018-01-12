package com.poker.cmd;

import com.poker.base.ServerIds;

public final class AllocatorCmd {
	
	public static final int CMD_GAMESERVER_TO_ALLOCATOR_REPORT_ROOMINFO = (ServerIds.SERVER_ALLOCATOR <<16) + 1;//0x60001,上报桌子信息
	public static final int CMD_GAMESERVER_TO_ALLOCATOR_UPDATE_ROOMINFO = (ServerIds.SERVER_ALLOCATOR <<16) + 2;//0x60002,上报桌子信息
	
	public static final int CMD_ALLOCATOR_BROADCAST_GET_ROOMINFO = (ServerIds.SERVER_ALLOCATOR <<16) + 3;   //0x60002,上报桌子信息
	
	
	
	//------------------------------------------------------------------------------------------
	public static String getCmdString(int cmd){
		if(cmd == CMD_GAMESERVER_TO_ALLOCATOR_REPORT_ROOMINFO){
			return "cmd_gameserver_to_allocator_report_roominfo";
		}else if(cmd == CMD_GAMESERVER_TO_ALLOCATOR_UPDATE_ROOMINFO){
			return "cmd_gameserver_to_allocator_update_roominfo";
		}else if(cmd == CMD_ALLOCATOR_BROADCAST_GET_ROOMINFO){
			return "cmd_allocator_broadcast_get_roominfo";
		}
		return "unknown";
	}
	
}
