//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.26 at 05:18:19 PM COT 
//


package com.belejanor.switcher.bimo.pacs.pacs_002_072;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FIToFIPaymentStatusReportV08 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FIToFIPaymentStatusReportV08">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GrpHdr" type="{urn:iso:std:iso:20022:tech:xsd:pacs.002.001.08.072}GroupHeader"/>
 *         &lt;element name="OrgnlGrpInfAndSts" type="{urn:iso:std:iso:20022:tech:xsd:pacs.002.001.08.072}OriginalGroupHeader7" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TxInfAndSts" type="{urn:iso:std:iso:20022:tech:xsd:pacs.002.001.08.072}PaymentTransaction80"/>
 *         &lt;element name="SplmtryData" type="{urn:iso:std:iso:20022:tech:xsd:pacs.002.001.08.072}SupplementaryData1" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "FIToFIPaymentStatusReportV08", propOrder = {
    "grpHdr",
    "orgnlGrpInfAndSts",
    "txInfAndSts",
    "splmtryData"
})
public class FIToFIPaymentStatusReportV08 implements Serializable{

    @XmlElement(name = "GrpHdr", required = true)
    protected GroupHeader grpHdr;
    @XmlElement(name = "OrgnlGrpInfAndSts")
    protected List<OriginalGroupHeader7> orgnlGrpInfAndSts;
    @XmlElement(name = "TxInfAndSts", required = true)
    protected PaymentTransaction80 txInfAndSts;
    @XmlElement(name = "SplmtryData")
    protected List<SupplementaryData1> splmtryData;

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
     * Gets the value of the orgnlGrpInfAndSts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orgnlGrpInfAndSts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrgnlGrpInfAndSts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OriginalGroupHeader7 }
     * 
     * 
     */
    public List<OriginalGroupHeader7> getOrgnlGrpInfAndSts() {
        if (orgnlGrpInfAndSts == null) {
            orgnlGrpInfAndSts = new ArrayList<OriginalGroupHeader7>();
        }
        return this.orgnlGrpInfAndSts;
    }

    /**
     * Gets the value of the txInfAndSts property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentTransaction80 }
     *     
     */
    public PaymentTransaction80 getTxInfAndSts() {
        return txInfAndSts;
    }

    /**
     * Sets the value of the txInfAndSts property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTransaction80 }
     *     
     */
    public void setTxInfAndSts(PaymentTransaction80 value) {
        this.txInfAndSts = value;
    }

    /**
     * Gets the value of the splmtryData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the splmtryData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSplmtryData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupplementaryData1 }
     * 
     * 
     */
    public List<SupplementaryData1> getSplmtryData() {
        if (splmtryData == null) {
            splmtryData = new ArrayList<SupplementaryData1>();
        }
        return this.splmtryData;
    }

}
