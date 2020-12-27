package com.belejanor.switcher.credencial;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

public class BalanceCredencialResponse implements Serializable{

	private static final long serialVersionUID = -4120360363337519243L;
	
	private int CodigoRespuesta;
	private String MensajeRespuesta;
	private double SaldoDisponible;
	private Date FechaOperacion;
	private String Identificador;
	
	@XmlElement(name="CodigoRespuesta")
	public int getCodigoRespuesta() {
		return CodigoRespuesta;
	}
	public void setCodigoRespuesta(int codigoRespuesta) {
		CodigoRespuesta = codigoRespuesta;
	}
	@XmlElement(name="MensajeRespuesta")
	public String getMensajeRespuesta() {
		return MensajeRespuesta;
	}
	public void setMensajeRespuesta(String mensajeRespuesta) {
		MensajeRespuesta = mensajeRespuesta;
	}
	@XmlElement(name="SaldoDisponible")
	public double getSaldoDisponible() {
		return SaldoDisponible;
	}
	public void setSaldoDisponible(double saldoDisponible) {
		SaldoDisponible = saldoDisponible;
	}
	@XmlElement(name="FechaOperacion")
	public Date getFechaOperacion() {
		return FechaOperacion;
	}
	public void setFechaOperacion(Date fechaOperacion) {
		FechaOperacion = fechaOperacion;
	}
	@XmlElement(name="Identificador")
	public String getIdentificador() {
		return Identificador;
	}
	public void setIdentificador(String identificador) {
		Identificador = identificador;
	}
	
}
