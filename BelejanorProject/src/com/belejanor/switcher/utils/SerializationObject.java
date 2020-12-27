package com.belejanor.switcher.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.tempuri.ConfirmaTransaccionStub;
import org.tempuri.ConfirmaTransaccionStub.ConfirmacionTransaccion;
import org.tempuri.ConfirmaTransaccionStub.ConfirmacionTransaccionResponse;
import org.tempuri.Service1Stub.Reply_Structure_SBD;
import org.tempuri.Service1Stub.Request_Structure_SBD;

import com.belejanor.switcher.asextreme.ExtremeReply;
import com.belejanor.switcher.asextreme.ExtremeRequest;
import com.belejanor.switcher.asextreme.Header;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.detail.FitBank;
import com.belejanor.switcher.electroniccash.DTORequestCredit;
import com.belejanor.switcher.electroniccash.DTORequestDebit;
import com.belejanor.switcher.electroniccash.DTORequestIsValidAccount;
import com.belejanor.switcher.electroniccash.DTORequestRevert;
import com.belejanor.switcher.electroniccashres.DTOResponseCredit;
import com.belejanor.switcher.electroniccashres.DTOResponseDebit;
import com.belejanor.switcher.electroniccashres.DTOResponseIsValidAccount;
import com.belejanor.switcher.electroniccashres.DTOResponseRevert;
import com.belejanor.switcher.fit1struct.Authorization;
import com.belejanor.switcher.fit1struct.Bloques;
import com.belejanor.switcher.fit1struct.Cabecera;
import com.belejanor.switcher.fit1struct.Campos;
import com.belejanor.switcher.fit1struct.Componente;
import com.belejanor.switcher.fit1struct.Creditos;
import com.belejanor.switcher.fit1struct.Debitos;
import com.belejanor.switcher.fit1struct.DetailFit1;
import com.belejanor.switcher.fit1struct.Detalle;
import com.belejanor.switcher.fit1struct.Respuesta;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.fitbank.dto.management.Detail;
import com.thoughtworks.xstream.XStream;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.DocumentCobroSCI;
import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.DocumentRetiro;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentDeposito;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferencia;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferenciaSPI;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTORequestAccountAssociation;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTORequestAccountAssociationConfirm;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTORequestAccountDissociation;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTOResponseAccountAssociation;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTOResponseAccountAssociationConfirm;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTOResponseAccountDissociation;
import com.thoughtworks.xstream.converters.basic.DateConverter;
public class SerializationObject {
	
