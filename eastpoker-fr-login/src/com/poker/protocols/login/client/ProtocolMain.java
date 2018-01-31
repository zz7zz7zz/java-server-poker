package com.poker.protocols.login.client;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.protobuf.ByteString;
import com.poker.protocols.login.client.RequestLoginProto.RequestLogin;
import com.poker.protocols.login.server.ResponseLoginProto.ResponseLogin;
import com.poker.protocols.server.DispatchChainProto;
import com.poker.protocols.server.DispatchPacketProto;
import com.poker.protocols.server.ServerProto;

public class ProtocolMain {

	public static void main(String []argc){
		testLogin();
//		testLoginResponse();
//		testDispatchReg();
//		testDispatch();
		//testImMessage();
	}
	
	public static void testDispatch(){
		
	// 按照定义的数据结构，创建一个对象
		DispatchPacketProto.DispatchPacket.Builder builder = DispatchPacketProto.DispatchPacket.newBuilder();
		builder.setSequenceId(1);
		builder.setData(ByteString.copyFrom("abc".getBytes()));

		DispatchChainProto.DispatchChain.Builder chainBuilder = DispatchChainProto.DispatchChain.newBuilder();
		chainBuilder.setSrcServerType(1);
		chainBuilder.setSrcServerId(11);
		chainBuilder.setDstServerType(2);
		chainBuilder.setDstServerId(21);
		
		builder.addDispatchChainList(chainBuilder);
		
		
		// 将数据写到输出流，如网络输出流，这里就用ByteArrayOutputStream来代替 
		ByteArrayOutputStream output = new ByteArrayOutputStream(16*1024);
		DispatchPacketProto.DispatchPacket obj = builder.build();
		try {
			obj.writeTo(output);
			//op.write(obj.toByteArray())
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// -------------- 分割线：上面是发送方，将数据序列化后发送 --------------- 
		byte[] byteArray = output.toByteArray(); 
		System.out.println(" output length " + byteArray.length + " toString " + new String(byteArray));

		
		// -------------- 分割线：下面是接收方，将数据接收后反序列化 ---------------  
        
		// 接收到流并读取，如网络输入流，这里用ByteArrayInputStream来代替  
        ByteArrayInputStream input = new ByteArrayInputStream(byteArray);
        try {
        	DispatchPacketProto.DispatchPacket readObj = DispatchPacketProto.DispatchPacket.parseFrom(input);
			
			System.out.println(" input length " + byteArray.length + " toString " + readObj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}

	public static void testDispatchReg(){
		
		// 按照定义的数据结构，创建一个对象
		ServerProto.Server.Builder builder = ServerProto.Server.newBuilder();
		builder.setType(1);
		builder.setName("name");
		builder.setId(1);
		builder.setHost("127.0.0.1");
		builder.setPort(9999);
		
		// 将数据写到输出流，如网络输出流，这里就用ByteArrayOutputStream来代替 
		ByteArrayOutputStream output = new ByteArrayOutputStream(16*1024);
		ServerProto.Server obj = builder.build();
		try {
			obj.writeTo(output);
			//op.write(obj.toByteArray())
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// -------------- 分割线：上面是发送方，将数据序列化后发送 ---------------
		byte[] byteArray = output.toByteArray(); 
		System.out.println(" output length " + byteArray.length + " toString " + new String(byteArray));
		
		
		// -------------- 分割线：下面是接收方，将数据接收后反序列化 ---------------  
        
		// 接收到流并读取，如网络输入流，这里用ByteArrayInputStream来代替  
        ByteArrayInputStream input = new ByteArrayInputStream(byteArray);
        try {
        	ServerProto.Server readObj = ServerProto.Server.parseFrom(input);
			
			System.out.println(" input length " + byteArray.length + " toString " + readObj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	public static void testLogin(){
		
		// 按照定义的数据结构，创建一个对象
		RequestLogin.Builder builder = RequestLogin.newBuilder();
		builder.setUuid("10001");
		builder.setUid(1);
		
		// 将数据写到输出流，如网络输出流，这里就用ByteArrayOutputStream来代替 
		ByteArrayOutputStream output = new ByteArrayOutputStream(16*1024);
		RequestLogin obj = builder.build();
		try {
			obj.writeTo(output);
			//op.write(obj.toByteArray())
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// -------------- 分割线：上面是发送方，将数据序列化后发送 ---------------
		byte[] byteArray = output.toByteArray(); 
		System.out.println(" output length " + byteArray.length + " toString " + new String(byteArray));
		
		
		// -------------- 分割线：下面是接收方，将数据接收后反序列化 ---------------  
        
		// 接收到流并读取，如网络输入流，这里用ByteArrayInputStream来代替  
//        ByteArrayInputStream input = new ByteArrayInputStream(byteArray);
        try {
        	RequestLogin readObj = RequestLogin.parseFrom(byteArray,0,byteArray.length);
			
			System.out.println(" input length " + byteArray.length + " toString " + readObj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	public static void testLoginResponse(){
		
		// 按照定义的数据结构，创建一个对象
		ResponseLogin.Builder builder =ResponseLogin.newBuilder();
		builder.setUid(1);
		
		// 将数据写到输出流，如网络输出流，这里就用ByteArrayOutputStream来代替 
		ByteArrayOutputStream output = new ByteArrayOutputStream(16*1024);
		ResponseLogin obj = builder.build();
		try {
			obj.writeTo(output);
			//op.write(obj.toByteArray())
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// -------------- 分割线：上面是发送方，将数据序列化后发送 ---------------
		byte[] byteArray = output.toByteArray(); 
		System.out.println(" output length " + byteArray.length + " toString " + new String(byteArray));
		
		
		// -------------- 分割线：下面是接收方，将数据接收后反序列化 ---------------  
        
		// 接收到流并读取，如网络输入流，这里用ByteArrayInputStream来代替  
//        ByteArrayInputStream input = new ByteArrayInputStream(byteArray);
        try {
        	ResponseLogin readObj = ResponseLogin.parseFrom(byteArray,0,byteArray.length);
			
			System.out.println(" input length " + byteArray.length + " toString " + readObj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
//	public static void testImMessage(){
//		
//		// 按照定义的数据结构，创建一个对象
//		ImMessageProto.ImMessage.Builder builder = ImMessageProto.ImMessage.newBuilder();
//		builder.setType(1);
//		builder.setText("Im123456789");
//		
//		// 将数据写到输出流，如网络输出流，这里就用ByteArrayOutputStream来代替 
//		ByteArrayOutputStream output = new ByteArrayOutputStream(16*1024);
//		ImMessageProto.ImMessage obj = builder.build();
//		try {
//			obj.writeTo(output);
//			//op.write(obj.toByteArray())
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		// -------------- 分割线：上面是发送方，将数据序列化后发送 ---------------
//		byte[] byteArray = output.toByteArray(); 
//		System.out.println(" output length " + byteArray.length + " toString " + new String(byteArray));
//		
//		
//		// -------------- 分割线：下面是接收方，将数据接收后反序列化 ---------------  
//        
//		// 接收到流并读取，如网络输入流，这里用ByteArrayInputStream来代替  
//        ByteArrayInputStream input = new ByteArrayInputStream(byteArray);
//        try {
//        	ImMessageProto.ImMessage readObj = ImMessageProto.ImMessage.parseFrom(input);
//			
//			System.out.println(" input length " + byteArray.length + " toString " + readObj.toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//        
//	}
}
