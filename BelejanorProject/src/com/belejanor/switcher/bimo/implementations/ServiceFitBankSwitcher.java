package com.belejanor.switcher.bimo.implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.bimo.genericerror.Document;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.parser.BimoParser;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;


@Path("/AuthorizationsFitBank")
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
public class ServiceFitBankSwitcher {

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Object TransactionRequest(@Context HttpServletRequest request){
		
		String trama = null;
		Object response = null;
		Logger log = new Logger();
		try {
			
			trama = getBody(request);
			
			if(!StringUtils.IsNullOrEmpty(trama)){
				
				log.WriteOptimizeLog(trama, TypeLog.brdAcq, String.class, false);
				BimoParser parser = new BimoParser();
				response = parser.parsePackToIso(trama ,ObtainIpClient());
				if(!parser.getCodError().equals("000")){
				
					Document doc = new Document(parser.getCodError() + "|" + parser.getDesError());
					log.WriteOptimizeLog(doc, TypeLog.brdAcq, Document.class, true);
					return doc;
			    }
				
			}else{
				
				Document doc = new Document( "100|ERROR AL PROCESAR LA TRANSACCION TRAMA INVALIDA");
				log.WriteOptimizeLog(doc, TypeLog.brdAcq, Document.class,true);
				return doc;
			}
		
		} catch (IOException e) {
			
			Document doc = new Document( "070|" + GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteOptimizeLog(doc, TypeLog.brdAcq, Document.class, true);
			return doc;
		}
		return response;
	}
	private String getBody(HttpServletRequest request) throws IOException {

	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;

	    try {
	        InputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	                throw ex;
	            }
	        }
	    }
	    body = stringBuilder.toString();
	    return body;
	}
	@Resource  
	WebServiceContext wsContext; 
	private String ObtainIpClient() {
		try {
			
			org.apache.cxf.message.Message message = PhaseInterceptorChain.getCurrentMessage();
			HttpServletRequest request = (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);
			return request.getRemoteAddr();
			
		} catch (Exception e) {
			return "undefined-error";
		}
		
	}

}
