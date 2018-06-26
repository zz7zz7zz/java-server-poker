package com.poker.match.handler;

import com.open.net.client.object.AbstractClient;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;


public class ClientHandler extends AbsClientHandler{
	
	public ClientHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
	}

	@Override
	public void dispatchMessage(AbstractClient client, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {

	}

}
