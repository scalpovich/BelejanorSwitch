//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.06.12 at 04:00:27 PM COT 
//


package urn.iso.std.iso20022.tech.xsd.pacs_003_001_04;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RegulatoryReportingType1Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RegulatoryReportingType1Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CRED"/>
 *     &lt;enumeration value="DEBT"/>
 *     &lt;enumeration value="BOTH"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RegulatoryReportingType1Code")
@XmlEnum
public enum RegulatoryReportingType1Code {

    CRED,
    DEBT,
    BOTH;

    public String value() {
        return name();
    }

    public static RegulatoryReportingType1Code fromValue(String v) {
        return valueOf(v);
    }

}
