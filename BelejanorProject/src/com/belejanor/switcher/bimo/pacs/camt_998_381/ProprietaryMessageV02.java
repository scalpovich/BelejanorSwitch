//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.08 at 11:51:01 AM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_381;

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
 *         &lt;element name="GrpHdr" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.381}GroupHeader"/>
 *         &lt;element name="CollectRq" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.381}CollectRq"/>
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
    "collectRq"
})
public class ProprietaryMessageV02 implements Serializable{

    @XmlElement(name = "GrpHdr", required = true)
    protected GroupHeader grpHdr;
    @XmlElement(name = "CollectRq", required = true)
    protected CollectRq collectRq;

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
     * Gets the value of the collectRq property.
     * 
     * @return
     *     possible object is
     *     {@link CollectRq }
     *     
     */
    public CollectRq getCollectRq() {
        return collectRq;
    }

    /**
     * Sets the value of the collectRq property.
     * 
     * @param value
     *     allowed object is
     *     {@link CollectRq }
     *     
     */
    public void setCollectRq(CollectRq value) {
        this.collectRq = value;
    }

}