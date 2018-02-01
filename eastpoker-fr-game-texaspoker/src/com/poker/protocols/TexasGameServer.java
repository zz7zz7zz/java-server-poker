package com.poker.protocols;


import com.poker.games.GDefine;
import com.poker.games.Table;
import com.poker.games.impl.GTable;
import com.poker.games.impl.GUser;
import com.poker.games.impl.config.GameConfig;
import com.poker.protocols.texaspoker.TexasGameStartProto.TexasGameStart;
import com.poker.protocols.texaspoker.TexasGameStartProto.Config;
import com.poker.protocols.texaspoker.TexasGameBroadcastActionProto.TexasGameBroadcastAction.Operate;
import com.poker.protocols.texaspoker.TexasGameBroadcastActionProto.TexasGameBroadcastAction;
import com.poker.protocols.texaspoker.TexasGameDealFlopProto.TexasGameDealFlop;
import com.poker.protocols.texaspoker.TexasGameDealPreFlopProto.TexasGameDealPreFlop;
import com.poker.protocols.texaspoker.TexasGameDealRiverProto.TexasGameDealRiver;
import com.poker.protocols.texaspoker.TexasGameDealTurnProto.TexasGameDealTurn;
import com.poker.protocols.texaspoker.TexasGameEndProto.Result;
import com.poker.protocols.texaspoker.TexasGameEndProto.TexasGameEnd;
import com.poker.protocols.texaspoker.TexasGameReconnectProto.TexasGameReconnect;
import com.poker.protocols.texaspoker.TexasGameReconnectProto.User;
import com.poker.protocols.texaspoker.TexasGameSbBbBetProto.TexasGameSbBbBet;
import com.poker.protocols.texaspoker.TexasGameShowHandProto.TexasGameShowHand;
import com.poker.protocols.texaspoker.TexasGameShowHandProto.UserCard;

public class TexasGameServer {
	
