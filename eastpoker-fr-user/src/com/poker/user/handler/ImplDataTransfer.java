package com.poker.user.handler;

import com.poker.data.DataTransfer;
import com.poker.user.Main;

public class ImplDataTransfer{
	
	public static int send2Access(byte[] writeBuff,int squenceId, byte[] data, int offset ,int length){
		int dst_server_id = Main.libArgsConfig.id;
		return DataTransfer.send2Access(writeBuff,squenceId,data,offset,length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,-1,-1);
	}
	
	public static int send2Login(byte[] writeBuff,int squenceId, byte[] data, int offset ,int length){
		int dst_server_id = Main.libArgsConfig.id;
		return DataTransfer.send2Login(writeBuff,squenceId,data,offset,length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,-1,-1);
	}
	
	public static int send2User(byte[] writeBuff,int squenceId , byte[] data, int offset ,int length){
		int dst_server_id = Main.libArgsConfig.id; 
		return DataTransfer.send2User(writeBuff,squenceId, data,offset,length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,-1,-1);
	}
	
	public static int send2Allocator(byte[] writeBuff,int squenceId , byte[] data, int offset ,int length){
		int dst_server_id = 0;
		return DataTransfer.send2Allocator(writeBuff,squenceId, data,offset,length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,-1,-1);
	}
}
