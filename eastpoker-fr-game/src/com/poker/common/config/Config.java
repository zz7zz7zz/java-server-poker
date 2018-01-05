package com.poker.common.config;

import java.util.Arrays;
import java.util.HashMap;

import com.open.net.client.object.TcpAddress;
import com.open.net.client.object.UdpAddress;
import com.open.net.server.utils.CfgParser;

public class Config {

	//Allocator
	public int game_id;
	
	//Dispatcher
    public TcpAddress[] dispatcher_net_tcp;
    public UdpAddress[] dispatcher_net_udp;
    
    //Monitor
    public UdpAddress[] monitor_net_udp;
    
    //解析文件配置参数
    public final void initFileConfig(String config_path) {
        HashMap<String,Object> map = CfgParser.parseToMap(config_path);
        initFileConfig(map);
    }
    
    //-------------------------------------------------------------------------------------------
    protected void initFileConfig(HashMap<String,Object> map){
    	if(null !=map){
    		
    		game_id = CfgParser.getInt(map, "Game","game");
    		
            String val[]     = CfgParser.getStringArray(map,"Dispatcher","net_tcp");
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
		return "Config [game_id=" + game_id + ", dispatcher_net_tcp=" + Arrays.toString(dispatcher_net_tcp)
				+ ", dispatcher_net_udp=" + Arrays.toString(dispatcher_net_udp) + ", monitor_net_udp="
				+ Arrays.toString(monitor_net_udp) + "]";
	}
    
}
