package com.belejanor.switcher.fit1struct;

import java.io.Serializable;

public class Dependencias extends Campos implements Serializable{

	private static final long serialVersionUID = -6215090077003247254L;
	
	public Dependencias(){
		super();
	}
	public Dependencias(String nombre, String valor, String nuevoValor, String insercion){
		super(nombre, valor, nuevoValor, insercion, null);
	}
	
}
