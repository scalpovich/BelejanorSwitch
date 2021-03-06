//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.26 at 05:17:52 PM COT 
//


package com.belejanor.switcher.bimo.pacs.pacs_008_071;

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
 *         &lt;element name="Header" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.06.071}HeaderData"/>
 *         &lt;element name="FIToFICstmrCdtTrf" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.06.071}FIToFICustomerCreditTransferV06"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@SuppressWarnings("serial")
@XmlRootElement(name="Document", namespace="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.06.071")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", propOrder = {
    "header",
    "fiToFICstmrCdtTrf"
}, namespace="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.06.071")
public class Document implements Serializable{

    @XmlElement(name = "Header", required = true)
    protected HeaderData header;
    @XmlElement(name = "FIToFICstmrCdtTrf", required = true)
    protected FIToFICustomerCreditTransferV06 fiToFICstmrCdtTrf;

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
     * Gets the value of the fiToFICstmrCdtTrf property.
     * 
     * @return
     *     possible object is
     *     {@link FIToFICustomerCreditTransferV06 }
     *     
     */
    public FIToFICustomerCreditTransferV06 getFIToFICstmrCdtTrf() {
        return fiToFICstmrCdtTrf;
    }

    /**
     * Sets the value of the fiToFICstmrCdtTrf property.
     * 
     * @param value
     *     allowed object is
     *     {@link FIToFICustomerCreditTransferV06 }
     *     
     */
    public void setFIToFICstmrCdtTrf(FIToFICustomerCreditTransferV06 value) {
        this.fiToFICstmrCdtTrf = value;
    }

}
