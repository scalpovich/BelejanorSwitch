package com.belejanor.switcher.tcpadmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.apache.mina.transport.nio.NioTcpServer;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;


public class TCPServerApache extends Thread{

	private Logger log;
	static NioTcpServer acceptor = null;
	
	public TCPServerApache(){
		this.log = new Logger();
	}
	
	private void InitServer(){
		
		int port = Integer.parseInt(System.getProperty("port", 
		           String.valueOf(MemoryGlobal.portTCPServer)));
		try {
			
			acceptor = new NioTcpServer();
			acceptor.setIoHandler(new ServerHandler());
			System.out.println("[TCP SERVER] Init!....127.0.0.1:" + port);
			log.WriteLogMonitor("[TCP SERVER] Init!....127.0.0.1:" + port, TypeMonitor.monitor, null);
			try {
				
				final SocketAddress address = new InetSocketAddress(port);
				acceptor.bind(address);
				new BufferedReader(new InputStreamReader(System.in)).readLine();
				
			} catch (final IOException e) {
				log.WriteLogMonitor("Error modulo TCPServerApache::InitServer ", TypeMonitor.error, e);
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo TCPServerApache[General]::InitServer ", TypeMonitor.error, e);
		}		
	}
	public void CloseServer(){
		
		try {
			if(acceptor.isActive())
				acceptor.unbind();
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo CloseServer[ApacheServer]::CloseServer ", TypeMonitor.error, e);
		}
	}

	@Override
	public void run() {
		InitServer();
	}
}
