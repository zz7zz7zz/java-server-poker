package com.poker.protocols.server;

import com.poker.cmd.SystemCmd;
import com.poker.data.DataPacket;

public class ErrorServer {
	
	public static int error(byte[] writeBuff,int squenceId,int err_code,String err_msg){
		
		ErrorProto.Error.Builder builder = ErrorProto.Error.newBuilder();
		builder.setErrCode(err_code);
		if(null != err_msg && err_msg.length() > 0){
			builder.setErrMsg(err_msg);
		}
		byte[] body = builder.build().toByteArray();
		
		return DataPacket.write(writeBuff, squenceId, SystemCmd.CMD_ERR, (byte)0, body,0,body.length);
	}
}
