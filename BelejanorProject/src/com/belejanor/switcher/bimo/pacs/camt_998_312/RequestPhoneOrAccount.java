//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.07 at 12:38:39 PM COT 
//


package com.belejanor.switcher.bimo.pacs.camt_998_312;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RequestPhoneOrAccount.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RequestPhoneOrAccount">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PHONE"/>
 *     &lt;enumeration value="CTA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RequestPhoneOrAccount")
@XmlEnum
public enum RequestPhoneOrAccount {

    PHONE,
    CTA;

    public String value() {
        return name();
    }

    public static RequestPhoneOrAccount fromValue(String v) {
        return valueOf(v);
    }

}
