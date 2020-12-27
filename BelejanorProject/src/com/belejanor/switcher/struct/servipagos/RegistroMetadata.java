package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;

@SuppressWarnings("serial")
public class RegistroMetadata implements Serializable{

	private String tipo;
	private List<CampoDef> campoDef;

	public RegistroMetadata(){
		
		this.tipo = null;
		this.campoDef = new ArrayList<>();
	}
	public RegistroMetadata(String tipo){
		
		this();
		this.tipo = tipo;
	}
	public void addCampoDef(String id, String name) {
		campoDef.add(new CampoDef(id, name));
	}
	@XmlAttribute
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public List<CampoDef> getCampoDef() {
		return campoDef;
	}
	public void setCampoDef(List<CampoDef> campoDef) {
		this.campoDef = campoDef;
	}
		
}
