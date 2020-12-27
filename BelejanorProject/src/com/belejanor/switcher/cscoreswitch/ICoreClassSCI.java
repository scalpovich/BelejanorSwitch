package com.belejanor.switcher.cscoreswitch;

import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.DocumentCobroSCI;

public interface ICoreClassSCI {

	abstract DocumentRespuesta recibirSolicitudOCI(DocumentCobroSCI documentoSolicitud) throws Error;
}
