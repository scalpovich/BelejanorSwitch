package com.belejanor.switcher.tcpclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.GzipUtil;
import com.belejanor.switcher.utils.StringUtils;

public class SocketClient {

	private String Ip;
	private int Port;
	@SuppressWarnings("unused")
	private boolean closeFlagConn;
	private Logger log;
	@SuppressWarnings("unused")
	private byte [] response;
	private Socket socket;
	private String detailError;

	public String getDetailError() {
		return detailError;
	}

	public SocketClient(String Ip, int Port) {
		
		log = new Logger();
	    this.Ip = Ip;
	    this.Port = Port;
	    this.response = null; 
	    this.detailError = "OK";
	    
	    try
	    {
	     
	      this.socket = openSocket(this.Ip, this.Port);
	      //byte[] result = writeToAndReadFromSocket(socket, Mensaje);
	      //this.response = result;
	      //socket.close();
	      
	    }
	    catch (Exception e)
	    {
	    	this.detailError = GeneralUtils.ExceptionToString("ERROR AL CONECTAR CON JNP/TCP ", e, false);
	    	log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::SocketClient(construct)", TypeMonitor.error, e);
	    	e.printStackTrace();
	    }
	}
	
	public String SendSocketClient(String trama){
		
		System.out.println("Listo para enviar: " + trama);
		String data = StringUtils.Empty();
		byte[] response = null;
		try {
			
			if(trama.length() >= 2000) {
				/*Envio*/
				//response = StringUtils.compress(trama, "UTF-8");
				response = GzipUtil.zip(trama);
				//response = Base64.encodeBase64(response);
			}
			
			response = writeToAndReadFromSocket(this.socket, response);
			if(response != null && "OK".equalsIgnoreCase(this.detailError)){
			
				/*Recibir*/
				//if(org.apache.mina.util.Base64.isArrayByteBase64(response)) {
				if(GzipUtil.isZipped(response)) {
					
					//byte[] barr = Base64.decodeBase64(response);
					data = StringUtils.decompress(response, "UTF-8").replace("??", "");
					
				}else {
					
					data = new String(response);
				}
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::SendSocketClient", TypeMonitor.error, e);
			this.detailError = GeneralUtils.ExceptionToString("ERROR AL ENVIAR TRAMA JNP/TCP", e, false);
			
		}finally {
			
			if(this.socket.isConnected())
				try {
					this.socket.close();
				} catch (IOException e) {
					
					this.detailError = GeneralUtils.ExceptionToString("ERROR AL CERRAR SOCKET JNP/TCP", e, false);
				}
		}

		return data;
	}
	
	private byte[] writeToAndReadFromSocket(Socket socket, byte[] tramaString) throws Exception
	  {
	    try 
	    {
	       //OJO VLI QUIERE DECIR QUE EL CAMPO VIENE AL PRINCIPIO INCLUIDO LA LONGITUD	
	       DataOutputStream output = SocketIO.getDataOutput(socket); 
	       byte[] trama = tramaString;
	       System.out.println("Trama Rq: " + tramaString);
	       byte[] tramaTotal = new byte[trama.length + 2];
	       byte[] header = new byte[]{ (byte)((trama.length + 2)/ 256), (byte) ((trama.length + 2) % 256) }; //Enviar el mensaje siempre con 2 bytes de Header
	       
	       System.arraycopy(header, 0, tramaTotal, 0, header.length);
		   System.arraycopy(trama, 0, tramaTotal, 2, trama.length);
	       
	       byte[] bytes = tramaTotal;
	       int length = bytes.length;  
	       //socket.setSoTimeout(timeout);
	       output.write(bytes);  
	         
	       DataInputStream input = SocketIO.getDataInput(socket);  
	       Thread.sleep(300);
	       length = input.readShort();
	       bytes = new byte[length - 2];  //ojo modifique aqui le puse -2 quitar!!
	       input.read(bytes);  
	       
	       
	       
	       System.out.println("Long. res ---> " + bytes.length); 
	       System.out.println("Trama respuesta ---> " + new String(bytes)); 
	       this.detailError = "OK";
	       return bytes;
	      
	    } 
	    catch (IOException e) 
	    {
	       this.detailError = GeneralUtils.ExceptionToString("ERROR ENVAIR MENSAJE JNP/TCP ", e, false);	
	       e.printStackTrace();
	       log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::writeToAndReadFromSocket", TypeMonitor.error, e);
	       return null;
	    }
	 }
	private Socket openSocket(String server, int port) throws Exception
	{
	    Socket socket;
	    
	    try
	    {
	      InetAddress inteAddress = InetAddress.getByName(server);
	      SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);
	      socket = new Socket();
	      int timeoutInMs = 20*1000;
	      socket.connect(socketAddress, timeoutInMs);
	      return socket;
	    } 
	    catch (SocketTimeoutException ste) 
	    {
	    	this.detailError = GeneralUtils.ExceptionToString("ERROR OPEN SOCKET JNP/TCP ", ste, false);
	      log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::openSocket", TypeMonitor.error, ste);
	      System.err.println("Ha ocurrido un error, El Servidor se ha demorado mas de 20 segundos en responder...");
	      ste.printStackTrace();
	      throw ste;
	    }
	 }
	
	
}
