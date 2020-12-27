package com.belejanor.switcher.parser;

import com.belejanor.switcher.banred.pagodirecto.struct.DataDependend;
import com.belejanor.switcher.banred.pagodirecto.struct.DependentTransactionReplyFields;
import com.belejanor.switcher.banred.pagodirecto.struct.StandardReplyFields;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxConsultaPagoDirectoRequest;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxConsultaPagoDirectoResponse;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxMessageControlRequest;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxMessageControlResponse;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxTransferenciaPagoDirectoRequest;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxTransferenciaPagoDirectoResponse;
import com.belejanor.switcher.banred.pagodirecto.struct.TypeMessageCode;
import com.belejanor.switcher.banred.pagodirecto.struct.UserRepplyDefinedData;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.structbanred.StructBanredMessage;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.NumbersUtils;
import com.belejanor.switcher.utils.StringUtils;

public class PagoDirectoParser {

	private Logger log;
	private String codError;
	private String desError;
	
	public PagoDirectoParser() {
		log = new Logger();
		
	}

	public String getCodError() {
		return codError;
	}

	public String getDesError() {
		return desError;
	}
	
	public TrxMessageControlRequest convertBytesToMessageControlRequest(StructBanredMessage message) {
		
		TrxMessageControlRequest trx = null;
		try {
			
			trx = new TrxMessageControlRequest();
			trx = trx.byteArraytoStruct(message.getBodyBytesMessage());
			if(trx == null) {
				
				this.codError = "908";
				this.desError = "ERROR AL PARSEAR A ESTRUCTURA [TrxMessageControlRequest]";
			}else {
				
				this.codError = "000";
			}
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::convertBytesToMessageControlRequest", TypeMonitor.error, e);
		}
		
		return trx;
	}
	
    public TrxConsultaPagoDirectoRequest convertBytesToTrxConsultaPagoDirectoRequest(StructBanredMessage message) {
		
    	TrxConsultaPagoDirectoRequest trx = null;
		try {
			
			trx = new TrxConsultaPagoDirectoRequest();
			trx = trx.byteArraytoStruct(message.getBodyBytesMessage());
			if(trx == null) {
				
				this.codError = "908";
				this.desError = "ERROR AL PARSEAR A ESTRUCTURA [TrxConsultaPagoDirectoRequest]";
			}else {
				
				this.codError = "000";
			}
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::convertBytesToTrxConsultaPagoDirectoRequest", TypeMonitor.error, e);
		}
		
		return trx;
	}
    
    public TrxTransferenciaPagoDirectoRequest convertBytesToTrxTransferenciaPagoDirectoRequest(StructBanredMessage message) {
		
    	TrxTransferenciaPagoDirectoRequest trx = null;
		try {
			
			trx = new TrxTransferenciaPagoDirectoRequest();
			trx = trx.byteArraytoStruct(message.getBodyBytesMessage());
			if(trx == null) {
				
				this.codError = "908";
				this.desError = "ERROR AL PARSEAR A ESTRUCTURA [TrxTransferenciaPagoDirectoRequest]";
			}else {
				
				this.codError = "000";
			}
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::convertBytesToTrxTransferenciaPagoDirectoRequest", TypeMonitor.error, e);
		}
		
		return trx;
	}
	
    /*-------------------------------------------------------------*/
	public Iso8583 convertTrxMessageControlRequestToIso8583(TrxMessageControlRequest trxMessageControl) {
		
		Iso8583 iso = null;
		try {
			
			iso = new Iso8583();
			iso.setISO_000_Message_Type("1800");
			iso.setISO_003_ProcessingCode("000000");
			iso.setISO_011_SysAuditNumber(trxMessageControl.getMessage_control_group().getSession_sequence_number_9006());
			iso.setISO_012_LocalDatetime(FormatUtils.StringToDate("20" + trxMessageControl.getControl_dependent_data().getSession_date_9006() + 
					trxMessageControl.getControl_dependent_data().getSession_time_9006(), "yyyyMMddHHmmss"));
			iso.setISO_015_SettlementDatel(FormatUtils.StringToDate("20" + trxMessageControl.getControl_dependent_data()
					                      .getSession_bussiness_date_9006(), "yyyyMMdd"));
			iso.setISO_018_MerchantType("0007");
			iso.setISO_024_NetworkId("555222");
			iso.setISO_041_CardAcceptorID(trxMessageControl.getMessage_control_group().getMessage_terminal_number_9005());
			iso.setISO_037_RetrievalReferenceNumber(trxMessageControl.getMessage_control_group().getMessage_sequence_number_9004());
			iso.setISO_032_ACQInsID(trxMessageControl.getMessage_control_group().getMessage_fi_number_9004());
			iso.setISO_120_ExtendedData(trxMessageControl.getMessage_control_group().getTransaction_code_S9004().toString());
			iso.setISO_BitMap(trxMessageControl.getMessage_control_group().getMessage_type_code_X002().name());
			this.codError = "000";
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::convertTrxMessageControlRequestToIso8583", TypeMonitor.error, e);
		}
		
		return iso;
	}
	
