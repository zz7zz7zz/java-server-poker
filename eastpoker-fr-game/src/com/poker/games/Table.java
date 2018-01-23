package com.poker.games;

import com.poker.cmd.SystemCmd;
import com.poker.common.config.Config;
import com.poker.data.DataPacket;
import com.poker.game.Main;
import com.poker.game.handler.ImplDataTransfer;
import com.poker.protocols.server.ErrorServer;

public abstract class Table {
	
	public final int tableId;
	public TableStatus table_status;
	
	public Config mConfig;
	public final User[] users;
	public int count;

	public enum TableStatus{
		TABLE_STATUS_PLAY(1),
		TABLE_STATUS_STOP(2);
		
		int code;
        private TableStatus(int code) {
            this.code = code;
        }
	}
	
	enum LoginRet{
		LOGIN_SUCCESS(1),
		LOGIN_FAILED_ALREADY_EXIST(2),
		LOGIN_FAILED_FULL(3);
		
		int code;
        private LoginRet(int code) {
            this.code = code;
        }
	}
	
	enum LogoutRet{
		LOGOUT_SUCCESS(1),
		LOGOUT_FAILED(2);
		
		int code;
        private LogoutRet(int code) {
            this.code = code;
        }
	}
	
	public Table(int tableId , Config mConfig) {
		this.tableId = tableId;
		this.mConfig = mConfig;
		users = new User[mConfig.table_max_user];
		count=0;
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
	
	public LoginRet userEnter(User user){
		for (User u : users) {
			if(null != u && u.uid == user.uid){
				return LoginRet.LOGIN_FAILED_ALREADY_EXIST;
			}
		}
		
		for(int i = 0;i<users.length;i++){
			if(null == users[i]){
				users[i] = user;
				count++;
				return LoginRet.LOGIN_SUCCESS;
			}
		}
		return LoginRet.LOGIN_FAILED_FULL;
	}
	
	public LogoutRet userExit(User user){
		for (int i = 0; i < users.length; i++) {
			if(null != users[i] && users[i].uid == user.uid){
				users[i] = null;
				count--;
				return LogoutRet.LOGOUT_SUCCESS;
			}
		}
		return LogoutRet.LOGOUT_FAILED;
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
	
	//---------------------------------------------------------------------
	public int onUserLogin(User mUser){
		LoginRet ret= userEnter(mUser);
		if(ret == LoginRet.LOGIN_SUCCESS){
			onTableUserFirstLogin(mUser);
			return 1;
		}else if(ret == LoginRet.LOGIN_FAILED_ALREADY_EXIST){
			onTableUserReLogin(mUser);
			return 1;
		}else if(ret == LoginRet.LOGIN_FAILED_FULL){
			send2Access(SystemCmd.CMD_ERR,0,ErrorServer.error(SystemCmd.ERR_CODE_LOGIN_FAILED_TABLE_FULL,""));
			return 0;
		}
		return 0;
	};
	
	public int onUserExit(User mUser){
		LogoutRet ret= userExit(mUser);
		if(ret ==  LogoutRet.LOGOUT_SUCCESS){
			onTableUserExit(mUser);
		}
		return ret == LogoutRet.LOGOUT_SUCCESS ? 1 : 0;
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
	}
	
	protected void stopGame(){
		table_status = TableStatus.TABLE_STATUS_STOP;
		//更新用户游戏状态
	}
	
	
	public void send2Access(int cmd,int squenceId ,byte[] body){
		this.send2Access(cmd, squenceId, body, 0, body.length);
	}
	public void send2Access(int cmd,int squenceId ,byte[] body,int offset ,int length){
		int size = DataPacket.write(Main.write_buff, squenceId, cmd, (byte)0, body,offset,length);
		size =  ImplDataTransfer.send2Allocator(Main.write_buff_dispatcher, 0, Main.write_buff, 0, size);
		Main.mClientMessageProcessor.send(Main.dispatcher[0], Main.write_buff_dispatcher, 0, size);
	}
	
	public void broadcast(int cmd,int squenceId ,byte[] body) {
		this.broadcast(cmd, squenceId, body, null);
	}
	
	public void broadcast(int cmd,int squenceId ,byte[] body,User user) {
		for(int i =0 ;i<users.length;i++){
			User mUser = users[i];
			if(null != mUser && mUser != user){
				send2Access(cmd,squenceId,body);
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
}
