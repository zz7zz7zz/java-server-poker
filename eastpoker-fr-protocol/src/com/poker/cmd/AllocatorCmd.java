package com.poker.cmd;

import com.poker.base.ServerIds;

public final class AllocatorCmd {
	
	public static final int CMD_REPORT_ROOMINFO = (ServerIds.SERVER_ALLOCATOR <<16) + 1;//0x60001,上报桌子信息
	
	public static final int CMD_GET_ROOMINFO = (ServerIds.SERVER_ALLOCATOR <<16) + 2;   //0x60002,上报桌子信息
	
	public static final int CMD_UPDATE_ROOMINFO = (ServerIds.SERVER_ALLOCATOR <<16) + 3;//0x60002,上报桌子信息
	
	//------------------------------------------------------------------------------------------
	public static String getCmdString(int cmd){
		if(cmd == CMD_REPORT_ROOMINFO){
			return "CMD_REPORT_ROOMINFO";
		}else if(cmd == CMD_GET_ROOMINFO){
			return "CMD_GET_ROOMINFO";
		}else if(cmd == CMD_UPDATE_ROOMINFO){
			return "CMD_UPDATE_ROOMINFO";
		}
		return "UNKNOWN";
	}
	
}
