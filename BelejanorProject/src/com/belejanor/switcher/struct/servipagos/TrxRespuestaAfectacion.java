package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlRootElement(name="RespuestaAfectacion")
@XmlType(propOrder={"transaccion", "detalleTransaccion"})
public class TrxRespuestaAfectacion implements Serializable{

	private TransaccionAfectacion transaccion;
	private DetalleTransaccion detalleTransaccion;
	
	@XmlElement(name="Transaccion")
	public TransaccionAfectacion getTransaccion() {
		return transaccion;
	}
	public void setTransaccion(TransaccionAfectacion transaccion) {
		this.transaccion = transaccion;
	}
	@XmlElement(name="DetalleTransaccion")
	public DetalleTransaccion getDetalleTransaccion() {
		return detalleTransaccion;
	}
	public void setDetalleTransaccion(DetalleTransaccion detalleTransaccion) {
		this.detalleTransaccion = detalleTransaccion;
	}
	
	

}
