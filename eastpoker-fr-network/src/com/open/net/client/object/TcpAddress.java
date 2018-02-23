package com.open.net.client.object;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :   Server地址
 */

public class TcpAddress {
    public String ip;
    public int     port;

    public TcpAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

	@Override
	public String toString() {
		return "TcpAddress [ip=" + ip + ", port=" + port + "]";
	}

}