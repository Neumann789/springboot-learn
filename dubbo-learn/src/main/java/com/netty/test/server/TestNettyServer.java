package com.netty.test.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class TestNettyServer {
	
	public static void main(String[] args) {
		ChannelFactory factory=
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool());
		
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				
				//return Channels.pipeline(new DiscardServerHandler());
				
                ChannelPipeline pipeline = Channels.pipeline();
                //pipeline.addLast("decoder", new InternalDecoder());
                pipeline.addLast("discard", new DiscardServerHandler());
                //pipeline.addLast("handler", new TimeServerHandler());
				return pipeline;
				
			}
		});
		
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.bind(new InetSocketAddress(9090));
		System.out.println("netty server start success!");
	}

}


class DiscardServerHandler extends SimpleChannelHandler {

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        
        if(buf.readable()){
        	System.out.println(new String(buf.array()));
        }
        
        ChannelBuffer writeBuf=ChannelBuffers.dynamicBuffer();
        writeBuf.writeBytes("我是服务器,已经收到请求!".getBytes());
        e.getChannel().write(writeBuf);
        
        
       /* while(buf.readable()) {
            System.out.println((char) buf.readByte());
            System.out.flush();
        }*/
    }
    
    public void messageReceived2(ChannelHandlerContext ctx, MessageEvent e) {
        Channel ch = e.getChannel();
        ch.write(e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        
        Channel ch = e.getChannel();
        ch.close();
    }
    
}




class TimeServerHandler extends SimpleChannelHandler {

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Channel ch = e.getChannel();
        
        ChannelBuffer time = ChannelBuffers.buffer(4);
        time.writeInt((int) (System.currentTimeMillis() / 1000));
        
        ChannelFuture f = ch.write(time);
        
        f.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                Channel ch = future.getChannel();
                ch.close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}

class InternalDecoder extends SimpleChannelUpstreamHandler {


    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
    	
    	System.out.println("come in ......");
    	
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
}
