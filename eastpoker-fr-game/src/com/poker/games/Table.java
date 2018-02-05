package com.poker.games;

import com.poker.cmd.AllocatorCmd;
import com.poker.cmd.SystemCmd;
import com.poker.cmd.UserCmd;
import com.poker.common.config.Config;
import com.poker.data.DistapchType;
import com.poker.game.Main;
import com.poker.games.define.GameDefine.LoginResult;
import com.poker.games.define.GameDefine.LogoutResult;
import com.poker.games.define.GameDefine.TableStatus;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.packet.PacketTransfer;
import com.poker.protocols.server.ErrorServer;

public abstract class Table {
	
	public Room mRoom;
	public final int tableId;
	public Config mConfig;
	
	public TableStatus table_status;
	public final User[] users;
	public final User[] onLookers = new User[10];
	public int count;

	
	static InPacket  mInPacket;
	static OutPacket mOutPacket;
	
	public Table(Room mRoom , int tableId , Config mConfig) {
		this.mRoom = mRoom;
		this.tableId = tableId;
		this.mConfig = mConfig;
		
		users = new User[mConfig.table_max_user];
		count=0;
		
		if(null == mInPacket){
			mInPacket = new InPacket(Main.libClientConfig.packet_max_length_tcp);
			mOutPacket= new OutPacket(Main.libClientConfig.packet_max_length_tcp);
		}
	}

	public int getUserCount(){
		return count;
	}
	
	public int userChangeSeat(User user,int new_seatId){
		
		//新座位无效
		if(new_seatId <0 || new_seatId>=users.length){
			return -1;
		}
		
		//已经是该座位了
		if(user.seatId == new_seatId){
			return -2;
		}
		
		//新座位已经有其它人了
		if(null != users[new_seatId]){
			return -3;
		}
		
		
		if(user.seatId != -1){//不等于-1：说明是已经在桌子上的用户进行换座位；等于-1：说明是新用户第一次进入
			users[user.seatId] = null;
			users[new_seatId] = user;
		}else{
			users[new_seatId] = user;
			count++;
		}
		return 1;
	}
	
	public LoginResult userEnter(User user){
		for (User u : users) {
			if(null != u && u.uid == user.uid){
				return LoginResult.LOGIN_SUCCESS_ALREADY_EXIST;
			}
		}
		
		for(int i = 0;i<users.length;i++){
			if(null == users[i]){
				users[i] = user;
				count++;
				return LoginResult.LOGIN_SUCCESS;
			}
		}
		return LoginResult.LOGIN_FAILED_FULL;
	}
	
	public LogoutResult userExit(User user){
		for (int i = 0; i < users.length; i++) {
			if(null != users[i] && users[i].uid == user.uid){
				users[i] = null;
				count--;
				return LogoutResult.LOGOUT_SUCCESS;
			}
		}
		return LogoutResult.LOGOUT_FAILED;
	}
	
	public int userReady(User user){
		for (int i = 0; i < users.length; i++) {
			if(null != users[i] && users[i].uid == user.uid){
				users[i].isReady = true;
				return 1;
			}
		}
		return 0;
	}
	
	
	public int userOffline(User user){
		for (int i = 0; i < users.length; i++) {
			if(null != users[i] && users[i].uid == user.uid){
				users[i].onLineStatus = 0;
				users[i].accessId = -1;
				return 1;
			}
		}
		return 0;
	}
	
	public boolean isUserInTable(User user){
		for (int i = 0; i < users.length; i++) {
			if(null != users[i] && users[i].uid == user.uid){
				return true;
			}
		}
		return false;
	}
	//---------------------------------------------------------------------
	public LoginResult onUserLogin(User mUser){
		LoginResult ret= userEnter(mUser);
		if(ret == LoginResult.LOGIN_SUCCESS){
			onTableUserFirstLogin(mUser);
		}else if(ret == LoginResult.LOGIN_SUCCESS_ALREADY_EXIST){
			onTableUserReLogin(mUser);
		}else if(ret == LoginResult.LOGIN_FAILED_FULL){
			send2Access(mUser,SystemCmd.CMD_ERR,0,ErrorServer.error(SystemCmd.ERR_CODE_LOGIN_FAILED_TABLE_FULL,""));
		}
		return ret;
	};
	
