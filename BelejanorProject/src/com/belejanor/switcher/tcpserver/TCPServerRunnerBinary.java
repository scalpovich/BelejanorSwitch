package com.belejanor.switcher.tcpserver;

import java.net.InetSocketAddress;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;

public class TCPServerRunnerBinary extends Thread{

	private static final int PORT = Integer.parseInt(System.getProperty("port", 
	           String.valueOf(7444)));
	private static final boolean USE_SSL = false;
	private Logger log;
	private static SocketAcceptor acceptor;
	
	public TCPServerRunnerBinary(){
		
		log = new Logger();
	}
	
	private void InitServer(){
		try {
			
			acceptor = new NioSocketAcceptor(MemoryGlobal.maxThreadsTCPServer);
			acceptor.setReuseAddress( true );
			DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
			if (USE_SSL) {
			    try {
					addSSLSupport(chain);
				} catch (Exception e) {
					e.printStackTrace();
					log.WriteLogMonitor("Error modulo InitServer[TCPServerRunnerBinary]::addSSLSupport ", TypeMonitor.error, e);
				}
			}
			acceptor.setHandler(new TCPServerProtcolDataFastBinary()/*new TCPServerProtcolISO8583Binary*/);
			acceptor.getSessionConfig().setReadBufferSize(41096);
			acceptor.getSessionConfig().setMaxReadBufferSize(41096);
			
			acceptor.bind(new InetSocketAddress(PORT));
			System.out.println("TCP SERVER [BINARY] INIT!, PORT: " + PORT);
			log.WriteLogMonitor("TCP SERVER [BINARY] INIT!, PORT: " + PORT, TypeMonitor.monitor, null);
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo TCPServerRunnerBinary[BINARY]::InitServer ", TypeMonitor.error, e);
		}
	}
	
private  void addSSLSupport(DefaultIoFilterChainBuilder chain){
		
		try {
			
			SslFilter sslFilter = new SslFilter(BogusSslContextFactory
		            .getInstance(true));
			sslFilter.setUseClientMode(false);
			sslFilter.setWantClientAuth(false);
			sslFilter.setNeedClientAuth(false);
		    chain.addLast("sslFilter", sslFilter);
		    System.out.println("Protocolo SSL es soportado en TCPServerRunnerBinary..");
		    log.WriteLogMonitor("Protocolo SSL es soportado en TCPServerRunnerBinary..", TypeMonitor.monitor, null);
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo TCPServerRunnerBinary[General]::addSSLSupport ", TypeMonitor.error, e);
		}
		
	}
	
	 public void CloseServer(){
			
			try {
				if(acceptor.isActive())
					acceptor.unbind();
				
			} catch (Exception e) {
				
				log.WriteLogMonitor("Error modulo CloseServer[TCPServerRunnerBinary]::CloseServer ", TypeMonitor.error, e);
			}
	}

	@Override
	public void run() {
		InitServer();
	}
}
