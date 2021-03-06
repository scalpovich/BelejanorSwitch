package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType(propOrder={"cabeceraDetalle", "registroMetadata", "registro"})
public class DetalleTransaccion implements Serializable{

	private CabeceraDetalle cabeceraDetalle;
	private List<RegistroMetadata> registroMetadata;
	private List<Registro> registro;
	
	public DetalleTransaccion(){
		
		this.cabeceraDetalle = null;
		this.registroMetadata = new ArrayList<>();
		this.registro = new ArrayList<>();
	}
	
	public void addRegistroMetadata(RegistroMetadata reg){
		
		this.registroMetadata.add(reg);
	}
	
	public void addRegistro(Registro reg){
		
		this.registro.add(reg);
	}
	
    public DetalleTransaccion(CabeceraDetalle cabDet, List<RegistroMetadata> regMeta, List<Registro> reg){
		
		this.cabeceraDetalle = cabDet;
		this.registroMetadata = regMeta;
		this.registro = reg;
	}
	
	@XmlElement(name="cabeceraDetalle")
	public CabeceraDetalle getCabeceraDetalle() {
		return cabeceraDetalle;
	}

	public void setCabeceraDetalle(CabeceraDetalle cabeceraDetalle) {
		this.cabeceraDetalle = cabeceraDetalle;
	}
	@XmlElement(name="registroMetadata")
	public List<RegistroMetadata> getRegistroMetadata() {
		return registroMetadata;
	}

	public void setRegistroMetadata(List<RegistroMetadata> registroMetadata) {
		this.registroMetadata = registroMetadata;
	}
	@XmlElement(name="registro")
	public List<Registro> getRegistro() {
		return registro;
	}

	public void setRegistro(List<Registro> registro) {
		this.registro = registro;
	}
	
	
}
