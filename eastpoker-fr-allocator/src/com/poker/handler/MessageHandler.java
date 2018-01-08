package com.poker.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.message.Message;
import com.open.net.client.object.AbstractClient;
import com.poker.data.DataPacket;
import com.poker.protocols.server.GameServerProto;
import com.poker.protocols.server.GameServerProto.GameServer;
import com.poker.protocols.server.GameTableProto.GameTable;


public class MessageHandler {
	
	//game_id -> Servers
	//Servers -> level-sever
	//server -> table
    public HashMap<Integer,LevelGameSer> 		game_server_map = new HashMap<Integer,LevelGameSer>();
    
    public static class LevelGameSer{
    	//level --> GameSer
    	public HashMap<Integer,ArrayList<GameSer>> level_gameser_list = new HashMap<Integer,ArrayList<GameSer>>();
    }
    
    public static class GameSer{
    	
    	public int server_id;
    	public int table_count;
    	public int table_max_user;
    	public Table[] tableList;
    	
    	public int unused_count;
    	public int used_count;//在线人数
    }
    
    public static class Table{
    	public int tid;
    	public int count;//桌子人数
    }
    
	public void on_report_roominfo(AbstractClient mClient , Message msg) throws InvalidProtocolBufferException{
    	
		GameServer mServer = GameServer.parseFrom(msg.data,msg.offset + DataPacket.getHeaderLength(),msg.length - DataPacket.getHeaderLength());

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
	}
	
	public void on_get_roominfo(AbstractClient mClient , Message msg) throws InvalidProtocolBufferException{
    	GameServerProto.GameServer mServer = GameServerProto.GameServer.parseFrom(msg.data,msg.offset + DataPacket.getHeaderLength(),msg.length - DataPacket.getHeaderLength());
    	
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
	
	public void on_update_roominfo(AbstractClient mClient , Message msg) throws InvalidProtocolBufferException{
    	
	}
}
