package com.poker.matchmgr.handler;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.client.object.AbstractClient;
import com.open.util.log.Logger;
import com.poker.base.cmd.Cmd;
import com.poker.base.cmd.CmdMatch;
import com.poker.base.packet.BasePacket;
import com.poker.base.packet.InPacket;
import com.poker.base.packet.OutPacket;
import com.poker.match.MatchServer;
import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;


public class ClientHandler extends AbsClientHandler{
	
	private HashMap<Integer,ArrayList<MatchServer>> map;
	
	public ClientHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
	}

	@Override
	public void dispatchMessage(AbstractClient client, byte[] data, int header_start, int header_length, int body_start,int body_length) {
		try {
			
			int cmd   = BasePacket.getCmd(data, header_start);
			int squenceId = BasePacket.getSequenceId(data, header_start);
			Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + Cmd.getCmdString(cmd) + " length " + BasePacket.getLength(data,header_start));
			
			if(cmd == CmdMatch.CMD_MATCH_TO_MATCHMGR_REGISTER){
				register_match(squenceId,data, header_start,header_length,body_start, body_length);
			}else if(cmd == CmdMatch.CMD_MATCH_TO_MATCHMGR_UNREGISTER){
				unregiser_match(squenceId,data, header_start,header_length,body_start, body_length);
			}else if(cmd == CmdMatch.CMD_USER_CREATE_MATCH){
				user_create_match(squenceId,data, header_start,header_length,body_start, body_length);
			}else if(cmd == CmdMatch.CMD_USER_DESTROY_MATCH){
				user_destroy_match(squenceId,data, header_start,header_length,body_start, body_length);
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 注册比赛服务
	 */
	private void register_match(int squenceId ,byte[] data, int header_start, int header_length, int body_start, int body_length) throws InvalidProtocolBufferException{
		
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());
		short matchType = mInPacket.readShort();
		short matchGameId = mInPacket.readShort();
		short matchServerId = mInPacket.readShort();
		String matchName = mInPacket.readString();
		
		ArrayList<MatchServer> match_server_list = map.get(matchGameId);
		if(null == match_server_list){
			match_server_list = new ArrayList<>();
		}
		boolean isExists = false;
		for (MatchServer matchServer : match_server_list) {
			if(matchServer.matchType == matchType && matchServer.matchGameId == matchGameId && matchServer.matchServerId == matchServerId){
				isExists = true;
				break;
			}
		}
		
		if(!isExists){
			match_server_list.add(new MatchServer(matchType, matchGameId, matchServerId, matchName));
		}
	}
	
	/**
	 * 取消比赛服务
	 */
	private void unregiser_match(int squenceId ,byte[] data, int header_start, int header_length, int body_start, int body_length) throws InvalidProtocolBufferException{
		
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());
		short matchType = mInPacket.readShort();
		short matchGameId = mInPacket.readShort();
		short matchServerId = mInPacket.readShort();

		ArrayList<MatchServer> match_server_list = map.get(matchGameId);
		if(null != match_server_list){
			for (MatchServer matchServer : match_server_list) {
				if(matchServer.matchType == matchType && matchServer.matchGameId == matchGameId && matchServer.matchServerId == matchServerId){
					match_server_list.remove(matchServer);
					break;
				}
			}
		}
	}

	private void user_create_match(int squenceId ,byte[] data, int header_start, int header_length, int body_start, int body_length) throws InvalidProtocolBufferException{
		
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());
		short matchType = mInPacket.readShort();
		short matchGameId = mInPacket.readShort();
		short matchServerId = mInPacket.readShort();
		
		ArrayList<MatchServer> match_server_list = map.get(matchGameId);
		if(null != match_server_list){
			for (MatchServer matchServer : match_server_list) {
				if(matchServer.matchType == matchType && matchServer.matchGameId == matchGameId && matchServer.matchServerId == matchServerId){
					
					//向matchServer 发送创建比赛的命令
					
					break;
				}
			}
		}
	}
	
	private void user_destroy_match(int squenceId ,byte[] data, int header_start, int header_length, int body_start, int body_length) throws InvalidProtocolBufferException{
		
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(data,body_start,body_length);
		mInPacket.copyFrom(mDispatchPacket.getData().toByteArray(), 0, mDispatchPacket.getData().size());
		short matchType = mInPacket.readShort();
		short matchGameId = mInPacket.readShort();
		short matchServerId = mInPacket.readShort();
		
		ArrayList<MatchServer> match_server_list = map.get(matchGameId);
		if(null != match_server_list){
			for (MatchServer matchServer : match_server_list) {
				if(matchServer.matchType == matchType && matchServer.matchGameId == matchGameId && matchServer.matchServerId == matchServerId){
					
					//向matchServer 发送销毁比赛的命令
					
					break;
				}
			}
		}
	}
}
