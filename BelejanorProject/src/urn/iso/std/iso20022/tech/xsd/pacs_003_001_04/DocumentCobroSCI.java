package urn.iso.std.iso20022.tech.xsd.pacs_003_001_04;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlRootElement(name = "documentoSolicitud", namespace = "http://www.bce.fin.ec/snp/sci/SolicitudCobroInterbancarioSNPWS")
@XmlType(name = "documentoSolicitud", namespace = "urn:iso:std:iso:20022:tech:xsd:pacs.003.001.04")
public class DocumentCobroSCI implements Serializable{

		private FIToFICustomerDirectDebitV04 FIToFICstmrDrctDbt;

		public FIToFICustomerDirectDebitV04 getFIToFICstmrDrctDbt() {
			return FIToFICstmrDrctDbt;
		}

		public void setFIToFICstmrDrctDbt(FIToFICustomerDirectDebitV04 fIToFICstmrDrctDbt) {
			FIToFICstmrDrctDbt = fIToFICstmrDrctDbt;
		}
		
}
