package com.poker.cmd;

import com.poker.base.ServerIds;

public final class DispatchCmd {
	
	public static final int CMD_DISPATCH_REGISTER = (ServerIds.SERVER_DIAPATCHER <<16) + 1;//0x20001
	public static final int CMD_DISPATCH_UNREGISTER = (ServerIds.SERVER_DIAPATCHER <<16) + 2;//0x20002
	
	public static final int CMD_DISPATCH_DATA = (ServerIds.SERVER_DIAPATCHER <<16) + 3;//0x20003
	public static final int CMD_DISPATCH_DATA_GAME_GROUP = (ServerIds.SERVER_DIAPATCHER <<16) + 4;//0x20004
	public static final int CMD_DISPATCH_DATA_MATCH_GROUP = (ServerIds.SERVER_DIAPATCHER <<16) + 5;//0x20005
	
	//------------------------------------------------------------------------------------------
	public static String getCmdString(int cmd){
		if(cmd == CMD_DISPATCH_REGISTER){
			return "cmd_dispatch_register";
		}else if(cmd == CMD_DISPATCH_UNREGISTER){
			return "cmd_dispatch_unregister";
		}else if(cmd == CMD_DISPATCH_DATA){
			return "cmd_dispatch_data";
		}else if(cmd == CMD_DISPATCH_DATA_GAME_GROUP){
			return "cmd_dispatch_data_game_group";
		}else if(cmd == CMD_DISPATCH_DATA_MATCH_GROUP){
			return "cmd_dispatch_data_match_group";
		}
		return "unknown_dispatchcmd";
	}
	
}
