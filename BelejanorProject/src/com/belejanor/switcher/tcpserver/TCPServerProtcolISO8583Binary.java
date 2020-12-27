package com.belejanor.switcher.tcpserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;

import com.belejanor.switcher.cscoreswitch.AdminConcurrentMap;
import com.belejanor.switcher.cscoreswitch.ContainerIsoQueue;
import com.belejanor.switcher.cscoreswitch.Iso8583Binary;
import com.belejanor.switcher.cscoreswitch.ResponseMsgIsoBinary;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.queueadmin.Queue;
import com.belejanor.switcher.queueadmin.typeMessage;
import com.belejanor.switcher.utils.GeneralUtils;

public class TCPServerProtcolISO8583Binary extends IoHandlerAdapter{
	
	private class SecuencialClass{ private String Sec;

		public String getSec() {
			return Sec;
		}
		public void setSec(String sec) {
			Sec = sec;
		}  
    }
	
	public TCPServerProtcolISO8583Binary() {
		
		super();
	}

	@Override
	  public void sessionCreated(IoSession session) {
	      session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
	      session.setAttribute(SslFilter.USE_NOTIFICATION);
	  }

	  @Override
	  public void sessionClosed(IoSession session) throws Exception {
		  System.out.println("SESSION CLOSED: "  +  session.getRemoteAddress().toString());
	  }

	  @Override
	  public void sessionOpened(IoSession session) throws Exception {
		  
		  System.out.println("SESSION OPENED " + session.toString());
	  }

	  @Override
	  public void sessionIdle(IoSession session, IdleStatus status) {
		  System.out.println("En espera de Transacciones, Count: " + session.getIdleCount(IdleStatus.BOTH_IDLE) + " [Espera.....]");
	  }

	  @SuppressWarnings("deprecation")
	  @Override
	  public void exceptionCaught(IoSession session, Throwable cause) {
		  System.out.println("Error!!!! " + cause.getMessage());
	      session.close(true);
	  }

	  @SuppressWarnings("static-access")
	  @Override
	  public void messageReceived(IoSession session, Object message) {
		  
		  org.apache.mina.core.buffer.IoBuffer data = null;
		  try {
			  
			  ResponseMsgIsoBinary res = new ResponseMsgIsoBinary();
			  ContainerIsoQueue<Object> cont = null;
			  IoBuffer buffer = null;
			  String mensajeIso = null;
			  byte[] b = null;
			  SecuencialClass secu = new SecuencialClass();
			  if (message instanceof IoBuffer) {
				 
	                buffer = (IoBuffer) message;
	                b = new byte[buffer.remaining()];
	                buffer.get(b);
	                buffer.flip();
	                mensajeIso = new String(b);
	                System.out.println(mensajeIso);
	                byte[] bufferTrama = processMessageWithHeader(b);
	                Iso8583Binary isoBinary = new Iso8583Binary(bufferTrama);
	                
	                Thread th = new Thread(new Runnable() {
						@Override
						public void run() {
							GeneralUtils.setterLoggerBinary(bufferTrama);
						}
					});
	                th.start();
	                
	                if(isoBinary.getDe39_RespCode().equals("96")){
	                	
	                	secu.setSec(isoBinary.getDe11_STAN());
	                	
	                	cont = new ContainerIsoQueue<>(isoBinary , getIpAddresClient(session));
	                	cont.setSecuencial(secu.getSec());
	                	
	                	Queue queue = new Queue();
						queue.SendMessage(typeMessage.initialMessage, cont, 1, 0);
	                   
						final CountDownLatch semaphore = new CountDownLatch(1);
						Thread t = new Thread(new Runnable() {
							@Override
							public void run() {
								
								while (true) {
									synchronized(MemoryGlobal.concurrentIso) {
										if(MemoryGlobal.concurrentIso.containsKey(secu.getSec())){
											res.setIsoBin((Iso8583Binary) MemoryGlobal.concurrentIso.get(secu.getSec()));
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
							res.setIsoBin(new Iso8583Binary().GenericError());
						
						Iso8583Binary binary = new Iso8583Binary();
						byte[] respuesta = binary.getIso8583BinaryResponse(res.getIsoBin());
						data = DataSend(respuesta);
		            	data.flip();
		            	session.write(data);
		            	
	                }else{
	                	
	                	Iso8583Binary bin = new Iso8583Binary();
	                	bin = isoBinary.GenericError();
	                	byte[] responseError = bin.getIso8583BinaryResponse(bin);
	                	data = DataSend(responseError);
		            	data.flip();
		            	session.write(data);
	                	session.write(data);
	                }
			  }
		 
		 } catch (Exception e) {

			 System.out.println("Error modulo: " + this.getClass().getName() + 
					  "::messageReceived" + GeneralUtils.ExceptionToString(null, e, true));
		 }
	}
	  protected byte[] processMessageWithHeader(byte[] msg){
	    	
	    	byte[] message = null;
	    	try {
				
	    		byte[] bytes;
				bytes = msg;
				System.out.println("Llego total: " + bytes.length);
				int a = bytes[0];
				System.out.println("Byte A: " + a);
				int b;
				if(bytes[1] < 0)
					b = 256 - Math.abs(bytes[1]);
				else
					b = bytes[1];
				System.out.println("Byte B: " + b);
				int longitudTrama = (a * 256) + b;
				System.out.println("Total lonfitud trama: " + longitudTrama);
				byte[] buffer = new byte[longitudTrama -2];	
				System.arraycopy(bytes, 2, buffer, 0, (longitudTrama - 2));
				message = buffer;
						  
			} catch (Exception e) {
				
				e.printStackTrace();
				
			}
	    	return message;
	    }
	    

	 protected String getIpAddresClient(IoSession session){
	    
	    	try {
				
	    		String ipAddress = session.getRemoteAddress().toString();
		    	ipAddress = Arrays.asList(ipAddress.split(":")).get(0).substring(1);
		        return ipAddress;
	    		
			} catch (Exception e) {
				
				System.out.println("Error modulo: " + this.getClass().getName() + 
						  "::getIpAddresClient" + GeneralUtils.ExceptionToString(null, e, true));
				return "127.0.0.1";
			}
	    
	 }
	 private org.apache.mina.core.buffer.IoBuffer DataSend(byte[] messageIso){
		 
		   org.apache.mina.core.buffer.IoBuffer data = null;
		   Thread th = new Thread(new Runnable() {
					@Override
					public void run() {
						GeneralUtils.setterLoggerBinary(messageIso);
					}
				});
	        th.start();
	    	
			 try {
				 
					byte[] header = new byte[]{ (byte)((messageIso.length + 2)/ 256), 
							(byte) ((messageIso.length + 2) % 256) };
					byte[] tramaTotal = new byte[messageIso.length + 2];
					
					data = org.apache.mina.core.buffer.IoBuffer.allocate(tramaTotal.length + 2);
					System.arraycopy(header, 0, tramaTotal, 0, header.length);
					System.arraycopy(messageIso, 0, tramaTotal, 2, messageIso.length);
	         	    data.put(tramaTotal);
				
			} catch (Exception e) {
				
				e.printStackTrace();
				return null;
			}
			 return data;
		 }
}
