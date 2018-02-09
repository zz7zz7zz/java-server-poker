package com.poker.access.object;

public class User {
	
	public long uid;
	
	public int accessId;
	public long socketId;
	public long socketHeartBeatTime;
	
	public short gameSid;
	public short gameId;
	
	public short matchSid;
	public short matchId;
	
	public int tableId = -1;
	
	public void reset(){
		uid = 0;
		socketId = 0;
		socketHeartBeatTime = 0;
		
		gameSid = 0;
		gameId = 0;
		
		matchSid = 0;
		matchId = 0;
		
		tableId = -1;
	}
}
