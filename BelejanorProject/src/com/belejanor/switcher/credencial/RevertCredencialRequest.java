package com.belejanor.switcher.credencial;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

public class RevertCredencialRequest implements Serializable {

	private static final long serialVersionUID = -8648733889956132616L;
	
	private String IdentificadorADQ;
	private String IdentificadorEMI;
	private String Origen;
	private int MotivoReversa;
	private double Monto;
	private int Moneda;
	private double MontoLIQ;
	private int MonedaLIQ;
	private Date FechaTransaccion;
	private String Identificador;
	private String NombreComercio;
	
	public RevertCredencialRequest(){
		
	}
	@XmlElement(name="IdentificadorADQ")
	public String getIdentificadorADQ() {
		return IdentificadorADQ;
	}

	public void setIdentificadorADQ(String identificadorADQ) {
		IdentificadorADQ = identificadorADQ;
	}
	@XmlElement(name="IdentificadorEMI")
	public String getIdentificadorEMI() {
		return IdentificadorEMI;
	}

	public void setIdentificadorEMI(String identificadorEMI) {
		IdentificadorEMI = identificadorEMI;
	}
	@XmlElement(name="Origen")
	public String getOrigen() {
		return Origen;
	}

	public void setOrigen(String origen) {
		Origen = origen;
	}
	@XmlElement(name="MotivoReversa")
	public int getMotivoReversa() {
		return MotivoReversa;
	}

	public void setMotivoReversa(int motivoReversa) {
		MotivoReversa = motivoReversa;
	}
	@XmlElement(name="Monto")
	public double getMonto() {
		return Monto;
	}

	public void setMonto(double monto) {
		Monto = monto;
	}
	@XmlElement(name="Moneda")
	public int getMoneda() {
		return Moneda;
	}

	public void setMoneda(int moneda) {
		Moneda = moneda;
	}
	@XmlElement(name="MontoLIQ")
	public double getMontoLIQ() {
		return MontoLIQ;
	}

	public void setMontoLIQ(double montoLIQ) {
		MontoLIQ = montoLIQ;
	}
	@XmlElement(name="MonedaLIQ")
	public int getMonedaLIQ() {
		return MonedaLIQ;
	}

	public void setMonedaLIQ(int monedaLIQ) {
		MonedaLIQ = monedaLIQ;
	}
	@XmlElement(name="FechaTransaccion")
	public Date getFechaTransaccion() {
		return FechaTransaccion;
	}

	public void setFechaTransaccion(Date fechaTransaccion) {
		FechaTransaccion = fechaTransaccion;
	}
	@XmlElement(name="Identificador")
	public String getIdentificador() {
		return Identificador;
	}

	public void setIdentificador(String identificador) {
		Identificador = identificador;
	}
	@XmlElement(name="NombreComercio")
	public String getNombreComercio() {
		return NombreComercio;
	}

	public void setNombreComercio(String nombreComercio) {
		NombreComercio = nombreComercio;
	}    
	
	
	
}
