package com.belejanor.switcher.parser;

import java.util.List;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.struct.servipagos.Campo;
import com.belejanor.switcher.struct.servipagos.CampoDef;
import com.belejanor.switcher.struct.servipagos.TrxRequerimientoAfectacion;
import com.belejanor.switcher.struct.servipagos.TrxRequerimientoConsulta;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

public class ServipagosParser {
	
	private Logger log;
	private String codError;
	private String desError;
	private TrxRequerimientoAfectacion trxReqAfec; 
	private TrxRequerimientoConsulta trxReqCon;

	public ServipagosParser() {
		
		log = new Logger();
		this.codError = "000";
		this.desError = "TRANSACCION PARSEADA";
	}
	
	public String getCodError() {
		return codError;
	}

	public String getDesError() {
		return desError;
	}
	
	public TrxRequerimientoAfectacion getTrxReqAfec() {
		return trxReqAfec;
	}

	public void setTrxReqAfec(TrxRequerimientoAfectacion trxReqAfec) {
		this.trxReqAfec = trxReqAfec;
	}

	public TrxRequerimientoConsulta getTrxReqCon() {
		return trxReqCon;
	}

	public void setTrxReqCon(TrxRequerimientoConsulta trxReqCon) {
		this.trxReqCon = trxReqCon;
	}

