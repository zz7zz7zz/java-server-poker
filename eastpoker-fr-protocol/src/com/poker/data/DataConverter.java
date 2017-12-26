package com.poker.data;

/**
 * author       :   Administrator
 * created on   :   2017/12/25
 * description  :
 */

public final class DataConverter {

    //---------------------------------------写--------------------------------------------
	
	public static int putByte(byte[] buff, int offset, byte value){
		buff[offset]=value;
		return 1;
	}

	public static int putByte(byte[] buff, int offset, byte[] in) {
		if (in == null || in.length == 0) {
			return 0;
		}
		for (int i = 0; i < in.length; i++) {
			buff[offset + i] = in[i];
		}
		return in.length;
	}
	
	public static int putShort(byte[] buff, int offset, short value) {
		buff[offset] = (byte) (value >> 8);
		buff[offset + 1] = (byte) (value);
		return 2;
	}
	
	public static int putInt(byte[] buff, int offset, int value) {
		buff[offset] = (byte) (value >> 24);
		buff[offset + 1] = (byte) (value >> 16);
		buff[offset + 2] = (byte) (value >> 8);
		buff[offset + 3] = (byte) value;
		return 4;
	}

	public static int putFloat(byte[] buff, int offset, float value) {
		return putInt(buff, offset, Float.floatToRawIntBits(value));
	}
	
	public static int putLong(byte[] buff, int offset, long value) {
		buff[offset] = (byte) (value >> 56);
		buff[offset + 1] = (byte) (value >> 48);
		buff[offset + 2] = (byte) (value >> 40);
		buff[offset + 3] = (byte) (value >> 32);
		buff[offset + 4] = (byte) (value >> 24);
		buff[offset + 5] = (byte) (value >> 16);
		buff[offset + 6] = (byte) (value >> 8);
		buff[offset + 7] = (byte) value;
		return 8;
	}

	public static int putDouble(byte[] buff, int offset, double value) {
		return putLong(buff, offset, Double.doubleToRawLongBits(value));
	}
	
    //---------------------------------------读--------------------------------------------
	
	public static byte getByte(byte[] buff, int offset){
		return buff[offset];
	}

	public static byte[] getBytes(byte[] buff, int offset,int maxSize){
		byte[] ret=new byte[maxSize];
		for(int i=0;i<maxSize;i++){
			ret[i]=buff[offset+i];
		}
		return ret;
	}
	
	public static short getShort(byte[] buff, int offset) {
		short result = (short) (((buff[offset] & 0xFF) << 8) | ((0xFF & buff[offset + 1])));
		return result;
	}
	
	public static int getInt(byte[] buff, int offset) {
		int result = ((0xFF & buff[offset]) << 24)
					|((0xFF & buff[offset + 1]) << 16)
				    |((0xFF & buff[offset + 2]) << 8) 
				    |(0xFF & buff[offset + 3]);
		return result;
	}

	public static float getFloat(byte[] buff, int offset) {
		int result = getInt(buff, offset);
		return Float.intBitsToFloat(result);
	}
	
	public static long getLong(byte[] buff, int offset) {
		long result = (((long) buff[offset] & (long) 0xFF) << 56)
					| (((long) buff[offset + 1] & (long) 0xFF) << 48)
					| (((long) buff[offset + 2] & (long) 0xFF) << 40)
					| (((long) buff[offset + 3] & (long) 0xFF) << 32)
					| (((long) buff[offset + 4] & (long) 0xFF) << 24)
					| (((long) buff[offset + 5] & (long) 0xFF) << 16)
					| (((long) buff[offset + 6] & (long) 0xFF) << 8)
					| ((long) buff[offset + 7] & (long) 0xFF);
		return result;
	}
	
	public static double getDouble(byte[] buff, int offset) {
		long result = getLong(buff, offset);
		return Double.longBitsToDouble(result);
	}
}
