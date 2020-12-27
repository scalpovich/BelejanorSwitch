package com.belejanor.switcher.fit1struct;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Respuesta implements Serializable{


	private static final long serialVersionUID = -4041155226708043763L;
	
	
	private String fechaContable;
	private String nroMensajeRespuesta;
	private String campoSDS;
	private String campoSAT;
	private String campoSBL;
	private String campoSRT;
	private String nombre;
	private String codRespuesta;
	private String desRespuesta;
	private List<String> CampoOdb;
	private List<String> CampoNumero;
	private String campoDSP;
	
	/*private String odb1;
	private String nro1;
	
	private String odb2;
	private String nro2;
	
	private String odb3;
	private String nro3;
	
	private String odb4;
	private String nro4;
	
	private String odb5;
	private String nro5;*/
	
	
	public Respuesta() {
		
		    this.fechaContable= null;
			this.nroMensajeRespuesta= null;
			this.campoSDS= null;
			this.campoSAT= null;
			this.campoSBL= null;
			this.campoSRT= null;
			this.nombre= null;
			this.codRespuesta= null;
			this.desRespuesta= null;
			this.campoDSP = null;
			/*this.odb1= null;
			this.nro1= null;
			this.odb2= null;
			this.nro2= null;
			this.odb3= null;
			this.nro3= null;
			this.odb4= null;
			this.nro4= null;
			this.odb5= null;
			this.nro5= null;*/
	}
	
	
	@XmlElement(name="FCN")
	public String getFechaContable() {
		return fechaContable;
	}
	public void setFechaContable(String fechaContable) {
		this.fechaContable = fechaContable;
	}
	@XmlElement(name="MSG")
	public String getNroMensajeRespuesta() {
		return nroMensajeRespuesta;
	}
	public void setNroMensajeRespuesta(String nroMensajeRespuesta) {
		this.nroMensajeRespuesta = nroMensajeRespuesta;
	}
	@XmlElement(name="SDS")
	public String getCampoSDS() {
		return campoSDS;
	}
	public void setCampoSDS(String campoSDS) {
		this.campoSDS = campoSDS;
	}
	@XmlElement(name="SAT")
	public String getCampoSAT() {
		return campoSAT;
	}
	public void setCampoSAT(String campoSAT) {
		this.campoSAT = campoSAT;
	}
	@XmlElement(name="SBL")
	public String getCampoSBL() {
		return campoSBL;
	}
	public void setCampoSBL(String campoSBL) {
		this.campoSBL = campoSBL;
	}
	@XmlElement(name="SRT")
	public String getCampoSRT() {
		return campoSRT;
	}
	public void setCampoSRT(String campoSRT) {
		this.campoSRT = campoSRT;
	}
	@XmlElement(name="NOM")
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	@XmlElement(name="COD")
	public String getCodRespuesta() {
		return codRespuesta;
	}
	public void setCodRespuesta(String codRespuesta) {
		this.codRespuesta = codRespuesta;
	}
	@XmlElement(name="DSC")
	public String getDesRespuesta() {
		return desRespuesta;
	}
	public void setDesRespuesta(String desRespuesta) {
		this.desRespuesta = desRespuesta;
	}

	@XmlElement(name="ODB")
	public List<String> getCampoOdb() {
		return CampoOdb;
	}
	public void setCampoOdb(List<String> campoOdb) {
		CampoOdb = campoOdb;
	}

	@XmlElement(name="NRO")
	public List<String> getCampoNumero() {
		return CampoNumero;
	}

	
	public void setCampoNumero(List<String> campoNumero) {
		CampoNumero = campoNumero;
	}

	@XmlElement(name="DSP")
	public String getCampoDSP() {
		return campoDSP;
	}


	public void setCampoDSP(String campoDSP) {
		this.campoDSP = campoDSP;
	}
	
	
	
	/*@XmlElement(name="ODB")
	public String getOdb1() {
		return odb1;
	}
	public void setOdb1(String odb1) {
		this.odb1 = odb1;
	}
	@XmlElement(name="NRO")
	public String getNro1() {
		return nro1;
	}
	public void setNro1(String nro1) {
		this.nro1 = nro1;
	}
	@XmlElement(name="ODB")
	public String getOdb2() {
		return odb2;
	}
	public void setOdb2(String odb2) {
		this.odb2 = odb2;
	}
	@XmlElement(name="NRO")
	public String getNro2() {
		return nro2;
	}
	public void setNro2(String nro2) {
		this.nro2 = nro2;
	}
	@XmlElement(name="ODB")
	public String getOdb3() {
		return odb3;
	}
	public void setOdb3(String odb3) {
		this.odb3 = odb3;
	}
	@XmlElement(name="NRO")
	public String getNro3() {
		return nro3;
	}
	public void setNro3(String nro3) {
		this.nro3 = nro3;
	}
	@XmlElement(name="ODB")
	public String getOdb4() {
		return odb4;
	}
	public void setOdb4(String odb4) {
		this.odb4 = odb4;
	}
	@XmlElement(name="NRO")
	public String getNro4() {
		return nro4;
	}
	public void setNro4(String nro4) {
		this.nro4 = nro4;
	}
	@XmlElement(name="ODB")
	public String getOdb5() {
		return odb5;
	}
	public void setOdb5(String odb5) {
		this.odb5 = odb5;
	}
	@XmlElement(name="NRO")
	public String getNro5() {
		return nro5;
	}
	public void setNro5(String nro5) {
		this.nro5 = nro5;
	}*/
	
	
	
}
