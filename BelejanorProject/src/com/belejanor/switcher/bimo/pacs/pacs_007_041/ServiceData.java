//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.26 at 10:01:11 AM COT 
//


package com.belejanor.switcher.bimo.pacs.pacs_007_041;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IdServ" type="{urn:iso:std:iso:20022:tech:xsd:pacs.007.001.07.041}Max256Text"/>
 *         &lt;element name="VersServ" type="{urn:iso:std:iso:20022:tech:xsd:pacs.007.001.07.041}Max35Text"/>
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
@XmlType(name = "ServiceData", propOrder = {
    "idServ",
    "versServ"
})
public class ServiceData implements Serializable{

    @XmlElement(name = "IdServ", required = true)
    protected String idServ;
    @XmlElement(name = "VersServ", required = true)
    protected String versServ;

    /**
     * Gets the value of the idServ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdServ() {
        return idServ;
    }

    /**
     * Sets the value of the idServ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdServ(String value) {
        this.idServ = value;
    }

    /**
     * Gets the value of the versServ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersServ() {
        return versServ;
    }

    /**
     * Sets the value of the versServ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersServ(String value) {
        this.versServ = value;
    }

}
