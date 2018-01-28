package com.poker.packet;

import com.google.protobuf.ByteString;
import com.poker.data.ByteUtil;
import com.poker.data.DataPacket;

public class BasePacket {
    
	public static final byte   PACKET_FLAG          		= (byte)'G';
	public static final byte   PACKET_VERSION          		= 1;
	public static final byte[] PACKET_HEADER_EXTEND 		= new byte[0];
	public static final byte[] PACKET_HEADER_EXTEND_FORGAME = new byte[6];//可将gameId和tableId封装在扩展包头中
	
	//---------------------------------------------------------------
	byte[] buff = null;
	int offset  = 0;
	int length  = 0;
	//---------------------------------------------------------------
	int packet_header_base_length = Header.HEADER_BASE_LENGTH;
	int packet_header_extend_length;
	int packet_body_ength;
	
	public BasePacket(int max_buff_length){
		buff = new byte[max_buff_length];
	}
	
	//---------------------------------------------------------------
	protected void reset(){
		offset = 0;
		
		packet_header_base_length = 0;
		packet_header_extend_length    = 0;
		packet_body_ength = 0;
	}
	
	//---------------------------------------------------------------
	protected void begin( int squenceId, int cmd){
		reset();
		
		packet_header_base_length   = Header.HEADER_BASE_LENGTH;
		packet_header_extend_length = PACKET_HEADER_EXTEND.length;
		
    	//组装包头
    	//ByteUtil.putInt(buff,   Header.HEADER_OFFSET_LENGTH,	packet_ength);
        ByteUtil.putInt(buff,   Header.HEADER_OFFSET_SEQUENCEID,	squenceId);
        ByteUtil.putInt(buff,   Header.HEADER_OFFSET_CMD,			cmd);
        
        ByteUtil.putByte(buff,  Header.HEADER_OFFSET_FLAG, 			PACKET_FLAG);
        ByteUtil.putByte(buff,  Header.HEADER_OFFSET_VERSION,		PACKET_VERSION);
        ByteUtil.putByte(buff,  Header.HEADER_OFFSET_ENCRYPT,		(byte)0);
        ByteUtil.putByte(buff,  Header.HEADER_OFFSET_EXTEND,		(byte)packet_header_extend_length);
        
    	//组装扩展包头
        ByteUtil.putBytes(buff, Header.HEADER_BASE_LENGTH, PACKET_HEADER_EXTEND, 0, packet_header_extend_length);
        
        offset += (packet_header_base_length + packet_header_extend_length);
	}
	
	protected void begin( int squenceId, int cmd ,short gameId,int tableId){
		reset();
		
    	ByteUtil.putShort(PACKET_HEADER_EXTEND_FORGAME, 0, gameId);
    	ByteUtil.putInt(PACKET_HEADER_EXTEND_FORGAME, 2, tableId);
    	
		packet_header_base_length   = Header.HEADER_BASE_LENGTH;
		packet_header_extend_length = PACKET_HEADER_EXTEND_FORGAME.length;
		
    	//组装包头
    	//ByteUtil.putInt(buff,   Header.HEADER_OFFSET_LENGTH,	packet_ength);
        ByteUtil.putInt(buff,   Header.HEADER_OFFSET_SEQUENCEID,	squenceId);
        ByteUtil.putInt(buff,   Header.HEADER_OFFSET_CMD,			cmd);
        
        ByteUtil.putByte(buff,  Header.HEADER_OFFSET_FLAG, 			PACKET_FLAG);
        ByteUtil.putByte(buff,  Header.HEADER_OFFSET_VERSION,		PACKET_VERSION);
        ByteUtil.putByte(buff,  Header.HEADER_OFFSET_ENCRYPT,		(byte)0);
        ByteUtil.putByte(buff,  Header.HEADER_OFFSET_EXTEND,		(byte)packet_header_extend_length);
        
    	//组装扩展包头
        ByteUtil.putBytes(buff, Header.HEADER_BASE_LENGTH, PACKET_HEADER_EXTEND_FORGAME, 0, packet_header_extend_length);
        
        offset += (packet_header_base_length + packet_header_extend_length);
	}
	
	//-----------------------------写入----------------------------------
	protected void writeByte(byte value){
		int size = ByteUtil.putByte(buff, offset, value);
		offset += size;
		packet_body_ength += size;
	}
	
	protected void writeShort(short value){
		int size = ByteUtil.putShort(buff, offset, value);
		offset += size;
		packet_body_ength += size;
	}
	
	protected void writeInt(int value){
		int size = ByteUtil.putInt(buff, offset, value);
		offset += size;
		packet_body_ength += size;
	}
	
