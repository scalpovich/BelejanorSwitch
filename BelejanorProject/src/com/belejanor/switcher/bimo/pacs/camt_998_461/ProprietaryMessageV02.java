//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.28 at 05:28:58 PM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_461;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProprietaryMessageV02 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProprietaryMessageV02">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GrpHdr" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.461}GroupHeader"/>
 *         &lt;element name="ValidateOTP" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.461}ValidateOTP"/>
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
@XmlType(name = "ProprietaryMessageV02", propOrder = {
    "grpHdr",
    "validateOTP"
})
public class ProprietaryMessageV02 implements Serializable{

    @XmlElement(name = "GrpHdr", required = true)
    protected GroupHeader grpHdr;
    @XmlElement(name = "ValidateOTP", required = true)
    protected ValidateOTP validateOTP;

    /**
     * Gets the value of the grpHdr property.
     * 
     * @return
     *     possible object is
     *     {@link GroupHeader }
     *     
     */
    public GroupHeader getGrpHdr() {
        return grpHdr;
    }

    /**
     * Sets the value of the grpHdr property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupHeader }
     *     
     */
    public void setGrpHdr(GroupHeader value) {
        this.grpHdr = value;
    }

    /**
     * Gets the value of the validateOTP property.
     * 
     * @return
     *     possible object is
     *     {@link ValidateOTP }
     *     
     */
    public ValidateOTP getValidateOTP() {
        return validateOTP;
    }

    /**
     * Sets the value of the validateOTP property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidateOTP }
     *     
     */
    public void setValidateOTP(ValidateOTP value) {
        this.validateOTP = value;
    }

}