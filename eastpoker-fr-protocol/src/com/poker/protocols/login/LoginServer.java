package com.poker.protocols.login;

import com.poker.cmd.LoginCmd;
import com.poker.data.DataPacket;

public class LoginServer {
	public static int login_response(byte[] writeBuff,int squenceId,int uid){
		
		LoginResponseProto.LoginResponse.Builder builder = LoginResponseProto.LoginResponse.newBuilder();
		builder.setUid(uid);
	
		byte[] body = builder.build().toByteArray();
		
		return DataPacket.write(writeBuff, squenceId, LoginCmd.CMD_LOGIN_RESPONSE, (byte)0, (byte)0, (short)0, body,0,body.length);
	}
}
