package com.poker.games.impl.handler;


import com.poker.data.DataPacket;
import com.poker.games.Table;
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
import com.poker.protocols.texaspoker.TexasGameShowHandProto.TexasGameShowHand;
import com.poker.protocols.texaspoker.TexasGameShowHandProto.UserCard;

public class TexasGameServer {
	
	public static int start(byte[] writeBuff,int squenceId,int sb_seatid,int bb_seatid,int btn_seateId, GameConfig mGameConfig){
		
		TexasGameStart.Builder builder = TexasGameStart.newBuilder();
		builder.setSbSeatId(sb_seatid);
		builder.setBbSeatId(bb_seatid);
		builder.setBtnSeatId(btn_seateId);
		
		Config.Builder configBuilder = Config.newBuilder();
		configBuilder.setLevel(mGameConfig.level);
		configBuilder.setLevelName(mGameConfig.level_name);
		configBuilder.setMaxUser(mGameConfig.table_max_user);
		configBuilder.setMaxChip(mGameConfig.table_max_chip);
		int size = mGameConfig.table_ante.length;
		for(int i = 0;i<size;i++){
			configBuilder.addAnte(mGameConfig.table_ante[i]);
			configBuilder.addBlind(mGameConfig.table_blind[i]);
			configBuilder.addBlindTime(mGameConfig.table_blind_time[i]);
		}
		
		builder.setConfig(configBuilder);
		
		byte[] body = builder.build().toByteArray();

		return DataPacket.write(writeBuff, squenceId, GCmd.CMD_SERVER_GAME_START, (byte)0, body,0,body.length);
	}
	
	public static int dealPreFlop(byte[] writeBuff,int squenceId,byte[] cards){
		
		TexasGameDealPreFlop.Builder builder = TexasGameDealPreFlop.newBuilder();
		for(int i = 0;i<cards.length;i++){
			builder.addCards(cards[i]);
		}
		
		byte[] body = builder.build().toByteArray();

		return DataPacket.write(writeBuff, squenceId, GCmd.CMD_SERVER_DEAL_PREFLOP, (byte)0, body,0,body.length);
	}
	
	public static int dealFlop(byte[] writeBuff,int squenceId,byte[] cards){
		
		TexasGameDealFlop.Builder builder = TexasGameDealFlop.newBuilder();
		for(int i = 0;i<cards.length;i++){
			builder.addCards(cards[i]);
		}
		
		byte[] body = builder.build().toByteArray();

		return DataPacket.write(writeBuff, squenceId, GCmd.CMD_SERVER_DEAL_FLOP, (byte)0, body,0,body.length);
	}
	
	public static int dealTrun(byte[] writeBuff,int squenceId,byte[] cards){
		
		TexasGameDealTurn.Builder builder = TexasGameDealTurn.newBuilder();
		for(int i = 0;i<cards.length;i++){
			builder.addCards(cards[i]);
		}
		
		byte[] body = builder.build().toByteArray();

		return DataPacket.write(writeBuff, squenceId, GCmd.CMD_SERVER_DEAL_TURN, (byte)0, body,0,body.length);
	}
	
	public static int dealRiver(byte[] writeBuff,int squenceId,byte[] cards){
		
		TexasGameDealRiver.Builder builder = TexasGameDealRiver.newBuilder();
		for(int i = 0;i<cards.length;i++){
			builder.addCards(cards[i]);
		}
		
		byte[] body = builder.build().toByteArray();

		return DataPacket.write(writeBuff, squenceId, GCmd.CMD_SERVER_DEAL_RIVER, (byte)0, body,0,body.length);
	}
	
	public static int broadcastUserAction(byte[] writeBuff,int squenceId,String uid,Table table) {
		
		TexasGameAction.Builder builder = TexasGameAction.newBuilder();
		builder.setSeatId(0);
		builder.setOperate(Operate.CALL);
		builder.setChip(100);
		builder.setRemainingChip(1000);
		builder.setRoundChip(888);
		
		builder.setNextOpSeatId(1);
		builder.setNextOpMinRaiseChip(200);
		builder.setNextOpMaxRaiseChip(500);
		builder.setNextOpCallChip(300);
		builder.setMaxRoundChip(5000);
		
		byte[] body = builder.build().toByteArray();

		return DataPacket.write(writeBuff, squenceId, GCmd.CMD_SERVER_BROADCAST_WHO_ACTION_WAHT, (byte)0, body,0,body.length);
	}
	
	public static int showHand(byte[] writeBuff,int squenceId,String uid,Table table){
		
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
//		
		byte[] body = builder.build().toByteArray();

		return DataPacket.write(writeBuff, squenceId, GCmd.CMD_SERVER_BROADCAST_SHOW_HAND, (byte)0, body,0,body.length);
	}
	
	public static int reconnect(byte[] writeBuff,int squenceId,String uid,Table table){
		TexasGameReconnect.Builder builder = TexasGameReconnect.newBuilder();
		
		byte[] body = builder.build().toByteArray();

		return DataPacket.write(writeBuff, squenceId, GCmd.CMD_SERVER_RECONNECT, (byte)0, body,0,body.length);
	}
	
	public static int end(byte[] writeBuff,int squenceId,String uid,Table table){
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

		return DataPacket.write(writeBuff, squenceId, GCmd.CMD_SERVER_GAME_END, (byte)0, body,0,body.length);
	}
}
