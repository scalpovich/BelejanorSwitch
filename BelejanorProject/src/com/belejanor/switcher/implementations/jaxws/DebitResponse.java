
package com.belejanor.switcher.implementations.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class was generated by Apache CXF 3.1.10
 * Fri Mar 16 16:27:46 COT 2018
 * Generated source version: 3.1.10
 */

@XmlRootElement(name = "debitResponse", namespace = "http://implementations.middleware.fitbank.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "debitResponse", namespace = "http://implementations.middleware.fitbank.com")

public class DebitResponse {

    @XmlElement(name = "DTOResponseDebit")
    private com.belejanor.switcher.electroniccashres.DTOResponseDebit _return;

    public com.belejanor.switcher.electroniccashres.DTOResponseDebit getReturn() {
        return this._return;
    }

    public void setReturn(com.belejanor.switcher.electroniccashres.DTOResponseDebit new_return)  {
        this._return = new_return;
    }

}
