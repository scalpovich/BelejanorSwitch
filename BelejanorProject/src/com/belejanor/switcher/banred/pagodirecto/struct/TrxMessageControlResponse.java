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
@XmlType(propOrder={"message_control_group", "control_dependent_data",
		 "result_code_9004"})
public class TrxMessageControlResponse extends TrxMessageControlRequest implements Serializable{

	private String result_code_9004;
	
	public TrxMessageControlResponse() {
		super();
		this.result_code_9004 = "7506";
	}

	public TrxMessageControlResponse(String resultCode) {

		this.result_code_9004 = resultCode;
	}

	public String getResult_code_9004() {
		return result_code_9004;
	}

	public void setResult_code_9004(String result_code_9004) {
		this.result_code_9004 = result_code_9004;
	}
	
	public byte[] convertTrxMessageControlResponseToBytes(TrxMessageControlResponse msgResponse) {
		
		byte[] response = new byte[40960];
		Logger log;
		int offset = 0;
		ResponseBynary bin = new ResponseBynary();
		bin.setBuffer(response);
		bin.setOffset(offset);
		Ref<ResponseBynary> ref = new Ref<ResponseBynary>(bin);
		try {
			
			GeneralUtils.ReadFixStringReverse(msgResponse.getMessage_control_group().getMessage_type_code_X002().name(), ref);
			GeneralUtils.ReadFixStringReverse(msgResponse.getMessage_control_group().getMessage_fi_number_9004(),ref);
			GeneralUtils.ReadFixStringReverse(msgResponse.getMessage_control_group().getMessage_terminal_number_9005(),ref);
			GeneralUtils.ReadFixStringReverse(msgResponse.getMessage_control_group().getSession_sequence_number_9006(),ref);
			GeneralUtils.ReadFixStringReverse(msgResponse.getMessage_control_group().getMessage_sequence_number_9004(),ref);
			GeneralUtils.ReadFixStringReverse(msgResponse.getMessage_control_group().getTransaction_code_S9004().toString(),ref);
			GeneralUtils.ReadFixStringReverse(msgResponse.getResult_code_9004(),ref);
			GeneralUtils.ReadFixStringReverse(msgResponse.getControl_dependent_data().getSession_date_9006(),ref);
			GeneralUtils.ReadFixStringReverse(msgResponse.getControl_dependent_data().getSession_time_9006(),ref);
			GeneralUtils.ReadFixStringReverse(msgResponse.getControl_dependent_data().getSession_bussiness_date_9006(),ref);
			int totalOffset = ref.get().getOffset();
			byte[] bufferTotal = new byte[totalOffset];
	        System.arraycopy(ref.get().getBuffer(), 0, bufferTotal, 0, totalOffset);
	        return bufferTotal;
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::convertTrxMessageControlResponseToBytes"
					, TypeMonitor.error, e);
			return null;
		}
		
	}
}
