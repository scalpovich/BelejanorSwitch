package com.belejanor.switcher.detail;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class GeneralResponseClass {

	private String msgu;
    private String msgp;
    private String stkt;
    private String cod;
    
    public GeneralResponseClass(){
    	
    }
    @XmlElement(name="MSGU")
	public String getMsgu() {
		return msgu;
	}

	public void setMsgu(String msgu) {
		this.msgu = msgu;
	}
	@XmlElement(name="MSGP")
	public String getMsgp() {
		return msgp;
	}

	public void setMsgp(String msgp) {
		this.msgp = msgp;
	}
	@XmlElement(name="STKT")
	public String getStkt() {
		return stkt;
	}

	public void setStkt(String stkt) {
		this.stkt = stkt;
	}
	@XmlAttribute
	public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}
    
    
}
