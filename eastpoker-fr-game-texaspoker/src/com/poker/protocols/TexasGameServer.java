package com.poker.protocols;

import com.poker.games.AbsTable;
import com.poker.games.AbsUser;
import com.poker.games.impl.Table;
import com.poker.games.impl.User;
import com.poker.games.impl.config.GameConfig;
import com.poker.games.impl.define.TexasDefine;
import com.poker.games.impl.define.TexasDefine.UserStatus;
import com.poker.protocols.texaspoker.TexasGameStartProto.TexasGameStart;
import com.poker.protocols.game.server.BroadcastUserExitProto.BroadcastUserExit;
import com.poker.protocols.game.server.BroadcastUserOfflineProto.BroadcastUserOffline;
import com.poker.protocols.game.server.BroadcastUserReadyProto.BroadcastUserReady;
import com.poker.protocols.texaspoker.BroadcastUserLoginProto.BroadcastUserLogin;
import com.poker.protocols.texaspoker.GameUserProto.GameUser;
import com.poker.protocols.texaspoker.TexasGameBroadcastNextOperateProto.TexasGameBroadcastNextOperate;
import com.poker.protocols.texaspoker.TexasGameBroadcastPotProto.TexasGameBroadcastPot;
import com.poker.protocols.texaspoker.TexasGameBroadcastUserActionProto.TexasGameBroadcastUserAction;
import com.poker.protocols.texaspoker.TexasGameBroadcastUserActionProto.TexasGameBroadcastUserAction.Operate;
import com.poker.protocols.texaspoker.TexasGameConfigProto.TexasGameConfig;
import com.poker.protocols.texaspoker.TexasGameDealFlopProto.TexasGameDealFlop;
import com.poker.protocols.texaspoker.TexasGameDealPreFlopProto.TexasGameDealPreFlop;
import com.poker.protocols.texaspoker.TexasGameDealRiverProto.TexasGameDealRiver;
import com.poker.protocols.texaspoker.TexasGameDealTurnProto.TexasGameDealTurn;
import com.poker.protocols.texaspoker.TexasGameEndProto.Result;
import com.poker.protocols.texaspoker.TexasGameEndProto.TexasGameEnd;
import com.poker.protocols.texaspoker.TexasGameReconnectProto.TexasGameReconnect;
import com.poker.protocols.texaspoker.TexasGameResponseLoginGameProto.TexasGameResponseLoginGame;
import com.poker.protocols.texaspoker.TexasGameShowHandProto.TexasGameShowHand;
import com.poker.protocols.texaspoker.TexasGameShowHandProto.UserCard;

public class TexasGameServer {
	
