package com.poker.game.handler;

import com.poker.games.Table;
import com.poker.games.User;
import com.poker.protocols.game.BroadcastUserExitProto.BroadcastUserExit;
import com.poker.protocols.game.BroadcastUserLoginProto.BroadcastUserLogin;
import com.poker.protocols.game.BroadcastUserOfflineProto.BroadcastUserOffline;
import com.poker.protocols.game.GameUserProto.GameUser;
import com.poker.protocols.game.UserLoginProto.UserLogin;

public class GameBaseServer {
	
	public static byte[] userLogin(int seatId,Table table){
		UserLogin.Builder builder = UserLogin.newBuilder();
		builder.setSeatId(seatId);
		
		for(int i =0 ;i<table.users.length;i++){
			User mUser = table.users[i];
			if(null  != mUser && mUser.seatId != seatId){
				GameUser.Builder userBuild = GameUser.newBuilder();
				userBuild.setUid(mUser.uid);
				userBuild.setNickName(mUser.nick_name);
				userBuild.setHeadPortrait(mUser.head_portrait);
				userBuild.setChip(mUser.chip);
				userBuild.setLevel(mUser.level);
				userBuild.setSeatId(mUser.seatId);
				
				builder.addUsers(userBuild);
			}
		}
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] broadUserLogin(User mUser){
		BroadcastUserLogin.Builder builder = BroadcastUserLogin.newBuilder();
		
		GameUser.Builder userBuild = GameUser.newBuilder();
		userBuild.setUid(mUser.uid);
		userBuild.setNickName(mUser.nick_name);
		userBuild.setHeadPortrait(mUser.head_portrait);
		userBuild.setChip(mUser.chip);
		userBuild.setLevel(mUser.level);
		userBuild.setSeatId(mUser.seatId);
		
		builder.addUsers(userBuild);
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] broadUserLogout(User mUser){
		BroadcastUserExit.Builder builder = BroadcastUserExit.newBuilder();
		
		GameUser.Builder userBuild = GameUser.newBuilder();
		userBuild.setUid(mUser.uid);
		userBuild.setSeatId(mUser.seatId);
		
		builder.addUsers(userBuild);
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] broadUserOffline(long uid,int status){
		BroadcastUserOffline.Builder builder = BroadcastUserOffline.newBuilder();
		builder.setUid(uid);
		builder.setStatus(status);
		byte[] body = builder.build().toByteArray();
		return body;
	}
}
