package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;

@SuppressWarnings("serial")
@XmlRootElement
public class TrxMessageControlRequest implements Serializable{

	private MessageControlGroup message_control_group;
	private ControlDependentData control_dependent_data;
	
	public MessageControlGroup getMessage_control_group() {
		return message_control_group;
	}
	public void setMessage_control_group(MessageControlGroup message_control_group) {
		this.message_control_group = message_control_group;
	}
	public ControlDependentData getControl_dependent_data() {
		return control_dependent_data;
	}
	public void setControl_dependent_data(ControlDependentData control_dependent_data) {
		this.control_dependent_data = control_dependent_data;
	}
	
	public TrxMessageControlRequest byteArraytoStruct(byte[] message) {
		
		Logger log;
		TrxMessageControlRequest messageRequest = null;
		MessageControlGroup messageControlgroup = null;
		ControlDependentData controlDependentData = null;
		
		int offset = 0;
		Ref<Integer> ref = new Ref<Integer>(offset);
		try {

			messageRequest = new TrxMessageControlRequest();
			messageControlgroup = new MessageControlGroup();
			controlDependentData = new ControlDependentData();
			messageControlgroup.setMessage_type_code_X002(Enum.valueOf(TypeMessageCode.class,
						GeneralUtils.ReadFixString(message, ref, 2)));
			messageControlgroup.setMessage_fi_number_9004(GeneralUtils.ReadFixString(message, ref,4));
			messageControlgroup.setMessage_terminal_number_9005(GeneralUtils.ReadFixString(message, ref,5));
			messageControlgroup.setSession_sequence_number_9006(GeneralUtils.ReadFixString(message, ref,6));
			messageControlgroup.setMessage_sequence_number_9004(GeneralUtils.ReadFixString(message, ref,4));
			messageControlgroup.setTransaction_code_S9004(TypeTransactionCode.fromDisplayString( 
					    GeneralUtils.ReadFixString(message, ref,5)));
			messageRequest.setMessage_control_group(messageControlgroup);
			controlDependentData.setSession_date_9006(GeneralUtils.ReadFixString(message, ref,6));
			controlDependentData.setSession_time_9006(GeneralUtils.ReadFixString(message, ref,6));
			controlDependentData.setSession_bussiness_date_9006(GeneralUtils.ReadFixString(message, ref,6));
			messageRequest.setControl_dependent_data(controlDependentData);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::byteArraytoStruct"
					, TypeMonitor.error, e);
		}
		
		return messageRequest;
	}
	
}
