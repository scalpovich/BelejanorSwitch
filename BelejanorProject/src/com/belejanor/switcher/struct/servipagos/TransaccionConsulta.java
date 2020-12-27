package com.belejanor.switcher.struct.servipagos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlType(propOrder={"con_codigo_consulta", "con_serial_consulta", "con_fecha_proceso", "con_hora",
		            "age_codigo_agencia", "usu_codigo_usuario", "con_fecha_contable"
		            ,"con_tipo_consulta", "cad_codigo_canal", "con_pin_trama", "ins_fi_number_origen"
		            ,"con_message_type_code", "con_error_consulta"})
public class TransaccionConsulta implements Serializable{

	private String con_codigo_consulta;
	private String con_serial_consulta;
	private String con_fecha_proceso;
	private String con_hora;
	private String age_codigo_agencia;
	private String usu_codigo_usuario;
	private String con_fecha_contable;
	private String con_tipo_consulta;
	private String cad_codigo_canal;
	private String con_pin_trama;
	private String ins_fi_number_origen;
	private String con_message_type_code;
	private String con_error_consulta;
	
	public TransaccionConsulta(){
		
		con_codigo_consulta = null;
		con_serial_consulta = null;
		con_fecha_proceso = null;
		age_codigo_agencia = null;
		usu_codigo_usuario = null;
		con_fecha_contable = null;
		con_tipo_consulta = null;
		cad_codigo_canal = null;
		con_pin_trama = null;
		ins_fi_number_origen = null;
		con_message_type_code = null;
		con_error_consulta = null;
		con_hora = null;
	}

	public String getCon_codigo_consulta() {
		return con_codigo_consulta;
	}

	public void setCon_codigo_consulta(String con_codigo_consulta) {
		this.con_codigo_consulta = con_codigo_consulta;
	}

	public String getCon_serial_consulta() {
		return con_serial_consulta;
	}

	public void setCon_serial_consulta(String con_serial_consulta) {
		this.con_serial_consulta = con_serial_consulta;
	}

	public String getCon_fecha_proceso() {
		return con_fecha_proceso;
	}

	public void setCon_fecha_proceso(String con_fecha_proceso) {
		this.con_fecha_proceso = con_fecha_proceso;
	}
	
	public String getCon_hora() {
		return con_hora;
	}

	public void setCon_hora(String con_hora) {
		this.con_hora = con_hora;
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

	public String getCon_fecha_contable() {
		return con_fecha_contable;
	}

	public void setCon_fecha_contable(String con_fecha_contable) {
		this.con_fecha_contable = con_fecha_contable;
	}

	public String getCon_tipo_consulta() {
		return con_tipo_consulta;
	}

	public void setCon_tipo_consulta(String con_tipo_consulta) {
		this.con_tipo_consulta = con_tipo_consulta;
	}

	public String getCad_codigo_canal() {
		return cad_codigo_canal;
	}

	public void setCad_codigo_canal(String cad_codigo_canal) {
		this.cad_codigo_canal = cad_codigo_canal;
	}

	public String getCon_pin_trama() {
		return con_pin_trama;
	}

	public void setCon_pin_trama(String con_pin_trama) {
		this.con_pin_trama = con_pin_trama;
	}

	public String getIns_fi_number_origen() {
		return ins_fi_number_origen;
	}

	public void setIns_fi_number_origen(String ins_fi_number_origen) {
		this.ins_fi_number_origen = ins_fi_number_origen;
	}

	public String getCon_message_type_code() {
		return con_message_type_code;
	}

	public void setCon_message_type_code(String con_message_type_code) {
		this.con_message_type_code = con_message_type_code;
	}

	public String getCon_error_consulta() {
		return con_error_consulta;
	}

	public void setCon_error_consulta(String con_error_consulta) {
		this.con_error_consulta = con_error_consulta;
	}
	
	
}
