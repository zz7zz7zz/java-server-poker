package com.poker.cmd;

import com.poker.base.ServerIds;

public class Cmd {
	
	public static String getCmdString(int cmd){
		int server = cmd >> 16;
		if(server == ServerIds.SERVER_ACCESS){
			return AccessCmd.getCmdString(cmd);
		}else if(server == ServerIds.SERVER_USER){
			return UserCmd.getCmdString(cmd);
		}else if(server == ServerIds.SERVER_ALLOCATOR){
			return AllocatorCmd.getCmdString(cmd);
		}else if(server == ServerIds.SERVER_LOGIN){
			return LoginCmd.getCmdString(cmd);
		}else if(server == ServerIds.SERVER_DIAPATCHER){
			return DispatchCmd.getCmdString(cmd);
		}else if(server == ServerIds.SERVER_MONITOR){
			return MonitorCmd.getCmdString(cmd);
		}else if(server == ServerIds.SERVER_GAME){
			return GameCmd.getCmdString(cmd);
		}else {
			if(cmd < 0x1001){
				return SystemCmd.getCmdString(cmd);
			}
			
			return "unknown_gameimpl_cmd";
		}
	}
}
