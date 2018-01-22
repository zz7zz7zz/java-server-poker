package com.poker.games.impl;

import java.util.Random;

import com.poker.games.Table;
import com.poker.games.User;
import com.poker.games.impl.config.CardConfig;
public class GTable extends Table {
	public CardConfig mCardConfig;
	
	public byte[] flop=new byte[3];
	public byte[] turn=new byte[1];
	public byte[] river=new byte[1];
	
	public GTable(int tableId, int table_max_user,CardConfig mCardConfig) {
		super(tableId, table_max_user);
		this.mCardConfig = mCardConfig;
	}

	@Override
	protected int onTableUserFirstLogin(User mUser) {
		//1。对进来的用户广播桌子上有哪些用户
		
		//2.对桌子上的用户广播谁进来类
		
		//3.判断游戏谁否可以开始了
		if(getUserCount()>=table_max_user) {
			startGame();
		}
		return 0;
	}



	@Override
	protected int onTableUserReLogin(User mUser) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	protected int onTableUserExit(User mUser) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	protected int onTableUserOffline(User mUser) {
		// TODO Auto-generated method stub
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
		
	}
	
	public void dealPreFlop() {
		if(mCardConfig.isEnable) {
	        GUser[] gGsers=(GUser[])users;
	        for(int i =0;i<gGsers.length;i++) {
	        		if(null ==gGsers[i]) {
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
	        
	        GUser[] gGsers=(GUser[])users;
	        for(int i =0;i<gGsers.length;i++) {
	        		if(null ==gGsers[i]) {
	        			continue;
	        		}
	        		
	        		GUser user = gGsers[i];
	        		for(int j=0;j<user.handCard.length;j++) {
	        			int cardIndex= rd.nextInt(GData.POKER_ARRAY.length);
	        			//判断该位是否被拿取过了,再赋值
	        			user.handCard[j]=GData.POKER_ARRAY[cardIndex];
	        		}
	        }
		}
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
	    			int cardIndex= rd.nextInt(GData.POKER_ARRAY.length);
	    			//判断该位是否被拿取过了,再赋值
	    			flop[j]=GData.POKER_ARRAY[cardIndex];
	    		}
		}
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
	    			int cardIndex= rd.nextInt(GData.POKER_ARRAY.length);
	    			//判断该位是否被拿取过了,再赋值
	    			turn[j]=GData.POKER_ARRAY[cardIndex];
	    		}
		}
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
	    			int cardIndex= rd.nextInt(GData.POKER_ARRAY.length);
	    			//判断该位是否被拿取过了,再赋值
	    			river[j]=GData.POKER_ARRAY[cardIndex];
	    		}
		}
	}
	
	public void endGame() {
		
	}
}
