package com.poker.games.impl.config;

import java.util.Arrays;
import java.util.HashMap;

import com.open.net.client.utils.CfgParser;
import com.open.net.client.utils.TextUtils;
import com.poker.games.impl.CardUtil;



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
	public byte[] turn;
	public byte[] river;
	
    
    //解析文件配置参数
    public final void initFileConfig(String config_path) {
        HashMap<String,Object> map = CfgParser.parseToMap(config_path);
        initFileConfig(map);
    }
    
    //-------------------------------------------------------------------------------------------
    protected void initFileConfig(HashMap<String,Object> map){
    	if(null !=map){
    		
    		String[] val = CfgParser.getStringArray(map,"Card","user0");
    		if(null != val){
        		user0 = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user0[i] = s2int(val[i]);
                }
    		}

    		val = CfgParser.getStringArray(map,"Card","user1");
    		if(null != val){
        		user1 = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user1[i] = s2int(val[i]);
                }
    		}

    		 
    		val = CfgParser.getStringArray(map,"Card","user2");
    		if(null != val){
    			user2 = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user2[i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","user3");
    		if(null != val){
    			user3 = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user3[i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","user4");
    		if(null != val){
    			user4 = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user4[i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","user5");
    		if(null != val){
    			user5 = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user5[i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","user6");
    		if(null != val){
    			user6 = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user6[i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","user7");
    		if(null != val){
    			user7 = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user7[i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","user8");
    		if(null != val){
    			user8 = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user8[i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","flop");
    		if(null != val){
    			flop = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	flop[i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","turn");
    		if(null != val){
    			turn = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	turn[i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","river");
    		if(null != val){
    			river = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	river[i] = s2int(val[i]);
                }
    		}

       }
    }

	public byte s2int(String str){
		if(!TextUtils.isEmpty(str)){
			try{
				return Byte.valueOf(str.replace("0x","").replace("0X",""),16);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return "CardConfig [user0=" + Arrays.toString(user0) + ", user1=" + Arrays.toString(user1) + ", user2="
				+ Arrays.toString(user2) + ", user3=" + Arrays.toString(user3) + ", user4=" + Arrays.toString(user4)
				+ ", user5=" + Arrays.toString(user5) + ", user6=" + Arrays.toString(user6) + ", user7="
				+ Arrays.toString(user7) + ", user8=" + Arrays.toString(user8) + ", flop=" + Arrays.toString(flop)
				+ ", turn=" + Arrays.toString(turn) + ", river=" + Arrays.toString(river) + "]";
	}   
	
	public static void main(String arg[]) {
		CardConfig mCardConfig = new CardConfig();
		mCardConfig.initFileConfig("./conf-game/card.config");
		
		System.out.println(mCardConfig);
		
		System.out.println(String.format("♠\u2660 ♥\u2665 ♣\u2663  ♦\u2666", 6));
		System.out.println(CardUtil.formatCard((byte)0x0e)+CardUtil.formatCard((byte)0x1b));
	}
}
