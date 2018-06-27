package com.poker.base.cmd;

import java.util.ArrayList;

import com.poker.base.type.TServer;

public class Cmd {
	
	public static String getCmdString(int cmd){
		int server = cmd >> 16;
		if(server == TServer.SERVER_ACCESS){
			return CmdAccess.getCmdString(cmd);
		}else if(server == TServer.SERVER_USER){
			return CmdUser.getCmdString(cmd);
		}else if(server == TServer.SERVER_ALLOCATOR){
			return CmdAllocator.getCmdString(cmd);
		}else if(server == TServer.SERVER_LOGIN){
			return CmdLogin.getCmdString(cmd);
		}else if(server == TServer.SERVER_DIAPATCHER){
			return CmdDispatcher.getCmdString(cmd);
		}else if(server == TServer.SERVER_MONITOR){
			return CmdMonitor.getCmdString(cmd);
		}else if(server == TServer.SERVER_GAME){
			return CmdGame.getCmdString(cmd);
		}else if(server == TServer.SERVER_MATCH){
			return CmdMatch.getCmdString(cmd);
		}else {
			if(cmd < 0x1001){
				return CmdSystem.getCmdString(cmd);
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
