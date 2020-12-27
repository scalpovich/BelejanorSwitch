package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DependentTransactionRequestFields implements Serializable{

	private DataTransfer transfer_data;
	private ReversalData reversal_data;
	
	public DependentTransactionRequestFields() {
		this.reversal_data = null;
	}
	
	public ReversalData getReversal_data() {
		return reversal_data;
	}

	public void setReversal_data(ReversalData reversal_data) {
		this.reversal_data = reversal_data;
	}

	public DataTransfer getTransfer_data() {
		return transfer_data;
	}

	public void setTransfer_data(DataTransfer transfer_data) {
		this.transfer_data = transfer_data;
	}	
	
}
