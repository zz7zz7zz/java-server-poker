package com.poker.monitor.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.server.message.Message;
import com.open.net.server.object.AbstractServerClient;
import com.open.net.server.utils.TextUtils;
import com.open.util.log.Logger;
import com.poker.protocols.server.ServerProto.Server;

public class ClientMessageHandler {

    public HashMap<Integer, ArrayList<AbstractServerClient>> serverOnlineList = new HashMap<Integer, ArrayList<AbstractServerClient>>();

	
    public void register(AbstractServerClient client, Message msg, int body_start, int body_length) throws InvalidProtocolBufferException{
    	Server enterServer = Server.parseFrom(msg.data,body_start,body_length);
		
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
