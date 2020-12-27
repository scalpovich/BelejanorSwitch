package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;

@SuppressWarnings("serial")
public class Registro implements Serializable{

	private String tipo;
	private List<Campo> campo;

	public Registro(){
		
		this.tipo = null;
		this.campo = new ArrayList<>();
	}
	public Registro(String tipo){
		
		this();
		this.tipo = tipo;
	}
	public void addCampo(String id, String valor) {
		campo.add(new Campo(id, valor));
	}
	@XmlAttribute
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public List<Campo> getCampo() {
		return campo;
	}
	public void setCampo(List<Campo> campo) {
		this.campo = campo;
	}
	
	
}
