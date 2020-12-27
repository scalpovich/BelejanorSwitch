package com.belejanor.switcher.cscoreswitch;

import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.DocumentRetiro;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentDeposito;


public interface ICoreClassDR {

	abstract DocumentRespuesta retirarDineroVC(DocumentRetiro  documentoRetiro) throws Error;
	abstract DocumentRespuesta depositarDineroVC(DocumentDeposito documentoDeposito) throws Error;
	
}