	protected void writeLong(long value){
		int size = ByteUtil.putLong(buff, offset, value);
		offset += size;
		packet_body_ength += size;
	}
	
	protected void writeFloat(float value){
		int size = ByteUtil.putFloat(buff, offset, value);
		offset += size;
		packet_body_ength += size;
	}
	
	protected void writeDouble(double value){
		int size = ByteUtil.putDouble(buff, offset, value);
		offset += size;
		packet_body_ength += size;
	}
	
	protected void writeString(String value){
		writeBytes(value.getBytes());
	}
	
	protected void writeBytes(byte[] value){
		int size1= ByteUtil.putInt(buff, offset,value.length);
		int size2= ByteUtil.putBytes(buff, offset+size1,value);
		offset += (size1+size2);
		packet_body_ength += (size1+size2);
	}
	
	protected void writeBytes(byte[] value,int value_offset,int value_length){
		int size1= ByteUtil.putInt(buff, offset,value_length);
		int size2= ByteUtil.putBytes(buff, offset+size1,value,value_offset,value_length);
		offset += (size1+size2);
		packet_body_ength += (size1+size2);
	}
	//-----------------------------读取----------------------------------
	protected byte readByte(){
		byte ret = ByteUtil.getByte(buff, offset);
		offset += 1;
		return ret;
	}
	
	protected short readShort(){
		short ret = ByteUtil.getShort(buff, offset);
		offset += 2;
		return ret;
	}
	
	protected int readInt(){
		int ret = ByteUtil.getInt(buff, offset);
		offset += 4;
		return ret;
	}
	
	protected long readLong(){
		long ret = ByteUtil.getLong(buff, offset);
		offset += 8;
		return ret;
	}
	
	protected float readFloat(){
		float ret = ByteUtil.getFloat(buff, offset);
		offset += 4;
		return ret;
	}
	
	protected double readeDouble(){
		double ret = ByteUtil.getDouble(buff, offset);
		offset += 8;
		return ret;
	}
	
	protected String readString(){
		return new String(readBytes());
	}
	
	protected byte[] readBytes(){
		int size = readInt();
		byte[] ret = ByteUtil.getBytes(buff, offset,size);
		offset += size;
		return ret;
	}
	
	protected int[] readBytesOffsetAndLenth(){
		int size = readInt();
		
		int ret_offset = offset;
		int ret_length = size;
		
		offset += size;
		return new int[]{ret_offset,ret_length};
	}
	
	protected PacketInfo readBytesToSubPacket(){
		int size = readInt();
		
		PacketInfo ret = new PacketInfo();
		ret.buff = buff;
		ret.length = size;
		ret.header_start = offset;
		ret.header_length = DataPacket.getHeaderLength(buff, ret.header_start);
		ret.body_start = ret.header_start + ret.header_length;
		ret.body_length= ret.length - ret.header_length;

		offset += size;
		return ret;
	}
	
	//---------------------------------------------------------------
	protected void end(){
		length = packet_header_base_length + packet_header_extend_length + packet_body_ength;
    	ByteUtil.putInt(buff,   Header.HEADER_OFFSET_LENGTH,	length);
	};
	
	//-------------------------------------------------包头定义--------------------------------------------
	public static final class Header {

        //-----------------------包头的基本长度----------------------
    	public static final int HEADER_BASE_LENGTH = 16;

        //-----------------------各个属性在包头中的偏移量----------------------
        public static final int HEADER_OFFSET_LENGTH 		= 0;
        public static final int HEADER_OFFSET_SEQUENCEID 	= 4;
        public static final int HEADER_OFFSET_CMD 			= 8;
        
        public static final int HEADER_OFFSET_FLAG 		    = 12;
        public static final int HEADER_OFFSET_VERSION 		= 13;
        public static final int HEADER_OFFSET_ENCRYPT 		= 14;
        public static final int HEADER_OFFSET_EXTEND 		= 15;
        
        public static final int HEADER_OFFSET_EXTEND_GID 	= HEADER_BASE_LENGTH;
        public static final int HEADER_OFFSET_EXTEND_TID 	= HEADER_BASE_LENGTH+2;
        
        //-----------------------包头各个属性定义----------------------
        public int      length;    		//包体长度（包含包头+包体）   长度:4
        public int      sequenceId;		//流水id                  长度:4
        public int      cmd;       		//命令字                                     长度:4
        
        public byte     flag;      		//标识  (比如写死'g')       长度:1                               
        public byte     version ;	    //包头版本                                 长度:1
        public byte     encrypt;		//是否加密                                 长度:1
        public byte     extend;         //扩展包头长度                          长度:1
        
