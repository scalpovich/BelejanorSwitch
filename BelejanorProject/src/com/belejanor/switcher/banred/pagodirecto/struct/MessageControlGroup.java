package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.StringUtils;

@SuppressWarnings("serial")
@XmlType(propOrder={"message_type_code_X002", "message_fi_number_9004", "message_terminal_number_9005",
					"session_sequence_number_9006","message_sequence_number_9004", "transaction_code_S9004"})
@XmlTransient
public class MessageControlGroup implements Serializable{

	private TypeMessageCode message_type_code_X002;
	private String message_fi_number_9004;
	private String message_terminal_number_9005;
	private String session_sequence_number_9006;
	private String message_sequence_number_9004;
	private TypeTransactionCode transaction_code_S9004;
	
	public MessageControlGroup() {
		
		this.message_type_code_X002 = TypeMessageCode.TC;
		this.message_fi_number_9004 = FormatUtils.filledWhitZeroes(4);
		this.message_terminal_number_9005 = FormatUtils.filledWhitZeroes(5);
		this.message_sequence_number_9004 = FormatUtils.filledWhitZeroes(4);
		this.session_sequence_number_9006 = FormatUtils.filledWhitZeroes(6);
		this.transaction_code_S9004 = TypeTransactionCode.TRANSFERENCIA_CTA_AHORROS;
	}
	
	public TypeMessageCode getMessage_type_code_X002() {
		return message_type_code_X002;
	}
	public void setMessage_type_code_X002(TypeMessageCode message_type_code_X002) {
		this.message_type_code_X002 = message_type_code_X002;
	}
	public String getMessage_fi_number_9004() {
		return message_fi_number_9004;
	}
	public void setMessage_fi_number_9004(String message_fi_number_9004) {
		this.message_fi_number_9004 = StringUtils.padLeft(message_fi_number_9004,4,"0").substring(0, 4);
	}
	public String getMessage_terminal_number_9005() {
		return message_terminal_number_9005;
	}
	public void setMessage_terminal_number_9005(String message_terminal_number_9005) {
		this.message_terminal_number_9005 = StringUtils.padLeft(message_terminal_number_9005,5,"0").substring(0, 5);
	}
	public String getSession_sequence_number_9006() {
		return session_sequence_number_9006;
	}
	public void setSession_sequence_number_9006(String session_sequence_number_9006) {
		this.session_sequence_number_9006 = session_sequence_number_9006;
	}
	public String getMessage_sequence_number_9004() {
		return message_sequence_number_9004;
	}
	public void setMessage_sequence_number_9004(String message_sequence_number_9004) {
		this.message_sequence_number_9004 = message_sequence_number_9004;
	}
	public TypeTransactionCode getTransaction_code_S9004() {
		return transaction_code_S9004;
	}
	public void setTransaction_code_S9004(TypeTransactionCode transaction_code_S9004) {
		this.transaction_code_S9004 = transaction_code_S9004;
	}
	
	
}
