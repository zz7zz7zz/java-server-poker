package com.open.net.client.object;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :   Server地址
 */

public class UdpAddress {
    public String  ip;
    public int     port;

    public UdpAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

	@Override
	public String toString() {
		return "UdpAddress [ip=" + ip + ", port=" + port + "]";
	}
    
}