	public static String ObjectToString( final Object obj){
		
		Logger log = null;
		String xml = null;
		try {	
			
			JAXBContext context = JAXBContext.newInstance(new Class[] {Iso8583.class, FitBank.class, DTORequestAccountAssociationConfirm.class,
					DTORequestAccountDissociation.class, DTOResponseAccountAssociationConfirm.class, DTOResponseAccountDissociation.class,
					DTORequestAccountAssociation.class, DTOResponseAccountAssociation.class, DTORequestCredit.class, DTORequestDebit.class,
					DTOResponseCredit.class, DTOResponseDebit.class, DTORequestIsValidAccount.class, DTOResponseIsValidAccount.class,
					DTORequestRevert.class, DTOResponseRevert.class, ExtremeRequest.class, ExtremeReply.class, 
					Reply_Structure_SBD.class, Request_Structure_SBD.class, ConfirmacionTransaccion.class, 
					ConfirmacionTransaccionResponse.class, DocumentRespuesta.class, DocumentDeposito.class,
					DocumentRetiro.class, DocumentReverso.class, DocumentTransferencia.class, Header.class,
					Authorization.class, Bloques.class, Cabecera.class, Campos.class, Componente.class, Creditos.class,
					Debitos.class, DetailFit1.class, Detalle.class, Respuesta.class, Detail.class,
					urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferenciaSPI.class,
					DocumentTransferenciaSPI.class,
					DocumentCobroSCI.class});
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);	
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			
			OutputStream output = new OutputStream() {
				private StringBuilder string = new StringBuilder();
				@Override
				public void write(int b) throws IOException {
					this.string.append((char) b );				
				}
				@Override
				public String toString(){
		            return this.string.toString();
		        }
			};	
			OutputStreamWriter oo = new OutputStreamWriter(output,"UTF-8");
			marshaller.marshal(obj, oo);
			xml = output.toString();
		}catch (JAXBException e){
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo SerializationObject::ObjectToString ", TypeMonitor.error, e);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo SerializationObject::ObjectToString (General) ", TypeMonitor.error, e);
		}
		return StringUtils.stripEspecial(xml);
	}
	public static String ObjectToString( final Object obj, final Class<?> zClass){
		
		Logger log = null;
		String xml = null;
		try {	
			
			JAXBContext context = JAXBContext.newInstance(new Class[] {
					zClass});
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);	
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			
			OutputStream output = new OutputStream() {
				private StringBuilder string = new StringBuilder();
				@Override
				public void write(int b) throws IOException {
					this.string.append((char) b );				
				}
				@Override
				public String toString(){
		            return this.string.toString();
		        }
			};	
			OutputStreamWriter oo = new OutputStreamWriter(output,"UTF-8");
			marshaller.marshal(obj, oo);
			xml = output.toString();
		}catch (JAXBException e){
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo SerializationObject::ObjectToString ", TypeMonitor.error, e);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo SerializationObject::ObjectToString (General) ", TypeMonitor.error, e);
		}
		return StringUtils.stripEspecial(xml);
	}
	public static String ObjectToStringII(final Object obj){
		
		Logger log = null;
		String xml = null;
		try {
			
		      JAXBContext context = JAXBContext.newInstance(new Class[] {Reply_Structure_SBD.class, Request_Structure_SBD.class});
		      Marshaller marshaller = context.createMarshaller();
		      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		      QName qName = null;
		      OutputStream output = new OutputStream() {
					private StringBuilder string = new StringBuilder();
					@Override
					public void write(int b) throws IOException {
						this.string.append((char) b );				
					}
					@Override
					public String toString(){
			            return this.string.toString();
			        }
				};	
				OutputStreamWriter oo = new OutputStreamWriter(output,"UTF-8");
		      
		      if(obj instanceof Reply_Structure_SBD){

		    	  Reply_Structure_SBD xx = (Reply_Structure_SBD) obj;
			      qName = new QName(null, "Reply_Structure_SBD".toUpperCase());
			      JAXBElement<Reply_Structure_SBD> root = new JAXBElement<Reply_Structure_SBD>(
			            qName, Reply_Structure_SBD.class, xx);
			        marshaller.marshal(root, oo);
			        xml = output.toString();
		      }else if (obj instanceof Request_Structure_SBD) {
				
		    	  Request_Structure_SBD xx = (Request_Structure_SBD) obj;
			      qName = new QName(null, "Request_Structure_SBD".toUpperCase());
			      JAXBElement<Request_Structure_SBD> root = new JAXBElement<Request_Structure_SBD>(
			            qName, Request_Structure_SBD.class, xx);
			        marshaller.marshal(root, oo);
			        xml = output.toString();
			  }else if (obj instanceof ConfirmacionTransaccion) {
				
				  ConfirmacionTransaccion xx = (ConfirmacionTransaccion) obj;
			      qName = new QName(null, "ConfirmacionTransaccion".toUpperCase());
			      JAXBElement<ConfirmacionTransaccion> root = new JAXBElement<ConfirmacionTransaccion>(
			            qName, ConfirmacionTransaccion.class, xx);
			        marshaller.marshal(root, oo);
			        xml = output.toString();
			        
			  }else if (obj instanceof ConfirmacionTransaccionResponse || obj instanceof ConfirmaTransaccionStub.Reply_SW) {
				
				  ConfirmacionTransaccion xx = (ConfirmacionTransaccion) obj;
			      qName = new QName(null, "ConfirmacionTransaccionResponse".toUpperCase());
			      JAXBElement<ConfirmacionTransaccion> root = new JAXBElement<ConfirmacionTransaccion>(
			            qName, ConfirmacionTransaccion.class, xx);
			        marshaller.marshal(root, oo);
			        xml = output.toString();
			  }
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo SerializationObject::ObjectToStringII ", TypeMonitor.error, e);
		}
		return  StringUtils.stripEspecial(xml);
	}
	public static Object StringToObject(String Xml){
		
		Logger log = null;
		Object obj = null;
		try {			
			JAXBContext context = JAXBContext.newInstance(new Class[] {Iso8583.class, ExtremeReply.class, 
														  ExtremeRequest.class, Iterables.class, DetailFit1.class,
														  Cabecera.class, Creditos.class, Debitos.class, Componente.class});
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);	
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");					
			InputStream stream = new ByteArrayInputStream(Xml.getBytes(StandardCharsets.UTF_8));
			Unmarshaller unmarshaller = context.createUnmarshaller();
			obj = (Object) unmarshaller.unmarshal(stream);
		}catch (JAXBException e){
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo SerializationObject::StringToObject ", TypeMonitor.error, e);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo SerializationObject::StringToObject (General) ", TypeMonitor.error, e);
		}
		return obj;
	}
	public static Object StringToObject(String Xml, Class<?> classe){
		
		Logger log = null;
		Object obj = null;
		try {	
			
			if(StringUtils.IsNullOrEmpty(Xml))
				return null;
			
			JAXBContext context = JAXBContext.newInstance(classe);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);	
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");					
			InputStream stream = new ByteArrayInputStream(Xml.getBytes(StandardCharsets.UTF_8));
			Unmarshaller unmarshaller = context.createUnmarshaller();
			obj = (Object) unmarshaller.unmarshal(stream);
		}catch (JAXBException e){
			
			System.out.println("*************** Error en: " + Xml + "  " + classe.getName());
			log = new Logger();
			log.WriteLogMonitor("Error modulo SerializationObject::StringToObject ", TypeMonitor.error, e);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo SerializationObject::StringToObject (General) ", TypeMonitor.error, e);
		}
		return obj;
	}
	public static String ObjectToXML(Serializable object) {
		XStream xs = new XStream();
		xs.processAnnotations(Iso8583.class);
		return StringUtils.stripEspecial(xs.toXML(object));
	}
		
	
	public static Object XMLToObject(String xml){
		XStream xs = new XStream();
		return xs.fromXML(xml);
	}
	public static Object XMLToObjectJson(String xml){
		XStream xs = new XStream();
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		String[] acceptableFormats = {"yyyy-MM-dd HH:mm:ss","yyyy/MM/dd HH:mm:ss", "YYYY-MM-DDThh:mm:ss", "YYYY-MM-DDTHH:MM:SSZ"};
		xs.registerConverter(new DateConverter(dateFormat, acceptableFormats));
		xs.alias("Iso8583", Iso8583.class); 
		xs.alias("iso8583", Iso8583.class); 
		return xs.fromXML(xml);
	}
}
