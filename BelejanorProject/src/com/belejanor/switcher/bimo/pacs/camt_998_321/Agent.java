//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.01 at 02:22:51 PM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_321;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Agent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Agent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinInstnId" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.321}FinancialInstitutionIdentification"/>
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
@XmlType(name = "Agent", propOrder = {
    "finInstnId"
})
public class Agent implements Serializable{

    @XmlElement(name = "FinInstnId", required = true)
    protected FinancialInstitutionIdentification finInstnId;

    /**
     * Gets the value of the finInstnId property.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitutionIdentification }
     *     
     */
    public FinancialInstitutionIdentification getFinInstnId() {
        return finInstnId;
    }

    /**
     * Sets the value of the finInstnId property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitutionIdentification }
     *     
     */
    public void setFinInstnId(FinancialInstitutionIdentification value) {
        this.finInstnId = value;
    }

}