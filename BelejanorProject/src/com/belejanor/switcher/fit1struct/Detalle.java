package com.belejanor.switcher.fit1struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Detalle implements Serializable{

	private static final long serialVersionUID = 282588147716552274L;
	
	private List<Bloques> bloques;
	private Componente componente;
	private Debitos debito;
	private Creditos credito;

	public Detalle() {
		
		this.bloques = new ArrayList<>();
		this.componente = null;
		this.debito = null;
		this.credito = null;
	}
	
	@XmlElement(name="BLQ")
	public List<Bloques> getBloques() {
		return bloques;
	}

	public void setBloques(List<Bloques> bloques) {
		this.bloques = bloques;
	}
	@XmlElement(name="COM")
	public Componente getComponente() {
		return componente;
	}

	public void setComponente(Componente componente) {
		this.componente = componente;
	}
	@XmlElement(name="DEB")
	public Debitos getDebito() {
		return debito;
	}

	public void setDebito(Debitos debito) {
		this.debito = debito;
	}
	@XmlElement(name="CRE")
	public Creditos getCredito() {
		return credito;
	}

	public void setCredito(Creditos credito) {
		this.credito = credito;
	}
	
	
	
}
