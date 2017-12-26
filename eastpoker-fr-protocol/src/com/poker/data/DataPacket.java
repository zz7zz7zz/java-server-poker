package com.poker.data;

/**
 * author       :   Administrator
 * created on   :   2017/12/25
 * description  :
 */

public class DataPacket {

    public static byte[] build(byte[] buff,int squenceId, int cmd , byte version, byte encrypt ,short gid, byte[] body){
    	
    	DataConverter.putInt(buff, 	 Header.OFFSET_LENGTH,	Header.HEADER_LENGTH + body.length);
        DataConverter.putInt(buff, 	 Header.OFFSET_SEQUENCEID,squenceId);
        DataConverter.putInt(buff, 	 Header.OFFSET_CMD,cmd);
        DataConverter.putByte(buff,  Header.OFFSET_VERSION,version);
        DataConverter.putByte(buff,	 Header.OFFSET_ENCRYPT,encrypt);
        DataConverter.putShort(buff, Header.OFFSET_GID,gid);
        
        DataConverter.putByte(buff, Header.HEADER_LENGTH,body);
        
        return buff;
    }
    
    //---------------------------------------------------------------------------------------------
    public static final class Header {

        static final int HEADER_LENGTH = 16;

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
        public int getLength(byte[] buff){
            return DataConverter.getInt(buff, OFFSET_LENGTH);
        }

        public int getSequenceId(byte[] buff){
            return DataConverter.getInt(buff, OFFSET_SEQUENCEID);
        }
        
        public int getCmd(byte[] buff){
        	return DataConverter.getInt(buff, OFFSET_CMD);
        }
        
        public byte getVersion(byte[] buff){
        	return DataConverter.getByte(buff, OFFSET_VERSION);
        }

        public byte getEncrypt(byte[] buff){
        	return DataConverter.getByte(buff, OFFSET_ENCRYPT);
        }

        public short getGid(byte[] buff){
        	return DataConverter.getShort(buff, OFFSET_GID);
        }

        //----------------------------------------------------------------------
        public int setLength(byte[] buff){
            return DataConverter.putInt(buff, OFFSET_LENGTH,this.length);
        }

        public int setSequenceId(byte[] buff){
            return DataConverter.putInt(buff, OFFSET_SEQUENCEID,this.sequenceId);
        }
        
        public int setCmd(byte[] buff){
        	return DataConverter.putInt(buff, OFFSET_CMD,this.cmd);
        }
        
        public int setVersion(byte[] buff){
        	return DataConverter.putByte(buff, OFFSET_VERSION,this.version);
        }

        public int setEncrypt(byte[] buff){
        	return DataConverter.putByte(buff, OFFSET_ENCRYPT,this.encrypt);
        }

        public int setGid(byte[] buff){
        	return DataConverter.putShort(buff, OFFSET_GID,this.gid);
        }
    }
}
