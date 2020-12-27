package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.ResponseBynary;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.NumbersUtils;
import com.belejanor.switcher.utils.Ref;

@XmlRootElement
@SuppressWarnings("serial")
@XmlType(propOrder={"message_type_code_X002", "message_fi_number_9004", "message_terminal_number_9005",
		"session_sequence_number_9006","message_sequence_number_9004", "transaction_code_S9004",
		 "standard_request_fields","dependent_transaction_request_fields","user_request_defined_data"})
public class TrxTransferenciaPagoDirectoRequest extends MessageControlGroup 
												implements Serializable{

	public TrxTransferenciaPagoDirectoRequest() {
		
		super();
	}
	
	private StandarRequestFieldsTransfers  standard_request_fields;
	private DependentTransactionRequestFields dependent_transaction_request_fields;
	private UserRequestDefinedData 		   user_request_defined_data;
	
	public StandarRequestFieldsTransfers getStandard_request_fields() {
		return standard_request_fields;
	}
	
	public void setStandard_request_fields(StandarRequestFieldsTransfers standard_request_fields) {
		this.standard_request_fields = standard_request_fields;
	}

	public DependentTransactionRequestFields getDependent_transaction_request_fields() {
		return dependent_transaction_request_fields;
	}

	public void setDependent_transaction_request_fields(
			DependentTransactionRequestFields dependent_transaction_request_fields) {
		this.dependent_transaction_request_fields = dependent_transaction_request_fields;
	}

	public UserRequestDefinedData getUser_request_defined_data() {
		return user_request_defined_data;
	}

	public void setUser_request_defined_data(UserRequestDefinedData user_request_defined_data) {
		this.user_request_defined_data = user_request_defined_data;
	}


	/*---**--ORDENANTE*/
    public byte[] TrxTransferenciaPagoDirectoRequestToByteArray(TrxTransferenciaPagoDirectoRequest trx) {
		
		byte[] response = new byte[40960];
		Logger log;
		int offset = 0;
		ResponseBynary bin = new ResponseBynary();
		bin.setBuffer(response);
		bin.setOffset(offset);
		Ref<ResponseBynary> ref = new Ref<ResponseBynary>(bin);
		try {
			
			/*Seccion Message Control*/
			GeneralUtils.ReadFixStringReverse(trx.getMessage_type_code_X002().name(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getMessage_fi_number_9004(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getMessage_terminal_number_9005(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getSession_sequence_number_9006(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getMessage_sequence_number_9004(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getTransaction_code_S9004().toString(), ref);
			/*Seccion STANDARD-REQUEST-FIELDS*/
			/*XACT-SOURCE-INFORMATION*/
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getXact_source_information().getSource_date_9006(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getXact_source_information().getSource_time_9006(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getXact_source_information().getSource_aba_number_9010(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getXact_source_information().getSource_branch_number_9004(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getXact_source_information().getSource_business_date_9006(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getXact_source_information().getTrf_terminal_type_9001(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getXact_source_information().getTrf_source_name_X031(), ref);
			/*DETAIL-TRANSACTION*/
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getDetail_transaction().getForce_post_indicator_X001(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getDetail_transaction().getReversal_indicator_X001(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getDetail_transaction().getTrans_acct_nbr_9018(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getDetail_transaction().getTransaction_amount_S9_018(), ref);
			/*TARGET-INSTITUTION-GROUP*/
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getTarget_institution_group().getAuth_fi_nbr_9004(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getTarget_institution_group().getHost_business_date_9006(), ref);
			/*STANDIN-GROUP*/
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getStand_in_group().getStandin_auth_type_X001(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getStand_in_group().getStandin_auth_metod_X001(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getStand_in_group().getStandin_result_code_9004(), ref);
			/*CARD-RELATED-GROUP*/
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getCard_related_group().getPin_verify_flag_X001(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getCard_related_group().getTrack_II_valid_flag_X001(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getCard_related_group().getRed_target_phone_9010(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getCard_related_group().getRed_appl_type_source_9001(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getCard_related_group().getRed_flg_red_pd_X002(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getCard_related_group().getRed_filler_X_003(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getCard_related_group().getTrack_2_data_X040(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_request_fields().getCard_related_group().getCard_appl_code_X002(), ref);
			
			/*TRANSACTION-DEPENDENT-FIELDS.*/
			/*TRANSFER-DATA.*/
			GeneralUtils.ReadFixStringReverse(trx.getDependent_transaction_request_fields().getTransfer_data().getOther_fi_9004(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getDependent_transaction_request_fields().getTransfer_data().getOther_appl_9002(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getDependent_transaction_request_fields().getTransfer_data().getOther_acct_9018(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getDependent_transaction_request_fields().getTransfer_data().getInternal_ind_X001(), ref);
			
			/*Si es reverso*/
			if(trx.getMessage_type_code_X002() == TypeMessageCode.FR || 
					trx.getMessage_type_code_X002() == TypeMessageCode.XR) {
				GeneralUtils.ReadFixStringReverse(trx.getDependent_transaction_request_fields().getReversal_data().getFinal_amount_S09018(), ref);
			}
			
			/*USER-REQUEST-DEFINED-DATA*/
			GeneralUtils.ReadFixStringReverse(trx.getUser_request_defined_data().getReqpdr_target_name_X035(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_request_defined_data().getReqpdr_observations_X010(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_request_defined_data().getReqpdr_source_id_X013(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_request_defined_data().getReqpdr_city_X010(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_request_defined_data().getReqpdr_reference_X020(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_request_defined_data().getReqpdr_target_id_X013(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_request_defined_data().getFiller_X006(), ref);
			
			int totalOffset = ref.get().getOffset();
			byte[] bufferTotal = new byte[totalOffset];
	        System.arraycopy(ref.get().getBuffer(), 0, bufferTotal, 0, totalOffset);
	        return bufferTotal;
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::TrxTransferenciaPagoDirectoRequestToByteArray"
					, TypeMonitor.error, e);
			return null;
		}
	}
    
    /*---**--ORDENANTE*/
    public TrxTransferenciaPagoDirectoRequest Iso8583ToTrxTransferenciaPagoDirectoRequest(Iso8583 iso) {
		Logger log;
		TrxTransferenciaPagoDirectoRequest trx = null;
		try {
			
			trx = new TrxTransferenciaPagoDirectoRequest(); 
			/*MessageGroup*/
			
			if(iso.getISO_000_Message_Type().startsWith("12"))
				trx.setMessage_type_code_X002(TypeMessageCode.TR);
			else
				trx.setMessage_type_code_X002(TypeMessageCode.XR);
			trx.setMessage_fi_number_9004(iso.getISO_032_ACQInsID());
			
			if(iso.getISO_003_ProcessingCode().substring(0, 1).equals("3"))
				trx.setMessage_sequence_number_9004("0001");
			else if (iso.getISO_000_Message_Type().startsWith("14")) {
				trx.setMessage_sequence_number_9004("0001");
			}else
				trx.setMessage_sequence_number_9004("0002");
			
			trx.setMessage_terminal_number_9005(iso.getISO_042_Card_Acc_ID_Code() + iso.getISO_043_CardAcceptorLoc());
			trx.setSession_sequence_number_9006(GeneralUtils.GetSecuencialNumeric(6));
			
			if(iso.getISO_003_ProcessingCode().substring(0, 1).equals("3"))
				trx.setTransaction_code_S9004(TypeTransactionCode.CONSULTA_PAGO_DIRECTO);
			else {
				switch (iso.getISO_003_ProcessingCode()) {
				case "011010":
						if(iso.getISO_000_Message_Type().startsWith("12"))
							trx.setTransaction_code_S9004(TypeTransactionCode.TRANSFERENCIA_CTA_AHORROS);
						else
							trx.setTransaction_code_S9004(TypeTransactionCode.REVERSO_REAL_TRANSFER_CTA_AHO);
				break;
				case "011020":
						if(iso.getISO_000_Message_Type().startsWith("12"))
							trx.setTransaction_code_S9004(TypeTransactionCode.TRANSFERENCIA_CTA_CORRIENTE);
						else
							trx.setTransaction_code_S9004(TypeTransactionCode.REVERSO_REAL_TRANSFER_CTA_CORR);
				break;
				case "011030":
					if(iso.getISO_000_Message_Type().startsWith("12"))
						trx.setTransaction_code_S9004(TypeTransactionCode.PAGO_TARJETA_CREDITO);
					else
						trx.setTransaction_code_S9004(TypeTransactionCode.REVERSO_REAL_TRANSFER_TRJ_CRED);
				break;

				default:
					break;
				}
			}
			
			/*Estructura XactSourceInformation*/
			XactSourceInformation xact = new XactSourceInformation();
			xact.setSource_date_9006(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "yyMMdd"));
			xact.setSource_time_9006(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "HHmmss"));
			xact.setSource_aba_number_9010(MemoryGlobal.BanredPagoDirectoAbaOrdenante); //aba ORDENANTE
			xact.setSource_branch_number_9004(iso.getISO_042_Card_Acc_ID_Code()); //se puede omitir
			xact.setSource_business_date_9006(FormatUtils.DateToString(iso.getISO_015_SettlementDatel(), "yyMMdd"));
			switch (iso.getISO_018_MerchantType()) {
			case "0005":
				xact.setTrf_terminal_type_9001("1"); //1 es Ventanilla
				break;
			case "0000":
				xact.setTrf_terminal_type_9001("0"); //1 es Atms
				break;
			case "0004":
				xact.setTrf_terminal_type_9001("2"); //1 es Web
				break;
			default:
				xact.setTrf_terminal_type_9001("6"); //1 es Otros
				break;
			}
			xact.setTrf_source_name_X031(iso.getISO_034_PANExt());
			
			/*Estructura DetailTransaction*/
			DetailTransaction detail = new DetailTransaction();
			detail.setForce_post_indicator_X001("0"); //Siempre
			if(iso.getISO_000_Message_Type().startsWith("12"))
				detail.setReversal_indicator_X001("0"); //0 no es reverso, 1 si lo es
			else
				detail.setReversal_indicator_X001("1");
			
			//Validacion si es tarjeta (se enciara en el ISO en el 103 y 35 track 2 el numero de tarjeta)
				detail.setTrans_acct_nbr_9018(iso.getISO_103_AccountID_2()); //Cta RECEPTORA OJO
			
			detail.setTransaction_amount_S9_018(NumbersUtils.DecimalToStringNumberIso(iso.getISO_004_AmountTransaction(), 17) + "+"); 
			
			/*Estructura TargetInstitutionGroup*/
			TargetInstitutionGroup target = new TargetInstitutionGroup();
			target.setAuth_fi_nbr_9004(iso.getISO_033_FWDInsID()); //codigo de la institucion duenia de la cuenta (Receptora aut)
			target.setHost_business_date_9006(FormatUtils.DateToString(iso.getISO_015_SettlementDatel(), "yyMMdd")); //fecha contable del host receptor
			
			/*Estructura StandInGroup*/
			StandInGroup stand = new StandInGroup();
			stand.setStandin_auth_type_X001("0"); //0 siempre 0 en consultas OJO se puede omitir
			stand.setStandin_auth_metod_X001("4"); //Preguntar 1 primaria o 4 en el host ????????????????
			stand.setStandin_result_code_9004("0000"); //Preguntar si es asi se piuede omitir???????????
			
			/*Estructura StandInGroup*/
			CardRelatedGroup card = new CardRelatedGroup();
			card.setPin_verify_flag_X001("0"); //Enviar 0 no valida PIN
			card.setTrack_II_valid_flag_X001("1"); //Enviar 1 si hay data (siempre 1 ojo)
			card.setRed_target_phone_9010("0000000000"); //se puede omitir (celular de la persona)
			
			//Tipo de cuenta del Ordenante
			switch (iso.getISO_003_ProcessingCode().substring(2, 4)) {
			case "10":
				card.setRed_appl_type_source_9001("5");
				break;
			case "20":
				card.setRed_appl_type_source_9001("4");	
				break;
			default:
				break;
			}			
			card.setRed_flg_red_pd_X002("PD"); //Valor fijo PD
			
			//BIN + 0000000000=000000000000000000000?
			//Si es consulta Tarjeta de credito
			if(iso.getISO_003_ProcessingCode().substring(4, 6).equals("30"))
				//iso.getISO_035_Track2() = NUMERO DE TARJETA DE 16
				card.setTrack_2_data_X040(";"+ iso.getISO_103_AccountID_2()+ "=000000000000000000000?");
			else
				// iiso.getISO_BitMap() = ABA RECEPTOR(6)
				card.setTrack_2_data_X040(";"+ iso.getISO_BitMap() + FormatUtils.filledWhitZeroes(10) + "=000000000000000000000?");	
			
			/*TIPO DE CUENTA RECEPTOR*/
			if(iso.getISO_003_ProcessingCode().substring(4,6).equals("10"))
				card.setCard_appl_code_X002("05");  //02 Tarjeta de Credito, 05 cuenta de ahorros, 04 corr //OJO ordenante
			else if (iso.getISO_003_ProcessingCode().substring(4,6).equals("20")) {
				card.setCard_appl_code_X002("04");
			}else {
				card.setCard_appl_code_X002("02");
			}
				
			/*Estructura Transaction Dependent Fields
			 * Transfer Data*/
			DependentTransactionRequestFields dtDep = new DependentTransactionRequestFields();
			DataTransfer dtrans = new DataTransfer();
			//CODIGO FI RECEPTOR
			dtrans.setOther_fi_9004(iso.getISO_033_FWDInsID());
			
			/*SI ES REVERSO*/
			ReversalData reversalData = null;
			if(iso.getISO_000_Message_Type().startsWith("14")) {
				reversalData = new ReversalData();
				reversalData.setFinal_amount_S09018(NumbersUtils.DecimalToStringNumberIso(iso.getISO_004_AmountTransaction(), 17) + "+");
			}
			
			
			switch (iso.getISO_003_ProcessingCode().substring(4,6)) { //Tipo de cuenta Receptora
			case "10":
				dtrans.setOther_appl_9002("05");
				dtrans.setInternal_ind_X001("1");
				break;
			case "20":
				dtrans.setOther_appl_9002("04");
				dtrans.setInternal_ind_X001("1");
				break;
			case "30":
				dtrans.setOther_appl_9002("02"); //AQUI SE EQUIVOCO EL MANUAL
				dtrans.setInternal_ind_X001("2");
				break;
			default:
				dtrans.setOther_appl_9002("06");
				dtrans.setInternal_ind_X001("4");
				break;
			}
			dtrans.setOther_acct_9018(iso.getISO_102_AccountID_1()); //Cuenta ORDENANTE
			dtDep.setTransfer_data(dtrans);
			dtDep.setReversal_data(reversalData);
			
			/*Estructura UserRequestDefinedData*/
			UserRequestDefinedData user = new UserRequestDefinedData();
			user.setReqpdr_target_name_X035(iso.getISO_036_Track3()); //Nombre del receptor
			user.setReqpdr_observations_X010(iso.getISO_120_ExtendedData());
			user.setReqpdr_source_id_X013(iso.getISO_002_PAN()); //Numero de cedula de la persona ORDENANTE
			user.setReqpdr_city_X010(iso.getISO_121_ExtendedData()); //La ciudad pero se manda 10 espacios en blaco, se puede omitir.
			user.setReqpdr_reference_X020(GeneralUtils.GetSecuencialNumeric(10) + "0000000000"); //Enviar siempre 20 carazcteres
			user.setReqpdr_target_id_X013(iso.getISO_023_CardSeq()); //cedula o ruc del RECEPTOR
			
			StandarRequestFieldsTransfers stndRq = new StandarRequestFieldsTransfers(xact, detail, target, stand, card);
			
			trx.setStandard_request_fields(stndRq);
			trx.setDependent_transaction_request_fields(dtDep);
			trx.setUser_request_defined_data(user);
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::Iso8583ToTrxTransferenciaPagoDirectoRequest"
					, TypeMonitor.error, e);
		}
		return trx;
	}
    
    /*---**--RECEPTOR*/
	public TrxTransferenciaPagoDirectoRequest byteArraytoStruct(byte[] message) {
		
		Logger log;
		TrxTransferenciaPagoDirectoRequest messageRequest = null;
		XactSourceInformation xact = null;
		DetailTransaction detail = null;
		TargetInstitutionGroup target = null;
		StandInGroup stand = null;
		CardRelatedGroup card = null;
		StandarRequestFieldsTransfers stndRq = null;
		DependentTransactionRequestFields depTrans = null;
		DataTransfer dataTrans = null;
		ReversalData reversalData = null;
		UserRequestDefinedData user = null;
		
		int offset = 0;
		Ref<Integer> ref = new Ref<Integer>(offset);
		try {
			
			messageRequest = new TrxTransferenciaPagoDirectoRequest();
			
			/*Estructura MessageControlGroup*/
			messageRequest.setMessage_type_code_X002(Enum.valueOf(TypeMessageCode.class,
					GeneralUtils.ReadFixString(message, ref, 2)));
			messageRequest.setMessage_fi_number_9004(GeneralUtils.ReadFixString(message, ref,4));
			messageRequest.setMessage_terminal_number_9005(GeneralUtils.ReadFixString(message, ref,5));
			messageRequest.setSession_sequence_number_9006(GeneralUtils.ReadFixString(message, ref,6));
			messageRequest.setMessage_sequence_number_9004(GeneralUtils.ReadFixString(message, ref,4));
			messageRequest.setTransaction_code_S9004(TypeTransactionCode.fromDisplayString( 
					    GeneralUtils.ReadFixString(message, ref,5)));
			
			/*Estructura XactSourceInformation*/
			xact = new XactSourceInformation();
			xact.setSource_date_9006(GeneralUtils.ReadFixString(message, ref,6));
			xact.setSource_time_9006(GeneralUtils.ReadFixString(message, ref,6));
			xact.setSource_aba_number_9010(GeneralUtils.ReadFixString(message, ref,10));
			xact.setSource_branch_number_9004(GeneralUtils.ReadFixString(message, ref,4));
			xact.setSource_business_date_9006(GeneralUtils.ReadFixString(message, ref,6));
			xact.setTrf_terminal_type_9001(GeneralUtils.ReadFixString(message, ref,1));
			xact.setTrf_source_name_X031(GeneralUtils.ReadFixString(message, ref,31));
			
			/*Estructura DetailTransaction*/
			detail = new DetailTransaction();
			detail.setForce_post_indicator_X001(GeneralUtils.ReadFixString(message, ref,1));
			detail.setReversal_indicator_X001(GeneralUtils.ReadFixString(message, ref,1));
			detail.setTrans_acct_nbr_9018(GeneralUtils.ReadFixString(message, ref,18));
			detail.setTransaction_amount_S9_018(GeneralUtils.ReadFixString(message, ref,18));
			
			/*Estructura TargetInstitutionGroup*/
			target = new TargetInstitutionGroup();
			target.setAuth_fi_nbr_9004(GeneralUtils.ReadFixString(message, ref,4));
			target.setHost_business_date_9006(GeneralUtils.ReadFixString(message, ref,6));
			
			/*Estructura StandInGroup*/
			stand = new StandInGroup();
			stand.setStandin_auth_type_X001(GeneralUtils.ReadFixString(message, ref,1));
			stand.setStandin_auth_metod_X001(GeneralUtils.ReadFixString(message, ref,1));
			stand.setStandin_result_code_9004(GeneralUtils.ReadFixString(message, ref,4));
			
			/*Estructura CardRelatedGroup*/
			card = new CardRelatedGroup();
			card.setPin_verify_flag_X001(GeneralUtils.ReadFixString(message, ref,1));
			card.setTrack_II_valid_flag_X001(GeneralUtils.ReadFixString(message, ref,1));
			card.setRed_target_phone_9010(GeneralUtils.ReadFixString(message, ref,10));
			card.setRed_appl_type_source_9001(GeneralUtils.ReadFixString(message, ref,1));
			card.setRed_flg_red_pd_X002(GeneralUtils.ReadFixString(message, ref,2));
			card.setRed_filler_X_003(GeneralUtils.ReadFixString(message, ref,3));
			card.setTrack_2_data_X040(GeneralUtils.ReadFixString(message, ref,40));
			card.setCard_appl_code_X002(GeneralUtils.ReadFixString(message, ref,2));
			
			
			/*Estructura constructor StandardRequestFieldsConsulta*/
			stndRq = new StandarRequestFieldsTransfers(xact, detail, target, stand, card); 
			
			/*Estructura TRANSACTION-DEPENDENT-FIELDS.
			 * TRANSFER DATA.*/
			depTrans = new DependentTransactionRequestFields();
			dataTrans = new DataTransfer();
			dataTrans.setOther_fi_9004(GeneralUtils.ReadFixString(message, ref,4));
			dataTrans.setOther_appl_9002(GeneralUtils.ReadFixString(message, ref,2));
			dataTrans.setOther_acct_9018(GeneralUtils.ReadFixString(message, ref,18));
			dataTrans.setInternal_ind_X001(GeneralUtils.ReadFixString(message, ref,1));
			
			
			/*Si es Reverso XR o FR*/ 
			if((message[0] == 0x58 || message[0] == 0x46) 
					&&  message[1] == 0x52) {
				
				reversalData = new ReversalData();
				reversalData.setFinal_amount_S09018(GeneralUtils.ReadFixString(message, ref,18));
			}
			
			depTrans.setTransfer_data(dataTrans);
			depTrans.setReversal_data(reversalData);
				
			/*Estructura UserRequestDefinedData*/
			user = new UserRequestDefinedData();
			user.setReqpdr_target_name_X035(GeneralUtils.ReadFixString(message, ref,35));
			user.setReqpdr_observations_X010(GeneralUtils.ReadFixString(message, ref,10));
			user.setReqpdr_source_id_X013(GeneralUtils.ReadFixString(message, ref,13));
			user.setReqpdr_city_X010(GeneralUtils.ReadFixString(message, ref,10));
			user.setReqpdr_reference_X020(GeneralUtils.ReadFixString(message, ref,20));
			user.setReqpdr_target_id_X013(GeneralUtils.ReadFixString(message, ref,13));
			user.setFiller_X006(GeneralUtils.ReadFixString(message, ref,6));
			
			/*Asignacion de clases a  TrxTransferenciaPagoDirectoRequest*/
			
			messageRequest.setStandard_request_fields(stndRq);
			messageRequest.setUser_request_defined_data(user);
			messageRequest.setDependent_transaction_request_fields(depTrans);
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::byteArraytoStruct"
					, TypeMonitor.error, e);
		}
		
		return messageRequest;
	}

}
