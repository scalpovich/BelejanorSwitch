package com.belejanor.switcher.fit1struct;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"cuenta", "moneda", "valor","valorO","campoFVL","campoFDS","campoFVO","campoFDO"})
public class Debitos implements Serializable{

	private static final long serialVersionUID = -2383763331379694690L;
	private String cuenta;
	private String moneda;
	private String valor;
	private String valorO;
	private String campoFVL;
	private String campoFDS;
	private String campoFVO;
	private String campoFDO;
	
	
	public Debitos() {

		super();
		this.cuenta = null;
		this.moneda = null;
		this.valor = null;
		this.valorO = null;
		this.campoFVL = null;
		this.campoFDS = null;
		this.campoFVO = null;
		this.campoFDO = null;
	}
	
	
	@XmlElement(name="CTA")
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	@XmlElement(name="MON")
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	@XmlElement(name="VAL")
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	@XmlElement(name="VLO")
	public String getValorO() {
		return valorO;
	}
	public void setValorO(String valorO) {
		this.valorO = valorO;
	}
	@XmlElement(name="FVL")
	public String getCampoFVL() {
		return campoFVL;
	}
	public void setCampoFVL(String campoFLV) {
		this.campoFVL = campoFLV;
	}
	@XmlElement(name="FDS")
	public String getCampoFDS() {
		return campoFDS;
	}
	public void setCampoFDS(String campoFDS) {
		this.campoFDS = campoFDS;
	}
	@XmlElement(name="FVO")
	public String getCampoFVO() {
		return campoFVO;
	}
	public void setCampoFVO(String campoFVO) {
		this.campoFVO = campoFVO;
	}
	@XmlElement(name="FDO")
	public String getCampoFDO() {
		return campoFDO;
	}
	public void setCampoFDO(String campoFDO) {
		this.campoFDO = campoFDO;
	}
	
	
}
