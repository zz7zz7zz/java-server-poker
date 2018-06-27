package com.poker.match;

public class MatchServer {
	
	public short matchType;//比赛类型
	public short matchGameId;//比赛游戏Id
	public short matchServerId;//比赛服务id
	public String matchName;//比赛名字
	
	public MatchServer(short matchType, short matchGameId, short matchServerId, String matchName) {
		super();
		this.matchType = matchType;
		this.matchGameId = matchGameId;
		this.matchServerId = matchServerId;
		this.matchName = matchName;
	}
	
}
