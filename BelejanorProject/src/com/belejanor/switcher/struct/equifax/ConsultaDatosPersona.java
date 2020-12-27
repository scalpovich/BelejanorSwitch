package com.belejanor.switcher.struct.equifax;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name="ConsultaDatosPersona", namespace="http://lorente.fin.ec/ConsultasEquifax")
public class ConsultaDatosPersona implements Serializable{

	private String numeroDocumento;

	@XmlElement(name="numeroDocumento")
	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	
}
