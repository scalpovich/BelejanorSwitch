package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.StringUtils;

@SuppressWarnings("serial")
@XmlType(propOrder={"force_post_indicator_X001", "reversal_indicator_X001",
					"trans_acct_nbr_9018","transaction_amount_S9_018"})

public class DetailTransaction implements Serializable{

	private String force_post_indicator_X001;
	private String reversal_indicator_X001;
	private String trans_acct_nbr_9018;
	private String transaction_amount_S9_018;
	
	public DetailTransaction() {
		
		this.force_post_indicator_X001 = FormatUtils.filledWhitSpaces(1);
		this.reversal_indicator_X001 = "0";
		this.trans_acct_nbr_9018 = FormatUtils.filledWhitZeroes(18);
		this.transaction_amount_S9_018 = FormatUtils.filledWhitZeroes(17) + "+";
	}
	
	public String getForce_post_indicator_X001() {
		return force_post_indicator_X001;
	}
	public void setForce_post_indicator_X001(String force_post_indicator_X001) {
		this.force_post_indicator_X001 = force_post_indicator_X001;
	}
	public String getReversal_indicator_X001() {
		return reversal_indicator_X001;
	}
	public void setReversal_indicator_X001(String reversal_indicator_X001) {
		this.reversal_indicator_X001 = reversal_indicator_X001;
	}
	public String getTrans_acct_nbr_9018() {
		return trans_acct_nbr_9018;
	}
	public void setTrans_acct_nbr_9018(String trans_acct_nbr_9018) {
		
		this.trans_acct_nbr_9018 = StringUtils.padLeft(trans_acct_nbr_9018,18,"0").substring(0,18);
	}
	public String getTransaction_amount_S9_018() {
		return transaction_amount_S9_018;
	}
	public void setTransaction_amount_S9_018(String transaction_amount_S9_018) {
		this.transaction_amount_S9_018 = transaction_amount_S9_018;
	}
	
	
}
