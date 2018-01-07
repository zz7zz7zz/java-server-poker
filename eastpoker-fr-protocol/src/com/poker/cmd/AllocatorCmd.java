package com.poker.cmd;

import com.poker.base.Server;

public final class AllocatorCmd {
	
	public static final int CMD_REPORT_ROOMINFO = (Server.SERVER_ALLOCATOR <<16) + 1;//0x60001,上报桌子信息
	
	public static final int CMD_GET_ROOMINFO = (Server.SERVER_ALLOCATOR <<16) + 2;   //0x60002,上报桌子信息
	
	public static final int CMD_UPDATE_ROOMINFO = (Server.SERVER_ALLOCATOR <<16) + 3;//0x60002,上报桌子信息
}
