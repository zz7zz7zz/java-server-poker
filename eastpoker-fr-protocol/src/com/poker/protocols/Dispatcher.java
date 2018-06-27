package com.poker.protocols;

import com.poker.base.cmd.DispatchCmd;
import com.poker.base.packet.BasePacket;
import com.poker.protocols.server.ServerProto;

public class Dispatcher {
	
	public static int register2Dispatcher(byte[] writeBuff,int type ,String name, int id, String host ,int port){
		return register2Dispatcher(writeBuff,type,name,id,host,port,-1,-1);
	}
	
	public static int register2Dispatcher(byte[] writeBuff,int type ,String name, int id, String host ,int port,int game_group,int match_group){
		
		ServerProto.Server.Builder builder = ServerProto.Server.newBuilder();
		builder.setType(type);
		builder.setName(name);
		builder.setId(id);
		builder.setHost(host);
		builder.setPort(port);
		if(-1 != game_group){
			builder.setGameGroup(game_group);
		}
		if(-1 != match_group){
			builder.setMatchGroup(match_group);
		}
		byte[] body = builder.build().toByteArray();
		return BasePacket.buildServerPacket(writeBuff, 1, DispatchCmd.CMD_DISPATCH_REGISTER, body, 0, body.length);
	}
	
	public static int unregister2Dispatcher(byte[] writeBuff,int type ,String name, int id, String host ,int port){
		return unregister2Dispatcher(writeBuff,type,name,id,host,port,-1,-1);
	}
	
	public static int unregister2Dispatcher(byte[] writeBuff,int type ,String name, int id, String host ,int port,int game_group,int match_group){
		
		ServerProto.Server.Builder builder = ServerProto.Server.newBuilder();
		builder.setType(type);
		builder.setName(name);
		builder.setId(id);
		builder.setHost(host);
		builder.setPort(port);
		if(-1 != game_group){
			builder.setGameGroup(game_group);
		}
		if(-1 != match_group){
			builder.setMatchGroup(match_group);
		}
		
		byte[] body = builder.build().toByteArray();
		return BasePacket.buildServerPacket(writeBuff, 2, DispatchCmd.CMD_DISPATCH_UNREGISTER, body, 0, body.length);
	}
}
