package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlRootElement(name="RespuestaConsulta")
@XmlType(propOrder={"consulta", "detalleTransaccion"})
public class TrxRespuestaConsulta implements Serializable{

	private TransaccionConsulta consulta;
	private DetalleTransaccion detalleTransaccion;
	
	@XmlElement(name="Consulta")
	public TransaccionConsulta getConsulta() {
		return consulta;
	}
	public void setConsulta(TransaccionConsulta consulta) {
		this.consulta = consulta;
	}
	@XmlElement(name="DetalleConsulta")
	public DetalleTransaccion getDetalleTransaccion() {
		return detalleTransaccion;
	}
	public void setDetalleTransaccion(DetalleTransaccion detalleTransaccion) {
		this.detalleTransaccion = detalleTransaccion;
	}
	
	
	
}
