//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.06 at 11:42:12 AM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_441;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for QryPhonebyAcc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QryPhonebyAcc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestPhoneOrAccount" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.441}RequestPhoneOrAccount"/>
 *         &lt;element name="Value" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.441}Max35Text"/>
 *         &lt;element name="SttlmDt" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.441}ISODate"/>
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
@XmlType(name = "QryPhonebyAcc", propOrder = {
    "requestPhoneOrAccount",
    "value",
    "sttlmDt"
})
public class QryPhonebyAcc implements Serializable{

    @XmlElement(name = "RequestPhoneOrAccount", required = true)
    protected RequestPhoneOrAccount requestPhoneOrAccount;
    @XmlElement(name = "Value", required = true)
    protected String value;
    @XmlElement(name = "SttlmDt", required = true)
    protected XMLGregorianCalendar sttlmDt;

    /**
     * Gets the value of the requestPhoneOrAccount property.
     * 
     * @return
     *     possible object is
     *     {@link RequestPhoneOrAccount }
     *     
     */
    public RequestPhoneOrAccount getRequestPhoneOrAccount() {
        return requestPhoneOrAccount;
    }

    /**
     * Sets the value of the requestPhoneOrAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestPhoneOrAccount }
     *     
     */
    public void setRequestPhoneOrAccount(RequestPhoneOrAccount value) {
        this.requestPhoneOrAccount = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
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
