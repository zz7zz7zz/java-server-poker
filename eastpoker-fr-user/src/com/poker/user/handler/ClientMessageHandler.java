package com.poker.user.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.poker.cmd.AllocatorCmd;
import com.poker.data.DataTransfer;
import com.poker.data.DistapchType;
import com.poker.user.Main;


public class ClientMessageHandler {

	
	public void login_game(AbstractClient mClient ,byte[] write_buff_dispatcher,byte[] write_buf, byte[] data, int body_start, int body_length, int squenceId,AbstractClientMessageProcessor sender) throws InvalidProtocolBufferException{
		
		byte[] body = write_buf;
		
		int dst_server_id =500;
		int dispatch_type = DistapchType.TYPE_P2P;
		int length = DataTransfer.send2Allocator(write_buff_dispatcher,squenceId,0,AllocatorCmd.CMD_LOGIN_GAME,dispatch_type, body,0,0, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,-1,-1);
		sender.send(mClient, write_buff_dispatcher, 0, length);
		
	}
}
