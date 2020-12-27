package com.belejanor.switcher.asextreme;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

@SuppressWarnings("serial")
public class MassiveMovements implements Serializable{

	public MassiveMovements() {
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
