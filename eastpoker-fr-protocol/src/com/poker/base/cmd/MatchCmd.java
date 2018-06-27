package com.poker.base.cmd;

import com.poker.base.type.TServer;

public class MatchCmd {
	
	//自建比赛场次
	public static final int CMD_MATCHSERVER_REGISTER   = (TServer.SERVER_MATCH <<16) + 1;//0x90001 , 注册比赛服务
	public static final int CMD_MATCHSERVER_UNREGISTER = (TServer.SERVER_MATCH <<16) + 2;//0x90002 , 取消比赛服务
	
	public static final int CMD_MATCH_CREATE  = (TServer.SERVER_MATCH <<16) + 1;//0x90001 , 创建比赛
	public static final int CMD_MATCH_DESTROY = (TServer.SERVER_MATCH <<16) + 2;//0x90002 , 销毁比赛
	
	public static String getCmdString(int cmd){
		if(cmd == CMD_MATCHSERVER_REGISTER){
			return "cmd_matchserver_register";
		}else if(cmd == CMD_MATCHSERVER_UNREGISTER){
			return "cmd_matchserver_unregister";
		}else if(cmd == CMD_MATCH_CREATE){
			return "cmd_match_create";
		}else if(cmd == CMD_MATCH_DESTROY){
			return "cmd_match_destroy";
		}
		return "unknown_match_cmd 0x"+Integer.toHexString(cmd);
	}
}
