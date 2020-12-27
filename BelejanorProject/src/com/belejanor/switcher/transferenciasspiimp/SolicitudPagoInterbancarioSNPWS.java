package com.belejanor.switcher.transferenciasspiimp;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.bridges.BridgeSPI_BCE;
import com.belejanor.switcher.cscoreswitch.ICoreClassSPI;

import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferenciaSPI;

@WebService(targetNamespace = "http://www.bce.fin.ec/snp/spi/SolicitudPagoInterbancarioSNPWS", 
portName = "SolicitudPagoInterbancarioSNPWSPort", 
serviceName = "SolicitudPagoInterbancarioSNPWSService")
@XmlRootElement(namespace = "http://www.bce.fin.ec/snp/spi/SolicitudPagoInterbancarioSNPWS")
@XmlType(namespace = "http://www.bce.fin.ec/snp/spi/SolicitudPagoInterbancarioSNPWS")
public class SolicitudPagoInterbancarioSNPWS implements ICoreClassSPI{

	public DocumentRespuesta recibirSolicitudOPI(DocumentTransferenciaSPI documentoSolicitud) throws Error {
		
	
		DocumentRespuesta doc = new DocumentRespuesta();
		BridgeSPI_BCE bridge = new BridgeSPI_BCE();
		doc = bridge.ProcesaSolicitudTransferenciaSPI(documentoSolicitud, ObtainIpClient());
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
