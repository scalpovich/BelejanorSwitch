package com.belejanor.switcher.electroniccash;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="DTORequestRevert", namespace="http://electroniccash.middleware.fitbank.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DTORequestRevert", namespace="http://electroniccash.middleware.fitbank.com")
public class DTORequestRevert implements Serializable {

	private static final long serialVersionUID = 3397566642231770633L;
	//@XmlElement(defaultValue = "-1")
	private long transactionSequenceId;

	public DTORequestRevert() {
		super();
		this.transactionSequenceId = -1;
	}
	
	public long getTransactionSequenceId() {
		return transactionSequenceId;
	}
	public void setTransactionSequenceId(long transactionSequenceId) {
		this.transactionSequenceId = transactionSequenceId;
	}
	
    public String ValidateMethod(DTORequestRevert obj){
    	System.out.println("Entro en switch ValidateMethod...");
		String response = "OK";
		try {
			
			if(obj.getTransactionSequenceId() == -1)
				response = "NUMERO UNICO DE LA TRANSACCION NO PUEDE NULO";
			
		} catch (Exception e) {
			
		}
		System.out.println("Entro en switch ValidateMethod...respuesta " + response);
		return response;
	}
}
