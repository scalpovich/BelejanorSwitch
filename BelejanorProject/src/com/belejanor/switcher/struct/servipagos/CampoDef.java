package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType(propOrder={"id", "name"})
public class CampoDef implements Serializable{

	private String id;
	private String name;
	
	public CampoDef(){
		
		this.id = null;
		this.name = null;
	}
	
	public CampoDef(String id, String name){
		
		this();
		this.id = id;
		this.name = name;
	}
	@XmlAttribute
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
