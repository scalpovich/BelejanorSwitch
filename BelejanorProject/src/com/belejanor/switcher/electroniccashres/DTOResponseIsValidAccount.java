package com.belejanor.switcher.electroniccashres;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DTOResponseIsValidAccount implements Serializable{

	private static final long serialVersionUID = -4224631891405005797L;
	private String operationResult;
	private String errorCode;
	private String message;
	
	public DTOResponseIsValidAccount() {
		super();
		
	}

	public String getOperationResult() {
		return operationResult;
	}

	public void setOperationResult(String operationResult) {
		this.operationResult = operationResult;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

}

