package com.poker.protocols;

import com.poker.protocols.texaspoker.TexasGameActionRequestProto.TexasGameActionRequest;
import com.poker.protocols.texaspoker.TexasGameBroadcastUserActionProto.TexasGameBroadcastUserAction.Operate;

public final class TexasGameClient {
	
	//用户执行操作
	public final static byte[] action(Operate operate,long chip){
		TexasGameActionRequest.Builder builder = TexasGameActionRequest.newBuilder();
		builder.setOperate(operate);
		builder.setChip(chip);
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
}
