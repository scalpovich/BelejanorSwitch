
package com.belejanor.switcher.implementations.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class was generated by Apache CXF 3.1.10
 * Tue May 29 12:37:27 COT 2018
 * Generated source version: 3.1.10
 */

@XmlRootElement(name = "movementsAccount", namespace = "http://implementations.middleware.fitbank.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "movementsAccount", namespace = "http://implementations.middleware.fitbank.com/")

public class MovementsAccount {

    @XmlElement(name = "movementsCredencialRequest")
    private com.belejanor.switcher.credencial.MovementsCredencialRequest arg0;

    public com.belejanor.switcher.credencial.MovementsCredencialRequest getArg0() {
        return this.arg0;
    }

    public void setArg0(com.belejanor.switcher.credencial.MovementsCredencialRequest newArg0)  {
        this.arg0 = newArg0;
    }

}

