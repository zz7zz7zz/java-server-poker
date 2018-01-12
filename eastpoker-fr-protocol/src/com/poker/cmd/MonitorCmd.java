package com.poker.cmd;

import com.poker.base.ServerIds;

public final class MonitorCmd {
	
	public static final int CMD_MONITOR_REGISTER    = (ServerIds.SERVER_MONITOR <<16) + 1;//0x10002
	public static final int CMD_MONITOR_UNREGISTER  = (ServerIds.SERVER_MONITOR <<16) + 2;//0x10002
	
	//------------------------------------------------------------------------------------------
	public static String getCmdString(int cmd){
		if(cmd == CMD_MONITOR_REGISTER){
			return "cmd_monitor_register";
		}else if(cmd == CMD_MONITOR_UNREGISTER){
			return "cmd_monitor_unregister";
		}
		return "unknown";
	}
}
