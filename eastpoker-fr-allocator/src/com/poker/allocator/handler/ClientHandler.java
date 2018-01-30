package com.poker.allocator.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.object.AbstractClient;
import com.open.net.client.object.AbstractClientMessageProcessor;
import com.open.util.log.Logger;
import com.poker.cmd.AllocatorCmd;
import com.poker.cmd.GameCmd;
import com.poker.data.DataPacket;
import com.poker.data.DistapchType;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.packet.PacketTransfer;
import com.poker.protocols.game.GameServerProto;
import com.poker.protocols.game.GameServerProto.GameServer;
import com.poker.protocols.game.GameTableProto.GameTable;
import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;


public class ClientHandler extends AbsClientHandler{
	
	public ClientHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
	}

	@Override
	public void dispatchMessage(AbstractClient client, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {
		try {
    		int cmd   = DataPacket.getCmd(data, header_start);
    		Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + AllocatorCmd.getCmdString(cmd) + " length " + DataPacket.getLength(data,header_start));
    		
        	if(cmd == AllocatorCmd.CMD_GAMESERVER_TO_ALLOCATOR_REPORT_ROOMINFO){
        		on_report_roominfo(client,data,header_start,header_length,body_start,body_length);
        	}else if(cmd == AllocatorCmd.CMD_ALLOCATOR_BROADCAST_GET_ROOMINFO){
        		on_get_roominfo(client,data,header_start,header_length,body_start,body_length);
        	}else if(cmd == AllocatorCmd.CMD_GAMESERVER_TO_ALLOCATOR_UPDATE_ROOMINFO){
        		on_update_roominfo(client,data,header_start,header_length,body_start,body_length);
        	}else if(cmd == AllocatorCmd.CMD_LOGIN_GAME){
        		
        		on_login_game(client, data, body_start, body_length, 1, this);
        	
        	}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	
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
    
	public void on_report_roominfo(AbstractClient mClient , byte[] data, int header_start,int header_length,int body_start,int body_length) throws InvalidProtocolBufferException{
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		mDispatchPacket.getData().copyTo(mInPacket.getPacket(), 0);
		GameServer mServer = GameServer.parseFrom(mInPacket.getPacket(),0,mDispatchPacket.getData().size());
		Logger.v(mServer.toString());
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
	
	public void on_get_roominfo(AbstractClient mClient , byte[] data, int header_start,int header_length,int body_start,int body_length) throws InvalidProtocolBufferException{
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
	
	public void on_update_roominfo(AbstractClient mClient ,byte[] data, int header_start,int header_length,int body_start,int body_length) throws InvalidProtocolBufferException{
    	
	}
	
	public void on_login_game(AbstractClient mClient ,byte[] data, int body_start, int body_length, int squenceId,AbstractClientMessageProcessor sender) throws InvalidProtocolBufferException{
		
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		long uid = mDispatchPacket.getDispatchChainList(0).getUid();
		
		mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());	
		int accessId = mInPacket.readInt();
		int gameId = mInPacket.readInt();
		int matchId = mInPacket.readInt();
		int tableId = mInPacket.readInt();
		
		mOutPacket.begin(squenceId, GameCmd.CMD_LOGIN_GAME);
		mOutPacket.writeInt(accessId);//AccessId
		mOutPacket.writeInt(gameId);//gameId
		mOutPacket.writeInt(matchId);//matchId
		mOutPacket.writeInt(tableId);//tableId
		mOutPacket.writeByte(tableId>0 ?(byte)1:(byte)0);//说明是重连
		mOutPacket.end();
		
		//当InPacket不需要使用时，可以复用buff，防止过多的分配内存，产生内存碎片
		byte[] mTempBuff = mInPacket.getPacket();
		int game_serverId= (tableId>>16);
		int length = PacketTransfer.send2Game(game_serverId,mTempBuff, squenceId, uid,GameCmd.CMD_LOGIN_GAME,DistapchType.TYPE_P2P,mOutPacket.getPacket(),0,  mOutPacket.getLength());
  		send2Dispatch(mTempBuff,0,length);	
	}

}
