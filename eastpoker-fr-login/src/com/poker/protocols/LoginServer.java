package com.poker.protocols;

import com.poker.protocols.login.server.ResponseLoginProto.ResponseLogin;

public class LoginServer {
	
	public static byte[] responseLogin(int squenceId,long uid,String nick_name, String head_portrait,int level){	
		ResponseLogin.Builder builder = ResponseLogin.newBuilder();
		builder.setUid(uid);
		builder.setNickName(nick_name);
		builder.setHeadPortrait(head_portrait);
		builder.setLevel(level);
		byte[] body = builder.build().toByteArray();
		return body;
	}
	
}
