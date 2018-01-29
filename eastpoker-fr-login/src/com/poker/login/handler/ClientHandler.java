package com.poker.login.handler;

import java.util.HashMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.open.util.log.Logger;
import com.poker.cmd.LoginCmd;
import com.poker.common.packet.PacketTransfer;
import com.poker.data.DataPacket;
import com.poker.data.DistapchType;
import com.poker.login.Main;
import com.poker.packet.InPacket;
import com.poker.protocols.login.LoginRequestProto;
import com.poker.protocols.login.LoginServer;
import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;


public class ClientHandler extends AbsClientHandler{

	public static HashMap<String, Long> uidMap = new HashMap<>();
	public static int uid_auto_generator = 10000;
	
	public ClientHandler(InPacket mInPacket) {
		super(mInPacket);
	}
	
	@Override
	public void dispatchMessage(AbstractClient client, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {
    	try {
    		int cmd   = DataPacket.getCmd(data, header_start);
    		Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + LoginCmd.getCmdString(cmd) + " length " + DataPacket.getLength(data,header_start));
    		
        	if(cmd == LoginCmd.CMD_LOGIN_REQUEST){
        		login(client, Main.write_buff_dispatcher, Main.write_buff, data, body_start, body_length, 1, this);
        	}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	
	public void login(AbstractClient mClient ,byte[] write_buff_dispatcher,byte[] write_buf, byte[] data, int body_start, int body_length, int squenceId,AbstractClientMessageProcessor sender) throws InvalidProtocolBufferException{
		

		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		mDispatchPacket.getData().copyTo(Main.write_buff, 0);
		
		mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());
		long socketId = mInPacket.readLong();

		//方法一：
//		byte[] binary = mInPacket.readBytes();
		
		//方法二：使用下面的方法代替，避免创建多余的数组，浪费内存
		int[] offset_length_array = mInPacket.readBytesOffsetAndLenth();
		int packet_start = offset_length_array[0];
		int packet_length = offset_length_array[1];
		
		int packet_header_length = DataPacket.getHeaderLength(mInPacket.getPacket(), packet_start);
		
		int packet_body_start = packet_start + packet_header_length;
		int packet_body_length = packet_length - packet_header_length;
		
		Logger.v(" socketId " + socketId);
		
		
		LoginRequestProto.LoginRequest loginRequest = LoginRequestProto.LoginRequest.parseFrom(mInPacket.getPacket(),packet_body_start,packet_body_length);
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
		
		byte[] resp_data = LoginServer.login_response(write_buf, squenceId, (int)uid);
//		int length = ImplDataTransfer.send2Access(write_buff_dispatcher, squenceId, uid, LoginCmd.CMD_LOGIN_RESPONSE, DistapchType.TYPE_P2P, resp_data, 0, resp_data.length);
//		sender.send(mClient, write_buff_dispatcher, 0, length);
		
		
		Main.mOutPacket.begin(squenceId, LoginCmd.CMD_LOGIN_RESPONSE);
		Main.mOutPacket.writeLong(socketId);//额外的数据
		Main.mOutPacket.writeLong(uid);//额外的数据
		
		//发给客户端的包
		int length = DataPacket.write(write_buff_dispatcher, squenceId, LoginCmd.CMD_LOGIN_RESPONSE, (byte)0, resp_data, 0, resp_data.length);
		Main.mOutPacket.writeBytes(write_buff_dispatcher,0,length);
		
		Main.mOutPacket.end();
		
		length = PacketTransfer.send2Access(write_buff_dispatcher, squenceId, uid, LoginCmd.CMD_LOGIN_RESPONSE, DistapchType.TYPE_P2P, Main.mOutPacket.getPacket(),0,  Main.mOutPacket.getLength());
		sender.send(mClient, write_buff_dispatcher, 0, length);
		
	}
}
