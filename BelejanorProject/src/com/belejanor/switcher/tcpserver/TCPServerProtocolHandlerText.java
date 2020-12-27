package com.belejanor.switcher.tcpserver;

import java.util.Arrays;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;
import com.belejanor.switcher.memcached.Config;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;

public class TCPServerProtocolHandlerText extends IoHandlerAdapter{
	
	private Logger log;
	private boolean flagError;
	private String causeError;
	private boolean headerVli;
	private TypeLog tipoLog;
	
	public TCPServerProtocolHandlerText() {
		
		log = new Logger();
		this.flagError = false;
		this.causeError = "PROCESO EXITOSO";
		this.headerVli = true;
		this.tipoLog = TypeLog.report;
		
	}

	@Override
	  public void sessionCreated(IoSession session) {
	      session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
	      session.setAttribute(SslFilter.USE_NOTIFICATION);
	      
	  }

	  @Override
	  public void sessionClosed(IoSession session) throws Exception {
		  System.out.println("[TXT:] SESSION CLOSED: "  +  session.getRemoteAddress().toString());
		  log.WriteLogMonitor("[TXT:] SESSION CLOSED: "  +  session.getRemoteAddress().toString(), TypeMonitor.monitor, null);
	  }

	  @Override
	  public void sessionOpened(IoSession session) throws Exception {
		  
		  log.WriteLogMonitor("[TXT:] SESSION OPENED " + session.toString(), TypeMonitor.monitor, null);
		  System.out.println("[TXT:] SESSION OPENED " + session.toString());
	  }

	  @Override
	  public void sessionIdle(IoSession session, IdleStatus status) {
		  System.out.println("***[TXT:] IDLE #" + session.getIdleCount(IdleStatus.BOTH_IDLE) + " ***");
	  }

	  @SuppressWarnings("deprecation")
	  @Override
	  public void exceptionCaught(IoSession session, Throwable cause) {
		  System.out.println("[TXT:] Error!!!! " + cause.getMessage());
	      session.close(true);
	  }

