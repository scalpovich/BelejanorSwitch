package com.belejanor.switcher.reversotecnicosciimp;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.cscoreswitch.ICoreClassReversoSCI;

import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;

@WebService(targetNamespace = "http://www.bce.fin.ec/wsdl/snp/ReversoTecnicoSNPSCIWS", 
portName = "ReversoTecnicoSNPSCIWSPort", 
serviceName = "ReversoTecnicoSNPSCIWSService")
@XmlRootElement(namespace = "http://www.bce.fin.ec/wsdl/snp/ReversoTecnicoSNPSCIWS")
@XmlType(namespace = "http://www.bce.fin.ec/wsdl/snp/ReversoTecnicoSNPSCIWS")
public class ReversoTecnicoSNPSCIWS implements ICoreClassReversoSCI{

	@Override
	public DocumentRespuesta realizarReverso(DocumentReverso documentoSolicitud) throws Error {
		// TODO Auto-generated method stub
		return null;
	}

}
