package com.belejanor.switcher.fit1struct;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("FITBANK")
@XmlRootElement(name = "FITBANK")
public class DetailFit1 implements Serializable{

	private static final long serialVersionUID = 6612762633900013687L;
	private Cabecera cabecera;
	private Detalle detalle;
	private Authorization authorization;
	private Respuesta respuesta;
	
	public DetailFit1() {
		
		super();
		this.cabecera = null;
		this.detalle = null;
		this.authorization = null;
		this.respuesta = null;
	}
	
	@XmlElement(name="CAB")
	public Cabecera getCabecera() {
		return cabecera;
	}
	public void setCabecera(Cabecera cabecera) {
		this.cabecera = cabecera;
	}
	@XmlElement(name="DET")
	public Detalle getDetalle() {
		return detalle;
	}
	public void setDetalle(Detalle detalle) {
		this.detalle = detalle;
	}
	@XmlElement(name="AUT")
	public Authorization getAuthorization() {
		return authorization;
	}
	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
	}
	@XmlElement(name="RES")
	public Respuesta getRespuesta() {
		return respuesta;
	}
	public void setRespuesta(Respuesta respuesta) {
		this.respuesta = respuesta;
	}
	
}
