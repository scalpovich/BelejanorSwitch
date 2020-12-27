package com.belejanor.switcher.detail;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Joins {

	private List<Dependencies> dep;
	private String alias;
    private String name;
    private String type;
    
	public Joins() {
    }
	@XmlElement(name="DEP")
	public List<Dependencies> getDep() {
		return dep;
	}

	public void setDep(List<Dependencies> dep) {
		this.dep = dep;
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
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
               
}
