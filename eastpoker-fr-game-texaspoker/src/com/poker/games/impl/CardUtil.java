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
}
