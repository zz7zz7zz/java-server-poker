package com.poker.access.handler;

import com.open.net.client.impl.tcp.nio.NioClient;
import com.open.net.server.object.AbstractServerClient;
import com.open.util.log.Logger;
import com.poker.access.Main;
import com.poker.base.ServerIds;
import com.poker.cmd.DispatchCmd;
import com.poker.data.DataPacket;
import com.poker.data.DistapchType;
import com.poker.packet.OutPacket;

public class ServerHandler extends AbsServerHandler{

	public ServerHandler(OutPacket mOutPacket, byte[] writeBuffer) {
		super(mOutPacket, writeBuffer);
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
      			
      			mOutPacket.begin(squenceId, cmd);
      			mOutPacket.writeLong(client.mClientId);//额外的数据
      			mOutPacket.writeBytes(data,header_start,header_length+body_length);//原始数据
      			mOutPacket.end();
      			
      			length = ImplDataTransfer.send2Login(write_buff, squenceId, 0,cmd,DistapchType.TYPE_P2P,mOutPacket.getPacket(),0,  mOutPacket.getLength());
      			
      		}else if(server == ServerIds.SERVER_USER){
      			length = ImplDataTransfer.send2User(write_buff, squenceId, 0,cmd,DistapchType.TYPE_P2P,data,body_start,body_length);
      		}
      		
      		Main.dispatchIndex = (Main.dispatchIndex+1) % Main.dispatcher.length;
      		NioClient mNioClient = Main.dispatcher[Main.dispatchIndex];
      		if(mNioClient.isConnected()){
      			mNioClient.getmMessageProcessor().send(mNioClient,write_buff,0,length);
      		}else{
      			for(int i = 0;i<Main.dispatcher.length;i++){
      				mNioClient = Main.dispatcher[Main.dispatchIndex];
              		if(mNioClient.isConnected()){
              			mNioClient.getmMessageProcessor().send(mNioClient,write_buff,0,length);
              			break;
              		}
      			}
      		}
      	}
	}
}
