package com.poker.games;

public abstract class Table {
	
	public final int tableId;
	public final User[] users;
	public int count;
	
	public Table(int tableId , int table_max_user) {
		this.tableId = tableId;
		users = new User[table_max_user];
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
	
	
	public int userEnter(User user){
		for (User u : users) {
			if(null != u && u == user){
				return -1;
			}
		}
		
		for(int i = 0;i<users.length;i++){
			if(null == users[i]){
				users[i] = user;
				count++;
				break;
			}
		}
		return 1;
	}
	
	public int userExit(User user){
		for (User u : users) {
			if(null != u && u == user){
				count--;
				return 1;
			}
		}
		return -1;
	}
	
	//---------------------------------------------------------------------
	public int onUserEnter(User mUser){
		return -1;
	};
	
	public int onUserExit(User mUser){
		return -1;
	};
	
	public int onUserReady(User mUser){
		return -1;
	};
	
	public int onUserOffline(User mUser){
		return -1;
	};
	
	public int onKickUser(User mUser , User kickedUser){
		return -1;
	};
	
	public int onSendCmd(int cmd, byte[] data, int offset, int length){
		return -1;
	}
	
	//------------------------------------子游戏必需实现的业务逻辑------------------------------------
	protected abstract int dispatchTableMessage(int cmd, byte[] data, int header_start, int header_length, int body_start,int body_length);
}