	  @Override
	  public void messageReceived(IoSession session, Object message) {
		  
		  boolean isUpperPrefix = false;
		  org.apache.mina.core.buffer.IoBuffer data = null;
		  log.WriteLogMonitor("[TXT:] MENSAJE RECIBIDO IP: " + session.getRemoteAddress().toString(), TypeMonitor.monitor, null);
		  try {
			  
			  System.out.println(message.getClass());
			  System.out.println( "Received : " + message );
			  IoBuffer buffer = null;
			  byte[] b = null;
			  
			  if (message instanceof IoBuffer) {
				
	                buffer = (IoBuffer) message;
	                b = new byte[buffer.remaining()];
	                buffer.get(b);
	                buffer.flip();
	                String mensajeDepurado = processMessageWithHeader(b);
	                System.out.println("---------------- " + mensajeDepurado);
	                System.out.println("Llego: " + new String(b));
	                
	                if(!this.flagError){
	                
	                  
	                  String IsoResponse = StringUtils.Empty();
	                  String _PREFIX = mensajeDepurado.substring(0,3).toUpperCase();
	                  getTipoLog(_PREFIX);
	                  log.WriteLog(mensajeDepurado, this.tipoLog, TypeWriteLog.file);
	                  isUpperPrefix = StringUtils.isUpper(mensajeDepurado.substring(0,3));
	                  Iso8583 isoReq = new Iso8583(mensajeDepurado, _PREFIX);
	                  Iso8583 isoRes = null;
	                  if(isoReq.getISO_039_ResponseCode().equals("004")){
	                	  
	                	  csProcess processor = new csProcess();
	                	  isoRes = processor.ProcessTransactionMain(isoReq, getIpAddresClient(session));
	                	  if(this.headerVli){
	                		  if(isUpperPrefix)
	                	  	 	 IsoResponse = _PREFIX + isoRes.Iso8583ObjToIsoText(isoRes, _PREFIX, isUpperPrefix);
	                		  else
	                			  IsoResponse = _PREFIX.toLowerCase() + isoRes.Iso8583ObjToIsoText(isoRes, _PREFIX, isUpperPrefix);
	                	  }else
	                		 IsoResponse = isoRes.Iso8583ObjToIsoText(isoRes, _PREFIX, isUpperPrefix);
	                	  
	                	  if(IsoResponse.contains("SYSTEM_MAL_FUNCTION")){
	                		  if(!isUpperPrefix)
	                			  IsoResponse.replace(_PREFIX.toLowerCase(), StringUtils.Empty());
	                		  else
	                			  IsoResponse.replace(_PREFIX, StringUtils.Empty());
	                	  }
	                	  
	                  }else{
	                	  
	                	  IsoResponse = "SYSTEM_MAL_FUNCTION: " + isoReq.getISO_039_ResponseCode() + 
	                			  "-" + isoReq.getISO_039p_ResponseDetail();
	                	  System.out.println("A. Se responde: " + IsoResponse);
	                  }
	                  
					  String tramaResponse = IsoResponse;
					  if(this.headerVli)
					  	 data = DataSend(tramaResponse);
					  else
						 data = DataSendWithOutVli(tramaResponse);
					  System.out.println("Se responde: " + tramaResponse);
					  log.WriteLog(tramaResponse, this.tipoLog, TypeWriteLog.file);
		              data.flip();
		              session.write(data);
	                
					}else {
						
						log.WriteLogMonitor("==> " + message.toString(), TypeMonitor.monitor, null);
					}
			  }
            	
		 }catch (Exception e) {
			
		    e.printStackTrace();
			this.flagError = true;
			this.causeError = GeneralUtils.ExceptionToString(null, e, true);
			data = DataSend(this.causeError);
			data.flip();
			session.write(data);
		 }
		
	}
	protected String processMessageWithHeader(byte[] msg){
	    	
	    	String message = null;
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
				//byte[] barr = Base64.decodeBase64(buffer);
				message = new String(buffer);//StringUtils.decompress(barr, "UTF-8").replace("??", "");
				this.flagError = false;
				
						  
			} catch (Exception e) {
				
				if((new String(msg).startsWith("1200") || new String(msg).startsWith("1400"))  
						&&  new String(msg).length() == 659){
					
					message = new String(msg);
					this.flagError = false;
					this.headerVli = false;
					
				}else{
				
					this.flagError = true;
					this.causeError = GeneralUtils.ExceptionToString(null, e, true);
					e.printStackTrace();
					log.WriteLogMonitor("Error modulo TCPServerProtocolHandler::processMessageHeader ", TypeMonitor.error, e);
				}
			}
	    	return message;
	    }
	    
	    protected String getIpAddresClient(IoSession session){
	    
	    	try {
				
	    		String ipAddress = session.getRemoteAddress().toString();
		    	ipAddress = Arrays.asList(ipAddress.split(":")).get(0).substring(1);
		        return ipAddress;
	    		
			} catch (Exception e) {
				
				log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::getIpAddresClient" , TypeMonitor.error, e);
				return "127.0.0.1";
			}
	    
	    }
	  
	    private org.apache.mina.core.buffer.IoBuffer DataSend(String mensaje){
			 
	    	org.apache.mina.core.buffer.IoBuffer data = null;
			 try {
				 
					byte[] tramas = mensaje.getBytes();
					byte[] header = new byte[]{ (byte)((tramas.length + 2)/ 256), (byte) ((tramas.length + 2) % 256) };
					byte[] tramaTotal = new byte[tramas.length + 2];
					
					data = org.apache.mina.core.buffer.IoBuffer.allocate(tramaTotal.length + 2);
					System.arraycopy(header, 0, tramaTotal, 0, header.length);
					System.arraycopy(tramas, 0, tramaTotal, 2, tramas.length);
	         	    data.put(tramaTotal);
				
			} catch (Exception e) {
				
				log.WriteLogMonitor("Error modulo TCPServerProtocolHandlerText::DataSend ", TypeMonitor.error, e);
				return null;
			}
			 return data;
		 }
	    private org.apache.mina.core.buffer.IoBuffer DataSendWithOutVli(String mensaje){
			 
	    	org.apache.mina.core.buffer.IoBuffer data = null;
			 try {
				 
					byte[] tramas = mensaje.getBytes();
					data = org.apache.mina.core.buffer.IoBuffer.allocate(tramas.length);
	         	    data.put(tramas);
				
			} catch (Exception e) {
				
				log.WriteLogMonitor("Error modulo TCPServerProtocolHandlerText::DataSendWithOutVli ", TypeMonitor.error, e);
				return null;
			}
			 return data;
		}
	    
	    private void getTipoLog(String Prefix){
	    	
	    	try {
				
	    		Config conf = new Config();
	    		conf = conf.getConfigSystem(Prefix);
	    		if(conf != null){
	    			
	    			this.tipoLog = (TypeLog) Enum.valueOf(TypeLog.class, conf.getCfg_Valor()
	    					.replace("TypeLog.", StringUtils.Empty()));
	    		}
	    		
			} catch (Exception e) {
				
				log.WriteLogMonitor("Error modulo TCPServerProtocolHandlerText::getTipoLog ", TypeMonitor.error, e);
			}
	    }
}
