package com.poker.access.handler;

import com.open.net.server.object.AbstractServerClient;
import com.open.util.log.Logger;
import com.poker.access.Main;
import com.poker.access.object.User;
import com.poker.base.ServerIds;
import com.poker.cmd.DispatchCmd;

import com.poker.data.DataPacket;
import com.poker.data.DistapchType;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.packet.PacketTransfer;

public class ServerHandler extends AbsServerHandler{

	public ServerHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
	}

	public void dispatchMessage(AbstractServerClient client ,byte[] data,int header_start,int header_length,int body_start,int body_length){
		int cmd   = DataPacket.getCmd(data, header_start);
		int server = cmd >> 16;
      	int squenceId = DataPacket.getSequenceId(data,header_start);
      	
      	Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + DispatchCmd.getCmdString(cmd) + " length " + DataPacket.getLength(data,header_start));
      	
      	//如果Server大于0，则将数据转发至对应的server
      	if(server > 0){
      		int length = 0;
      		if(server == ServerIds.SERVER_LOGIN){
      			
      			User user = (User)client.getAttachment();
      			long uid = null != user ? user.uid : 0;
      			
      			mOutPacket.begin(squenceId, cmd);
      			mOutPacket.writeInt(Main.libArgsConfig.id);//AccessId
      			mOutPacket.writeLong(client.mClientId);//socketId
      			mOutPacket.writeBytes(data,header_start,header_length+body_length);//原始数据
      			mOutPacket.end();
      			
      			//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
      			byte[] mTempBuff = mInPacket.getPacket();
      			length = PacketTransfer.send2Login(0,mTempBuff, squenceId, uid,cmd,DistapchType.TYPE_P2P,mOutPacket.getPacket(),0,  mOutPacket.getLength());
          		send2Dispatch(mTempBuff,0,length);
          		
      		}else if(server == ServerIds.SERVER_USER){
      			User user = (User)client.getAttachment();
      			long uid = null != user ? user.uid : 0;
      			
      			mOutPacket.begin(squenceId, cmd);
      			mOutPacket.writeInt(Main.libArgsConfig.id);//AccessId
      			mOutPacket.writeBytes(data,header_start,header_length+body_length);//原始数据
      			mOutPacket.end();
      			
      			//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
      			byte[] mTempBuff = mInPacket.getPacket();
      			length = PacketTransfer.send2User(0,mTempBuff, squenceId, uid,cmd,DistapchType.TYPE_P2P,mOutPacket.getPacket(),0,  mOutPacket.getLength());
          		send2Dispatch(mTempBuff,0,length);
      		}
      		

      	}
	}
}
