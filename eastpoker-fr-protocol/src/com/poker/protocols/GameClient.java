package com.poker.protocols;

import com.poker.protocols.game.client.RequestLoginGameProto.RequestLoginGame;

public class GameClient {
	
	/**
	 * 
	 * @param gameId 游戏ID
	 * @param gameLevel 游戏Level
	 * @return
	 */
	public static byte[] requestLogin(int gameId,int gameLevel){
		RequestLoginGame.Builder builder = RequestLoginGame.newBuilder();
		builder.setGameid(gameId);
		builder.setLevel(gameLevel);
		byte[] body = builder.build().toByteArray();
		return body;
	}

}
