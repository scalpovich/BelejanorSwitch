package com.belejanor.switcher.cobrossci;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.bridges.BridgeSCI_BCE;
import com.belejanor.switcher.cscoreswitch.ICoreClassSCI;

import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.DocumentCobroSCI;

@WebService(targetNamespace = "http://www.bce.fin.ec/snp/sci/SolicitudCobroInterbancarioSNPWS", 
portName = "SolicitudCobroInterbancarioSNPWSPort", 
serviceName = "SolicitudCobroInterbancarioSNPWSService")
@XmlRootElement(namespace = "http://www.bce.fin.ec/snp/sci/SolicitudCobroInterbancarioSNPWS")
@XmlType(namespace = "http://www.bce.fin.ec/snp/sci/SolicitudCobroInterbancarioSNPWS")
public class SolicitudCobroInterbancarioSNPWS implements ICoreClassSCI{

	@Override
	public DocumentRespuesta recibirSolicitudOCI(DocumentCobroSCI documentoSolicitud) throws Error {
		
		DocumentRespuesta doc = new DocumentRespuesta();
		BridgeSCI_BCE bridge = new BridgeSCI_BCE();
		doc = bridge.ProcesaSolicitudCobrosSCI(documentoSolicitud, ObtainIpClient());
		if(!bridge.getCodError().equals("000")) 
			throw new Error(bridge.getCodError(),bridge.getDesError());
		else 
			return doc;
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
