package com.netty.test.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class TestNettyClient {
	
	public static void main(String[] args) throws InterruptedException, IOException {
        //String host = args[0];
        //int port = Integer.parseInt(args[1]);
        
        String host="127.0.0.1";
        int port=9090;

        ChannelFactory factory =
            new NioClientSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());

        ClientBootstrap bootstrap = new ClientBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(new TimeClientHandler());
            }
        });
        
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);

        ChannelFuture cf=bootstrap.connect(new InetSocketAddress(host, port));
        
        final Channel cl=cf.getChannel();
        cl.setInterestOps(Channel.OP_READ_WRITE);
        
        new Thread(){
        	
        	public void run() {
        		 
        		handle(cl);
        		
        	};
        	
        }.start();
        
        
	}
	
	
	
	public static void handle(Channel channel){
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            try {
            	String msg=in.readLine() + "\r\n";
				channel.write(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		
	}

}

class TimeClientHandler extends SimpleChannelHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        long currentTimeMillis = buf.readInt() * 1000L;
        System.out.println(new Date(currentTimeMillis));
        e.getChannel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
