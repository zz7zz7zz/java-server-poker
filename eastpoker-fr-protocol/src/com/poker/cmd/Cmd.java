package com.poker.cmd;

import java.util.ArrayList;

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
		}else if(server == ServerIds.SERVER_MATCH){
			return MatchCmd.getCmdString(cmd);
		}else {
			if(cmd < 0x1001){
				return SystemCmd.getCmdString(cmd);
			}
			
			int size = cmdListener.size();
			for(int i = 0;i<size;i++){
				String ret = cmdListener.get(i).getCmdString(cmd);
				if(null != ret && ret.length() > 0){
					return ret;
				}
			}
			return "unknown_gameimpl_cmd 0x"+Integer.toHexString(cmd);
		}
	}
	
	private static ArrayList<ICmdRecognizer> cmdListener = new ArrayList<ICmdRecognizer>();
	public static void AddCmdRecognizer(ICmdRecognizer listener){
		cmdListener.remove(listener);
		cmdListener.add(listener);
	}
	
	public interface ICmdRecognizer{
		public String getCmdString(int cmd);
	}
}
