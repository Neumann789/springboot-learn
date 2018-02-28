package com.agent.comm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerManager {
	
	
	public static void start(){
		ServerSocket ss = null;
		try {
			LogUtil.info("开启监听服务端口:"+Constant.SERVER_PORT);
			ss = new ServerSocket(Constant.SERVER_PORT);
			dispatch(ss);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 对客户端的请求进行分发
	 * @param ss
	 * @throws IOException
	 */
	private static void dispatch(ServerSocket ss) throws IOException{
		
		while(true){
			Socket socket = ss.accept();
			LogUtil.info("收到一个请求:"+socket.getInetAddress());
			socketHandle(socket);
		}
		
	}
	
	/**
	 * 对客户端请求进行处理
	 * @param socket
	 */
	private static void socketHandle(final Socket socket){
		
		ThreadPoolUtil.getCachedThreadPool().execute(new Runnable() {
			
			@Override
			public void run() {
				//TODO
				System.out.println("处理客户端请求逻辑TODO");
				
			}
		});
	}
	

}
