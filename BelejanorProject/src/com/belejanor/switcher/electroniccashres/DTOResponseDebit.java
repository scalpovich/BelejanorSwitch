package com.belejanor.switcher.electroniccashres;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DTOResponseDebit implements Serializable {

	private static final long serialVersionUID = -668367536968680351L;
	private long transactionSequenceId;
	private String externalTransactionId;
	private String operationResult;
	private String errorCode;
	private String message;
	
	public DTOResponseDebit() {
		super();
	}

	public long getTransactionSequenceId() {
		return transactionSequenceId;
	}

	public void setTransactionSequenceId(long transactionSequenceId) {
		this.transactionSequenceId = transactionSequenceId;
	}

	public String getExternalTransactionId() {
		return externalTransactionId;
	}

	public void setExternalTransactionId(String externalTransactionId) {
		this.externalTransactionId = externalTransactionId;
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
