package com.poker.allocator.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.poker.cmd.GameCmd;
import com.poker.data.DataPacket;
import com.poker.protocols.game.GameServerProto;
import com.poker.protocols.game.GameServerProto.GameServer;
import com.poker.protocols.game.GameTableProto.GameTable;
import com.poker.allocator.handler.ImplDataTransfer;


public class ClientMessageHandler {
	
	//game_id -> Servers
	//Servers -> level-sever
	//server -> table
    public HashMap<Integer,LevelGameSer> 		game_server_map = new HashMap<Integer,LevelGameSer>();
    
    public static class LevelGameSer{
    	//level --> GameSer
    	public HashMap<Integer,ArrayList<GameSer>> level_gameser_list = new HashMap<Integer,ArrayList<GameSer>>();

		@Override
		public String toString() {
			return "LevelGameSer [level_gameser_list=" + level_gameser_list + "]";
		}
    }
    
    public static class GameSer{
    	
    	public int server_id;
    	public int table_count;
    	public int table_max_user;
    	public Table[] tableList;
    	
    	public int unused_count;
    	public int used_count;//在线人数
		@Override
		public String toString() {
			return "GameSer [server_id=" + server_id + ", table_count=" + table_count + ", table_max_user="
					+ table_max_user + ", tableList=" + Arrays.toString(tableList) + ", unused_count=" + unused_count
					+ ", used_count=" + used_count + "]";
		}
    }
    
    public static class Table{
    	public int tid;
    	public int count;//桌子人数
    	
		@Override
		public String toString() {
			return "Table [tid=" + tid + ", count=" + count + "]";
		}
    }
    
	public void on_report_roominfo(AbstractClient mClient , byte[] data, int body_start, int body_length) throws InvalidProtocolBufferException{
    	
		GameServer mServer = GameServer.parseFrom(data,body_start,body_length);

    	//找gameid->(level-gameSers)
		int game_id = mServer.getGameId();
    	LevelGameSer level_gameser = game_server_map.get(game_id);
		if(null == level_gameser){
			level_gameser = new LevelGameSer();
			game_server_map.put(game_id, level_gameser);
		}
		
    	//找game_level-(gamesers)
		int game_level = mServer.getGameLevel();
		ArrayList<GameSer> gameSers = level_gameser.level_gameser_list.get(game_level);
		if(null == gameSers){
			gameSers = new ArrayList<GameSer>();
			level_gameser.level_gameser_list.put(game_level, gameSers);
		}
		
		//找GameSer
		int server_id = mServer.getServerId();
		GameSer gameSer=null;
		for (int i = 0; i < gameSers.size(); i++) {
			if(gameSers.get(i).server_id == server_id){
				gameSer =gameSers.get(i);
				break;
			}
		}
		
		if(null != gameSer){
			gameSer.server_id = mServer.getServerId();
			gameSer.table_count=mServer.getTableCount();
			gameSer.table_max_user=mServer.getTableMaxUser();
			
			if(null == gameSer.tableList || gameSer.tableList.length != gameSer.table_count){
				gameSer.tableList = new Table[gameSer.table_count];
				for(int i =0;i<gameSer.table_count;i++){
					gameSer.tableList[i] = new Table(); 
				}
			}
			
			gameSer.unused_count = gameSer.table_max_user * gameSer.table_count;
			gameSer.used_count = 0;
			
			for(int i =0;i<gameSer.table_count;i++){
				gameSer.tableList[i].tid = i;
				gameSer.tableList[i].count = 0;
			}
		}else{
			gameSer = new GameSer();
			gameSer.server_id = mServer.getServerId();
			gameSer.table_count=mServer.getTableCount();
			gameSer.table_max_user=mServer.getTableMaxUser();
			
			gameSer.tableList = new Table[gameSer.table_count];
			for(int i =0;i<gameSer.table_count;i++){
				gameSer.tableList[i] = new Table(); 
			}
			
			gameSer.unused_count = gameSer.table_max_user * gameSer.table_count;
			gameSer.used_count = 0;
			
			for(int i =0;i<gameSer.table_count;i++){
				gameSer.tableList[i].tid = i;
				gameSer.tableList[i].count = 0;
			}
			
			gameSers.add(gameSer);
		}
		
		System.out.println(game_server_map);
	}
	
