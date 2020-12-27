package com.belejanor.switcher.asextreme;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;


public class FxTables implements Serializable{

	private static final long serialVersionUID = 7905471217455011837L;
	private Clients clientes;
	private Products productos;
	private ConsProducts consolidadoProductos;
	private Cuotas cuotas;
	private Payments pagos;
	private LastMovements movimientos;
	private MassiveMovements transferencias;
	
	public FxTables() {
		
		this.clientes = null;
		this.productos = null;
		this.consolidadoProductos = null;
		this.cuotas = null;
		this.pagos = null;
		this.movimientos = null;
		this.transferencias = null;
	}
	
	
	@XmlElement(name="Cuotas")
	public Cuotas getCuotas() {
		return cuotas;
	}

	public void setCuotas(Cuotas cuotas) {
		this.cuotas = cuotas;
	}

	@XmlElement(name="Clients")
	public Clients getClientes() {
		return clientes;
	}

	public void setClientes(Clients clientes) {
		this.clientes = clientes;
	}
	@XmlElement(name="Products")
	public Products getProductos() {
		return productos;
	}

	public void setProductos(Products productos) {
		this.productos = productos;
	}
	@XmlElement(name="ConsProducts")
	public ConsProducts getConsolidadoProductos() {
		return consolidadoProductos;
	}

	public void setConsolidadoProductos(ConsProducts consolidadoProductos) {
		this.consolidadoProductos = consolidadoProductos;
	}

	@XmlElement(name="Payments")
	public Payments getPagos() {
		return pagos;
	}

	public void setPagos(Payments pagos) {
		this.pagos = pagos;
	}

	@XmlElement(name="LastMovements")
	public LastMovements getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(LastMovements movimientos) {
		this.movimientos = movimientos;
	}

	@XmlElement(name="MassiveMovements")
	public MassiveMovements getTransferencias() {
		return transferencias;
	}
	public void setTransferencias(MassiveMovements transferencias) {
		this.transferencias = transferencias;
	}
	
	
}
