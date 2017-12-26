package com.poker.cmd;

import com.poker.server.Server;

public final class DispatchCmd {
	
	public static final int CMD_REGISTER = (Server.SERVER_DIAPATCHER <<16) + 1;//0x20001
}
