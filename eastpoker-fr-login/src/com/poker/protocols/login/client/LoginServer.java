package com.poker.protocols.login.client;

import com.poker.protocols.login.server.ResponseLoginProto.ResponseLogin;

public class LoginServer {
	
	public static byte[] login_response(int squenceId,int uid){	
		ResponseLogin.Builder builder = ResponseLogin.newBuilder();
		builder.setUid(uid);
	
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
}
