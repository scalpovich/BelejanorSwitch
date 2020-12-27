package com.belejanor.switcher.implementations.servipagos;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.authorizations.ITransactionWebServipagos;
import com.belejanor.switcher.bridges.BridgeServipagos;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.struct.servipagos.Error;


@WebService(targetNamespace = "http://servipagos.implementations.middleware.fitbank.com/", portName = "ExecuteTransactionServipagosPort", serviceName = "ExecuteTransactionServipagosService")
public class ExecuteTransactionServipagos implements ITransactionWebServipagos{
	
	@SuppressWarnings("rawtypes")
	@Override
	public String ProcesaTransacciones(String xml) throws Error{
		
		Logger log = new Logger();
		log.WriteOptimizeLog(xml, TypeLog.srvPgAcq, String.class, false);
		BridgeServipagos bridge = new BridgeServipagos<>();
		String data = bridge.ProcesaTransacciones(xml, ObtainIpClient());
		if(!bridge.getCodError().equals("999")){
			
			log.WriteOptimizeLog(data, TypeLog.srvPgAcq, String.class, false);
			return data;
			
		}else {
			
			log.WriteOptimizeLog(("Error en Procesos " + bridge.getCodError() + " - " + bridge.getDesError()), TypeLog.srvPgAcq, String.class, false);
			throw new Error("0" + bridge.getCodError(), bridge.getDesError());
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
