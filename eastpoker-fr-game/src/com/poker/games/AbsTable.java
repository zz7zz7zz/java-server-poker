package com.poker.games;

import com.poker.cmd.AccessCmd;
import com.poker.cmd.AllocatorCmd;
import com.poker.cmd.UserCmd;
import com.poker.common.config.Config;
import com.poker.data.DistapchType;
import com.poker.game.Main;
import com.poker.games.define.GameDefine.LoginResult;
import com.poker.games.define.GameDefine.LogoutResult;
import com.poker.games.define.GameDefine.TableStatus;
import com.poker.games.impl.User;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.packet.PacketTransfer;

public abstract class AbsTable {
	
	public Room mRoom;
	public final int tableId;
	public Config mConfig;
	public TableStatus table_status;

	static InPacket  mInPacket;
	static OutPacket mOutPacket;
	
	public AbsTable(Room mRoom , int tableId , Config mConfig) {
		this.mRoom = mRoom;
		this.tableId = tableId;
		this.mConfig = mConfig;
		
		if(null == mInPacket){
			mInPacket = new InPacket(Main.libClientConfig.packet_max_length_tcp);
			mOutPacket= new OutPacket(Main.libClientConfig.packet_max_length_tcp);
		}
	}
	
	//---------------------------------------------------------------------
	protected void startGame(){
		table_status = TableStatus.TABLE_STATUS_PLAY;
	}
	
	protected void stopGame(){
		table_status = TableStatus.TABLE_STATUS_STOP;
	}
	
	public void resetTable(){
		table_status = TableStatus.TABLE_STATUS_STOP;
	}
	
	public void enterRoom2Access(User user,AbsTable table){
		int squenceId = 0;
		mOutPacket.begin(squenceId, AccessCmd.CMD_LOGIN_GAME);
		mOutPacket.writeInt(table.tableId);
		mOutPacket.writeShort(mRoom.gameId);
		mOutPacket.writeShort(mRoom.gameSid);
		mOutPacket.end();
		
		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
		byte[] mTempBuff = mInPacket.getPacket();
		int length = PacketTransfer.send2Access(user.accessId, mTempBuff, squenceId, user.uid, AccessCmd.CMD_LOGIN_GAME, DistapchType.TYPE_P2P, mOutPacket.getPacket(),0,  mOutPacket.getLength());
		Main.send2Dispatch(mTempBuff,0,length);	
	}
	
	public void enterRoom2Alloc(User user,AbsTable table){

	}
	
	public void enterRoom2User(User user,AbsTable table){
		int squenceId = 0;
		int dst_server_id = 0;
		mOutPacket.begin(squenceId, AllocatorCmd.CMD_LOGIN_GAME);
		mOutPacket.writeInt(table.tableId);
		mOutPacket.writeShort(mRoom.gameId);
		mOutPacket.writeShort(mRoom.gameSid);
		mOutPacket.end();
		
		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
		byte[] mTempBuff = mInPacket.getPacket();
		int length = PacketTransfer.send2User(dst_server_id, mTempBuff, squenceId, user.uid, UserCmd.CMD_ENTER_ROOM, DistapchType.TYPE_P2P, mOutPacket.getPacket(),0,  mOutPacket.getLength());
		Main.send2Dispatch(mTempBuff,0,length);	
	}
	
	public void leaveRoom2Access(User user){
		int squenceId = 0;
		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
		byte[] mTempBuff = mInPacket.getPacket();
		int length = PacketTransfer.send2User(user.accessId, mTempBuff, squenceId, user.uid, AccessCmd.CMD_LOGINOUT_GAME, DistapchType.TYPE_P2P, mOutPacket.getPacket(),0,  0);
		Main.send2Dispatch(mTempBuff,0,length);	
	}
	
	public void leaveRoom2Alloc(User user){

	}
	
	public void leaveRoom2User(User user){
		int squenceId = 0;
		int dst_server_id = 0;
		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
		byte[] mTempBuff = mInPacket.getPacket();
		int length = PacketTransfer.send2User(dst_server_id, mTempBuff, squenceId, user.uid, UserCmd.CMD_LEAVE_ROOM, DistapchType.TYPE_P2P, mOutPacket.getPacket(),0,  0);
		Main.send2Dispatch(mTempBuff,0,length);	
	}
	
	protected void send2Access(int cmd,int squenceId ,byte[] body,User user){
		this.send2Access(cmd, squenceId, body, 0, body.length,user);
	}

	protected int send2Access(int cmd,int squenceId ,byte[] body,int offset ,int length,User user){
		length = PacketTransfer.send2Access(user.accessId,mOutPacket.getPacket(), squenceId, user.uid, cmd, DistapchType.TYPE_P2P, body,offset,length);
		Main.send2Dispatch(mOutPacket.getPacket(), 0, length);
		return 1;
	}
	
	//------------------------------------子游戏必需实现的业务逻辑------------------------------------
	//游戏基础接口(非游戏内接口)
	public abstract LoginResult onUserLogin(User mUser);
	public abstract int onUserReady(User mUser);
	public abstract LogoutResult onUserExit(User mUser);
	
	public abstract int getUserCount();
	public abstract int onUserOffline(User mUser);
	public abstract int onKickUser(User mUser,User kickedUser);
	public abstract boolean isUserInTable(User mUser);

	//发数据接口
	protected abstract int sendToClient(int cmd,int squenceId ,byte[] body,User user);//用户准备
	protected abstract int broadcastToClient(int cmd,int squenceId ,byte[] body,User user);//用户准备
	
	//定时器回调接口
	public abstract int onTimeOut();
	
	//游戏内接口数据
	public abstract int dispatchTableMessage(User mUser,int cmd, byte[] data, int header_start, int header_length, int body_start,int body_length);
}
