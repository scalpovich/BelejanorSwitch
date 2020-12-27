package com.belejanor.switcher.spi.exceptions;

import javax.xml.ws.WebFault;


@WebFault(name="Errors",targetNamespace="http://www.bce.fin.ec/snp/fault")
public class ServiceException extends Exception{

	private static final long serialVersionUID = 4473272190233000001L;
	private Errors fault;
	
	public ServiceException(){
		
	}
	public ServiceException(Errors fault){
		super(fault.getDescription()); 
        this.fault = fault;
	}
	public ServiceException(String Mensaje, Errors fault){
		super(Mensaje);
        this.fault = fault;
	}
	
	public ServiceException(String Mensaje, Errors fault, Throwable cause){
		super(Mensaje);
        this.fault = fault;
	}
	public Errors getFaultInfo() {
		return fault;
	}
	
	public ServiceException(String Code, String Mensaje){
		super(Mensaje);
        this.fault = new Errors();
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