	public Iso8583 convertTrxConsultaPagoDirectoRequestToIso8583(TrxConsultaPagoDirectoRequest trxConsultaRequest) {
		
		Iso8583 iso = null;
		try {
			
			iso = new Iso8583();
			switch (trxConsultaRequest.getMessage_type_code_X002()) {
			case TR:
				iso.setISO_000_Message_Type("1200");
				break;
			case FR:
			case XR:
				iso.setISO_000_Message_Type("1400");
				break;
			default:
				iso.setISO_000_Message_Type("1200");
				break;
			}
			
			String tagPrefijo = StringUtils.Empty();
			String tagOrigen = StringUtils.Empty();
			String tagDestino = StringUtils.Empty();
						
			switch (trxConsultaRequest.getTransaction_code_S9004()) {
			case CONSULTA_PAGO_DIRECTO:
				tagPrefijo = "31";
				break;
				
			case REVERSO_REAL_TRANSFER_CTA_AHO:
			case REVERSO_COND_TRANSFER_CTA_AHO:
			case TRANSFERENCIA_CTA_AHORROS:
			case REVERSO_COND_TRANSFER_TRJ_CRED:
			case REVERSO_REAL_TRANSFER_TRJ_CRED:
			case PAGO_TARJETA_CREDITO:
				tagPrefijo = "01";
				break;
			default:
				/*obliga a que la transaccion no exista*/
				iso.setISO_003_ProcessingCode("XX");
				break;
			}
			
			switch (trxConsultaRequest.getStandard_request_fields()
					.getCard_related_group().getRed_appl_type_source_9001()) {
			case "4":
				tagOrigen = "20";
				break;
			case "2":	
			case "5":
				tagOrigen = "10";
				break;
			case "6":
				tagOrigen = "40";
				break;
			case "7":
				tagOrigen = "50";
				break;
			default:
				tagOrigen = "XX";
				break;
			}
			
			switch (trxConsultaRequest.getStandard_request_fields()
					.getCard_related_group().getCard_appl_code_X002()) {
			case "02":
				/*Modulo de Tarjetas de Credito RECEPTOR*/
				tagDestino = "30";
				break;
			case "05":
				tagDestino = "10";
				break;
			default:
				tagDestino = "XX";
				break;
			}
			
			iso.setISO_003_ProcessingCode(tagPrefijo + tagOrigen + tagDestino);
			
			String signoValor = trxConsultaRequest.getStandard_request_fields().getDetail_transaction().getTransaction_amount_S9_018()
					.substring(trxConsultaRequest.getStandard_request_fields().getDetail_transaction()
					.getTransaction_amount_S9_018().length() - 1);
			
			long num = Long.parseLong(trxConsultaRequest.getStandard_request_fields().getDetail_transaction()
					.getTransaction_amount_S9_018().replace("+", StringUtils.Empty())
					.replaceFirst("-", StringUtils.Empty()));
			if(signoValor.equals("+"))
				iso.setISO_004_AmountTransaction(num/100);
			else
				iso.setISO_004_AmountTransaction((num/100) * -1);
			
			
			
			iso.setISO_BitMap(trxConsultaRequest.getStandard_request_fields().getXact_source_information()
					         .getSource_aba_number_9010().trim());
			
			iso.setISO_011_SysAuditNumber(trxConsultaRequest.getSession_sequence_number_9006().trim());
			
			iso.setISO_037_RetrievalReferenceNumber(trxConsultaRequest.getMessage_sequence_number_9004().trim());
			
			iso.setISO_022_PosEntryMode(trxConsultaRequest.getStandard_request_fields().getDetail_transaction()
										.getForce_post_indicator_X001().trim());
			
			iso.setISO_090_OriginalData(trxConsultaRequest.getStandard_request_fields().getDetail_transaction()
					                   .getReversal_indicator_X001().trim());
			
			iso.setISO_012_LocalDatetime(FormatUtils.StringToDate("20" + trxConsultaRequest.getStandard_request_fields().getXact_source_information()
					                     .getSource_date_9006() +
					                     trxConsultaRequest.getStandard_request_fields().getXact_source_information().getSource_time_9006(),
					                     "yyyyMMddHHmmss"));
			iso.setISO_042_Card_Acc_ID_Code(trxConsultaRequest.getStandard_request_fields().getXact_source_information()
					                     .getSource_branch_number_9004().trim());
			
			iso.setISO_043_CardAcceptorLoc(trxConsultaRequest.getMessage_terminal_number_9005().trim());
			
			iso.setISO_041_CardAcceptorID(trxConsultaRequest.getStandard_request_fields().getXact_source_information()
					                     .getTrf_terminal_type_9001());
			iso.setISO_015_SettlementDatel(FormatUtils.StringToDate("20" + trxConsultaRequest.getStandard_request_fields().getXact_source_information()
					                     .getSource_business_date_9006(),"yyyyMMdd"));
			
			iso.setISO_018_MerchantType("0007");//Switch Externo
			iso.setISO_024_NetworkId("555222"); //Red Pago Directo
			
			iso.setISO_032_ACQInsID(trxConsultaRequest.getMessage_fi_number_9004().trim());//???
			iso.setISO_033_FWDInsID(trxConsultaRequest.getStandard_request_fields().getTarget_institution_group().getAuth_fi_nbr_9004().trim()); //???
			iso.setISO_013_LocalDate(FormatUtils.StringToDate("20" + trxConsultaRequest.getStandard_request_fields().getTarget_institution_group()
									.getHost_business_date_9006(),"yyyyMMdd"));
			/*Se concatena toda la estructura STANDIN-GROUP*/
			iso.setISO_114_ExtendedData(trxConsultaRequest.getStandard_request_fields().getStand_in_group().getStandin_auth_type_X001().trim() + "|" +
										trxConsultaRequest.getStandard_request_fields().getStand_in_group().getStandin_auth_metod_X001().trim() + "|" +
										trxConsultaRequest.getStandard_request_fields().getStand_in_group().getStandin_result_code_9004().trim());
			
			iso.setISO_052_PinBlock(trxConsultaRequest.getStandard_request_fields().getCard_related_group().getPin_verify_flag_X001().trim());
			iso.setISO_055_EMV(trxConsultaRequest.getStandard_request_fields().getCard_related_group().getTrack_II_valid_flag_X001().trim());
			
			/*Se concatentan de la estrictura CARD-RELATED-GROUP, los campos RED-TARGET-PHONE, RED-APPL-TYPE-SOURCE,
			 * RED-FLG-REV-PD, RED-FILLER y CARD-APPL-CODE*/
			
			iso.setISO_115_ExtendedData(trxConsultaRequest.getStandard_request_fields().getCard_related_group().getRed_target_phone_9010().trim() + "|" +
					                    trxConsultaRequest.getStandard_request_fields().getCard_related_group().getRed_appl_type_source_9001().trim() + "|" +
					                    trxConsultaRequest.getStandard_request_fields().getCard_related_group().getRed_flg_red_pd_X002().trim() + "|" +
					                    trxConsultaRequest.getStandard_request_fields().getCard_related_group().getRed_filler_X_003().trim() + "|" + 
					                    trxConsultaRequest.getStandard_request_fields().getCard_related_group().getCard_appl_code_X002().trim());
			iso.setISO_035_Track2(trxConsultaRequest.getStandard_request_fields().getCard_related_group()
								 .getTrack_2_data_X040().trim());
			
			
			iso.setISO_034_PANExt(trxConsultaRequest.getStandard_request_fields().getXact_source_information()
								 .getTrf_source_name_X031().trim());
			
			iso.setISO_036_Track3(trxConsultaRequest.getUser_request_defined_data().getReqpdr_target_name_X035().trim());
			
			iso.setISO_102_AccountID_1(String.valueOf(Long.parseLong(trxConsultaRequest.getStandard_request_fields().getDetail_transaction().getTrans_acct_nbr_9018().trim())));
			
			iso.setISO_120_ExtendedData(trxConsultaRequest.getUser_request_defined_data().getReqpdr_observations_X010().trim());
			
			iso.setISO_002_PAN(trxConsultaRequest.getUser_request_defined_data().getReqpdr_source_id_X013().trim());
			
			iso.setISO_023_CardSeq(trxConsultaRequest.getUser_request_defined_data().getReqpdr_target_id_X013().trim());
			
			iso.setISO_121_ExtendedData(trxConsultaRequest.getUser_request_defined_data().getReqpdr_city_X010().trim());
			
			iso.setISO_122_ExtendedData(trxConsultaRequest.getUser_request_defined_data().getReqpdr_reference_X020().trim());
			
			iso.setISO_123_ExtendedData(trxConsultaRequest.getUser_request_defined_data().getFiller_X006().trim());
			
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::convertTrxConsultaPagoDirectoRequestToIso8583", TypeMonitor.error, e);
		}
		
		return iso;
	}
	
