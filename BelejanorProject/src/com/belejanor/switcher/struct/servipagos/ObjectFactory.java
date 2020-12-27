package com.belejanor.switcher.struct.servipagos;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _Error_QNAME = new QName("http://servipagos.implementations.middleware.fitbank.com/fault", "Error");

   
    public ObjectFactory() {
    }

    public Error createError() {
        return new Error();
    }

    @XmlElementDecl(namespace = "http://servipagos.implementations.middleware.fitbank.com/fault", name = "Error")
    public JAXBElement<Error> createError(Error value) {
        return new JAXBElement<Error>(_Error_QNAME, Error.class, null, value);
    }

}
