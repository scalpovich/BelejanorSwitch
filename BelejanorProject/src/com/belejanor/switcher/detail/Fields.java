package com.belejanor.switcher.detail;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Fields {

	private List<Dependencies> dep;
	private String oldval;
    private String val;
    private String alias;
    private String name;
    private String pk;
    private String tipo;
    private String functionName;
    
	public Fields() {
		
	}
	@XmlElement(name="DEP")
	public List<Dependencies> getDep() {
		return dep;
	}

	public void setDep(List<Dependencies> dep) {
		this.dep = dep;
	}
	@XmlElement(name="OLDVAL")
	public String getOldval() {
		return oldval;
	}

	public void setOldval(String oldval) {
		this.oldval = oldval;
	}
	@XmlElement(name="VAL")
	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
	@XmlAttribute
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute
	public String getPk() {
		return pk;
	}

	public void setPk(String pk) {
		this.pk = pk;
	}
	@XmlAttribute
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	@XmlAttribute
	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
       
}
