package com.belejanor.switcher.electroniccash;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.StringUtils;

@XmlRootElement(name="DTORequestIsValidAccount", namespace="http://electroniccash.middleware.fitbank.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DTORequestIsValidAccount", namespace="http://electroniccash.middleware.fitbank.com")
public class DTORequestIsValidAccount implements Serializable{

	private static final long serialVersionUID = 1886712909144049754L;
	private String accountId;
	private String accountType;
	public DTORequestIsValidAccount() {
		super();
		
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
	public String ValidateMethod(DTORequestIsValidAccount obj){
		
		String response = "OK";
		try {
			
			if(StringUtils.IsNullOrEmpty(obj.getAccountType()))
				response = "TIPO DE CUENTA NO PUEDE SER NULO O VACIO";
			else if (!obj.getAccountType().equals("CA") && !obj.getAccountType().equals("CC")) {
				response = "TIPO DE CUENTA NO VALIDO";
			}
			
		} catch (Exception e) {
			
		}
		return response;
	}
	
}

