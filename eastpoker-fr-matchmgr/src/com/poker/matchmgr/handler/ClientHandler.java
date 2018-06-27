package com.poker.matchmgr.handler;

import java.util.ArrayList;
import java.util.HashMap;


import com.open.net.client.object.AbstractClient;
import com.open.util.log.Logger;
import com.poker.base.cmd.Cmd;
import com.poker.base.cmd.MatchCmd;
import com.poker.base.packet.BasePacket;
import com.poker.base.packet.InPacket;
import com.poker.base.packet.OutPacket;
import com.poker.match.MatchServer;


public class ClientHandler extends AbsClientHandler{
	
	private HashMap<Integer,ArrayList<MatchServer>> map;
	
	public ClientHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
	}

	@Override
	public void dispatchMessage(AbstractClient client, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {
		int cmd   = BasePacket.getCmd(data, header_start);
		Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + Cmd.getCmdString(cmd) + " length " + BasePacket.getLength(data,header_start));
		
		if(cmd == MatchCmd.CMD_MATCHSERVER_REGISTER){
			MatchServer inMatchServer = null;
			ArrayList<MatchServer> match_server_list = map.get(inMatchServer.matchGameId);
			if(null == match_server_list){
				match_server_list = new ArrayList<>();
			}
			boolean isExists = false;
			for (MatchServer matchServer : match_server_list) {
				if(matchServer.matchGameId == inMatchServer.matchGameId && matchServer.matchServerId == inMatchServer.matchServerId){
					isExists = true;
					break;
				}
			}
			
			if(!isExists){
				match_server_list.add(inMatchServer);
			}
		}
		
	}
	
	/**
	 * 注册比赛服务
	 */
	private void registerMatch(){
		
	}
	
	/**
	 * 取消比赛服务
	 */
	private void unregiserMatch(){
		
	}

}
