package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.StringUtils;

@SuppressWarnings("serial")
@XmlType(propOrder={"acct_1_info_flag_X001", "host_data_info_flag_X001", "acct_1_nbr_9018",
					"acct_1_avail_bal_S90018","acct_1_curr_bal_S90018", "acct_1_appl_code_9002",
					"trf_terminal_type_9001","trf_source_name_X031"})
public class StandardReplyFields  implements Serializable{

	private String acct_1_info_flag_X001;
	private String host_data_info_flag_X001;
	private String acct_1_nbr_9018;
	private String acct_1_avail_bal_S90018;
	private String acct_1_curr_bal_S90018;
	private String acct_1_appl_code_9002;
	private String trf_terminal_type_9001;
	private String trf_source_name_X031;
		
	
	public StandardReplyFields() {

		this.acct_1_info_flag_X001 = FormatUtils.filledWhitSpaces(1);
		this.host_data_info_flag_X001 = FormatUtils.filledWhitSpaces(1);
		this.acct_1_nbr_9018 = FormatUtils.filledWhitZeroes(18);
		this.acct_1_avail_bal_S90018 = FormatUtils.filledWhitZeroes(17) + "+";
		this.acct_1_curr_bal_S90018 = FormatUtils.filledWhitZeroes(17) + "+";
		this.acct_1_appl_code_9002 = FormatUtils.filledWhitZeroes(2);
		this.trf_terminal_type_9001 = FormatUtils.filledWhitZeroes(1);
		this.trf_source_name_X031 = FormatUtils.filledWhitSpaces(31);
	}
	
	public String getAcct_1_info_flag_X001() {
		return acct_1_info_flag_X001;
	}
	public void setAcct_1_info_flag_X001(String acct_1_info_flag_X001) {
		this.acct_1_info_flag_X001 = acct_1_info_flag_X001;
	}
	public String getHost_data_info_flag_X001() {
		return host_data_info_flag_X001;
	}
	public void setHost_data_info_flag_X001(String host_data_info_flag_X001) {
		this.host_data_info_flag_X001 = host_data_info_flag_X001;
	}
	public String getAcct_1_nbr_9018() {
		return acct_1_nbr_9018;
	}
	public void setAcct_1_nbr_9018(String acct_1_nbr_9018) {
		this.acct_1_nbr_9018 =  StringUtils.padLeft(acct_1_nbr_9018,18,"0").substring(0, 18);
	}
	public String getAcct_1_avail_bal_S90018() {
		return acct_1_avail_bal_S90018;
	}
	public void setAcct_1_avail_bal_S90018(String acct_1_avail_bal_S90018) {
		this.acct_1_avail_bal_S90018 = acct_1_avail_bal_S90018;
	}
	public String getAcct_1_curr_bal_S90018() {
		return acct_1_curr_bal_S90018;
	}
	public void setAcct_1_curr_bal_S90018(String acct_1_curr_bal_S90018) {
		this.acct_1_curr_bal_S90018 = acct_1_curr_bal_S90018;
	}
	public String getAcct_1_appl_code_9002() {
		return acct_1_appl_code_9002;
	}
	public void setAcct_1_appl_code_9002(String acct_1_appl_code_9002) {
		this.acct_1_appl_code_9002 = StringUtils.padLeft(acct_1_appl_code_9002,2,"0").substring(0, 2);
	}
	public String getTrf_terminal_type_9001() {
		return trf_terminal_type_9001;
	}
	public void setTrf_terminal_type_9001(String trf_terminal_type_9001) {
		this.trf_terminal_type_9001 = trf_terminal_type_9001;
	}
	public String getTrf_source_name_X031() {
		return trf_source_name_X031;
	}
	public void setTrf_source_name_X031(String trf_source_name_X031) {
		this.trf_source_name_X031 = StringUtils.padRight(trf_source_name_X031,31," ").substring(0, 31);
	}
	
	
}
