package com.belejanor.switcher.detail;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlRootElement(name = "FITBANK")
@XmlType(propOrder = {"grq","det","grs"})
public class FitBank  implements Serializable{

	private GeneralRequestClass grq;
	private Details det;
	private GeneralResponseClass grs;
	
	public FitBank(){
		
	}
	@XmlElement(name="GRQ")
	public GeneralRequestClass getGrq() {
		return grq;
	}

	public void setGrq(GeneralRequestClass grq) {
		this.grq = grq;
	}
	@XmlElement(name="DET")
	public Details getDet() {
		return det;
	}

	public void setDet(Details det) {
		this.det = det;
	}
	@XmlElement(name="GRS")
	public GeneralResponseClass getGrs() {
		return grs;
	}

	public void setGrs(GeneralResponseClass grs) {
		this.grs = grs;
	}
		
}


