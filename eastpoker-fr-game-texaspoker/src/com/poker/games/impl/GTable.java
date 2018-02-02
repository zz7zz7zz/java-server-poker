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
import com.poker.games.impl.TexasDefine.GStatus;
import com.poker.games.impl.TexasDefine.GStep;
import com.poker.games.impl.config.CardConfig;
import com.poker.games.impl.config.GameConfig;
import com.poker.games.protocols.GBaseCmd;
import com.poker.protocols.TexasCmd;
import com.poker.protocols.TexasGameServer;
import com.poker.protocols.texaspoker.TexasGameActionRequestProto.TexasGameActionRequest;
import com.poker.protocols.texaspoker.TexasGameBroadcastUserActionProto.TexasGameBroadcastUserAction.Operate;


public class GTable extends Table {
	
	public GameConfig mGameConfig;
	public CardConfig mCardConfig;
	
	public long   cardFlags = Long.MAX_VALUE;
	
	public int squenceId = 0;
	
	public long ante;
	public long sb_chip;
	
	public int sb_seatid = -1;
	public int bb_seatid = -1;
	public int btn_seateId =-1;
	
	public byte[] flop=new byte[3];
	public byte[] turn=new byte[1];
	public byte[] river=new byte[1];
	
	public int  op_seatid = -1;
	public int  op_sets = 0;
	public long op_call_chip ;
	public long op_min_raise_chip ;
	public long op_max_raise_chip ;
	
	public long max_round_chip = 0;
	public int  max_round_chip_seatid = 0;
	
	public long ante_all;
	public long sb_force_bet;
	public long bb_force_bet;
	public long pots [] = new long[9];//最多9个pot
	
	public GStep step;
	
	public GTable(Room mRoom,int tableId, Config mConfig,GameConfig mGameConfig,CardConfig mCardConfig) {
		super(mRoom,tableId, mConfig);
		this.mCardConfig = mCardConfig;
		this.ante = mGameConfig.table_ante[0];
		this.sb_chip = mGameConfig.table_blind[0];
	}

	@Override
	protected int onTableUserFirstLogin(User mUser) {
		
		GUser gUser = (GUser)mUser;
		
		//1。对进来的用户广播桌子上有哪些用户
		squenceId++;
		send2Access(mUser,GBaseCmd.CMD_SERVER_USERLOGIN, squenceId, TexasGameServer.userLogin(gUser,this,mGameConfig));
		
		//2.对桌子上的用户广播谁进来类
		squenceId++;
		broadcast(GBaseCmd.CMD_SERVER_BROAD_USERLOGIN, squenceId, TexasGameServer.broadUserLogin(gUser),mUser);
		
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
		broadcast(GBaseCmd.CMD_SERVER_BROAD_USERLOGOUT, squenceId, TexasGameServer.broadUserLogout(mUser),mUser);
		return 0;
	}



