package com.poker.data;

/**
 * author       :   Administrator
 * created on   :   2017/12/25
 * description  :
 */

public class DataPacket {

    public Header header;
    
    public byte[] head = new byte[Header.HEADER_LENGTH];
    public byte[] body;

    //---------------------------------------------------------------------------------------------
    public static class Header {

        public static final int HEADER_LENGTH = 16;

        public int      length;
        public byte     version = 1;
        public byte     encrypt;
        public int      cmd;
        public short    gid;
        public int      sequenceId;

        //-----------------------------------
        public int getLength(byte[] data){
            return 0;
        }

        public byte getVersion(byte[] data){
            return 0;
        }

        public byte getEncrypt(byte[] data){
            return 0;
        }

        public int getCmd(byte[] data){
            return 0;
        }

        public short getGid(byte[] data){
            return 0;
        }

        public short getSequenceId(byte[] data){
            return 0;
        }
        //-----------------------------------
        public void setLength(int length){
            this.length = length;
        }

        public void setVersion(byte version){
            this.version = version;
        }

        public void setEncrypt(boolean isEncrypt){
            this.encrypt = (byte)(isEncrypt ? 1 : 0);
        }

        public void setCmd(int cmd){
            this.cmd = cmd;
        }

        public void setGid(short gid){
            this.gid = gid;
        }

        public void setSequenceId(short sequenceId){
            this.sequenceId = sequenceId;
        }  
        //-----------------------------------
        public static byte[] build(byte[] header , Header mHeader){

            return null;
        }

        public static Header parse(byte[] data){

            return null;
        }
    }
}
