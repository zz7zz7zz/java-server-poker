package com.poker.games.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CardUtil {
	
	public static String[] colr= {"♠","♥","♣","♦"};
	public static String[] value= {"0","1","2","3","4","5","6","7","8","9","10","J","Q","K","A"};
	
	public static enum TCard{
		NO(0),  //无
		HIGHT(1),  //高牌
		ONE_PAIR(2),//对子
		TWO_PAIR(3),//两对
		SET(4),		//三条
		STRAIGHT(5),//顺子
		FLUSH(6),//同花
		FULL_HOUSE(7),//葫芦
		FOUR(8),//金刚
		STRAIGHT_FLUSH(9),//同花顺
		ROYAL_STRAIGHT_FLUSH(10);//皇家同花顺
		
		private int code;
		TCard(int code){
			this.code = code;
		}
	}
	public static byte getColor(byte card) {
		return (byte) (card>>4);
	}
	
	public static byte getValue(byte card) {
		return (byte) (card & 0x0F);
	}
	
	public static String formatCard(byte card) {
		return colr[getColor(card)]+value[getValue(card)];
	}
	
	public static class Result{
		public TCard cardType;//牌的类型
		public byte[] finalCards=new byte[5];//最好的牌型	
		public int    value;//牌型的值，用于比较牌的大小
		public long   winChip;//输赢情况
		public void clear(){
			cardType = TCard.NO;
			Arrays.fill(finalCards, (byte)0);
			value = 0;
			winChip = 0;
		}
	}
	
	//--------------------------------------------------------
	public static void getCardResult(byte[] hands,byte[] flop,byte[] turn,byte[] river,Result result) {
		
		//花色
		HashMap<Byte,ArrayList<Byte>> color_map = new HashMap<Byte,ArrayList<Byte>>();		
		//花值
		HashMap<Byte,ArrayList<Byte>> value_map = new HashMap<Byte,ArrayList<Byte>>();	
		
		//----------------------花色数目-------------------------
		for(int i = 0;i<hands.length;i++) {
			byte color = getColor(hands[i]);
			byte value = getValue(hands[i]);
			
			ArrayList<Byte> color_cards_list = color_map.get(color);
			if(null == color_cards_list){
				color_cards_list = new ArrayList<>();
				color_map.put(color, color_cards_list);
			}
			color_cards_list.add(hands[i]);
			
			ArrayList<Byte> value_cards_list = value_map.get(value);
			if(null == value_cards_list){
				value_cards_list = new ArrayList<>();
				color_map.put(color, value_cards_list);
			}
			value_cards_list.add(hands[i]);
		}
		
		for(int i = 0;i<flop.length;i++) {
			byte color = getColor(flop[i]);
			byte value = getValue(flop[i]);
			
			ArrayList<Byte> color_cards_list = color_map.get(color);
			if(null == color_cards_list){
				color_cards_list = new ArrayList<>();
				color_map.put(color, color_cards_list);
			}
			color_cards_list.add(flop[i]);
			
			ArrayList<Byte> value_cards_list = value_map.get(value);
			if(null == value_cards_list){
				value_cards_list = new ArrayList<>();
				color_map.put(color, value_cards_list);
			}
			value_cards_list.add(flop[i]);
		}
		
		for(int i = 0;i<turn.length;i++) {
			byte color = getColor(turn[i]);
			byte value = getValue(turn[i]);
			
			ArrayList<Byte> color_cards_list = color_map.get(color);
			if(null == color_cards_list){
				color_cards_list = new ArrayList<>();
				color_map.put(color, color_cards_list);
			}
			color_cards_list.add(turn[i]);
			
			ArrayList<Byte> value_cards_list = value_map.get(value);
			if(null == value_cards_list){
				value_cards_list = new ArrayList<>();
				color_map.put(color, value_cards_list);
			}
			value_cards_list.add(turn[i]);
		}
		
		for(int i = 0;i<river.length;i++) {
			byte color = getColor(river[i]);
			byte value = getValue(river[i]);
			
			ArrayList<Byte> color_cards_list = color_map.get(color);
			if(null == color_cards_list){
				color_cards_list = new ArrayList<>();
				color_map.put(color, color_cards_list);
			}
			color_cards_list.add(river[i]);
			
			ArrayList<Byte> value_cards_list = value_map.get(value);
			if(null == value_cards_list){
				value_cards_list = new ArrayList<>();
				color_map.put(color, value_cards_list);
			}
			value_cards_list.add(river[i]);
		}
		
		//看是否有同花顺
		if(value_map.size()>=5 && color_map.size()<=3){
				for (Entry<Byte, ArrayList<Byte>> entry : color_map.entrySet()) {
					ArrayList<Byte> color_cards_list= entry.getValue();
					if(color_cards_list.size()>=5){
						
						Collections.sort(color_cards_list);
						
						int serial_count = 1;
						int max_serial_count = 1;
						int card_start = 0;
						for(int i =color_cards_list.size()-1;i>0;i--){
							if((color_cards_list.get(i)- color_cards_list.get(i-1)) == 1){
								serial_count++;
								max_serial_count = Math.max(serial_count, max_serial_count);
								if(serial_count == 4 || serial_count == 5){//10,J,Q,K,A ;A,2,3,4,5
									card_start = i-1;
								}
								if(serial_count == 5){
									break;
								}
							}else{
								serial_count =1;
							}
						}
						if(max_serial_count == 4){
							if(color_cards_list.get(card_start)%0x0F == 0x02 && color_cards_list.get(color_cards_list.size()-1)%0x0F == 0x0E){
								result.cardType = TCard.STRAIGHT_FLUSH;
								for(int i = 0;i<4;i++){
									result.finalCards[i] = color_cards_list.get(card_start+i);
								}
								result.finalCards[4] = color_cards_list.get(color_cards_list.size()-1);
								result.value = result.cardType.ordinal()<<20  + 1;
							}
						}else if(max_serial_count == 5){
							result.cardType = TCard.STRAIGHT_FLUSH;
							if(color_cards_list.get(card_start) == 0x0A){
								result.cardType = TCard.ROYAL_STRAIGHT_FLUSH;
							}
							for(int i = 0;i<result.finalCards.length;i++){
								result.finalCards[i] = color_cards_list.get(card_start+i);
							}
							result.value = result.cardType.ordinal()<<20 + result.finalCards[0]%0x0F;
						}else{
							result.cardType = TCard.FLUSH;
							int size = color_cards_list.size();
							for(int i = 0;i<result.finalCards.length;i++){
								result.finalCards[i] = color_cards_list.get(size-5+i);
							}
							result.value = result.cardType.ordinal()<<20 + (result.finalCards[0]%0x0F)<<16 + (result.finalCards[1]%0x0F)<<12+ (result.finalCards[2]%0x0F)<<8 + (result.finalCards[3]%0x0F)<<4 + (result.finalCards[4]%0x0F);
						}
						break;
					}
				}
		}
		
		//1.如果有同花顺以上的牌，则不需要再比牌了
		if(result.cardType == TCard.STRAIGHT_FLUSH || result.cardType == TCard.ROYAL_STRAIGHT_FLUSH){
			//do Nothing
		}else{
			//2.看看是否有金刚
			ArrayList<Pair> tmpList = new ArrayList<Pair>();
			for (Entry<Byte, ArrayList<Byte>> entry : value_map.entrySet()) {
				tmpList.add(new Pair(entry.getKey(),entry.getValue()));
			}
			Collections.sort(tmpList,new PairComparator());
			
			if(tmpList.get(0).cards.size() == 4){
				result.cardType = TCard.FOUR;
				for(int i = 0;i<4;i++){
					result.finalCards[i] = tmpList.get(0).cards.get(i);
				}
				
				byte card = 0;
				for(int i = 0;i < tmpList.size();i++){
					if(tmpList.size() < 4){
						if(card == 0){
							card = tmpList.get(i).cards.get(0);
						}else{
							if(tmpList.get(i).cards.get(0)%0x0F > card%0x0F){
								card = tmpList.get(i).cards.get(0);
							}
						}
					}
				}
				result.finalCards[4] = card;
				result.value = result.cardType.ordinal()<<20 + (result.finalCards[4]%0x0F);
			}
			else if(tmpList.get(0).cards.size() == 3 && tmpList.get(1).cards.size() >=2){
				result.cardType = TCard.FULL_HOUSE;
				result.finalCards[0] = tmpList.get(0).cards.get(0);
				result.finalCards[1] = tmpList.get(0).cards.get(1);
				result.finalCards[2] = tmpList.get(0).cards.get(2);
				result.finalCards[3] = tmpList.get(1).cards.get(0);
				result.finalCards[4] = tmpList.get(1).cards.get(1);
				result.value = result.cardType.ordinal()<<20 + (result.finalCards[4]%0x0F)<<16;
			}else if(result.cardType == TCard.FLUSH){
				//doNothing
			}else {
				//看看能不能成顺子
				int serial_count = 1;
				int max_serial_count = 1;
				int card_start = 0;
				for(int i =tmpList.size()-1;i>0;i--){
					if((tmpList.get(i).color_value- tmpList.get(i-1).color_value) == 1){
						serial_count++;
						max_serial_count = Math.max(serial_count, max_serial_count);
						if(serial_count == 4 || serial_count == 5){//10,J,Q,K,A ;A,2,3,4,5
							card_start = i-1;
						}
						if(serial_count == 5){
							break;
						}
					}else{
						serial_count =1;
					}
				}
				if(max_serial_count == 4){
					if(tmpList.get(card_start).color_value == 0x02 && tmpList.get(tmpList.size()-1).color_value == 0x0E){
						result.cardType = TCard.STRAIGHT;
						for(int i = 0;i<4;i++){
							result.finalCards[i] = tmpList.get(card_start+i).cards.get(0);
						}
						result.finalCards[4] = tmpList.get(tmpList.size()-1).cards.get(0);
						result.value = result.cardType.ordinal()<<20  + 1;
					}
				}else if(max_serial_count == 5){
					result.cardType = TCard.STRAIGHT;
					for(int i = 0;i<result.finalCards.length;i++){
						result.finalCards[i] = tmpList.get(card_start+i).cards.get(0);
					}
					result.value = result.cardType.ordinal()<<20 + result.finalCards[0]%0x0F;
				}
				
				if(result.cardType != TCard.STRAIGHT){
					if(tmpList.get(0).cards.size() == 3){
						result.cardType = TCard.SET;
						result.finalCards[0] = tmpList.get(0).cards.get(0);
						result.finalCards[1] = tmpList.get(0).cards.get(1);
						result.finalCards[2] = tmpList.get(0).cards.get(2);
						result.finalCards[3] = tmpList.get(1).cards.get(0);
						result.finalCards[4] = tmpList.get(2).cards.get(0);
						result.value = result.cardType.ordinal()<<20 + (result.finalCards[3]%0x0F) <<4 + result.finalCards[4]%0x0F;
					}else if(tmpList.get(0).cards.size() == 2 && tmpList.get(1).cards.size() == 2){
						result.cardType = TCard.TWO_PAIR;
						result.finalCards[0] = tmpList.get(0).cards.get(0);
						result.finalCards[1] = tmpList.get(0).cards.get(1);
						result.finalCards[2] = tmpList.get(1).cards.get(0);
						result.finalCards[3] = tmpList.get(1).cards.get(1);
						result.finalCards[4] = tmpList.get(2).cards.get(0);
						result.value = result.cardType.ordinal()<<20 + (result.finalCards[0]%0x0F)<<16 + (result.finalCards[2]%0x0F)<<8 + (result.finalCards[4]%0x0F) ;
					}else if(tmpList.get(0).cards.size() == 2){
						result.cardType = TCard.TWO_PAIR;
						result.finalCards[0] = tmpList.get(0).cards.get(0);
						result.finalCards[1] = tmpList.get(0).cards.get(1);
						result.finalCards[2] = tmpList.get(1).cards.get(0);
						result.finalCards[3] = tmpList.get(2).cards.get(0);
						result.finalCards[4] = tmpList.get(3).cards.get(0);
						result.value = result.cardType.ordinal()<<20 + (result.finalCards[0]%0x0F)<<16 + (result.finalCards[2]%0x0F)<<8 + (result.finalCards[3]%0x0F)<<4 + (result.finalCards[4]%0x0F) ;
					}else{
						result.cardType = TCard.HIGHT;
						result.finalCards[0] = tmpList.get(0).cards.get(0);
						result.finalCards[1] = tmpList.get(1).cards.get(0);
						result.finalCards[2] = tmpList.get(2).cards.get(0);
						result.finalCards[3] = tmpList.get(3).cards.get(0);
						result.finalCards[4] = tmpList.get(4).cards.get(0);
						result.value = result.cardType.ordinal()<<20 + (result.finalCards[0]%0x0F)<<16 + (result.finalCards[1]%0x0F)<<12+ (result.finalCards[2]%0x0F)<<8 + (result.finalCards[3]%0x0F)<<4 + (result.finalCards[4]%0x0F) ;
					}
				}
			}
			
		}

	}
	
	public static class Pair{
		
		public byte color_value;
		public ArrayList<Byte> cards;
		
		public Pair(byte color_value, ArrayList<Byte> cards) {
			super();
			this.color_value = color_value;
			this.cards = cards;
		}
	}
	
	public static class PairComparator implements Comparator<Pair>{
		
		@Override
		public int compare(Pair o1, Pair o2) {
			int ret =  o2.cards.size() - o1.cards.size();
			if(ret == 0){
				return o2.color_value - o1.color_value;
			}
			return ret;
		} 
	}
}
