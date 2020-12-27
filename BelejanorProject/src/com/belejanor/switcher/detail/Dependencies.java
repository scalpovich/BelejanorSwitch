package com.belejanor.switcher.detail;

import javax.xml.bind.annotation.XmlAttribute;

public class Dependencies {

	private String aliasDesde;
    private String aliasHacia;
    private String desde;
    private String hacia;
    private String val;
    
    public Dependencies(){
    	
    }
    @XmlAttribute
	public String getAliasDesde() {
		return aliasDesde;
	}

	public void setAliasDesde(String aliasDesde) {
		this.aliasDesde = aliasDesde;
	}
	 @XmlAttribute
	public String getAliasHacia() {
		return aliasHacia;
	}

	public void setAliasHacia(String aliasHacia) {
		this.aliasHacia = aliasHacia;
	}
	 @XmlAttribute
	public String getDesde() {
		return desde;
	}

	public void setDesde(String desde) {
		this.desde = desde;
	}
	 @XmlAttribute
	public String getHacia() {
		return hacia;
	}

	public void setHacia(String hacia) {
		this.hacia = hacia;
	}
	 @XmlAttribute
	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}           
}
