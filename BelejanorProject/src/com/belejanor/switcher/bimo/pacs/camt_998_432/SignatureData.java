//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.09 at 04:34:00 PM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_432;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SignatureData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SignatureData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="XMLSgntrs" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.432}Max256Text"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignatureData", propOrder = {
    "xmlSgntrs"
})
public class SignatureData implements Serializable{

    @XmlElement(name = "XMLSgntrs", required = true)
    protected String xmlSgntrs;

    /**
     * Gets the value of the xmlSgntrs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXMLSgntrs() {
        return xmlSgntrs;
    }

    /**
     * Sets the value of the xmlSgntrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXMLSgntrs(String value) {
        this.xmlSgntrs = value;
    }

}