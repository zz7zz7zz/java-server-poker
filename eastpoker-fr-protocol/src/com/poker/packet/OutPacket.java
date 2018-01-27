package com.poker.packet;

public class OutPacket extends BasePacket{
	
	//-----------------------------开始----------------------------------
	public void begin( int squenceId, int cmd){
		super.begin(squenceId, cmd);
	}
	
	public void begin( int squenceId, int cmd ,short gameId,int tableId){
    	super.begin(squenceId, cmd, gameId, tableId);
	}
	
	//-----------------------------写入----------------------------------
	public void writeByte(byte value){
		super.writeByte(value);
	}
	
	public void writeShort(short value){
		super.writeShort(value);
	}
	
	public void writeInt(int value){
		super.writeInt(value);
	}
	
	public void writeLong(long value){
		super.writeLong(value);
	}
	
	public void writeFloat(float value){
		super.writeFloat(value);
	}
	
	public void writeDouble(double value){
		super.writeDouble(value);
	}
	
	public void writeString(String value){
		writeBytes(value.getBytes());
	}
	
	public void writeBytes(byte[] value){
		super.writeBytes(value);
	}
	
	//-----------------------------结束----------------------------------
	public void end(){
    	super.end();
	};
}
