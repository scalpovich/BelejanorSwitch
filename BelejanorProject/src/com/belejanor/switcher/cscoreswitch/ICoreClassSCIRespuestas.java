package com.belejanor.switcher.cscoreswitch;

import java.util.Date;
import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;

public interface ICoreClassSCIRespuestas {

	abstract Date recibirRespuestaServiciosSNPSCI(DocumentRespuesta documentoRespuestaSolicitud) throws Error;
}