        public short    extend_gid;		//游戏id                  长度:2
        public int      extend_tid;     //桌子id                  长度:4
        
        //--------------------------------------------------------------
    }
    
    public int getLength(){
    	 return ByteUtil.getInt(buff, Header.HEADER_OFFSET_LENGTH);
    }

    public static int getLength(byte[] buff,int header_start_offset){
        return ByteUtil.getInt(buff, header_start_offset+Header.HEADER_OFFSET_LENGTH);
    }
    
    public int getSequenceId(){
    	return ByteUtil.getInt(buff, Header.HEADER_OFFSET_SEQUENCEID);
    }
    
    public static int getSequenceId(byte[] buff,int header_start_offset){
        return ByteUtil.getInt(buff, header_start_offset+Header.HEADER_OFFSET_SEQUENCEID);
    }
    
    public int getCmd(){
    	return ByteUtil.getInt(buff, Header.HEADER_OFFSET_CMD);
    }
    
    public static int getCmd(byte[] buff,int header_start_offset){
    	return ByteUtil.getInt(buff, header_start_offset + Header.HEADER_OFFSET_CMD);
    }
    
    public byte getFlag(){
    	return ByteUtil.getByte(buff, Header.HEADER_OFFSET_FLAG);
    }
    
    public static byte getFlag(byte[] buff,int header_start_offset){
    	return ByteUtil.getByte(buff, header_start_offset+Header.HEADER_OFFSET_FLAG);
    }
    
    public byte getVersion(){
    	return ByteUtil.getByte(buff, Header.HEADER_OFFSET_VERSION);
    }

    public static byte getVersion(byte[] buff,int header_start_offset){
    	return ByteUtil.getByte(buff, header_start_offset+Header.HEADER_OFFSET_VERSION);
    }
    
    public byte getEncrypt(){
    	return ByteUtil.getByte(buff, Header.HEADER_OFFSET_ENCRYPT);
    }

    public static byte getEncrypt(byte[] buff,int header_start_offset){
    	return ByteUtil.getByte(buff, header_start_offset+Header.HEADER_OFFSET_ENCRYPT);
    }
    
    public int getExtendHeaderLength(){
    	return ByteUtil.getByte(buff, Header.HEADER_OFFSET_EXTEND);
    }
    
    public static byte getExtendHeaderLength(byte[] buff,int header_start_offset){
    	return ByteUtil.getByte(buff, header_start_offset + Header.HEADER_OFFSET_EXTEND);
    }
    
    public short getGid(){
    	return ByteUtil.getShort(buff, Header.HEADER_OFFSET_EXTEND_GID);
    }

    public static short getGid(byte[] buff,int header_start_offset){
    	return ByteUtil.getShort(buff, header_start_offset+Header.HEADER_OFFSET_EXTEND_GID);
    }
    
    public int getTid(){
    	return ByteUtil.getInt(buff, Header.HEADER_OFFSET_EXTEND_TID);
    }
    
    public static int getTid(byte[] buff,int header_start_offset){
    	return ByteUtil.getInt(buff, header_start_offset+Header.HEADER_OFFSET_EXTEND_TID);
    }
    
    public int getBaseHeaderLength(){
    	return Header.HEADER_BASE_LENGTH;
    }
    
    public int getHeaderLength(){
    	return getBaseHeaderLength() + getExtendHeaderLength();
    }
    
    public int getHeaderLength(byte[] buff,int header_start_offset){
    	return getBaseHeaderLength() + getExtendHeaderLength(buff,header_start_offset);
    }
    
    public int getBodyLenth(){
    	return getLength() - getHeaderLength();
    }
   
    public int getBodyLenth(byte[] buff,int header_start_offset){
    	return getLength(buff,header_start_offset) - getHeaderLength(buff,header_start_offset);
    }
    
    //--------------------------------------------------------------------------------
    //从外部数组将数据拷贝进来
    public void copyFrom(byte[] from ,int fromOffset , int fromLenth){
    	reset();
    	
        System.arraycopy(from,fromOffset,buff,0,fromLenth);
        
    	offset = getHeaderLength();
    	length = fromLenth;

    }
    
    public void copyFrom(ByteString mByteString){
    	reset();
    	
    	mByteString.copyTo(buff, 0);
    	
    	offset = getHeaderLength();
    	length = mByteString.size();
    }
    
    //将内部数据拷贝到外部数组
    public void copyTo(byte[] to ,int toOffset){
    	System.arraycopy(buff,0,to,toOffset,length);
    }
    
    public byte[] getPacket(){
    	return buff;
    }
    
    public int getPacketLength(){
    	return length;
    }
}
