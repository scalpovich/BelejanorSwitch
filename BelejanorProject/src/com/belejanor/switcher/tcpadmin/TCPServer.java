package com.belejanor.switcher.tcpadmin;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public final class TCPServer extends Thread{
	
	private Logger log;
	private static ChannelFuture f;
	private static EventLoopGroup bossGroup;
	public static EventLoopGroup workerGroup;
	
	public TCPServer(){
		this.log = new Logger();
	}
	
	static final boolean SSL = System.getProperty("ssl") != null;
	static final int PORT = Integer.parseInt(System.getProperty("port", 
			         String.valueOf(MemoryGlobal.portTCPServer)));
	
	private void InitServer(){
		
		try {
			
			final SslContext sslCtx;
	        if (SSL) {
	            SelfSignedCertificate ssc = new SelfSignedCertificate();
	           sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
	        } else {
	            sslCtx = null;
	        }
	        
	        bossGroup = new NioEventLoopGroup(MemoryGlobal.maxThreadsTCPServer);
	        workerGroup = new NioEventLoopGroup();
		          try {
		               ServerBootstrap b = new ServerBootstrap();
	            b.group(bossGroup, workerGroup)
	             .channel(NioServerSocketChannel.class)
	             .option(ChannelOption.SO_BACKLOG, 100)
	             .handler(new LoggingHandler(LogLevel.INFO))
	             .childHandler(new ChannelInitializer<SocketChannel>() {
	                  @Override
	                  public void initChannel(SocketChannel ch) throws Exception {
	                      ChannelPipeline p = ch.pipeline();
	                   if (sslCtx != null) {
	                          p.addLast(sslCtx.newHandler(ch.alloc()));
	                      }
	                     //p.addLast(new LoggingHandler(LogLevel.INFO));
	                     p.addLast(new TCPServerHandler());
	                  }
	             });
	 
	            // Start the server.
	             //ChannelFuture f = b.bind(PORT).sync();
	            f = b.bind(PORT).sync();
	             // Wait until the server socket is closed.
	            f.channel().closeFuture().sync();
	        } finally {
	             // Shut down all event loops to terminate all threads.
	             //bossGroup.shutdownGracefully();
	             //workerGroup.shutdownGracefully();
	        }
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo TCPServer::InitServer ", TypeMonitor.error, e);
		}
	}
	
	public void CloseServer(){
		
		try {
			
			 f.channel().close().sync();
			 bossGroup.shutdownGracefully();
             workerGroup.shutdownGracefully();
			
		} catch (Exception e) {
			System.out.println("***************************************************************************");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		
		InitServer();
	}
	
}
