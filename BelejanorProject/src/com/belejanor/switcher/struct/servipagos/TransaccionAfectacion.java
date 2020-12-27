package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType(propOrder={"tra_codigo_transaccion", "tra_serial_transaccion", "tra_fecha_proceso",
		            "tra_hora", "age_codigo_agencia", "usu_codigo_usuario", "tra_fecha_contable"
		            ,"tra_tipo_transaccion", "tra_cat_motivo_reverso", "tra_monto_total", "cad_codigo_canal"
		            ,"tra_pin_trama", "ins_fi_number_origen", "tra_message_type_code","tra_error_transaccion"})
public class TransaccionAfectacion implements Serializable{

	private String tra_codigo_transaccion;
	private String tra_serial_transaccion;
	private String tra_fecha_proceso;
	private String tra_hora;
	private String age_codigo_agencia;
	private String usu_codigo_usuario;
	private String tra_fecha_contable;
	private String tra_tipo_transaccion;
	private String tra_cat_motivo_reverso;
	private String tra_monto_total;
	private String cad_codigo_canal;
	private String tra_pin_trama;
	private String ins_fi_number_origen;
	private String tra_message_type_code;
	private String tra_error_transaccion;
	
	public TransaccionAfectacion(){
		
		this.tra_codigo_transaccion = null;
		this.tra_serial_transaccion = null;
		this.tra_fecha_proceso= null;
		this.tra_hora= null;
		this.age_codigo_agencia = null;
		this.usu_codigo_usuario = null;
		this.tra_fecha_contable = null;
		this.tra_tipo_transaccion = null;
		this.tra_cat_motivo_reverso = null;
		this.tra_monto_total = null;
		this.cad_codigo_canal = null;
		this.tra_pin_trama = null;
		this.ins_fi_number_origen = null;
		this.tra_message_type_code = null;
	}

	public String getTra_codigo_transaccion() {
		return tra_codigo_transaccion;
	}

	public void setTra_codigo_transaccion(String tra_codigo_transaccion) {
		this.tra_codigo_transaccion = tra_codigo_transaccion;
	}

	public String getTra_serial_transaccion() {
		return tra_serial_transaccion;
	}

	public void setTra_serial_transaccion(String tra_serial_transaccion) {
		this.tra_serial_transaccion = tra_serial_transaccion;
	}

	public String getTra_fecha_proceso() {
		return tra_fecha_proceso;
	}

	public void setTra_fecha_proceso(String tra_fecha_proceso) {
		this.tra_fecha_proceso = tra_fecha_proceso;
	}

	public String getTra_hora() {
		return tra_hora;
	}

	public void setTra_hora(String tra_hora) {
		this.tra_hora = tra_hora;
	}

	public String getAge_codigo_agencia() {
		return age_codigo_agencia;
	}

	public void setAge_codigo_agencia(String age_codigo_agencia) {
		this.age_codigo_agencia = age_codigo_agencia;
	}

	public String getUsu_codigo_usuario() {
		return usu_codigo_usuario;
	}

	public void setUsu_codigo_usuario(String usu_codigo_usuario) {
		this.usu_codigo_usuario = usu_codigo_usuario;
	}

	public String getTra_fecha_contable() {
		return tra_fecha_contable;
	}

	public void setTra_fecha_contable(String tra_fecha_contable) {
		this.tra_fecha_contable = tra_fecha_contable;
	}

	public String getTra_tipo_transaccion() {
		return tra_tipo_transaccion;
	}

	public void setTra_tipo_transaccion(String tra_tipo_transaccion) {
		this.tra_tipo_transaccion = tra_tipo_transaccion;
	}

	public String getTra_cat_motivo_reverso() {
		return tra_cat_motivo_reverso;
	}

	public void setTra_cat_motivo_reverso(String tra_cat_motivo_reverso) {
		this.tra_cat_motivo_reverso = tra_cat_motivo_reverso;
	}

	public String getTra_monto_total() {
		return tra_monto_total;
	}

	public void setTra_monto_total(String tra_monto_total) {
		this.tra_monto_total = tra_monto_total;
	}

	public String getCad_codigo_canal() {
		return cad_codigo_canal;
	}

	public void setCad_codigo_canal(String cad_codigo_canal) {
		this.cad_codigo_canal = cad_codigo_canal;
	}

	public String getTra_pin_trama() {
		return tra_pin_trama;
	}

	public void setTra_pin_trama(String tra_pin_trama) {
		this.tra_pin_trama = tra_pin_trama;
	}

	public String getIns_fi_number_origen() {
		return ins_fi_number_origen;
	}

	public void setIns_fi_number_origen(String ins_fi_number_origen) {
		this.ins_fi_number_origen = ins_fi_number_origen;
	}

	public String getTra_message_type_code() {
		return tra_message_type_code;
	}

	public void setTra_message_type_code(String tra_message_type_code) {
		this.tra_message_type_code = tra_message_type_code;
	}

	public String getTra_error_transaccion() {
		return tra_error_transaccion;
	}

	public void setTra_error_transaccion(String tra_error_transaccion) {
		this.tra_error_transaccion = tra_error_transaccion;
	}
	
}
