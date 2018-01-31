package com.poker.protocols.login.client;
import com.poker.protocols.login.client.RequestLoginProto.RequestLogin;

public class LoginClient {
	
	/**
	 * 用户登录
	 * @param writeBuff
	 * @param squenceId
	 * @param uuid
	 * @param uid
	 * @return
	 */
	public static byte[] requestLogin(int squenceId ,String uuid, int uid){
		RequestLogin.Builder builder = RequestLogin.newBuilder();
		builder.setUuid(uuid);
		builder.setUid(uid);
		byte[] body = builder.build().toByteArray();
		return body;
	}
}
