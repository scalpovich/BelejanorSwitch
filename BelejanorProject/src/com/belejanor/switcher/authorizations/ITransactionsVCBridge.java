package com.belejanor.switcher.authorizations;

import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.DocumentRetiro;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentDeposito;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferencia;

public interface ITransactionsVCBridge {

	abstract DocumentRespuesta ProcesaDepositoVC_BCE(DocumentDeposito documentDeposito, String IP);
	abstract DocumentRespuesta ProcesaRetiroVC_BCE(DocumentRetiro documentRetiro, String IP);
	abstract DocumentRespuesta ProcesarreversoVC_BCE(DocumentReverso documentReverso, String IP);
	abstract DocumentRespuesta ProcesarTransferenciaVC_BCE(DocumentTransferencia documentTransferencia, String IP);
}