    public Iso8583 convertTrxTransferenciaPagoDirectoRequestToIso8583(TrxTransferenciaPagoDirectoRequest trxTransferenciaRequest) {
		
		Iso8583 iso = null;
		try {
			
			iso = new Iso8583();
			switch (trxTransferenciaRequest.getMessage_type_code_X002()) {
			case TR:
				iso.setISO_000_Message_Type("1200");
				break;
			case FR:
			case XR:
				iso.setISO_000_Message_Type("1400");
				break;
			default:
				iso.setISO_000_Message_Type("1200");
				break;
			}
			
			String tagPrefijo = StringUtils.Empty();
			String tagOrigen = StringUtils.Empty();
			String tagDestino = StringUtils.Empty();
						
			switch (trxTransferenciaRequest.getTransaction_code_S9004()) {
			case CONSULTA_PAGO_DIRECTO:
				tagPrefijo = "31";
				break;
				
			case REVERSO_REAL_TRANSFER_CTA_AHO:
			case REVERSO_COND_TRANSFER_CTA_AHO:
			case TRANSFERENCIA_CTA_AHORROS:
			case REVERSO_COND_TRANSFER_TRJ_CRED:
			case REVERSO_REAL_TRANSFER_TRJ_CRED:
			case PAGO_TARJETA_CREDITO:
				tagPrefijo = "01";
				break;
			default:
				/*obliga a que la transaccion no exista*/
				iso.setISO_003_ProcessingCode("XX");
				break;
			}
			
			switch (trxTransferenciaRequest.getStandard_request_fields()
					.getCard_related_group().getRed_appl_type_source_9001()) {
			case "4":
				tagOrigen = "20";
				break;
			case "2":	
			case "5":
				tagOrigen = "10";
				break;
			case "6":
				tagOrigen = "40";
				break;
			case "7":
				tagOrigen = "50";
				break;
			default:
				tagOrigen = "XX";
				break;
			}
			
			switch (trxTransferenciaRequest.getStandard_request_fields()
					.getCard_related_group().getCard_appl_code_X002()) {
			case "02":
				/*Modulo de Tarjetas de Credito RECEPTOR*/
				tagDestino = "30";
				break;
			case "05":
				tagDestino = "10";
				break;
			default:
				tagDestino = "XX";
				break;
			}
			
			iso.setISO_003_ProcessingCode(tagPrefijo + tagOrigen + tagDestino);
			
			String signoValor = trxTransferenciaRequest.getStandard_request_fields().getDetail_transaction().getTransaction_amount_S9_018()
					.substring(trxTransferenciaRequest.getStandard_request_fields().getDetail_transaction()
					.getTransaction_amount_S9_018().length() - 1);
			
			long num = Long.parseLong(trxTransferenciaRequest.getStandard_request_fields().getDetail_transaction()
					.getTransaction_amount_S9_018().replace("+", StringUtils.Empty())
					.replaceFirst("-", StringUtils.Empty()));
			if(signoValor.equals("+"))
				iso.setISO_004_AmountTransaction(num/100);
			else
				iso.setISO_004_AmountTransaction((num/100) * -1);
			
			
			iso.setISO_BitMap(trxTransferenciaRequest.getStandard_request_fields().getXact_source_information()
					         .getSource_aba_number_9010().trim());
			
			iso.setISO_011_SysAuditNumber(trxTransferenciaRequest.getSession_sequence_number_9006().trim());
			
			iso.setISO_037_RetrievalReferenceNumber(trxTransferenciaRequest.getMessage_sequence_number_9004().trim());
			
			iso.setISO_022_PosEntryMode(trxTransferenciaRequest.getStandard_request_fields().getDetail_transaction()
										.getForce_post_indicator_X001().trim());
			
			iso.setISO_090_OriginalData(trxTransferenciaRequest.getStandard_request_fields().getDetail_transaction()
					                   .getReversal_indicator_X001().trim());
			
			iso.setISO_012_LocalDatetime(FormatUtils.StringToDate("20" + trxTransferenciaRequest.getStandard_request_fields().getXact_source_information()
					                     .getSource_date_9006() +
					                     trxTransferenciaRequest.getStandard_request_fields().getXact_source_information().getSource_time_9006(),
					                     "yyyyMMddHHmmss"));
			iso.setISO_042_Card_Acc_ID_Code(trxTransferenciaRequest.getStandard_request_fields().getXact_source_information()
					                     .getSource_branch_number_9004().trim());
			
			iso.setISO_043_CardAcceptorLoc(trxTransferenciaRequest.getMessage_terminal_number_9005().trim());
			
			iso.setISO_041_CardAcceptorID(trxTransferenciaRequest.getStandard_request_fields().getXact_source_information()
					                     .getTrf_terminal_type_9001());
			iso.setISO_015_SettlementDatel(FormatUtils.StringToDate("20" + trxTransferenciaRequest.getStandard_request_fields().getXact_source_information()
					                     .getSource_business_date_9006(),"yyyyMMdd"));
			
			iso.setISO_018_MerchantType("0007");//Switch Externo
			iso.setISO_024_NetworkId("555222"); //Red Pago Directo
			
			iso.setISO_032_ACQInsID(trxTransferenciaRequest.getMessage_fi_number_9004().trim());//???
			iso.setISO_033_FWDInsID(trxTransferenciaRequest.getStandard_request_fields().getTarget_institution_group().getAuth_fi_nbr_9004().trim()); //???
			iso.setISO_013_LocalDate(FormatUtils.StringToDate("20" + trxTransferenciaRequest.getStandard_request_fields().getTarget_institution_group()
									.getHost_business_date_9006(),"yyyyMMdd"));
			/*Se concatena toda la estructura STANDIN-GROUP*/
			iso.setISO_114_ExtendedData(trxTransferenciaRequest.getStandard_request_fields().getStand_in_group().getStandin_auth_type_X001().trim() + "|" +
										trxTransferenciaRequest.getStandard_request_fields().getStand_in_group().getStandin_auth_metod_X001().trim() + "|" +
										trxTransferenciaRequest.getStandard_request_fields().getStand_in_group().getStandin_result_code_9004().trim());
			
			iso.setISO_052_PinBlock(trxTransferenciaRequest.getStandard_request_fields().getCard_related_group().getPin_verify_flag_X001().trim());
			iso.setISO_055_EMV(trxTransferenciaRequest.getStandard_request_fields().getCard_related_group().getTrack_II_valid_flag_X001().trim());
			
			/*Se concatentan de la estrictura CARD-RELATED-GROUP, los campos RED-TARGET-PHONE, RED-APPL-TYPE-SOURCE,
			 * RED-FLG-REV-PD, RED-FILLER y CARD-APPL-CODE*/
			
			iso.setISO_115_ExtendedData(trxTransferenciaRequest.getStandard_request_fields().getCard_related_group().getRed_target_phone_9010().trim() + "|" +
										trxTransferenciaRequest.getStandard_request_fields().getCard_related_group().getRed_appl_type_source_9001().trim() + "|" +
										trxTransferenciaRequest.getStandard_request_fields().getCard_related_group().getRed_flg_red_pd_X002().trim() + "|" +
										trxTransferenciaRequest.getStandard_request_fields().getCard_related_group().getRed_filler_X_003().trim() + "|" + 
										trxTransferenciaRequest.getStandard_request_fields().getCard_related_group().getCard_appl_code_X002().trim());
			iso.setISO_035_Track2(trxTransferenciaRequest.getStandard_request_fields().getCard_related_group()
								 .getTrack_2_data_X040().trim());
			
			
			iso.setISO_034_PANExt(trxTransferenciaRequest.getStandard_request_fields().getXact_source_information()
								 .getTrf_source_name_X031().trim());
			
			iso.setISO_036_Track3(trxTransferenciaRequest.getUser_request_defined_data().getReqpdr_target_name_X035().trim());
			
			iso.setISO_102_AccountID_1(String.valueOf(Long.parseLong(trxTransferenciaRequest.getStandard_request_fields().getDetail_transaction().getTrans_acct_nbr_9018().trim())));
			
			iso.setISO_103_AccountID_2(String.valueOf(Long.parseLong(trxTransferenciaRequest.getDependent_transaction_request_fields().getTransfer_data()
									   .getOther_acct_9018().trim())));
			
			iso.setISO_120_ExtendedData(trxTransferenciaRequest.getUser_request_defined_data().getReqpdr_observations_X010().trim());
			
			iso.setISO_002_PAN(trxTransferenciaRequest.getUser_request_defined_data().getReqpdr_source_id_X013().trim());
			
			iso.setISO_023_CardSeq(trxTransferenciaRequest.getUser_request_defined_data().getReqpdr_target_id_X013().trim());
			
			iso.setISO_121_ExtendedData(trxTransferenciaRequest.getUser_request_defined_data().getReqpdr_city_X010().trim());
			
			iso.setISO_122_ExtendedData(trxTransferenciaRequest.getUser_request_defined_data().getReqpdr_reference_X020().trim());
			
			iso.setISO_123_ExtendedData(trxTransferenciaRequest.getUser_request_defined_data().getFiller_X006().trim());
			
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::convertTrxTransferenciaPagoDirectoRequestToIso8583", TypeMonitor.error, e);
		}
		
		return iso;
	}
	
