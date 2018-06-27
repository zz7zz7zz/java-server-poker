package com.poker.access.handler;

import com.open.net.server.object.AbstractServerClient;
import com.open.util.log.Logger;
import com.poker.access.Main;
import com.poker.access.object.User;
import com.poker.access.object.UserPool;
import com.poker.base.cmd.BaseGameCmd;
import com.poker.base.cmd.Cmd;
import com.poker.base.cmd.LoginCmd;
import com.poker.base.cmd.SystemCmd;
import com.poker.base.packet.BasePacket;
import com.poker.base.packet.InPacket;
import com.poker.base.packet.OutPacket;
import com.poker.base.packet.PacketTransfer;
import com.poker.base.type.TDistapch;
import com.poker.base.type.TServer;
import com.poker.protocols.server.SCConfigProto.SCConfig;

public class ServerHandler extends AbsServerHandler{

	public ServerHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
	}

	@Override
	public void onClientEnter(AbstractServerClient client) {
		super.onClientEnter(client);
		
		
		//下发服务器相关配置，比如心跳包
		SCConfig.Builder builder = SCConfig.newBuilder();
		builder.setHeatBeatInterval(10000);
		byte[] body = builder.build().toByteArray();

		byte[] mTempBuff = mInPacket.getPacket();
		int length 		= BasePacket.buildClientPacekt(mTempBuff, 0, SystemCmd.CMD_SYS_SERVER_CONFIG, (byte)0,body,0,body.length);
        Main.mServerHandler.unicast(client, mTempBuff,0,length);
        
	}


	@Override
	public void onClientExit(AbstractServerClient client) {

		User attachUser = (User)client.getAttachment();
        if(null != attachUser){
        	//掉线
        	if(attachUser.gameId>0){
        		int squenceId = 0;
        		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
        		byte[] mTempBuff = mInPacket.getPacket();
        		int length = PacketTransfer.send2Game(attachUser.gameId, attachUser.gameSid, mTempBuff, squenceId, attachUser.uid, BaseGameCmd.CMD_CLIENT_OFFLINE, TDistapch.TYPE_P2P, mOutPacket.getPacket(),0,  0);
        		send2Dispatch(mTempBuff,0,length);	
        	}
        	
        	//删除链接
        	Main.userMap.remove(attachUser.uid);
        	UserPool.release(attachUser);
        }
        
		super.onClientExit(client);
	}

	public void dispatchMessage(AbstractServerClient client ,byte[] data,int header_start,int header_length,int body_start,int body_length){
		int cmd   = BasePacket.getCmd(data, header_start);
      	Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + Cmd.getCmdString(cmd) + " length " + BasePacket.getLength(data,header_start));
      	
		User user = (User)client.getAttachment();
		if(cmd != LoginCmd.CMD_LOGIN_REQUEST && null == user){//非登录指令/非心跳指令，一律必需先登录
			Logger.v("please login frist ");
			return;
		}	
		
		int server = cmd >> 16;
      	int squenceId = BasePacket.getSequenceId(data,header_start);
      	//如果Server大于0，则将数据转发至对应的server
      	if(server > 0){
      		int length = 0;
      		if(server == TServer.SERVER_LOGIN){
      			
      			long uid = null != user ? user.uid : 0;
      			
      			mOutPacket.begin(squenceId, cmd);
      			mOutPacket.writeInt(Main.libArgsConfig.id);//AccessId
      			mOutPacket.writeLong(client.mClientId);//socketId
      			mOutPacket.writeBytes(data,header_start,header_length+body_length);//原始数据
      			mOutPacket.end();
      			
      			//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
      			byte[] mTempBuff = mInPacket.getPacket();
      			length = PacketTransfer.send2Login(0,mTempBuff, squenceId, uid,cmd,TDistapch.TYPE_P2P,mOutPacket.getPacket(),0,  mOutPacket.getLength());
          		send2Dispatch(mTempBuff,0,length);
          		
      		}else if(server == TServer.SERVER_USER){

      			mOutPacket.begin(squenceId, cmd);
      			mOutPacket.writeInt(Main.libArgsConfig.id);//AccessId
      			mOutPacket.writeBytes(data,header_start,header_length+body_length);//原始数据
      			mOutPacket.end();
      			
      			//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
      			byte[] mTempBuff = mInPacket.getPacket();
      			length = PacketTransfer.send2User(0,mTempBuff, squenceId, user.uid,cmd,TDistapch.TYPE_P2P,mOutPacket.getPacket(),0,  mOutPacket.getLength());
          		send2Dispatch(mTempBuff,0,length);
      		}else if(server == TServer.SERVER_MATCH){

      			mOutPacket.begin(squenceId, cmd);
      			mOutPacket.writeInt(Main.libArgsConfig.id);//AccessId
      			mOutPacket.writeBytes(data,header_start,header_length+body_length);//原始数据
      			mOutPacket.end();
      			
      			//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
      			byte[] mTempBuff = mInPacket.getPacket();
      			length = PacketTransfer.send2Match(0,mTempBuff, squenceId, user.uid,cmd,TDistapch.TYPE_P2P,mOutPacket.getPacket(),0,  mOutPacket.getLength());
          		send2Dispatch(mTempBuff,0,length);
      		}else{
      			Logger.v("unhandled cmd 0x"+Integer.toHexString(cmd));
      		}
      	}else{
			if(cmd < 0x1001){//系统处理
				if(cmd == SystemCmd.CMD_SYS_HEAR_BEAT){
	      			byte[] mTempBuff = mInPacket.getPacket();
					int length 		= BasePacket.buildClientPacekt(mTempBuff, squenceId+1, SystemCmd.CMD_SYS_HEAR_BEAT_REPONSE, (byte)0,mOutPacket.getPacket(),0,0);
			        Main.mServerHandler.unicast(client, mTempBuff,0,length);
				}else{
					Logger.v("unhandled sys_cmd 0x"+Integer.toHexString(cmd));
				}
			}else{//游戏处理
				if(user.gameId>0){//说明处于游戏中
	      			//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
	      			byte[] mTempBuff = mInPacket.getPacket();
	      			int length = PacketTransfer.send2Game(user.gameId, user.gameSid,mTempBuff, squenceId, user.uid,cmd,TDistapch.TYPE_P2P,data,header_start,  header_length + body_length);
	          		send2Dispatch(mTempBuff,0,length);
				}else{
					Logger.v("unhandled game_cmd 0x"+Integer.toHexString(cmd));
				}
			}
  		}
	}
}
