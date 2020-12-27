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
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for MjeData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MjeData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="camt.998.201"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="RoR" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.201}RoRCod"/>
 *         &lt;element name="IdMge" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.201}Max35Text"/>
 *         &lt;element name="OpeDate" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.201}ISODateTime"/>
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
@XmlType(name = "MjeData", propOrder = {
    "type",
    "roR",
    "idMge",
    "opeDate"
})
public class MjeData  implements Serializable{

    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(name = "RoR", required = true)
    protected RoRCod roR;
    @XmlElement(name = "IdMge", required = true)
    protected String idMge;
    @XmlElement(name = "OpeDate", required = true)
    protected XMLGregorianCalendar opeDate;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the roR property.
     * 
     * @return
     *     possible object is
     *     {@link RoRCod }
     *     
     */
    public RoRCod getRoR() {
        return roR;
    }

    /**
     * Sets the value of the roR property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoRCod }
     *     
     */
    public void setRoR(RoRCod value) {
        this.roR = value;
    }

    /**
     * Gets the value of the idMge property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdMge() {
        return idMge;
    }

    /**
     * Sets the value of the idMge property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdMge(String value) {
        this.idMge = value;
    }

    /**
     * Gets the value of the opeDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOpeDate() {
        return opeDate;
    }

    /**
     * Sets the value of the opeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOpeDate(XMLGregorianCalendar value) {
        this.opeDate = value;
    }

}
