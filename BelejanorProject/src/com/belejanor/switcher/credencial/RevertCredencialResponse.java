package com.belejanor.switcher.credencial;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

public class RevertCredencialResponse implements Serializable {

	private static final long serialVersionUID = 3264507990065604890L;
	
	private int CodigoRespuesta;
	private String MensajeRespuesta;
	private String IdentificadorEMI;
	private double SaldoPosterior;
	private double SaldoAnterior;
	private Date FechaOperacion;
	private String Identificador;

	public RevertCredencialResponse(){
		
	}
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
	@XmlElement(name="IdentificadorEMI")
	public String getIdentificadorEMI() {
		return IdentificadorEMI;
	}
	public void setIdentificadorEMI(String identificadorEMI) {
		IdentificadorEMI = identificadorEMI;
	}
	@XmlElement(name="SaldoPosterior")
	public double getSaldoPosterior() {
		return SaldoPosterior;
	}
	public void setSaldoPosterior(double saldoPosterior) {
		SaldoPosterior = saldoPosterior;
	}
	@XmlElement(name="SaldoAnterior")
	public double getSaldoAnterior() {
		return SaldoAnterior;
	}
	public void setSaldoAnterior(double saldoAnterior) {
		SaldoAnterior = saldoAnterior;
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
