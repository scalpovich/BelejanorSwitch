//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.01.18 at 08:58:22 AM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_221;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Z_FinancialInstitutionIdentification complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Z_FinancialInstitutionIdentification">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Othr" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.221}Z_GenericFinancialIdentification"/>
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
@XmlType(name = "Z_FinancialInstitutionIdentification", propOrder = {
    "othr"
})
public class ZFinancialInstitutionIdentification implements Serializable{

    @XmlElement(name = "Othr", required = true)
    protected ZGenericFinancialIdentification othr;

    /**
     * Gets the value of the othr property.
     * 
     * @return
     *     possible object is
     *     {@link ZGenericFinancialIdentification }
     *     
     */
    public ZGenericFinancialIdentification getOthr() {
        return othr;
    }

    /**
     * Sets the value of the othr property.
     * 
     * @param value
     *     allowed object is
     *     {@link ZGenericFinancialIdentification }
     *     
     */
    public void setOthr(ZGenericFinancialIdentification value) {
        this.othr = value;
    }

}
