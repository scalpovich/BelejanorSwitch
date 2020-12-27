package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType(propOrder={"det_codigo_detalle", "ins_fi_number", "trb_codigo_traban",
        "mot_codigo_motivo", "mon_codigo_moneda", "det_cuenta", "det_referencia"
        ,"det_monto", "det_monto_efe", "det_monto_doc"})

public class CabeceraDetalle implements Serializable{

	private String det_codigo_detalle;
	private String ins_fi_number;
	private String trb_codigo_traban;
	private String mot_codigo_motivo;
	private String mon_codigo_moneda;
	private String det_cuenta;
	private String det_referencia;
	private String det_monto;
	private String det_monto_efe;
	private String det_monto_doc;
	
	
	public CabeceraDetalle(){
		
		this.det_codigo_detalle = null;
		this.ins_fi_number = null;
		this.trb_codigo_traban = null;
		this.mon_codigo_moneda = null;
		this.mot_codigo_motivo = null;
		this.det_cuenta = null;
		this.det_referencia = null;
		this.det_monto = null;
		this.det_monto_efe = null;
		this.det_monto_doc = null;
	}
	
	public String getDet_codigo_detalle() {
		return det_codigo_detalle;
	}
	public void setDet_codigo_detalle(String det_codigo_detalle) {
		this.det_codigo_detalle = det_codigo_detalle;
	}
	public String getIns_fi_number() {
		return ins_fi_number;
	}
	public void setIns_fi_number(String ins_fi_number) {
		this.ins_fi_number = ins_fi_number;
	}
	public String getTrb_codigo_traban() {
		return trb_codigo_traban;
	}
	public void setTrb_codigo_traban(String trb_codigo_traban) {
		this.trb_codigo_traban = trb_codigo_traban;
	}
	public String getMot_codigo_motivo() {
		return mot_codigo_motivo;
	}
	public void setMot_codigo_motivo(String mot_codigo_motivo) {
		this.mot_codigo_motivo = mot_codigo_motivo;
	}
	public String getMon_codigo_moneda() {
		return mon_codigo_moneda;
	}
	public void setMon_codigo_moneda(String mon_codigo_moneda) {
		this.mon_codigo_moneda = mon_codigo_moneda;
	}
	public String getDet_cuenta() {
		return det_cuenta;
	}
	public void setDet_cuenta(String det_cuenta) {
		this.det_cuenta = det_cuenta;
	}
	public String getDet_referencia() {
		return det_referencia;
	}
	public void setDet_referencia(String det_referencia) {
		this.det_referencia = det_referencia;
	}
	public String getDet_monto() {
		return det_monto;
	}
	public void setDet_monto(String det_monto) {
		this.det_monto = det_monto;
	}
	public String getDet_monto_efe() {
		return det_monto_efe;
	}
	public void setDet_monto_efe(String det_monto_efe) {
		this.det_monto_efe = det_monto_efe;
	}
	public String getDet_monto_doc() {
		return det_monto_doc;
	}
	public void setDet_monto_doc(String det_monto_doc) {
		this.det_monto_doc = det_monto_doc;
	}
	
	
}
