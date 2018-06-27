package com.poker.match.handler;

import com.open.net.client.object.AbstractClient;
import com.poker.base.cmd.CmdMatch;
import com.poker.base.packet.InPacket;
import com.poker.base.packet.OutPacket;
import com.poker.base.packet.PacketTransfer;
import com.poker.base.type.TDistapch;
import com.poker.match.Main;


public class ClientHandler extends AbsClientHandler{
	
	public ClientHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
	}

	@Override
	public void dispatchMessage(AbstractClient client, byte[] data, int header_start, int header_length, int body_start,
			int body_length) {

	}

	public void register_to_matchmgr(){
		
		int cmd = CmdMatch.CMD_MATCH_TO_MATCHMGR_REGISTER;
		int squenceId = 0;
		int dst_server_id = 0;

		mOutPacket.begin(squenceId, cmd);
		mOutPacket.writeShort(Main.mServerConfig.matchType);
		mOutPacket.writeShort(Main.mServerConfig.matchId);
		mOutPacket.writeShort(Main.mServerConfig.matchServerId);
		mOutPacket.writeString(Main.mServerConfig.matchName);
		mOutPacket.end();
		
		byte[] mTempBuff = mInPacket.getPacket();
		int length = PacketTransfer.send2MatchMgr(dst_server_id, mTempBuff, squenceId, 0, cmd, TDistapch.TYPE_P2P, mOutPacket.getPacket(),0,  mOutPacket.getLength());
		send2Dispatch(mTempBuff,0,length);	
	}
	
	public void unregister_to_matchmgr(){
		
		int cmd = CmdMatch.CMD_MATCH_TO_MATCHMGR_UNREGISTER;
		int squenceId = 0;
		int dst_server_id = 0;

		mOutPacket.begin(squenceId, cmd);
		mOutPacket.writeShort(Main.mServerConfig.matchType);
		mOutPacket.writeShort(Main.mServerConfig.matchId);
		mOutPacket.writeShort(Main.mServerConfig.matchServerId);
		mOutPacket.writeString(Main.mServerConfig.matchName);
		mOutPacket.end();
		
		byte[] mTempBuff = mInPacket.getPacket();
		int length = PacketTransfer.send2MatchMgr(dst_server_id, mTempBuff, squenceId, 0, cmd, TDistapch.TYPE_P2P, mOutPacket.getPacket(),0,  mOutPacket.getLength());
		send2Dispatch(mTempBuff,0,length);	
	}
	
	public void report_matchmgr_heartbeat(){
		int cmd = CmdMatch.CMD_MATCH_TO_MATCHMGR_HEARTBEAT;
		int squenceId = 0;
		int dst_server_id = 0;

		mOutPacket.begin(squenceId, cmd);
		mOutPacket.end();
		
		byte[] mTempBuff = mInPacket.getPacket();
		int length = PacketTransfer.send2MatchMgr(dst_server_id, mTempBuff, squenceId, 0, cmd, TDistapch.TYPE_P2P, mOutPacket.getPacket(),0,  mOutPacket.getLength());
		send2Dispatch(mTempBuff,0,length);	
	}
}
