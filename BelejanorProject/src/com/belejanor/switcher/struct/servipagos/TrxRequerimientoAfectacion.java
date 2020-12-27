package com.belejanor.switcher.struct.servipagos;


import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlRootElement(name="RequerimientoAfectacion")
@XmlType(propOrder={"transaccion", "detalleTransaccion", "cheques"})
public class TrxRequerimientoAfectacion implements Serializable{

	private TransaccionAfectacion transaccion;
	private DetalleTransaccion detalleTransaccion;
	private ContenedorCheques cheques;

	public TrxRequerimientoAfectacion(){
		
		this.transaccion = null;
		this.detalleTransaccion = null;
		this.cheques = null;
	}
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
	
	@XmlElement(name="Cheques")
	public ContenedorCheques getCheques() {
		return cheques;
	}
	public void setCheques(ContenedorCheques cheques) {
		this.cheques = cheques;
	}
	
}
