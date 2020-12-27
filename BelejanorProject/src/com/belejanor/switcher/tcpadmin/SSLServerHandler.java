package com.belejanor.switcher.tcpadmin;

import org.apache.mina.core.session.IdleStatus;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.binary.Base64;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;

import com.belejanor.switcher.asextreme.ExtremeRequest;
import com.belejanor.switcher.asextreme.HField;
import com.belejanor.switcher.cscoreswitch.AdminConcurrentMap;
import com.belejanor.switcher.cscoreswitch.ContainerIsoQueue;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.queueadmin.Queue;
import com.belejanor.switcher.queueadmin.typeMessage;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;


public class SSLServerHandler extends IoHandlerAdapter
{
	
	private class SecuencialClass{ private String Sec;

		public String getSec() {
			return Sec;
		}
		public void setSec(String sec) {
			Sec = sec;
		}  
    }
	
	private Logger log;
	private boolean flagError;
	private String causeError;
	
	public SSLServerHandler(){
		log = new Logger();
		this.flagError = false;
		this.causeError = "PROCESO EXITOSO";
	}
 
	 @Override
	 public void sessionOpened(IoSession session)
	 {
		 System.out.println("Server Session Opened"  + session); 
		 log.WriteLogMonitor("Server Session Opened"  + session, TypeMonitor.monitor, null);
	 }
 
	 @Override
	 public void messageReceived(IoSession session, Object message)
	 {
		 
		 org.apache.mina.core.buffer.IoBuffer data = null;
		 System.out.println("Mensaje recibido IP: " + session.getRemoteAddress().toString());
		 log.WriteLogMonitor("Mensaje recibido IP: " + session.getRemoteAddress().toString(), 
				             TypeMonitor.monitor, null);
		 Response res = new Response();
		 ContainerIsoQueue<Object> cont = null;
		  try {
			  
	            if (message instanceof String) {
					
	            	SecuencialClass secu = new SecuencialClass();
	            	String trama = message.toString();
	            	System.out.println(trama);
	            	String mensajeDepurado = processMessageHeader(trama);
	            	if(this.flagError){
	            		
	            		log.WriteLog(mensajeDepurado, TypeLog.alexsoft, TypeWriteLog.file);
	            		ExtremeRequest messageReq = (ExtremeRequest) 
			    			       SerializationObject.StringToObject(mensajeDepurado);
	            		
	            		if(messageReq != null){
				    		
				    		HField sec1 = messageReq.getHeader().getHfield().stream()
				    			    .filter(a -> a.getName().equals("TraceId"))
				    			    .findFirst().orElseGet(() -> null);
				    		
					    	HField sec = messageReq.getHeader().getHfield().stream()
					    			    .filter(a -> a.getName().equals("TerminalSeq"))
					    			    .findFirst().orElseGet(() -> null);
					    	
					    	if(sec != null && sec1 != null){
					    		
					    		cont = new ContainerIsoQueue<>(messageReq , getIpAddresClient(session));
						    	cont.setSecuencial(sec1.getValue() + "_" + sec.getValue());
						    	secu.setSec(sec1.getValue() + "_" + sec.getValue());
						    	
					    	}else {
					    		
					    		data = DataSend("Trama Invalida");
				            	data.flip();
				            	session.write(data);
							}
				    		
				    	}else{
				    		
				    		data = DataSend("Error al Deserealizar Mensaje AlexStreme");
			            	data.flip();
			            	session.write(data);
				    	}
	            		
	            	}
	            	
	            	Queue queue = new Queue();
					queue.SendMessage(typeMessage.initialMessage, cont, 1, 0);

					final CountDownLatch semaphore = new CountDownLatch(1);
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							
							while (true) {
								synchronized(MemoryGlobal.concurrentIso) {
									if(MemoryGlobal.concurrentIso.containsKey(secu.getSec())){
										res.setMessResponse((String) MemoryGlobal.concurrentIso.get(secu.getSec()));
										@SuppressWarnings("unused")
										AdminConcurrentMap map = new AdminConcurrentMap(secu.getSec());
										semaphore.countDown();
										break;
									}
								}
							}
						}
					});
					t.start();
	            	
					if(!semaphore.await(50000, TimeUnit.MILLISECONDS))
						res.setMessResponse("909TIEMPO EXPIRADO");
					
					//Respuesta correcta final
					data = DataSend(res.getMessResponse());
	            	data.flip();
	            	session.write(data);
	            	
				}else {
					
					data = DataSend(this.causeError);
					data.flip();
					session.write(data);
				}
			  	
			} catch (Exception e) {
				
				e.printStackTrace();
				this.flagError = true;
				this.causeError = GeneralUtils.ExceptionToString(null, e, true);
				data = DataSend(this.causeError);
				data.flip();
				session.write(data);
			}
		  
	 }
	 
	 private org.apache.mina.core.buffer.IoBuffer DataSend(String mensaje){
		 org.apache.mina.core.buffer.IoBuffer data = 
				 org.apache.mina.core.buffer.IoBuffer.allocate(mensaje.length() + 2);
		 try {
				byte[] compress = StringUtils.compress(mensaje, "UTF-8");
				byte[] tramas = Base64.encodeBase64(compress);
				byte[] header = new byte[]{ (byte)((tramas.length + 2)/ 256), (byte) ((tramas.length + 2) % 256) };
				byte[] tramaTotal = new byte[tramas.length + 2];
				System.arraycopy(header, 0, tramaTotal, 0, header.length);
				System.arraycopy(tramas, 0, tramaTotal, 2, tramas.length);
         	    data.put(tramaTotal);
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo SSLServerHandler::DataSend ", TypeMonitor.error, e);
			return null;
		}
		 return data;
	 }
 
	 @Override
	 public void sessionIdle(IoSession session, IdleStatus status)
	 {
		
	 }
 
	 @Override
	 public void exceptionCaught(IoSession session, Throwable cause)
	 {
		 System.out.println("Error!!!!!!!!!!!!!! " + cause.getMessage());
	     
	 }
 
    @Override
	public void sessionClosed(IoSession session) {
		System.out.println("IP: "  +  session.getRemoteAddress().toString() +  " Close");
		log.WriteLogMonitor("IP: "  +  session.getRemoteAddress().toString() + " Close", TypeMonitor.monitor, null);
	}
 
	 public void apply(IoSession session) throws SocketException {
		    org.apache.mina.core.session.IoSessionConfig sessionConfig = session.getConfig();
		    
		    if (sessionConfig instanceof SocketSessionConfig) {
		        SocketSessionConfig socketSessionConfig = (SocketSessionConfig) sessionConfig;
		            socketSessionConfig.setSendBufferSize(1024 * 1024);  
		    }
	  }
	 
	 protected String getIpAddresClient(IoSession session){
	    	String ip = session.getRemoteAddress().toString().replace("/", "");
	    	ip = Arrays.asList(ip.split(":")).get(0);
	        return ip;
	 }
	 
	 private String processMessageHeader(String mensaje){
			
			String message = null;
			try {
				
				byte[] barr = Base64.decodeBase64(mensaje.getBytes());
				message = StringUtils.decompress(barr, "UTF-8").replace("??", "");
				this.flagError = true;
				
			} catch (Exception e) {
				
				this.flagError = true;
				this.causeError = GeneralUtils.ExceptionToString(null, e, true);
				e.printStackTrace();
				log.WriteLogMonitor("Error modulo processMessageHeader::processMessageHeader ", TypeMonitor.error, e);
			}
			
			return message;
	  }
	
}
