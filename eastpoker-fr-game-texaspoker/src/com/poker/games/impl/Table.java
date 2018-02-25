package com.poker.games.impl;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.util.log.Logger;
import com.poker.cmd.BaseGameCmd;
import com.poker.cmd.Cmd;
import com.poker.cmd.Cmd.ICmdRecognizer;
import com.poker.common.config.Config;
import com.poker.games.Room;
import com.poker.games.AbsTable;
import com.poker.games.define.GameDefine.LoginResult;
import com.poker.games.define.GameDefine.LogoutResult;
import com.poker.games.define.GameDefine.TableStatus;
import com.poker.games.impl.config.CardConfig;
import com.poker.games.impl.config.GameConfig;
import com.poker.games.impl.define.TexasDefine;
import com.poker.games.impl.define.TexasUtil;
import com.poker.games.impl.define.TexasDefine.GameStep;
import com.poker.games.impl.define.TexasDefine.Operate;
import com.poker.games.impl.define.TexasDefine.Pot;
import com.poker.games.impl.define.TexasDefine.PotComparator;
import com.poker.games.impl.define.TexasDefine.TimerId;
import com.poker.protocols.TexasCmd;
import com.poker.protocols.TexasGameServer;
import com.poker.protocols.server.ErrorServer;
import com.poker.protocols.texaspoker.TexasGameActionRequestProto.TexasGameActionRequest;


public class Table extends AbsTable {
	
	public static GameConfig mGameConfig;
	public static CardConfig mCardConfig;
	
	//-----------------------------------------------------
	public final User[] users;
	public final User[] onLookers = new User[10];
	public int count;
	
	//-----------------------------------------------------
	public String game_sequence_id ;//由gameId-gamelevel-gameserverid-timestamp
	public long cardFlags = Long.MAX_VALUE;
	
	public int sequence_id = 0;
	
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
	
	public GameStep step = GameStep.STOP;
	public ArrayList<Pot> potList = new ArrayList<Pot>();
	private PotComparator mPotComparator = new PotComparator();
	
	public Table(Room mRoom,int tableId, Config mConfig) {
		super(mRoom,tableId, mConfig);
		
		users = new User[mConfig.table_max_user];
		count=0;
		
		if(null == mGameConfig){
			mGameConfig = new GameConfig();
			mGameConfig.initFileConfig("./conf-game/game.config");
		}

		if(null == mCardConfig){
			mCardConfig = new CardConfig();
			mCardConfig.initFileConfig("./conf-game/card.config");
			
	        Cmd.AddCmdRecognizer(new ICmdRecognizer() {
				@Override
				public String getCmdString(int cmd) {
					return TexasCmd.getCmdString(cmd);
				}
			});
		}

		this.ante = mGameConfig.table_ante[0];
		this.sb_chip = mGameConfig.table_blind[0];
	}

	@Override
	public LoginResult onUserLogin(User mUser){
		LoginResult ret= userlogin(mUser);
		if(ret == LoginResult.LOGIN_SUCCESS){
			onFirstLogin(mUser);
		}else if(ret == LoginResult.LOGIN_SUCCESS_ALREADY_EXIST){
			onReLogin(mUser);
		}else if(ret == LoginResult.LOGIN_FAILED_FULL){
			sendToUser(BaseGameCmd.CMD_SERVER_LOGIN_ERR,++sequence_id,ErrorServer.error(BaseGameCmd.ERR_CODE_LOGIN_TABLE_FULL,""),mUser);
	    	printLog(game_sequence_id, sequence_id, BaseGameCmd.CMD_SERVER_LOGIN_ERR, TexasGameServer.DEBUG_LOG);
		}
		return ret;
	};
	
	@Override
	public int onUserReady(User mUser){
		if(userReady(mUser) == 1){
			broadcastToUser(BaseGameCmd.CMD_SERVER_BROAD_USERREADY, ++sequence_id, TexasGameServer.broadUserReady(mUser),mUser);
			printLog(game_sequence_id, sequence_id, BaseGameCmd.CMD_SERVER_BROAD_USERREADY, TexasGameServer.DEBUG_LOG);
			return 1;
		}
		return 0;
	};
	
	@Override
	public LogoutResult onUserExit(User mUser){
		LogoutResult ret= userExit(mUser);
		if(ret ==  LogoutResult.LOGOUT_SUCCESS){
			broadcastToUser(BaseGameCmd.CMD_SERVER_BROAD_USERLOGOUT, ++sequence_id, TexasGameServer.broadUserLogout(mUser),mUser);
			printLog(game_sequence_id, sequence_id, BaseGameCmd.CMD_SERVER_BROAD_USERLOGOUT, TexasGameServer.DEBUG_LOG);
		}
		return ret;
	};
	
