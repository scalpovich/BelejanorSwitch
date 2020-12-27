package com.belejanor.switcher.struct.equifax;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlRootElement(name="ConsultaDatosPersonaResponse", namespace="http://lorente.fin.ec/ConsultasEquifax")
@XmlType(propOrder={"datosPrincipales", "datosDirecciones"})
public class ConsultaDatosPersonaRespuesta implements Serializable{

	private DatosDirecciones datosDirecciones;
	private DatosPrincipales datosPrincipales;
	
	@XmlElement(name="DatosDirecciones")
	public DatosDirecciones getDatosDirecciones() {
		return datosDirecciones;
	}
	public void setDatosDirecciones(DatosDirecciones datosDirecciones) {
		this.datosDirecciones = datosDirecciones;
	}
	@XmlElement(name="DatosPrincipales")
	public DatosPrincipales getDatosPrincipales() {
		return datosPrincipales;
	}
	public void setDatosPrincipales(DatosPrincipales datosPrincipales) {
		this.datosPrincipales = datosPrincipales;
	}
	
}
