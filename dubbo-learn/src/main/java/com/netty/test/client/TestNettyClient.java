package com.netty.test.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class TestNettyClient {
	
	public static void main(String[] args) throws InterruptedException, IOException {
        //String host = args[0];
        //int port = Integer.parseInt(args[1]);
		
		for(int i=0;i<1;i++){
			
		System.out.println("执行次数#####################"+(++i));
        
        String host="127.0.0.1";
        int port=9090;

        ChannelFactory factory =
            new NioClientSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());

        ClientBootstrap bootstrap = new ClientBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(
                		new TimeClientHandler(),
                		new ChannelDownstreamHandler1(),
                		new ChannelDownstreamHandler2(),
                		new ChannelUpstreamHandler1(),
                		new ChannelUpstreamHandler2()
                		);
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
	}
	
	
	
	public static void handle(Channel channel){
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            try {
            	String msg=in.readLine();
            	ChannelBuffer clb=ChannelBuffers.dynamicBuffer();
            	clb.writeBytes(msg.getBytes());
				channel.write(clb);
				
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
        System.out.println(new String(buf.array()));
        ctx.sendUpstream(e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}

class ChannelUpstreamHandler1 implements ChannelUpstreamHandler{

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		
		System.out.println("ChannelUpstreamHandler1");
		
		ctx.sendUpstream(e);
		
	}
	
}

class ChannelUpstreamHandler2 implements ChannelUpstreamHandler{

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		
		System.out.println("ChannelUpstreamHandler2");
		
		ctx.sendUpstream(e);
		
	}
	
}

class ChannelDownstreamHandler1 implements ChannelDownstreamHandler{

	@Override
	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		
		System.out.println("ChannelDownstreamHandler1");
		
		ctx.sendDownstream(e);
	}
	
}

class ChannelDownstreamHandler2 implements ChannelDownstreamHandler{

	@Override
	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		
		System.out.println("ChannelDownstreamHandler2");
		ctx.sendDownstream(e);
	}
	
}
