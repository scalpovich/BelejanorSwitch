package com.belejanor.switcher.detail;

import javax.xml.bind.annotation.XmlAttribute;

public class Criterions{
	
	private String alias;
    private String cond;
    private String name;
    private String val;
    private String ord;
    private String tipo;
    
    public Criterions(){
    	
    }
    @XmlAttribute
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	@XmlAttribute
	public String getCond() {
		return cond;
	}
	public void setCond(String cond) {
		this.cond = cond;
	}
	@XmlAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	@XmlAttribute
	public String getOrd() {
		return ord;
	}
	public void setOrd(String ord) {
		this.ord = ord;
	}
	@XmlAttribute
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
        
}
