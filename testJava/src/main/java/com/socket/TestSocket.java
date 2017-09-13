package com.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TestSocket {
	
	public static void main(String[] args) throws Throwable{
		
		
		ServerSocket ss= new ServerSocket(59090);
		System.out.println("服务启动");
		Socket socket=ss.accept();
		
		System.out.println("收到一个请求!");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		String msg=br.readLine();
		System.out.println("收到一个消息:"+msg);
		
	}

}
