package com.poker.games;

public class Table {
	
	public int tableId;
	public User[] users;
	public int count;
	
	public Table(int table_max_user) {
		users = new User[table_max_user];
		count=0;
	}

	public int getUserCount(){
		return count;
	}
	
	public void userChangeSeat(User user,int new_seatId){
		
		//新座位无效
		if(new_seatId <0 || new_seatId>=users.length){
			return;
		}
		
		//已经是该座位了
		if(user.seatId == new_seatId){
			return;
		}
		
		//新座位已经有其它人了
		if(null != users[new_seatId]){
			return;
		}
		
		
		if(user.seatId != -1){//不等于-1：说明是已经在桌子上的用户进行换座位；等于-1：说明是新用户第一次进入
			users[user.seatId] = null;
			users[new_seatId] = user;
		}else{
			users[new_seatId] = user;
			count++;
		}
	}
	
	
	public void userEnter(User user){
		for (User u : users) {
			if(null != u && u == user){
				return;
			}
		}
		
		for(int i = 0;i<users.length;i++){
			if(null == users[i]){
				users[i] = user;
				count++;
				break;
			}
		}
	}
	
	public void userExit(User user){
		for (User u : users) {
			if(null != u && u == user){
				count--;
				return;
			}
		}
	}
}