	@Override
	public int getUserCount(){
		return count;
	}
	
	@Override
	public int updateOnLineStatus(User mUser,boolean isOnLine){
		//两个状态不一致
		if(mUser.isOnLine() != isOnLine){
			mUser.setOnLine(isOnLine);
			broadcastToUser(BaseGameCmd.CMD_SERVER_BROAD_USEROFFLINE, ++sequence_id, TexasGameServer.broadUserOffline(mUser.uid,mUser.isOnLine()),mUser);
			printLog(game_sequence_id, sequence_id, BaseGameCmd.CMD_SERVER_BROAD_USEROFFLINE, TexasGameServer.DEBUG_LOG);
			return 1;
		}

		return 0;
	};
	
	@Override
	public int onKickUser(User mUser , User kickedUser){
		return -1;
	};
	
	@Override
	public boolean isUserInTable(User user){
		for (int i = 0; i < users.length; i++) {
			if(null != users[i] && users[i].uid == user.uid){
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected int sendToUser(int cmd, int squenceId, byte[] body, User user) {
		if(user.isOnLine()){
			sendToClient(cmd,squenceId,body,user);
			return 1;
		}
		return 0;
	}
	
	@Override
	protected int broadcastToUser(int cmd, int squenceId, byte[] body, User user) {
		int ret = 0;
		for(int i =0 ;i<users.length;i++){
			User mUser = (User) users[i];
			if(null != mUser && mUser != user && mUser.isOnLine()){
				sendToClient(cmd,squenceId,body,mUser);
				ret++;
			}
		}
		return ret;
	}
	
	@Override
	public int dispatchTableMessage(User mUser,int cmd, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {
		if(cmd == TexasCmd.CMD_CLIENT_ACTION) {
			user_request_action((User)mUser,data, body_start, body_length);
		}
		return 0;
	}
	
	//----------------------------------------
	public LoginResult userlogin(User user){
		for (User u : users) {
			if(null != u && u.uid == user.uid){
				return LoginResult.LOGIN_SUCCESS_ALREADY_EXIST;
			}
		}
		
		for(int i = 0;i<users.length;i++){
			if(null == users[i]){
				user.seatId = i;
				users[i] = user;
				count++;
				return LoginResult.LOGIN_SUCCESS;
			}
		}
		return LoginResult.LOGIN_FAILED_FULL;
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
	
	protected int onFirstLogin(User mUser) {
		
		int userCount = getUserCount();
		
		//每次进来桌子只有一个用户时，产生牌局id
		if(userCount == 1){
			this.sequence_id = 0;
			this.game_sequence_id = String.format("%d-%d-%d-%s", mGameConfig.game_id,mGameConfig.level,super.mConfig.server_id,new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis())));
		}
		
		//1。对进来的用户广播桌子上有哪些用户
		sendToUser(BaseGameCmd.CMD_SERVER_USERLOGIN, ++sequence_id, TexasGameServer.userLogin(mUser,this,mGameConfig),mUser);
		printLog(game_sequence_id, sequence_id, BaseGameCmd.CMD_SERVER_USERLOGIN, TexasGameServer.DEBUG_LOG);
		
		//2.对桌子上的用户广播谁进来类
		broadcastToUser(BaseGameCmd.CMD_SERVER_BROAD_USERLOGIN, ++sequence_id, TexasGameServer.broadUserLogin(mUser),mUser);
		printLog(game_sequence_id, sequence_id, BaseGameCmd.CMD_SERVER_BROAD_USERLOGIN, TexasGameServer.DEBUG_LOG);
		
		//3.判断游戏谁否可以开始了
		if(table_status == TableStatus.TABLE_STATUS_PLAY){
			return 0;
		}
		
		if(userCount>=this.mConfig.table_min_user) {
			readyToStartGame();
		}
		
		return 0;
	}

	protected int onReLogin(User mUser) {
		if(table_status == TableStatus.TABLE_STATUS_PLAY){//处于游戏中，表示重连
			sendToUser(TexasCmd.CMD_SERVER_RECONNECT, ++sequence_id, TexasGameServer.reconnect(this,(User)mUser,mGameConfig),mUser);
			printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_RECONNECT, TexasGameServer.DEBUG_LOG);
			return 0;
		}else{//游戏暂停中，直接返回登录即可
			sequence_id++;
			sendToUser(BaseGameCmd.CMD_SERVER_USERLOGIN, ++sequence_id, TexasGameServer.userLogin((User)mUser,this,mGameConfig),mUser);
			printLog(game_sequence_id, sequence_id, BaseGameCmd.CMD_SERVER_USERLOGIN, TexasGameServer.DEBUG_LOG);
		}
		return 0;
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
			user.seatId = new_seatId;
		}else{
			users[new_seatId] = user;
			user.seatId = new_seatId;
			count++;
		}
		return 1;
	}

	//-------------------------------------------------------
	public void readyToStartGame() {

        //1.设置每个玩家的游戏状态
		int play_user_count = 0;
        for(int i =0;i<users.length;i++) {
    		if(null ==users[i]) {
    			continue;
    		}
    		users[i].startGame();
    		users[i].chip = this.mConfig.table_init_chip;
    		play_user_count++;
        }
        
        if(play_user_count<this.mConfig.table_min_user){
//        	stopGame();
        	supendGame();
        	return;
        }
        
        startGame();
	}
	
	public void startGame(){
		super.startGame();
		
		//设置开始时的金币，方便结束时进行结算
        for(int i =0;i<users.length;i++) {
    		if(null ==users[i] || !users[i].isPlaying()) {
    			continue;
    		}
    		users[i].originalChip = users[i].chip;
        }
        
		 //2.找出按钮位 , 小盲，大盲位
        //2.1找Button位
        int next_seatId_index = 0 ;
        if(btn_seateId == -1){//说明是游戏刚开始，没有持续
			long t = System.currentTimeMillis();//获得当前时间的毫秒数
	        Random rd = new Random(t);//作为种子数传入到Random的构造器中
	        next_seatId_index = rd.nextInt(this.mConfig.table_max_user);
        }else{//说明是游戏持续
        	next_seatId_index = (btn_seateId+1)%this.mConfig.table_max_user;
        }
    	if(null == users[next_seatId_index]){
        	for(int i = 1 ;i<this.mConfig.table_max_user;i++){
        		int r_next_seatId_index = (next_seatId_index +i) %this.mConfig.table_max_user;
            	if(null != users[r_next_seatId_index]){
            		next_seatId_index = r_next_seatId_index;
            		break;
            	}
        	}
    	}
    	btn_seateId = next_seatId_index;
    	
        //2.2找小盲位
    	next_seatId_index = (btn_seateId+1)%this.mConfig.table_max_user;
        if(null == users[next_seatId_index]){
        	for(int i = 1 ;i<this.mConfig.table_max_user;i++){
        		int r_next_seatId_index = (next_seatId_index + i)%this.mConfig.table_max_user;
            	if(null != users[r_next_seatId_index]){
            		next_seatId_index = r_next_seatId_index;
            		break;
            	}
        	}
        }
        sb_seatid = next_seatId_index;
        		
       //2.3找大盲位
    	next_seatId_index = (sb_seatid+1)%this.mConfig.table_max_user;
    	if(null == users[next_seatId_index]){
        	for(int i = 1 ;i<this.mConfig.table_max_user;i++){
        		int r_next_seatId_index = (next_seatId_index + i)%this.mConfig.table_max_user;
            	if(null != users[r_next_seatId_index]){
            		next_seatId_index = r_next_seatId_index;
            		break;
            	}
        	}
    	}
    	bb_seatid = next_seatId_index;
    	

		//3.强制玩家交Ante,小盲大盲强制下盲注
		for(int i =0;i<users.length;i++) {
     		if(null ==users[i] || !users[i].isPlaying()) {
     			continue;
     		}
     		
     		long user_ante = users[i].chip > ante ? ante : users[i].chip;
     		users[i].chip -= user_ante;
     		ante_all += user_ante;
	    }
		
		users[sb_seatid].round_chip =  users[sb_seatid].chip > sb_chip ? sb_chip : users[sb_seatid].chip;
		users[sb_seatid].chip -= users[sb_seatid].round_chip;
		users[sb_seatid].isAllIn = (users[sb_seatid].chip == 0);
		
		users[bb_seatid].round_chip =  users[bb_seatid].chip > sb_chip*2 ? sb_chip*2 : users[bb_seatid].chip;
		users[bb_seatid].chip -= users[bb_seatid].round_chip;
		users[bb_seatid].isAllIn = (users[bb_seatid].chip == 0);
		
		if(users[bb_seatid].round_chip > users[sb_seatid].round_chip){
			max_round_chip = users[bb_seatid].round_chip;
			max_round_chip_seatid = bb_seatid;
		}else{
			max_round_chip = users[sb_seatid].round_chip;
			max_round_chip_seatid = sb_seatid;
		}
		
		sb_force_bet = users[sb_seatid].round_chip;
		bb_force_bet = users[bb_seatid].round_chip;
		
    	//4.发送游戏开始数据
    	broadcastToUser(TexasCmd.CMD_SERVER_GAME_START, ++sequence_id, TexasGameServer.gameStart(btn_seateId,sb_seatid, bb_seatid, ante_all,sb_force_bet,bb_force_bet,this),null);
    	printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_GAME_START, TexasGameServer.DEBUG_LOG);
    	
  		step = GameStep.START;
  		
  		nextStep(false,false);
	}
	