    /*--------------------------------------------------------------------------------------------*/
	public byte[] TrxMessageControlResponseToBytes(TrxMessageControlResponse message) {
		
		TrxMessageControlResponse res = new TrxMessageControlResponse();
		message.getMessage_control_group().setMessage_type_code_X002(TypeMessageCode.AC);
		try {
			
			byte[] response = res.convertTrxMessageControlResponseToBytes(message);
			if(response != null) {
				
				this.codError = "000";
				return response;
				
			}else {
				
				this.codError = "909";
				this.desError = "ERROR EN PROCESOS, BYTE ARRAY RESPUESTA ES NULO";
				return null;
			}
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::TrxMessageControlResponseToBytes", TypeMonitor.error, e);
			return null;
		}
	}
	
	public byte[]  TrxConsultaPagoDirectoRequestToByte(TrxConsultaPagoDirectoRequest trx) {
		
		TrxConsultaPagoDirectoRequest req = new TrxConsultaPagoDirectoRequest();
		try {
			
			byte[] response = req.TrxConsultaPagoDirectoRequestToByteArray(trx);
			if(response != null) {
				
				this.codError = "000";
				return response;
				
			}else {
				
				this.codError = "909";
				this.desError = "ERROR EN PROCESOS, BYTE ARRAY RESPUESTA ES NULO";
				return null;
			}
			
		} catch (Exception e) {
		
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::TrxConsultaPagoDirectoRequestToByte", TypeMonitor.error, e);
			return null;
		}
	}
	
