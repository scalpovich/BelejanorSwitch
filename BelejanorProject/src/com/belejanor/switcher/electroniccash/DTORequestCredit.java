package com.belejanor.switcher.electroniccash;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="DTORequestCredit", namespace="http://implementations.middleware.fitbank.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DTORequestCredit", namespace="http://implementations.middleware.fitbank.com")
public class DTORequestCredit extends DTORequestDebit {

	private static final long serialVersionUID = 5275047371334814952L;

	public DTORequestCredit() {
		super();
	}
	
	
}
