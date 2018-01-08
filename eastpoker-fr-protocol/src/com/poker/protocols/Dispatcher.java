package com.poker.protocols;

import com.poker.cmd.DispatchCmd;
import com.poker.data.DataPacket;
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
		
		return DataPacket.write(writeBuff, 1, DispatchCmd.CMD_REGISTER, (byte)0, (byte)0, (short)0, body,0,body.length);
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
		
		return DataPacket.write(writeBuff, 1, DispatchCmd.CMD_UNREGISTER, (byte)0, (byte)0, (short)0, body,0,body.length);
	}
}
