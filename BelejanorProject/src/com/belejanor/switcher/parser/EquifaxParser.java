package com.belejanor.switcher.parser;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.struct.equifax.ConsultaDatosPersona;
import com.belejanor.switcher.struct.equifax.ConsultaDatosPersonaRespuesta;
import com.belejanor.switcher.struct.equifax.DatosDirecciones;
import com.belejanor.switcher.struct.equifax.DatosPrincipales;
import com.belejanor.switcher.struct.equifax.RegisterData;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

public class EquifaxParser {

	private Logger log;
	private String codError;
	private String desError;
	
	public EquifaxParser() {
		
		log = new Logger();
		this.codError = "000";
		this.desError = "PARSEO EXITOSO";
	}

	public String getCodError() {
		return codError;
	}

	public String getDesError() {
		return desError;
	}
	
	public Iso8583 parserGetDataEquifax(ConsultaDatosPersona data) {
		
		Iso8583 iso = null;
		try {
			
			iso = new Iso8583();
			if(data != null) {
				if(!StringUtils.IsNullOrEmpty(data.getNumeroDocumento())) {
					
					iso.setISO_002_PAN(data.getNumeroDocumento().trim());
					iso.setISO_000_Message_Type("1200");
					iso.setISO_003_ProcessingCode("323001");
					iso.setISO_018_MerchantType("0007");
					iso.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(11));
					iso.setISO_024_NetworkId("555599");
					
				}else {
					
					iso.setISO_039_ResponseCode("101");
					iso.setISO_039p_ResponseDetail("NUMERO DE DOCUMENTO ES NULO O VACIO");
					this.codError = "999";
					this.desError = iso.getISO_039p_ResponseDetail();
				}
			}else {
				
				iso.setISO_039_ResponseCode("100");
				iso.setISO_039p_ResponseDetail("ESTRUCTURA CONSULTADATOS ES NULA");
				this.codError = "999";
				this.desError = iso.getISO_039p_ResponseDetail();
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS, ", e, true));
			this.codError = "999";
			this.desError = iso.getISO_039p_ResponseDetail();
			log.WriteLogMonitor("Error modulo EquifaxParser::parserGetDataEquifax ", TypeMonitor.error, e);
		}
		return iso;
	}
	
	public Iso8583 parserRegisterDataEquifax(RegisterData data) {
		
		Iso8583 iso = null;
		try {
			
			iso = new Iso8583();
			if(data != null) {
				
				iso.setISO_000_Message_Type("1200");
				iso.setISO_003_ProcessingCode("823001");
				iso.setISO_018_MerchantType("0007");
				iso.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(11));
				iso.setISO_024_NetworkId("555599");
				
				if(data.getIdConsultaEquifax() != -1)
					
					iso.setISO_037_RetrievalReferenceNumber(String.valueOf(data.getIdConsultaEquifax()).toUpperCase());
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO ID_CONSULTA_EQUIFAX ES NULO, O AUSENTE");
					return iso;
				}
					
				if(!StringUtils.IsNullOrEmpty(data.getUsuarioConsulta()))
					
					iso.setISO_041_CardAcceptorID(data.getUsuarioConsulta().toUpperCase());
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO USUARIO_CONSULTA ES NULO, O AUSENTE");
					return iso;
				}
				
				if(!StringUtils.IsNullOrEmpty(data.getNumeroDocumento()))
					
