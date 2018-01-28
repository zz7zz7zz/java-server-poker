package com.poker.access.object;

public class User {
	
	public long uid;
	
	public long socketId;
	public long socketHeartBeatTime;
	
	public int gameSid;
	public int gameId;
	
	public int matchSid;
	public int matchId;
	
	public void reset(){
		uid = 0;
		socketId = 0;
		socketHeartBeatTime = 0;
		
		gameSid = 0;
		gameId = 0;
		
		matchSid = 0;
		matchId = 0;
	}
}
