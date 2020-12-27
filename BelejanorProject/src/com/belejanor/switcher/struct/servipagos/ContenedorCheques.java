package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;

@SuppressWarnings("serial")
public class ContenedorCheques implements Serializable{

	public ContenedorCheques() {
		
		this.cheques = null;
	}
	
	private Cheques cheques;

	@XmlElement(name="cheques")
	public Cheques getCheques() {
		return cheques;
	}

	public void setCheques(Cheques cheques) {
		this.cheques = cheques;
	}
	
}
