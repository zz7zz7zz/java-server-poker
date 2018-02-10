package com.poker.login.handler;

import java.util.HashMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.open.net.server.utils.ExceptionUtil;
import com.open.util.log.Logger;
import com.poker.cmd.Cmd;
import com.poker.cmd.LoginCmd;
import com.poker.cmd.UserCmd;
import com.poker.data.DistapchType;
import com.poker.packet.BasePacket;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.packet.PacketInfo;
import com.poker.packet.PacketTransfer;
import com.poker.protocols.LoginServer;
import com.poker.protocols.login.client.RequestLoginProto.RequestLogin;
import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;


public class ClientHandler extends AbsClientHandler{

	public static HashMap<String, Long> uidMap = new HashMap<>();
	public static int uid_auto_generator = 10001;
	
	public ClientHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
	}

	@Override
	public void dispatchMessage(AbstractClient client, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {
    	try {
    		int cmd   = BasePacket.getCmd(data, header_start);
    		Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + Cmd.getCmdString(cmd) + " length " + BasePacket.getLength(data,header_start));
    		
        	if(cmd == LoginCmd.CMD_LOGIN_REQUEST){
        		login(client, data, body_start, body_length, 1, this);
        	}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.v(ExceptionUtil.getStackTraceString(e));
		}
	}
	
	public void login(AbstractClient mClient, byte[] data, int body_start, int body_length, int squenceId,AbstractClientMessageProcessor sender) throws InvalidProtocolBufferException{
		
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());
		int accessId  = mInPacket.readInt();
		long socketId = mInPacket.readLong();
		PacketInfo mSubPacket = mInPacket.readBytesToSubPacket();

		RequestLogin loginRequest = RequestLogin.parseFrom(mSubPacket.buff,mSubPacket.body_start, mSubPacket.body_length);
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
		
		//1.返回给客户端
		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
		byte[] mTempBuff = mInPacket.getPacket();
		
		mOutPacket.begin(squenceId, LoginCmd.CMD_LOGIN_RESPONSE);
		mOutPacket.writeLong(socketId);//额外的数据
		mOutPacket.writeLong(uid);//额外的数据
		//发给客户端的包
		byte[] resp_data = LoginServer.responseLogin(squenceId, uid,"粤B"+uid%100000,"http://img0.bdstatic.com/static/searchresult/img/logo-2X_b99594a.png",1);
		int length = BasePacket.buildClientPacekt(mTempBuff, squenceId, LoginCmd.CMD_LOGIN_RESPONSE, (byte)0, resp_data, 0, resp_data.length);
		mOutPacket.writeBytes(mTempBuff,0,length);
		mOutPacket.end();
		
		length = PacketTransfer.send2Access(accessId,mTempBuff, squenceId, uid, LoginCmd.CMD_LOGIN_RESPONSE, DistapchType.TYPE_P2P, mOutPacket.getPacket(),0,  mOutPacket.getLength());
		send2Dispatch(mTempBuff, 0, length);
		
		//2.检查游戏状态
		mOutPacket.begin(squenceId, UserCmd.CMD_CHECK_GAME_STATUS);
		mOutPacket.writeInt(accessId);//额外的数据
		mOutPacket.end();
		int user_server_id = 0;
		length = PacketTransfer.send2User(user_server_id,mTempBuff, squenceId, uid, UserCmd.CMD_CHECK_GAME_STATUS, DistapchType.TYPE_P2P, mOutPacket.getPacket(),0,  mOutPacket.getLength());
		send2Dispatch(mTempBuff, 0, length);
		
		System.out.println(" socketId " + socketId + " login "+loginRequest.toString());
	}
}
