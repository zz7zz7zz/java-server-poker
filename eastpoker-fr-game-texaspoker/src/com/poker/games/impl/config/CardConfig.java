package com.poker.games.impl.config;

import java.util.HashMap;

import com.open.net.client.utils.CfgParser;



public class CardConfig {

	public byte[] user0;
	public byte[] user1;
	public byte[] user2;
	public byte[] user3;
	public byte[] user4;
	public byte[] user5;
	public byte[] user6;
	public byte[] user7;
	public byte[] user8;
	
	public byte[] flop;
	public byte[] ture;
	public byte[] river;
	
    
    //解析文件配置参数
    public final void initFileConfig(String config_path) {
        HashMap<String,Object> map = CfgParser.parseToMap(config_path);
        initFileConfig(map);
    }
    
    //-------------------------------------------------------------------------------------------
    protected void initFileConfig(HashMap<String,Object> map){
    	if(null !=map){
    		
    		user0 = CfgParser.getByteArray(map,"Card","user0");
    		user1 = CfgParser.getByteArray(map,"Card","user1");
    		user2 = CfgParser.getByteArray(map,"Card","user2");
    		user3 = CfgParser.getByteArray(map,"Card","user3");
    		user4 = CfgParser.getByteArray(map,"Card","user4");
    		user5 = CfgParser.getByteArray(map,"Card","user5");
    		user6 = CfgParser.getByteArray(map,"Card","user6");
    		user7 = CfgParser.getByteArray(map,"Card","user7");
    		user8 = CfgParser.getByteArray(map,"Card","user8");
    		
    		flop = CfgParser.getByteArray(map,"Card","flop");
    		ture = CfgParser.getByteArray(map,"Card","ture");
    		river = CfgParser.getByteArray(map,"Card","river");
       }
    }

    
}
