//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.15 at 09:21:11 AM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_201;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetAcct complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetAcct">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AcctQryDef" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.201}AcctQryDef"/>
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
@XmlType(name = "GetAcct", propOrder = {
    "acctQryDef"
})
public class GetAcct  implements Serializable{

    @XmlElement(name = "AcctQryDef", required = true)
    protected AcctQryDef acctQryDef;

    /**
     * Gets the value of the acctQryDef property.
     * 
     * @return
     *     possible object is
     *     {@link AcctQryDef }
     *     
     */
    public AcctQryDef getAcctQryDef() {
        return acctQryDef;
    }

    /**
     * Sets the value of the acctQryDef property.
     * 
     * @param value
     *     allowed object is
     *     {@link AcctQryDef }
     *     
     */
    public void setAcctQryDef(AcctQryDef value) {
        this.acctQryDef = value;
    }

}
