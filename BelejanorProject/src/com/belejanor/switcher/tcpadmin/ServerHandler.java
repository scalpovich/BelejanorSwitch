package com.belejanor.switcher.tcpadmin;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.binary.Base64;
import org.apache.mina.api.IdleStatus;
import org.apache.mina.api.IoHandler;
import org.apache.mina.api.IoService;
import org.apache.mina.api.IoSession;
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

public class ServerHandler implements IoHandler {

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
	
	public ServerHandler() {
		
		this.log = new Logger();
		this.flagError = false;
		this.causeError = "PROCESO EXITOSO";
	}

	@Override
		public void sessionOpened(IoSession session) {
		
			System.out.println("Server Session Opened"  + session);
			log.WriteLogMonitor("Server Session Opened"  + session, TypeMonitor.monitor, null);
		}
	
		@Override
		public void sessionClosed(IoSession session) {
			System.out.println("IP: "  +  session.getRemoteAddress().toString() +  " Close");
			log.WriteLogMonitor("IP: "  +  session.getRemoteAddress().toString() +  " Close", TypeMonitor.monitor, null);
			
		}
	
		@Override
		public void sessionIdle(IoSession session, IdleStatus status) {
	
			System.out.println("Session Status: " +  status.toString());
			log.WriteLogMonitor("Session Status: " +  status.toString(), TypeMonitor.monitor, null);
		}
	
		@Override
		public void messageReceived(IoSession session, Object message) {
			
			System.out.println("Mensaje recibido de la IP: " + session.getRemoteAddress().toString());
			ByteBuffer bfResponse = null;
			Response res = new Response();
			ContainerIsoQueue<Object> cont = null;
			if (message instanceof ByteBuffer) {
				try {
					
					SecuencialClass secu = new SecuencialClass();
				    ByteBuffer bb = (ByteBuffer) message;
				    byte[] b = new byte[bb.remaining()];
				    bb.get(b);
				    
				    String trama = new String(b);
				    System.out.println(trama);  
				    String mensajeDepurado = processMessageHeader(b);
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
					    		
					    		bfResponse = ByteBuffer.wrap("TRAMA INVALIDA".getBytes());
							    session.write(bfResponse);
					    		return;
							}
				    		
				    	}else{
				    		
				    		bfResponse = ByteBuffer.wrap("Error al Deserealizar Mensaje AlexStreme".getBytes());
						    session.write(bfResponse);
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
						bfResponse = ByteBuffer.wrap(processMessageReturn(res.getMessResponse()));
					    session.write(bfResponse);
				    	
				    	
				    }else{
				    	
				    	bfResponse = ByteBuffer.wrap(this.causeError.getBytes());
					    session.write(bfResponse);
				    }
				
				} catch (Exception e) {
					e.printStackTrace();
					this.flagError = true;
					this.causeError = GeneralUtils.ExceptionToString(null, e, true);
					bfResponse = ByteBuffer.wrap(this.causeError.getBytes());
				    session.write(bfResponse);
				}
			} 
			  
		}
		
		private String processMessageHeader(byte[] inByteBuf){
			
			String message = null;
			try {
				int header = getHeader(inByteBuf);
				byte[] bufferTotal = new byte[header - 2];
				System.arraycopy(inByteBuf, 2, bufferTotal, 0, header - 2);
				message = new String(bufferTotal);
				byte[] barr = Base64.decodeBase64(bufferTotal);
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
		
		private int getHeader(byte[] buffer){
			
			int[] header = new int[2];
			try {
				
				header[0] = buffer[0];
				header[1] = buffer[1];
				if(header[1] < 0)
					header[1] = 256 - Math.abs(header[1]);
				return (header[0] * 256) + header[1];		
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}
		
		protected String getIpAddresClient(IoSession session){
	    	String ip = session.getRemoteAddress().toString().replace("/", "");
	    	ip = Arrays.asList(ip.split(":")).get(0);
	        return ip;
	    }
		
		protected byte[] processMessageReturn(String messageResponse){
	    	
	    	try {
	    		
	    		messageResponse = messageResponse.replace("&lt;**&gt;", "")
	    				.replace(" &lt;*&gt;", "");
	    		log.WriteLog(messageResponse, TypeLog.alexsoft, TypeWriteLog.file);
	    		byte[] compress = StringUtils.compress(messageResponse, "UTF-8");
	    		byte[] trama = Base64.encodeBase64(compress);
				byte[] header = new byte[]{ (byte)((trama.length + 2)/ 256), (byte) ((trama.length + 2) % 256)};
				System.out.println("Longitud respuesta texto: " + trama.length);
				System.out.println("Respuesta header respuesta A: " + header[0]);
				System.out.println("header respuesta B: " + header[1]);
				System.out.println("Respuesta header respuesta A: " + (header[1] > 0 ? header[1] : (256 - header[1])));
				byte[] totalTrama = new byte[trama.length + 2];
				System.arraycopy(header, 0, totalTrama, 0, header.length);
				System.arraycopy(trama, 0, totalTrama, 2, trama.length);	
				return totalTrama;
	    		
			} catch (Exception e) {
				log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::processMessageReturn" , TypeMonitor.error, e);
				return null;
			}
	    }
		
		@Override
		public void messageSent(IoSession session, Object message) {
			System.out.println("Send message: " +  message.toString());
			log.WriteLogMonitor("Send message: " +  message.toString(), TypeMonitor.monitor, null);
			System.out.println("Server send message: " + message.toString());
			log.WriteLogMonitor("Server send message: " + message.toString(), TypeMonitor.monitor, null);
		}
	
		@Override 
		public void serviceActivated(IoService service) {
	
			System.out.println("Socket Server Initialized... " + service.getSessionConfig());
			log.WriteLogMonitor("Socket Server Initialized... " + service.getSessionConfig(), TypeMonitor.monitor, null);
		}
	
		@Override
		public void serviceInactivated(IoService service) {
	
			System.out.println("Servicio Inactivo!!!");
			log.WriteLogMonitor("Servicio Inactivo!!!", TypeMonitor.monitor, null);
		}
	
		@Override
		public void exceptionCaught(IoSession session, Exception cause) {
	
			//cause.printStackTrace();
			log.WriteLogMonitor("Error modulo ServerHandler::exceptionCaught ", TypeMonitor.error, cause);
			session.close(true);
		}
}