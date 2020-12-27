package com.belejanor.switcher.bimo.genericerror;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.bimo.genericerror.Reason;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusReasonInformation", propOrder = {
    "rsn"
})
public class StatusReasonInformation implements Serializable{

    @XmlElement(name = "Rsn", required = true)
    protected Reason rsn;

    public Reason getRsn() {
        return rsn;
    }

    public void setRsn(Reason value) {
        this.rsn = value;
    }

}

