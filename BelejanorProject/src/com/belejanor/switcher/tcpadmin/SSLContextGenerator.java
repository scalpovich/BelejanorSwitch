package com.belejanor.switcher.tcpadmin;

import java.io.File;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import org.apache.mina.filter.ssl.KeyStoreFactory;
import org.apache.mina.filter.ssl.SslContextFactory;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;

public class SSLContextGenerator {

	private Logger log;
	
	public SSLContextGenerator(){
		
		log = new Logger();
	}
	
	public SSLContext getSslContext()
	{
	  SSLContext sslContext = null;
		  try
		  {
			  
			    String path = MemoryGlobal.currentPath;
			 	log.WriteLogMonitor("Current Path [SSL_TCPServer] ===> " + path + MemoryGlobal.nameSSLTCPFile, TypeMonitor.monitor, null);
			  
			  
			   File keyStoreFile = new File(path + MemoryGlobal.nameSSLTCPFile);
			   File trustStoreFile = new File(path + MemoryGlobal.nameSSLTCPFile);
		 
			   if (keyStoreFile.exists() && trustStoreFile.exists())
			   {
				    final KeyStoreFactory keyStoreFactory = new KeyStoreFactory();
				    System.out.println("Path KeyStore is: " + keyStoreFile.getAbsolutePath());
				    log.WriteLogMonitor("Path KeyStore is: " + keyStoreFile.getAbsolutePath(), TypeMonitor.monitor, null);
				    keyStoreFactory.setDataFile(keyStoreFile);
				    keyStoreFactory.setPassword(MemoryGlobal.passSSLTCPFile);
				 
				    final KeyStoreFactory trustStoreFactory = new KeyStoreFactory();
				    trustStoreFactory.setDataFile(trustStoreFile);
				    trustStoreFactory.setPassword(MemoryGlobal.passSSLTCPFile);
				 
				    final SslContextFactory sslContextFactory = new SslContextFactory();
				    final KeyStore keyStore = keyStoreFactory.newInstance();
				    sslContextFactory.setKeyManagerFactoryKeyStore(keyStore);
				 
				    final KeyStore trustStore = trustStoreFactory.newInstance();
				    sslContextFactory.setTrustManagerFactoryKeyStore(trustStore);
				    sslContextFactory.setKeyManagerFactoryKeyStorePassword(MemoryGlobal.passSSLTCPFile);
				    sslContext = sslContextFactory.newInstance();
				    System.out.println("SSL provider is: " + sslContext.getProvider());
				    log.WriteLogMonitor("SSL provider is: " + sslContext.getProvider(), TypeMonitor.monitor, null);
				    SSLParameters params = sslContext.getDefaultSSLParameters(); 
				    System.out.println("SSL context params - need client auth: {} want client auth: {} endpoint id algorithm: {}" + params.getNeedClientAuth() + " - " + params.getWantClientAuth() + " - " + params.getEndpointIdentificationAlgorithm()); 
				    String[] supportedProtocols = params.getProtocols(); 
				    for (String protocol : supportedProtocols) { 
				    	System.out.println("Protocolos SSL Soportados: " +  protocol);
				    	log.WriteLogMonitor("Protocolos SSL Soportados: " +  protocol, TypeMonitor.monitor, null);
				    } 
			   }
			   else
			   {
				   System.out.println("Archivo(s) Keystore o Truststore No Existen!!");
			   }
		  }
		  catch (Exception ex)
		  {
			  ex.printStackTrace();
		  }
		  return sslContext;
	 }
}
