package com.poker.access.packet;

import com.poker.data.DataTransfer;

public class PacketTransfer{
	
	public static int SERVER_ID = 0;
	public static int SERVER_TYPE;
	
	public static void init(int server_type,int server_id){
		SERVER_TYPE = server_type;
		SERVER_ID = server_id;
	}
	
	public static int send2Login(byte[] writeBuff,int squenceId,long uid, int cmd ,int dispatch_type, byte[] data, int offset ,int length){
		int dst_server_id = 0;
		return DataTransfer.send2Login(writeBuff,squenceId,uid,cmd,dispatch_type,data,offset,length, SERVER_TYPE, SERVER_ID, dst_server_id,-1,-1);
	}
	
	public static int send2User(byte[] writeBuff,int squenceId ,long uid, int cmd ,int dispatch_type, byte[] data, int offset ,int length){
		int dst_server_id = 0;
		return DataTransfer.send2User(writeBuff,squenceId,uid,cmd,dispatch_type, data,offset,length, SERVER_TYPE, SERVER_ID, dst_server_id,-1,-1);
	}
}