	public void dealPreFlop() {
        
		if(mCardConfig.isEnable) {
	        for(int i =0;i<users.length;i++) {
        		if(null ==users[i] || !users[i].isPlaying()) {
        			continue;
        		}
        		
        		User user = users[i];
        		for(int j=0;j<user.handCard.length;j++) {
        			user.handCard[j]=mCardConfig.user_cards[i][j];
        		}
	        }
		}else {
	        long t = System.currentTimeMillis();//获得当前时间的毫秒数
	        Random rd = new Random(t);//作为种子数传入到Random的构造器中
	        for(int i =0;i<users.length;i++) {
        		if(null ==users[i] || !users[i].isPlaying()) {
        			continue;
        		}
        		
        		User user = users[i];
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
		
		for(int i =0;i<users.length;i++) {
     		if(null ==users[i] || !users[i].isPlaying()) {
     			continue;
     		}
     		
     		User user = users[i];

     		sendToUser(TexasCmd.CMD_SERVER_DEAL_PREFLOP, ++sequence_id, TexasGameServer.dealPreFlop( user.handCard),user);
        	printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_DEAL_PREFLOP, TexasGameServer.DEBUG_LOG);
	    }
		 
  		step = GameStep.PREFLOP;
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
  		
		broadcastToUser(TexasCmd.CMD_SERVER_DEAL_FLOP, ++sequence_id, TexasGameServer.dealFlop(flop),null);
    	printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_DEAL_FLOP, TexasGameServer.DEBUG_LOG);
    	
		step = GameStep.FLOP;
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

		broadcastToUser(TexasCmd.CMD_SERVER_DEAL_TURN, ++sequence_id, TexasGameServer.dealTrun(turn),null);
    	printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_DEAL_TURN, TexasGameServer.DEBUG_LOG);
    	
