package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType(propOrder={"id", "valore"})
public class Campo implements Serializable{

	private String id;
	private String valore;
	
	public Campo(){
		
		this.id = null;
		this.valore = null;
	}
	
	public Campo(String id, String valor){
		this.id = id;
		this.valore = valor;
	}
	@XmlAttribute
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	@XmlAttribute(name="valor")
	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}
	
	
	
}
