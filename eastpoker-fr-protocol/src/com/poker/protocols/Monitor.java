package com.poker.protocols;

import com.poker.base.cmd.CmdMonitor;
import com.poker.base.packet.BasePacket;
import com.poker.protocols.server.ServerProto;

public class Monitor {
	
	public static int register2Monitor(byte[] writeBuff,int type ,String name, int id, String host ,int port){
		
		ServerProto.Server.Builder builder = ServerProto.Server.newBuilder();
		builder.setType(type);
		builder.setName(name);
		builder.setId(id);
		builder.setHost(host);
		builder.setPort(port);
		
		byte[] body = builder.build().toByteArray();
		return BasePacket.buildServerPacket(writeBuff, 1, CmdMonitor.CMD_MONITOR_REGISTER, body, 0, body.length);
	}
	
	public static int unregister2Monitor(byte[] writeBuff,int type ,String name, int id, String host ,int port){

		ServerProto.Server.Builder builder = ServerProto.Server.newBuilder();
		builder.setType(type);
		builder.setName(name);
		builder.setId(id);
		builder.setHost(host);
		builder.setPort(port);

		byte[] body = builder.build().toByteArray();
		return BasePacket.buildServerPacket(writeBuff, 2, CmdMonitor.CMD_MONITOR_UNREGISTER, body, 0, body.length);
	}
}
