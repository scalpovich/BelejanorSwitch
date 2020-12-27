package com.belejanor.switcher.credencial;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

public class DebitCredencialResponse implements Serializable {

	private static final long serialVersionUID = -4231612352037888560L;
	
	private int CodigoRespuesta;
	private String MensajeRespuesta;
	private String IdentificadorEMI;
	private Date FechaOperacion;
	private double SaldoPosterior;
	private double SaldoAnterior;
	private int Moneda;
	private String Identificador;
	
	public DebitCredencialResponse(){
		
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
	@XmlElement(name="FechaOperacion")
	public Date getFechaOperacion() {
		return FechaOperacion;
	}
	public void setFechaOperacion(Date fechaOperacion) {
		FechaOperacion = fechaOperacion;
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
	@XmlElement(name="Moneda")
	public int getMoneda() {
		return Moneda;
	}
	public void setMoneda(int moneda) {
		Moneda = moneda;
	}
	@XmlElement(name="Identificador")
	public String getIdentificador() {
		return Identificador;
	}
	public void setIdentificador(String identificador) {
		Identificador = identificador;
	}
	
}
