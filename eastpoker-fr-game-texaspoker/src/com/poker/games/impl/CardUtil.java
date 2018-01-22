package com.poker.games.impl;

public class CardUtil {
	
	public static String[] colr= {"♠","♥","♣","♦"};
	public static String[] value= {"0","1","2","3","4","5","6","7","8","9","10","J","Q","K","A"};
	
	public static int getColor(byte card) {
		return card>>4;
	}
	
	public static int getValue(byte card) {
		return card & 0x0F;
	}
	
	public static String formatCard(byte card) {
		return colr[getColor(card)]+value[getValue(card)];
	}
	
	//--------------------------------------------------------
	public int getCardResult(byte[] hands,byte[] flop,byte[] trun,byte[] river) {
		
		//----------------------花色数目-------------------------
		int[] color_count = new int[4];
		
		for(int i = 0;i<hands.length;i++) {
			color_count[getColor(hands[i])]++;
		}
		
		for(int i = 0;i<flop.length;i++) {
			color_count[getColor(flop[i])]++;
		}
		
		for(int i = 0;i<trun.length;i++) {
			color_count[getColor(trun[i])]++;
		}
		
		for(int i = 0;i<river.length;i++) {
			color_count[getColor(river[i])]++;
		}
		
		//----------------------花色值数目-------------------------
		int[] color_value = new int[15];
		int   color_value_count = 0;//花色值的不同数目
		
		for(int i = 0;i<hands.length;i++) {
			int value = getValue(hands[i]);
			color_value[value]++;
			if(value ==1) {
				color_value_count++;
			}
		}
		
		for(int i = 0;i<flop.length;i++) {
			int value = getValue(flop[i]);
			color_value[value]++;
			if(value ==1) {
				color_value_count++;
			}
		}
		
		for(int i = 0;i<trun.length;i++) {
			int value = getValue(trun[i]);
			color_value[value]++;
			if(value ==1) {
				color_value_count++;
			}
		}
		
		for(int i = 0;i<river.length;i++) {
			int value = getValue(river[i]);
			color_value[value]++;
			if(value ==1) {
				color_value_count++;
			}
		}
		
		return 0;
	}
	
}