  		step = GameStep.TRUN;
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
  		
		broadcastToUser(TexasCmd.CMD_SERVER_DEAL_RIVER, ++sequence_id,TexasGameServer.dealRiver(river),null);
    	printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_DEAL_RIVER, TexasGameServer.DEBUG_LOG);
    	
  		step = GameStep.RIVER;
		next_option(null);
	}
	
	public void user_request_action(User mUser,byte[] data, int body_start, int body_length) {
		
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
			}else{
				//用户的操作在操作之内才可以
				boolean checkAction = (op_sets & action.getOperate())>0;
				if(!checkAction){
					ret = -8;
				}else{
					stopTimer(TimerId.TIMER_ID_USER_ACTION, this);
					ret = user_request_action(mUser,Operate.valueOf(action.getOperate()),action.getChip());
				}
			}
		}
		
		if(ret < 0){
			//发送错误给客户端
			sendToUser(TexasCmd.CMD_SERVER_USER_ERROR, ++sequence_id, TexasGameServer.broadcastUserActionError(mUser.seatId,ret," User Action Error ! "),mUser);
			printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_USER_ERROR, TexasGameServer.DEBUG_LOG);
		}
		
		Logger.v("user_request_action ret " + ret);
	}
	
	public int user_request_action(User mUser,Operate actOperate,long betChip) {
		int ret = 0;
		long actBetChip = 0;
		switch(actOperate){
		
			case FOLD:
				mUser.isFold  = true;
				//如果一个用户弃牌了，从所有的Pot中将其删除，因为他将不参与分Pot
				for(int i= 0;i< potList.size();i++){
					potList.get(i).seatIds.remove((Integer)mUser.seatId);
				}
				break;
				
			case CHECK:
				//如果有人下注，这里是不能进行Check的
				if(max_round_chip > 0 && (max_round_chip- mUser.round_chip) >0){
					ret =  -100 ;
				}
				break;
				
			case CALL:
				{
					long callChip = max_round_chip - mUser.round_chip;
					if(mUser.chip >= callChip){
						mUser.round_chip += callChip;
						mUser.chip -= callChip;
					}else{
						mUser.round_chip += mUser.chip;
						mUser.chip = 0;
					}
					actBetChip = mUser.round_chip;
					mUser.isAllIn =(mUser.chip == 0);
				}
				break;
		
			case RAISE:
				{
					if(betChip <= op_min_raise_chip){
						betChip = op_min_raise_chip;
					}else if(betChip > op_max_raise_chip){
						betChip = op_max_raise_chip;
					}
					if(mUser.chip >= betChip){
						mUser.round_chip += betChip;
						mUser.chip -= betChip;
					}else{
						mUser.round_chip += mUser.chip;
						mUser.chip = 0;
					}
					actBetChip = mUser.round_chip;
					mUser.isAllIn =(mUser.chip == 0);
				}
				break;
				
			default:
				ret =  -101 ;
				break;
		}
		
		//所有条件都符合才算操作成功
		if(ret == 0){
			
			mUser.operate = actOperate;
			
			if(actBetChip > max_round_chip) {
				max_round_chip = actBetChip;
				max_round_chip_seatid = mUser.seatId;
			}
			
			broadcastToUser(TexasCmd.CMD_SERVER_BROADCAST_USER_ACTION, ++sequence_id, TexasGameServer.broadcastUserAction(mUser.seatId,mUser.operate,mUser.chip,actBetChip),null);
			printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_BROADCAST_USER_ACTION, TexasGameServer.DEBUG_LOG);
			
			next_option(mUser);
		}
		
		return ret;
	}
	public void next_option(User preActionUser) {
		Logger.v(" step " + step);
		
		//说明是新的一轮开始
		if(null == preActionUser){
			if(step == GameStep.PREFLOP){
				//翻牌前枪口位开始
				op_seatid = (bb_seatid+1)%mConfig.table_max_user;
			}else{
				op_seatid = sb_seatid;
			}
		}else{
			op_seatid = (preActionUser.seatId+1) %mConfig.table_max_user;
		}
		

		//如果只有一个可操作的玩家，则牌局走到底/结束
		int play_user_count 	= 0;//用户数
		int allin_user_count 	= 0;//allin 用户数
		int fold_user_count 	= 0;//fold 用户数
		int action_user_count 	= 0;//需要继续操作的用户
		
		for(int i = 0 ;i<this.mConfig.table_max_user;i++){
	 		//对于不在游戏中，已经弃牌，AllIn的玩家不处理
	    		if(null ==users[i] || !users[i].isPlaying()) {
	     			continue;
	     		}
				
	    		play_user_count ++;
	    		
				if(users[i].isAllIn) {
					allin_user_count++;
					continue;
				}
	    		
				if(users[i].isFold) {
					fold_user_count++;
					continue;
				}
				
				//本局中 有人 下注，用户下注还未加到最大注，还有筹码可下
				if(max_round_chip > 0 && users[i].round_chip < max_round_chip && users[i].chip>0) {
	     	   		action_user_count++;
	     		}
		}
		
		//case 1: 当所有人都Allin/Fold，游戏结束
		//case 2: 前面的人都Fold，游戏结束
		//case 2: 当前面用户有下注，现在又没有人可以跟注时，游戏结束；
		int rest_op_user_count = play_user_count - allin_user_count - fold_user_count;
		boolean isGameOver =   (rest_op_user_count == 0) 
				|| (rest_op_user_count == 1) && allin_user_count == 0//这里说明没有all的用户，前面都是弃牌的用户
		        || (rest_op_user_count == 1) && action_user_count == 0;//这里说明前面有人Allin，但是没人需要再下注
		
		//说明游戏结束
		if(isGameOver) {
			if(allin_user_count > 0) {//自动走到摊牌阶段
				nextStep(false,true);
			}else {//直接结束游戏
				nextStep(true,true);
			}
			return;
		}

		//寻找下一个操作seatid
	    int next_seatId_index = op_seatid;
		for(int i = 0 ;i<this.mConfig.table_max_user ;i++){
        		int r_next_seatId_index = (next_seatId_index + i)%this.mConfig.table_max_user;
        		
        		//说明大家下注额是一样了，进入下一圈
        		if(r_next_seatId_index == max_round_chip_seatid){
        			
        			//翻牌前大盲位还可以加注的机会
        			if(step == GameStep.PREFLOP){
        				if(max_round_chip_seatid == bb_seatid && users[bb_seatid].chip > 0 && users[bb_seatid].operate == Operate.UNRECOGNIZED){
        					op_seatid = bb_seatid;
        					break;
        				}
        			}
        			
	    			nextStep(false,false);
	    			return;
	    		}
        		
	     		//对于不在游戏中，已经弃牌，AllIn的玩家不处理
        		if(null ==users[r_next_seatId_index] || !users[r_next_seatId_index].isPlaying() || users[r_next_seatId_index].isFold|| users[r_next_seatId_index].isAllIn) {
	     			continue;
	     		}
	    		
        		if(max_round_chip == 0) {
        			op_seatid = r_next_seatId_index;
        			break;
        		}else if(max_round_chip >0 && users[r_next_seatId_index].round_chip < max_round_chip) {
	    			op_seatid = r_next_seatId_index;
	    			break;
	    		}
        }
		
		if(max_round_chip_seatid == -1){
			max_round_chip_seatid = op_seatid;
		}
		op_call_chip 	  = max_round_chip;
		op_min_raise_chip = Math.min(sb_chip *2,users[op_seatid].chip);
		op_max_raise_chip = users[op_seatid].chip;

		//用户没有筹码了
		long needChip = op_call_chip - users[op_seatid].round_chip;
		
		op_sets = Operate.FOLD.getValue();
		
		if(op_call_chip == 0 || (needChip == 0 && step == GameStep.PREFLOP && max_round_chip_seatid == bb_seatid)){
			op_sets |= Operate.CHECK.getValue();
		}
		
		if(needChip >0 && users[op_seatid].chip >0) {
			op_sets |= Operate.CALL.getValue();
		}
		
		if(users[op_seatid].chip > needChip){
			op_sets |= Operate.RAISE.getValue();
		}
		
		broadcastToUser(TexasCmd.CMD_SERVER_BROADCAST_NEXT_OPERATE, ++sequence_id, TexasGameServer.broadcastNextOperateUser(op_seatid,op_sets,op_call_chip,op_min_raise_chip,op_max_raise_chip),null);
		printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_BROADCAST_NEXT_OPERATE, TexasGameServer.DEBUG_LOG);
		
  		startTimer(TimerId.TIMER_ID_USER_ACTION, mGameConfig.timeout_user_action, this);
	}
	
	private void nextStep(boolean isGameOver,boolean isAutoEnd){
		
		//刚开始强制大小盲后，还没有到翻牌前不需要处理Pots
		if(step != GameStep.START){
			dealPots();
		}

		if(isGameOver) {
			stopGame();
			return;
		}
		
		if(step == GameStep.START){
//			dealPreFlop();
			startTimer(TimerId.TIMER_ID_PREFLOP, isAutoEnd ? 1 : mGameConfig.timeout_preflop, this);
		}else if(step == GameStep.PREFLOP) {
//			dealFlop();
			startTimer(TimerId.TIMER_ID_FLOP, isAutoEnd ? 1 : mGameConfig.timeout_flop, this);
		}else if(step == GameStep.FLOP) {
//			dealTrun();
			startTimer(TimerId.TIMER_ID_TRUN, isAutoEnd ? 1 : mGameConfig.timeout_turn, this);
		}else if(step == GameStep.TRUN) {
//			dealRiver();
			startTimer(TimerId.TIMER_ID_RIVER, isAutoEnd ? 1 : mGameConfig.timeout_river, this);
		}else if(step == GameStep.RIVER) {
//			dealShowHands();
			startTimer(TimerId.TIMER_ID_SHOWHAND, isAutoEnd ? 1 : mGameConfig.timeout_showhand, this);
		}else{
//			stopGame();
			startTimer(TimerId.TIMER_ID_CLEARING, isAutoEnd ? 1 : mGameConfig.timeout_clearing, this);
		}
	}
 
	private void dealPots(){
		//如果有人下注才需要处理Pots
		if(max_round_chip >0){
			
			int pot_start_index = potList.size();
			ArrayList<User> round_chip_users=new ArrayList<User>();//有下注金额的用户
			ArrayList<User> share_pot_users=new ArrayList<User>();//参与分Pot用户
			
			User[] gUsers=(User[])users;
			for(int i = 0 ;i<this.mConfig.table_max_user;i++){
				//不在玩的，没有下注的不处理
	     		if(null ==gUsers[i] || !gUsers[i].isPlaying() && gUsers[i].round_chip <= 0) {
	     			continue;
	     		}
	     		round_chip_users.add(gUsers[i]);
	     		if(!gUsers[i].isFold){
	     			share_pot_users.add(gUsers[i]);
	     		}
			}

			int round_chip_user_size= round_chip_users.size();
			int share_pot_user_size = share_pot_users.size();
			//说明下注人数大于1
			if(share_pot_user_size>1){
				//对每个人的按下注额按升序进行排序，看是否要进行分Pot
				Collections.sort(share_pot_users, mPotComparator);
				long min_round_chip = share_pot_users.get(0).round_chip;
				long max_round_chip = share_pot_users.get(share_pot_user_size -1).round_chip;
				
				//大家下注额是一致的，不需要分Pot
				if(min_round_chip == max_round_chip){
					Pot mPot = new Pot();
					mPot.name = step.name();
					mPot.pot_chips = 0;
					for (int i = 0; i < round_chip_user_size; i++) {
						mPot.pot_chips += round_chip_users.get(i).round_chip;
						round_chip_users.get(i).round_chip = 0;
						
						if(!round_chip_users.get(i).isFold) {
							mPot.seatIds.add(round_chip_users.get(i).seatId);		
						}
					}
					potList.add(mPot);
				}else{
					//大家下注额不一致，需要分Pot
					int index = -1;
					while(index<share_pot_user_size-1){
						index++;
						long minRoundChip = share_pot_users.get(index).round_chip;
						if(minRoundChip>0){
							Pot mPot = new Pot();
							mPot.name = step.name();
							mPot.pot_chips = 0;
							for(int i =0;i<round_chip_user_size;i++){
								if(round_chip_users.get(i).round_chip <= 0){
									continue;
								}
								long r_round_chip = Math.min(minRoundChip, round_chip_users.get(i).round_chip);
								mPot.pot_chips += r_round_chip;
								round_chip_users.get(i).round_chip -= minRoundChip;
								
								if(!round_chip_users.get(i).isFold) {
									mPot.seatIds.add(round_chip_users.get(i).seatId);		
								}
							}
							
							potList.add(mPot);
						}
					}
				}
			}else if(share_pot_user_size == 1){//只有一人分Pot
				Pot mPot = new Pot();
				mPot.name = step.name();
				mPot.pot_chips = 0;
				for (int i = 0; i < round_chip_user_size; i++) {
					mPot.pot_chips+= round_chip_users.get(i).round_chip;
					round_chip_users.get(i).round_chip = 0;
					
					if(!round_chip_users.get(i).isFold) {
						mPot.seatIds.add(round_chip_users.get(i).seatId);		
					}
				}

				potList.add(mPot);
				
			}else{

			}
			
			//这一轮有新的下注，将Pot信息发送给客户端
			if(potList.size() > pot_start_index){
				long[] pots = new long[potList.size() - pot_start_index];
				for(int i= pot_start_index;i<potList.size();i++){
					pots[i-pot_start_index] = potList.get(i).pot_chips;
				}

				broadcastToUser(TexasCmd.CMD_SERVER_BROADCAST_POTS, ++sequence_id, TexasGameServer.broadcastPots(pots),null);
				printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_BROADCAST_POTS, TexasGameServer.DEBUG_LOG);
			}
		}

		//清空数据
		for(int i = 0 ;i<this.mConfig.table_max_user;i++){
     		if(null ==users[i]) {
     			continue;
     		}
     		users[i].round_chip = 0;
		}

		op_sets = 0; 
		op_call_chip = 0;
		op_min_raise_chip = 0;
		op_max_raise_chip = 0;
		
		max_round_chip = 0;
		max_round_chip_seatid = -1;
	}
	
	public void dealShowHands() {

		broadcastToUser(TexasCmd.CMD_SERVER_BROADCAST_SHOW_HAND, ++sequence_id, TexasGameServer.showHand(this),null);
		printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_BROADCAST_SHOW_HAND, TexasGameServer.DEBUG_LOG);
		
		step = GameStep.SHOWHAND;
		nextStep(false,false);
	}
	
	//给每个玩家算牌型
	public void calculateCardType(){
		for(int i = 0 ;i<this.mConfig.table_max_user;i++){
     		if(null ==users[i] || !users[i].isPlaying() || users[i].isFold) {
     			continue;
     		}
     		TexasUtil.calculateCardResult(users[i].handCard, flop, turn, river, users[i].result);
		}
	}
	
	//算Pot
	public void calculatePot(){
		
		ArrayList<User> winer = new ArrayList<>();
		
		for(int i = potList.size()-1; i>=0 ; i--){
			Pot mPot = potList.get(i);
			if(mPot.seatIds.size()<=0){
				continue;
			}
			
			winer.clear();
			User user1 = null;
			for (int j = 0; j < mPot.seatIds.size(); j++) {
				User user = (User) users[mPot.seatIds.get(j)];
				if(null == user || !user.isPlaying() || user.isFold){
					continue;
				}
				
				if(null == user1){
					user1 = user;
					winer.add(user);
					continue;
				}
				
				if(user.result.value > user1.result.value){
					winer.clear();
					winer.add(user);
					user1 = user;
				}else if(user.result.value == user1.result.value){
					winer.add(user);
				}
			}
			
			int win_user_size = winer.size();
			if(win_user_size == 0){
				continue;
			}
			
			long average_chip = mPot.pot_chips/win_user_size;//平均的
			long extra_chip = mPot.pot_chips%win_user_size;//多出来的
			for(int j = 0; j < win_user_size; ++j){
				winer.get(j).win_pot_chip += average_chip;
				winer.get(j).chip += average_chip;
			}
			if(extra_chip != 0){
				winer.get(win_user_size-1).win_pot_chip += average_chip;
				winer.get(win_user_size-1).chip += average_chip;
			}
		}
	}
	
	public void calculateWinLose(){
		for(int i = 0 ;i<this.mConfig.table_max_user;i++){
     		if(null ==users[i] || !users[i].isPlaying()) {
     			continue;
     		}
     		users[i].win_chip =  users[i].chip - users[i].originalChip;
		}
	}
	
	public void stopGame() {
		if(step == GameStep.SHOWHAND) {
			calculateCardType();
		}
		calculatePot();
		calculateWinLose();
		
		broadcastToUser(TexasCmd.CMD_SERVER_GAME_OVER, ++sequence_id, TexasGameServer.gameOver(this),null);
		printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_GAME_OVER, TexasGameServer.DEBUG_LOG);
		
		//--------------------------------------------------------------------------------
		super.stopGame();
		
		//更新用户游戏状态，
		for (int i = 0; i < users.length; i++) {
			if(null != users[i]){
				
				users[i].stopGame();
				
				//将不在线的用户/金币不足的用户           踢出去
				if(!users[i].isOnLine() || users[i].chip ==0){
					mRoom.logoutGame(users[i], this);
					users[i] = null;
				}
			}
		}
				
		step = GameStep.STOP;
		cardFlags |= Long.MAX_VALUE;
		sequence_id = 0;
		
		op_seatid = -1;
		op_sets = 0;
		
		Arrays.fill(flop, (byte)0);
		Arrays.fill(turn, (byte)0);
		Arrays.fill(river, (byte)0);
	
		max_round_chip = 0;
		max_round_chip_seatid = 0;
		potList.clear();
		
		TexasGameServer.DEBUG_LOG = null;
		
		stopTimer(TimerId.TIMER_ID_PREFLOP, this);
		stopTimer(TimerId.TIMER_ID_FLOP, this);
		stopTimer(TimerId.TIMER_ID_TRUN, this);
		stopTimer(TimerId.TIMER_ID_RIVER, this);
		stopTimer(TimerId.TIMER_ID_SHOWHAND, this);
		stopTimer(TimerId.TIMER_ID_USER_ACTION, this);
		stopTimer(TimerId.TIMER_ID_NEXT_USER_ACTION, this);
		stopTimer(TimerId.TIMER_ID_POT, this);
		stopTimer(TimerId.TIMER_ID_CLEARING, this);
		stopTimer(TimerId.TIMER_ID_NEW_GAME, this);
		
		//------------------------------------------------------
		startTimer(TimerId.TIMER_ID_NEW_GAME, mGameConfig.timeout_new_game, this);
	}
	
	public void supendGame() {
		stopGame();
		stopTimer(TimerId.TIMER_ID_NEW_GAME, this);
	}

	public void resetTable(){
		super.resetTable();
		
		for (int i = 0; i < users.length; i++) {
			if(null != users[i]){
				users[i].stopGame();
				users[i] = null;
			}
		}
		
		step = GameStep.STOP;
		cardFlags |= Long.MAX_VALUE;
		game_sequence_id = String.format("%d-%d-%d-%s", mGameConfig.game_id,mGameConfig.level,super.mConfig.server_id,new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis())));
		sequence_id = 0;
		
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
		potList.clear();
		
		TexasGameServer.DEBUG_LOG = null;
	}

	@Override
	public void onTimeOut(int timeOutId) {
		super.onTimeOut(timeOutId);
		
		switch(timeOutId){
			case TimerId.TIMER_ID_PREFLOP:
				dealPreFlop();
				break;
				
			case TimerId.TIMER_ID_FLOP:
				dealFlop();
				break;
				
			case TimerId.TIMER_ID_TRUN:
				dealTrun();
				break;
				
			case TimerId.TIMER_ID_RIVER:
				dealRiver();
				break;
				
			case TimerId.TIMER_ID_SHOWHAND:
				dealShowHands();
				break;
				
			case TimerId.TIMER_ID_CLEARING:
				stopGame();
				break;
				
			case TimerId.TIMER_ID_USER_ACTION:
				user_request_action(users[op_seatid],Operate.FOLD,0);
				break;
				
			case TimerId.TIMER_ID_NEXT_USER_ACTION:
				break;
				
			case TimerId.TIMER_ID_POT:
				break;
				
			case TimerId.TIMER_ID_NEW_GAME:
				
				int play_user_count = 0;
		        for(int i =0;i<users.length;i++) {
		    		if(null == users[i]) {
		    			continue;
		    		}
		    		play_user_count++;
		        }
		        if(play_user_count<this.mConfig.table_min_user){
		        	return;
		        }
		        
				startGame();
				break;
		}
	}

	private static StringBuilder logSb = new StringBuilder(256);
	public void printLog(String game_sequence_id,int sequence_id,int cmd ,String data){
		logSb.delete( 0, logSb.length() );
		logSb.append("game_sequence_id ").append(game_sequence_id);
		logSb.append(" sequence_id ").append(sequence_id);
		logSb.append(" cmd 0x").append(Integer.toHexString(cmd));
		logSb.append(" data \n").append(data);

		Logger.v(logSb.toString());
	}
}
