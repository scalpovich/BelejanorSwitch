//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.15 at 01:12:59 PM COT 
//


package com.belejanor.switcher.bimo.pacs.pacs_008_021;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrigIdData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrigIdData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Channel" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.06.021}Max35Text"/>
 *         &lt;element name="App" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.06.021}Max35Text"/>
 *         &lt;element name="Service" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.06.021}ServiceData"/>
 *         &lt;element name="OtherId" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.06.021}Max35Text"/>
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
@XmlType(name = "OrigIdData", propOrder = {
    "channel",
    "app",
    "service",
    "otherId"
})
public class OrigIdData implements Serializable{

    @XmlElement(name = "Channel", required = true)
    protected String channel;
    @XmlElement(name = "App", required = true)
    protected String app;
    @XmlElement(name = "Service", required = true)
    protected ServiceData service;
    @XmlElement(name = "OtherId", required = true)
    protected String otherId;

    /**
     * Gets the value of the channel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Sets the value of the channel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChannel(String value) {
        this.channel = value;
    }

    /**
     * Gets the value of the app property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApp() {
        return app;
    }

    /**
     * Sets the value of the app property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApp(String value) {
        this.app = value;
    }

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceData }
     *     
     */
    public ServiceData getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceData }
     *     
     */
    public void setService(ServiceData value) {
        this.service = value;
    }

    /**
     * Gets the value of the otherId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherId() {
        return otherId;
    }

    /**
     * Sets the value of the otherId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherId(String value) {
        this.otherId = value;
    }

}