					iso.setISO_002_PAN(data.getNumeroDocumento().toUpperCase());
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO NUMERO_DOCUMENTO ES NULO, O AUSENTE");
					return iso;
				}
				
				if(!StringUtils.IsNullOrEmpty(data.getTipoConsuta())) {
					//if(tipoConsulta.isMember(data.getTipoConsuta().name())) {
						
						iso.setISO_023_CardSeq(data.getTipoConsuta().toUpperCase());
						
					/*}else {
						
						this.codError = "906";
						iso.setISO_039_ResponseCode("906");
						iso.setISO_039p_ResponseDetail("CAMPO TIPO_CONSULTA ES NULO, O AUSENTE");
						return iso;
					//}*/
				}else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO TIPO CONSULTA ES NULO O VACIO");
					return iso;
				}
				
				if(!StringUtils.IsNullOrEmpty(data.getTipoCreditoSolicitado()))
					
					iso.setISO_022_PosEntryMode(data.getTipoCreditoSolicitado().toUpperCase());
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO TIPO_CREDITO_SOLICITADO ES NULO, O AUSENTE");
					return iso;
				}
				
				if(data.getMontoSolicitado() != -1)
					
					iso.setISO_004_AmountTransaction(data.getMontoSolicitado());
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO MONTO_SOLICITADO ES NULO, O AUSENTE");
					return iso;
				}
				
				if(data.getPlazoSolicitado() != -1)
					
					iso.setISO_120_ExtendedData(String.valueOf(data.getPlazoSolicitado()).toUpperCase());
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO PLAZO SOLICITADO ES NULO, O AUSENTE");
					return iso;
				}
				
				if(data.getGastosFinancieros() != -1)
					
					iso.setISO_006_BillAmount(data.getGastosFinancieros());
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO GASTOS FINANCIEROS ES NULO, O AUSENTE");
					return iso;
				}
				
				if(!StringUtils.IsNullOrEmpty(data.getTipoCliente()))
					
					iso.setISO_019_AcqCountryCode(data.getTipoCliente().toUpperCase());
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO TIPO CLIENTE ES NULO, O AUSENTE");
					return iso;
				}
				
				if(data.getCapacidadPago() != -1)
					
					iso.setISO_008_BillFeeAmount(data.getCapacidadPago());
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO CAPACIDAD DE PAGO ES NULO, O AUSENTE");
					return iso;
				}
				
				if(data.getScoreTitular() != -1)
					
					iso.setISO_121_ExtendedData(String.valueOf(data.getScoreTitular()).toUpperCase());
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO SCORE TITULAR ES NULO, O AUSENTE");
					return iso;
				}
				
				if(data.getScoreTitularSobreendeudamiento() != -1)
					
					iso.setISO_122_ExtendedData(String.valueOf(data.getScoreTitularSobreendeudamiento()));
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO SCORE TITULAR SOBREENDEUDAMIENTO ES NULO, O AUSENTE");
					return iso;
				}
				
				if(!StringUtils.IsNullOrEmpty(data.getNumeroDocumentoConyuge())) {
					
					iso.setISO_034_PANExt(data.getNumeroDocumentoConyuge().toUpperCase());
				}
				
				if(data.getScoreConyuge() != -1) {
					
					iso.setISO_123_ExtendedData(String.valueOf(data.getScoreConyuge()));
				}
				
				if(!StringUtils.IsNullOrEmpty(data.getDesicion())) {
					//if(tipoDecision.isMember(data.getDesicion().name())) {
						
						iso.setISO_044_AddRespData(data.getDesicion().toUpperCase());
						
					/*}else {
						
						this.codError = "906";
						iso.setISO_039_ResponseCode("906");
						iso.setISO_039p_ResponseDetail("CAMPO DECISION ES NULO, O AUSENTE");
						return iso;
					}*/
				}else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO DECISION ES NULO, O AUSENTE");
					return iso;
				}
				
				if(!StringUtils.IsNullOrEmpty(data.getSegmento())) {
					//if(tipoSegmento.isMember(data.getSegmento().name())) {
						
						iso.setISO_090_OriginalData(data.getSegmento().toUpperCase());
						
					/*}else {
						
						this.codError = "906";
						iso.setISO_039_ResponseCode("906");
						iso.setISO_039p_ResponseDetail("CAMPO SEGMENTO ES NULO, O AUSENTE");
						return iso;
					}*/
				}else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO SEGMENTO ES NULO, O AUSENTE");
					return iso;
				}
				
				if(!StringUtils.IsNullOrEmpty(data.getModelo())) {
					//if(tipoModelo.isMember(data.getModelo().name())) {
						
						iso.setISO_114_ExtendedData(data.getModelo().toUpperCase());
						
					/*}else {
						
						this.codError = "906";
						iso.setISO_039_ResponseCode("906");
						iso.setISO_039p_ResponseDetail("CAMPO MODELO ES NULO, O AUSENTE");
						return iso;
					}*/
				}else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO MODELO ES NULO, O AUSENTE");
					return iso;
				}
				
				if(data.getMontoSugerido() != -1)
					
					iso.setISO_028_TranFeeAmount(data.getMontoSugerido());
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO MONTO SUGERIDO ES NULO, O AUSENTE");
					return iso;
				}
				
				if(data.getPlazoSugerido() != -1)
					
					iso.setISO_124_ExtendedData(String.valueOf(data.getPlazoSugerido()));
				
				else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO PLAZO SUGERIDO ES NULO, O AUSENTE");
					return iso;
				}
				
				if(data.getFechaConsulta() != null) {
					
					iso.setISO_007_TransDatetime(data.getFechaConsulta());
					
				}else {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("906");
					iso.setISO_039p_ResponseDetail("CAMPO FECHA CONSULTA ES NULO, O AUSENTE");
					return iso;
				}
				
				/*Serializo la trama y la guardo en el campo 115*/
				iso.setISO_115_ExtendedData(SerializationObject.ObjectToString(data, RegisterData.class));
				if(StringUtils.IsNullOrEmpty(iso.getISO_115_ExtendedData())) {
					
					this.codError = "906";
					iso.setISO_039_ResponseCode("909");
					iso.setISO_039p_ResponseDetail("ERROR AL SERIALIZAR DATA REQUERIMIENTO");
					return iso;
				}
				
			}else {
				
				iso.setISO_039_ResponseCode("100");
				iso.setISO_039p_ResponseDetail("ESTRUCTURA REGISTERDATA ES NULA");
				this.codError = "999";
				this.desError = iso.getISO_039p_ResponseDetail();
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS, ", e, true));
			this.codError = "999";
			this.desError = iso.getISO_039p_ResponseDetail();
			log.WriteLogMonitor("Error modulo EquifaxParser::RegisterDataEquifax ", TypeMonitor.error, e);
		}
		
		return iso;
	}
	
	public ConsultaDatosPersonaRespuesta buildResponseToEquifax(Iso8583 iso) {
		
		ConsultaDatosPersonaRespuesta response = null;
		try {
			
			response = new ConsultaDatosPersonaRespuesta();
			String [] dataQuery = iso.getISO_114_ExtendedData().split("\\|");
			/*DatosPrincipales*/
			DatosPrincipales dataPerson = new DatosPrincipales();
			dataPerson.setNumeroDocummento(iso.getISO_002_PAN());
			dataPerson.setPersonaId(dataQuery[1]);
			dataPerson.setTipoDocumento(dataQuery[2]);
			dataPerson.setIngresosUnidadFamiliar(Double.parseDouble(dataQuery[6]) + Double.parseDouble(dataQuery[7]));
			dataPerson.setGastosHogarUnidadFamiliar(Double.parseDouble(dataQuery[8]) + Double.parseDouble(dataQuery[9]));
			dataPerson.setGastosFinancierosInternos(Double.parseDouble(dataQuery[17]));
			if(dataQuery[3].equals("NULL"))
				dataPerson.setNumeroDocumentoConyuge(null);
			else
				dataPerson.setNumeroDocumentoConyuge(dataQuery[3]);
			if(dataQuery[4].equals("NULL"))
				dataPerson.setTipoDocumentoConyuge(null);
			else
				dataPerson.setTipoDocumentoConyuge(dataQuery[4]);
			dataPerson.setFechaNacimientoPrincipal(dataQuery[18]);
			dataPerson.setFechaNacimientoConyugue(dataQuery[19]);
			
			/*DatosDireccion*/
			DatosDirecciones dataDirec = new DatosDirecciones();
			dataDirec.setDireccion(dataQuery[10]);
			dataDirec.setCiudad(dataQuery[12]);
			dataDirec.setVerficado(true);
			dataDirec.setFechaModificacion(FormatUtils.StringToDate(dataQuery[14], "yyyy-MM-dd"));
			dataDirec.setParroquia(dataQuery[13]);
			dataDirec.setTipoDireccion(dataQuery[11]);
			dataDirec.setTelefono(dataQuery[15]);
			dataDirec.setTipoTelefono(dataQuery[16]);
			
			response.setDatosPrincipales(dataPerson);
			response.setDatosDirecciones(dataDirec);
			
			
		} catch (Exception e) {
			
			this.codError = "999";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS, ", e, true);
			log.WriteLogMonitor("Error modulo EquifaxParser::parserGetDataEquifax ", TypeMonitor.error, e);
		}
		
		return response;
	}
}
