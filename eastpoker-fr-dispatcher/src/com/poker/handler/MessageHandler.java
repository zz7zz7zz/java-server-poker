package com.poker.handler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.protobuf.InvalidProtocolBufferException;
import com.open.net.server.message.Message;
import com.open.net.server.object.AbstractServerClient;
import com.open.net.server.object.AbstractServerMessageProcessor;
import com.open.net.server.utils.TextUtils;
import com.open.util.log.Logger;
import com.poker.data.DataPacket;
import com.poker.protocols.server.DispatchChainProto.DispatchChain;
import com.poker.protocols.server.DispatchPacketProto.DispatchPacket;
import com.poker.protocols.server.ServerProto.Server;

public class MessageHandler {

    public HashMap<Integer, ArrayList<AbstractServerClient>> serverList = new HashMap<Integer, ArrayList<AbstractServerClient>>();
    public HashMap<Integer, ArrayList<AbstractServerClient>> gameGroupList = new HashMap<Integer, ArrayList<AbstractServerClient>>();
    public HashMap<Integer, ArrayList<AbstractServerClient>> matchGroupList = new HashMap<Integer, ArrayList<AbstractServerClient>>();
    
    public void register(AbstractServerClient client, Message msg) throws InvalidProtocolBufferException{
    	
    	Server mServer = Server.parseFrom(msg.data,msg.offset + DataPacket.getHeaderLength(),msg.length - DataPacket.getHeaderLength());
		addServer(client, mServer);
		addGameGroup(client, mServer);
		addMatchGroup(client, mServer);
		
		logServer();
    }
    
    public void dispatch(AbstractServerClient client, Message msg,ByteBuffer mWriteBuffer,AbstractServerMessageProcessor mServerMessageProcessor) throws InvalidProtocolBufferException{
		DispatchPacket mDispatchPacket = DispatchPacket.parseFrom(msg.data,msg.offset + DataPacket.getHeaderLength(),msg.length - DataPacket.getHeaderLength());
		int count = mDispatchPacket.getDispatchChainListCount();
		if(count>0){
			DispatchChain chain = mDispatchPacket.getDispatchChainList(count-1);
			int dstServerType = chain.getDstServerType();
			int dstServerId = chain.getDstServerId();
			
			AbstractServerClient server = null;
			ArrayList<AbstractServerClient> serverArray = serverList.get(dstServerType);
			if(null != serverArray){
				for(AbstractServerClient ser:serverArray){
					Server serInfo = (Server) ser.getAttachment();
					if(serInfo.getId() == dstServerId){
						server = ser;
						break;
					}
				}
			}
			
			Logger.v(String.format("dispatch %d %d %d %d %d", chain.getSrcServerType(),chain.getSrcServerId(),chain.getDstServerType(),chain.getDstServerId(),(null != server) ? 1 : 0));
			
			if(null != server){
				mWriteBuffer.clear();
				mDispatchPacket.getData().copyTo(mWriteBuffer);
				mWriteBuffer.flip();
				mServerMessageProcessor.unicast(server, mWriteBuffer.array(),0,mWriteBuffer.remaining());
			}
		}
		
		System.out.println("DispatchPacket "+mDispatchPacket.toString());
    }

    public void dispatchGameGoup(AbstractServerClient client, Message msg,ByteBuffer mWriteBuffer,AbstractServerMessageProcessor mServerMessageProcessor) throws InvalidProtocolBufferException{
    	
    }
    
    public void dispatchMatchGroup(AbstractServerClient client, Message msg,ByteBuffer mWriteBuffer,AbstractServerMessageProcessor mServerMessageProcessor) throws InvalidProtocolBufferException{
    	
    }
    
    public void exit(AbstractServerClient client){
    	removeServer(client);
    	removeGameGroupServer(client);
    	removeMatchGroupServer(client);
    	
    	logServer();
    }
    