	public byte[]  TrxConsultaPagoDirectoResponseToByte(TrxConsultaPagoDirectoResponse trx) {
		
		TrxConsultaPagoDirectoResponse res = new TrxConsultaPagoDirectoResponse();
		try {
			
			byte[] response = res.TrxConsultaPagoDirectoResponseToByteArray(trx);
			if(response != null) {
				
				this.codError = "000";
				return response;
				
			}else {
				
				this.codError = "909";
				this.desError = "ERROR EN PROCESOS, BYTE ARRAY RESPUESTA ES NULO";
				return null;
			}
			
		} catch (Exception e) {
		
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::TrxConsultaPagoDirectoResponseToByte", TypeMonitor.error, e);
			return null;
		}
	}
	
	public byte[]  TrxTransferenciaPagoDirectoResponseToByte(TrxTransferenciaPagoDirectoResponse trx) {
		
		TrxTransferenciaPagoDirectoResponse res = new TrxTransferenciaPagoDirectoResponse();
		try {
			
			byte[] response = res.TrxTransferenciaPagoDirectoResponseToByteArray(trx);
			if(response != null) {
				
				this.codError = "000";
				return response;
				
			}else {
				
				this.codError = "909";
				this.desError = "ERROR EN PROCESOS, BYTE ARRAY RESPUESTA ES NULO";
				return null;
			}
			
		} catch (Exception e) {
		
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::TrxTransferenciaPagoDirectoResponseToByte", TypeMonitor.error, e);
			return null;
		}
	}
	
