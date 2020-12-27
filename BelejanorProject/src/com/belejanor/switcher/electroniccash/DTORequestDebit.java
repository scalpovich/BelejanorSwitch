package com.belejanor.switcher.electroniccash;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.StringUtils;

@XmlRootElement(name="DTORequestDebit", namespace="http://electroniccash.middleware.fitbank.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DTORequestDebit", propOrder = {
	    "accountId",
	    "accountType",
	    "amount",
	    "extraInfo",
	    "msisdnSource",
	    "transactionSequenceId"
	}, namespace = "http://electroniccash.middleware.fitbank.com")
public class DTORequestDebit implements Serializable{

	private static final long serialVersionUID = -6370719512243865534L;
	private long transactionSequenceId;
	private String accountId;
	private String accountType;
	private String msisdnSource;
	private double amount; 
	private String extraInfo;
	
	public DTORequestDebit() {
		super();
	}
	
	public long getTransactionSequenceId() {
		return transactionSequenceId;
	}

	public void setTransactionSequenceId(long transactionSequenceId) {
		this.transactionSequenceId = transactionSequenceId;
	}

	public String getAccountId() {
		return accountId;
	}


	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getMsisdnSource() {
		return msisdnSource;
	}

	public void setMsisdnSource(String msisdnSource) {
		this.msisdnSource = msisdnSource;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public String ValidateMethod(DTORequestDebit obj){
		
		String response = "OK";
		try {
			
			if(StringUtils.IsNullOrEmpty(obj.getAccountType()))
				response = "TIPO DE CUENTA NO PUEDE SER NULO O VACIO";
			else if (!obj.getAccountType().equals("CA") && !obj.getAccountType().equals("CC")) {
				response = "TIPO DE CUENTA NO VALIDO";
			}
			if(obj.getTransactionSequenceId() == 0)
				response = "NUMERO UNICO DE LA TRANSACCION NO PUEDE NULO";
			
		} catch (Exception e) {
			
		}
		return response;
	}
	
}