	public static byte[] start(int sb_seatid,int bb_seatid,int btn_seateId, GameConfig mGameConfig){
		
		TexasGameStart.Builder builder = TexasGameStart.newBuilder();
		builder.setSbSeatId(sb_seatid);
		builder.setBbSeatId(bb_seatid);
		builder.setBtnSeatId(btn_seateId);
		
		Config.Builder configBuilder = Config.newBuilder();
		configBuilder.setLevel(mGameConfig.level);
		configBuilder.setLevelName(mGameConfig.level_name);
		configBuilder.setMinUser(mGameConfig.table_min_user);
		configBuilder.setMaxUser(mGameConfig.table_max_user);
		configBuilder.setMinChip(mGameConfig.table_min_chip);
		configBuilder.setMaxChip(mGameConfig.table_max_chip);
		int size = mGameConfig.table_ante.length;
		for(int i = 0;i<size;i++){
			configBuilder.addAnte(mGameConfig.table_ante[i]);
			configBuilder.addBlind(mGameConfig.table_blind[i]);
			configBuilder.addBlindTime(mGameConfig.table_blind_time[i]);
		}
		
		builder.setConfig(configBuilder);
		
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
	
	public static byte[] sbBbBet(int sbSeatId,long sbBet , int bbSeatId,long bbBet){
		
		TexasGameSbBbBet.Builder builder = TexasGameSbBbBet.newBuilder();
		builder.setBbseatId(sbSeatId);
		builder.setSBBet(sbBet);
		builder.setBbseatId(bbSeatId);
		builder.setBbBet(bbBet);
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
	
	public static byte[] broadcastUserAction(GUser mUser ,long max_round_chip,int op_seate_id ,long op_min_raise_chip,long op_max_raise_chip,long op_call_chip) {
		
		//上一个操作者
		TexasGameBroadcastAction.Builder builder = TexasGameBroadcastAction.newBuilder();
		if(null != mUser) {
			builder.setSeatId(mUser.seatId);
			builder.setOperate(mUser.operate);
			builder.setChip(mUser.chip);
			builder.setRoundChip(mUser.round_chip);
			
			builder.setRemainingChip(mUser.chip);
		}

		builder.setMaxRoundChip(max_round_chip);
		
		//下一个操作者
		builder.setNextOpSeatId(op_seate_id);
		builder.setNextOpCallChip(op_call_chip);
		builder.setNextOpMinRaiseChip(op_min_raise_chip);
		builder.setNextOpMaxRaiseChip(op_max_raise_chip);

		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] showHand(Table table){
		
		TexasGameShowHand.Builder builder = TexasGameShowHand.newBuilder();
        GUser[] gGsers=(GUser[])table.users;
		for(int i = 0;i<gGsers.length;i++){
			if(null == gGsers[i]){
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
	
	public static byte[] reconnect(GTable table, GUser mUser,GameConfig mGameConfig){
		TexasGameReconnect.Builder builder = TexasGameReconnect.newBuilder();
		
		Config.Builder configBuilder = Config.newBuilder();
		configBuilder.setLevel(mGameConfig.level);
		configBuilder.setLevelName(mGameConfig.level_name);
		configBuilder.setMinUser(mGameConfig.table_min_user);
		configBuilder.setMaxUser(mGameConfig.table_max_user);
		configBuilder.setMinChip(mGameConfig.table_min_chip);
		configBuilder.setMaxChip(mGameConfig.table_max_chip);
		int size = mGameConfig.table_ante.length;
		for(int i = 0;i<size;i++){
			configBuilder.addAnte(mGameConfig.table_ante[i]);
			configBuilder.addBlind(mGameConfig.table_blind[i]);
			configBuilder.addBlindTime(mGameConfig.table_blind_time[i]);
		}
		
		builder.setConfig(configBuilder);
		
		 GUser[] gGsers=(GUser[])table.users;
		for(int i = 0;i<gGsers.length;i++){
			User.Builder userBuilder = User.newBuilder();
			userBuilder.setUid(gGsers[i].uid);
			userBuilder.setSeatId(gGsers[i].seatId);
			userBuilder.setNickName(gGsers[i].nick_name);
			userBuilder.setHeadPortrait(gGsers[i].head_portrait);
			userBuilder.setChip(gGsers[i].chip);
			userBuilder.setLevel(gGsers[i].level);
			userBuilder.setOperate(gGsers[i].operate);
			userBuilder.setRoundChip(gGsers[i].round_chip);
			builder.addMUsers(userBuilder);
		}
		
		builder.setTableStatus(table.table_status.ordinal());
		builder.setSbSeatId(table.sb_seatid);
		builder.setSbSeatId(table.bb_seatid);
		builder.setBtnSeatId(table.btn_seateId);

		for(int i = 0 ;i < mUser.handCard.length;i++){
			builder.addCards(mUser.handCard[i]);
		}

		for(int i = 0 ;i < table.flop.length;i++){
			builder.addCards(table.flop[i]);
		}
		
		for(int i = 0 ;i < table.turn.length;i++){
			builder.addCards(table.turn[i]);
		}
		
		for(int i = 0 ;i < table.river.length;i++){
			builder.addCards(table.river[i]);
		}
		
		builder.setNextOpSeatId(table.action_seatid);
		builder.setNextOpMinRaiseChip(table.op_min_raise_chip);
		builder.setNextOpMaxRaiseChip(table.op_max_raise_chip);
		builder.setNextOpCallChip(table.op_call_chip);
		
		builder.setMaxRoundChip(table.max_round_chip);
		
		builder.setRestActionTimeout(mGameConfig.table_action_timeout);
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] stop(Table table){
		TexasGameEnd.Builder builder = TexasGameEnd.newBuilder();
		for(int i = 0;i<table.users.length;i++){
			if(null == table.users[i]){
				continue;
			}
			Result.Builder resultBuilder = Result.newBuilder();
			resultBuilder.setSeateId(table.users[i].seatId);
			resultBuilder.setCardResult(0);
			for(int j=0;j<5;j++) {
				resultBuilder.addCards(i);
			}
			resultBuilder.setChip(0);
			resultBuilder.setWinChip(0);
			builder.addMResults(resultBuilder);
		}
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
}
