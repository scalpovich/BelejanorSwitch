package com.belejanor.switcher.credencial;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MovementsCredencialResponse implements Serializable {

	private static final long serialVersionUID = -5897869333212663211L;
	
	private int CodigoRespuesta;
	private String MensajeRespuesta;
	private String Identificador;
	private Date FechaOperacion;
	
	private Movimientos Movimientos;
    
	public MovementsCredencialResponse(){
		
	}
	@XmlElement(name = "Movimientos")
	public Movimientos getMov() {
		return Movimientos;
	}
	public void setMov(Movimientos mov) {
		this.Movimientos = mov;
	}
	@XmlElement(name = "CodigoRespuesta")
	public int getCodigoRespuesta() {
		return CodigoRespuesta;
	}
	public void setCodigoRespuesta(int codigoRespuesta) {
		CodigoRespuesta = codigoRespuesta;
	}
	@XmlElement(name = "MensajeRespuesta")
	public String getMensajeRespuesta() {
		return MensajeRespuesta;
	}
	public void setMensajeRespuesta(String mensajeRespuesta) {
		MensajeRespuesta = mensajeRespuesta;
	}
	@XmlElement(name = "Identificador")
	public String getIdentificador() {
		return Identificador;
	}
	public void setIdentificador(String identificador) {
		Identificador = identificador;
	}
	@XmlElement(name = "FechaOperacion")
	public Date getFechaOperacion() {
		return FechaOperacion;
	}
	public void setFechaOperacion(Date fechaOperacion) {
		FechaOperacion = fechaOperacion;
	}
	
}

