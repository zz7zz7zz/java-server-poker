package com.poker.cmd;

import com.poker.base.ServerIds;

public final class DispatchCmd {
	
	public static final int CMD_REGISTER = (ServerIds.SERVER_DIAPATCHER <<16) + 1;//0x20001
	public static final int CMD_UNREGISTER = (ServerIds.SERVER_DIAPATCHER <<16) + 2;//0x20002
	
	public static final int CMD_DISPATCH = (ServerIds.SERVER_DIAPATCHER <<16) + 3;//0x20003
}
