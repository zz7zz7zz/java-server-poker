package com.poker.games.impl.config;

import java.util.HashMap;

import com.open.net.client.utils.CfgParser;
import com.open.net.client.utils.TextUtils;
import com.poker.games.impl.CardUtil;



public class CardConfig {

	public boolean isEnable = false;
	
	public byte[][] user_cards=new byte[9][2];
	
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
    		
    		isEnable = CfgParser.getBoolean(map, "Card","isEnable");
    		
    		String[] val = CfgParser.getStringArray(map,"Card","user0");
    		if(null != val){
        		user_cards[0] = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user_cards[0][i] = s2int(val[i]);
                }
    		}

    		val = CfgParser.getStringArray(map,"Card","user1");
    		if(null != val){
    			user_cards[1] = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user_cards[1][i] = s2int(val[i]);
                }
    		}

    		 
    		val = CfgParser.getStringArray(map,"Card","user2");
    		if(null != val){
    			user_cards[2] = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user_cards[2][i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","user3");
    		if(null != val){
    			user_cards[3] = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user_cards[3][i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","user4");
    		if(null != val){
    			user_cards[4] = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user_cards[4][i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","user5");
    		if(null != val){
    			user_cards[5] = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user_cards[5][i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","user6");
    		if(null != val){
    			user_cards[6] = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user_cards[6][i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","user7");
    		if(null != val){
    			user_cards[7] = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user_cards[7][i] = s2int(val[i]);
                }
    		}
    		
    		val = CfgParser.getStringArray(map,"Card","user8");
    		if(null != val){
    			user_cards[8] = new byte[val.length];
                for (int i = 0; i < val.length; i++) {
                	user_cards[8][i] = s2int(val[i]);
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
	
	public String card2String(byte[] cards) {
		if(null !=cards && cards.length >0) {
			StringBuilder sb = new StringBuilder(12);
			for(int i=0;i<cards.length;i++) {
				sb.append(CardUtil.formatCard(cards[i]));
			}
			return sb.toString();
		}
		return "";
	}
	
	@Override
	public String toString() {
		return "CardConfig [isEnable=" +isEnable+ ",user0=" + card2String(user_cards[0]) + ", user1=" + card2String(user_cards[1]) + ", user2="
				+ card2String(user_cards[2]) + ", user3=" + card2String(user_cards[3]) + ", user4=" + card2String(user_cards[4])
				+ ", user5=" + card2String(user_cards[5]) + ", user6=" + card2String(user_cards[6]) + ", user7="
				+ card2String(user_cards[7]) + ", user8=" + card2String(user_cards[8]) + ", flop=" + card2String(flop)
				+ ", turn=" + card2String(turn) + ", river=" + card2String(river) + "]";
	}   
	
	public static void main(String arg[]) {
		CardConfig mCardConfig = new CardConfig();
		mCardConfig.initFileConfig("./conf-game/card.config");
		
		System.out.println(mCardConfig);
		
		System.out.println(String.format("♠\u2660 ♥\u2665 ♣\u2663  ♦\u2666", 6));
		System.out.println(CardUtil.formatCard((byte)0x0e)+CardUtil.formatCard((byte)0x1b));
	}
}
