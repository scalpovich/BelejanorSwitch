package com.belejanor.switcher.fit1struct;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"nombre", "valor", "nuevoValor","insercion","modificable", "dependiencias"})
public class Campos implements Serializable{

	private static final long serialVersionUID = -3816514496428176045L;
	
	private String nombre;
	private String valor;
	private String nuevoValor;
	private String insercion;
	private String modificable;
	private List<Dependencias> dependiencias;
	
	public Campos() {
		
		super();
		this.nombre = null;
		this.valor = null;
		this.nuevoValor = null;
		this.insercion = null;
		this.modificable = null;
		this.dependiencias = null;
	}
	
	public Campos(String nombre, String valor, String nuevoValor, String insercion, String modificable){
		
		this();
		this.nombre = nombre;
		this.valor = valor;
		this.nuevoValor = nuevoValor;
		this.insercion = insercion;
		this.modificable = modificable;
	}
	public Campos(String nombre, String valor, String nuevoValor, String insercion, String modificable, List<Dependencias> dep){
		
		this();
		this.nombre = nombre;
		this.valor = valor;
		this.nuevoValor = nuevoValor;
		this.insercion = insercion;
		this.modificable = modificable;
		this.dependiencias = dep;
	}
	
	@XmlElement(name="NOM")
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	@XmlElement(name="VAL")
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	@XmlElement(name="NVL")
	public String getNuevoValor() {
		return nuevoValor;
	}
	public void setNuevoValor(String nuevoValor) {
		this.nuevoValor = nuevoValor;
	}
	@XmlElement(name="INS")
	public String getInsercion() {
		return insercion;
	}
	public void setInsercion(String insercion) {
		this.insercion = insercion;
	}
	@XmlElement(name="MOD")
	public String getModificable() {
		return modificable;
	}
	public void setModificable(String modificable) {
		this.modificable = modificable;
	}
	@XmlElement(name="DEP")
	public List<Dependencias> getDependiencias() {
		return dependiencias;
	}

	public void setDependiencias(List<Dependencias> dependiencias) {
		this.dependiencias = dependiencias;
	}
	
	
	
}
