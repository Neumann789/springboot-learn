package com.agent.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.agent.instructs.InstructManager;
import com.agent.util.LogUtil;
import com.agent.util.ThreadPoolUtil;

public class ServerManager {
	
	
	public static void startVmagentThread(){
		
		Thread t = new Thread(){
			@Override
			public void run() {
				ServerManager.start();
			}
		};
		t.setDaemon(true);
		t.setName("vmagent-thread");
		t.start();
		
	}
	
	
	public static void start(){
		ServerSocket ss = null;
		try {
			LogUtil.info("socket server stat at port="+Constant.SERVER_PORT);
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
			LogUtil.info("receive request from "+socket.getInetAddress());
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
				BufferedReader br = null;
				PrintWriter pw = null;
				try {
					
					br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					pw = new PrintWriter(socket.getOutputStream(),true);
					
					String buf = null;
					
					while((buf=br.readLine())!=null){
						LogUtil.info("receive instruct msg:"+buf);
						String returnMsg = InstructManager.handleInstructMsg(buf);
						pw.println(returnMsg);
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					if(br != null){
						try {
							br.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(pw != null){
						pw.close();
					}
				}
				
			}
		});
	}
	
	
	public static void main(String[] args) throws Throwable {
		
		startVmagentThread();
		
		System.in.read();
	}

}
