package com.poker.monitor.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.server.object.AbstractServerClient;
import com.open.net.server.utils.ExceptionUtil;
import com.open.net.server.utils.TextUtils;
import com.open.util.log.Logger;
import com.poker.cmd.Cmd;
import com.poker.cmd.MonitorCmd;
import com.poker.packet.BasePacket;
import com.poker.packet.InPacket;
import com.poker.packet.OutPacket;
import com.poker.protocols.server.ServerProto.Server;

public class ServerHandler extends AbsServerHandler{

    public ServerHandler(InPacket mInPacket, OutPacket mOutPacket) {
		super(mInPacket, mOutPacket);
	}

	@Override
	public void dispatchMessage(AbstractServerClient client, byte[] data, int header_start, int header_length,
			int body_start, int body_length) {
		try {
			int cmd   = BasePacket.getCmd(data, header_start);
	      	Logger.v("input_packet cmd 0x" + Integer.toHexString(cmd) + " name " + Cmd.getCmdString(cmd) + " length " + BasePacket.getLength(data,header_start));
	      	
    		if(cmd == MonitorCmd.CMD_MONITOR_REGISTER){
    			register(client, data, body_start,body_length);
    		}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.v(ExceptionUtil.getStackTraceString(e));
		}
	}
	
	public HashMap<Integer, ArrayList<AbstractServerClient>> serverOnlineList = new HashMap<Integer, ArrayList<AbstractServerClient>>();

    public void register(AbstractServerClient client, byte[] data, int body_start, int body_length) throws InvalidProtocolBufferException{
    	Server enterServer = Server.parseFrom(data,body_start,body_length);
		
		boolean add = true;
		ArrayList<AbstractServerClient> clientArray = serverOnlineList.get(enterServer.getType());
		if(null == clientArray){
			clientArray = new ArrayList<AbstractServerClient>(10);
			serverOnlineList.put(enterServer.getType(), clientArray);
		}else{
    		for(AbstractServerClient ser:clientArray){
    			if((ser == client)){
    				add = false;
    				break;
    			}else{ 
    				Server attachObj = (Server) ser.getAttachment();
    				if(null != attachObj && attachObj.getId() == enterServer.getId()){
        				clientArray.remove(ser);
        				break;
        			}
    			}
    		}
		}
		
		client.attach(enterServer);
		if(add){
			clientArray.add(client);
		}
		
		//打印所有的服务
		Iterator<Entry<Integer, ArrayList<AbstractServerClient>>> iter = serverOnlineList.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, ArrayList<AbstractServerClient>> entry = iter.next();
			Integer key = entry.getKey();
			ArrayList<AbstractServerClient> val = entry.getValue();
			
	        Logger.v("------- "+key+" size " + val.size() + " -------");
	        for(AbstractServerClient ser:val){
	        	Server serInfo = (Server) ser.getAttachment();
	        	Logger.v(String.format("------- name %s id %d bindHost %s bindPort %d host %s port %d", serInfo.getName(),serInfo.getId(),!TextUtils.isEmpty(serInfo.getHost())? serInfo.getHost() : "null",serInfo.getPort(),ser.mHost,ser.mPort));
	        }
		}
    }



}
