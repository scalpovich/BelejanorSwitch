package com.belejanor.switcher.cscoreswitch;

import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferenciaSPI;

public interface ICoreClassSPI {

	abstract DocumentRespuesta recibirSolicitudOPI(DocumentTransferenciaSPI documentoSolicitud) throws Error;
	
}
