package com.belejanor.switcher.struct.equifax;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType(propOrder={"direccion", "tipoDireccion","parroquia","ciudad","fechaModificacion","verficado",
		"telefono","tipoTelefono"})
public class DatosDirecciones implements Serializable{

	public DatosDirecciones() {
		
		this.verficado = false;
	}
	
	private String direccion;
	private String tipoDireccion;
	private String parroquia;
	private String ciudad;
	private Date fechaModificacion;
	private boolean verficado;
	private String telefono;
	private String tipoTelefono;
	
	@XmlElement(name="Direccion")
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	@XmlElement(name="TipoDireccion")
	public String getTipoDireccion() {
		return tipoDireccion;
	}
	public void setTipoDireccion(String tipoDireccion) {
		this.tipoDireccion = tipoDireccion;
	}
	@XmlElement(name="Parroquia")
	public String getParroquia() {
		return parroquia;
	}
	public void setParroquia(String parroquia) {
		this.parroquia = parroquia;
	}
	@XmlElement(name="Ciudad")
	public String getCiudad() {
		return ciudad;
	}
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}
	@XmlElement(name="FechaModificacion")
	public Date getFechaModificacion() {
		return fechaModificacion;
	}
	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}
	@XmlElement(name="Verificado")
	public boolean isVerficado() {
		return verficado;
	}
	public void setVerficado(boolean verficado) {
		this.verficado = verficado;
	}
	@XmlElement(name="Telefono")
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	@XmlElement(name="TipoTelefono")
	public String getTipoTelefono() {
		return tipoTelefono;
	}
	public void setTipoTelefono(String tipoTelefono) {
		this.tipoTelefono = tipoTelefono;
	}
}
