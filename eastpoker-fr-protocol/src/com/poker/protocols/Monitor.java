package com.poker.protocols;

import com.poker.cmd.MonitorCmd;
import com.poker.data.DataPacket;
import com.poker.protocols.server.ServerInfoProto;

public class Monitor {
	
	public static int register2Monitor(byte[] writeBuff,int type ,String name, int id, String host ,int port){
		
		ServerInfoProto.ServerInfo.Builder builder = ServerInfoProto.ServerInfo.newBuilder();
		builder.setType(type);
		builder.setName(name);
		builder.setId(id);
		builder.setHost(host);
		builder.setPort(port);
		
		byte[] body = builder.build().toByteArray();
		
		return DataPacket.write(writeBuff, 2, MonitorCmd.CMD_REGISTER, (byte)0, (byte)0, (short)0, body,0,body.length);
	}
	
	public static int unregister2Monitor(byte[] writeBuff,int type ,String name, int id, String host ,int port){

		ServerInfoProto.ServerInfo.Builder builder = ServerInfoProto.ServerInfo.newBuilder();
		builder.setType(type);
		builder.setName(name);
		builder.setId(id);
		builder.setHost(host);
		builder.setPort(port);

		byte[] body = builder.build().toByteArray();
		
		return DataPacket.write(writeBuff, 2, MonitorCmd.CMD_UNREGISTER, (byte)0, (byte)0, (short)0, body,0,body.length);
	}
}
