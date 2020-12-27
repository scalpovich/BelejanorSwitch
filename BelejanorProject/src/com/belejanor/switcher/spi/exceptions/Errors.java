package com.belejanor.switcher.spi.exceptions;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Errors", namespace = "http://www.bce.fin.ec/srr/fault")
@SuppressWarnings("serial")
@XmlType(name = "Error", propOrder = {
	    "code",
	    "description"
	}, namespace = "http://www.bce.fin.ec/srr/fault")

public class Errors extends Exception implements Serializable{

	private String code;
	private String description;
	
	public Errors() {
		
	}
	
	public Errors(String code, String description) {
		
		this.code = code;
		this.description = description;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
