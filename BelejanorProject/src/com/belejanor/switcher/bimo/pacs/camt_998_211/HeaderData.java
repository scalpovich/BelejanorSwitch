//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.01.16 at 02:28:55 PM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_211;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HeaderData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HeaderData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrigId" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.211}OrigIdData"/>
 *         &lt;element name="Sender" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.211}EntityCode"/>
 *         &lt;element name="Receiver" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.211}EntityCode"/>
 *         &lt;element name="Mge" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.211}MjeData"/>
 *         &lt;element name="CpyDplct" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.211}CopyDuplicate1Code" minOccurs="0"/>
 *         &lt;element name="PssblDplct" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.211}Indicator"/>
 *         &lt;element name="Prty" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.211}Priority"/>
 *         &lt;element name="Sgntr" type="{urn:iso:std:iso:20022:tech:xsd:camt.998.211}SignatureData" minOccurs="0"/>
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
@XmlType(name = "HeaderData", propOrder = {
    "origId",
    "sender",
    "receiver",
    "mge",
    "cpyDplct",
    "pssblDplct",
    "prty",
    "sgntr"
})
public class HeaderData implements Serializable{

    @XmlElement(name = "OrigId", required = true)
    protected OrigIdData origId;
    @XmlElement(name = "Sender", required = true)
    protected String sender;
    @XmlElement(name = "Receiver", required = true)
    protected String receiver;
    @XmlElement(name = "Mge", required = true)
    protected MjeData mge;
    @XmlElement(name = "CpyDplct")
    protected String cpyDplct;
    @XmlElement(name = "PssblDplct")
    protected boolean pssblDplct;
    @XmlElement(name = "Prty", required = true)
    protected Priority prty;
    @XmlElement(name = "Sgntr")
    protected SignatureData sgntr;

    /**
     * Gets the value of the origId property.
     * 
     * @return
     *     possible object is
     *     {@link OrigIdData }
     *     
     */
    public OrigIdData getOrigId() {
        return origId;
    }

    /**
     * Sets the value of the origId property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrigIdData }
     *     
     */
    public void setOrigId(OrigIdData value) {
        this.origId = value;
    }

    /**
     * Gets the value of the sender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSender() {
        return sender;
    }

    /**
     * Sets the value of the sender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSender(String value) {
        this.sender = value;
    }

    /**
     * Gets the value of the receiver property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Sets the value of the receiver property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiver(String value) {
        this.receiver = value;
    }

    /**
     * Gets the value of the mge property.
     * 
     * @return
     *     possible object is
     *     {@link MjeData }
     *     
     */
    public MjeData getMge() {
        return mge;
    }

    /**
     * Sets the value of the mge property.
     * 
     * @param value
     *     allowed object is
     *     {@link MjeData }
     *     
     */
    public void setMge(MjeData value) {
        this.mge = value;
    }

    /**
     * Gets the value of the cpyDplct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCpyDplct() {
        return cpyDplct;
    }

    /**
     * Sets the value of the cpyDplct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCpyDplct(String value) {
        this.cpyDplct = value;
    }

    /**
     * Gets the value of the pssblDplct property.
     * 
     */
    public boolean isPssblDplct() {
        return pssblDplct;
    }

    /**
     * Sets the value of the pssblDplct property.
     * 
     */
    public void setPssblDplct(boolean value) {
        this.pssblDplct = value;
    }

    /**
     * Gets the value of the prty property.
     * 
     * @return
     *     possible object is
     *     {@link Priority }
     *     
     */
    public Priority getPrty() {
        return prty;
    }

    /**
     * Sets the value of the prty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Priority }
     *     
     */
    public void setPrty(Priority value) {
        this.prty = value;
    }

    /**
     * Gets the value of the sgntr property.
     * 
     * @return
     *     possible object is
     *     {@link SignatureData }
     *     
     */
    public SignatureData getSgntr() {
        return sgntr;
    }

    /**
     * Sets the value of the sgntr property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureData }
     *     
     */
    public void setSgntr(SignatureData value) {
        this.sgntr = value;
    }

}
