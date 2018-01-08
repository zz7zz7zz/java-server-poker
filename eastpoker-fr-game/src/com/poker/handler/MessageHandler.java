package com.poker.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.poker.cmd.AllocatorCmd;
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.games.Room;
import com.poker.games.Table;
import com.poker.protocols.server.GameServerProto;
import com.poker.protocols.server.GameTableProto;
import com.poker.server.ImplDataTransfer;



public class MessageHandler {
	
	boolean isReported = false;
	public int report_roominfo(byte[] write_buff_dispatcher,byte[] write_buf, int squenceId,AbstractClientMessageProcessor sender,Config config){
		if(!isReported){
			isReported = true;
			GameServerProto.GameServer.Builder builder = GameServerProto.GameServer.newBuilder();
			builder.setServerId(config.server_id);
			builder.setGameId(config.game_id);
			builder.setGameLevel(config.game_level);
			builder.setTableCount(config.table_count);
			builder.setTableMaxUser(config.table_max_user);
			
			byte[] body = builder.build().toByteArray();
			int length = DataPacket.write(write_buf, squenceId, AllocatorCmd.CMD_REPORT_ROOMINFO, (byte)0, (byte)0, (short)0, body,0,body.length);
			
			return ImplDataTransfer.send2Allocator(write_buff_dispatcher, squenceId, write_buf, 0, length);
		}
		return 0;
	}
	
	public void on_get_roominfo(byte[] write_buff_dispatcher,byte[] write_buf, int squenceId,AbstractClientMessageProcessor sender,Config config,Room mRoom) throws InvalidProtocolBufferException{
		GameServerProto.GameServer.Builder builder = GameServerProto.GameServer.newBuilder();
		builder.setServerId(config.server_id);
		builder.setGameId(config.game_id);
		builder.setGameLevel(config.game_level);
		builder.setTableCount(config.table_count);
		builder.setTableMaxUser(config.table_max_user);
		
		for (Table table : mRoom.mTables) {
			GameTableProto.GameTable.Builder tableBuilder = GameTableProto.GameTable.newBuilder();
			tableBuilder.setTid(table.tableId);
			tableBuilder.setCount(table.count);
			builder.addTableList(tableBuilder);
		}
		
		byte[] body = builder.build().toByteArray();
		int length = DataPacket.write(write_buf, squenceId, AllocatorCmd.CMD_REPORT_ROOMINFO, (byte)0, (byte)0, (short)0, body,0,body.length);
		
		ImplDataTransfer.send2Allocator(write_buff_dispatcher, squenceId, write_buf, 0, length);
	}

}