	public LogoutResult onUserExit(User mUser){
		LogoutResult ret= userExit(mUser);
		if(ret ==  LogoutResult.LOGOUT_SUCCESS){
			onTableUserExit(mUser);
		}
		return ret;
	};
	
	public int onUserReady(User mUser){
		if(userReady(mUser) == 1){
			onTableUserReady(mUser);
			return 1;
		}
		return 0;
	};
	
	public int onUserOffline(User mUser){
		if(userOffline(mUser) == 1){
			onTableUserOffline(mUser);
			return 1;
		}
		return 0;
	};
	
	public int onKickUser(User mUser , User kickedUser){
		return -1;
	};
	
	public int onSendCmd(int cmd, byte[] data, int offset, int length){
		return -1;
	}
	
	protected void startGame(){
		table_status = TableStatus.TABLE_STATUS_PLAY;
		//更新用户游戏状态
		for (int i = 0; i < users.length; i++) {
			if(null != users[i]){
				users[i].startGame();
			}
		}
	}
	
	protected void stopGame(){
		table_status = TableStatus.TABLE_STATUS_STOP;
		//更新用户游戏状态，
		for (int i = 0; i < users.length; i++) {
			if(null != users[i]){
				
				users[i].stopGame();
				
				//将不在线的用户踢出去
				if(users[i].isOffline()){
					mRoom.logoutGame(users[i], this);
					users[i] = null;
				}
			}
		}
	}
	
	public void resetTable(){
		table_status = TableStatus.TABLE_STATUS_STOP;
		for (int i = 0; i < users.length; i++) {
			if(null != users[i]){
				users[i].stopGame();
				users[i] = null;
			}
		}
	}
	
	public void enterRoom(long uid,Table table){
		
		mOutPacket.begin(0, AllocatorCmd.CMD_LOGIN_GAME);
		mOutPacket.writeInt(table.tableId);
		mOutPacket.writeShort(mRoom.gameId);
		mOutPacket.writeShort(mRoom.gameSid);
		mOutPacket.end();
		
		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
		byte[] mTempBuff = mInPacket.getPacket();
		int length = PacketTransfer.send2User(0, mTempBuff, 0, uid, UserCmd.CMD_ENTER_ROOM, DistapchType.TYPE_P2P, mOutPacket.getPacket(),0,  mOutPacket.getLength());
		Main.send2Dispatch(mTempBuff,0,length);	
	}
	
	public void leaveRoom(long uid){
		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
		byte[] mTempBuff = mInPacket.getPacket();
		
		int length = PacketTransfer.send2User(0, mTempBuff, 0, uid, UserCmd.CMD_LEAVE_ROOM, DistapchType.TYPE_P2P, mOutPacket.getPacket(),0,  0);
		Main.send2Dispatch(mTempBuff,0,length);	
	}
	
	public void send2Access(User user,int cmd,int squenceId ,byte[] body){
		this.send2Access(user,cmd, squenceId, body, 0, body.length);
	}

	public int send2Access(User user,int cmd,int squenceId ,byte[] body,int offset ,int length){
		length = PacketTransfer.send2Access(user.accessId,mOutPacket.getPacket(), squenceId, user.uid, cmd, DistapchType.TYPE_P2P, body,offset,length);
		Main.send2Dispatch(mOutPacket.getPacket(), 0, length);
		return 1;
	}
	
	public void broadcast(User user,int cmd,int squenceId ,byte[] body) {
		this.broadcast(cmd, squenceId, body, null);
	}
	
	public void broadcast(int cmd,int squenceId ,byte[] body,User user) {
		for(int i =0 ;i<users.length;i++){
			User mUser = users[i];
			if(null != mUser && mUser != user){
				send2Access(mUser,cmd,squenceId,body);
			}
		}
	}
	
	//------------------------------------子游戏必需实现的业务逻辑------------------------------------
	protected abstract int onTableUserFirstLogin(User mUser);//用户首次进入这张桌子
	protected abstract int onTableUserReLogin(User mUser);//相当于用户进行重连
	protected abstract int onTableUserExit(User mUser);//用户退出桌子
	protected abstract int onTableUserOffline(User mUser);//用户掉线
	protected abstract int onTableUserReady(User mUser);//用户准备
	protected abstract int dispatchTableMessage(User mUser,int cmd, byte[] data, int header_start, int header_length, int body_start,int body_length);
	protected abstract int onTimeOut();
}
