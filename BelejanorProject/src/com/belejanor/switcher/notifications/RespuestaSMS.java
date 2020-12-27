package com.belejanor.switcher.notifications;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name="respuesta")
public class RespuestaSMS implements Serializable{
	
	private String cod_respuesta;
	private String des_respuesta;
	private String id_transaccion;
	
	public RespuestaSMS() {
		super();
	}
	public String getCod_respuesta() {
		return cod_respuesta;
	}
	public void setCod_respuesta(String cod_respuesta) {
		this.cod_respuesta = cod_respuesta;
	}
	public String getDes_respuesta() {
		return des_respuesta;
	}
	public void setDes_respuesta(String des_respuesta) {
		this.des_respuesta = des_respuesta;
	}
	public String getId_transaccion() {
		return id_transaccion;
	}
	public void setId_transaccion(String id_transaccion) {
		this.id_transaccion = id_transaccion;
	}

}
