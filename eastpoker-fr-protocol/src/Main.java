import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.poker.protocols.im.ImMessageProto;

public class Main {

	public static void main(String []argc){
		testImMessage();
	}
	
	public static void testImMessage(){
		
		// 按照定义的数据结构，创建一个Person  
		ImMessageProto.ImMessage.Builder builder = ImMessageProto.ImMessage.newBuilder();
		builder.setType(1);
		builder.setText("Im123456789");
		
		// 将数据写到输出流，如网络输出流，这里就用ByteArrayOutputStream来代替  
		ByteArrayOutputStream output = new ByteArrayOutputStream(16*1024);
		ImMessageProto.ImMessage obj = builder.build();
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
        	ImMessageProto.ImMessage readObj = ImMessageProto.ImMessage.parseFrom(input);
			
			System.out.println(" input length " + byteArray.length + " toString " + readObj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
}
