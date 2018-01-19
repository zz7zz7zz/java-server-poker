package com.poker.games.impl.handler;


import com.poker.data.DataPacket;
import com.poker.games.impl.config.GameConfig;
import com.poker.protocols.texaspoker.TexasGameStartProto.TexasGameStart;
import com.poker.protocols.texaspoker.TexasGameStartProto.Config;;

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
	
}
