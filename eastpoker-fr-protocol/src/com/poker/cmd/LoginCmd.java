package com.poker.cmd;

import com.poker.base.Server;

public final class LoginCmd {
	
	public static final int CMD_LOGIN = (Server.SERVER_LOGIN <<16) + 1;//0x40001
	
	public static final int CMD_LOGIN_RESPONSE = (Server.SERVER_LOGIN <<16) + 2;//0x40001
}