	public byte[]  TrxTransferenciaPagoDirectoRequestToByte(TrxTransferenciaPagoDirectoRequest trx) {
		
		TrxTransferenciaPagoDirectoRequest req = new TrxTransferenciaPagoDirectoRequest();
		try {
			
			byte[] response = req.TrxTransferenciaPagoDirectoRequestToByteArray(trx);
			if(response != null) {
				
				this.codError = "000";
				return response;
				
			}else {
				
				this.codError = "909";
				this.desError = "ERROR EN PROCESOS, BYTE ARRAY RESPUESTA ES NULO";
				return null;
			}
			
		} catch (Exception e) {
		
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::TrxTransferenciaPagoDirectoRequestToByte", TypeMonitor.error, e);
			return null;
		}
	}
	
	/*-----------------------------------------------------------------------------------------------------------------------------------------*/
	public TrxConsultaPagoDirectoResponse Iso8583ToTrxConsultaPagoDirectoResponse(TrxConsultaPagoDirectoRequest trxRq, Iso8583 isoResponse) {
		
		TrxConsultaPagoDirectoResponse res = null;
		try {
			
			res = new TrxConsultaPagoDirectoResponse();
			
			/*Seccion MessageControlGroup*/
			res.setMessage_type_code_X002(TypeMessageCode.TC);
			res.setMessage_fi_number_9004(trxRq.getMessage_fi_number_9004());
			res.setMessage_terminal_number_9005(trxRq.getMessage_terminal_number_9005());
			res.setSession_sequence_number_9006(trxRq.getSession_sequence_number_9006());
			res.setMessage_sequence_number_9004(trxRq.getMessage_sequence_number_9004());
			res.setTransaction_code_S9004(trxRq.getTransaction_code_S9004());
			
			res.setResultCode_9004(HomologaErrorIso8583ToOnTwo(isoResponse));
			
			/* Seccion STANDARD-REPLY-FIELDS.*/
			StandardReplyFields stand = new StandardReplyFields();
			stand.setAcct_1_info_flag_X001("'");
			stand.setHost_data_info_flag_X001("!");
			stand.setAcct_1_nbr_9018(isoResponse.getISO_102_AccountID_1());
			//omito ACCT-1-AVAIL-BAL
			//omito ACCT-1-CURR-BAL
			stand.setAcct_1_appl_code_9002(trxRq.getStandard_request_fields().getCard_related_group()
										  .getRed_appl_type_source_9001());
			stand.setTrf_terminal_type_9001(trxRq.getStandard_request_fields().getXact_source_information()
					                      .getTrf_terminal_type_9001());
			stand.setTrf_source_name_X031(trxRq.getStandard_request_fields().getXact_source_information()
										  .getTrf_source_name_X031());
			
			
			/*Seccion USER-REPLY-DEFINED-DATA*/
			UserRepplyDefinedData user = new UserRepplyDefinedData();
			user.setReppdr_acct_type_9002("00"); //Opcional ?????
			user.setReppdr_acct_number_9018(isoResponse.getISO_102_AccountID_1());		
			user.setReppdr_account_name_X040(isoResponse.getISO_036_Track3());
			user.setReppdr_min_payment_9008(NumbersUtils.DecimalToStringNumberIso(isoResponse.getISO_006_BillAmount(), 8));
			user.setReppdr_tot_payment_9008(NumbersUtils.DecimalToStringNumberIso(isoResponse.getISO_008_BillFeeAmount(), 8));
			
			String fecLimitePayment = FormatUtils.DateToString(isoResponse.getISO_013_LocalDate(), "yyyyMMdd");
			if(!fecLimitePayment.equals("19801129"))
				user.setReppdr_limit_date_9008(fecLimitePayment);
			user.setReppdr_target_phone_9010(trxRq.getStandard_request_fields().getCard_related_group()
											 .getRed_target_phone_9010());
			user.setReppdr_target_id_X013(isoResponse.getISO_023_CardSeq());
			
			res.setStandard_reply_fields(stand);
			res.setUser_repply_defined_data(user);
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::TrxConsultaPagoDirectoRequestToByte", TypeMonitor.error, e);
			return null;
		}
		
		return res;
	}
	
