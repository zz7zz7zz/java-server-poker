package com.poker.common.config;

import java.util.Arrays;
import java.util.HashMap;

import com.open.net.client.object.UdpAddress;
import com.open.net.server.utils.CfgParser;

public class Config {
    
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
            String val[] = CfgParser.getStringArray(map,"Monitor","net_udp");
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
		return "Config [monitor_net_udp=" + Arrays.toString(monitor_net_udp) + "]";
	}
}
