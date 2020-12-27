package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.StringUtils;

@SuppressWarnings("serial")
@XmlType(propOrder={"other_fi_9004", "other_appl_9002", 
					"other_acct_9018",
					"internal_ind_X001"})
public class DataTransfer implements Serializable{

	private String other_fi_9004;
	private String other_appl_9002;
	private String other_acct_9018;
	private String internal_ind_X001;
	
	public DataTransfer() {
		
		this.other_fi_9004 = FormatUtils.filledWhitZeroes(4);
		this.other_appl_9002 = FormatUtils.filledWhitZeroes(2);
		this.other_acct_9018 = FormatUtils.filledWhitZeroes(18);
		this.internal_ind_X001 = FormatUtils.filledWhitSpaces(1);
	}
	
	public String getOther_fi_9004() {
		return other_fi_9004;
	}
	public void setOther_fi_9004(String other_fi_9004) {
		this.other_fi_9004 = StringUtils.padLeft(other_fi_9004,4,"0").substring(0, 4);
	}
	public String getOther_appl_9002() {
		return other_appl_9002;
	}
	public void setOther_appl_9002(String other_appl_9002) {
		this.other_appl_9002 = other_appl_9002;
	}
	public String getOther_acct_9018() {
		return other_acct_9018;
	}
	public void setOther_acct_9018(String other_acct_9018) {
		this.other_acct_9018 = StringUtils.padLeft(other_acct_9018,18,"0").substring(0, 18);
	}
	public String getInternal_ind_X001() {
		return internal_ind_X001;
	}
	public void setInternal_ind_X001(String internal_ind_X001) {
		this.internal_ind_X001 = internal_ind_X001;
	}
	
	
}