	public void on_get_roominfo(AbstractClient mClient , byte[] data, int body_start, int body_length) throws InvalidProtocolBufferException{
    	GameServerProto.GameServer mServer = GameServerProto.GameServer.parseFrom(data,body_start,body_length);
    	
    	//找gameid->(level-gameSers)
		int game_id = mServer.getGameId();
    	LevelGameSer level_gameser = game_server_map.get(game_id);
		if(null == level_gameser){
			level_gameser = new LevelGameSer();
			game_server_map.put(game_id, level_gameser);
		}
		
    	//找game_level-(gamesers)
		int game_level = mServer.getGameLevel();
		ArrayList<GameSer> gameSers = level_gameser.level_gameser_list.get(game_level);
		if(null == gameSers){
			gameSers = new ArrayList<GameSer>();
			level_gameser.level_gameser_list.put(game_level, gameSers);
		}
		
		//找GameSer
		int server_id = mServer.getServerId();
		GameSer gameSer=null;
		for (int i = 0; i < gameSers.size(); i++) {
			if(gameSers.get(i).server_id == server_id){
				gameSer =gameSers.get(i);
				break;
			}
		}
		
		List<GameTable> tableList= mServer.getTableListList();
		
		if(null != gameSer){
			gameSer.server_id = mServer.getServerId();
			gameSer.table_count=mServer.getTableCount();
			gameSer.table_max_user=mServer.getTableMaxUser();
			
			if(null == gameSer.tableList || gameSer.tableList.length != gameSer.table_count){
				gameSer.tableList = new Table[gameSer.table_count];
				for(int i =0;i<gameSer.table_count;i++){
					gameSer.tableList[i] = new Table(); 
				}
			}
			
			gameSer.unused_count = gameSer.table_max_user * gameSer.table_count;
			gameSer.used_count = 0;
			
			for(int i =0;i<gameSer.table_count;i++){
				gameSer.tableList[i].tid =   tableList.get(i).getTid();
				gameSer.tableList[i].count = tableList.get(i).getCount();
				gameSer.used_count+=gameSer.tableList[i].count;
			}

			gameSer.unused_count = gameSer.unused_count - gameSer.used_count;
		}else{
			gameSer = new GameSer();
			
			gameSer.server_id = mServer.getServerId();
			gameSer.table_count=mServer.getTableCount();
			gameSer.table_max_user=mServer.getTableMaxUser();
			
			gameSer.tableList = new Table[gameSer.table_count];
			for(int i =0;i<gameSer.table_count;i++){
				gameSer.tableList[i] = new Table(); 
			}
			
			gameSer.unused_count = gameSer.table_max_user * gameSer.table_count;
			gameSer.used_count = 0;
			
			for(int i =0;i<gameSer.table_count;i++){
				gameSer.tableList[i].tid =   tableList.get(i).getTid();
				gameSer.tableList[i].count = tableList.get(i).getCount();
				gameSer.used_count+=gameSer.tableList[i].count;
			}
			
			gameSer.unused_count = gameSer.unused_count - gameSer.used_count;
			
			gameSers.add(gameSer);
		}
	}
	
	public void on_update_roominfo(AbstractClient mClient ,byte[] data, int body_start, int body_length) throws InvalidProtocolBufferException{
    	
	}
	
	public void on_login_game(AbstractClient mClient ,byte[] write_buff_dispatcher,byte[] write_buf, byte[] data, int body_start, int body_length, int squenceId,AbstractClientMessageProcessor sender) throws InvalidProtocolBufferException{
		int length = DataPacket.write(write_buf, squenceId, GameCmd.CMD_LOGIN_GAME, (byte)0, data,0,0);
		
		length =  ImplDataTransfer.send2Allocator(write_buff_dispatcher, squenceId, write_buf, 0, length);
		sender.send(mClient, write_buff_dispatcher, 0, length);
	}
}
