package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;

@SuppressWarnings("serial")
@XmlType(propOrder={"standin_auth_type_X001", "standin_auth_metod_X001",
					"standin_result_code_9004"})
public class StandInGroup implements Serializable{

	private String standin_auth_type_X001;
	private String standin_auth_metod_X001;
	private String standin_result_code_9004;
	
	
	public StandInGroup() {

		this.standin_auth_type_X001 = FormatUtils.filledWhitSpaces(1);
		this.standin_auth_metod_X001 = FormatUtils.filledWhitSpaces(1);
		this.standin_result_code_9004 = FormatUtils.filledWhitZeroes(4);
	}
	
	public String getStandin_auth_type_X001() {
		return standin_auth_type_X001;
	}
	public void setStandin_auth_type_X001(String standin_auth_type_X001) {
		this.standin_auth_type_X001 = standin_auth_type_X001;
	}
	public String getStandin_auth_metod_X001() {
		return standin_auth_metod_X001;
	}
	public void setStandin_auth_metod_X001(String standin_auth_metod_X001) {
		this.standin_auth_metod_X001 = standin_auth_metod_X001;
	}
	public String getStandin_result_code_9004() {
		return standin_result_code_9004;
	}
	public void setStandin_result_code_9004(String standin_result_code_9004) {
		this.standin_result_code_9004 = standin_result_code_9004;
	}
	
	
	
	
	
}
