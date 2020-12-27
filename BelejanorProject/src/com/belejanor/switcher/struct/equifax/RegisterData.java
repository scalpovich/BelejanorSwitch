package com.belejanor.switcher.struct.equifax;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlRootElement(name="RegistrarDatosEvaluacion", namespace="http://lorente.fin.ec/RegisterEquifax")
@XmlType(propOrder={"idConsultaEquifax", "usuarioConsulta","numeroDocumento","tipoConsuta",
					"tipoCreditoSolicitado","montoSolicitado","plazoSolicitado","gastosFinancieros",
					"tipoCliente","capacidadPago","scoreTitular","scoreTitularSobreendeudamiento",
					"numeroDocumentoConyuge","scoreConyuge","desicion","segmento","modelo","montoSugerido",
					"plazoSugerido","fechaConsulta"})
public class RegisterData implements Serializable{

	
	private String numeroDocumento;
	//private tipoConsulta tipoConsuta;
	private String tipoConsuta;
	private String tipoCreditoSolicitado;
	private double montoSolicitado;
	private int plazoSolicitado;
	private double gastosFinancieros;
	private String tipoCliente;
	private double capacidadPago;
	private int scoreTitular;
	private int scoreTitularSobreendeudamiento;
	private String numeroDocumentoConyuge;
	private int scoreConyuge;
	/*private tipoDecision desicion;*
	private tipoSegmento segmento;
	private tipoModelo modelo;*/
	private String desicion;
	private String segmento;
	private String modelo;
	private double montoSugerido;
	private int plazoSugerido;
	private Date fechaConsulta;
	private String usuarioConsulta;
	private long idConsultaEquifax;
	
	
	public RegisterData() {
	
		this.numeroDocumento = null;
		this.tipoCreditoSolicitado = null;
		this.tipoCliente = null;
		this.numeroDocumentoConyuge = null;
		this.usuarioConsulta = null;
		this.montoSolicitado = -1;
		this.idConsultaEquifax = -1;
		this.gastosFinancieros = -1;
		this.montoSugerido = -1;
		this.capacidadPago = -1;
		this.plazoSolicitado = -1;
		this.gastosFinancieros = -1;
		this.scoreConyuge = -1;
		this.scoreTitular = -1;
		this.scoreTitularSobreendeudamiento = -1;
	}

	@XmlElement(name="NumeroDocumento")
	public String getNumeroDocumento() {
		return numeroDocumento;
	}


	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	@XmlElement(name="TipoConsuta")
	public String getTipoConsuta() {
		return tipoConsuta;
	}


	public void setTipoConsuta(String tipoConsuta) {
		this.tipoConsuta = tipoConsuta;
	}

	@XmlElement(name="TipoCreditoSolicitado")
	public String getTipoCreditoSolicitado() {
		return tipoCreditoSolicitado;
	}


	public void setTipoCreditoSolicitado(String tipoCreditoSolicitado) {
		this.tipoCreditoSolicitado = tipoCreditoSolicitado;
	}

	@XmlElement(name="MontoSolicitado")
	public double getMontoSolicitado() {
		return montoSolicitado;
	}


	public void setMontoSolicitado(double montoSolicitado) {
		this.montoSolicitado = montoSolicitado;
	}

	@XmlElement(name="PlazoSolicitado")
	public int getPlazoSolicitado() {
		return plazoSolicitado;
	}


	public void setPlazoSolicitado(int plazoSolicitado) {
		this.plazoSolicitado = plazoSolicitado;
	}

	@XmlElement(name="GastosFinancieros")
	public double getGastosFinancieros() {
		return gastosFinancieros;
	}


	public void setGastosFinancieros(double gastosFinancieros) {
		this.gastosFinancieros = gastosFinancieros;
	}

	@XmlElement(name="TipoCliente")
	public String getTipoCliente() {
		return tipoCliente;
	}


	public void setTipoCliente(String tipoCliente) {
		this.tipoCliente = tipoCliente;
	}

	@XmlElement(name="CapacidadPago")
	public double getCapacidadPago() {
		return capacidadPago;
	}


	public void setCapacidadPago(double capacidadPago) {
		this.capacidadPago = capacidadPago;
	}

	@XmlElement(name="ScoreTitular")
	public int getScoreTitular() {
		return scoreTitular;
	}


	public void setScoreTitular(int scoreTitular) {
		this.scoreTitular = scoreTitular;
	}

	@XmlElement(name="ScoreTitularSobreendeudamiento")
	public int getScoreTitularSobreendeudamiento() {
		return scoreTitularSobreendeudamiento;
	}


	public void setScoreTitularSobreendeudamiento(int scoreTitularSobreendeudamiento) {
		this.scoreTitularSobreendeudamiento = scoreTitularSobreendeudamiento;
	}

	@XmlElement(name="NumeroDocumentoConyuge")
	public String getNumeroDocumentoConyuge() {
		return numeroDocumentoConyuge;
	}


	public void setNumeroDocumentoConyuge(String numeroDocumentoConyuge) {
		this.numeroDocumentoConyuge = numeroDocumentoConyuge;
	}

	@XmlElement(name="ScoreConyuge")
	public int getScoreConyuge() {
		return scoreConyuge;
	}


	public void setScoreConyuge(int scoreConyuge) {
		this.scoreConyuge = scoreConyuge;
	}

	@XmlElement(name="Desicion")
	public String getDesicion() {
		return desicion;
	}


	public void setDesicion(String desicion) {
		this.desicion = desicion;
	}

	@XmlElement(name="Segmento")
	public String getSegmento() {
		return segmento;
	}


	public void setSegmento(String segmento) {
		this.segmento = segmento;
	}

	@XmlElement(name="Modelo")
	public String getModelo() {
		return modelo;
	}


	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	@XmlElement(name="MontoSugerido")
	public double getMontoSugerido() {
		return montoSugerido;
	}


	public void setMontoSugerido(double montoSugerido) {
		this.montoSugerido = montoSugerido;
	}

	@XmlElement(name="PlazoSugerido")
	public int getPlazoSugerido() {
		return plazoSugerido;
	}


	public void setPlazoSugerido(int plazoSugerido) {
		this.plazoSugerido = plazoSugerido;
	}

	@XmlElement(name="FechaConsulta")
	public Date getFechaConsulta() {
		return fechaConsulta;
	}


	public void setFechaConsulta(Date fechaConsulta) {
		this.fechaConsulta = fechaConsulta;
	}

	@XmlElement(name="UsuarioConsulta")
	public String getUsuarioConsulta() {
		return usuarioConsulta;
	}


	public void setUsuarioConsulta(String usuarioConsulta) {
		this.usuarioConsulta = usuarioConsulta;
	}

	@XmlElement(name="IdConsultaEquifax")
	public long getIdConsultaEquifax() {
		return idConsultaEquifax;
	}


	public void setIdConsultaEquifax(long idConsultaEquifax) {
		this.idConsultaEquifax = idConsultaEquifax;
	}
	
	
	
}

