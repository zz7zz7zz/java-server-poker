package com.poker.games.impl.handler;


import com.poker.games.Table;
import com.poker.games.impl.GUser;
import com.poker.games.impl.config.GameConfig;
import com.poker.protocols.texaspoker.TexasGameStartProto.TexasGameStart;
import com.poker.protocols.texaspoker.TexasGameStartProto.Config;
import com.poker.protocols.texaspoker.TexasGameActionProto.TexasGameAction;
import com.poker.protocols.texaspoker.TexasGameActionProto.TexasGameAction.Operate;
import com.poker.protocols.texaspoker.TexasGameDealFlopProto.TexasGameDealFlop;
import com.poker.protocols.texaspoker.TexasGameDealPreFlopProto.TexasGameDealPreFlop;
import com.poker.protocols.texaspoker.TexasGameDealRiverProto.TexasGameDealRiver;
import com.poker.protocols.texaspoker.TexasGameDealTurnProto.TexasGameDealTurn;
import com.poker.protocols.texaspoker.TexasGameEndProto.Result;
import com.poker.protocols.texaspoker.TexasGameEndProto.TexasGameEnd;
import com.poker.protocols.texaspoker.TexasGameReconnectProto.TexasGameReconnect;
import com.poker.protocols.texaspoker.TexasGameReconnectProto.User;
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
//		return DataPacket.write(writeBuff, squenceId, GCmd.CMD_SERVER_DEAL_PREFLOP, (byte)0, body,0,body.length);
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
	
	public static byte[] broadcastUserAction(GUser mUser ,
			
			long max_round_chip,int op_seate_id ,long op_min_raise_chip,long op_max_raise_chip,long op_call_chip) {
		
		//上一个操作者
		TexasGameAction.Builder builder = TexasGameAction.newBuilder();
		if(null != mUser) {
			builder.setSeatId(mUser.seatId);
			builder.setOperate(mUser.action_type);
			builder.setChip(mUser.chip);
			builder.setRemainingChip(mUser.chip);
			builder.setRoundChip(mUser.round_chip);
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
		for(int i = 0;i<table.users.length;i++){
			if(null == table.users[i]){
				continue;
			}
			UserCard.Builder usercardBuilder =  UserCard.newBuilder();
			usercardBuilder.setSeateId(table.users[i].seatId);
			usercardBuilder.addCards(1);
			usercardBuilder.addCards(2);
			builder.addMUserCards(usercardBuilder);
		}
	
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] reconnect(Table table, GameConfig mGameConfig){
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
		
		
		for(int i = 0;i<table.users.length;i++){
			User.Builder userBuilder = User.newBuilder();
			userBuilder.setUid(1000);
			userBuilder.setSeatId(i);
			userBuilder.setNickName("name");
			userBuilder.setHeadPortrait("www.baidu.com/logo.png");
			userBuilder.setChip(100);
			userBuilder.setLevel(1);
			userBuilder.setOperate(Operate.CALL);
			userBuilder.setRoundChip(200);
			builder.addMUsers(userBuilder);
		}
		
		builder.setTableStatus(1);
		builder.setSbSeatId(1);
		builder.setSbSeatId(1);
		builder.setBtnSeatId(1);
		
		
		builder.addCards(1);
		builder.addCards(1);
		
		builder.addCardsFlop(1);
		builder.addCardsFlop(1);
		builder.addCardsFlop(1);
		
		builder.addCardsTrun(1);
		
		builder.addCardsRiver(1);
		
		builder.setNextOpSeatId(1);
		builder.setNextOpMinRaiseChip(200);
		builder.setNextOpMaxRaiseChip(500);
		builder.setNextOpCallChip(300);
		builder.setMaxRoundChip(5000);
		
		builder.setRestActionTimeout(10);
		
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
	public static byte[] end(Table table){
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
