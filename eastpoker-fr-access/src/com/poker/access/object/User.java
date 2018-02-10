package com.poker.access.object;

public class User {
	
	public long uid;
	
	public long socketId;
	public long socketHeartBeatTime;
	
	public short gameSid;
	public short gameId;
	
	public short matchSid;
	public short matchId;
	
	public int tid;
	
	public void reset(){
		uid = -1;
		socketId = -1;
		socketHeartBeatTime = -1;
		
		gameSid = -1;
		gameId = -1;
		
		matchSid = -1;
		matchId = -1;
		
		tid   = -1;
	}

	@Override
	public String toString() {
		return "User [uid=" + uid + ", socketId=" + socketId + ", socketHeartBeatTime=" + socketHeartBeatTime
				+ ", gameSid=" + gameSid + ", gameId=" + gameId + ", matchSid=" + matchSid + ", matchId=" + matchId
				+ ", tid=" + tid + "]";
	}
	
}
