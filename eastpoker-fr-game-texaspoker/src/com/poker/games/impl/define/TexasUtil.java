package com.poker.games.impl.define;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import com.poker.games.impl.define.TexasDefine.CardComparator;
import com.poker.games.impl.define.TexasDefine.Pair;
import com.poker.games.impl.define.TexasDefine.PairComparator;
import com.poker.games.impl.define.TexasDefine.PairComparator2;
import com.poker.games.impl.define.TexasDefine.Result;
import com.poker.games.impl.define.TexasDefine.TCard;

public final class TexasUtil {
	
	public static final void calculateCardResult(byte[] hands,byte[] flop,byte[] turn,byte[] river,Result result) {
		//最大牌型值
		byte max_value = 16;
		//花色
		HashMap<Byte,ArrayList<Byte>> colorMap = new HashMap<Byte,ArrayList<Byte>>();		
		//花值
		HashMap<Byte,ArrayList<Byte>> valueMap = new HashMap<Byte,ArrayList<Byte>>();	
		
		//----------------------花色数目与花色数目-------------------------
		for(int i = 0;i<hands.length;i++) {
			byte color = PokerUtil.getColor(hands[i]);
			byte value = PokerUtil.getValue(hands[i]);
			
			ArrayList<Byte> color_cards_list = colorMap.get(color);
			if(null == color_cards_list){
				color_cards_list = new ArrayList<>();
				colorMap.put(color, color_cards_list);
			}
			color_cards_list.add(hands[i]);
			
			ArrayList<Byte> value_cards_list = valueMap.get(value);
			if(null == value_cards_list){
				value_cards_list = new ArrayList<>();
				valueMap.put(value, value_cards_list);
			}
			value_cards_list.add(hands[i]);
		}
		
		for(int i = 0;i<flop.length;i++) {
			byte color = PokerUtil.getColor(flop[i]);
			byte value = PokerUtil.getValue(flop[i]);
			
			ArrayList<Byte> color_cards_list = colorMap.get(color);
			if(null == color_cards_list){
				color_cards_list = new ArrayList<>();
				colorMap.put(color, color_cards_list);
			}
			color_cards_list.add(flop[i]);
			
			ArrayList<Byte> value_cards_list = valueMap.get(value);
			if(null == value_cards_list){
				value_cards_list = new ArrayList<>();
				valueMap.put(value, value_cards_list);
			}
			value_cards_list.add(flop[i]);
		}
		
		for(int i = 0;i<turn.length;i++) {
			byte color = PokerUtil.getColor(turn[i]);
			byte value = PokerUtil.getValue(turn[i]);
			
			ArrayList<Byte> color_cards_list = colorMap.get(color);
			if(null == color_cards_list){
				color_cards_list = new ArrayList<>();
				colorMap.put(color, color_cards_list);
			}
			color_cards_list.add(turn[i]);
			
			ArrayList<Byte> value_cards_list = valueMap.get(value);
			if(null == value_cards_list){
				value_cards_list = new ArrayList<>();
				valueMap.put(value, value_cards_list);
			}
			value_cards_list.add(turn[i]);
		}
		
		for(int i = 0;i<river.length;i++) {
			byte color = PokerUtil.getColor(river[i]);
			byte value = PokerUtil.getValue(river[i]);
			
			ArrayList<Byte> color_cards_list = colorMap.get(color);
			if(null == color_cards_list){
				color_cards_list = new ArrayList<>();
				colorMap.put(color, color_cards_list);
			}
			color_cards_list.add(river[i]);
			
			ArrayList<Byte> value_cards_list = valueMap.get(value);
			if(null == value_cards_list){
				value_cards_list = new ArrayList<>();
				valueMap.put(value, value_cards_list);
			}
			value_cards_list.add(river[i]);
		}
		
		//看是否有同花顺
		if(valueMap.size()>=5){
				if(colorMap.size()<=3){
					for (Entry<Byte, ArrayList<Byte>> entry : colorMap.entrySet()) {
						ArrayList<Byte> color_cards_list= entry.getValue();
						if(color_cards_list.size()>=5){
							
							Collections.sort(color_cards_list,new CardComparator());
							
							int serial_count = 1;
							int max_serial_count = 1;
							int card_start = 0;
							for(int i = 0;i<color_cards_list.size()-1;i++){
								if((color_cards_list.get(i) - color_cards_list.get(i+1)) == 1){
									serial_count++;
									max_serial_count = Math.max(serial_count, max_serial_count);
									if(serial_count == 5){
										break;
									}
								}else{
									card_start   = i+1;
									serial_count = 1;
								}
							}
							if(max_serial_count == 4 && (color_cards_list.get(0)%max_value == 0x0e && color_cards_list.get(card_start)%max_value == 0x05 && color_cards_list.get(color_cards_list.size()-1)%max_value == 0x02)){
								
								result.cardType = TCard.STRAIGHT_FLUSH;
								for(int i = 0;i<4;i++){
									result.finalCards[i] = color_cards_list.get(card_start+i);
								}
								result.finalCards[4] = color_cards_list.get(0);
								result.value = (result.cardType.getValue()<<20)  + ((result.finalCards[0]%max_value)<<16);
							
							}else if(max_serial_count == 5){
								
								result.cardType = TCard.STRAIGHT_FLUSH;
								if(color_cards_list.get(card_start)%max_value  == 0x0e){
									result.cardType = TCard.ROYAL_STRAIGHT_FLUSH;
								}
								for(int i = 0;i<result.finalCards.length;i++){
									result.finalCards[i] = color_cards_list.get(card_start+i);
								}
								result.value = (result.cardType.getValue()<<20) + ((result.finalCards[0]%max_value)<<16);
							
							}else{
								
								result.cardType = TCard.FLUSH;
								for(int i = 0;i<result.finalCards.length;i++){
									result.finalCards[i] = color_cards_list.get(i);
								}
								result.value = (result.cardType.getValue()<<20) + ((result.finalCards[0]%max_value)<<16) + ((result.finalCards[1]%max_value)<<12)+ ((result.finalCards[2]%max_value)<<8) + ((result.finalCards[3]%max_value)<<4) + ((result.finalCards[4]%max_value));
							
							}
							break;
						}
					}
				}
					
				if(result.cardType == TCard.NO){
					ArrayList<Pair> tmpList = new ArrayList<Pair>();
					for (Entry<Byte, ArrayList<Byte>> entry : valueMap.entrySet()) {
						tmpList.add(new Pair(entry.getKey(),entry.getValue()));
					}
					Collections.sort(tmpList,new PairComparator2());
					
					//看看能不能成顺子
					int serial_count = 1;
					int max_serial_count = 1;
					int card_start = 0;
					for(int i =0;i<tmpList.size()-1;i++){
						if((tmpList.get(i).color_value- tmpList.get(i+1).color_value) == 1){
							serial_count++;
							max_serial_count = Math.max(serial_count, max_serial_count);
							if(serial_count == 5){
								break;
							}
						}else{
							card_start   = i+1;
							serial_count =1;
						}
					}
					
					if(max_serial_count == 4 && (tmpList.get(0).color_value == 0x0e && tmpList.get(card_start).color_value == 0x05 && tmpList.get(tmpList.size()-1).color_value == 0x02)){
						
						result.cardType = TCard.STRAIGHT;
						for(int i = 0;i<4;i++){
							result.finalCards[i] = tmpList.get(card_start+i).cards.get(0);
						}
						result.finalCards[4] = tmpList.get(0).cards.get(0);
						result.value = (result.cardType.getValue()<<20)  + ((result.finalCards[0]%max_value)<<16);
						
					}else if(max_serial_count == 5){
						
						result.cardType = TCard.STRAIGHT;
						for(int i = 0;i<result.finalCards.length;i++){
							result.finalCards[i] = tmpList.get(card_start+i).cards.get(0);
						}
						result.value = (result.cardType.getValue()<<20) + + ((result.finalCards[0]%max_value)<<16);
						
					}
				}
		}
		
		//1.如果有同花顺以上的牌，则不需要再比牌了
		if(result.cardType == TCard.STRAIGHT_FLUSH || result.cardType == TCard.ROYAL_STRAIGHT_FLUSH){
			//do Nothing
		}else{
			//2.看看是否有金刚
			ArrayList<Pair> tmpList = new ArrayList<Pair>();
			for (Entry<Byte, ArrayList<Byte>> entry : valueMap.entrySet()) {
				tmpList.add(new Pair(entry.getKey(),entry.getValue()));
			}
			Collections.sort(tmpList,new PairComparator());
			
			if(tmpList.get(0).cards.size() == 4){
				
				result.cardType = TCard.FOUR;
				for(int i = 0;i<4;i++){
					result.finalCards[i] = tmpList.get(0).cards.get(i);
				}
				result.finalCards[4] = tmpList.get(1).cards.get(0);
				//取一张最大的牌
				for(int i = 2;i<tmpList.size();i++){
					if(tmpList.get(i).cards.get(0)>result.finalCards[4]){
						result.finalCards[4] = tmpList.get(i).cards.get(0);
					}
				}
				
				result.value = (result.cardType.getValue()<<20) +((result.finalCards[0]%max_value)<<16)+ ((result.finalCards[4]%max_value)<<12);
			}
			else if(tmpList.get(0).cards.size() == 3 && tmpList.get(1).cards.size() >=2){
				
				result.cardType = TCard.FULL_HOUSE;
				result.finalCards[0] = tmpList.get(0).cards.get(0);
				result.finalCards[1] = tmpList.get(0).cards.get(1);
				result.finalCards[2] = tmpList.get(0).cards.get(2);
				result.finalCards[3] = tmpList.get(1).cards.get(0);
				result.finalCards[4] = tmpList.get(1).cards.get(1);
				result.value = (result.cardType.getValue()<<20) + ((result.finalCards[0]%max_value)<<16)+ ((result.finalCards[3]%max_value)<<12);
				
			}else if(result.cardType == TCard.FLUSH){
				//doNothing
			}else if(result.cardType == TCard.STRAIGHT){
				//doNothing
			}else {

				if(tmpList.get(0).cards.size() == 3){
					
					result.cardType = TCard.SET;
					result.finalCards[0] = tmpList.get(0).cards.get(0);
					result.finalCards[1] = tmpList.get(0).cards.get(1);
					result.finalCards[2] = tmpList.get(0).cards.get(2);
					result.finalCards[3] = tmpList.get(1).cards.get(0);
					result.finalCards[4] = tmpList.get(2).cards.get(0);
					result.value = (result.cardType.getValue()<<20) + ((result.finalCards[0]%max_value) <<16) + ((result.finalCards[3]%max_value)<<12)+ ((result.finalCards[4]%max_value)<<8);
				
				}else if(tmpList.get(0).cards.size() == 2 && tmpList.get(1).cards.size() == 2){
					
					result.cardType = TCard.TWO_PAIR;
					result.finalCards[0] = tmpList.get(0).cards.get(0);
					result.finalCards[1] = tmpList.get(0).cards.get(1);
					result.finalCards[2] = tmpList.get(1).cards.get(0);
					result.finalCards[3] = tmpList.get(1).cards.get(1);
					result.finalCards[4] = tmpList.get(2).cards.get(0);
					result.value = (result.cardType.getValue()<<20) + ((result.finalCards[0]%max_value)<<16) + ((result.finalCards[2]%max_value)<<12) + ((result.finalCards[4]%max_value)<<8) ;
				
				}else if(tmpList.get(0).cards.size() == 2){
					
					result.cardType = TCard.ONE_PAIR;
					result.finalCards[0] = tmpList.get(0).cards.get(0);
					result.finalCards[1] = tmpList.get(0).cards.get(1);
					result.finalCards[2] = tmpList.get(1).cards.get(0);
					result.finalCards[3] = tmpList.get(2).cards.get(0);
					result.finalCards[4] = tmpList.get(3).cards.get(0);
					result.value = (result.cardType.getValue()<<20) + ((result.finalCards[0]%max_value)<<16) + ((result.finalCards[2]%max_value)<<12) + ((result.finalCards[3]%max_value)<<8) + (result.finalCards[4]%max_value) ;
				
				}else{
					
					result.cardType = TCard.HIGHT;
					result.finalCards[0] = tmpList.get(0).cards.get(0);
					result.finalCards[1] = tmpList.get(1).cards.get(0);
					result.finalCards[2] = tmpList.get(2).cards.get(0);
					result.finalCards[3] = tmpList.get(3).cards.get(0);
					result.finalCards[4] = tmpList.get(4).cards.get(0);
					result.value = (result.cardType.getValue()<<20) + ((result.finalCards[0]%max_value)<<16) + ((result.finalCards[1]%max_value)<<12)+ ((result.finalCards[2]%max_value)<<8) + ((result.finalCards[3]%max_value)<<4) + ((result.finalCards[4]%max_value)) ;
				
				}
			}
		}

	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
	public static void main(String arg[]) {
		testCardType();
	}
	
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void min_four(){
		byte[] flop ={0x02,0x12,0x22};
		byte[] turn ={0x14};
		byte[] river ={0x13};
		
		Result result = new Result();
		byte[] hands  = {0x32,0x03};
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
	public static void min_two_pair(){
		byte[] flop ={0x02,0x12,0x23};
		byte[] turn ={0x27};
		byte[] river ={0x15};
		
		Result result = new Result();
		byte[] hands  = {0x13,0x04};
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
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
		calculateCardResult(hands, flop, turn, river, result);
		
		System.out.println(PokerUtil.toHexString(hands)+PokerUtil.toHexString(flop)+PokerUtil.toHexString(turn)+PokerUtil.toHexString(river));
		System.out.println(PokerUtil.toSymbol(hands)+PokerUtil.toSymbol(flop)+PokerUtil.toSymbol(turn)+PokerUtil.toSymbol(river));
		System.out.println(result.toString());
	}
	
}
