package com.poker.protocols.login;


public class LoginServer {
	public static byte[] login_response(int squenceId,int uid){
		
		LoginResponseProto.LoginResponse.Builder builder = LoginResponseProto.LoginResponse.newBuilder();
		builder.setUid(uid);
	
		byte[] body = builder.build().toByteArray();
		
		return body;
	}
}
