package com.belejanor.switcher.authorizations;
import java.util.Date;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferenciaSPI;

public interface ITransactionsSPIBridge {

	abstract DocumentRespuesta ProcesaSolicitudTransferenciaSPI
	                           (DocumentTransferenciaSPI documentSolicitud, String IP);
	abstract Date ProcesaRespuestaTransferenciaSPI
	                           (DocumentRespuesta documentoRespuestaSolicitud, String IP);
	abstract DocumentRespuesta ProcesoRespuestaReversosTecnicosSPI(DocumentReverso documentoReverso, String IP);
}
