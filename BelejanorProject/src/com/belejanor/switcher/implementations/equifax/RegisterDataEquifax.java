package com.belejanor.switcher.implementations.equifax;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.acquirers.IRegisterEquifax;
import com.belejanor.switcher.bridges.BridgeEquifax;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.struct.equifax.ErrorRegister;
import com.belejanor.switcher.struct.equifax.RegisterData;
import com.belejanor.switcher.struct.equifax.ResponseRegisterDataEquifax;
import com.belejanor.switcher.utils.GeneralUtils;

@WebService(targetNamespace = "http://equifax.implementations.middleware.fitbank.com/", portName = "RegisterDataEquifaxPort", serviceName = "RegisterDataEquifaxService")
public class RegisterDataEquifax implements IRegisterEquifax{

	@Override
	public ResponseRegisterDataEquifax insertarEvaluacion(RegisterData dataIn) throws ErrorRegister {
		
		Logger log = new Logger();
		try {
			
			log.WriteOptimizeLog(dataIn, TypeLog.equifxAcq, RegisterData.class, true);
			
			ResponseRegisterDataEquifax response = new ResponseRegisterDataEquifax();
			BridgeEquifax bridge = new BridgeEquifax();
			response = bridge.procesaRegistroEquifax(dataIn, ObtainIpClient());
			if(bridge.getCodError().equals("999")) {
				
				log.WriteOptimizeLog("Se responde con Error: " + bridge.getCodError() + " - " + bridge.getDesError()
				, TypeLog.equifxAcq, ResponseRegisterDataEquifax.class, false);
				throw new ErrorRegister(bridge.getCodError(), bridge.getDesError());
			}else {
				
				log.WriteOptimizeLog(response, TypeLog.equifxAcq, ResponseRegisterDataEquifax.class, true);
				return response;
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo RegisterDataEquifax::insertarEvaluacion ", TypeMonitor.error, e);
			throw new ErrorRegister("999", GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, true));
			
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
