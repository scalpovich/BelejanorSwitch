package com.belejanor.switcher.authorizations;

import java.util.Date;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.DocumentCobroSCI;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;

public interface ITransactionsSCIBridge {

	abstract DocumentRespuesta ProcesaSolicitudCobrosSCI
    						 (DocumentCobroSCI documentSolicitud, String IP);
	abstract Date ProcesaRespuestaCobrosSCI
    						 (DocumentRespuesta documentoRespuestaSolicitud, String IP);
	abstract DocumentRespuesta ProcesoRespuestaReversosTecnicosSCI
	                         (DocumentReverso documentoReverso, String IP);
}
