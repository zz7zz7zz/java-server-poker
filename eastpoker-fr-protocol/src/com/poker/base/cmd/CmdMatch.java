package com.poker.base.cmd;

import com.poker.base.type.TServer;

public class CmdMatch {
	
	//自建比赛场次
	public static final int CMD_MATCH_TO_MATCHMGR_REGISTER     = (TServer.SERVER_MATCH <<16) + 1;//0x90001 , 注册比赛服务
	public static final int CMD_MATCH_TO_MATCHMGR_UNREGISTER   = (TServer.SERVER_MATCH <<16) + 2;//0x90002 , 取消比赛服务
	public static final int CMD_MATCH_TO_MATCHMGR_HEARTBEAT    = (TServer.SERVER_MATCH <<16) + 3;//0x90001 , 注册比赛服务
	
	public static final int CMD_USER_CREATE_MATCH  = (TServer.SERVER_MATCH <<16) + 4;//0x90001 , 创建比赛
	public static final int CMD_USER_DESTROY_MATCH = (TServer.SERVER_MATCH <<16) + 5;//0x90002 , 销毁比赛
	
	public static String getCmdString(int cmd){
		if(cmd == CMD_MATCH_TO_MATCHMGR_REGISTER){
			return "cmd_match_to_matchmgr_register";
		}else if(cmd == CMD_MATCH_TO_MATCHMGR_UNREGISTER){
			return "cmd_match_to_matchmgr_unregister";
		}else if(cmd == CMD_USER_CREATE_MATCH){
			return "cmd_user_create_match";
		}else if(cmd == CMD_USER_DESTROY_MATCH){
			return "cmd_user_destroy_match";
		}
		return "unknown_match_cmd 0x"+Integer.toHexString(cmd);
	}
}
