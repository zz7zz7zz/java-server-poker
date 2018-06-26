package com.poker.cmd;


public class SystemCmd {
	
	public static final int CMD_SYS_HEAR_BEAT 			= 0x0001;
	
	//--------------------------------------------------
	public static final int CMD_SYS_HEAR_BEAT_REPONSE 	= 0x0101;
	public static final int CMD_SYS_SERVER_CONFIG 		= 0x0102;

	public static String getCmdString(int cmd){
		if(cmd == CMD_SYS_HEAR_BEAT){
			return "cmd_sys_hear_beat";
		}else if(cmd == CMD_SYS_HEAR_BEAT_REPONSE){
			return "cmd_sys_hear_beat_reponse";
		}else if(cmd == CMD_SYS_SERVER_CONFIG){
			return "cmd_sys_server_config";
		}
		return "unknown_system_cmd";
	}
}
