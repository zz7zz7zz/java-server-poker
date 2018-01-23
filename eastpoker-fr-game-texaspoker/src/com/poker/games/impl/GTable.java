package com.poker.games.impl;

import java.util.Random;

import com.poker.common.config.Config;
import com.poker.game.handler.GameBaseServer;
import com.poker.games.GBaseCmd;
import com.poker.games.Table;
import com.poker.games.User;
import com.poker.games.impl.GUser.GStatus;
import com.poker.games.impl.config.CardConfig;
import com.poker.games.impl.config.GameConfig;
import com.poker.games.impl.handler.GCmd;
import com.poker.games.impl.handler.TexasGameServer;

public class GTable extends Table {
	
	public GameConfig mGameConfig;
	public CardConfig mCardConfig;
	
	public long   cardFlags = Long.MAX_VALUE;
	
	public int squenceId = 0;
	
	public int sb_seatid = -1;
	public int bb_seatid = -1;
	public int btn_seateId =-1;
	
	public byte[] flop=new byte[3];
	public byte[] turn=new byte[1];
	public byte[] river=new byte[1];
	
	
	public GTable(int tableId, Config mConfig,GameConfig mGameConfig,CardConfig mCardConfig) {
		super(tableId, mConfig);
		this.mCardConfig = mCardConfig;
	}

	@Override
	protected int onTableUserFirstLogin(User mUser) {
		//1。对进来的用户广播桌子上有哪些用户
		send2Access(GBaseCmd.CMD_SERVER_USERLOGIN, squenceId, GameBaseServer.userLogin(mUser.seatId,this));
		
		//2.对桌子上的用户广播谁进来类
		broadcast(GBaseCmd.CMD_SERVER_BROAD_USERLOGIN, squenceId, GameBaseServer.broadUserLogin(mUser),mUser);
		
		//3.判断游戏谁否可以开始了
		if(table_status == TableStatus.TABLE_STATUS_PLAY){
			return 0;
		}
		if(getUserCount()>=this.mConfig.table_max_user) {
			startGame();
		}
		return 0;
	}



	@Override
	protected int onTableUserReLogin(User mUser) {
		if(table_status == TableStatus.TABLE_STATUS_PLAY){
			squenceId++;
			send2Access(GCmd.CMD_SERVER_RECONNECT, squenceId, TexasGameServer.reconnect(this,mGameConfig));
			return 0;
		}
		return 0;
	}



	@Override
	protected int onTableUserExit(User mUser) {
		broadcast(GBaseCmd.CMD_SERVER_BROAD_USERLOGOUT, squenceId, GameBaseServer.broadUserLogout(mUser),mUser);
		return 0;
	}



	@Override
	protected int onTableUserOffline(User mUser) {
		broadcast(GBaseCmd.CMD_SERVER_BROAD_USERLOGOUT, squenceId, GameBaseServer.broadUserOffline(mUser.uid,mUser.onLineStatus),mUser);
		return 0;
	}



