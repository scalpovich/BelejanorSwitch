package urn.iso.std.iso20022.tech.xsd.SRRFaultException;

import javax.xml.ws.WebFault;

@WebFault(name="Error",targetNamespace="http://www.bce.fin.ec/snp/fault")
public class ServiceException extends Exception{

	private static final long serialVersionUID = 4473272190233000001L;
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
