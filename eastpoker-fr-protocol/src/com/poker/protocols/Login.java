package com.poker.protocols;

import com.poker.cmd.LoginCmd;
import com.poker.data.DataPacket;
import com.poker.protocols.server.LoginProto;

public class Login {
	
	public static void login(byte[] writeBuff,int squenceId ,String uuid, int uid){
		
		LoginProto.Login.Builder builder = LoginProto.Login.newBuilder();
		builder.setUuid(uuid);
		builder.setUid(uid);
	
		DataPacket.write(writeBuff, squenceId, LoginCmd.CMD_LOGIN, (byte)0, (byte)0, (short)0, builder.build().toByteArray());
	}
}
