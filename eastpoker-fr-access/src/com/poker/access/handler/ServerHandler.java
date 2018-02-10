package com.poker.access.handler;

import com.open.net.server.object.AbstractServerClient;
import com.open.util.log.Logger;
import com.poker.access.Main;
import com.poker.access.object.User;
import com.poker.access.object.UserPool;
import com.poker.base.ServerIds;
import com.poker.cmd.BaseGameCmd;
import com.poker.cmd.Cmd;
import com.poker.cmd.LoginCmd;
import com.poker.data.DistapchType;
import com.poker.packet.BasePacket;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.packet.PacketTransfer;

public class ServerHandler extends AbsServerHandler{

	public ServerHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
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
        		int length = PacketTransfer.send2Game(attachUser.gameId, attachUser.gameSid, mTempBuff, squenceId, attachUser.uid, BaseGameCmd.CMD_CLIENT_OFFLINE, DistapchType.TYPE_P2P, mOutPacket.getPacket(),0,  0);
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
      		if(server == ServerIds.SERVER_LOGIN){
      			
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

      			mOutPacket.begin(squenceId, cmd);
      			mOutPacket.writeInt(Main.libArgsConfig.id);//AccessId
      			mOutPacket.writeBytes(data,header_start,header_length+body_length);//原始数据
      			mOutPacket.end();
      			
      			//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
      			byte[] mTempBuff = mInPacket.getPacket();
      			length = PacketTransfer.send2User(0,mTempBuff, squenceId, user.uid,cmd,DistapchType.TYPE_P2P,mOutPacket.getPacket(),0,  mOutPacket.getLength());
          		send2Dispatch(mTempBuff,0,length);
      		}else{
      			Logger.v("unhandled sys_cmd_a ");
      		}
      	}else{
			if(cmd < 0x1001){//系统处理
				Logger.v("unhandled sys_cmd_b ");
			}else{//游戏处理
				if(user.gameId>0){//说明处于游戏中
	      			//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
	      			byte[] mTempBuff = mInPacket.getPacket();
	      			int length = PacketTransfer.send2Game(user.gameId, user.gameSid,mTempBuff, squenceId, user.uid,cmd,DistapchType.TYPE_P2P,data,header_start,  header_length + body_length);
	          		send2Dispatch(mTempBuff,0,length);
				}else{
					Logger.v("unhandled game_cmd ");
				}
			}
  		}
	}
}
