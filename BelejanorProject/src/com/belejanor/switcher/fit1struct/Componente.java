package com.belejanor.switcher.fit1struct;

import javax.xml.bind.annotation.XmlElement;

public class Componente {

	private String campoSEC;
	private String campoCON;
	private String campoDES;
	private String campoIDP;
	private String campoNMP;
	private String campoVMO;
	private String campoLCR;
	private String campoDLO;
	private String campoLOC;
	private String campoRUT;
	private String campoCTG;
	private String campoCHG;
	private String campoSUD;
	private String campoOFD;
	
	public Componente() {
		super();
		this.campoSEC=null;
		this.campoCON=null;
		this.campoDES=null;
		this.campoIDP=null;
		this.campoNMP=null;
		this.campoVMO=null;
		this.campoLCR=null;
		this.campoDLO=null;
		this.campoLOC=null;
		this.campoRUT=null;
		this.campoCTG=null;
		this.campoCHG=null;
		this.campoSUD=null;
		this.campoOFD=null;
	}
	
	@XmlElement(name="SEC")
	public String getCampoSEC() {
		return campoSEC;
	}
	
	public void setCampoSEC(String campoSEC) {
		this.campoSEC = campoSEC;
	}
	@XmlElement(name="CON")
	public String getCampoCON() {
		return campoCON;
	}
	public void setCampoCON(String campoCON) {
		this.campoCON = campoCON;
	}
	@XmlElement(name="DES")
	public String getCampoDES() {
		return campoDES;
	}

	public void setCampoDES(String campoDES) {
		this.campoDES = campoDES;
	}

	@XmlElement(name="IDP")
	public String getCampoIDP() {
		return campoIDP;
	}
	public void setCampoIDP(String campoIDP) {
		this.campoIDP = campoIDP;
	}
	@XmlElement(name="NMP")
	public String getCampoNMP() {
		return campoNMP;
	}
	public void setCampoNMP(String campoNMP) {
		this.campoNMP = campoNMP;
	}
	@XmlElement(name="VMO")
	public String getCampoVMO() {
		return campoVMO;
	}
	public void setCampoVMO(String campoVMO) {
		this.campoVMO = campoVMO;
	}
	@XmlElement(name="LRC")
	public String getCampoLCR() {
		return campoLCR;
	}
	public void setCampoLCR(String campoLCR) {
		this.campoLCR = campoLCR;
	}
	@XmlElement(name="DLO")
	public String getCampoDLO() {
		return campoDLO;
	}
	public void setCampoDLO(String campoDLO) {
		this.campoDLO = campoDLO;
	}
	@XmlElement(name="LOC")
	public String getCampoLOC() {
		return campoLOC;
	}
	public void setCampoLOC(String campoLOC) {
		this.campoLOC = campoLOC;
	}
	@XmlElement(name="RUT")
	public String getCampoRUT() {
		return campoRUT;
	}
	public void setCampoRUT(String campoRUT) {
		this.campoRUT = campoRUT;
	}
	@XmlElement(name="CTG")
	public String getCampoCTG() {
		return campoCTG;
	}
	public void setCampoCTG(String campoCTG) {
		this.campoCTG = campoCTG;
	}
	@XmlElement(name="CHG")
	public String getCampoCHG() {
		return campoCHG;
	}
	public void setCampoCHG(String campoCHG) {
		this.campoCHG = campoCHG;
	}
	@XmlElement(name="SUD")
	public String getCampoSUD() {
		return campoSUD;
	}
	public void setCampoSUD(String campoSUD) {
		this.campoSUD = campoSUD;
	}
	@XmlElement(name="OFD")
	public String getCampoOFD() {
		return campoOFD;
	}
	public void setCampoOFD(String campoOFD) {
		this.campoOFD = campoOFD;
	}
	
	
}
