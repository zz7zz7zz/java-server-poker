package com.poker.game.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.open.util.log.Logger;
import com.poker.cmd.AllocatorCmd;
import com.poker.cmd.DispatchCmd;
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.data.DataTransfer;
import com.poker.data.DistapchType;
import com.poker.game.Main;
import com.poker.games.Room;
import com.poker.games.Table;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.protocols.game.GameServerProto;
import com.poker.protocols.game.GameTableProto;

public class ClientHandler extends AbsClientHandler{
	
	public ClientHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void dispatchMessage(AbstractClient client, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {
		try {
     		int cmd = DataPacket.getCmd(data, header_start);
    		Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + DispatchCmd.getCmdString(cmd) + " length " + DataPacket.getLength(data,header_start));
    		
    		//先判断游戏外逻辑，再判断游戏内逻辑
        	if(cmd == AllocatorCmd.CMD_ALLOCATOR_BROADCAST_GET_ROOMINFO){
        		on_get_roominfo(client,1,this,Main.mServerConfig,Main.mRoom);
        	}else{
        		Main.mRoom.dispatchRoomMessage(cmd, data, header_start, header_length, body_start, body_length);
        	}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	
	boolean isReported = false;
	public int report_roominfo(AbstractClient client,int squenceId,AbstractClientMessageProcessor sender,Config config){
		if(!isReported){
			isReported = true;
			GameServerProto.GameServer.Builder builder = GameServerProto.GameServer.newBuilder();
			builder.setServerId(config.server_id);
			builder.setGameId(config.game_id);
			builder.setGameLevel(config.game_level);
			builder.setTableCount(config.table_count);
			builder.setTableMaxUser(config.table_max_user);
			
			byte[] body = builder.build().toByteArray();

			int dst_server_id = Main.mServerConfig.game_id;
			int dispatch_type = DistapchType.TYPE_P2P;
			int length = DataTransfer.send2Allocator(mOutPacket.getPacket(),squenceId,0,AllocatorCmd.CMD_GAMESERVER_TO_ALLOCATOR_REPORT_ROOMINFO,dispatch_type, body,0,body.length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,-1,-1);
			sender.send(client, mOutPacket.getPacket(), 0, length);
			return 1;
		}
		return 0;
	}
	
	public int on_get_roominfo(AbstractClient client, int squenceId,AbstractClientMessageProcessor sender,Config config,Room mRoom) throws InvalidProtocolBufferException{
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
		
		int dst_server_id = Main.mServerConfig.game_id;
		int dispatch_type = DistapchType.TYPE_P2P;
		int length = DataTransfer.send2Allocator(mOutPacket.getPacket(),squenceId,0,AllocatorCmd.CMD_ALLOCATOR_BROADCAST_GET_ROOMINFO,dispatch_type, body,0,body.length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,-1,-1);
		sender.send(client, mOutPacket.getPacket(), 0, length);
		return 1;
	}


}
