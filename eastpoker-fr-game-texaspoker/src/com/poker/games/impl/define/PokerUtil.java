package com.poker.games.impl.define;

public final class PokerUtil {
	
	public final static String[] colr= {"♠","♥","♣","♦"};
	public final static String[] value= {"0","1","2","3","4","5","6","7","8","9","10","J","Q","K","A"};
	
	public final static byte getColor(byte card) {
		return (byte) (card>>4);
	}
	
	public final static byte getValue(byte card) {
		return (byte) (card & 0x0F);
	}
	
	public final static String toSymbol(byte card) {
		return colr[getColor(card)]+value[getValue(card)];
	}
	
	public final static String toSymbol(byte[] card) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(int i =0;i<card.length;i++){
			sb.append(" "+toSymbol(card[i]));
		}
		sb.append(" ]");
		return sb.toString();
	}
	
	public final static String toHexString(byte[] card) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(int i =0;i<card.length;i++){
			sb.append(" 0x"+Integer.toHexString(card[i]));
		}
		sb.append(" ]");
		return sb.toString();
	}
}
