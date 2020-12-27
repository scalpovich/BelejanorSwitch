package com.belejanor.switcher.bimo.genericerror;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionInformationAndStatus", propOrder = {
   
    "txSts",
    "stsRsnInf"
   
})
public class TransactionInformationAndStatus implements Serializable{

	 @XmlElement(name = "TxSts", required = true)
	    protected StatusCode txSts;
	 @XmlElement(name = "StsRsnInf", required = true)
	    protected StatusReasonInformation stsRsnInf;
	public StatusCode getTxSts() {
		return txSts;
	}
	public void setTxSts(StatusCode txSts) {
		this.txSts = txSts;
	}
	public StatusReasonInformation getStsRsnInf() {
		return stsRsnInf;
	}
	public void setStsRsnInf(StatusReasonInformation stsRsnInf) {
		this.stsRsnInf = stsRsnInf;
	}
	 
	
}
