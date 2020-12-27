package com.belejanor.switcher.respuestasspiimp;

import java.util.Date;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.bridges.BridgeSPI_BCE;
import com.belejanor.switcher.cscoreswitch.ICoreClassSPIRespuestas;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;

import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;

@WebService(targetNamespace = "http://www.bce.fin.ec/wsdl/snp/RespuestaServiciosSNPWS", 
portName = "RespuestaServiciosSNPWSPort", 
serviceName = "RespuestaServiciosSNPWSService")
@XmlRootElement(namespace = "http://www.bce.fin.ec/wsdl/snp/RespuestaServiciosSNPWS")
@XmlType(namespace = "http://www.bce.fin.ec/wsdl/snp/RespuestaServiciosSNPWS")
public class RespuestaServiciosSNPWS implements ICoreClassSPIRespuestas{
	
	@Override
	public Date recibirRespuestaServiciosSNP(DocumentRespuesta documentoRespuestaSolicitud) throws Error {
		
		try {
			Logger log = new Logger();
			log.WriteLogMonitor("**** Entro MENSAJE ASYNCRONO DE RESPUESTAS DEL BCE ********", TypeMonitor.monitor, null);
			
		} catch (Exception e) {
			
		}
		
		BridgeSPI_BCE bridge = new BridgeSPI_BCE();
		return bridge.ProcesaRespuestaTransferenciaSPI(documentoRespuestaSolicitud, ObtainIpClient());
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