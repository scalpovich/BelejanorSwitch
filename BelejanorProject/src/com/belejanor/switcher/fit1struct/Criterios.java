package com.belejanor.switcher.fit1struct;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"codigo", "valor", "orden","tipo"})
public class Criterios implements Serializable{

	private static final long serialVersionUID = -1197234388961689518L;
	
	private String codigo;
	private String valor;
	private String orden;
	private String tipo;
	
	public Criterios() {
		super();
		this.codigo = null;
		this.valor = null;
		this.orden = null;
		this.tipo = null;
	}
	
	public Criterios(String codigo, String valor, String orden, String tipo){
		this();
		this.codigo = codigo;
		this.valor = valor;
		this.orden = orden;
		this.tipo = tipo;
		
	}
	
	@XmlElement(name="COD")
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	@XmlElement(name="VAL")
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	@XmlElement(name="ORD")
	public String getOrden() {
		return orden;
	}
	public void setOrden(String orden) {
		this.orden = orden;
	}
	@XmlElement(name="TIP")
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	
}
