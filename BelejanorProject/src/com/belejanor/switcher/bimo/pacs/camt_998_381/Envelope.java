//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.08 at 11:51:01 AM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_381;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Envelope complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Envelope">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cnts" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.381}ContentsV01"/>
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
@XmlType(name = "Envelope", propOrder = {
    "cnts"
})
public class Envelope implements Serializable{

    @XmlElement(name = "Cnts", required = true)
    protected ContentsV01 cnts;

    /**
     * Gets the value of the cnts property.
     * 
     * @return
     *     possible object is
     *     {@link ContentsV01 }
     *     
     */
    public ContentsV01 getCnts() {
        return cnts;
    }

    /**
     * Sets the value of the cnts property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentsV01 }
     *     
     */
    public void setCnts(ContentsV01 value) {
        this.cnts = value;
    }

}
