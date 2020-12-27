package com.belejanor.switcher.asextreme.implementations;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.belejanor.switcher.utils.StringUtils;

@XmlRootElement(name="ErrorGenerico")
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericError implements Serializable{

	private String codigoError;
	private String descripcionError;

	public GenericError() {
		
		this.codigoError = "999";
		this.descripcionError = StringUtils.Empty();
	}
	
    public GenericError(String codigoError, String descripcionError) {
		this.codigoError = codigoError;
		this.descripcionError = descripcionError;
	}

	public String getCodigoError() {
		return codigoError;
	}

	public void setCodigoError(String codigoError) {
		this.codigoError = codigoError;
	}
	public String getDescripcionError() {
		return descripcionError;
	}

	public void setDescripcionError(String descripcionError) {
		this.descripcionError = descripcionError;
	}
	
	

	

}
