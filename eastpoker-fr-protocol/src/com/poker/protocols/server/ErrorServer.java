package com.poker.protocols.server;


public class ErrorServer {
	
	public static byte[] error(int err_code,String err_msg){
		
		ErrorProto.Error.Builder builder = ErrorProto.Error.newBuilder();
		builder.setErrCode(err_code);
		if(null != err_msg && err_msg.length() > 0){
			builder.setErrMsg(err_msg);
		}
		byte[] body = builder.build().toByteArray();
		return body;
	}
}
