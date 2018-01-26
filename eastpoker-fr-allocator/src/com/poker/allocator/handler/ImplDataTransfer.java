package com.poker.allocator.handler;

import com.poker.allocator.Main;
import com.poker.data.DataTransfer;


public class ImplDataTransfer{
	
	public static int send2Access(byte[] writeBuff,int squenceId,long uid, int cmd ,int dispatch_type, byte[] data, int offset ,int length){
		int dst_server_id = Main.libArgsConfig.id;
		return DataTransfer.send2Access(writeBuff,squenceId,uid,cmd,dispatch_type,data,offset,length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,-1,-1);
	}
	
	public static int send2Login(byte[] writeBuff,int squenceId,long uid, int cmd ,int dispatch_type, byte[] data, int offset ,int length){
		int dst_server_id = Main.libArgsConfig.id;
		return DataTransfer.send2Login(writeBuff,squenceId,uid,cmd,dispatch_type,data,offset,length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,-1,-1);
	}
	
	public static int send2User(byte[] writeBuff,int squenceId ,long uid, int cmd ,int dispatch_type, byte[] data, int offset ,int length){
		int dst_server_id = Main.libArgsConfig.id;
		return DataTransfer.send2User(writeBuff,squenceId,uid,cmd,dispatch_type, data,offset,length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,-1,-1);
	}
	
	public static int send2Allocator(byte[] writeBuff,int squenceId ,long uid, int cmd ,int dispatch_type, byte[] data, int offset ,int length){
		int dst_server_id = Main.mServerConfig.game_id;
		return DataTransfer.send2Allocator(writeBuff,squenceId,uid,cmd,dispatch_type, data,offset,length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,-1,-1);
	}
	
	public static int send2Gamer(byte[] writeBuff,int squenceId, long uid, int cmd ,int dispatch_type , byte[] data, int offset ,int length){
		int dst_server_id = Main.libArgsConfig.id;
		int gameGroup = Main.mServerConfig.game_id;
		return DataTransfer.send2Gamer(writeBuff,squenceId,uid,cmd,dispatch_type, data,offset,length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,gameGroup,-1);
	}
	
	public static int send2GamerGroup(byte[] writeBuff,int squenceId, long uid, int cmd ,int dispatch_type , byte[] data, int offset ,int length){
		int dst_server_id = Main.libArgsConfig.id;
		int gameGroup = Main.mServerConfig.game_id;
		return DataTransfer.send2Gamer(writeBuff,squenceId,uid,cmd,dispatch_type, data,offset,length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,gameGroup,-1);
	}
	
	public static int send2GoldCoin(byte[] writeBuff,int squenceId, long uid, int cmd ,int dispatch_type, byte[] data, int offset ,int length){
		int dst_server_id = Main.libArgsConfig.id;
		return DataTransfer.send2GoldCoin(writeBuff,squenceId,uid,cmd,dispatch_type, data,offset,length, Main.libArgsConfig.server_type, Main.libArgsConfig.id, dst_server_id,-1,-1);
	}
}