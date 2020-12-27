package com.belejanor.switcher.cscoreswitch;

import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;

public interface ICoreClassRev {

	abstract DocumentRespuesta realizarReverso
	(DocumentReverso documentoSolicitud) throws Error;
}

