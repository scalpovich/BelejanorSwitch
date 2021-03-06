package com.belejanor.switcher.bridges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.belejanor.switcher.authorizations.ITransactionServipagos;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.parser.ServipagosParser;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.struct.servipagos.Registro;
import com.belejanor.switcher.struct.servipagos.RegistroMetadata;
import com.belejanor.switcher.struct.servipagos.TrxRequerimientoAfectacion;
import com.belejanor.switcher.struct.servipagos.TrxRequerimientoConsulta;
import com.belejanor.switcher.struct.servipagos.TrxRespuestaAfectacion;
import com.belejanor.switcher.struct.servipagos.TrxRespuestaConsulta;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

public class BridgeServipagos<T> implements ITransactionServipagos{

	private Logger log;
	private String codError;
	private String desError;
	
	public  BridgeServipagos() {
		
		log = new Logger();
		this.codError = "000";
		this.desError = "TRANSACCION DIRECCIONADA";
	}
	
	public String getCodError() {
		return codError;
	}

	public String getDesError() {
		return desError;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String ProcesaTransacciones(String xml, String IP) {
		
		ServipagosParser parser = null;
		String data = StringUtils.Empty();
		Iso8583 iso = null;
		csProcess proccesor = null;
		try {
			
			parser = new ServipagosParser();
			iso = new Iso8583();
			iso = parser.parserSeripagos(xml);
			if(parser.getCodError().equals("000")) {
				
				proccesor = new csProcess();
				iso = proccesor.ProcessTransactionMain(iso, IP);
				if(iso.getISO_039_ResponseCode().startsWith("9")) {
				
					this.codError = "999";
					this.desError = iso.getISO_039p_ResponseDetail();
					
				}else {
					switch (iso.getISO_003_ProcessingCode()) {
					case "011000":
					case "231010":
					case "271010":
						data = GenericResponse(iso, parser, (T) parser.getTrxReqAfec());
						break;
					case "323011":
						data = GenericResponse(iso, parser, (T) parser.getTrxReqCon());
						break;
					default:
						break;
					}
					
				}
				
			}else {
				
				if(parser.getCodError().equals("999")) {
					
					this.codError = parser.getCodError();
					this.desError = parser.getDesError();
					
				}else {
					
					data = GenericResponse(iso, parser, (T) parser.getTrxReqAfec());
				}
					
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BridgeServipagos::ProcesaTransacciones ", TypeMonitor.error, e);
		}
		return data;
	}
	
	protected String GenericResponse(Iso8583 iso, ServipagosParser parser, T mensaje) {
		
		String data = StringUtils.Empty();
		TrxRequerimientoAfectacion trxReqAfectacion;
		TrxRequerimientoConsulta trxReqConsulta;
		TrxRespuestaAfectacion trxRespuestaAfectacion = null;
		TrxRespuestaConsulta trxRespuestaConsulta = null;
		try {
			
			if(mensaje instanceof TrxRequerimientoAfectacion) {
				
				try {
					
					trxReqAfectacion = (TrxRequerimientoAfectacion) mensaje;
					trxRespuestaAfectacion = new TrxRespuestaAfectacion();
					trxRespuestaAfectacion.setTransaccion(trxReqAfectacion.getTransaccion());
					trxRespuestaAfectacion.setDetalleTransaccion(trxReqAfectacion.getDetalleTransaccion());
					
					if(parser.getCodError().equals("000")) {
						
						if(iso.getISO_039_ResponseCode().equals("000") && iso.getISO_000_Message_Type().startsWith("12")) {
						
							List<Registro> regTemp = new ArrayList<>();
							List<RegistroMetadata> regMetaTemp = new ArrayList<>();
							trxRespuestaAfectacion.getDetalleTransaccion().setRegistro(regTemp);
							trxRespuestaAfectacion.getDetalleTransaccion().setRegistroMetadata(regMetaTemp);
							
							RegistroMetadata registroMeta = new RegistroMetadata("Cabecera");
							registroMeta.addCampoDef("1", "NOMBRECLIENTE");
							registroMeta.addCampoDef("2", "TIPODOCUMENTO");
							registroMeta.addCampoDef("3", "NUMERODOCUMENTO");
							registroMeta.addCampoDef("4", "CONSEP");
							
							
							Registro registro = new Registro("Cabecera");
							registro.addCampo("1", Arrays.asList(iso.getISO_124_ExtendedData().split("\\^")).get(2));
							registro.addCampo("2", Arrays.asList(iso.getISO_124_ExtendedData().split("\\^")).get(3));
							registro.addCampo("3", Arrays.asList(iso.getISO_124_ExtendedData().split("\\^")).get(1));
							
							IsoRetrievalTransaction sql = null;
							wIso8583 isoDirec = null;
							if(iso.getISO_004_AmountTransaction() >= MemoryGlobal.ServipagosMontoConsep) {
								
								sql = new IsoRetrievalTransaction();
								isoDirec = new wIso8583();
								isoDirec.setISO_002_PAN(Arrays.asList(iso.getISO_124_ExtendedData().split("\\^")).get(1));
								isoDirec = sql.getDireccionTelefonoFit1(isoDirec);
								registroMeta.addCampoDef("5", "DIRECCION");
								registroMeta.addCampoDef("6", "CIUDAD");
								registroMeta.addCampoDef("7", "TELEFONO");
								if(isoDirec.getISO_039_ResponseCode().equals("000")) {
									
									registro.addCampo("4", "Y");
									registro.addCampo("5", isoDirec.getISO_120_ExtendedData());
									registro.addCampo("6", isoDirec.getISO_122_ExtendedData());
									registro.addCampo("7", isoDirec.getISO_123_ExtendedData());
									
								}else {
									
									registro.addCampo("4", "Y");
									registro.addCampo("5", "N/D");
									registro.addCampo("6", "N/D");
									registro.addCampo("7", "N/D");
								}
								
								
							}else {
								
								registro.addCampo("4", "N");
							}
							
							trxRespuestaAfectacion.getDetalleTransaccion().addRegistroMetadata(registroMeta);
							trxRespuestaAfectacion.getDetalleTransaccion().addRegistro(registro);
							trxRespuestaAfectacion.getTransaccion().setTra_error_transaccion("0000");
						}else {
							
							trxRespuestaAfectacion.getTransaccion().setTra_error_transaccion("0" + iso.getISO_039_ResponseCode());
						}
							
					}else {
						
						trxRespuestaAfectacion.getTransaccion().setTra_error_transaccion("0909");
					}
					
				} finally {
					
					data = SerializationObject.ObjectToString(trxRespuestaAfectacion, TrxRespuestaAfectacion.class);
				}
			}
			
			if(mensaje instanceof TrxRequerimientoConsulta) {
				
				try {
					
					trxReqConsulta = (TrxRequerimientoConsulta) mensaje;
					trxRespuestaConsulta = new TrxRespuestaConsulta();
					trxRespuestaConsulta.setConsulta(trxReqConsulta.getConsulta());
					trxRespuestaConsulta.setDetalleTransaccion(trxReqConsulta.getDetalleConsulta());
					
					
					if(parser.getCodError().equals("000")) {
						
						if(iso.getISO_039_ResponseCode().equals("000") && iso.getISO_000_Message_Type().startsWith("12")) {
						
							List<Registro> regTemp = new ArrayList<>();
							List<RegistroMetadata> regMetaTemp = new ArrayList<>();
							trxRespuestaConsulta.getDetalleTransaccion().setRegistro(regTemp);
							trxRespuestaConsulta.getDetalleTransaccion().setRegistroMetadata(regMetaTemp);
							
							RegistroMetadata registroMeta = new RegistroMetadata("Cabecera");
							registroMeta.addCampoDef("1", "IDENTIFICACIONCARTERA");
							registroMeta.addCampoDef("2", "NOMBRECLIENTE");
							
							
							RegistroMetadata registroMeta2 = new RegistroMetadata("Cabecera");
							registroMeta2.addCampoDef("1", "NUMEROOPERACION");
							registroMeta2.addCampoDef("2", "TIPOCARTERA");
							registroMeta2.addCampoDef("3", "VALORPAGAR");
							registroMeta2.addCampoDef("4", "NUMEROCUOTASVENCIDAS");
							registroMeta2.addCampoDef("5", "VALORCUOTASVENCIDAS");
							registroMeta2.addCampoDef("6", "FECHACUOTAPORVENCER");
							registroMeta2.addCampoDef("7", "VALORCUOTAPORVENCER");
							
							
							
							Registro registro = new Registro("Cabecera");
							registro.addCampo("1", iso.getISO_115_ExtendedData());
							registro.addCampo("2", iso.getISO_122_ExtendedData());
							
							
							Registro registro2 = new Registro("Cabecera");
							registro2.addCampo("1", iso.getISO_102_AccountID_1());
							registro2.addCampo("2", iso.getISO_123_ExtendedData());
							registro2.addCampo("3", iso.getISO_054_AditionalAmounts());
							registro2.addCampo("4", iso.getISO_124_ExtendedData());
							registro2.addCampo("5", iso.getISO_090_OriginalData());
							registro2.addCampo("6", iso.getISO_114_ExtendedData());
							registro2.addCampo("7", iso.getISO_103_AccountID_2());
							
							
							trxRespuestaConsulta.getDetalleTransaccion().addRegistroMetadata(registroMeta);
							trxRespuestaConsulta.getDetalleTransaccion().addRegistroMetadata(registroMeta2);
							trxRespuestaConsulta.getDetalleTransaccion().addRegistro(registro);
							trxRespuestaConsulta.getDetalleTransaccion().addRegistro(registro2);
							trxRespuestaConsulta.getConsulta().setCon_error_consulta("0000");
						}else {
							
							trxRespuestaConsulta.getConsulta().setCon_error_consulta("0" + iso.getISO_039_ResponseCode());
						}
							
					}else {
						
						trxRespuestaConsulta.getConsulta().setCon_error_consulta("0909");
					}
					
				} finally {
					
					data = SerializationObject.ObjectToString(trxRespuestaConsulta, TrxRespuestaConsulta.class);
				}	
			}
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo BridgeServipagos::GenericResponse ", TypeMonitor.error, e);
		}finally {
			
			//data = SerializationObject.ObjectToString(trxRespuestaAfectacion, TrxRespuestaAfectacion.class);
		}
		return data;
	}
}
