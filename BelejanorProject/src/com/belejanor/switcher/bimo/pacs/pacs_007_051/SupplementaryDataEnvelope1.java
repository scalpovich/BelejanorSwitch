//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.28 at 02:59:57 PM COT 
//


package com.belejanor.switcher.bimo.pacs.pacs_007_051;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SupplementaryDataEnvelope1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupplementaryDataEnvelope1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cnts" type="{urn:iso:std:iso:20022:tech:xsd:pacs.007.001.07.051}ContentsV02"/>
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
@XmlType(name = "SupplementaryDataEnvelope1", propOrder = {
    "cnts"
})
public class SupplementaryDataEnvelope1 implements Serializable{

    @XmlElement(name = "Cnts", required = true)
    protected ContentsV02 cnts;

    /**
     * Gets the value of the cnts property.
     * 
     * @return
     *     possible object is
     *     {@link ContentsV02 }
     *     
     */
    public ContentsV02 getCnts() {
        return cnts;
    }

    /**
     * Sets the value of the cnts property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentsV02 }
     *     
     */
    public void setCnts(ContentsV02 value) {
        this.cnts = value;
    }

}
