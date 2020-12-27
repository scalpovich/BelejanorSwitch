package com.belejanor.switcher.authorizations;

import java.io.Serializable;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlRootElement;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;
import com.fitbank.client.rest.ConsumerRestProsupply;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
//import org.codehaus.jackson.map.ObjectMapper;

public class ProsupplyIsAut {

	private Logger log;
	
	public ProsupplyIsAut() {
		
		log = new Logger();
	}
	
	public wIso8583 BlockTDD(wIso8583 iso) {
		
		try {
			log.WriteLogMonitor("Ingresando a bloqueo de TDD 29......", TypeMonitor.monitor, null);
			iso.setISO_041_CardAcceptorID("1800-292929");
			iso.getTickAut().reset();
			iso.getTickAut().start();
				iso = BlqTDDExtern(iso);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::sendSMS"
					, TypeMonitor.error, e);
		}finally {
		
			if(iso.getISO_039_ResponseCode().equals("000")) {
				
				iso.setAuxiliarArrayValues(new String[] { "name|" + FormatUtils.DateToString(new Date(), "yyyy/MM/dd") + "|" 
						+ FormatUtils.DateToString(new Date(), "HH:mm:ss"),
				        "#|" + FormatUtils.DateToString(new Date(), "yyyy/MM/dd") + "|" + FormatUtils.DateToString(new Date(), "HH:mm")});
			}
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
	private wIso8583 BlqTDDExtern(wIso8583 iso) {
		
		String[] response = new String[2];
		try {
			
			ConsumerRestProsupply processor = new ConsumerRestProsupply();
			response = processor.BlockTDDRest(iso.getISO_002_PAN(), MemoryGlobal.restAction, MemoryGlobal.restUserAuth, 
					MemoryGlobal.restUserPassw, MemoryGlobal.restUserRest, 
					MemoryGlobal.restUrlProsupply, MemoryGlobal.restMediaType);
			iso.setISO_039_ResponseCode(response[0]);
			iso.setISO_039p_ResponseDetail(response[1]);
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::BlqTDD"
					, TypeMonitor.error, e);
		}
		
		return iso;
	}
	
	@SuppressWarnings("unused")
	private wIso8583 BlqTDDRest(wIso8583 iso) {
		
		Request rq = null;
		Client client = null;
		String responseString = StringUtils.Empty();
		try {
			
			rq = new Request();
			rq.setAction("BlockCardsIVR");
			rq.setAuthenticationUser("29OctubreIsClient");
			rq.setAuthenticationPassword("290ctubr3");
			
			String auth =  "{\"Identification\" : "+ iso.getISO_002_PAN() +", \"UserName\": \"prosupply\"}";
			rq.setParameters(auth);
			
			client = Client.create();
			
			WebResource webResource = client
					   .resource("http://127.0.0.1:5050/api/CardMaster/services");	
			try {
				
				Class<?> classNameOrigen = Request.class;
				ClientResponse docRes =  webResource.type("application/json")
						   .post(ClientResponse.class, classNameOrigen.cast(rq));
				
				if(docRes.getStatus() == 200){
					System.out.println("ok");
					
					docRes.bufferEntity();
					responseString = docRes.getEntity(String.class);
					//Response res =   new ObjectMapper().readValue(responseString, Response.class);
						
					//iso.setISO_039_ResponseCode(StringUtils.padLeft(String.valueOf(res.getResponseCode()),3,"0"));
					//iso.setISO_039p_ResponseDetail(res.getResponseDescription().toUpperCase());
					
				}else {
					System.out.println("Nok" + docRes.getStatus());
				}
				
			} catch (Exception e) {
				
				if (e.getCause() instanceof SocketTimeoutException || e.getCause() instanceof java.util.concurrent.TimeoutException) {
					   
					   iso.setISO_039_ResponseCode("907");
					   iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("HA EXPIRADO EL TIEMPO DE ESPERA A "
					   		+ "SERVICIOS REST DE PROSUPPLY", e, true));
				       iso.setWsIso_LogStatus(9);
				       iso.setWsISO_FlagStoreForward(true);
					}else if (e.getCause() instanceof SocketException) {
						
						 iso.setISO_039_ResponseCode("906");
						 iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("SERVICIO REST PROSUPPLY NO HABILITADO, SOCKET_EXCEPTION (CONNECTION RESET) "
						   		, e, true));
					     iso.setWsIso_LogStatus(9);
						
					}else if (e.getCause() instanceof ClientHandlerException) {
						
						 iso.setISO_039_ResponseCode("901");
						 iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("NO SE PUDO TENER CONEXION CON SERVICIO REST PROSUPPLY (CLIENTHANDLER_EXCEPTION) "
						   		, e, true));
					     iso.setWsIso_LogStatus(9);
					}else if (e.getCause() instanceof WebApplicationException) {
						
						 iso.setISO_039_ResponseCode("904");
						 iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("NO SE PUDO TENER CONEXION CON SERVICIO BIMO BANRED (WEBAPPLICATION_EXCEPTION) "
						   		, e, true));
					     iso.setWsIso_LogStatus(9);
					     
					}else{
						
						iso.setISO_039_ResponseCode("908");
						iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
					}
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::BlqTDD"
					, TypeMonitor.error, e);
		}
		return iso;
	}
	
	@SuppressWarnings("serial")
	@XmlRootElement
	public class Request implements Serializable{

		private String AuthenticationUser; 
		private String AuthenticationPassword;
		private String Action;
		private String Parameters;
		
		
		public String getAuthenticationUser() {
			return AuthenticationUser;
		}
		public void setAuthenticationUser(String authenticationUser) {
			AuthenticationUser = authenticationUser;
		}
		public String getAuthenticationPassword() {
			return AuthenticationPassword;
		}
		public void setAuthenticationPassword(String authenticationPassword) {
			AuthenticationPassword = authenticationPassword;
		}
		public String getAction() {
			return Action;
		}
		public void setAction(String action) {
			Action = action;
		}
		public String getParameters() {
			return Parameters;
		}
		public void setParameters(String parameters) {
			Parameters = parameters;
		}
	}

	@SuppressWarnings("serial")
	@XmlRootElement
	public class Response implements Serializable{

		private int ResponseCode;
	    private String ResponseDescription;
		public int getResponseCode() {
			return ResponseCode;
		}
		public void setResponseCode(int responseCode) {
			ResponseCode = responseCode;
		}
		public String getResponseDescription() {
			return ResponseDescription;
		}
		public void setResponseDescription(String responseDescription) {
			ResponseDescription = responseDescription;
		}
	}
	
}


