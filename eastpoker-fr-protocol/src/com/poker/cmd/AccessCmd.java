package com.poker.cmd;

import com.poker.base.ServerIds;

public final class AccessCmd {
	
	public static final int CMD_ACCESS_HEAR_BEAT 			= (ServerIds.SERVER_ACCESS <<16) + 1;//0x30001
	public static final int CMD_ACCESS_HEAR_BEAT_RESPONSE 	= (ServerIds.SERVER_ACCESS <<16) + 2;//0x30001
	
	//------------------------------------------------------------------------------------------
	public static String getCmdString(int cmd){
		if(cmd == CMD_ACCESS_HEAR_BEAT){
			return "CMD_ACCESS_HEAR_BEAT";
		}else if(cmd == CMD_ACCESS_HEAR_BEAT_RESPONSE){
			return "cmd_access_hear_beat_response";
		}
		return "unknown_accesscmd";
	}
	
}
