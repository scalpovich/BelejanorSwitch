package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DependentTransactionReplyFields implements Serializable{

	private DataDependend dependend_data;

	public DataDependend getDependend_data() {
		return dependend_data;
	}

	public void setDependend_data(DataDependend dependend_data) {
		this.dependend_data = dependend_data;
	}
	
}
