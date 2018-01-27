package com.poker.packet;

public class InPacket extends BasePacket {
	
	public InPacket(int max_buff_length) {
		super(max_buff_length);
	}

	//-----------------------------读取----------------------------------
	public byte readByte(){
		return super.readByte();
	}
	
	public int readShort(){
		return super.readShort();
	}
	
	public int readInt(){
		return super.readInt();
	}
	
	public long readLong(){
		return super.readLong();
	}
	
	public float readFloat(){
		return super.readFloat();
	}
	
	public double readeDouble(){
		return super.readeDouble();
	}
	
	public String readString(){
		return new String(readBytes());
	}
	
	public byte[] readBytes(){
		return super.readBytes();
	}
	
}
