package com.poker.cmd;

import com.poker.server.Server;

public final class AccessCmd {
	
	public static final int CMD_CLIENT_HEAR_BEAT = (Server.SERVER_ACCESS <<16) + 1;//0x30001
}
