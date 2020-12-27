package com.belejanor.switcher.reversotecnicospiimp;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.bridges.BridgeSPI_BCE;
import com.belejanor.switcher.cscoreswitch.ICoreClassReversoSPI;

import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;

@WebService(targetNamespace = "http://www.bce.fin.ec/wsdl/snp/ReversoTecnicoSNPSPIWS", portName = "ReversoTecnicoSNPSPIWSPort", serviceName = "ReversoTecnicoSNPSPIWSService")
@XmlRootElement(namespace = "http://www.bce.fin.ec/wsdl/snp/ReversoTecnicoSNPSPIWS")
@XmlType(namespace = "http://www.bce.fin.ec/wsdl/snp/ReversoTecnicoSNPSPIWS")
public class ReversoTecnicoSNPSPIWS implements ICoreClassReversoSPI{

	@Override
	public DocumentRespuesta realizarReverso(DocumentReverso documentoSolicitud) throws Error {
		
		DocumentRespuesta doc = new DocumentRespuesta();
		BridgeSPI_BCE bridge = new BridgeSPI_BCE();
		doc = bridge.ProcesoRespuestaReversosTecnicosSPI(documentoSolicitud, ObtainIpClient());
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
