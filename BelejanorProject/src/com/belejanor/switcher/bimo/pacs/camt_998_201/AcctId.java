//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.15 at 09:21:11 AM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_201;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AcctId complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AcctId">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EQ" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.201}EQ"/>
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
@XmlType(name = "AcctId", propOrder = {
    "eq"
})
public class AcctId  implements Serializable{

    @XmlElement(name = "EQ", required = true)
    protected EQ eq;

    /**
     * Gets the value of the eq property.
     * 
     * @return
     *     possible object is
     *     {@link EQ }
     *     
     */
    public EQ getEQ() {
        return eq;
    }

    /**
     * Sets the value of the eq property.
     * 
     * @param value
     *     allowed object is
     *     {@link EQ }
     *     
     */
    public void setEQ(EQ value) {
        this.eq = value;
    }

}
