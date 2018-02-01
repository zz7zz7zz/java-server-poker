package com.poker.games;

public class User {
	
	public long uid;//用户唯一Id
	public String nick_name;//用户昵称
	public String head_portrait;//头像
	public long chip;//用户筹码
	public int level;//用户等级
	
	public int seatId = -1;//座位id
	
	public int tid;
	public int accessId = -1;
	public int onLineStatus;
	public boolean isReady;
	
	public void reset(){
		tid = -1;
		
		uid = 0;
		chip = 0;
		level = 0;
		
		seatId = -1;
	}
	
	public void startGame(){
		
	}

	public void stopGame(){
		
	}
	
	public boolean isOffline(){
		return onLineStatus == 0;
	}
}