    public TrxTransferenciaPagoDirectoResponse Iso8583ToTrxTransferenciaPagoDirectoResponse(TrxTransferenciaPagoDirectoRequest trxRq, Iso8583 isoResponse) {
		
    	TrxTransferenciaPagoDirectoResponse res = null;
		try {
			
			res = new TrxTransferenciaPagoDirectoResponse();
			
			/*Seccion MessageControlGroup*/
			if(isoResponse.getISO_000_Message_Type().startsWith("12"))
				res.setMessage_type_code_X002(TypeMessageCode.TC);
			else {
				if(trxRq.getMessage_type_code_X002() == TypeMessageCode.XR)
					res.setMessage_type_code_X002(TypeMessageCode.XC);
				else
					res.setMessage_type_code_X002(TypeMessageCode.FC);
			}
			
			res.setMessage_fi_number_9004(trxRq.getMessage_fi_number_9004());
			res.setMessage_terminal_number_9005(trxRq.getMessage_terminal_number_9005());
			res.setSession_sequence_number_9006(trxRq.getSession_sequence_number_9006());
			res.setMessage_sequence_number_9004(trxRq.getMessage_sequence_number_9004());
			res.setTransaction_code_S9004(trxRq.getTransaction_code_S9004());
			
			res.setResult_code_9004(HomologaErrorIso8583ToOnTwo(isoResponse));
			
			/* Seccion STANDARD-REPLY-FIELDS.*/
			StandardReplyFields stand = new StandardReplyFields();
			
			/*Reversos condicionales*/
			if(trxRq.getMessage_type_code_X002() == TypeMessageCode.FC) {
				
				stand.setAcct_1_info_flag_X001("#");
				stand.setHost_data_info_flag_X001("\"");
			}else {
				stand.setAcct_1_info_flag_X001("'");
				stand.setHost_data_info_flag_X001("!");
			}
			
			stand.setAcct_1_nbr_9018(isoResponse.getISO_102_AccountID_1());
			//omito ACCT-1-AVAIL-BAL
			//omito ACCT-1-CURR-BAL
			stand.setAcct_1_appl_code_9002(trxRq.getStandard_request_fields().getCard_related_group()
										  .getRed_appl_type_source_9001());
			stand.setTrf_terminal_type_9001(trxRq.getStandard_request_fields().getXact_source_information()
					                      .getTrf_terminal_type_9001());
			stand.setTrf_source_name_X031(trxRq.getStandard_request_fields().getXact_source_information()
										  .getTrf_source_name_X031());
			
			/*TRANSACTION-DEPENDENT-REPLY-FIELDS.
			 * DEPENDEND-DATA.*/
			
			DependentTransactionReplyFields depTrx = new DependentTransactionReplyFields();
			DataDependend datDepend = new DataDependend();
			datDepend.setAcct_2_nbr_9018(isoResponse.getISO_103_AccountID_2());
			//ACCT-2-AVAIL-BAL //omito por manual valor siempre en 0
			//ACCT-2-CURR-BAL //omito por manual valor siempre en 0
			//Tipo de la cuenta destino
			switch (isoResponse.getISO_003_ProcessingCode().substring(4, 6)) {
			case "10":
				datDepend.setAcct_2_appl_code_9002("05");
				break;
			case "20":
				datDepend.setAcct_2_appl_code_9002("04");
				break;
			case "30":
				datDepend.setAcct_2_appl_code_9002("02"); //No esta en el manual pero aplica
				break;
			default:
				break;
			}
				
			depTrx.setDependend_data(datDepend);
			
			/*Seccion USER-REPLY-DEFINED-DATA*/
			UserRepplyDefinedData user = new UserRepplyDefinedData();
			user.setReppdr_acct_type_9002("00"); //Opcional ?????
			user.setReppdr_acct_number_9018(isoResponse.getISO_102_AccountID_1());		
			user.setReppdr_account_name_X040(isoResponse.getISO_036_Track3());
			user.setReppdr_min_payment_9008(NumbersUtils.DecimalToStringNumberIso(isoResponse.getISO_006_BillAmount(), 8));
			user.setReppdr_tot_payment_9008(NumbersUtils.DecimalToStringNumberIso(isoResponse.getISO_008_BillFeeAmount(), 8));
			
			String fecLimitePayment = FormatUtils.DateToString(isoResponse.getISO_013_LocalDate(), "yyyyMMdd");
			if(!fecLimitePayment.equals("19801129"))
				user.setReppdr_limit_date_9008(fecLimitePayment);
			user.setReppdr_target_phone_9010(trxRq.getStandard_request_fields().getCard_related_group()
											 .getRed_target_phone_9010());
			user.setReppdr_target_id_X013(isoResponse.getISO_023_CardSeq());
			
			res.setStandar_reply_fields(stand);
			res.setUser_repply_defined_data(user);
			res.setTransaction_dependent_reply_fields(depTrx);
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::Iso8583ToTrxTransferenciaPagoDirectoResponse", TypeMonitor.error, e);
			return null;
		}
		
		return res;
	}
	
