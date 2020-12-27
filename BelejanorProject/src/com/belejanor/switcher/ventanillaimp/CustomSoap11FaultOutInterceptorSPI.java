package com.belejanor.switcher.ventanillaimp;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Document;

import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;

public class CustomSoap11FaultOutInterceptorSPI extends AbstractSoapInterceptor {

	public CustomSoap11FaultOutInterceptorSPI() {
        super(Phase.MARSHAL);
    }
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		
		String serializateError = null;
		String xml = null;
		Document doc = null;
		DocumentBuilder builder = null;
		Fault fault = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Exception e = message.getContent(Exception.class); 
		if(!e.getCause().getClass().toString()
				.equalsIgnoreCase("class javax.xml.bind.UnmarshalException")){
			
			if(e.getCause().getClass().toString()
					.equalsIgnoreCase("class urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error")){
				
				Error eee = (Error) e.getCause();
				System.out.println(eee.getCode() + "  " + eee.getDescription());
				try {
					
					fault = (Fault) e; 
	                fault.setMessage("Fault2"); 
	                
	                serializateError = SerializationObject.ObjectToString(eee, Error.class);
					serializateError = serializateError
            				.replace("<stackTrace/>", "").replace("\n", "")
            				.replace(":ns2", "").replace("ns2:", "");
            		serializateError = GeneralUtils.formatXML(serializateError);
            		xml = "<detail><Error xmlns=\"http://www.bce.fin.ec/snp/fault\"><Code>"+ eee.getCode() +"</Code><Description>"+ eee.getDescription() +"</Description></Error></detail>";
            		xml = StringUtils.replaceAcentsAndCharsEspecial(xml);
                    doc = builder.parse(new ByteArrayInputStream(xml.getBytes())); 
                    fault.setDetail(doc.getDocumentElement()); 
					
				} catch (Exception e2) {
					 System.out.println("Cannot build detail element[0]: " + e.getMessage()); 
				}
				
			}else{
			
		        if (e instanceof Fault) { 
		                fault = (Fault) e; 
		                fault.setMessage("Fault1"); 
		                
		                try { 
		                        
		                		Error err = new Error("FF02", e.getCause().toString());
		                		serializateError = SerializationObject.ObjectToString(err, Error.class);
		                		serializateError = serializateError
		                				.replace("<stackTrace/>", "").replace("\n", "")
		                				.replace(":ns2", "").replace("ns2:", "");
		                		serializateError = GeneralUtils.formatXML(serializateError);
		                		xml = "<detail><Error xmlns=\"http://www.bce.fin.ec/snp/fault\"><Code>"+ err.getCode() +"</Code><Description>"+ err.getDescription() +"</Description></Error></detail>";
		                		xml = StringUtils.replaceAcentsAndCharsEspecial(xml);
		                        doc = builder.parse(new ByteArrayInputStream(xml.getBytes())); 
		                        fault.setDetail(doc.getDocumentElement()); 
		                } catch (Exception e1) { 
		                        System.out.println("Cannot build detail element[1]: " + e.getMessage()); 
		                } 
		        } 
			}
		
		}
	}
}