	//--------------------------------------------------------基础指令----------------------------------------------------------------
	public static byte[] userLogin(User gUser,AbsTable table,GameConfig mGameConfig){
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
		
		//桌子上其它人的信息
		User[] gTableUsers = (User[])table.users;
		for(int i =0 ;i<gTableUsers.length;i++){
			User user = gTableUsers[i];
			if(null  != user && user.seatId != gUser.seatId){
				GameUser.Builder userBuild = GameUser.newBuilder();
				userBuild.setSeatId(user.seatId);
				userBuild.setUid(user.uid);
				userBuild.setNickName(user.nick_name);
				userBuild.setHeadPortrait(user.head_portrait);
				userBuild.setLevel(user.level);
				userBuild.setChip(user.chip);
				userBuild.setChipTotal(user.chip_total);
				userBuild.setPlayStatus(user.play_status.ordinal());
				
				builder.addUsers(userBuild);
			}
		}
		
		//旁观用户信息
		User[] onLookerUsers = (User[])table.users;
		for(int i =0 ;i<onLookerUsers.length;i++){
			User user = onLookerUsers[i];
			if(null  != user){
				GameUser.Builder userBuild = GameUser.newBuilder();
				userBuild.setSeatId(user.seatId);
				userBuild.setUid(user.uid);
				userBuild.setNickName(user.nick_name);
				userBuild.setHeadPortrait(user.head_portrait);
				userBuild.setLevel(user.level);
				userBuild.setChip(user.chip);
				userBuild.setChipTotal(user.chip_total);
				userBuild.setPlayStatus(user.play_status.ordinal());

				builder.addOnLooker(userBuild);
			}
		}
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] broadUserLogin(User mUser){
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
	
	public static byte[] broadUserLogout(AbsUser mUser){
		BroadcastUserExit.Builder builder = BroadcastUserExit.newBuilder();
		builder.setSeatId(mUser.seatId);
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] broadUserReady(AbsUser mUser){
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
	
	//--------------------------------------------------------游戏指令----------------------------------------------------------------
	public static byte[] gameStart(int sb_seatid,int bb_seatid,int btn_seateId,long ante, long sb_round_chip,long bb_round_chip,Table table){
		
		TexasGameStart.Builder builder = TexasGameStart.newBuilder();
		builder.setSbSeatId(sb_seatid);
		builder.setBbSeatId(bb_seatid);
		builder.setBtnSeatId(btn_seateId);
		
		builder.setAnteAll(ante);
		builder.setSbForceBetChip(sb_round_chip);
		builder.setBbForceBetChip(bb_round_chip);
		
		User[] gTableUsers = (User[])table.users;
		for(int i =0 ;i<gTableUsers.length;i++){
			User user = gTableUsers[i];
			if(null  != user && user.play_status == UserStatus.PLAY ){
				GameUser.Builder userBuild = GameUser.newBuilder();
				userBuild.setSeatId(user.seatId);
				userBuild.setChip(user.chip);
				userBuild.setChipTotal(user.chip_total);
				userBuild.setRoundChip(user.round_chip);
				
				builder.addUser(userBuild);
			}
		}
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] dealPreFlop(byte[] cards){
		
		TexasGameDealPreFlop.Builder builder = TexasGameDealPreFlop.newBuilder();
		for(int i = 0;i<cards.length;i++){
			builder.addCards(cards[i]);
		}
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] dealFlop(byte[] cards){
		
		TexasGameDealFlop.Builder builder = TexasGameDealFlop.newBuilder();
		for(int i = 0;i<cards.length;i++){
			builder.addCards(cards[i]);
		}
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] dealTrun(byte[] cards){
		
		TexasGameDealTurn.Builder builder = TexasGameDealTurn.newBuilder();
		for(int i = 0;i<cards.length;i++){
			builder.addCards(cards[i]);
		}
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] dealRiver(byte[] cards){
		
		TexasGameDealRiver.Builder builder = TexasGameDealRiver.newBuilder();
		for(int i = 0;i<cards.length;i++){
			builder.addCards(cards[i]);
		}
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	//上一个操作者
	public static byte[] broadcastUserAction(int seateId , Operate operate , long chip ,long round_chip) {
		TexasGameBroadcastUserAction.Builder builder = TexasGameBroadcastUserAction.newBuilder();
		builder.setSeatId(seateId);
		builder.setOperate(operate);
		builder.setChip(chip);
		builder.setRoundChip(round_chip);
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	//下一个操作者
	public static byte[] broadcastNextOperateUser(int op_seate_id ,long op_call_chip,long op_min_raise_chip,long op_max_raise_chip) {
		TexasGameBroadcastNextOperate.Builder builder = TexasGameBroadcastNextOperate.newBuilder();
		builder.setNextOpSeatId(op_seate_id);
		builder.setNextOpCallChip(op_call_chip);
		builder.setNextOpMinRaiseChip(op_min_raise_chip);
		builder.setNextOpMaxRaiseChip(op_max_raise_chip);
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] broadcastPots(long ... pots) {
		TexasGameBroadcastPot.Builder builder = TexasGameBroadcastPot.newBuilder();
		for(int i = 0 ;i<pots.length;i++){
			builder.addPots(pots[i]);
		}
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] showHand(AbsTable table){
		
		TexasGameShowHand.Builder builder = TexasGameShowHand.newBuilder();
        User[] gGsers=(User[])table.users;
		for(int i = 0;i<gGsers.length;i++){
			if(null == gGsers[i] || !gGsers[i].isPlaying() || gGsers[i].isFold){
				continue;
			}
			UserCard.Builder usercardBuilder =  UserCard.newBuilder();
			usercardBuilder.setSeateId(table.users[i].seatId);
			for(int j = 0 ;j<gGsers[i].handCard.length;j++){
				usercardBuilder.addCards(gGsers[i].handCard[j]);
			}
			builder.addMUserCards(usercardBuilder);
		}
	
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] reconnect(Table table, User self,GameConfig mGameConfig){
		TexasGameReconnect.Builder builder = TexasGameReconnect.newBuilder();
		
		//配置信息
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
		
		//玩家列表
		User[] gTableUsers = (User[])table.users;
		for(int i =0 ;i<gTableUsers.length;i++){
			User user = gTableUsers[i];
			if(null  != user){
				GameUser.Builder userBuild = GameUser.newBuilder();
				userBuild.setSeatId(user.seatId);
				
				if(user.uid != self.uid){
					userBuild.setUid(user.uid);
					userBuild.setNickName(user.nick_name);
					userBuild.setHeadPortrait(user.head_portrait);
					userBuild.setLevel(user.level);
				}
				
				userBuild.setOperate(user.operate.ordinal());
				userBuild.setChip(user.chip);
				userBuild.setChipTotal(user.chip_total);
				userBuild.setPlayStatus(user.play_status.ordinal());
				
				builder.addUsers(userBuild);
			}
		}
		
		//大小盲注信息
		builder.setTableStatus(table.table_status.ordinal());
		builder.setBtnSeatId(table.btn_seateId);
		builder.setSbSeatId(table.sb_seatid);
		builder.setSbSeatId(table.bb_seatid);

		builder.setAnteAll(table.ante_all);
		builder.setSbForceBetChip(table.sb_force_bet);
		builder.setBbForceBetChip(table.bb_force_bet);

		//牌的信息
		for(int i = 0 ;i < self.handCard.length;i++){
			builder.addCards(self.handCard[i]);
		}
		
		int flop = table.step.ordinal();
		int FLOP = TexasDefine.GameStep.FLOP.ordinal();
		int TRUN = TexasDefine.GameStep.TRUN.ordinal();
		int RIVER = TexasDefine.GameStep.RIVER.ordinal();
		
		if(flop >= FLOP){
			for(int i = 0 ;i < table.flop.length;i++){
				builder.addCardsFlop(table.flop[i]);
			}
		}

		if(flop >= TRUN){
			for(int i = 0 ;i < table.flop.length;i++){
				builder.addCardsTrun(table.flop[i]);
			}
		}

		if(flop >= RIVER){
			for(int i = 0 ;i < table.flop.length;i++){
				builder.addCardsRiver(table.flop[i]);
			}
		}
		
		//当前操作人信息
		builder.setNextOpSeatId(table.op_seatid);
		builder.setNextOpCallChip(table.op_call_chip);
		builder.setNextOpMinRaiseChip(table.op_min_raise_chip);
		builder.setNextOpMaxRaiseChip(table.op_max_raise_chip);
		builder.setMaxRoundChip(table.max_round_chip);
		
		//下一个操作者剩余操作时间
		builder.setRestActionTimeout(mGameConfig.table_action_timeout);
		
		//Pot信息
		for(int i = 0 ;i<table.potList.size();i++){
			builder.addPots(table.potList.get(i).pot_chips);
		}
		
		//围观用户
		gTableUsers = (User[])table.onLookers;
		for(int i =0 ;i<gTableUsers.length;i++){
			User user = gTableUsers[i];
			if(null  != user){
				GameUser.Builder userBuild = GameUser.newBuilder();
				userBuild.setSeatId(user.seatId);
			
				userBuild.setUid(user.uid);
				userBuild.setNickName(user.nick_name);
				userBuild.setHeadPortrait(user.head_portrait);
				userBuild.setLevel(user.level);

				userBuild.setOperate(user.operate.ordinal());
				userBuild.setChip(user.chip);
				userBuild.setChipTotal(user.chip_total);
				userBuild.setPlayStatus(user.play_status.ordinal());
				
				builder.addUsers(userBuild);
			}
		}
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] gameOver(AbsTable table){
		TexasGameEnd.Builder builder = TexasGameEnd.newBuilder();
		User[] gTableUsers = (User[])table.users;
		for(int i = 0;i<gTableUsers.length;i++){
			if(null == gTableUsers[i]){
				continue;
			}
			Result.Builder resultBuilder = Result.newBuilder();
			resultBuilder.setSeateId(gTableUsers[i].seatId);
			resultBuilder.setCardResult(gTableUsers[i].result.cardType.ordinal());
			for(int j=0;j<gTableUsers[i].result.finalCards.length;j++) {
				resultBuilder.addCards(gTableUsers[i].result.finalCards[i]);
			}
			resultBuilder.setWinChip(gTableUsers[i].win_chip);
			resultBuilder.setChip(gTableUsers[i].chip);
			builder.addMResults(resultBuilder);
		}
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
}
