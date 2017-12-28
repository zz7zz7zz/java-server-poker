package com.poker.cmd;

import com.poker.base.Server;

public final class DispatchCmd {
	
	public static final int CMD_REGISTER = (Server.SERVER_DIAPATCHER <<16) + 1;//0x20001
	public static final int CMD_UNREGISTER = (Server.SERVER_DIAPATCHER <<16) + 2;//0x20002
}
