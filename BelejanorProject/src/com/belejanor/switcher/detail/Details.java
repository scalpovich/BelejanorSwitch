package com.belejanor.switcher.detail;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"tables","controlFields"})
public class Details {

	private List<Tables> tables;
	private List<ControlField> controlFields;
	
	public Details(){
		
	}
	@XmlElement(name="TBL")
	public List<Tables> getTables() {
		return tables;
	}

	public void setTables(List<Tables> tables) {
		this.tables = tables;
	}
	@XmlElement(name="CTL")
	public List<ControlField> getControlFields() {
		return controlFields;
	}

	public void setControlFields(List<ControlField> controlFields) {
		this.controlFields = controlFields;
	}
		
}