    /*-----------------------------------------------------------------------------------------------------------------------------------------*/
    
    public TrxConsultaPagoDirectoResponse bytesToTrxConsultaPagoDirectoResponse(byte[] message) {
    	
    	TrxConsultaPagoDirectoResponse trx = null;
		try {
			
			trx = new TrxConsultaPagoDirectoResponse();
			trx = trx.transformBytesArrayToTrxConsultaPagoDirectoResponse(message);
			if(trx == null) {
				
				this.codError = "908";
				this.desError = "ERROR AL PARSEAR A ESTRUCTURA [TrxConsultaPagoDirectoResponse]";
			}else {
				
				this.codError = "000";
			}
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::bytesToTrxConsultaPagoDirectoResponse", TypeMonitor.error, e);
		}
		
		return trx;
    }
    
    public TrxTransferenciaPagoDirectoResponse bytesToTrxTransferenciaPagoDirectoResponse(byte[] message) {
    	
    	TrxTransferenciaPagoDirectoResponse trx = null;
		try {
			
			trx = new TrxTransferenciaPagoDirectoResponse();
			trx = trx.transformBytesArrayToTrxTransferenciaPagoDirectoResponse(message);
			if(trx == null) {
				
				this.codError = "908";
				this.desError = "ERROR AL PARSEAR A ESTRUCTURA [TrxTransferenciaPagoDirectoResponse]";
			}else {
				
				this.codError = "000";
			}
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS,", e, false);
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::bytesToTrxTransferenciaPagoDirectoResponse", TypeMonitor.error, e);
		}
		
		return trx;
    }
    
	public String HomologaErrorIso8583ToOnTwo(Iso8583 iso) {
		
		String codError = StringUtils.Empty();
		try {
			
			switch (iso.getISO_039_ResponseCode()) {
			case "000":
				codError = "0000";
				break;
			case "217": //Problemas con el estado de la cuenta
				codError = "8338";
				break;
			case "114":
			case "300": //Tipos de Cuenta no coinciden
				codError = "8359";
				break;
			case "601"://Transaccion a reversar no existe
				codError = "8410";
				break;
			case "606": //Reverso ya efectuado
				codError = "8411";
				break;
			case "115": //La cedula receptora no coincide con la registrada
				codError = "8408";
				break;
			case "907": //TimeOuts
				codError = "7803";
			case "906": //Validaciones de campos
				codError = "8510";
				break;
			case "909":
			case "908":
				codError = "8510";
				break;
			default:
				codError = "8510";
				break;
			}
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::HomologaErrorIso8583ToOnTwo", TypeMonitor.error, e);
		}
		return codError;
	}
	
	public String HomologaErrorOnTwoToIso8583(String codErrorIso) {
		
		String codError = StringUtils.Empty();
		try {
			
			switch (codErrorIso) {
			case "0000":
				codError = "000|TRANSACCION EXITOSA";
				break;
			case "8330": 
				codError = "907|TRANSACCION NO PROCESADA, RE-INTENTE EN OTRO BANCO DE LA RED";
				break;
			case "8365": 
				codError = "907|SU BANCO TEMPORALMENTE ESTA DESCONECTADO DE LA RED, RE-INTENTE EN UNOS MINUTOS";
				break;
			case "8370":
			case "7506": 
				codError = "907|SWITCH NO DISPONIBLE POR EL MOMENTO";
				break;
			case "7508": 
				codError = "301|CODIGO DE APLICACION NO DEFINIDO";
				break;
			case "7516":
				codError = "909|ERROR EN PROCESOS, RE-INTENTE NUEVAMENTE";
				break;
			case "8038": 
				codError = "906|DATOS INGRESADOS INCORRECTOS, RE-INTENTE NUEVAMENET";
				break;
			case "8371": 
				codError = "905|LA FECHA DEL TERMINAL ES INCORRECTA";
			case "8410": 
				codError = "601|TRANSACCION A REVERSAR NO EXISTE";
				break;
			case "8411": 
				codError = "606|TRANSACCION ORIGINAL YA REVERSADA";
				break;
			case "8418": 
				codError = "904|CODIGO FI NO EXISTENTE";
				break;
			case "8507": 
				codError = "101|TRANSACCION NO PROCESADA, ERROR EN LA TARJETA, CONSULTE A SU BANCO";
				break;
			case "8510":
				codError = "308|TRANSACCION NO PROCESADA POR SU BANCO, FAVOR CONSULTE CON EL MISMO";
				break;
			default:
				codError = "308|TRANSACCION NO PUEDE SER PROCESADA";
				break;
			}
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::HomologaErrorOnTwoToIso8583", TypeMonitor.error, e);
		}
		return codError;
	}
	
}
