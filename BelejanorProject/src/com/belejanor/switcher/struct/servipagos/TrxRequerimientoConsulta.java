package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType(propOrder={"consulta", "detalleConsulta"})
@XmlRootElement(name ="RequerimientoConsulta")
public class TrxRequerimientoConsulta implements Serializable{

	private TransaccionConsulta consulta;
	private DetalleTransaccion detalleConsulta;
	
	public TrxRequerimientoConsulta(){
		
		this.consulta = null;
		this.detalleConsulta = null;
	}
	@XmlElement(name="Consulta")
	public TransaccionConsulta getConsulta() {
		return consulta;
	}

	public void setConsulta(TransaccionConsulta consulta) {
		this.consulta = consulta;
	}
	@XmlElement(name="DetalleConsulta")
	public DetalleTransaccion getDetalleConsulta() {
		return detalleConsulta;
	}

	public void setDetalleConsulta(DetalleTransaccion detalleConsulta) {
		this.detalleConsulta = detalleConsulta;
	}
	
	
}