	public Iso8583 parserSeripagos(String TramaXML) {
		
		Iso8583 iso = null;
		TrxRequerimientoAfectacion trx = null;
		TrxRequerimientoConsulta trxCon = null;
		boolean flagMensaje = false;
		try {
			
			iso = new Iso8583();
			iso.setISO_018_MerchantType("0007");
			iso.setISO_024_NetworkId("555588");
			
			if(TramaXML.contains("<RequerimientoAfectacion>")) {
			
				flagMensaje = true;
					trx	= (TrxRequerimientoAfectacion) SerializationObject
										.StringToObject(TramaXML, TrxRequerimientoAfectacion.class);
				if(trx != null) {
					
					if(trx.getTransaccion() != null) {
						/*---------------SECCION  TRANSACCION---------------------------------------------*/
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getTra_serial_transaccion())) {
							iso.setISO_011_SysAuditNumber(trx.getTransaccion().getTra_serial_transaccion());
						}
						else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR SERIAL DE LA TRANSACCION";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getTra_codigo_transaccion())) {
							iso.setISO_023_CardSeq(trx.getTransaccion().getTra_codigo_transaccion());
						}
						else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR CODIGO UNICO DE LA TRANSACCION";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getTra_fecha_proceso()) 
								       && !StringUtils.IsNullOrEmpty(trx.getTransaccion().getTra_hora())) {
							
						
							iso.setISO_012_LocalDatetime(FormatUtils.StringToDate(trx.getTransaccion().getTra_fecha_proceso() 
								                      + " " + trx.getTransaccion().getTra_hora(),"yyyy-MM-dd HH:mm:ss"));
						}else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR FECHA Y/O HORA TRANSACCION";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getAge_codigo_agencia())) {
							iso.setISO_041_CardAcceptorID(trx.getTransaccion().getAge_codigo_agencia());
						}
						else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR CODIGO AGENCIA SERVIPAGOS";
							return null;
						}
							
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getUsu_codigo_usuario())) {
							iso.setISO_042_Card_Acc_ID_Code(trx.getTransaccion().getUsu_codigo_usuario());
						}
						else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR CODIGO USUARIO SERVIPAGOS";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getTra_fecha_contable())) {
							
							iso.setISO_015_SettlementDatel(FormatUtils.StringToDate(trx.getTransaccion()
								                       .getTra_fecha_contable(), "yyyy-MM-dd"));
						}else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR FECHA CONTABLE DE LA TRANSACCION";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getTra_tipo_transaccion())) {
							
							switch (trx.getTransaccion().getTra_tipo_transaccion()) {
							case "0001":
									iso.setISO_003_ProcessingCode("231010");
								break;
							case "0115":
								iso.setISO_003_ProcessingCode("271010");
							break;
							case "0004":
								iso.setISO_003_ProcessingCode("011000");
							break;
							default:
								this.codError = "999";
								this.desError = "CODIGO TIPO DE TRANSACCION NO RECONOCIDO O SOPORTADO";
								return null;
							}
							
						}else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR EL CODIGO DE TRANSACCION";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getTra_cat_motivo_reverso())) {
							iso.setISO_090_OriginalData(trx.getTransaccion().getTra_cat_motivo_reverso());
						}
						
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getTra_monto_total())) {
							iso.setISO_004_AmountTransaction(Double.parseDouble(trx.getTransaccion().getTra_monto_total()));
							if(iso.getISO_004_AmountTransaction() < 0) {
								
								this.codError = "999";
								this.desError = "EL MONTO DE LA TRANSACCION DEBE SER MAYOR A 0";
								return null;
							}
						}
						else {
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR EL MONTO TOTAL DE LA TRANSACCION";
							return null;
						}
							
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getCad_codigo_canal()))
							iso.setISO_043_CardAcceptorLoc(trx.getTransaccion().getCad_codigo_canal());
						else {
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR EL CODIGO DEL CANAL DE LA TRANSACCION";
							return null;
						}
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getTra_pin_trama())) {
							iso.setISO_052_PinBlock(trx.getTransaccion().getTra_pin_trama());
						}
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getIns_fi_number_origen())) 
							iso.setISO_032_ACQInsID(trx.getTransaccion().getIns_fi_number_origen());
						else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR EL CODIGO DE LA INSTITUCION ORIGEN DE LA TRANSACCION";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trx.getTransaccion().getTra_message_type_code())) {
							switch (trx.getTransaccion().getTra_message_type_code().toUpperCase()) {
							case "TR":
								iso.setISO_000_Message_Type("1200");
								break;
							case "FR":
							case "XR":
								iso.setISO_000_Message_Type("1400");
								break;
							default:
								
								this.codError = "999";
								this.desError = "TIPO DE MENSAJE NO RECONOCIDO";
								return null;
							}
						}else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR EL TIPO DE MENSAJE DE LA TRANSACCION";
							return null;
						}
					}else {
						
						this.codError = "999";
						this.desError = "NO SE HA PODIDO RECUPERAR LA SECCION TRANSACCION";
						return null;
					}
					/*---------------SECCION  DETALLE TRANSACCION---------------------------------------------*/
					
					/*cabeceraDetalle*/
					if(trx.getDetalleTransaccion() != null) {
						
						if(trx.getDetalleTransaccion().getCabeceraDetalle() != null) {
							
							if(!StringUtils.IsNullOrEmpty(trx.getDetalleTransaccion().getCabeceraDetalle()
									              .getDet_codigo_detalle())) {
								
								iso.setISO_022_PosEntryMode(trx.getDetalleTransaccion().getCabeceraDetalle()
							              .getDet_codigo_detalle());
								
							}else {
								
								this.codError = "999";
								this.desError = "NO SE HA PODIDO RECUPERAR EL CAMPO CODIGO DETALLE TRANSACCION (CABECERA DETALLE)";
								return null;
							}
							
							if(!StringUtils.IsNullOrEmpty(trx.getDetalleTransaccion().getCabeceraDetalle()
						              .getIns_fi_number())) {
								
								  iso.setISO_033_FWDInsID(trx.getDetalleTransaccion().getCabeceraDetalle()
						              .getIns_fi_number());
								  
								  if(!iso.getISO_033_FWDInsID().equals(MemoryGlobal.ServipagosCodeEfi)) {
									  
									  this.codError = "999";
										this.desError = "CAMPO CODIGO IFI DESTINO NO COINCIDE POR EL ASIGNADO EN SERVIPAGOS (CABECERA DETALLE)";
										return null;
								  }
								
							}else {
							
								this.codError = "999";
								this.desError = "NO SE HA PODIDO RECUPERAR EL CAMPO CODIGO IFI DESTINO (CABECERA DETALLE)";
								return null;
							}
							
							if(!StringUtils.IsNullOrEmpty(trx.getDetalleTransaccion().getCabeceraDetalle()
						              .getTrb_codigo_traban())) {
								
								iso.setISO_036_Track3(trx.getDetalleTransaccion().getCabeceraDetalle()
						              .getTrb_codigo_traban());
								
							}else {
							
								this.codError = "999";
								this.desError = "NO SE HA PODIDO RECUPERAR EL CAMPO CODIGO TRABAN (CABECERA DETALLE)";
								return null;
							}
							
							if(!StringUtils.IsNullOrEmpty(trx.getDetalleTransaccion().getCabeceraDetalle()
						              .getMot_codigo_motivo())) {
								
								iso.setISO_055_EMV(trx.getDetalleTransaccion().getCabeceraDetalle()
						              .getMot_codigo_motivo());
								
							}else {
							
								this.codError = "999";
								this.desError = "NO SE HA PODIDO RECUPERAR EL CAMPO CODIGO MOTIVO TRANSACCION (CABECERA DETALLE)";
								return null;
							}
							
							if(!StringUtils.IsNullOrEmpty(trx.getDetalleTransaccion().getCabeceraDetalle()
						              .getMon_codigo_moneda())) {
								
								iso.setISO_051_CardCurrCode(840);
								
							}else {
							
								this.codError = "999";
								this.desError = "NO SE HA PODIDO RECUPERAR EL CAMPO CODIGO MONEDA TRANSACCION (CABECERA DETALLE)";
								return null;
							}
							
							if(!StringUtils.IsNullOrEmpty(trx.getDetalleTransaccion().getCabeceraDetalle()
						              .getDet_cuenta())) {
								
								iso.setISO_102_AccountID_1(trx.getDetalleTransaccion().getCabeceraDetalle()
						              .getDet_cuenta());
								
							}else {
							
								this.codError = "999";
								this.desError = "NO SE HA PODIDO RECUPERAR LA CUENTA DE LA TRANSACCION (CABECERA DETALLE)";
								return null;
							}
							
							if(!StringUtils.IsNullOrEmpty(trx.getDetalleTransaccion().getCabeceraDetalle()
						              .getDet_referencia())) {
								
								iso.setISO_037_RetrievalReferenceNumber(trx.getDetalleTransaccion().getCabeceraDetalle()
							              .getDet_referencia());
								
							}else {
							
								this.codError = "999";
								this.desError = "NO SE HA PODIDO RECUPERAR LA CUENTA DE LA TRANSACCION (CABECERA DETALLE)";
								return null;
							}
							
							if(StringUtils.IsNullOrEmpty(trx.getDetalleTransaccion().getCabeceraDetalle()
										.getDet_monto())) {
								
								this.codError = "999";
								this.desError = "NO SE HA PODIDO RECUPERAR EL MONTO TOTAL DE LA TRANSACCION (CABECERA DETALLE)";
								return null;
							}
							
							if(!StringUtils.IsNullOrEmpty(trx.getDetalleTransaccion().getCabeceraDetalle()
									.getDet_monto_efe())) {
							
								iso.setISO_006_BillAmount(Double.parseDouble(trx.getDetalleTransaccion().getCabeceraDetalle()
									.getDet_monto_efe()));
								
							}/*else {
								
								this.codError = "999";
								this.desError = "NO SE HA PODIDO RECUPERAR EL MONTO EN EFECTIVO DE LA TRANSACCION (CABECERA DETALLE)";
								return null;
							}*/
							
							if(!StringUtils.IsNullOrEmpty(trx.getDetalleTransaccion().getCabeceraDetalle()
									.getDet_monto_doc())) {
							
								iso.setISO_008_BillFeeAmount(Double.parseDouble(trx.getDetalleTransaccion().getCabeceraDetalle()
										.getDet_monto_doc()));
								
							}/*else {
								
								this.codError = "100";
								this.desError = "NO SE HA PODIDO RECUPERAR EL MONTO EN DOCUMENTOS DE LA TRANSACCION (CABECERA DETALLE)";
								return null;
							}*/
							
						}else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR LA SECCION DETALLE TRANSACCION/CABECERA DETALLE";
							return null;
						}
						
						/*------------------ REGISTRO METADATA -------------------------- */
						
						if(trx.getDetalleTransaccion().getRegistro() != null && trx.getDetalleTransaccion().getRegistro().size() >= 1) {
							
							if(trx.getDetalleTransaccion().getRegistro().get(0).getCampo() != null && 
									    trx.getDetalleTransaccion().getRegistro().get(0).getCampo().size() >= 1) {
								
								switch (iso.getISO_003_ProcessingCode()) {
								case "011000":
									
									iso.setISO_034_PANExt(getRegistro(trx.getDetalleTransaccion().getRegistro().get(0).getCampo(), "3"));
									iso.setISO_002_PAN(getRegistro(trx.getDetalleTransaccion().getRegistro().get(0).getCampo(), "5"));
									iso.setISO_035_Track2(getRegistro(trx.getDetalleTransaccion().getRegistro().get(0).getCampo(), "4"));
									
									break;
									
								case "231010":
								case "271010":
									
									iso.setISO_034_PANExt(getRegistro(trx.getDetalleTransaccion().getRegistro().get(0).getCampo(), "2"));
									iso.setISO_002_PAN(getRegistro(trx.getDetalleTransaccion().getRegistro().get(0).getCampo(), "4"));
									iso.setISO_035_Track2(getRegistro(trx.getDetalleTransaccion().getRegistro().get(0).getCampo(), "3"));
									
									break;
								default:
									break;
								}
								
								String etiquetas = StringUtils.Empty();
								for (int i = 1; i <= trx.getDetalleTransaccion().getRegistroMetadata()
										   .get(0).getCampoDef().size(); i++) {
									
									etiquetas += getRegistroMetadata(trx.getDetalleTransaccion().getRegistroMetadata()
										   .get(0).getCampoDef(), String.valueOf(i)) + "|";
								}
								iso.setISO_120_ExtendedData(StringUtils.trimEnd(etiquetas, "|"));
								String valores = StringUtils.Empty();
								for (int j = 1; j <= trx.getDetalleTransaccion().getRegistro().get(0).getCampo().size(); j++) {
									
									valores += getRegistro(trx.getDetalleTransaccion().getRegistro().get(0).getCampo()
											               , String.valueOf(j)) + "|";
								}
								iso.setISO_121_ExtendedData(StringUtils.trimEnd(valores, "|"));
								this.trxReqAfec = trx;
								
							}else {
								
								this.codError = "999";
								this.desError = "NO SE HA PODIDO RECUPERAR LA ESTRUCTURA REGISTRO TIPO CABECERA";
								return null;
							}
							
						}else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR LA ESTRUCTURA REGISTRO TIPO CABECERA";
							return null;
						}
						
					}else {
						
						this.codError = "999";
						this.desError = "NO SE HA PODIDO RECUPERAR LA SECCION DETALLE TRANSACCION";
						return null;
					}
					
				}else {
					
					flagMensaje = false;
					this.codError = "999";
					this.desError = "ERROR AL SERIALIZAR TRAMA SERVIPAGOS";
				}
			}
			
			if(TramaXML.contains("<RequerimientoConsulta>")){
				
				flagMensaje = true;
				trxCon = (TrxRequerimientoConsulta) 
						SerializationObject.StringToObject(TramaXML, TrxRequerimientoConsulta.class);
				
				if(trxCon != null) {
					
					if(trxCon.getConsulta() != null) {
						
						if(!StringUtils.IsNullOrEmpty(trxCon.getConsulta().getCon_codigo_consulta())) {
							
							iso.setISO_011_SysAuditNumber(trxCon.getConsulta().getCon_codigo_consulta());
							
						}else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR CODIGO CONSULTA DE LA TRANSACCION";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trxCon.getConsulta().getCon_serial_consulta())) {
							
							iso.setISO_023_CardSeq(trxCon.getConsulta().getCon_serial_consulta());
						}else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR CODIGO SERIAL DE LA CONSULTA DE LA TRANSACCION";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trxCon.getConsulta().getCon_fecha_proceso()) 
								&& !StringUtils.IsNullOrEmpty(trxCon.getConsulta().getCon_hora())){
							
							
							iso.setISO_012_LocalDatetime(FormatUtils.StringToDate(trxCon.getConsulta().getCon_fecha_proceso() 
				                      + " " + trxCon.getConsulta().getCon_hora(),"yyyy-MM-dd HH:mm:ss"));
							
						}else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR FECHA Y HORA PROCESO";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trxCon.getConsulta().getAge_codigo_agencia())) {
							iso.setISO_041_CardAcceptorID(trxCon.getConsulta().getAge_codigo_agencia());
						}
						else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR CODIGO AGENCIA SERVIPAGOS";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trxCon.getConsulta().getUsu_codigo_usuario())) {
							iso.setISO_042_Card_Acc_ID_Code(trxCon.getConsulta().getUsu_codigo_usuario());
						}
						else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR CODIGO USUARIO SERVIPAGOS";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trxCon.getConsulta().getCon_fecha_contable())) {
							
							iso.setISO_015_SettlementDatel(FormatUtils.StringToDate(trxCon.getConsulta()
									.getCon_fecha_contable(), "yyyy-MM-dd"));
						}else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR FECHA CONTABLE DE LA TRANSACCION";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trxCon.getConsulta().getCon_tipo_consulta())) {
							
							switch (trxCon.getConsulta().getCon_tipo_consulta()) {
							case "0021":
									iso.setISO_003_ProcessingCode("323011");
								break;
							default:
								this.codError = "999";
								this.desError = "CODIGO TIPO DE TRANSACCION NO RECONOCIDO O SOPORTADO";
								return null;
							}
							
						}else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR EL CODIGO CONSULTA DE TRANSACCION";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trxCon.getConsulta().getCad_codigo_canal()))
							iso.setISO_043_CardAcceptorLoc(trxCon.getConsulta().getCad_codigo_canal());
						else {
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR EL CODIGO DEL CANAL DE LA TRANSACCION";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trxCon.getConsulta().getCon_pin_trama())) {
							iso.setISO_052_PinBlock(trxCon.getConsulta().getCon_pin_trama());
						}
						if(!StringUtils.IsNullOrEmpty(trxCon.getConsulta().getIns_fi_number_origen())) 
							iso.setISO_032_ACQInsID(trxCon.getConsulta().getIns_fi_number_origen());
						else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR EL CODIGO DE LA INSTITUCION ORIGEN DE LA TRANSACCION";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(trxCon.getConsulta().getCon_message_type_code())) {
							switch (trxCon.getConsulta().getCon_message_type_code()) {
							case "TR":
								iso.setISO_000_Message_Type("1200");
								break;
							default:
								
								this.codError = "999";
								this.desError = "TIPO DE MENSAJE NO RECONOCIDO, O INVALIDO";
								return null;
							}
						}else {
							
							this.codError = "999";
							this.desError = "NO SE HA PODIDO RECUPERAR EL TIPO DE MENSAJE DE LA TRANSACCION";
							return null;
						}
						
					}else {
						
						this.codError = "999";
						this.desError = "SE ESPERABA LA SECCION CONSULTA EN EL MENSAJE";
						return null;
					}
					
					if(trxCon.getDetalleConsulta() != null) {
						
						if(trxCon.getDetalleConsulta().getCabeceraDetalle() != null) {
							
							if(!StringUtils.IsNullOrEmpty(trxCon.getDetalleConsulta().getCabeceraDetalle()
									.getDet_codigo_detalle())) {
								
								iso.setISO_022_PosEntryMode(trxCon.getDetalleConsulta().getCabeceraDetalle()
									.getDet_codigo_detalle());
							}else {
								
								this.codError = "999";
								this.desError = "SE ESPERABA EL CAMPO CODIGO DETALLE -  CABECERA DETALLE EN EL MENSAJE";
								return null;
							}
							
							if(!StringUtils.IsNullOrEmpty(trxCon.getDetalleConsulta().getCabeceraDetalle()
						              .getIns_fi_number())) {
								
								  iso.setISO_033_FWDInsID(trxCon.getDetalleConsulta().getCabeceraDetalle()
							              .getIns_fi_number());
								  
								  if(!iso.getISO_033_FWDInsID().equals(MemoryGlobal.ServipagosCodeEfi)) {
									  
									    this.codError = "999";
										this.desError = "CAMPO CODIGO IFI DESTINO NO COINCIDE POR EL ASIGNADO EN SERVIPAGOS (CABECERA DETALLE)";
										return null;
								  }
								
							}else {
							
								this.codError = "999";
								this.desError = "NO SE HA PODIDO RECUPERAR EL CAMPO CODIGO IFI DESTINO (CABECERA DETALLE)";
								return null;
							}
							
							if(!StringUtils.IsNullOrEmpty(trxCon.getDetalleConsulta().getCabeceraDetalle()
						              .getMon_codigo_moneda())) {
								
								iso.setISO_049_TranCurrCode(trxCon.getDetalleConsulta().getCabeceraDetalle()
						              .getMon_codigo_moneda().equals("001")?840:0);
							}else {
								
								this.codError = "999";
								this.desError = "NO SE HA PODIDO RECUPERAR EL CAMPO CODIGO MONEDA (CABECERA DETALLE)";
								return null;
							}
							
							if(trxCon.getDetalleConsulta().getRegistro().get(0).getCampo() != null && 
									trxCon.getDetalleConsulta().getRegistro().get(0).getCampo().size() >= 1) {
								
								iso.setISO_002_PAN(getRegistro(trxCon.getDetalleConsulta().getRegistro().get(0).getCampo(), "1"));
								iso.setISO_034_PANExt(getRegistro(trxCon.getDetalleConsulta().getRegistro().get(0).getCampo(), "2"));
								iso.setISO_035_Track2(getRegistro(trxCon.getDetalleConsulta().getRegistro().get(0).getCampo(), "3"));
								iso.setISO_036_Track3(getRegistro(trxCon.getDetalleConsulta().getRegistro().get(0).getCampo(), "4"));
								
								String etiquetas = StringUtils.Empty();
								for (int i = 1; i <= trxCon.getDetalleConsulta().getRegistroMetadata()
										   .get(0).getCampoDef().size(); i++) {
									
									etiquetas += getRegistroMetadata(trxCon.getDetalleConsulta().getRegistroMetadata()
										   .get(0).getCampoDef(), String.valueOf(i)) + "|";
								}
								iso.setISO_120_ExtendedData(StringUtils.trimEnd(etiquetas, "|"));
								String valores = StringUtils.Empty();
								for (int j = 1; j <= trxCon.getDetalleConsulta().getRegistro().get(0).getCampo().size(); j++) {
									
									valores += getRegistro(trxCon.getDetalleConsulta().getRegistro().get(0).getCampo()
											               , String.valueOf(j)) + "|";
								}
								iso.setISO_121_ExtendedData(StringUtils.trimEnd(valores, "|"));
								this.trxReqCon = trxCon;
								
								
							}else {
								
								this.codError = "999";
								this.desError = "SE ESPERABA ESTRUCTURA REGISTRO TIPO CABECERA EN EL MENSAJE";
								return null;
							}
							
							
						}else {
							
							this.codError = "999";
							this.desError = "SE ESPERABA LA SECCION DETALLE CONSULTA/CABECERA DETALLE EN EL MENSAJE";
							return null;
						}
						
					}else {
						
						this.codError = "999";
						this.desError = "SE ESPERABA LA SECCION DETALLE CONSULTA EN EL MENSAJE";
						return null;
					}
					
				}else {
					
					flagMensaje = false;
					this.codError = "999";
					this.desError = "ERROR AL SERIALIZAR/DESEREALIZAR MENSAJE CONSULTA SERVIPAGOS";
					return null;
				}
			}
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError = "ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, true);
			log.WriteLogMonitor("Error modulo ServipagosParser::parserSeripagos ", TypeMonitor.error, e);
			
		}finally {
		
			if(!flagMensaje) {
				this.codError = "999";
				if(this.desError.equalsIgnoreCase("TRANSACCION PARSEADA"))
					this.desError = "HA OCURRIDO UN ERROR AL PROCESAR EL MENSAJE DE SERVIPAGOS";
				this.trxReqAfec = null;
				this.trxReqCon = null;
			}
			else {
				if(trx!=null)
					this.trxReqAfec = trx;
				if(trxCon!=null)
					this.trxReqCon = trxCon;
			}
				
		}
		
		return iso;
	}
	
	protected String getRegistro(List<Campo> reg,  String id) {
		
		String value = StringUtils.Empty();
		try {
			
			value = reg
					.stream()
					.filter(p -> p.getId().equals(id))
					.findFirst().orElseGet(() -> null)
					.getValore();
					
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo ServipagosParser::getRegistro ", TypeMonitor.error, e);
		}
		return value;
	}
	
	protected String getRegistroMetadata(List<CampoDef> reg,  String id) {
		
		String value = StringUtils.Empty();
		try {
			
			value = reg
					.stream()
					.filter(p -> p.getId().equals(id))
					.findFirst().orElseGet(() -> null)
					.getName();
					
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo ServipagosParser::getRegistroMetadata ", TypeMonitor.error, e);
		}
		return value;
	}
}
