package com.belejanor.switcher.detail;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Records {

	private List<Fields> field;
	private String numero;
	
	public Records(){
		
	}
	@XmlElement(name="CAM")
	public List<Fields> getField() {
		return field;
	}

	public void setField(List<Fields> field) {
		this.field = field;
	}
	@XmlAttribute
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	
}
