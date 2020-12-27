package com.belejanor.switcher.respuestassciimp;

import java.util.Date;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.cscoreswitch.ICoreClassSCIRespuestas;

import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;

@WebService(targetNamespace = "http://www.bce.fin.ec/wsdl/snp/RespuestaServiciosSNPSCIWS", 
portName = "RespuestaServiciosSNPSCIWSPort", 
serviceName = "RespuestaServiciosSNPSCIWSService")
@XmlRootElement(namespace = "http://www.bce.fin.ec/wsdl/snp/RespuestaServiciosSNPSCIWS")
@XmlType(namespace = "http://www.bce.fin.ec/wsdl/snp/RespuestaServiciosSNPSCIWS")
public class RespuestaServiciosSNPSCIWS implements ICoreClassSCIRespuestas{

	@Override
	public Date recibirRespuestaServiciosSNPSCI(DocumentRespuesta documentoRespuestaSolicitud) throws Error {
		// TODO Auto-generated method stub
		return null;
	}

}
