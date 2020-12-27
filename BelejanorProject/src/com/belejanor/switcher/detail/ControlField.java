package com.belejanor.switcher.detail;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ControlField {

	private List<Fields> fields;
	
	public ControlField(){		
	}
	@XmlElement(name="CAM")
	public List<Fields> getFields() {
		return fields;
	}

	public void setFields(List<Fields> fields) {
		this.fields = fields;
	}
		
}
