package com.poker.cmd;

import com.poker.base.ServerIds;

public final class LoginCmd {
	
	public static final int CMD_LOGIN = (ServerIds.SERVER_LOGIN <<16) + 1;//0x40001
	
	public static final int CMD_LOGIN_RESPONSE = (ServerIds.SERVER_LOGIN <<16) + 2;//0x40001
}
