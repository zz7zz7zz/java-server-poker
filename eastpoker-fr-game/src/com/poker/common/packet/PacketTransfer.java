package com.poker.common.packet;

import com.google.protobuf.ByteString;
import com.poker.base.ServerIds;
import com.poker.packet.BasePacket;
import com.poker.protocols.server.DispatchChainProto;
import com.poker.protocols.server.DispatchPacketProto;

public class PacketTransfer{
	
	public static int SERVER_ID = 0;
	public static int SERVER_TYPE;
	
	public static void init(int server_type,int server_id){
		SERVER_TYPE = server_type;
		SERVER_ID = server_id;
	}
	
	public static int send2Access(int dst_server_id ,byte[] writeBuff,int squenceId,long uid, int cmd ,int dispatch_type, byte[] data, int offset ,int length){
		return send2Dispatcher(writeBuff,squenceId, uid,cmd,dispatch_type, data, offset, length, SERVER_TYPE, SERVER_ID, ServerIds.SERVER_ACCESS, dst_server_id,-1,-1);
	}
	
	public static int send2Login(byte[] writeBuff,int squenceId,long uid, int cmd ,int dispatch_type, byte[] data, int offset ,int length){
		int dst_server_id = 0;
		return send2Dispatcher(writeBuff,squenceId, uid,cmd,dispatch_type, data, offset, length, SERVER_TYPE, SERVER_ID, ServerIds.SERVER_LOGIN, dst_server_id,-1,-1);
	}
	
	public static int send2User(byte[] writeBuff,int squenceId ,long uid, int cmd ,int dispatch_type, byte[] data, int offset ,int length){
		int dst_server_id = 0;
		return send2Dispatcher(writeBuff,squenceId, uid,cmd,dispatch_type, data, offset, length, SERVER_TYPE, SERVER_ID, ServerIds.SERVER_USER, dst_server_id,-1,-1);
	}
	
	public static int send2Dispatcher(byte[] writeBuff,int squenceId,long uid, int cmd ,int dispatch_type, byte[] data,int offset,int length, int src_server_type , int src_server_id ,int dst_server_type , int dst_server_id,int gameGroup,int matchGroup){
		
		//流水id
		DispatchPacketProto.DispatchPacket.Builder builder = DispatchPacketProto.DispatchPacket.newBuilder();
		builder.setSequenceId(squenceId);
		
		//调用链
		DispatchChainProto.DispatchChain.Builder chainBuilder = DispatchChainProto.DispatchChain.newBuilder();
		chainBuilder.setSrcServerType(src_server_type);
		chainBuilder.setSrcServerId(src_server_id);
		chainBuilder.setDstServerType(dst_server_type);
		chainBuilder.setDstServerId(dst_server_id);
		
		if(uid !=0){
			chainBuilder.setUid(uid);
		}

		if(cmd !=0){
			chainBuilder.setCmd(cmd);
		}
		
		if(dispatch_type !=0){
			chainBuilder.setDispatchType(dispatch_type);
		}
		
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
		
		return BasePacket.buildServerPacket(writeBuff, squenceId, cmd, body,0,body.length);
	}
}