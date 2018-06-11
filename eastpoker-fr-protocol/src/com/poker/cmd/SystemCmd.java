package com.poker.cmd;


public class SystemCmd {
	
	public static final int CMD_SYS_HEAR_BEAT 			= 0x0001;
	public static final int CMD_SYS_UPDATE_CONFIG 		= 0x0002;
	
	//--------------------------------------------------
	public static final int CMD_SYS_HEAR_BEAT_REPONSE 	= 0x0101;

	public static String getCmdString(int cmd){
		if(cmd == CMD_SYS_HEAR_BEAT){
			return "cmd_sys_hear_beat";
		}else if(cmd == CMD_SYS_UPDATE_CONFIG){
			return "cmd_sys_update_config";
		}else if(cmd == CMD_SYS_HEAR_BEAT_REPONSE){
			return "cmd_sys_hear_beat_reponse";
		}
		return "unknown_SystemCmd";
	}
}
