package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;

@SuppressWarnings("serial")
@XmlType(propOrder={"session_date_9006", "session_time_9006", 
					"session_bussiness_date_9006"})
public class ControlDependentData implements Serializable{

	private String session_date_9006;
	private String session_time_9006;
	private String session_bussiness_date_9006;
	
	
	public ControlDependentData() {

		this.session_date_9006 = FormatUtils.filledWhitZeroes(6);
		this.session_time_9006 = FormatUtils.filledWhitZeroes(6);
		this.session_bussiness_date_9006 = FormatUtils.filledWhitZeroes(6);
	}
	
	public String getSession_date_9006() {
		return session_date_9006;
	}
	public void setSession_date_9006(String session_date_9006) {
		this.session_date_9006 = session_date_9006;
	}
	public String getSession_time_9006() {
		return session_time_9006;
	}
	public void setSession_time_9006(String session_time_9006) {
		this.session_time_9006 = session_time_9006;
	}
	public String getSession_bussiness_date_9006() {
		return session_bussiness_date_9006;
	}
	public void setSession_bussiness_date_9006(String session_bussiness_date_9006) {
		this.session_bussiness_date_9006 = session_bussiness_date_9006;
	}
	
	
}
