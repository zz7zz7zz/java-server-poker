package com.poker.server;

import com.open.net.client.object.AbstractClientMessageProcessor;
import com.poker.cmd.AllocatorCmd;
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.games.Room;
import com.poker.games.Table;
import com.poker.protocols.server.RoomInfoProto;
import com.poker.protocols.server.TableInfoProto;

public class TabelMgr {
	
	static boolean isReported = false;
	public static int reportRoomInfo(byte[] write_buff_dispatcher,byte[] write_buf, int squenceId,AbstractClientMessageProcessor sender,Config config){
		if(!isReported){
			isReported = true;
			RoomInfoProto.RoomInfo.Builder builder = RoomInfoProto.RoomInfo.newBuilder();
			builder.setTableCount(config.table_count);
			builder.setTableMaxUser(config.table_max_user);
			
			byte[] body = builder.build().toByteArray();
			int length = DataPacket.write(write_buf, squenceId, AllocatorCmd.CMD_REPORT_ROOMINFO, (byte)0, (byte)0, (short)0, body,0,body.length);
			
			return ImplDataTransfer.send2Allocator(write_buff_dispatcher, squenceId, write_buf, 0, length);
		}
		return 0;
	}
	
	public static int getRoomInfo(byte[] write_buff_dispatcher,byte[] write_buf, int squenceId,AbstractClientMessageProcessor sender,Config config,Room mRoom){
		
		RoomInfoProto.RoomInfo.Builder builder = RoomInfoProto.RoomInfo.newBuilder();
		builder.setTableCount(config.table_count);
		builder.setTableMaxUser(config.table_max_user);
		
		for (Table table : mRoom.mTables) {
			TableInfoProto.TableInfo.Builder tableBuilder = TableInfoProto.TableInfo.newBuilder();
			tableBuilder.setTid(table.tableId);
			tableBuilder.setCount(table.count);
			builder.addTableInfoList(tableBuilder);
		}
		
		byte[] body = builder.build().toByteArray();
		int length = DataPacket.write(write_buf, squenceId, AllocatorCmd.CMD_REPORT_ROOMINFO, (byte)0, (byte)0, (short)0, body,0,body.length);
		
		return ImplDataTransfer.send2Allocator(write_buff_dispatcher, squenceId, write_buf, 0, length);
	}
	
	public static int updateRoomInfo(byte[] write_buff_dispatcher,byte[] write_buf, int squenceId,AbstractClientMessageProcessor sender,Config config,Table table){
		
		RoomInfoProto.RoomInfo.Builder builder = RoomInfoProto.RoomInfo.newBuilder();
		builder.setTableCount(config.table_count);
		builder.setTableMaxUser(config.table_max_user);
		
		TableInfoProto.TableInfo.Builder tableBuilder = TableInfoProto.TableInfo.newBuilder();
		tableBuilder.setTid(table.tableId);
		tableBuilder.setCount(table.count);
		builder.addTableInfoList(tableBuilder);
		
		byte[] body = builder.build().toByteArray();
		int length = DataPacket.write(write_buf, squenceId, AllocatorCmd.CMD_REPORT_ROOMINFO, (byte)0, (byte)0, (short)0, body,0,body.length);
		
		return ImplDataTransfer.send2Allocator(write_buff_dispatcher, squenceId, write_buf, 0, length);
	}
}
