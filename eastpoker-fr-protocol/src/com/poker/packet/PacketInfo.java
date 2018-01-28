package com.poker.packet;

public final class PacketInfo {
	
	public byte[] buff;
	public int length;
	public int header_start;
	public int header_length;
	public int body_start;
	public int body_length;

	public void reset() {
		buff = null;
		length = 0;
		header_start = 0;
		header_length = 0;
		body_start = 0;
		body_length = 0;
	}
}
