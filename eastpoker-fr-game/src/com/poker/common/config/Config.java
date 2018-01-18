package com.poker.common.config;

import java.util.Arrays;
import java.util.HashMap;

import com.open.net.client.object.TcpAddress;
import com.open.net.client.object.UdpAddress;
import com.open.net.server.utils.CfgParser;

public class Config {

	//Dispatcher
    public TcpAddress[] dispatcher_net_tcp;
    public UdpAddress[] dispatcher_net_udp;
    
    //Monitor
    public UdpAddress[] monitor_net_udp;
    
	//Allocator
    public int server_id;
	public int game_id;
	public int game_level;
	
    //桌子信息
    public int table_count = 0;                   //一个房间中对桌子数量

    public int table_min_user = 0;                //每台桌子对最少玩家数量，达到这个数量即可开始游戏
    public int table_max_user = 0;                //每台桌子对最大玩家数量
    public long table_min_chip = 0;             //最小进入筹码
    public long table_max_chip = 0;             //最大进入筹码
    public long table_init_chip = 0;             //初始化筹码
    public long[] table_ante;                  	//前注;数组意味着比赛场，如SNG
    public long[] table_blind ;                 //大盲注;数组意味着比赛场，如SNG
    public int[] table_blind_time ;             //每个盲注持续时间;数组意味着比赛场，如SNG

    public int table_action_timeout = 0;        //允许玩家执行动作对最大超时时间
    
    //解析文件配置参数
    public final void initFileConfig(String config_path) {
        HashMap<String,Object> map = CfgParser.parseToMap(config_path);
        initFileConfig(map);
    }
    
    //-------------------------------------------------------------------------------------------
    protected void initFileConfig(HashMap<String,Object> map){
    	if(null !=map){
    		
    		game_id = CfgParser.getInt(map, "Game","game_id");
    		game_level = CfgParser.getInt(map, "Game","game_level");
    		
    		table_count = CfgParser.getInt(map, "Game","table_count");
    		table_min_user = CfgParser.getInt(map, "Game","table_min_user");
    		table_max_user = CfgParser.getInt(map, "Game","table_max_user");
    		table_min_chip = CfgParser.getLong(map, "Game","table_min_chip");
    		table_max_chip = CfgParser.getLong(map, "Game","table_max_chip");
    		table_init_chip = CfgParser.getLong(map, "Game","table_init_chip");
    		
	        String val[]     = CfgParser.getStringArray(map,"Game","table_blind");
	        if(null != val){
	        		table_blind = new long[val.length];
	            for (int i = 0; i < val.length; i++) {
	            		table_blind[i] = Long.valueOf(val[i]);
	            }
	        }
	            
	        val    = CfgParser.getStringArray(map,"Game","table_blind_time");
	        if(null != val){
	        		table_blind_time = new int[val.length];
	            for (int i = 0; i < val.length; i++) {
	            		table_blind_time[i] = Integer.valueOf(val[i]);
	            }
	        }
	        
	        val    = CfgParser.getStringArray(map,"Game","table_ante");
	        if(null != val){
	        	table_ante = new long[val.length];
	            for (int i = 0; i < val.length; i++) {
	            	table_ante[i] = Integer.valueOf(val[i]);
	            }
	        }
        
    		table_action_timeout = CfgParser.getInt(map, "Game","table_action_timeout");
    		
            val     = CfgParser.getStringArray(map,"Dispatcher","net_tcp");
            if(null != val){
            	dispatcher_net_tcp = new TcpAddress[val.length];
                for (int i = 0; i < val.length; i++) {
                    String[] v = val[i].split(":");
                    if(v.length>1){
                    	dispatcher_net_tcp[i]  = new TcpAddress(v[0],Integer.valueOf(v[1]));
                    }
                }
            }
            
            val              = CfgParser.getStringArray(map,"Dispatcher","net_udp");
            if(null != val){
            	dispatcher_net_udp = new UdpAddress[val.length];
                for (int i = 0; i < val.length; i++) {
                    String[] v = val[i].split(":");
                    if(v.length>1){
                    	dispatcher_net_udp[i] = new UdpAddress(v[0],Integer.valueOf(v[1]));
                    }
                }
            }
            
            val              = CfgParser.getStringArray(map,"Monitor","net_udp");
            if(null != val){
            	monitor_net_udp = new UdpAddress[val.length];
                for (int i = 0; i < val.length; i++) {
                    String[] v = val[i].split(":");
                    if(v.length>1){
                    	monitor_net_udp[i] = new UdpAddress(v[0],Integer.valueOf(v[1]));
                    }
                }
            }
       }
    }

	@Override
	public String toString() {
		return "Config [dispatcher_net_tcp=" + Arrays.toString(dispatcher_net_tcp) + ", dispatcher_net_udp="
				+ Arrays.toString(dispatcher_net_udp) + ", monitor_net_udp=" + Arrays.toString(monitor_net_udp)
				+ ", server_id=" + server_id + ", game_id=" + game_id + ", game_level=" + game_level + ", table_count="
				+ table_count + ", table_min_user=" + table_min_user + ", table_max_user=" + table_max_user
				+ ", table_min_chip=" + table_min_chip + ", table_max_chip=" + table_max_chip + ", table_init_chip="
				+ table_init_chip + ", table_ante=" + Arrays.toString(table_ante) + ", table_blind="
				+ Arrays.toString(table_blind) + ", table_blind_time=" + Arrays.toString(table_blind_time)
				+ ", table_action_timeout=" + table_action_timeout + "]";
	}
    
}