	@Override
	protected int onTableUserReady(User mUser) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	protected int dispatchTableMessage(int cmd, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {
		// TODO Auto-generated method stub
		return 0;
	}

	//-------------------------------------------------------
	public void startGame() {
		super.startGame();
		
        //1.设置每个玩家的游戏状态
		int play_user_count = 0;
		GUser[] gGsers=(GUser[])users;
        for(int i =0;i<gGsers.length;i++) {
    		if(null ==gGsers[i]) {
    			continue;
    		}
    		gGsers[i].play_status = GStatus.GStatus_PLAY;
    		play_user_count++;
        }
        
        if(play_user_count<this.mConfig.table_min_user){
        	stopGame();
        	return;
        }
        
        //2.找出小盲，大盲位，按钮位
        int next_seatId_index;
        if(sb_seatid == -1){//说明是游戏刚开始，没有持续
			long t = System.currentTimeMillis();//获得当前时间的毫秒数
	        Random rd = new Random(t);//作为种子数传入到Random的构造器中
	        next_seatId_index = rd.nextInt(this.mConfig.table_max_user);
        }else{//说明是游戏持续
        	next_seatId_index = sb_seatid+1;
        }
        
    	for(int i = 0 ;i<this.mConfig.table_max_user;i++){
    		int r_next_seatId_index = (next_seatId_index + i)%this.mConfig.table_max_user;
        	if(null != gGsers[r_next_seatId_index] && gGsers[r_next_seatId_index].play_status == GStatus.GStatus_PLAY){
        		sb_seatid = r_next_seatId_index;
        		break;
        	}
    	}

    	next_seatId_index = (sb_seatid+1)%this.mConfig.table_max_user;
    	for(int i = 0 ;i<this.mConfig.table_max_user;i++){
    		int r_next_seatId_index = (next_seatId_index + i)%this.mConfig.table_max_user;
        	if(null != gGsers[r_next_seatId_index] && gGsers[r_next_seatId_index].play_status == GStatus.GStatus_PLAY){
        		bb_seatid = r_next_seatId_index;
        		break;
        	}
    	}
    	
    	next_seatId_index = (sb_seatid-1)%this.mConfig.table_max_user;
    	for(int i = 0 ;i<this.mConfig.table_max_user;i++){
    		int r_next_seatId_index = (next_seatId_index - i + this.mConfig.table_max_user)%this.mConfig.table_max_user;
        	if(null != gGsers[r_next_seatId_index] && gGsers[r_next_seatId_index].play_status == GStatus.GStatus_PLAY){
        		btn_seateId = r_next_seatId_index;
        		break;
        	}
    	}
    	
    	//3.发送游戏开始数据
    	squenceId++;
    	broadcast(GCmd.CMD_SERVER_GAME_START, squenceId, TexasGameServer.start(sb_seatid, bb_seatid, btn_seateId, mGameConfig));
	}
	
	public void dealPreFlop() {
        GUser[] gGsers=(GUser[])users;
        
		if(mCardConfig.isEnable) {
	        for(int i =0;i<gGsers.length;i++) {
        		if(null ==gGsers[i] || gGsers[i].play_status != GStatus.GStatus_PLAY) {
        			continue;
        		}
        		
        		GUser user = gGsers[i];
        		for(int j=0;j<user.handCard.length;j++) {
        			user.handCard[j]=mCardConfig.user_cards[i][j];
        		}
	        }
		}else {
	        long t = System.currentTimeMillis();//获得当前时间的毫秒数
	        Random rd = new Random(t);//作为种子数传入到Random的构造器中
	        for(int i =0;i<gGsers.length;i++) {
        		if(null ==gGsers[i] || gGsers[i].play_status != GStatus.GStatus_PLAY) {
        			continue;
        		}
        		
        		GUser user = gGsers[i];
        		for(int j=0;j<user.handCard.length;j++) {
        			int cardIndex;
        			while(true){
        				cardIndex= rd.nextInt(GData.POKER_ARRAY.length);
    	    			//判断该位是否被拿取过了,再赋值
    	    			if(((cardFlags >>(cardIndex)) & 0x1) == 0x1){
    	    				user.handCard[j]=GData.POKER_ARRAY[cardIndex];
    		    			cardFlags &= ~(1<<cardIndex);
    		    			break;
    	    			}
        			}
        		}
	        }
		}
		
		for(int i =0;i<gGsers.length;i++) {
     		if(null ==gGsers[i] || gGsers[i].play_status != GStatus.GStatus_PLAY) {
     			continue;
     		}
     		
     		GUser user = gGsers[i];
     		squenceId++;
        	send2Access(GCmd.CMD_SERVER_DEAL_PREFLOP, squenceId, TexasGameServer.dealPreFlop( user.handCard));
	    }
		 
  		squenceId++;
  		broadcast(GCmd.CMD_SERVER_BROADCAST_WHO_ACTION_WAHT, squenceId, TexasGameServer.broadcastUserAction());
	}
	
	public void dealFlop() {
		if(mCardConfig.isEnable) {
			for(int j=0;j<flop.length;j++) {
				flop[j]=mCardConfig.flop[j];
    			}
		}else {
			long t = System.currentTimeMillis();//获得当前时间的毫秒数
	        Random rd = new Random(t);//作为种子数传入到Random的构造器中
    		for(int j=0;j<flop.length;j++) {
    			int cardIndex;
    			while(true){
    				cardIndex= rd.nextInt(GData.POKER_ARRAY.length);
	    			//判断该位是否被拿取过了,再赋值
	    			if(((cardFlags >>(cardIndex)) & 0x1) == 0x1){
	    				flop[j]=GData.POKER_ARRAY[cardIndex];
		    			cardFlags &= ~(1<<cardIndex);
		    			break;
	    			}
    			}
    		}
		}
		
		squenceId++;
		broadcast(GCmd.CMD_SERVER_DEAL_FLOP, squenceId, TexasGameServer.dealFlop(flop));
		
		squenceId++;
		broadcast(GCmd.CMD_SERVER_BROADCAST_WHO_ACTION_WAHT, squenceId, TexasGameServer.broadcastUserAction());
	}
	
	public void dealTrun() {
		if(mCardConfig.isEnable) {
			for(int j=0;j<turn.length;j++) {
				turn[j]=mCardConfig.turn[j];
    			}
		}else {
			long t = System.currentTimeMillis();//获得当前时间的毫秒数
	        Random rd = new Random(t);//作为种子数传入到Random的构造器中
    		for(int j=0;j<turn.length;j++) {
    			int cardIndex;
    			while(true){
    				cardIndex= rd.nextInt(GData.POKER_ARRAY.length);
	    			//判断该位是否被拿取过了,再赋值
	    			if(((cardFlags >>(cardIndex)) & 0x1) == 0x1){
		    			turn[j]=GData.POKER_ARRAY[cardIndex];
		    			cardFlags &= ~(1<<cardIndex);
		    			break;
	    			}
    			}
    		}
		}
		
		squenceId++;
		broadcast(GCmd.CMD_SERVER_DEAL_TURN, squenceId, TexasGameServer.dealTrun(turn));
		
		squenceId++;
		broadcast(GCmd.CMD_SERVER_BROADCAST_WHO_ACTION_WAHT, squenceId, TexasGameServer.broadcastUserAction());
	}
	
	public void dealRiver() {
		if(mCardConfig.isEnable) {
			for(int j=0;j<river.length;j++) {
				river[j]=mCardConfig.river[j];
    			}
		}else { 
			long t = System.currentTimeMillis();//获得当前时间的毫秒数
	        Random rd = new Random(t);//作为种子数传入到Random的构造器中
    		for(int j=0;j<river.length;j++) {
    			int cardIndex;
    			while(true){
    				cardIndex= rd.nextInt(GData.POKER_ARRAY.length);
	    			//判断该位是否被拿取过了,再赋值
	    			if(((cardFlags >>(cardIndex)) & 0x1) == 0x1){
	    				river[j]=GData.POKER_ARRAY[cardIndex];
		    			cardFlags &= ~(1<<cardIndex);
		    			break;
	    			}
    			}
    		}
		}
		
		squenceId++;
		broadcast(GCmd.CMD_SERVER_DEAL_RIVER, squenceId,TexasGameServer.dealRiver(river));
		
		squenceId++;
		broadcast(GCmd.CMD_SERVER_BROADCAST_WHO_ACTION_WAHT, squenceId, TexasGameServer.broadcastUserAction());
	}
	
	public void showHands() {
		squenceId++;
		broadcast(GCmd.CMD_SERVER_BROADCAST_SHOW_HAND, squenceId, TexasGameServer.showHand(this));
	}
	
	public void stopGame() {
		squenceId++;
		broadcast(GCmd.CMD_SERVER_GAME_END, squenceId, TexasGameServer.end(this));
		
		//--------------------------------------------------------------------------------
		cardFlags |= Long.MAX_VALUE;
		
		GUser[] gGsers=(GUser[])users;
        for(int i =0;i<gGsers.length;i++) {
    		if(null ==gGsers[i]) {
    			continue;
    		}
    		gGsers[i].clear();
        }
        
		super.stopGame();
	}
}
