//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.15 at 09:21:23 AM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_202;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BranchAndFinancialInstitutionIdentification5 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BranchAndFinancialInstitutionIdentification5">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinInstnId" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.202}Z_FinancialInstitutionIdentification"/>
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
@XmlType(name = "BranchAndFinancialInstitutionIdentification5", propOrder = {
    "finInstnId"
})
public class BranchAndFinancialInstitutionIdentification5 implements Serializable{

    @XmlElement(name = "FinInstnId", required = true)
    protected ZFinancialInstitutionIdentification finInstnId;

    /**
     * Gets the value of the finInstnId property.
     * 
     * @return
     *     possible object is
     *     {@link ZFinancialInstitutionIdentification }
     *     
     */
    public ZFinancialInstitutionIdentification getFinInstnId() {
        return finInstnId;
    }

    /**
     * Sets the value of the finInstnId property.
     * 
     * @param value
     *     allowed object is
     *     {@link ZFinancialInstitutionIdentification }
     *     
     */
    public void setFinInstnId(ZFinancialInstitutionIdentification value) {
        this.finInstnId = value;
    }

}