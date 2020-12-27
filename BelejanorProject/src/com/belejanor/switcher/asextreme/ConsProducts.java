package com.belejanor.switcher.asextreme;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ConsProducts implements Serializable {

	private static final long serialVersionUID = -2385731691914914506L;

	public ConsProducts(){
		super();
	}

	private List<Registro> registro;
	
	@XmlElement(name="Reg")
	public List<Registro> getRegistro() {
		return registro;
	}

	public void setRegistro(List<Registro> registro) {
		this.registro = registro;
	}
}
