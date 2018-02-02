package com.poker.games;

public class User {
	
	public long uid;//用户唯一Id
	public String nick_name;//用户昵称
	public String head_portrait;//头像
	public long chip_total;//用户筹码
	public long chip;//用户筹码
	public int level;//用户等级
	
	public int seatId = -1;//座位id
	
	public int tid;
	public int accessId = -1;
	public int onLineStatus;//1在线，2掉线
	public boolean isReady;
	
	public void reset(){
		uid = 0;
		nick_name = "";
		head_portrait = "";
		chip_total = 0;
		chip = 0;
		level = 0;
		
		seatId = -1;
		tid = -1;
		accessId = -1;
		onLineStatus = 1;
		isReady = false;
	}
	
	public void startGame(){
		
	}

	public void stopGame(){
		
	}
	
	public boolean isOffline(){
		return onLineStatus == 2;
	}
}
