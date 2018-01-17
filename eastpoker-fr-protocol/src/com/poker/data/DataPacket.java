package com.poker.data;

/**
 * author       :   Administrator
 * created on   :   2017/12/25
 * description  :
 */

public class DataPacket {

	public static final byte   PACKET_FLAG          		= (byte)'G';
	public static final byte   PACKET_VERSION          		= 1;
	public static final byte[] PACKET_HEADER_EXTEND 		= new byte[0];
	public static final byte[] PACKET_HEADER_EXTEND_FORGAME = new byte[6];//可将gameId和tableId封装在扩展包头中
	
    public static int write(byte[] writeBuff, int squenceId, int cmd , byte encrypt, byte[] body,int bodyOffset,int bodyLength){
    	return write(writeBuff,squenceId,cmd,PACKET_FLAG,PACKET_VERSION,encrypt,PACKET_HEADER_EXTEND,body,bodyOffset,bodyLength);
    }
    
    public static int writeGame(short gameId,int tableId,byte[] writeBuff, int squenceId, int cmd , byte encrypt, byte[] body,int bodyOffset,int bodyLength){
    	DataConverter.putShort(PACKET_HEADER_EXTEND_FORGAME, 0, gameId);
    	DataConverter.putInt(PACKET_HEADER_EXTEND_FORGAME, 2, tableId);
    	return write(writeBuff,squenceId,cmd,PACKET_FLAG,PACKET_VERSION,encrypt,PACKET_HEADER_EXTEND_FORGAME,body,bodyOffset,bodyLength);
    }
    
    private static int write(byte[] writeBuff, int squenceId, int cmd ,byte flag ,byte version, byte encrypt,byte[] packet_header_extend, byte[] body,int bodyOffset,int bodyLength){
    	
    	byte packet_header_extend_length	= (byte)packet_header_extend.length;				//包头，扩展长度
    	int  packet_header_length   		= Header.HEADER_BASE_LENGTH + packet_header_extend_length; //包头，长度
    	
    	int  packet_ength 		 			= packet_header_length + body.length;//完整包长度
    	
    	//组装包头
    	DataConverter.putInt(writeBuff,   Header.HEADER_OFFSET_LENGTH,	packet_ength);
        DataConverter.putInt(writeBuff,   Header.HEADER_OFFSET_SEQUENCEID,squenceId);
        DataConverter.putInt(writeBuff,   Header.HEADER_OFFSET_CMD,cmd);
        
        DataConverter.putByte(writeBuff,  Header.HEADER_OFFSET_FLAG, flag);
        DataConverter.putByte(writeBuff,  Header.HEADER_OFFSET_VERSION,version);
        DataConverter.putByte(writeBuff,  Header.HEADER_OFFSET_ENCRYPT,encrypt);
        DataConverter.putByte(writeBuff,  Header.HEADER_OFFSET_EXTEND,packet_header_extend_length);
        
    	//组装扩展包头
        DataConverter.putByte(writeBuff, Header.HEADER_BASE_LENGTH, packet_header_extend, 0, packet_header_extend_length);
        
        //组装包体
        DataConverter.putByte(writeBuff, packet_header_length,body,bodyOffset,bodyLength);
        
        return packet_ength;
    }
    
  //----------------------------------------------------------------------    
    public static int getLength(byte[] buff){
        return getLength(buff, 0);
    }
    
    public static int getLength(byte[] buff,int header_start_offset){
        return DataConverter.getInt(buff, header_start_offset+Header.HEADER_OFFSET_LENGTH);
    }

    public static int getSequenceId(byte[] buff){
        return getSequenceId(buff,0);
    }
    
    public static int getSequenceId(byte[] buff,int header_start_offset){
        return DataConverter.getInt(buff, header_start_offset+Header.HEADER_OFFSET_SEQUENCEID);
    }
    
    public static int getCmd(byte[] buff){
        return getCmd(buff,0);
    }
    
    public static int getCmd(byte[] buff,int header_start_offset){
    	return DataConverter.getInt(buff, header_start_offset + Header.HEADER_OFFSET_CMD);
    }
    
    public static byte getFlag(byte[] buff){
        return getFlag(buff,0);
    }
    
    public static byte getFlag(byte[] buff,int header_start_offset){
    	return DataConverter.getByte(buff, header_start_offset+Header.HEADER_OFFSET_FLAG);
    }
    
    public static byte getVersion(byte[] buff){
        return getVersion(buff,0);
    }
    
    public static byte getVersion(byte[] buff,int header_start_offset){
    	return DataConverter.getByte(buff, header_start_offset+Header.HEADER_OFFSET_VERSION);
    }

    public static byte getEncrypt(byte[] buff){
        return getEncrypt(buff,0);
    }
    
    public static byte getEncrypt(byte[] buff,int header_start_offset){
    	return DataConverter.getByte(buff, header_start_offset+Header.HEADER_OFFSET_ENCRYPT);
    }

    public static int getHeaderLength(byte[] buff,int header_start_offset){
    	return getBaseHeaderLength() + getExtendHeaderLength(buff,header_start_offset);
    }
    
    public static int getBaseHeaderLength(){
    	return Header.HEADER_BASE_LENGTH;
    }
    
    public static int getExtendHeaderLength(byte[] buff){
    	return getExtendHeaderLength(buff,0);
    }
    
    public static byte getExtendHeaderLength(byte[] buff,int header_start_offset){
    	return DataConverter.getByte(buff, header_start_offset + Header.HEADER_OFFSET_EXTEND);
    }
    
    public static short getGid(byte[] buff){
        return getGid(buff,0);
    }
    
    public static short getGid(byte[] buff,int header_start_offset){
    	return DataConverter.getShort(buff, header_start_offset+Header.HEADER_OFFSET_EXTEND_GID);
    }
    
    public static int getTid(byte[] buff){
        return getTid(buff,0);
    }
    
    public static int getTid(byte[] buff,int header_start_offset){
    	return DataConverter.getInt(buff, header_start_offset+Header.HEADER_OFFSET_EXTEND_TID);
    }
    //---------------------------------------------------------------------------------------------
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
}
