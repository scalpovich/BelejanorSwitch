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
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ContentsV02 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContentsV02">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CdtrAcct" type="{urn:iso:std:iso:20022:tech:xsd:pacs.007.001.07.051}AccountInfo"/>
 *         &lt;element name="Prtry" type="{urn:iso:std:iso:20022:tech:xsd:pacs.007.001.07.051}TransCode"/>
 *         &lt;element name="Phone" type="{urn:iso:std:iso:20022:tech:xsd:pacs.007.001.07.051}PhoneNumber"/>
 *         &lt;element name="ReversalRsn" type="{urn:iso:std:iso:20022:tech:xsd:pacs.007.001.07.051}RevReason"/>
 *         &lt;element name="OrgnSetlmtDt" type="{urn:iso:std:iso:20022:tech:xsd:pacs.007.001.07.051}ISODate"/>
 *         &lt;element name="SttlmDt" type="{urn:iso:std:iso:20022:tech:xsd:pacs.007.001.07.051}ISODate"/>
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
@XmlType(name = "ContentsV02", propOrder = {
    "cdtrAcct",
    "prtry",
    "phone",
    "reversalRsn",
    "orgnSetlmtDt",
    "sttlmDt"
})
public class ContentsV02 implements Serializable{

    @XmlElement(name = "CdtrAcct", required = true)
    protected String cdtrAcct;
    @XmlElement(name = "Prtry", required = true)
    protected String prtry;
    @XmlElement(name = "Phone", required = true)
    protected String phone;
    @XmlElement(name = "ReversalRsn", required = true)
    protected String reversalRsn;
    @XmlElement(name = "OrgnSetlmtDt", required = true)
    protected XMLGregorianCalendar orgnSetlmtDt;
    @XmlElement(name = "SttlmDt", required = true)
    protected XMLGregorianCalendar sttlmDt;

    /**
     * Gets the value of the cdtrAcct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCdtrAcct() {
        return cdtrAcct;
    }

    /**
     * Sets the value of the cdtrAcct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCdtrAcct(String value) {
        this.cdtrAcct = value;
    }

    /**
     * Gets the value of the prtry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrtry() {
        return prtry;
    }

    /**
     * Sets the value of the prtry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrtry(String value) {
        this.prtry = value;
    }

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the reversalRsn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReversalRsn() {
        return reversalRsn;
    }

    /**
     * Sets the value of the reversalRsn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReversalRsn(String value) {
        this.reversalRsn = value;
    }

    /**
     * Gets the value of the orgnSetlmtDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOrgnSetlmtDt() {
        return orgnSetlmtDt;
    }

    /**
     * Sets the value of the orgnSetlmtDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOrgnSetlmtDt(XMLGregorianCalendar value) {
        this.orgnSetlmtDt = value;
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
