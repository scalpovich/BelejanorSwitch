package com.belejanor.switcher.bimo.genericerror;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Reason", propOrder = {
    "code",
    "addtlInf"
})
public class Reason implements Serializable{

    @XmlElement(name = "Code", required = true)
    protected String code;
    @XmlElement(name = "AddtlInf")
    protected List<String> addtlInf;
    
    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        this.code = value;
    }
    
    public List<String> getAddtlInf() {
        if (addtlInf == null) {
            addtlInf = new ArrayList<String>();
        }
        return this.addtlInf;
    }

	public void setAddtlInf(List<String> addtlInf) {
		this.addtlInf = addtlInf;
	}

    
}
