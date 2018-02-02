package com.poker.protocols;

import com.poker.games.Table;
import com.poker.games.User;
import com.poker.games.impl.GUser;
import com.poker.games.impl.config.GameConfig;
import com.poker.protocols.game.server.BroadcastUserExitProto.BroadcastUserExit;
import com.poker.protocols.game.server.BroadcastUserOfflineProto.BroadcastUserOffline;
import com.poker.protocols.game.server.BroadcastUserReadyProto.BroadcastUserReady;
import com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin;
import com.poker.protocols.texaspoker.GameUserProto.GameUser;
import com.poker.protocols.texaspoker.TexasGameConfigProto.TexasGameConfig;
import com.poker.protocols.texaspoker.TexasGameResponseLoginGameProto.TexasGameResponseLoginGame;



public class GBaseServer {
	
	public static byte[] userLogin(GUser gUser,Table table,GameConfig mGameConfig){
		TexasGameResponseLoginGame.Builder builder = TexasGameResponseLoginGame.newBuilder();
		
		//配置
		TexasGameConfig.Builder configBuilder = TexasGameConfig.newBuilder();
		configBuilder.setLevel(mGameConfig.level);
		configBuilder.setLevelName(mGameConfig.level_name);
		configBuilder.setMinUser(mGameConfig.table_min_user);
		configBuilder.setMaxUser(mGameConfig.table_max_user);
		configBuilder.setMinChip(mGameConfig.table_min_chip);
		configBuilder.setMaxChip(mGameConfig.table_max_chip);
		configBuilder.setActionTimeout(mGameConfig.table_action_timeout);
		int size = mGameConfig.table_ante.length;
		for(int i = 0;i<size;i++){
			configBuilder.addAnte(mGameConfig.table_ante[i]);
			configBuilder.addBlind(mGameConfig.table_blind[i]);
			configBuilder.addBlindTime(mGameConfig.table_blind_time[i]);
		}
		builder.setConfig(configBuilder);
		
		//自己信息
		builder.setSeatId(gUser.seatId);
		builder.setChipTotal(gUser.chip);
		builder.setChip(gUser.chip);
		builder.setPlayStatus(gUser.play_status.ordinal());
		
		//其它人信息
		for(int i =0 ;i<table.users.length;i++){
			User mUser = table.users[i];
			if(null  != mUser && mUser.seatId != gUser.seatId){
				GameUser.Builder userBuild = GameUser.newBuilder();
				userBuild.setSeatId(mUser.seatId);
				userBuild.setUid(mUser.uid);
				userBuild.setNickName(mUser.nick_name);
				userBuild.setHeadPortrait(mUser.head_portrait);
				userBuild.setLevel(mUser.level);
				userBuild.setChip(mUser.chip);
				userBuild.setChipTotal(mUser.chip_total);
				userBuild.setPlayStatus(gUser.play_status.ordinal());
				
				builder.addUsers(userBuild);
			}
		}
		
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] broadUserLogin(GUser mUser){
		BroadcastUserLogin.Builder builder = BroadcastUserLogin.newBuilder();
		
		GameUser.Builder userBuild = GameUser.newBuilder();
		userBuild.setSeatId(mUser.seatId);
		userBuild.setUid(mUser.uid);
		userBuild.setNickName(mUser.nick_name);
		userBuild.setHeadPortrait(mUser.head_portrait);
		userBuild.setLevel(mUser.level);
		userBuild.setChip(mUser.chip);
		userBuild.setChipTotal(mUser.chip_total);
		userBuild.setPlayStatus(mUser.play_status.ordinal());

		builder.setUsers(userBuild);
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] broadUserLogout(User mUser){
		BroadcastUserExit.Builder builder = BroadcastUserExit.newBuilder();
		builder.setSeatId(mUser.seatId);
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] broadUserReady(User mUser){
		BroadcastUserReady.Builder builder = BroadcastUserReady.newBuilder();
		builder.setSeatId(mUser.seatId);
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
