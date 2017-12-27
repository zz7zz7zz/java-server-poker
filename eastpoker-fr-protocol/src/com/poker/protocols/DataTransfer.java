
package com.poker.protocols;

import com.google.protobuf.ByteString;
import com.poker.base.Server;
import com.poker.cmd.DispatchCmd;
import com.poker.cmd.MonitorCmd;
import com.poker.data.DataPacket;
import com.poker.protocols.server.DispatchChainProto;
import com.poker.protocols.server.DispatchPacketProto;
import com.poker.protocols.server.ServerInfoProto;

public final class DataTransfer {
	
	public static byte[] BUFF = new byte[16*1024];
	
	public static int server_type;
	public static int server_id;
	
	public static void init(int server_type , int server_id){
		DataTransfer.server_type = server_type;
		DataTransfer.server_id   = server_id;
	}
	
	public static byte[] send2Dispatcher(int squenceId, int cmd , byte[] data){
		return DataPacket.build(BUFF, squenceId, cmd, (byte)0, (byte)0, (short)0, data);
	}
	
	public static byte[] send2Access(int squenceId, int cmd , byte[] data, int dst_server_id){
		return send2Dispatcher(squenceId, cmd, data, DataTransfer.server_type, DataTransfer.server_id, Server.SERVER_ACCESS, dst_server_id);
	}
	
	public static byte[] send2Login(int squenceId, int cmd , byte[] data, int dst_server_id){
		return send2Dispatcher(squenceId, cmd, data, DataTransfer.server_type, DataTransfer.server_id, Server.SERVER_LOGIN, dst_server_id);
	}
	
	public static byte[] send2User(int squenceId, int cmd , byte[] data, int dst_server_id){
		return send2Dispatcher(squenceId, cmd, data, DataTransfer.server_type, DataTransfer.server_id, Server.SERVER_USER, dst_server_id);
	}
	
	public static byte[] send2Allocator(int squenceId, int cmd , byte[] data, int dst_server_id){
		return send2Dispatcher(squenceId, cmd, data, DataTransfer.server_type, DataTransfer.server_id, Server.SERVER_ALLOCATOR, dst_server_id);
	}
	
	public static byte[] send2Gamer(int squenceId, int cmd , byte[] data, int dst_server_id){
		return send2Dispatcher(squenceId, cmd, data, DataTransfer.server_type, DataTransfer.server_id, Server.SERVER_GAME, dst_server_id);
	}
	
	public static byte[] send2GoldCoin(int squenceId, int cmd , byte[] data, int dst_server_id){
		return send2Dispatcher(squenceId, cmd, data, DataTransfer.server_type, DataTransfer.server_id, Server.SERVER_GOLDCOIN, dst_server_id);
	}
	
	public static byte[] send2Dispatcher(int squenceId, int cmd , byte[] data, int src_server_type , int src_server_id ,int dst_server_type , int dst_server_id){
		
		//流水id
		DispatchPacketProto.DispatchPacket.Builder builder = DispatchPacketProto.DispatchPacket.newBuilder();
		builder.setSequenceId(squenceId);
		
		//调用链
		DispatchChainProto.DispatchChain.Builder chainBuilder = DispatchChainProto.DispatchChain.newBuilder();
		chainBuilder.setSrcServerType(src_server_type);
		chainBuilder.setSrcServerId(src_server_id);
		chainBuilder.setDstServerType(dst_server_type);
		chainBuilder.setDstServerId(dst_server_id);
		builder.addDispatchChainList(chainBuilder);
		
		//转发的数据
		builder.setData(ByteString.copyFrom(data));
		
		DispatchPacketProto.DispatchPacket dispatchPacket = builder.build();
		byte[] body = dispatchPacket.toByteArray();
		
		return DataPacket.build(BUFF, squenceId, cmd, (byte)0, (byte)0, (short)0, body);
	}
	
	//--------------------------------------------------------------------------------------
	public static byte[] register2Dispatcher(int type ,String name, int id, String host ,int port){
		
		ServerInfoProto.ServerInfo.Builder builder = ServerInfoProto.ServerInfo.newBuilder();
		builder.setType(type);
		builder.setName(name);
		builder.setId(id);
		builder.setHost(host);
		builder.setPort(port);
		
		ServerInfoProto.ServerInfo obj = builder.build();
		byte[] buff = DataTransfer.send2Dispatcher(1, DispatchCmd.CMD_REGISTER,obj.toByteArray());
		return buff;
	}
	
	public static byte[] register2Monitor(int type ,String name, int id, String host ,int port){
		
		ServerInfoProto.ServerInfo.Builder builder = ServerInfoProto.ServerInfo.newBuilder();
		builder.setType(type);
		builder.setName(name);
		builder.setId(id);
		builder.setHost(host);
		builder.setPort(port);
		
		ServerInfoProto.ServerInfo obj = builder.build();
		byte[] buff = DataTransfer.send2Dispatcher(1, MonitorCmd.CMD_SERVER_ENTER,obj.toByteArray());
		return buff;
	}
	
	public static byte[] unregister2Monitor(int type ,String name, int id, String host ,int port){
		
		ServerInfoProto.ServerInfo.Builder builder = ServerInfoProto.ServerInfo.newBuilder();
		builder.setType(type);
		builder.setName(name);
		builder.setId(id);
		builder.setHost(host);
		builder.setPort(port);
		
		ServerInfoProto.ServerInfo obj = builder.build();
		byte[] buff = DataTransfer.send2Dispatcher(1, MonitorCmd.CMD_SERVER_EXIT,obj.toByteArray());
		return buff;
	}
}