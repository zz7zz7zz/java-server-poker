package com.poker.games.protocols;

public final class GBaseCmd {
	
	public static final int CMD_SERVER_USERLOGIN   				= 0x1001; 
	public static final int CMD_SERVER_BROAD_USERLOGIN   		= 0x1002; 
	
	public static final int CMD_SERVER_USERLOGOUT   			= 0x1003; 
	public static final int CMD_SERVER_BROAD_USERLOGOUT   		= 0x1004; 
	
	public static final int CMD_SERVER_USERREADY   				= 0x1005; 
	public static final int CMD_SERVER_BROAD_USERREADY   		= 0x1006; 
	
	public static final int CMD_SERVER_BROAD_USEROFFLINE  		= 0x1007; 
}
