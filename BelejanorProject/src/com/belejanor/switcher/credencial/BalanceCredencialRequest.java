package com.belejanor.switcher.credencial;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;

public class BalanceCredencialRequest implements Serializable {

	private static final long serialVersionUID = 7268155825208898244L;
	
	private String ReferenciaCuentaEMI;
	private String IdentificadorCuentaEMI;
	private String IdentificadorADQ;
	private String Identificador;
	private String Origen;
	
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
	@XmlElement(name="Origen")
	public String getOrigen() {
		return Origen;
	}
	
	public void setOrigen(String origen) {
		Origen = origen;
	}
	
	
}
