package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.StringUtils;

@SuppressWarnings("serial")
@XmlType(propOrder={"acct_2_nbr_X018", "acct_2_avail_bal_S9018", 
					"acct_2_curr_bal_S9018" ,
					"acct_2_appl_code_9002"})
public class DataDependend implements Serializable{

	private String acct_2_nbr_9018;
	private String acct_2_avail_bal_S9018;
	private String acct_2_curr_bal_S9018;
	private String acct_2_appl_code_9002;
	

	public DataDependend() {
		
		this.acct_2_nbr_9018 = FormatUtils.filledWhitZeroes(18);
		this.acct_2_avail_bal_S9018 = FormatUtils.filledWhitZeroes(17) + "+";
		this.acct_2_curr_bal_S9018 = FormatUtils.filledWhitZeroes(17) + "+";
		this.acct_2_appl_code_9002 = FormatUtils.filledWhitZeroes(2);
	}
	
	
	
	public String getAcct_2_nbr_9018() {
		return acct_2_nbr_9018;
	}

	public void setAcct_2_nbr_9018(String acct_2_nbr_9018) {
		this.acct_2_nbr_9018 = StringUtils.padLeft(acct_2_nbr_9018,18,"0").substring(0, 18);
	}

	public String getAcct_2_avail_bal_S9018() {
		return acct_2_avail_bal_S9018;
	}
	public void setAcct_2_avail_bal_S9018(String acct_2_avail_bal_S9018) {
		this.acct_2_avail_bal_S9018 = acct_2_avail_bal_S9018;
	}
	public String getAcct_2_curr_bal_S9018() {
		return acct_2_curr_bal_S9018;
	}
	public void setAcct_2_curr_bal_S9018(String acct_2_curr_bal_S9018) {
		this.acct_2_curr_bal_S9018 = acct_2_curr_bal_S9018;
	}
	public String getAcct_2_appl_code_9002() {
		return acct_2_appl_code_9002;
	}
	public void setAcct_2_appl_code_9002(String acct_2_appl_code_9002) {
		this.acct_2_appl_code_9002 = acct_2_appl_code_9002;
	}
	
	
}
