package com.belejanor.switcher.struct.equifax;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlRootElement(name="RegistrarDatosEvaluacionResponse", namespace="http://lorente.fin.ec/RegisterEquifax")
@XmlType(propOrder={"codError", "desError"})
public class ResponseRegisterDataEquifax implements Serializable{

	private String codError;
	private String desError;
	public String getCodError() {
		return codError;
	}
	public void setCodError(String codError) {
		this.codError = codError;
	}
	public String getDesError() {
		return desError;
	}
	public void setDesError(String desError) {
		this.desError = desError;
	}
	
	
}
