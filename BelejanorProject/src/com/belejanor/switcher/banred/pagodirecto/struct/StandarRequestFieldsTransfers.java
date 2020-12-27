package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;

@SuppressWarnings("serial")

public class StandarRequestFieldsTransfers extends StandardRequestFieldsConsulta 
										   implements Serializable{

	private DependentTransactionRequestFields transaction_dependent_fields;

	public StandarRequestFieldsTransfers(XactSourceInformation xact_source_information,
			DetailTransaction detail_transaction, TargetInstitutionGroup target_institution_group,
			StandInGroup stand_in_group, CardRelatedGroup card_related_group) {
		super(xact_source_information, detail_transaction, target_institution_group, stand_in_group, card_related_group);
	}

	public DependentTransactionRequestFields getTransaction_dependent_fields() {
		return transaction_dependent_fields;
	}

	public void setTransaction_dependent_fields(DependentTransactionRequestFields transaction_dependent_fields) {
		this.transaction_dependent_fields = transaction_dependent_fields;
	}
	
	
}
