//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.01.18 at 08:58:51 AM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_222;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AcctUnEnroll complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AcctUnEnroll">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrgnlGrpInf" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.222}OriginalGroupInformation"/>
 *         &lt;element name="TxSts" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.222}StatusCode"/>
 *         &lt;element name="StsRsnInf" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.222}StatusReasonInformation"/>
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
@XmlType(name = "AcctUnEnroll", propOrder = {
    "orgnlGrpInf",
    "txSts",
    "stsRsnInf"
})
public class AcctUnEnroll implements Serializable{

    @XmlElement(name = "OrgnlGrpInf", required = true)
    protected OriginalGroupInformation orgnlGrpInf;
    @XmlElement(name = "TxSts", required = true)
    protected StatusCode txSts;
    @XmlElement(name = "StsRsnInf", required = true)
    protected StatusReasonInformation stsRsnInf;

    /**
     * Gets the value of the orgnlGrpInf property.
     * 
     * @return
     *     possible object is
     *     {@link OriginalGroupInformation }
     *     
     */
    public OriginalGroupInformation getOrgnlGrpInf() {
        return orgnlGrpInf;
    }

    /**
     * Sets the value of the orgnlGrpInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginalGroupInformation }
     *     
     */
    public void setOrgnlGrpInf(OriginalGroupInformation value) {
        this.orgnlGrpInf = value;
    }

    /**
     * Gets the value of the txSts property.
     * 
     * @return
     *     possible object is
     *     {@link StatusCode }
     *     
     */
    public StatusCode getTxSts() {
        return txSts;
    }

    /**
     * Sets the value of the txSts property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusCode }
     *     
     */
    public void setTxSts(StatusCode value) {
        this.txSts = value;
    }

    /**
     * Gets the value of the stsRsnInf property.
     * 
     * @return
     *     possible object is
     *     {@link StatusReasonInformation }
     *     
     */
    public StatusReasonInformation getStsRsnInf() {
        return stsRsnInf;
    }

    /**
     * Sets the value of the stsRsnInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusReasonInformation }
     *     
     */
    public void setStsRsnInf(StatusReasonInformation value) {
        this.stsRsnInf = value;
    }

}
