//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.28 at 03:01:26 PM COT 
//


package com.belejanor.switcher.bimo.pacs.pacs_002_052;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatusTypeCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StatusTypeCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ERR"/>
 *     &lt;enumeration value="OK"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "StatusTypeCode")
@XmlEnum
public enum StatusTypeCode {

    ERR,
    OK;

    public String value() {
        return name();
    }

    public static StatusTypeCode fromValue(String v) {
        return valueOf(v);
    }

}
