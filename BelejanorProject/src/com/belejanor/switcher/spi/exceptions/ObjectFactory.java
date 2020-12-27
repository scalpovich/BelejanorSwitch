package com.belejanor.switcher.spi.exceptions;


import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

 private final static QName _Error_QNAME = new QName("http://www.bce.fin.ec/srr/fault", "Errors");

 /**
  * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: urn.iso.std.iso20022.tech.xsd.SRRFaultException
  * 
  */
 public ObjectFactory() {
 }

 /**
  * Create an instance of {@link Error }
  * 
  */
 public Errors createError() {
     return new Errors();
 }

 /**
  * Create an instance of {@link JAXBElement }{@code <}{@link Error }{@code >}}
  * 
  */
 @XmlElementDecl(namespace = "http://www.bce.fin.ec/srr/fault", name = "Errors")
 public JAXBElement<Errors> createError(Errors value) {
     return new JAXBElement<Errors>(_Error_QNAME, Errors.class, null, value);
 }

}

