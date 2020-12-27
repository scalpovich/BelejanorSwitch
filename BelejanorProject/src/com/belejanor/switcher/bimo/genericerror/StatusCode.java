package com.belejanor.switcher.bimo.genericerror;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "StatusCode")
@XmlEnum
public enum StatusCode {

    ERR,
    OK;

    public String value() {
        return name();
    }

    public static StatusCode fromValue(String v) {
        return valueOf(v);
    }

}
