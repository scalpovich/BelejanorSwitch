package com.belejanor.switcher.implementations.equifax;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.acquirers.ITranEquifax;
import com.belejanor.switcher.bridges.BridgeEquifax;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.struct.equifax.ConsultaDatosPersona;
import com.belejanor.switcher.struct.equifax.ConsultaDatosPersonaRespuesta;
import com.belejanor.switcher.struct.equifax.Error;
import com.belejanor.switcher.utils.GeneralUtils;

@WebService(targetNamespace = "http://equifax.implementations.middleware.fitbank.com/", portName = "GetDataEquifaxPort", serviceName = "GetDataEquifaxService")
public class GetDataEquifax implements ITranEquifax{
	

	@Override
	public ConsultaDatosPersonaRespuesta consultarDatosPersona(ConsultaDatosPersona data) throws Error {
		
		Logger log = new Logger();
		try {
			
			log.WriteOptimizeLog(data, TypeLog.equifxAcq, ConsultaDatosPersona.class, true);
			
			ConsultaDatosPersonaRespuesta response = new ConsultaDatosPersonaRespuesta();
			BridgeEquifax bridge = new BridgeEquifax();
			response = bridge.procesaConsultaEquifax(data, ObtainIpClient());
			if(bridge.getCodError().equals("999")) {
				
				log.WriteOptimizeLog("Se responde con Error: " + bridge.getCodError() + " - " + bridge.getDesError()
				, TypeLog.equifxAcq, ConsultaDatosPersonaRespuesta.class, false);
				throw new Error(bridge.getCodError(), bridge.getDesError());
			}else {
				
				log.WriteOptimizeLog(response, TypeLog.equifxAcq, ConsultaDatosPersonaRespuesta.class, true);
				return response;
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo GetDataEquifax::consultarDatosPersona ", TypeMonitor.error, e);
			throw new Error("999", GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, true));
			
		}
		
			
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
