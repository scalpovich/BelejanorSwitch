//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.09 at 04:33:39 PM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_431;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CollectRjct complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CollectRjct">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EndToEnd" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.431}Max35Text"/>
 *         &lt;element name="Reason" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.431}ReasonRjct"/>
 *         &lt;element name="SttlmDt" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.431}ISODate"/>
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
@XmlType(name = "CollectRjct", propOrder = {
    "endToEnd",
    "reason",
    "sttlmDt"
})
public class CollectRjct implements Serializable{

    @XmlElement(name = "EndToEnd", required = true)
    protected String endToEnd;
    @XmlElement(name = "Reason", required = true)
    protected ReasonRjct reason;
    @XmlElement(name = "SttlmDt", required = true)
    protected XMLGregorianCalendar sttlmDt;

    /**
     * Gets the value of the endToEnd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndToEnd() {
        return endToEnd;
    }

    /**
     * Sets the value of the endToEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndToEnd(String value) {
        this.endToEnd = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link ReasonRjct }
     *     
     */
    public ReasonRjct getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReasonRjct }
     *     
     */
    public void setReason(ReasonRjct value) {
        this.reason = value;
    }

    /**
     * Gets the value of the sttlmDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSttlmDt() {
        return sttlmDt;
    }

    /**
     * Sets the value of the sttlmDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSttlmDt(XMLGregorianCalendar value) {
        this.sttlmDt = value;
    }

}
