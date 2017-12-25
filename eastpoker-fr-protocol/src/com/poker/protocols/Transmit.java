
package com.poker.protocols;

import com.google.protobuf.ByteString;
import com.poker.paser.Packet;
import com.poker.protocols.server.DispatchChainProto;
import com.poker.protocols.server.DispatchPacketProto;

public final class Transmit {
	
	public static Packet getTransmitData(int squenceId, int cmd , byte[] data, int src_server_type , int src_server_id ,int dst_server_type , int dst_server_id){
		
		DispatchPacketProto.DispatchPacket.Builder builder = DispatchPacketProto.DispatchPacket.newBuilder();
		builder.setSequenceId(squenceId);
		builder.setData(ByteString.copyFrom(data));

		DispatchChainProto.DispatchChain.Builder chainBuilder = DispatchChainProto.DispatchChain.newBuilder();
		chainBuilder.setSrcServerType(src_server_type);
		chainBuilder.setSrcServerId(src_server_id);
		chainBuilder.setDstServerType(dst_server_type);
		chainBuilder.setDstServerId(dst_server_id);
		
		builder.addDispatchChainList(chainBuilder);
		
		DispatchPacketProto.DispatchPacket dispatchPacket = builder.build();
		byte[] body = dispatchPacket.toByteArray();
		
		Packet mPacket = new Packet();
		mPacket.body = body;
		
		return mPacket;
	}
	
}