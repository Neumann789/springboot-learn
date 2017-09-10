package com.dubbo.xml.zookeeper.dubbo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.common.serialize.ObjectInput;
import com.alibaba.dubbo.common.serialize.ObjectOutput;
import com.alibaba.dubbo.common.serialize.support.hessian.Hessian2Serialization;
import com.alibaba.dubbo.remoting.exchange.Request;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.alibaba.fastjson.JSON;

public class SocketComsumer {
	
	
	
	public static void main(String[] args) throws Throwable {
		
		Socket s = new Socket("127.0.0.1", 20881);
		
		BufferedInputStream  bis=new BufferedInputStream(s.getInputStream());
		BufferedOutputStream bos=new BufferedOutputStream(s.getOutputStream());
		
		//bos.write(genRequest());
		bos.write(genRequest4SendMsg());
		bos.flush();
		
		byte[] rspBuf=new byte[1024];
		int size=bis.read(rspBuf);
		byte[] requestData=new byte[size];
		System.arraycopy(rspBuf, 0, requestData, 0, size);
		Object obj=getRspObj(requestData);
		System.out.println(JSON.toJSONString(obj));
		
		
	}
	
	public static Object getRspObj(byte[] buf) throws Throwable{
		byte[] bodyData=new byte[buf.length-16];
		System.arraycopy(buf, 16, bodyData, 0, bodyData.length);
		
		Hessian2Serialization serialization=new Hessian2Serialization();
		ByteArrayInputStream bis = new ByteArrayInputStream(bodyData);
		ObjectInput in = serialization.deserialize(null, bis);
		
		int rspType=in.readByte();
		return in.readObject();
	}
	
	
	
public static byte[] genRequest4SendMsg() throws Throwable{
		
		Request request = new Request();
		request.setVersion("2.0.0");
		request.setTwoWay(true);
		request.setHeartbeat(false);
		
		RpcInvocation rpcInvocation = new RpcInvocation();
		
		request.setData(rpcInvocation);
		Hessian2Serialization serialization=new Hessian2Serialization();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutput out = serialization.serialize(null, os);
		out.writeUTF("2.5.3");
		out.writeUTF("com.dubbo.xml.zookeeper.dubbo.TestService");
		out.writeUTF("0.0.0");
		out.writeUTF("sendMsg");
		out.writeUTF("Lcom/dubbo/xml/zookeeper/dubbo/Request;Lcom/dubbo/xml/zookeeper/dubbo/Message;");
		com.dubbo.xml.zookeeper.dubbo.Request rq = new com.dubbo.xml.zookeeper.dubbo.Request();
		rq.setAge(10);
		rq.setName("fhb");
		out.writeObject(rq);
		
		Message msg=new Message();
		msg.setMsgId("1111");
		msg.setMsgLen(4);
		out.writeObject(msg);
		//{
		//path=com.dubbo.xml.zookeeper.dubbo.TestService, 
		//interface=com.dubbo.xml.zookeeper.dubbo.TestService, 
		//timeout=6000000,
		//version=0.0.0
		//}
		Map<String, String> attachemets=new HashMap<>();
		attachemets.put("path", "com.dubbo.xml.zookeeper.dubbo.TestService");
		attachemets.put("interface", "com.dubbo.xml.zookeeper.dubbo.TestService");
		attachemets.put("timeout", "6000000");
		attachemets.put("version", "0.0.0");
		out.writeObject(attachemets);
		
		out.flushBuffer();
		
		os.flush();
		byte[] bodyData=os.toByteArray();
		int bodyLen=bodyData.length;
		
		byte[] header=new byte[16];
		header[0]=-38;
		header[1]=-69;
		header[2]=-62;
		header[3]=0;
		header[4]=0;
		header[5]=0;
		header[6]=0;
		header[7]=0;
		header[8]=0;
		header[9]=0;
		header[10]=0;
		header[11]=0;
		header[12]=0;
		header[13]=0;
		header[14]=1;
		header[15]=-119;
		
		
		byte[] requestData=new byte[header.length+bodyLen];
		System.arraycopy(header, 0, requestData, 0, header.length);
		System.arraycopy(bodyData, 0, requestData, header.length, bodyLen);
		
		
		
		
		return requestData;
	}
	
	public static byte[] genRequest() throws Throwable{
		
		Request request = new Request();
		request.setVersion("2.0.0");
		request.setTwoWay(true);
		request.setHeartbeat(false);
		
		RpcInvocation rpcInvocation = new RpcInvocation();
		
		request.setData(rpcInvocation);
		Hessian2Serialization serialization=new Hessian2Serialization();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutput out = serialization.serialize(null, os);
		out.writeUTF("2.5.3");
		out.writeUTF("com.dubbo.xml.zookeeper.dubbo.TestService");
		out.writeUTF("0.0.0");
		out.writeUTF("sayHello");
		out.writeUTF("Lcom/dubbo/xml/zookeeper/dubbo/Request;");
		com.dubbo.xml.zookeeper.dubbo.Request rq = new com.dubbo.xml.zookeeper.dubbo.Request();
		rq.setAge(10);
		rq.setName("fhb");
		out.writeObject(rq);
		//{
		//path=com.dubbo.xml.zookeeper.dubbo.TestService, 
		//interface=com.dubbo.xml.zookeeper.dubbo.TestService, 
		//timeout=6000000,
		//version=0.0.0
		//}
		Map<String, String> attachemets=new HashMap<>();
		attachemets.put("path", "com.dubbo.xml.zookeeper.dubbo.TestService");
		attachemets.put("interface", "com.dubbo.xml.zookeeper.dubbo.TestService");
		attachemets.put("timeout", "6000000");
		attachemets.put("version", "0.0.0");
		out.writeObject(attachemets);
		
		out.flushBuffer();
		
		os.flush();
		byte[] bodyData=os.toByteArray();
		int bodyLen=bodyData.length;
		
		byte[] header=new byte[16];
		header[0]=-38;
		header[1]=-69;
		header[2]=-62;
		header[3]=0;
		header[4]=0;
		header[5]=0;
		header[6]=0;
		header[7]=0;
		header[8]=0;
		header[9]=0;
		header[10]=0;
		header[11]=0;
		header[12]=0;
		header[13]=0;
		header[14]=1;
		header[15]=38;
		
		
		byte[] requestData=new byte[header.length+bodyLen];
		System.arraycopy(header, 0, requestData, 0, header.length);
		System.arraycopy(bodyData, 0, requestData, header.length, bodyLen);
		
		
		
		
		return requestData;
	}
	
	
	public static String telnetDubbo(String ip, int port, String request) {

		Socket socket;
		String result="";
		try {
			socket = new Socket(ip, port);
			PrintWriter pw = new PrintWriter(socket.getOutputStream());

			String msg = "\r\n";
			pw.write(msg);
			pw.flush();

			InputStream ins = socket.getInputStream();
			
			byte[] tt = new byte[1024];
			int len=0;
			
			len=ins.read(tt, 0, tt.length);

			pw.write("invoke " + request + "\r\n");
			pw.flush();
			len=ins.read(tt, 0, tt.length);
			result = new String(tt,0,len,"gbk");
			result = result.split("\r\n")[0];
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