    //--------------------------------------------------------------------------------------------------------
    private void addServer(AbstractServerClient client,Server mServer){
		boolean add = true;
		ArrayList<AbstractServerClient> mServerList = serverList.get(mServer.getType());
		if(null == mServerList){
			mServerList = new ArrayList<AbstractServerClient>(10);
			serverList.put(mServer.getType(), mServerList);
		}else{
    		for(AbstractServerClient ser:mServerList){
    			if((ser == client)){
    				add = false;
    				break;
    			}else{ 
    				Server attachObj = (Server) ser.getAttachment();
    				if(null != attachObj && attachObj.getId() == mServer.getId()){
        				mServerList.remove(ser);
        				break;
        			}
    			}
    		}
		}
		client.attach(mServer);
		if(add){
			mServerList.add(client);
		}
    }
    
    private void addGameGroup(AbstractServerClient client,Server mServer){
		int gameGroup = mServer.getGameGroup();
		if(gameGroup >0){
			boolean add = true;
			ArrayList<AbstractServerClient> gameGroupArray = gameGroupList.get(mServer.getType());
    		if(null == gameGroupArray){
    			gameGroupArray = new ArrayList<AbstractServerClient>(10);
    			gameGroupList.put(gameGroup, gameGroupArray);
    		}else{
        		for(AbstractServerClient ser:gameGroupArray){
        			if((ser == client)){
        				add = false;
        				break;
        			}else{ 
        				Server attachObj = (Server) ser.getAttachment();
        				if(null != attachObj && attachObj.getId() == mServer.getId()){
        					gameGroupArray.remove(ser);
            				break;
            			}
        			}
        		}
    		}
    		
    		if(add){
    			gameGroupArray.add(client);
    		}
		}
    }
    
    private void addMatchGroup(AbstractServerClient client,Server mServer){
    	int matchGroup = mServer.getGameGroup();
		if(matchGroup >0){
			boolean add = true;
			ArrayList<AbstractServerClient> matchGroupArray = matchGroupList.get(mServer.getType());
    		if(null == matchGroupArray){
    			matchGroupArray = new ArrayList<AbstractServerClient>(10);
    			matchGroupList.put(matchGroup, matchGroupArray);
    		}else{
        		for(AbstractServerClient ser:matchGroupArray){
        			if((ser == client)){
        				add = false;
        				break;
        			}else{ 
        				Server attachObj = (Server) ser.getAttachment();
        				if(null != attachObj && attachObj.getId() == mServer.getId()){
        					matchGroupArray.remove(ser);
            				break;
            			}
        			}
        		}
    		}
    		
    		if(add){
    			matchGroupArray.add(client);
    		}
		}
    }
    
    private void removeServer(AbstractServerClient client){
    	Server attachObj = (Server) client.getAttachment();
    	if(null != attachObj){
        	ArrayList<AbstractServerClient> mServerList = serverList.get(attachObj.getType());
        	if(null != mServerList){
        		for(AbstractServerClient ser:mServerList){
        			if((ser == client)){
        				mServerList.remove(client);
        				break;
        			}
        		}
        	}
    	}
    }
    
    private void removeGameGroupServer(AbstractServerClient client){
    	Server attachObj = (Server) client.getAttachment();
    	if(null != attachObj){
        	ArrayList<AbstractServerClient> mServerList = serverList.get(attachObj.getGameGroup());
        	if(null != mServerList){
        		for(AbstractServerClient ser:mServerList){
        			if((ser == client)){
        				mServerList.remove(client);
        				break;
        			}
        		}
        	}
    	}
    }
    
    private void removeMatchGroupServer(AbstractServerClient client){
    	Server attachObj = (Server) client.getAttachment();
    	if(null != attachObj){
        	ArrayList<AbstractServerClient> mServerList = serverList.get(attachObj.getMatchGroup());
        	if(null != mServerList){
        		for(AbstractServerClient ser:mServerList){
        			if((ser == client)){
        				mServerList.remove(client);
        				break;
        			}
        		}
        	}
    	}
    }
    //--------------------------------------------------------------------------------------------------------
    public void logServer(){
		//打印所有的服务
    	logServer(serverList);
    	
    	logServer(gameGroupList);
    	
    	logServer(matchGroupList);
    }
    
    public void logServer(HashMap<Integer, ArrayList<AbstractServerClient>> serverList){
		//打印所有的服务
		Iterator<Entry<Integer, ArrayList<AbstractServerClient>>> iter = serverList.entrySet().iterator();
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