	@Override
	protected int onTableUserOffline(User mUser) {
		broadcast(GBaseCmd.CMD_SERVER_BROAD_USERLOGOUT, squenceId, TexasGameServer.broadUserOffline(mUser.uid,mUser.onLineStatus),mUser);
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
			user_request_action((GUser)mUser,data, body_start, body_length);
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
    	
		//强制玩家交Ante,小盲大盲强制下盲注
		for(int i =0;i<gGsers.length;i++) {
     		if(null ==gGsers[i] || gGsers[i].play_status != GStatus.PLAY) {
     			continue;
     		}
     		
     		long user_ante = gGsers[i].chip > ante ? gGsers[i].chip : ante;
     		gGsers[i].chip -= user_ante;
     		ante_all += user_ante;
	    }
		
		gGsers[sb_seatid].round_chip =  gGsers[sb_seatid].chip > sb_chip ? sb_chip : gGsers[sb_seatid].chip;
		gGsers[sb_seatid].chip -= gGsers[sb_seatid].round_chip;
		
		gGsers[bb_seatid].round_chip =  gGsers[bb_seatid].chip > sb_chip*2 ? sb_chip*2 : gGsers[bb_seatid].chip;
		gGsers[bb_seatid].chip -= gGsers[bb_seatid].round_chip;
		
		if(gGsers[bb_seatid].round_chip > gGsers[sb_seatid].round_chip){
			max_round_chip = gGsers[bb_seatid].round_chip;
			max_round_chip_seatid = bb_seatid;
		}else{
			max_round_chip = gGsers[sb_seatid].round_chip;
			max_round_chip_seatid = sb_seatid;
		}
		
		sb_force_bet = gGsers[sb_seatid].round_chip;
		bb_force_bet = gGsers[bb_seatid].round_chip;
		
    	squenceId++;
    	broadcast(null,TexasCmd.CMD_SERVER_GAME_START, squenceId, TexasGameServer.start(sb_seatid, bb_seatid, btn_seateId, ante_all,sb_force_bet,bb_force_bet,this));
    	
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
        				cardIndex= rd.nextInt(TexasDefine.POKER_ARRAY.length);
    	    			//判断该位是否被拿取过了,再赋值
    	    			if(((cardFlags >>(cardIndex)) & 0x1) == 0x1){
    	    				user.handCard[j]=TexasDefine.POKER_ARRAY[cardIndex];
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
    				cardIndex= rd.nextInt(TexasDefine.POKER_ARRAY.length);
	    			//判断该位是否被拿取过了,再赋值
	    			if(((cardFlags >>(cardIndex)) & 0x1) == 0x1){
	    				flop[j]=TexasDefine.POKER_ARRAY[cardIndex];
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
    				cardIndex= rd.nextInt(TexasDefine.POKER_ARRAY.length);
	    			//判断该位是否被拿取过了,再赋值
	    			if(((cardFlags >>(cardIndex)) & 0x1) == 0x1){
		    			turn[j]=TexasDefine.POKER_ARRAY[cardIndex];
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
    				cardIndex= rd.nextInt(TexasDefine.POKER_ARRAY.length);
	    			//判断该位是否被拿取过了,再赋值
	    			if(((cardFlags >>(cardIndex)) & 0x1) == 0x1){
	    				river[j]=TexasDefine.POKER_ARRAY[cardIndex];
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
	
	public void user_request_action(GUser mUser,byte[] data, int body_start, int body_length) {
		
		int ret = 0;
		if(mUser.isFold ){
			ret = -1;
		}else if(mUser.isAllIn){
			ret = -2;
		}else if(!mUser.isPlaying()){
			ret = -3;
		}else if(mUser.seatId != op_seatid){
			ret = -4;
		}else if(table_status == TableStatus.TABLE_STATUS_WAIT){
			ret = -5;
		}else if(table_status == TableStatus.TABLE_STATUS_STOP){
			ret = -6;
		}else{
			
			TexasGameActionRequest action = null;
			try {
				action = TexasGameActionRequest.parseFrom(data, body_start, body_length);
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}
			
			if(action == null) {
				ret = -7;
			}
			
			//判断操作是否合理
			if((op_sets & action.getOperateValue()) <=0) {
				ret =  -8 ;
			}
			
			GUser user = ((GUser)mUser);
			user.operate = action.getOperate();
			long actBetChip = 0;
			switch(action.getOperate()){
			
				case FOLD:
					user.isFold  = true;
					break;
					
				case CHECK:
					break;
					
				case CALL:
					{
						long callChip = max_round_chip - user.round_chip;
						if(user.chip >= callChip){
							user.round_chip += callChip;
							user.chip -= callChip;
						}else{
							user.round_chip += user.chip;
							user.chip = 0;
						}
						actBetChip = user.round_chip;
						user.isAllIn =(user.chip == 0);
					}
					break;
			
				case RAISE:
					{
						long betChip = action.getChip();
						if(betChip <= op_min_raise_chip){
							betChip = op_min_raise_chip;
						}else if(betChip > op_max_raise_chip){
							betChip = op_max_raise_chip;
						}
						if(user.chip >= betChip){
							user.round_chip += betChip;
							user.chip -= betChip;
						}else{
							user.round_chip += user.chip;
							user.chip = 0;
						}
						actBetChip = user.round_chip;
						user.isAllIn =(user.chip == 0);
					}
					break;
					
				default:
					ret =  -9 ;
					break;
			}
			
			if(actBetChip > max_round_chip) {
				max_round_chip = actBetChip;
				max_round_chip_seatid = user.seatId;
			}
			
			squenceId++;
			broadcast(null,TexasCmd.CMD_SERVER_BROADCAST_USER_ACTION, squenceId, TexasGameServer.broadcastUserAction(mUser.seatId,mUser.operate,mUser.chip,actBetChip));
			
			next_option(user);
		}
		
		Logger.v("user_request_action ret " + ret);
	}
	
	public void next_option(GUser preActionUser) {
		Logger.v(" step " + step);
		
		//说明是新的一轮开始
		if(null == preActionUser){
			
			GUser[] gGsers=(GUser[])users;
			for(int i = 0 ;i<this.mConfig.table_max_user;i++){
	     		if(null ==gGsers[i] || gGsers[i].play_status != GStatus.PLAY ) {
	     			continue;
	     		}
	     		
	     		gGsers[i].round_chip = 0;
			}
			
			op_call_chip = 0;
			op_min_raise_chip = Math.min(sb_chip * 2,users[op_seatid].chip);
			op_max_raise_chip = users[op_seatid].chip;
			
			max_round_chip = 0;
			max_round_chip_seatid = -1;
			
			if(step == GStep.PREFLOP){
				//翻牌前枪口位开始
				op_seatid = (bb_seatid+1)%mConfig.table_max_user;
			}else{
				op_seatid = sb_seatid;
			}
		}else{
			op_seatid = (preActionUser.seatId+1) %mConfig.table_max_user;
		}
		
		//如果只有一个可操作的玩家，则牌局一直自动走到底
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
		    int next_seatId_index = op_seatid;
			for(int i = 0 ;i<this.mConfig.table_max_user ;i++){
	        		int r_next_seatId_index = (next_seatId_index + i)%this.mConfig.table_max_user;
	        		
	        		//说明大家下注额是一样了，进入下一圈
	        		if(r_next_seatId_index == max_round_chip_seatid){
		    			nextStep();
		    			return;
		    		}
	        		
		     		//对于不在游戏中，已经弃牌，AllIn的玩家不处理
	        		if(null ==gGsers[r_next_seatId_index] 
		     				|| gGsers[r_next_seatId_index].play_status != GStatus.PLAY 
		     				|| gGsers[r_next_seatId_index].operate ==Operate.FOLD
		     				|| gGsers[r_next_seatId_index].chip <= 0) {
		     			continue;
		     		}
		    		
	        		if(gGsers[r_next_seatId_index].round_chip < max_round_chip) {
		    			op_seatid = r_next_seatId_index;
		    			break;
		    		}
	        }
			
			op_call_chip 	  = max_round_chip;
			op_min_raise_chip = Math.min(max_round_chip *2,gGsers[op_seatid].chip);
			op_max_raise_chip = gGsers[op_seatid].chip;

			squenceId++;
			broadcast(null,TexasCmd.CMD_SERVER_BROADCAST_USER_ACTION, squenceId, TexasGameServer.broadcastNextOperateUser(op_seatid,op_call_chip,op_min_raise_chip,op_max_raise_chip));
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
		
		op_seatid = -1;
		op_sets = 0;
		
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
		
		op_seatid = -1;
		op_sets = 0;
		
		Arrays.fill(flop, (byte)0);
		Arrays.fill(turn, (byte)0);
		Arrays.fill(river, (byte)0);
	
		max_round_chip = 0;
		max_round_chip_seatid = 0;
	}
	

}
