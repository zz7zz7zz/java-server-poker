
package com.poker.data;

import com.google.protobuf.ByteString;
import com.poker.base.ServerIds;
import com.poker.cmd.DispatchCmd;
import com.poker.protocols.server.DispatchChainProto;
import com.poker.protocols.server.DispatchPacketProto;

public final class DataTransfer {

	//---------------------------------------------------------------------------------------------------
	public static int send2Access(byte[] writeBuff,int squenceId, byte[] data, int offset , int length,int src_server_type , int src_server_id , int dst_server_id,int gameGroup,int matchGroup){
		return send2Dispatcher(writeBuff,squenceId, DispatchCmd.CMD_DISPATCH_DATA, data, offset, length, src_server_type, src_server_id, ServerIds.SERVER_ACCESS, dst_server_id,gameGroup,matchGroup);
	}
	
	public static int send2Login(byte[] writeBuff,int squenceId, byte[] data, int offset , int length,int src_server_type , int src_server_id , int dst_server_id,int gameGroup,int matchGroup){
		return send2Dispatcher(writeBuff,squenceId, DispatchCmd.CMD_DISPATCH_DATA, data, offset, length, src_server_type, src_server_id, ServerIds.SERVER_LOGIN, dst_server_id,gameGroup,matchGroup);
	}
	
	public static int send2User(byte[] writeBuff,int squenceId,  byte[] data, int offset , int length,int src_server_type , int src_server_id , int dst_server_id,int gameGroup,int matchGroup){
		return send2Dispatcher(writeBuff,squenceId, DispatchCmd.CMD_DISPATCH_DATA, data, offset, length,  src_server_type, src_server_id, ServerIds.SERVER_USER, dst_server_id,gameGroup,matchGroup);
	}
	
	public static int send2Allocator(byte[] writeBuff,int squenceId, byte[] data, int offset , int length,int src_server_type , int src_server_id , int dst_server_id,int gameGroup,int matchGroup){
		return send2Dispatcher(writeBuff,squenceId, DispatchCmd.CMD_DISPATCH_DATA, data, offset, length,  src_server_type, src_server_id, ServerIds.SERVER_ALLOCATOR, dst_server_id,gameGroup,matchGroup);
	}
	
	public static int send2Gamer(byte[] writeBuff,int squenceId, byte[] data, int offset , int length,int src_server_type , int src_server_id , int dst_server_id,int gameGroup,int matchGroup){
		return send2Dispatcher(writeBuff,squenceId, DispatchCmd.CMD_DISPATCH_DATA, data, offset, length,  src_server_type, src_server_id, ServerIds.SERVER_GAME, dst_server_id,gameGroup,matchGroup);
	}
	
	public static int send2GoldCoin(byte[] writeBuff,int squenceId, byte[] data, int offset , int length,int src_server_type , int src_server_id , int dst_server_id,int gameGroup,int matchGroup){
		return send2Dispatcher(writeBuff,squenceId, DispatchCmd.CMD_DISPATCH_DATA, data, offset, length,  src_server_type, src_server_id, ServerIds.SERVER_GOLDCOIN, dst_server_id,gameGroup,matchGroup);
	}
	
	//---------------------------------------------------------------------------------------------------
	//转发
	public static int send2Dispatcher(byte[] writeBuff,int squenceId, int cmd , byte[] data,int offset,int length, int src_server_type , int src_server_id ,int dst_server_type , int dst_server_id,int gameGroup,int matchGroup){
		
		//流水id
		DispatchPacketProto.DispatchPacket.Builder builder = DispatchPacketProto.DispatchPacket.newBuilder();
		builder.setSequenceId(squenceId);
		
		//调用链
		DispatchChainProto.DispatchChain.Builder chainBuilder = DispatchChainProto.DispatchChain.newBuilder();
		chainBuilder.setSrcServerType(src_server_type);
		chainBuilder.setSrcServerId(src_server_id);
		chainBuilder.setDstServerType(dst_server_type);
		chainBuilder.setDstServerId(dst_server_id);
		if(-1 != gameGroup){
			chainBuilder.setDstGameGroup(gameGroup);
		}
		if(-1 != matchGroup){
			chainBuilder.setDstMatchGroup(matchGroup);
		}
		builder.addDispatchChainList(chainBuilder);
		
		//转发的数据
		builder.setData(ByteString.copyFrom(data,offset,length));
		
		DispatchPacketProto.DispatchPacket dispatchPacket = builder.build();
		byte[] body = dispatchPacket.toByteArray();
		
		return DataPacket.write(writeBuff, squenceId, cmd, (byte)0, body,0,body.length);
	}

	//---------------------------------------------------------------------------------------------------
	//直发
	public static int send2Client(byte[] writeBuff,int squenceId, int cmd , byte[] data, int offset , int length){
		return DataPacket.write(writeBuff, squenceId, cmd, (byte)0, data,offset,length);
	}
	
}