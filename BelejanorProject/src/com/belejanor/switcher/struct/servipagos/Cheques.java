package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType(propOrder={"valor", "numero_documentos", "descripcion"})
public class Cheques implements Serializable{

	private String valor;
	private String numero_documentos;
	private String descripcion;
	
	public Cheques(){
		
		this.valor = null;
		this.numero_documentos = null;
		this.descripcion = null;
	}
	
	public Cheques(String valor, String numero, String descripcion){
		
		this.valor = valor;
		this.numero_documentos = numero;
		this.descripcion = descripcion;
	}
	@XmlAttribute(name="Valor")
	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	@XmlAttribute(name="numero_documentos")
	public String getNumero_documentos() {
		return numero_documentos;
	}

	public void setNumero_documentos(String numero_documentos) {
		this.numero_documentos = numero_documentos;
	}
	@XmlAttribute(name="descripcion")
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	
	
}
