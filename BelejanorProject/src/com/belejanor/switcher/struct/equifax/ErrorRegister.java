package com.belejanor.switcher.struct.equifax;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Error", propOrder = {
    "code",
    "description"
}, namespace = "http://lorente.fin.ec/RegisterEquifax/fault")
@XmlRootElement(name = "Error", namespace = "http://lorente.fin.ec/RegisterEquifax/fault")
public class ErrorRegister extends Throwable implements Serializable{

	 @XmlElement(name = "Code", required = true)
	    protected String code;
	    @XmlElement(name = "Description", required = true)
	    protected String description;
	    
	    public ErrorRegister() {
	    	
	    }
	    
	    public ErrorRegister(String code, String desError){	
	    	
	    	this.code = code;
	    	this.description = desError;
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
