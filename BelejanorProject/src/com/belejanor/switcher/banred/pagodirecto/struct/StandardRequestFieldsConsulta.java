package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType(propOrder={"xact_source_information", "detail_transaction",
					"target_institution_group", "stand_in_group",
					"card_related_group"})

public class StandardRequestFieldsConsulta implements Serializable{

	private XactSourceInformation xact_source_information;
	private DetailTransaction detail_transaction;
	private TargetInstitutionGroup target_institution_group;
	private StandInGroup stand_in_group;
	private CardRelatedGroup card_related_group;
	
	
	
	public StandardRequestFieldsConsulta(XactSourceInformation xact_source_information,
			DetailTransaction detail_transaction, TargetInstitutionGroup target_institution_group,
			StandInGroup stand_in_group, CardRelatedGroup card_related_group) {
		super();
		this.xact_source_information = xact_source_information;
		this.detail_transaction = detail_transaction;
		this.target_institution_group = target_institution_group;
		this.stand_in_group = stand_in_group;
		this.card_related_group = card_related_group;
	}
	public XactSourceInformation getXact_source_information() {
		return xact_source_information;
	}
	public void setXact_source_information(XactSourceInformation xact_source_information) {
		this.xact_source_information = xact_source_information;
	}
	public DetailTransaction getDetail_transaction() {
		return detail_transaction;
	}
	public void setDetail_transaction(DetailTransaction detail_transaction) {
		this.detail_transaction = detail_transaction;
	}
	public TargetInstitutionGroup getTarget_institution_group() {
		return target_institution_group;
	}
	public void setTarget_institution_group(TargetInstitutionGroup target_institution_group) {
		this.target_institution_group = target_institution_group;
	}
	public StandInGroup getStand_in_group() {
		return stand_in_group;
	}
	public void setStand_in_group(StandInGroup stand_in_group) {
		this.stand_in_group = stand_in_group;
	}
	public CardRelatedGroup getCard_related_group() {
		return card_related_group;
	}
	public void setCard_related_group(CardRelatedGroup card_related_group) {
		this.card_related_group = card_related_group;
	}
	
	
}
