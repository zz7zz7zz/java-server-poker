package com.poker.cmd;

import com.poker.base.ServerIds;

public class MatchCmd {
	
	//自建比赛场次
	public static final int CMD_MATCH_CREATE  = (ServerIds.SERVER_MATCH <<16) + 1;//0x90001 , 创建比赛
	public static final int CMD_MATCH_DESTROY = (ServerIds.SERVER_MATCH <<16) + 2;//0x90002 , 销毁比赛
	
	public static String getCmdString(int cmd){
		if(cmd == CMD_MATCH_CREATE){
			return "cmd_match_create";
		}else if(cmd == CMD_MATCH_DESTROY){
			return "cmd_match_destroy";
		}
		return "unknown_match_cmd 0x"+Integer.toHexString(cmd);
	}
}
