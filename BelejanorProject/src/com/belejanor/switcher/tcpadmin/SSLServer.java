package com.belejanor.switcher.tcpadmin;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;

public class SSLServer extends Thread{

	private static final int PORT = Integer.parseInt(System.getProperty("port", 
	           String.valueOf(MemoryGlobal.portTCPServer)));
	static IoAcceptor acceptor = null;
	private Logger log;
	
	public SSLServer(){
		log = new Logger();
	}
	
	private void addSSLSupport(DefaultIoFilterChainBuilder chain)
	{
		  try
		  {
			   SslFilter sslFilter = new SslFilter(new SSLContextGenerator().getSslContext());
			   sslFilter.setUseClientMode(false);
			   sslFilter.setWantClientAuth(false);
			   sslFilter.setNeedClientAuth(false);
			   chain.addLast("sslFilter", sslFilter);
			   System.out.println("SSL support is added..");
			   log.WriteLogMonitor("SSL support is added..", TypeMonitor.monitor, null);
		  }
		  catch (Exception ex)
		  {
			  ex.printStackTrace();
		  }
	 }
	
	 private void InitServer(){
		 
		 
		 try {
			 
			acceptor = new NioSocketAcceptor();
			DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
			addSSLSupport(chain);
			 
			TextLineCodecFactory textLineFactory = new TextLineCodecFactory(Charset.forName("UTF-8"), 
		                   LineDelimiter.WINDOWS, LineDelimiter.WINDOWS);
			textLineFactory.setDecoderMaxLineLength(512*1024);
			textLineFactory.setEncoderMaxLineLength(512*2028);
			
			
			chain.addLast("logger", new LoggingFilter());
			chain.addLast("codec", new ProtocolCodecFilter(textLineFactory));
			acceptor.setHandler(new SSLServerHandler());
			acceptor.getSessionConfig().setReadBufferSize(41096);
			acceptor.getSessionConfig().setMaxReadBufferSize(41096);
			acceptor.bind(new InetSocketAddress(PORT));
		    System.out.println("TCP SSL SERVER INIT!, PORT: " + PORT);
			log.WriteLogMonitor("TCP SSL SERVER INIT!, PORT: " + PORT, TypeMonitor.monitor, null);
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo SSLServer[General]::InitServer ", TypeMonitor.error, e);
		}
		 
	 }
	 public void CloseServer(){
			
			try {
				if(acceptor.isActive())
					acceptor.unbind();
				
			} catch (Exception e) {
				
				log.WriteLogMonitor("Error modulo CloseServer[SSLServer]::CloseServer ", TypeMonitor.error, e);
			}
		}

		@Override
		public void run() {
			InitServer();
		}
}
