package com.poker.data;

/**
 * author       :   Administrator
 * created on   :   2017/12/25
 * description  :
 */

public class DataPacket {

    public static void write(byte[] writeBuff, int squenceId, int cmd , byte version, byte encrypt ,short gid, byte[] body){
    	
    	DataConverter.putInt(writeBuff,   Header.OFFSET_LENGTH,	Header.HEADER_LENGTH + body.length);
        DataConverter.putInt(writeBuff,   Header.OFFSET_SEQUENCEID,squenceId);
        DataConverter.putInt(writeBuff,   Header.OFFSET_CMD,cmd);
        DataConverter.putByte(writeBuff,  Header.OFFSET_VERSION,version);
        DataConverter.putByte(writeBuff,  Header.OFFSET_ENCRYPT,encrypt);
        DataConverter.putShort(writeBuff, Header.OFFSET_GID,gid);
        
        DataConverter.putByte(writeBuff, Header.HEADER_LENGTH,body);
    }
    
    //---------------------------------------------------------------------------------------------
    public static final class Header {

    	public static final int HEADER_LENGTH = 16;

        public int      length;//包体长度，包含包头+包体
        public int      sequenceId;//流水id
        public int      cmd;   //命令字
        public byte     version = 1;
        public byte     encrypt;
        public short    gid;
        
        static final int OFFSET_LENGTH 		= 0;
        static final int OFFSET_SEQUENCEID 	= 4;
        static final int OFFSET_CMD 		= 8;
        static final int OFFSET_VERSION 	= 12;
        static final int OFFSET_ENCRYPT 	= 13;
        static final int OFFSET_GID 		= 14;
        
        //----------------------------------------------------------------------
        public void setLength(int length){
            this.length = length;
        }

        public void setSequenceId(short sequenceId){
            this.sequenceId = sequenceId;
        }  
        
        public void setCmd(int cmd){
            this.cmd = cmd;
        }
        
        public void setVersion(byte version){
            this.version = version;
        }

        public void setEncrypt(boolean isEncrypt){
            this.encrypt = (byte)(isEncrypt ? 1 : 0);
        }

        public void setGid(short gid){
            this.gid = gid;
        }
        
        //----------------------------------------------------------------------
        public static int getLength(byte[] buff){
            return getLength(buff, 0);
        }
        
        public static int getLength(byte[] buff,int offset){
            return DataConverter.getInt(buff, offset+OFFSET_LENGTH);
        }

        public static int getSequenceId(byte[] buff){
            return getSequenceId(buff,0);
        }
        
        public static int getSequenceId(byte[] buff,int offset){
            return DataConverter.getInt(buff, offset+OFFSET_SEQUENCEID);
        }
        
        public static int getCmd(byte[] buff){
            return getCmd(buff,0);
        }
        
        public static int getCmd(byte[] buff,int offset){
        	return DataConverter.getInt(buff, OFFSET_CMD);
        }
        
        public static byte getVersion(byte[] buff){
            return getVersion(buff,0);
        }
        
        public static byte getVersion(byte[] buff,int offset){
        	return DataConverter.getByte(buff, offset+OFFSET_VERSION);
        }

        public static byte getEncrypt(byte[] buff){
            return getEncrypt(buff,0);
        }
        
        public static byte getEncrypt(byte[] buff,int offset){
        	return DataConverter.getByte(buff, offset+OFFSET_ENCRYPT);
        }

        public static short getGid(byte[] buff){
            return getGid(buff,0);
        }
        
        public static short getGid(byte[] buff,int offset){
        	return DataConverter.getShort(buff, offset+OFFSET_GID);
        }

        //----------------------------------------------------------------------
        public int setLength(byte[] buff){
            return setLength(buff, 0);
        }

        public int setLength(byte[] buff,int offset){
            return DataConverter.putInt(buff,offset+ OFFSET_LENGTH,this.length);
        }
        
        public int setSequenceId(byte[] buff){
            return setSequenceId(buff, 0);
        }
        
        public int setSequenceId(byte[] buff,int offset){
            return DataConverter.putInt(buff, offset+OFFSET_SEQUENCEID,this.sequenceId);
        }
        
        public int setCmd(byte[] buff){
        	return setCmd(buff, 0);
        }
        
        public int setCmd(byte[] buff,int offset){
        	return DataConverter.putInt(buff, offset+OFFSET_CMD,this.cmd);
        }
        
        public int setVersion(byte[] buff){
        	return setVersion(buff, 0);
        }
        
        public int setVersion(byte[] buff,int offset){
        	return DataConverter.putByte(buff, offset+OFFSET_VERSION,this.version);
        }

        public int setEncrypt(byte[] buff){
        	return setEncrypt(buff, 0);
        }
        
        public int setEncrypt(byte[] buff,int offset){
        	return DataConverter.putByte(buff, offset+OFFSET_ENCRYPT,this.encrypt);
        }

        public int setGid(byte[] buff){
        	return setGid(buff, 0);
        }
        
        public int setGid(byte[] buff,int offset){
        	return DataConverter.putShort(buff, offset+OFFSET_GID,this.gid);
        }
    }
}
