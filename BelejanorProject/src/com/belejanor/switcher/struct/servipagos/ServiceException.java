package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;
import javax.xml.ws.WebFault;


@WebFault(name="Error",targetNamespace="http://servipagos.implementations.middleware.fitbank.com/fault")
public class ServiceException extends Exception implements Serializable{

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
