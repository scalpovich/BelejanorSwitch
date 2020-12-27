package com.belejanor.switcher.credencial;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="debitCredencialRequest")
public class DebitCredencialRequest implements Serializable{

	private static final long serialVersionUID = -6393544679158015130L;
	
	private String ReferenciaCuentaEMI;
	private String IdentificadorCuentaEMI;
	private String Tarjeta;
	private double Monto;
	private int Moneda;
	private double MontoLIQ;
	private int MonedaLIQ;
	private String IdentificadorADQ;
	private String Identificador;
	private double Comision;
	private double ComisionLIQ;
	private String Origen;
	private int Motivo;
	private String Adquiriente;
	private String Ciudad;
	private Date FechaTransaccion;
	private String Terminal;
	private String NombreComercio;
	
	public DebitCredencialRequest(){
		
	}
	@XmlElement(name="ReferenciaCuentaEMI")
	public String getReferenciaCuentaEMI() {
		return ReferenciaCuentaEMI;
	}
	public void setReferenciaCuentaEMI(String referenciaCuentaEMI) {
		ReferenciaCuentaEMI = referenciaCuentaEMI;
	}
	@XmlElement(name="IdentificadorCuentaEMI")
	public String getIdentificadorCuentaEMI() {
		return IdentificadorCuentaEMI;
	}
	public void setIdentificadorCuentaEMI(String identificadorCuentaEMI) {
		IdentificadorCuentaEMI = identificadorCuentaEMI;
	}
	@XmlElement(name="Tarjeta")
	public String getTarjeta() {
		return Tarjeta;
	}
	public void setTarjeta(String tarjeta) {
		Tarjeta = tarjeta;
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
	@XmlElement(name="IdentificadorADQ")
	public String getIdentificadorADQ() {
		return IdentificadorADQ;
	}
	public void setIdentificadorADQ(String identificadorADQ) {
		IdentificadorADQ = identificadorADQ;
	}
	@XmlElement(name="Identificador")
	public String getIdentificador() {
		return Identificador;
	}
	public void setIdentificador(String identificador) {
		Identificador = identificador;
	}
	@XmlElement(name="Comision")
	public double getComision() {
		return Comision;
	}
	public void setComision(double comision) {
		Comision = comision;
	}
	@XmlElement(name="ComisionLIQ")
	public double getComisionLIQ() {
		return ComisionLIQ;
	}
	public void setComisionLIQ(double comisionLIQ) {
		ComisionLIQ = comisionLIQ;
	}
	@XmlElement(name="Origen")
	public String getOrigen() {
		return Origen;
	}
	public void setOrigen(String origen) {
		Origen = origen;
	}
	@XmlElement(name="Motivo")
	public int getMotivo() {
		return Motivo;
	}
	public void setMotivo(int motivo) {
		Motivo = motivo;
	}
	@XmlElement(name="Adquiriente")
	public String getAdquiriente() {
		return Adquiriente;
	}
	public void setAdquiriente(String adquiriente) {
		Adquiriente = adquiriente;
	}
	@XmlElement(name="Ciudad")
	public String getCiudad() {
		return Ciudad;
	}
	public void setCiudad(String ciudad) {
		Ciudad = ciudad;
	}
	@XmlElement(name="FechaTransaccion")
	public Date getFechaTransaccion() {
		return FechaTransaccion;
	}
	public void setFechaTransaccion(Date fechaTransaccion) {
		FechaTransaccion = fechaTransaccion;
	}
	@XmlElement(name="Terminal")
	public String getTerminal() {
		return Terminal;
	}
	public void setTerminal(String terminal) {
		Terminal = terminal;
	}
	@XmlElement(name="NombreComercio")
	public String getNombreComercio() {
		return NombreComercio;
	}

	public void setNombreComercio(String nombreComercio) {
		NombreComercio = nombreComercio;
	}
	
}
