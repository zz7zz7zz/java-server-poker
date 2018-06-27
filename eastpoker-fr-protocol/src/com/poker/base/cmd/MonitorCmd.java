package com.poker.base.cmd;

import com.poker.base.type.TServer;

public final class MonitorCmd {
	
	public static final int CMD_MONITOR_REGISTER    = (TServer.SERVER_MONITOR <<16) + 1;//0x10002
	public static final int CMD_MONITOR_UNREGISTER  = (TServer.SERVER_MONITOR <<16) + 2;//0x10002
	
	//------------------------------------------------------------------------------------------
	public static String getCmdString(int cmd){
		if(cmd == CMD_MONITOR_REGISTER){
			return "cmd_monitor_register";
		}else if(cmd == CMD_MONITOR_UNREGISTER){
			return "cmd_monitor_unregister";
		}
		return "unknown_monitor_cmd 0x"+Integer.toHexString(cmd);
	}
}
