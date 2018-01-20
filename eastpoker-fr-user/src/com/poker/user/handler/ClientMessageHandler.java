package com.poker.user.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.poker.cmd.AllocatorCmd;
import com.poker.data.DataPacket;
import com.poker.user.handler.ImplDataTransfer;


public class ClientMessageHandler {

	
	public void login_game(AbstractClient mClient ,byte[] write_buff_dispatcher,byte[] write_buf, byte[] data, int body_start, int body_length, int squenceId,AbstractClientMessageProcessor sender) throws InvalidProtocolBufferException{
		
		int length = DataPacket.write(write_buf, squenceId, AllocatorCmd.CMD_LOGIN_GAME, (byte)0, data,0,0);
		
		length =  ImplDataTransfer.send2Allocator(write_buff_dispatcher, squenceId, write_buf, 0, length);
		sender.send(mClient, write_buff_dispatcher, 0, length);
	}
}
