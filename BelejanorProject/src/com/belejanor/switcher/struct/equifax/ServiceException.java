package com.belejanor.switcher.struct.equifax;

import java.io.Serializable;
import javax.xml.ws.WebFault;

import com.belejanor.switcher.struct.servipagos.Error;

@WebFault(name="Error",targetNamespace="http://lorente.fin.ec/ConsultasEquifax/fault")
public class ServiceException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;
	private Error fault;
	
	public ServiceException(){
		
	}
	public ServiceException(Error fault){
		super(fault.getDescription()); 
        this.fault = fault;
	}
	public ServiceException(String Mensaje, Error fault){
		super(Mensaje);
        this.fault = fault;
	}
	
	public ServiceException(String Mensaje, Error fault, Throwable cause){
		super(Mensaje);
        this.fault = fault;
	}
	public Error getFaultInfo() {
		return fault;
	}
	
	public ServiceException(String Code, String Mensaje){
		super(Mensaje);
        this.fault = new Error();
        this.fault.setCode(Code);
        this.fault.setDescription(Mensaje);
	}
	
	public ServiceException(Throwable cause){
		super(cause);
       
	}
	
	public ServiceException(String Mensaje, Throwable cause){
		super(Mensaje, cause);
       
	}
}
