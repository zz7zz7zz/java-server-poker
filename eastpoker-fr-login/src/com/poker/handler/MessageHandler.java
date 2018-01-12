package com.poker.handler;

import java.util.HashMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.message.Message;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.poker.protocols.LoginResponse;
import com.poker.protocols.server.LoginProto;
import com.poker.server.ImplDataTransfer;

public class MessageHandler {

	public static HashMap<String, Long> uidMap = new HashMap<>();
	public static int uid_auto_generator = 10000;
	
	public void login(AbstractClient mClient ,byte[] write_buff_dispatcher,byte[] write_buf, Message msg, int body_start, int body_length, int squenceId,AbstractClientMessageProcessor sender) throws InvalidProtocolBufferException{
		LoginProto.Login loginRequest = LoginProto.Login.parseFrom(msg.data,body_start,body_length);
		System.out.println("login "+loginRequest.toString());
		
		String uuid = loginRequest.getUuid();
		long uid = 0;
		Long uidObject = uidMap.get(uuid);
		if(null == uidObject){
			uid = uid_auto_generator;
			uidMap.put(uuid, uid);
			
			uid_auto_generator++;
		}else{
			uid = uidObject;
		}
		
		int length = LoginResponse.login(write_buf, squenceId, (int)uid);
		length = ImplDataTransfer.send2Access(write_buff_dispatcher, squenceId, write_buf, 0, length);
		sender.send(mClient, write_buff_dispatcher,0,length);
	}
}
