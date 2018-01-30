package com.poker.user.handler;


import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.open.util.log.Logger;
import com.poker.access.object.User;
import com.poker.cmd.AllocatorCmd;
import com.poker.cmd.LoginCmd;
import com.poker.cmd.UserCmd;
import com.poker.common.packet.PacketTransfer;
import com.poker.data.DataPacket;
import com.poker.data.DistapchType;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;
import com.poker.user.Main;


public class ClientHandler extends AbsClientHandler{


	
	public ClientHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void dispatchMessage(AbstractClient client, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {
		try {
    		int cmd   = DataPacket.getCmd(data, header_start);
    		Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + LoginCmd.getCmdString(cmd) + " length " + DataPacket.getLength(data,header_start));
        	if(cmd == UserCmd.CMD_LOGIN_GAME){
        		
        		login_game(client, data, header_start,header_length,body_start, body_length, 1, this);
        	}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	
	public void login_game(AbstractClient mClient ,byte[] data, int header_start, int header_length, int body_start, int body_length, int squenceId,AbstractClientMessageProcessor sender) throws InvalidProtocolBufferException{
		
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		long uid = mDispatchPacket.getDispatchChainList(0).getUid();
		
		mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());
		int accessId = mInPacket.readInt();
		
		int gameId = 0;
		int matchId = 0;
		int tableId = 0;
		User user = Main.userMap.get(uid);
		if(null != user){
			gameId = user.gameId;
			matchId = user.matchId;
			tableId = user.tableId;
		}
		
		mOutPacket.begin(squenceId, AllocatorCmd.CMD_LOGIN_GAME);
		mOutPacket.writeInt(accessId);//AccessId
		mOutPacket.writeInt(gameId);//gameId
		mOutPacket.writeInt(matchId);//matchId
		mOutPacket.writeInt(tableId);//tableId
		mOutPacket.end();
		
		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
		byte[] mTempBuff = mInPacket.getPacket();
		int length = PacketTransfer.send2User(mTempBuff, squenceId, uid,AllocatorCmd.CMD_LOGIN_GAME,DistapchType.TYPE_P2P,mOutPacket.getPacket(),0,  mOutPacket.getLength());
  		send2Dispatch(mTempBuff,0,length);		
	}
}
