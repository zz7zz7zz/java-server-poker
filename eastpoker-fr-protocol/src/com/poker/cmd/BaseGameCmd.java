package com.poker.cmd;


public final class BaseGameCmd {
	
	//客户端发起   （游戏基础命令字）,范围是0x1001~0x1FFF
	public static final int CMD_CLIENT_USER_EXIT             	= 0x1002;//用户退出
	public static final int CMD_CLIENT_USER_READY 				= 0x1003;//用户准备
	public static final int CMD_CLIENT_KICK_USER 	    		= 0x1004;//用户踢人
	public static final int CMD_CLIENT_OFFLINE 	    			= 0x1005;//用户掉线
	
	//服务器返回  （游戏基础命令字）,范围是0x2001~0x2FFF
	public static final int CMD_SERVER_USERLOGIN   				= 0x2001;//用户进入
	public static final int CMD_SERVER_BROAD_USERLOGIN   		= 0x2002; 
	
	public static final int CMD_SERVER_USERLOGOUT   			= 0x2003;//用户退出 
	public static final int CMD_SERVER_BROAD_USERLOGOUT   		= 0x2004; 
	
	public static final int CMD_SERVER_USERREADY   				= 0x2005;//用户准备
	public static final int CMD_SERVER_BROAD_USERREADY   		= 0x2006; 
	
	public static final int CMD_SERVER_BROAD_USEROFFLINE  		= 0x2007;//用户掉线
	
	//登录错误类型
	public static final int CMD_SERVER_LOGIN_ERR 				= 0x2100;
	
	
	//----------------------------------------------------------------
	public static final int ERR_CODE_LOGIN_TABLE_FULL = 1;//桌子满了
	
	
	public static String getCmdString(int cmd){
		if(cmd == CMD_CLIENT_USER_EXIT){
			return "cmd_client_user_exit";
		}else if(cmd == CMD_CLIENT_USER_READY){
			return "cmd_client_user_ready";
		}else if(cmd == CMD_CLIENT_KICK_USER){
			return "cmd_client_kick_user";
		}else if(cmd == CMD_CLIENT_OFFLINE){
			return "cmd_client_offline";
		}else if(cmd == CMD_SERVER_USERLOGIN){
			return "cmd_server_userlogin";
		}else if(cmd == CMD_SERVER_BROAD_USERLOGIN){
			return "cmd_server_broad_userlogin";
		}else if(cmd == CMD_SERVER_USERLOGOUT){
			return "cmd_server_userlogout";
		}else if(cmd == CMD_SERVER_BROAD_USERLOGOUT){
			return "cmd_server_broad_userlogout";
		}else if(cmd == CMD_SERVER_USERREADY){
			return "cmd_server_userready";
		}else if(cmd == CMD_SERVER_BROAD_USERREADY){
			return "cmd_server_broad_userready";
		}else if(cmd == CMD_SERVER_BROAD_USEROFFLINE){
			return "cmd_server_broad_useroffline";
		}else if(cmd == CMD_SERVER_LOGIN_ERR){
			return "cmd_server_login_err";
		}
		return "";
	}
}
