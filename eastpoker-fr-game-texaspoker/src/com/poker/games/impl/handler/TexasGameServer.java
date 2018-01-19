package com.poker.games.impl.handler;


import com.poker.data.DataPacket;
import com.poker.games.impl.config.GameConfig;
import com.poker.protocols.texaspoker.TexasGameStartProto.TexasGameStart;
import com.poker.protocols.texaspoker.TexasGameStartProto.Config;
import com.poker.protocols.texaspoker.TexasGameDealFlopProto.TexasGameDealFlop;
import com.poker.protocols.texaspoker.TexasGameDealPreFlopProto.TexasGameDealPreFlop;
import com.poker.protocols.texaspoker.TexasGameDealRiverProto.TexasGameDealRiver;
import com.poker.protocols.texaspoker.TexasGameDealTurnProto.TexasGameDealTurn;;

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
	
	
}
