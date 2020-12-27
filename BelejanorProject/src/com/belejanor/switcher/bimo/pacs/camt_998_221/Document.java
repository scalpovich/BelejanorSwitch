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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Document complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Document">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Header" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.221}HeaderData"/>
 *         &lt;element name="PrtryMsg" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.221}ProprietaryMessageV02"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name="Document", namespace="urn:iso:std:iso:20022:tech:xsd:camt.998.221")								
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", propOrder = {
    "header",
    "prtryMsg"
},namespace = "urn:iso:std:iso:20022:tech:xsd:camt.998.221")
public class Document implements Serializable{

    @XmlElement(name = "Header", required = true)
    protected HeaderData header;
    @XmlElement(name = "PrtryMsg", required = true)
    protected ProprietaryMessageV02 prtryMsg;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link HeaderData }
     *     
     */
    public HeaderData getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link HeaderData }
     *     
     */
    public void setHeader(HeaderData value) {
        this.header = value;
    }

    /**
     * Gets the value of the prtryMsg property.
     * 
     * @return
     *     possible object is
     *     {@link ProprietaryMessageV02 }
     *     
     */
    public ProprietaryMessageV02 getPrtryMsg() {
        return prtryMsg;
    }

    /**
     * Sets the value of the prtryMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProprietaryMessageV02 }
     *     
     */
    public void setPrtryMsg(ProprietaryMessageV02 value) {
        this.prtryMsg = value;
    }

}
