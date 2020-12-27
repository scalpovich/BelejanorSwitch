package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.cscoreswitch.ResponseBynary;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;


@XmlRootElement
@SuppressWarnings("serial")
@XmlType(propOrder={"message_type_code_X002", "message_fi_number_9004", "message_terminal_number_9005",
					"session_sequence_number_9006","message_sequence_number_9004", "transaction_code_S9004",
					"resultCode_9004","standard_reply_fields","user_repply_defined_data"})
public class TrxConsultaPagoDirectoResponse extends MessageControlGroup implements Serializable{

	private String resultCode_9004;
	private StandardReplyFields standard_reply_fields;
	private UserRepplyDefinedData user_repply_defined_data;
	
	public TrxConsultaPagoDirectoResponse() {
		
		this.resultCode_9004 = "7803";
	}
	
	public TrxConsultaPagoDirectoResponse(String resultCode) {
		
		this.resultCode_9004 = resultCode;
	}
	
	public String getResultCode_9004() {
		return resultCode_9004;
	}
	public void setResultCode_9004(String resultCode_9004) {
		this.resultCode_9004 = resultCode_9004;
	}
	public StandardReplyFields getStandard_reply_fields() {
		return standard_reply_fields;
	}
	public void setStandard_reply_fields(StandardReplyFields standard_reply_fields) {
		this.standard_reply_fields = standard_reply_fields;
	}
	public UserRepplyDefinedData getUser_repply_defined_data() {
		return user_repply_defined_data;
	}
	public void setUser_repply_defined_data(UserRepplyDefinedData user_repply_defined_data) {
		this.user_repply_defined_data = user_repply_defined_data;
	}
	
	public byte[] TrxConsultaPagoDirectoResponseToByteArray(TrxConsultaPagoDirectoResponse trx) {
		
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
			/*CODIGO RESPUESTA*/
			GeneralUtils.ReadFixStringReverse(trx.getResultCode_9004(), ref);
			
			/*STANDARD-REPLY-FIELDS.*/
			GeneralUtils.ReadFixStringReverse(trx.getStandard_reply_fields().getAcct_1_info_flag_X001(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_reply_fields().getHost_data_info_flag_X001(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_reply_fields().getAcct_1_nbr_9018(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_reply_fields().getAcct_1_avail_bal_S90018(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_reply_fields().getAcct_1_curr_bal_S90018(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_reply_fields().getAcct_1_appl_code_9002(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_reply_fields().getTrf_terminal_type_9001(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getStandard_reply_fields().getTrf_source_name_X031(), ref);
			
			/*USER-REPLY-DEFINED-DATA*/
			GeneralUtils.ReadFixStringReverse(trx.getUser_repply_defined_data().getReppdr_acct_type_9002(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_repply_defined_data().getReppdr_acct_number_9018(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_repply_defined_data().getReppdr_account_name_X040(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_repply_defined_data().getReppdr_min_payment_9008(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_repply_defined_data().getReppdr_tot_payment_9008(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_repply_defined_data().getReppdr_limit_date_9008(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_repply_defined_data().getReppdr_target_phone_9010(), ref);
			GeneralUtils.ReadFixStringReverse(trx.getUser_repply_defined_data().getReppdr_target_id_X013(), ref);
			
			int totalOffset = ref.get().getOffset();
			byte[] bufferTotal = new byte[totalOffset];
	        System.arraycopy(ref.get().getBuffer(), 0, bufferTotal, 0, totalOffset);
	        return bufferTotal;
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::TrxConsultaPagoDirectoResponseToByteArray"
					, TypeMonitor.error, e);
			return null;
		}
	}
	
	public TrxConsultaPagoDirectoResponse transformBytesArrayToTrxConsultaPagoDirectoResponse(byte[] message) {
		
		int offset = 0;
		Ref<Integer> ref = new Ref<Integer>(offset);
		Logger log;
		TrxConsultaPagoDirectoResponse msgResponse = null;
		StandardReplyFields replyFields = null;
		UserRepplyDefinedData user = null;
		try {
			
			msgResponse = new TrxConsultaPagoDirectoResponse();
			msgResponse.setMessage_type_code_X002(Enum.valueOf(TypeMessageCode.class,
					GeneralUtils.ReadFixString(message, ref, 2)));
			msgResponse.setMessage_fi_number_9004(GeneralUtils.ReadFixString(message, ref,4));
			msgResponse.setMessage_terminal_number_9005(GeneralUtils.ReadFixString(message, ref,5));
			msgResponse.setSession_sequence_number_9006(GeneralUtils.ReadFixString(message, ref,6));
			msgResponse.setMessage_sequence_number_9004(GeneralUtils.ReadFixString(message, ref,4));
			msgResponse.setTransaction_code_S9004(TypeTransactionCode.fromDisplayString( 
					    GeneralUtils.ReadFixString(message, ref,5)));
			
			
			/*Codigo de respuesta*/
			msgResponse.setResultCode_9004(GeneralUtils.ReadFixString(message, ref,4));
			
			/*Seccion STANDARD-REPLY-FIELDS.*/
			replyFields = new StandardReplyFields();
			replyFields.setAcct_1_info_flag_X001(GeneralUtils.ReadFixString(message, ref,1));
			replyFields.setHost_data_info_flag_X001(GeneralUtils.ReadFixString(message, ref,1));
			replyFields.setAcct_1_nbr_9018(GeneralUtils.ReadFixString(message, ref,18));
			replyFields.setAcct_1_avail_bal_S90018(GeneralUtils.ReadFixString(message, ref,18));
			replyFields.setAcct_1_curr_bal_S90018(GeneralUtils.ReadFixString(message, ref,18));
			replyFields.setAcct_1_appl_code_9002(GeneralUtils.ReadFixString(message, ref,2));
			replyFields.setTrf_terminal_type_9001(GeneralUtils.ReadFixString(message, ref,1));
			replyFields.setTrf_source_name_X031(GeneralUtils.ReadFixString(message, ref,31));
			
			/*Seccion USER-REPLY-DEFINED-DATA*/
			user = new UserRepplyDefinedData();
			user.setReppdr_acct_type_9002(GeneralUtils.ReadFixString(message, ref,2));
			user.setReppdr_acct_number_9018(GeneralUtils.ReadFixString(message, ref,18));
			user.setReppdr_account_name_X040(GeneralUtils.ReadFixString(message, ref,40));
			user.setReppdr_min_payment_9008(GeneralUtils.ReadFixString(message, ref,8));
			user.setReppdr_tot_payment_9008(GeneralUtils.ReadFixString(message, ref,8));
			user.setReppdr_limit_date_9008(GeneralUtils.ReadFixString(message, ref,8));
			user.setReppdr_target_phone_9010(GeneralUtils.ReadFixString(message, ref,10));
			user.setReppdr_target_id_X013(GeneralUtils.ReadFixString(message, ref,13));
			
			
			msgResponse.setStandard_reply_fields(replyFields);
			msgResponse.setUser_repply_defined_data(user);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::transformBytesArrayToTrxConsultaPagoDirectoResponse"
					, TypeMonitor.error, e);
			return null;
		}
		
		return msgResponse;
	}
}
