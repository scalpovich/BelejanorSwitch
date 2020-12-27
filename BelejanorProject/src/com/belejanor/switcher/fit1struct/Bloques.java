package com.belejanor.switcher.fit1struct;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;;

public class Bloques implements Serializable{

	private static final long serialVersionUID = 2027881338458499923L;
	
	private String numeroBloque;
	private String numeroPagina;
	private String numeroRegistros;
	private List<Criterios> criterios;
	private List<Campos> campos;
	private Componente componente;
	
	public Bloques() {
		
		super();
	}
	public Bloques(String nroBloque, String nroPag, String nroReg) {
		
		this.numeroBloque = nroBloque;
		this.numeroPagina = nroPag;
		this.numeroRegistros = nroReg;
	}
	
	@XmlAttribute(name ="NBQ")
	public String getNumeroBloque() {
		return numeroBloque;
	}
	public void setNumeroBloque(String numeroBloque) {
		this.numeroBloque = numeroBloque;
	}
	@XmlAttribute(name ="NPG")
	public String getNumeroPagina() {
		return numeroPagina;
	}
	public void setNumeroPagina(String numeroPagina) {
		this.numeroPagina = numeroPagina;
	}
	@XmlAttribute(name ="NRG")
	public String getNumeroRegistros() {
		return numeroRegistros;
	}
	public void setNumeroRegistros(String numeroRegistros) {
		this.numeroRegistros = numeroRegistros;
	}
	@XmlElement(name="CRI")
	public List<Criterios> getCriterios() {
		return criterios;
	}
	public void setCriterios(List<Criterios> criterios) {
		this.criterios = criterios;
	}
	@XmlElement(name="CAM")
	public List<Campos> getCampos() {
		return campos;
	}
	public void setCampos(List<Campos> campos) {
		this.campos = campos;
	}
	@XmlElement(name="COM")
	public Componente getComponente() {
		return componente;
	}
	public void setComponente(Componente componente) {
		this.componente = componente;
	}
	
	
}
