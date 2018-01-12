package com.poker.cmd;

import com.poker.base.ServerIds;

public final class DispatchCmd {
	
	public static final int CMD_REGISTER = (ServerIds.SERVER_DIAPATCHER <<16) + 1;//0x20001
	public static final int CMD_UNREGISTER = (ServerIds.SERVER_DIAPATCHER <<16) + 2;//0x20002
	
	public static final int CMD_DISPATCH = (ServerIds.SERVER_DIAPATCHER <<16) + 3;//0x20003
	public static final int CMD_DISPATCH_GAME_GROUP = (ServerIds.SERVER_DIAPATCHER <<16) + 4;//0x20004
	public static final int CMD_DISPATCH_MATCH_GROUP = (ServerIds.SERVER_DIAPATCHER <<16) + 5;//0x20005
	
	//------------------------------------------------------------------------------------------
	public static String getCmdString(int cmd){
		if(cmd == CMD_REGISTER){
			return "cmd_register";
		}else if(cmd == CMD_UNREGISTER){
			return "cmd_unregister";
		}else if(cmd == CMD_DISPATCH){
			return "cmd_dispatch";
		}else if(cmd == CMD_DISPATCH_GAME_GROUP){
			return "cmd_dispatch_game_group";
		}else if(cmd == CMD_DISPATCH_MATCH_GROUP){
			return "cmd_dispatch_match_group";
		}
		return "unknown";
	}
	
}
