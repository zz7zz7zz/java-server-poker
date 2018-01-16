package com.poker.protocols.login;

import com.poker.cmd.LoginCmd;
import com.poker.data.DataPacket;

public class LoginClient {
	
	/**
	 * 用户登录
	 * @param writeBuff
	 * @param squenceId
	 * @param uuid
	 * @param uid
	 * @return
	 */
	public static int login_request(byte[] writeBuff,int squenceId ,String uuid, int uid){
		
		LoginRequestProto.LoginRequest.Builder builder = LoginRequestProto.LoginRequest.newBuilder();
		builder.setUuid(uuid);
		builder.setUid(uid);
	
		byte[] body = builder.build().toByteArray();
		
		return DataPacket.write(writeBuff, squenceId, LoginCmd.CMD_LOGIN_REQUEST, (byte)0, (byte)0, (short)0, body,0,body.length);
	}
}
