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
import com.poker.games.impl.define.TexasDefine.UserStatus;
import com.poker.games.impl.define.TexasDefine.GameStep;
import com.poker.games.impl.define.TexasDefine.Pot;
import com.poker.games.impl.define.TexasDefine.PotComparator;
import com.poker.protocols.TexasCmd;
import com.poker.protocols.TexasGameServer;
import com.poker.protocols.server.ErrorServer;
import com.poker.protocols.texaspoker.TexasGameActionRequestProto.TexasGameActionRequest;
import com.poker.protocols.texaspoker.TexasGameBroadcastUserActionProto.TexasGameBroadcastUserAction.Operate;


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
			sendToUser(BaseGameCmd.CMD_SERVER_LOGIN_ERR,0,ErrorServer.error(BaseGameCmd.ERR_CODE_LOGIN_TABLE_FULL,""),mUser);
		}
		return ret;
	};
	
	@Override
	public int onUserReady(User mUser){
		if(userReady(mUser) == 1){
			broadcastToUser(BaseGameCmd.CMD_SERVER_BROAD_USERREADY, sequence_id, TexasGameServer.broadUserReady(mUser),mUser);
			return 1;
		}
		return 0;
	};
	
	@Override
	public LogoutResult onUserExit(User mUser){
		LogoutResult ret= userExit(mUser);
		if(ret ==  LogoutResult.LOGOUT_SUCCESS){
			broadcastToUser(BaseGameCmd.CMD_SERVER_BROAD_USERLOGOUT, sequence_id, TexasGameServer.broadUserLogout(mUser),mUser);
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
			broadcastToUser(BaseGameCmd.CMD_SERVER_BROAD_USEROFFLINE, sequence_id, TexasGameServer.broadUserOffline(mUser.uid,mUser.isOnLine()),mUser);
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
		}
		return 0;
	}
	
	@Override
	protected int broadcastToUser(int cmd, int squenceId, byte[] body, User user) {
		for(int i =0 ;i<users.length;i++){
			User mUser = (User) users[i];
			if(null != mUser && mUser != user && mUser.isOnLine()){
				sendToClient(cmd,squenceId,body,mUser);
			}
		}
		return 0;
	}
	
	@Override
	public int dispatchTableMessage(User mUser,int cmd, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {
		if(cmd == TexasCmd.CMD_CLIENT_ACTION) {
			user_request_action((User)mUser,data, body_start, body_length);
		}
		return 0;
	}

	@Override
	public int onTimeOut() {
		// TODO Auto-generated method stub
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
		
		//1。对进来的用户广播桌子上有哪些用户
		sequence_id++;
		sendToUser(BaseGameCmd.CMD_SERVER_USERLOGIN, sequence_id, TexasGameServer.userLogin(mUser,this,mGameConfig),mUser);
		
		//2.对桌子上的用户广播谁进来类
		sequence_id++;
		broadcastToUser(BaseGameCmd.CMD_SERVER_BROAD_USERLOGIN, sequence_id, TexasGameServer.broadUserLogin(mUser),mUser);
		
		//3.判断游戏谁否可以开始了
		if(table_status == TableStatus.TABLE_STATUS_PLAY){
			return 0;
		}
		
		if(getUserCount()>=this.mConfig.table_min_user) {
			startGame();
		}
		
		return 0;
	}

	protected int onReLogin(User mUser) {
		if(table_status == TableStatus.TABLE_STATUS_PLAY){//处于游戏中，表示重连
			sequence_id++;
			sendToUser(TexasCmd.CMD_SERVER_RECONNECT, sequence_id, TexasGameServer.reconnect(this,(User)mUser,mGameConfig),mUser);
			return 0;
		}else{//游戏暂停中，直接返回登录即可
			sequence_id++;
			sendToUser(BaseGameCmd.CMD_SERVER_USERLOGIN, sequence_id, TexasGameServer.userLogin((User)mUser,this,mGameConfig),mUser);
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
	public void startGame() {
		super.startGame();
		
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
        	stopGame();
        	return;
        }
        
        game_sequence_id = String.format("%d-%d-%d-%s", mGameConfig.game_id,mGameConfig.level,super.mConfig.server_id,new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis())));
        
        //2.找出按钮位 , 小盲，大盲位
        //2.1找Button位
        int next_seatId_index = 0 ;
        if(btn_seateId == -1){//说明是游戏刚开始，没有持续
			long t = System.currentTimeMillis();//获得当前时间的毫秒数
	        Random rd = new Random(t);//作为种子数传入到Random的构造器中
	        next_seatId_index = rd.nextInt(this.mConfig.table_max_user);
        }else{//说明是游戏持续
        	next_seatId_index = sb_seatid+1;
        }
    	if(null == users[next_seatId_index]){
        	for(int i = 1 ;i<this.mConfig.table_max_user;i++){
        		int r_next_seatId_index = (next_seatId_index - i + this.mConfig.table_max_user)%this.mConfig.table_max_user;
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
     		if(null ==users[i] || users[i].play_status != UserStatus.PLAY) {
     			continue;
     		}
     		
     		long user_ante = users[i].chip > ante ? ante : users[i].chip;
     		users[i].chip -= user_ante;
     		ante_all += user_ante;
	    }
		
		users[sb_seatid].round_chip =  users[sb_seatid].chip > sb_chip ? sb_chip : users[sb_seatid].chip;
		users[sb_seatid].chip -= users[sb_seatid].round_chip;
		
		users[bb_seatid].round_chip =  users[bb_seatid].chip > sb_chip*2 ? sb_chip*2 : users[bb_seatid].chip;
		users[bb_seatid].chip -= users[bb_seatid].round_chip;
		
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
    	sequence_id++;
    	broadcastToUser(TexasCmd.CMD_SERVER_GAME_START, sequence_id, TexasGameServer.gameStart(btn_seateId,sb_seatid, bb_seatid, ante_all,sb_force_bet,bb_force_bet,this),null);
    	printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_GAME_START, "");
    	
  		step = GameStep.START;
  		
  		dealPreFlop();
	}
	
	public void dealPreFlop() {
        
		if(mCardConfig.isEnable) {
	        for(int i =0;i<users.length;i++) {
        		if(null ==users[i] || users[i].play_status != UserStatus.PLAY) {
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
        		if(null ==users[i] || users[i].play_status != UserStatus.PLAY) {
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
     		if(null ==users[i] || users[i].play_status != UserStatus.PLAY) {
     			continue;
     		}
     		
     		User user = users[i];
     		sequence_id++;
     		sendToUser(TexasCmd.CMD_SERVER_DEAL_PREFLOP, sequence_id, TexasGameServer.dealPreFlop( user.handCard),user);
        	printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_DEAL_PREFLOP, "");
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
  		
		sequence_id++;
		broadcastToUser(TexasCmd.CMD_SERVER_DEAL_FLOP, sequence_id, TexasGameServer.dealFlop(flop),null);
    	printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_DEAL_FLOP, "");
    	
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

		sequence_id++;
		broadcastToUser(TexasCmd.CMD_SERVER_DEAL_TURN, sequence_id, TexasGameServer.dealTrun(turn),null);
    	printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_DEAL_TURN, "");
    	
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
  		
		sequence_id++;
		broadcastToUser(TexasCmd.CMD_SERVER_DEAL_RIVER, sequence_id,TexasGameServer.dealRiver(river),null);
    	printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_DEAL_RIVER, "");
    	
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
				User user = ((User)mUser);
				user.operate = action.getOperate();
				long actBetChip = 0;
				switch(action.getOperate()){
				
					case FOLD:
						user.isFold  = true;
						//如果一个用户弃牌了，从所有的Pot中将其删除，因为他将不参与分Pot
						for(int i= 0;i< potList.size();i++){
							potList.get(i).seatIds.remove(user.seatId);
						}
						break;
						
					case CHECK:
						//如果有人下注，这里是不能进行Check的
						if(max_round_chip > 0){
							ret =  -8 ;
							user.operate = Operate.FOLD;
						}
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
				
				sequence_id++;
				broadcastToUser(TexasCmd.CMD_SERVER_BROADCAST_USER_ACTION, sequence_id, TexasGameServer.broadcastUserAction(mUser.seatId,mUser.operate,mUser.chip,actBetChip),user);
				printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_BROADCAST_USER_ACTION, "");
				
				next_option(user);
			}
		}
		
		if(ret <0){
			//发送错误给客户端
		}
		
		Logger.v("user_request_action ret " + ret);
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
		
		//如果只有一个可操作的玩家，则牌局一直自动走到底
		int bet_user_count = 0;
		for(int i = 0 ;i<this.mConfig.table_max_user;i++){
     		//对于不在游戏中，已经弃牌，AllIn的玩家不处理
    		if(null ==users[i] || !users[i].isPlaying() || users[i].isFold|| users[i].isAllIn) {
     			continue;
     		}
     		
     		if(users[i].chip>0) {
     			bet_user_count++;
     		}
		}
		
		if(bet_user_count <= 1) {
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
	        		if(null ==users[r_next_seatId_index] || !users[r_next_seatId_index].isPlaying() || users[r_next_seatId_index].isFold|| users[r_next_seatId_index].isAllIn) {
		     			continue;
		     		}
		    		
	        		if(users[r_next_seatId_index].round_chip < max_round_chip) {
		    			op_seatid = r_next_seatId_index;
		    			break;
		    		}
	        }
			
			if(max_round_chip_seatid == -1){
				max_round_chip_seatid = op_seatid;
			}
			op_call_chip 	  = max_round_chip;
			op_min_raise_chip = Math.min(max_round_chip *2,users[op_seatid].chip);
			op_max_raise_chip = users[op_seatid].chip;

			sequence_id++;
			broadcastToUser(TexasCmd.CMD_SERVER_BROADCAST_NEXT_OPERATE, sequence_id, TexasGameServer.broadcastNextOperateUser(op_seatid,op_call_chip,op_min_raise_chip,op_max_raise_chip),null);
			printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_BROADCAST_NEXT_OPERATE, "");
		}
	}
	
	private void nextStep(){
		
		handPots();
		
		if(step == GameStep.PREFLOP) {
			dealFlop();
		}else if(step == GameStep.FLOP) {
			dealTrun();
		}else if(step == GameStep.TRUN) {
			dealRiver();
		}else if(step == GameStep.RIVER) {
			showHands();
		}else{
			stopGame();
		}
	}
 
	private void handPots(){
		//如果有人下注才需要处理Pots
		if(max_round_chip >=0){
			
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
					for (int i = 0; i < round_chip_user_size; i++) {
						mPot.pot_chips += round_chip_users.get(i).round_chip;
						round_chip_users.get(i).round_chip = 0;
					}
					for (int i = 0; i < share_pot_user_size; i++) {
						mPot.seatIds.add(share_pot_users.get(i).seatId);
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
							}
							
							for(int i =index;i<share_pot_user_size;i++){
								mPot.seatIds.add(share_pot_users.get(i).seatId);
							}
							potList.add(mPot);
						}
					}
				}
			}else if(share_pot_user_size == 1){//只有一人分Pot
				Pot mPot = new Pot();
				mPot.name = step.name();
				for (int i = 0; i < round_chip_user_size; i++) {
					mPot.pot_chips+= round_chip_users.get(i).round_chip;
					round_chip_users.get(i).round_chip = 0;
				}
				mPot.seatIds.add(share_pot_users.get(0).seatId);
				potList.add(mPot);
				
			}else{

			}
			
			//这一轮有新的下注，将Pot信息发送给客户端
			if(potList.size() > pot_start_index){
				long[] pots = new long[potList.size() - pot_start_index];
				for(int i= pot_start_index;i<potList.size();i++){
					pots[i-pot_start_index] = potList.get(i).pot_chips;
				}
				sequence_id++;
				broadcastToUser(TexasCmd.CMD_SERVER_BROADCAST_POTS, sequence_id, TexasGameServer.broadcastPots(pots),null);
				printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_BROADCAST_POTS, "");
			}
		}

		//清空数据
		for(int i = 0 ;i<this.mConfig.table_max_user;i++){
     		if(null ==users[i] || !users[i].isPlaying()) {
     			continue;
     		}
     		users[i].round_chip = 0;
		}

		op_call_chip = 0;
		op_min_raise_chip = 0;
		op_max_raise_chip = 0;
		
		max_round_chip = 0;
		max_round_chip_seatid = -1;
	}
	
	public void showHands() {
		sequence_id++;
		broadcastToUser(TexasCmd.CMD_SERVER_BROADCAST_SHOW_HAND, sequence_id, TexasGameServer.showHand(this),null);
		printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_BROADCAST_SHOW_HAND, "");
		
		step = GameStep.SHOWHAND;
		nextStep();
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
				winer.get(j).win_chip += average_chip;
				winer.get(j).chip += average_chip;
			}
			if(extra_chip != 0){
				winer.get(win_user_size-1).win_chip += average_chip;
				winer.get(win_user_size-1).chip += average_chip;
			}
		}
	}
	
	public void stopGame() {
		
		calculateCardType();
		calculatePot();
		
		sequence_id++;
		broadcastToUser(TexasCmd.CMD_SERVER_GAME_OVER, sequence_id, TexasGameServer.gameOver(this),null);
		printLog(game_sequence_id, sequence_id, TexasCmd.CMD_SERVER_GAME_OVER, "");
		
		//--------------------------------------------------------------------------------
		super.stopGame();
		
		//更新用户游戏状态，
		for (int i = 0; i < users.length; i++) {
			if(null != users[i]){
				
				users[i].stopGame();
				
				//将不在线的用户踢出去
				if(!users[i].isOnLine()){
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
	}
	
	private static StringBuilder logSb = new StringBuilder(256);
	public void printLog(String game_sequence_id,int sequence_id,int cmd ,String data){
		logSb.delete( 0, logSb.length() );
		logSb.append("game_sequence_id ").append(game_sequence_id);
		logSb.append(" sequence_id ").append(sequence_id);
		logSb.append(" cmd 0x").append(Integer.toHexString(cmd));
		logSb.append(" data ").append(data);
		Logger.v(logSb.toString());
	}
}
