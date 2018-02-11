package com.poker.games.impl.config;

import java.util.HashMap;

import com.open.net.client.utils.CfgParser;
import com.open.net.client.utils.TextUtils;
import com.poker.games.impl.Table;
import com.poker.games.impl.define.PokerUtil;
import com.poker.games.impl.define.TexasDefine.Result;

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
				sb.append(PokerUtil.toSymbol(cards[i]));
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
		
//		testCardConfig();
		testCardType();
	}
	
//	public static void testCardConfig(String arg[]) {
//		CardConfig mCardConfig = new CardConfig();
//		mCardConfig.initFileConfig("./conf-game/card.config");
//		
//		System.out.println(mCardConfig);
//		
//		System.out.println(String.format("♠\u2660 ♥\u2665 ♣\u2663  ♦\u2666", 6));
//		System.out.println(CardUtil.formatCard((byte)0x0e)+CardUtil.formatCard((byte)0x1b));
//		
//		System.out.println((0x3 >>2) ==1);
//		System.out.println((0x3 >>1) ==1);
//		System.out.println((0x3 >>0 & 01) == 0x1);
//		System.out.println((Integer.toHexString(1<<1)));
//		System.out.println((Long.toHexString(Long.MAX_VALUE)));
//		System.out.println(-1%6);
//	}
	
	public static void testCardType(){
		System.out.println("\n royal_straight_flush");
		royal_straight_flush();

		System.out.println("\n max_straight_flush");
		max_straight_flush();
		
		System.out.println("\n min_straight_flush");
		min_straight_flush();

		System.out.println("\n max_four");
		max_four();
		
		System.out.println("\n min_four");
		min_four();
		
		System.out.println("\n max_full_house");
		max_full_house();
		
		System.out.println("\n min_full_house");
		min_full_house();
		
		System.out.println("\n max_flush");
		max_flush();
		
		System.out.println("\n min_flush");
		min_flush();
		
		System.out.println("\n max_straight");
		max_straight();
		
		System.out.println("\n min_straight");
		min_straight();
		
		System.out.println("\n max_set");
		max_set();
		
		System.out.println("\n min_set");
		min_set();
		
		System.out.println("\n max_two_pair");
		max_two_pair();
		
		System.out.println("\n min_two_pair");
		min_two_pair();
		
		System.out.println("\n max_one_pair");
		max_one_pair();
		
		System.out.println("\n min_one_pair");
		min_one_pair();		
		
		System.out.println("\n max_high");
		max_high();
		
		System.out.println("\n min_high");
		min_high();	
	}
	
	public static void royal_straight_flush(){
		byte[] flop ={0x0a,0x0b,0x0c};
		byte[] turn ={0x02};
		byte[] river ={0x03};
		
		Result result = new Result();
		byte[] hands  = {0x0d,0x0e};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void max_straight_flush(){
		byte[] flop ={0x09,0x0a,0x0b};
		byte[] turn ={0x12};
		byte[] river ={0x13};
		
		Result result = new Result();
		byte[] hands  = {0x0c,0x0d};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void min_straight_flush(){
		byte[] flop ={0x04,0x05,0x0e};
		byte[] turn ={0x12};
		byte[] river ={0x13};
		
		Result result = new Result();
		byte[] hands  = {0x02,0x03};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void max_four(){
		byte[] flop ={0x0e,0x1e,0x2e};
		byte[] turn ={0x12};
		byte[] river ={0x13};
		
		Result result = new Result();
		byte[] hands  = {0x3e,0x0d};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void min_four(){
		byte[] flop ={0x02,0x12,0x22};
		byte[] turn ={0x15};
		byte[] river ={0x13};
		
		Result result = new Result();
		byte[] hands  = {0x32,0x03};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void max_full_house(){
		byte[] flop ={0x0e,0x1e,0x2e};
		byte[] turn ={0x15};
		byte[] river ={0x13};
		
		Result result = new Result();
		byte[] hands  = {0x0d,0x1d};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void min_full_house(){
		byte[] flop ={0x02,0x12,0x22};
		byte[] turn ={0x15};
		byte[] river ={0x17};
		
		Result result = new Result();
		byte[] hands  = {0x33,0x03};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void max_flush(){
		byte[] flop ={0x0e,0x0d,0x0c};
		byte[] turn ={0x15};
		byte[] river ={0x17};
		
		Result result = new Result();
		byte[] hands  = {0x0b,0x09};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void min_flush(){
		byte[] flop ={0x02,0x03,0x04};
		byte[] turn ={0x15};
		byte[] river ={0x17};
		
		Result result = new Result();
		byte[] hands  = {0x05,0x07};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void max_straight(){
		byte[] flop ={0x0e,0x0d,0x0c};
		byte[] turn ={0x15};
		byte[] river ={0x17};
		
		Result result = new Result();
		byte[] hands  = {0x1a,0x1b};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void min_straight(){
		byte[] flop ={0x02,0x03,0x04};
		byte[] turn ={0x25};
		byte[] river ={0x18};
		
		Result result = new Result();
		byte[] hands  = {0x15,0x0e};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void max_set(){
		byte[] flop ={0x0e,0x1e,0x2e};
		byte[] turn ={0x15};
		byte[] river ={0x17};
		
		Result result = new Result();
		byte[] hands  = {0x0d,0x1c};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void min_set(){
		byte[] flop ={0x02,0x12,0x22};
		byte[] turn ={0x25};
		byte[] river ={0x17};
		
		Result result = new Result();
		byte[] hands  = {0x13,0x04};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void max_two_pair(){
		byte[] flop ={0x0e,0x1e,0x2d};
		byte[] turn ={0x15};
		byte[] river ={0x17};
		
		Result result = new Result();
		byte[] hands  = {0x0d,0x1c};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void min_two_pair(){
		byte[] flop ={0x02,0x12,0x23};
		byte[] turn ={0x27};
		byte[] river ={0x16};
		
		Result result = new Result();
		byte[] hands  = {0x13,0x04};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void max_one_pair(){
		byte[] flop ={0x0e,0x1e,0x0d};
		byte[] turn ={0x15};
		byte[] river ={0x17};
		
		Result result = new Result();
		byte[] hands  = {0x0b,0x1c};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void min_one_pair(){
		byte[] flop ={0x02,0x12,0x23};
		byte[] turn ={0x24};
		byte[] river ={0x18};
		
		Result result = new Result();
		byte[] hands  = {0x17,0x05};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void max_high(){
		byte[] flop ={0x0e,0x1d,0x0c};
		byte[] turn ={0x15};
		byte[] river ={0x17};
		
		Result result = new Result();
		byte[] hands  = {0x09,0x1b};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void min_high(){
		byte[] flop ={0x02,0x13,0x24};
		byte[] turn ={0x25};
		byte[] river ={0x19};
		
		Result result = new Result();
		byte[] hands  = {0x17,0x08};
		Table.calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
}
