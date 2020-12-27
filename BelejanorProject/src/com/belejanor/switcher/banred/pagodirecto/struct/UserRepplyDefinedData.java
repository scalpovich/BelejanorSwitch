package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.StringUtils;


@SuppressWarnings("serial")
@XmlType(propOrder={"reppdr_acct_type_9002", "reppdr_acct_number_9018", "reppdr_account_name_X040",
					"reppdr_min_payment_9008","reppdr_tot_payment_9008", "reppdr_limit_date_9008",
					"reppdr_target_phone_9010","reppdr_target_id_X013"})
public class UserRepplyDefinedData implements Serializable{

	private String reppdr_acct_type_9002;
	private String reppdr_acct_number_9018;
	private String reppdr_account_name_X040;
	private String reppdr_min_payment_9008;
	private String reppdr_tot_payment_9008;
	private String reppdr_limit_date_9008;
	private String reppdr_target_phone_9010;
	private String reppdr_target_id_X013;

	
	public UserRepplyDefinedData() {
		
		this.reppdr_acct_type_9002 = FormatUtils.filledWhitZeroes(2);
		this.reppdr_acct_number_9018 = FormatUtils.filledWhitZeroes(18);
		this.reppdr_account_name_X040 = FormatUtils.filledWhitSpaces(40);
		this.reppdr_min_payment_9008 = FormatUtils.filledWhitZeroes(8);
		this.reppdr_tot_payment_9008 = FormatUtils.filledWhitZeroes(8);
		this.reppdr_limit_date_9008 = FormatUtils.filledWhitZeroes(8);
		this.reppdr_target_phone_9010 = FormatUtils.filledWhitZeroes(10);
		this.reppdr_target_id_X013 = FormatUtils.filledWhitZeroes(13);
	}
	
	public String getReppdr_acct_type_9002() {
		return reppdr_acct_type_9002;
	}
	public void setReppdr_acct_type_9002(String reppdr_acct_type_9002) {
		this.reppdr_acct_type_9002 = reppdr_acct_type_9002;
	}
	public String getReppdr_acct_number_9018() {
		return reppdr_acct_number_9018;
	}
	public void setReppdr_acct_number_9018(String reppdr_acct_number_9018) {
		this.reppdr_acct_number_9018 = StringUtils.padLeft(reppdr_acct_number_9018,18,"0").substring(0, 18);
	}
	public String getReppdr_account_name_X040() {
		return reppdr_account_name_X040;
	}
	public void setReppdr_account_name_X040(String reppdr_account_name_X040) {
		this.reppdr_account_name_X040 = StringUtils.padRight(reppdr_account_name_X040,40," ").substring(0, 40);
	}
	public String getReppdr_min_payment_9008() {
		return reppdr_min_payment_9008;
	}
	public void setReppdr_min_payment_9008(String reppdr_min_payment_9008) {
		this.reppdr_min_payment_9008 = reppdr_min_payment_9008;
	}
	public String getReppdr_tot_payment_9008() {
		return reppdr_tot_payment_9008;
	}
	public void setReppdr_tot_payment_9008(String reppdr_tot_payment_9008) {
		this.reppdr_tot_payment_9008 = reppdr_tot_payment_9008;
	}
	public String getReppdr_limit_date_9008() {
		return reppdr_limit_date_9008;
	}
	public void setReppdr_limit_date_9008(String reppdr_limit_date_9008) {
		this.reppdr_limit_date_9008 = reppdr_limit_date_9008;
	}
	public String getReppdr_target_phone_9010() {
		return reppdr_target_phone_9010;
	}
	public void setReppdr_target_phone_9010(String reppdr_target_phone_9010) {
		this.reppdr_target_phone_9010 = StringUtils.padLeft(reppdr_target_phone_9010, 10, "0").substring(0, 10);
	}
	public String getReppdr_target_id_X013() {
		return reppdr_target_id_X013;
	}
	public void setReppdr_target_id_X013(String reppdr_target_id_X013) {
		this.reppdr_target_id_X013 = StringUtils.padRight(reppdr_target_id_X013,13," ").substring(0, 13);
	}
	
	
}
