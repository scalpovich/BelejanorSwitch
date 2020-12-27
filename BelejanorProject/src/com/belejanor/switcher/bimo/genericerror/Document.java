package com.belejanor.switcher.bimo.genericerror;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.StringUtils;

@XmlRootElement(name="Document", namespace="urn:swift:xsd:camt.998.999")
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", namespace = "urn:swift:xsd:camt.998.999")
public class Document implements Serializable{
	
	public Document() {
		super();
	}
	
	public Document(String cause){
		
		try {
			
			this.trx = new TransactionInformationAndStatus();
			StatusReasonInformation stst = new StatusReasonInformation();
			
			Reason rs = new Reason();
			rs.setCode(Arrays.asList(cause.split("\\|")).get(0));
			
			List<String> list = new ArrayList<>();
			list.add(Arrays.asList(cause.split("\\|")).get(1).replace("<**>", StringUtils.Empty()));
			
			rs.setAddtlInf(list);
			
			stst.setRsn(rs);
			
			this.trx.setTxSts(StatusCode.ERR);
			this.trx.setStsRsnInf(stst);
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@XmlElement(name = "GenericError", required = true)
	private TransactionInformationAndStatus trx;


	public TransactionInformationAndStatus getTrx() {
		return trx;
	}

	public void setTrx(TransactionInformationAndStatus trx) {
		this.trx = trx;
	}

	
}
