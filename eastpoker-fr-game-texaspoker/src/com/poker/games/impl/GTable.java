package com.poker.games.impl;

import java.util.Arrays;
import java.util.Random;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.util.log.Logger;
import com.poker.common.config.Config;
import com.poker.games.GDefine.TableStatus;
import com.poker.games.Room;
import com.poker.games.Table;
import com.poker.games.User;
import com.poker.games.impl.GDefine.GStatus;
import com.poker.games.impl.GDefine.GStep;
import com.poker.games.impl.config.CardConfig;
import com.poker.games.impl.config.GameConfig;
import com.poker.games.protocols.GBaseCmd;
import com.poker.protocols.GBaseServer;
import com.poker.protocols.TexasCmd;
import com.poker.protocols.TexasGameServer;
import com.poker.protocols.texaspoker.TexasGameActionRequestProto.TexasGameActionRequest;
import com.poker.protocols.texaspoker.TexasGameBroadcastActionProto.TexasGameBroadcastAction.Operate;

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
	
	public int action_seatid = -1;
	public int action_set = 0;
	public long op_call_chip ;
	public long op_min_raise_chip ;
	public long op_max_raise_chip ;
	
	public long max_round_chip = 0;
	public int max_round_chip_seatid = 0;
	
	public long ante;
	public long bb_chip;
	
	public long ante_all;
	public long pots [] = new long[9];//最多9个pot
	
	public GStep step;
	
	public GTable(Room mRoom,int tableId, Config mConfig,GameConfig mGameConfig,CardConfig mCardConfig) {
		super(mRoom,tableId, mConfig);
		this.mCardConfig = mCardConfig;
		this.ante = mGameConfig.table_ante[0];
		this.bb_chip = mGameConfig.table_blind[0];
	}

	@Override
	protected int onTableUserFirstLogin(User mUser) {
		
		GUser gUser = (GUser)mUser;
		
		//1。对进来的用户广播桌子上有哪些用户
		squenceId++;
		send2Access(mUser,GBaseCmd.CMD_SERVER_USERLOGIN, squenceId, GBaseServer.userLogin(gUser,this,mGameConfig));
		
		//2.对桌子上的用户广播谁进来类
		squenceId++;
		broadcast(GBaseCmd.CMD_SERVER_BROAD_USERLOGIN, squenceId, GBaseServer.broadUserLogin(gUser),mUser);
		
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
			send2Access(mUser,TexasCmd.CMD_SERVER_RECONNECT, squenceId, TexasGameServer.reconnect(this,(GUser)mUser,mGameConfig));
			return 0;
		}
		return 0;
	}



	@Override
	protected int onTableUserExit(User mUser) {
		broadcast(GBaseCmd.CMD_SERVER_BROAD_USERLOGOUT, squenceId, GBaseServer.broadUserLogout(mUser),mUser);
		return 0;
	}



	@Override
	protected int onTableUserOffline(User mUser) {
		broadcast(GBaseCmd.CMD_SERVER_BROAD_USERLOGOUT, squenceId, GBaseServer.broadUserOffline(mUser.uid,mUser.onLineStatus),mUser);
		return 0;
	}



	@Override
	protected int onTableUserReady(User mUser) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	protected int dispatchTableMessage(User mUser,int cmd, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {
		if(cmd == TexasCmd.CMD_CLIENT_WHO_ACTION_WHAT) {
			action(mUser,data, body_start, body_length);
		}
		return 0;
	}

	@Override
	protected int onTimeOut() {
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
    		play_user_count++;
        }
        
        if(play_user_count<this.mConfig.table_min_user){
        	stopGame();
        	return;
        }
        
        //2.找出小盲，大盲位，按钮位
        
        //2.3找Button位
        int next_seatId_index = 0 ;
        if(btn_seateId == -1){//说明是游戏刚开始，没有持续
			long t = System.currentTimeMillis();//获得当前时间的毫秒数
	        Random rd = new Random(t);//作为种子数传入到Random的构造器中
	        next_seatId_index = rd.nextInt(this.mConfig.table_max_user);
        }else{//说明是游戏持续
        	next_seatId_index = sb_seatid+1;
        }
    	if(null == gGsers[next_seatId_index]){
        	for(int i = 1 ;i<this.mConfig.table_max_user;i++){
        		int r_next_seatId_index = (next_seatId_index - i + this.mConfig.table_max_user)%this.mConfig.table_max_user;
            	if(null != gGsers[r_next_seatId_index]){
            		next_seatId_index = r_next_seatId_index;
            		break;
            	}
        	}
    	}
    	btn_seateId = next_seatId_index;
    	
        //2.2找小盲位
    	next_seatId_index = (btn_seateId+1)%this.mConfig.table_max_user;
        if(null == gGsers[next_seatId_index]){
        	for(int i = 1 ;i<this.mConfig.table_max_user;i++){
        		int r_next_seatId_index = (next_seatId_index + i)%this.mConfig.table_max_user;
            	if(null != gGsers[r_next_seatId_index]){
            		next_seatId_index = r_next_seatId_index;
            		break;
            	}
        	}
        }
        sb_seatid = next_seatId_index;
        		
       //2.3找大盲位
    	next_seatId_index = (sb_seatid+1)%this.mConfig.table_max_user;
    	if(null == gGsers[next_seatId_index]){
        	for(int i = 1 ;i<this.mConfig.table_max_user;i++){
        		int r_next_seatId_index = (next_seatId_index + i)%this.mConfig.table_max_user;
            	if(null != gGsers[r_next_seatId_index]){
            		next_seatId_index = r_next_seatId_index;
            		break;
            	}
        	}
    	}
    	bb_seatid = next_seatId_index;
    	
    	//3.发送游戏开始数据
    	squenceId++;
    	broadcast(null,TexasCmd.CMD_SERVER_GAME_START, squenceId, TexasGameServer.start(sb_seatid, bb_seatid, btn_seateId, mGameConfig));
    	
  		step = GStep.START;
	}
	
	public void dealPreFlop() {
        GUser[] gGsers=(GUser[])users;
        
		if(mCardConfig.isEnable) {
	        for(int i =0;i<gGsers.length;i++) {
        		if(null ==gGsers[i] || gGsers[i].play_status != GStatus.PLAY) {
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
        		if(null ==gGsers[i] || gGsers[i].play_status != GStatus.PLAY) {
        			continue;
        		}
        		
        		GUser user = gGsers[i];
        		for(int j=0;j<user.handCard.length;j++) {
        			int cardIndex;
        			while(true){
        				cardIndex= rd.nextInt(GDefine.POKER_ARRAY.length);
    	    			//判断该位是否被拿取过了,再赋值
    	    			if(((cardFlags >>(cardIndex)) & 0x1) == 0x1){
    	    				user.handCard[j]=GDefine.POKER_ARRAY[cardIndex];
    		    			cardFlags &= ~(1<<cardIndex);
    		    			break;
    	    			}
        			}
        		}
	        }
		}
		
		for(int i =0;i<gGsers.length;i++) {
     		if(null ==gGsers[i] || gGsers[i].play_status != GStatus.PLAY) {
     			continue;
     		}
     		
     		GUser user = gGsers[i];
     		squenceId++;
        	send2Access(user,TexasCmd.CMD_SERVER_DEAL_PREFLOP, squenceId, TexasGameServer.dealPreFlop( user.handCard));
	    }
		 
  		step = GStep.PREFLOP;
  		next_option(null);
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
    				cardIndex= rd.nextInt(GDefine.POKER_ARRAY.length);
	    			//判断该位是否被拿取过了,再赋值
	    			if(((cardFlags >>(cardIndex)) & 0x1) == 0x1){
	    				flop[j]=GDefine.POKER_ARRAY[cardIndex];
		    			cardFlags &= ~(1<<cardIndex);
		    			break;
	    			}
    			}
    		}
		}
  		
		squenceId++;
		broadcast(null,TexasCmd.CMD_SERVER_DEAL_FLOP, squenceId, TexasGameServer.dealFlop(flop));
		
		step = GStep.FLOP;
		next_option(null);
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
    				cardIndex= rd.nextInt(GDefine.POKER_ARRAY.length);
	    			//判断该位是否被拿取过了,再赋值
	    			if(((cardFlags >>(cardIndex)) & 0x1) == 0x1){
		    			turn[j]=GDefine.POKER_ARRAY[cardIndex];
		    			cardFlags &= ~(1<<cardIndex);
		    			break;
	    			}
    			}
    		}
		}

		squenceId++;
		broadcast(null,TexasCmd.CMD_SERVER_DEAL_TURN, squenceId, TexasGameServer.dealTrun(turn));
		
  		step = GStep.TRUN;
		next_option(null);
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
    				cardIndex= rd.nextInt(GDefine.POKER_ARRAY.length);
	    			//判断该位是否被拿取过了,再赋值
	    			if(((cardFlags >>(cardIndex)) & 0x1) == 0x1){
	    				river[j]=GDefine.POKER_ARRAY[cardIndex];
		    			cardFlags &= ~(1<<cardIndex);
		    			break;
	    			}
    			}
    		}
		}
  		
		squenceId++;
		broadcast(null,TexasCmd.CMD_SERVER_DEAL_RIVER, squenceId,TexasGameServer.dealRiver(river));
		
  		step = GStep.RIVER;
		next_option(null);
	}
	
	public void action(User mUser,byte[] data, int body_start, int body_length) {
		TexasGameActionRequest action = null;
		try {
			action = TexasGameActionRequest.parseFrom(data, body_start, body_length);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		
		if(action == null) {
			return;
		}
		
		if(action.getSeatId() != action_seatid) {
			return;
		}
		
		//判断操作是否合理
		if((action_set & action.getOperateValue()) <=0) {
			return;
		}
		
		if(action.getChip()>mUser.chip) {
			return;
		}
		
		GUser user = ((GUser)mUser);
		user.operate = action.getOperate();
		user.round_chip += action.getChip();
		user.chip -= action.getChip();
		
		if(user.round_chip > max_round_chip) {
			max_round_chip = user.round_chip;
			max_round_chip_seatid = user.seatId;
		}

		next_option(user);
	}
	
	public void next_option(GUser preActionUser) {
		Logger.v(" step " + step);
		
		//说明是新的一轮开始
		if(null == preActionUser){
			if(step == GStep.PREFLOP){
				
				//小盲大盲强制下
				GUser[] gGsers=(GUser[])users;
				for(int i =0;i<gGsers.length;i++) {
		     		if(null ==gGsers[i] || gGsers[i].play_status != GStatus.PLAY) {
		     			continue;
		     		}
		     		
		     		long user_ante = gGsers[i].chip > ante ? gGsers[i].chip : ante;
		     		gGsers[i].chip -= user_ante;
		     		ante_all += user_ante;
			    }
				
				gGsers[sb_seatid].round_chip =  gGsers[sb_seatid].chip > bb_chip/2 ? bb_chip/2 : gGsers[sb_seatid].chip;
				gGsers[sb_seatid].chip -= gGsers[sb_seatid].round_chip;
				
				gGsers[bb_seatid].round_chip =  gGsers[bb_seatid].chip > bb_chip/2 ? bb_chip/2 : gGsers[bb_seatid].chip;
				gGsers[bb_seatid].chip -= gGsers[bb_seatid].round_chip;
				
				if(gGsers[bb_seatid].round_chip > gGsers[sb_seatid].round_chip){
					max_round_chip = gGsers[bb_seatid].round_chip;
					max_round_chip_seatid = bb_seatid;
				}else{
					max_round_chip = gGsers[sb_seatid].round_chip;
					max_round_chip_seatid = sb_seatid;
				}
				
				squenceId++;
				broadcast(null,TexasCmd.CMD_SERVER_SB_BB_BET, squenceId,TexasGameServer.sbBbBet(sb_seatid, gGsers[sb_seatid].round_chip, bb_seatid, gGsers[bb_seatid].round_chip));
				
				//翻牌前枪口位开始
				action_seatid = (bb_seatid+1)%mConfig.table_max_user;
				
			}else{
				
				action_seatid = sb_seatid;
			}
		}else{
			action_seatid = (action_seatid+1) %mConfig.table_max_user;
		}
		
		//如果只有一个可操作的人，则牌局一直自动走到底
		int bet_user_count = 0;
		GUser[] gGsers=(GUser[])users;
		for(int i = 0 ;i<this.mConfig.table_max_user;i++){
     		if(null ==gGsers[i] || gGsers[i].play_status != GStatus.PLAY ) {
     			continue;
     		}
     		
     		if(gGsers[i].chip>0) {
     			bet_user_count++;
     		}
		}
		
		if(bet_user_count == 1) {
			nextStep();
			return;
		}else{
			//寻找下一个操作seatid
		    int next_seatId_index = action_seatid;
			for(int i = 0 ;i<this.mConfig.table_max_user -1;i++){
	        		int r_next_seatId_index = (next_seatId_index + i)%this.mConfig.table_max_user;
		     		if(null ==gGsers[r_next_seatId_index] 
		     				|| gGsers[r_next_seatId_index].play_status != GStatus.PLAY 
		     				|| gGsers[r_next_seatId_index].operate !=Operate.FOLD
		     				|| gGsers[r_next_seatId_index].chip > 0) {
		     			continue;
		     		}
		    		if(gGsers[r_next_seatId_index].round_chip < max_round_chip) {
		    			action_seatid = r_next_seatId_index;
		    			break;
		    		}else if(r_next_seatId_index == max_round_chip_seatid){//说明大家下注额是一样了，进入下一圈
		    			nextStep();
		    			return;
		    		}
	        }
			
			op_call_chip = max_round_chip;
			op_min_raise_chip = Math.min(max_round_chip *2,gGsers[action_seatid].chip);
			op_max_raise_chip = gGsers[action_seatid].chip;

			squenceId++;
			broadcast(null,TexasCmd.CMD_SERVER_BROADCAST_WHO_ACTION_WAHT, squenceId, TexasGameServer.broadcastUserAction(preActionUser,max_round_chip,action_seatid,op_min_raise_chip,op_max_raise_chip,op_call_chip));
		}
	}
	
	private void nextStep(){
		if(step == GStep.PREFLOP) {
			dealFlop();
		}else if(step == GStep.FLOP) {
			dealTrun();
		}else if(step == GStep.TRUN) {
			dealRiver();
			return;
		}else if(step == GStep.RIVER) {
			showHands();
		}else{
			stopGame();
		}
	}
	
	public void showHands() {
		squenceId++;
		broadcast(null,TexasCmd.CMD_SERVER_BROADCAST_SHOW_HAND, squenceId, TexasGameServer.showHand(this));
		
		step = GStep.SHOWHAND;
	}
	
	public void stopGame() {
		
		squenceId++;
		broadcast(null,TexasCmd.CMD_SERVER_GAME_END, squenceId, TexasGameServer.stop(this));
		
		//--------------------------------------------------------------------------------
		super.stopGame();
		
		step = GStep.STOP;
		cardFlags |= Long.MAX_VALUE;
		squenceId = 0;
		
		action_seatid = -1;
		action_set = 0;
		
		Arrays.fill(flop, (byte)0);
		Arrays.fill(turn, (byte)0);
		Arrays.fill(river, (byte)0);
	
		max_round_chip = 0;
		max_round_chip_seatid = 0;
	}

	public void resetTable(){
		super.resetTable();
		
		step = GStep.STOP;
		cardFlags |= Long.MAX_VALUE;
		squenceId = 0;
		
		sb_seatid = -1;
		bb_seatid = -1;
		btn_seateId =-1;
		
		action_seatid = -1;
		action_set = 0;
		
		Arrays.fill(flop, (byte)0);
		Arrays.fill(turn, (byte)0);
		Arrays.fill(river, (byte)0);
	
		max_round_chip = 0;
		max_round_chip_seatid = 0;
	}
	

}
