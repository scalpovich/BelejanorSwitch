package com.belejanor.switcher.banred.pagodirecto.struct;

import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.StringUtils;

@XmlType(propOrder={"source_date_9006", "source_time_9006",
					"source_aba_number_9010","source_branch_number_9004",
					"source_business_date_9006","trf_terminal_type_9001",
					"trf_source_name_X031"})

public class XactSourceInformation {

	private String source_date_9006;
	private String source_time_9006;
	private String source_aba_number_9010;
	private String source_branch_number_9004;
	private String source_business_date_9006;
	private String trf_terminal_type_9001;
	private String trf_source_name_X031;
	
	
	public XactSourceInformation() {
		
		this.source_date_9006 = FormatUtils.filledWhitZeroes(6);
		this.source_time_9006 = FormatUtils.filledWhitZeroes(6);
		this.source_aba_number_9010 = FormatUtils.filledWhitZeroes(10);
		this.source_branch_number_9004 = FormatUtils.filledWhitZeroes(4);
		this.source_business_date_9006 = FormatUtils.filledWhitZeroes(6);
		this.trf_terminal_type_9001 = FormatUtils.filledWhitZeroes(1);
		this.trf_source_name_X031 = FormatUtils.filledWhitSpaces(31);
	}
	
	public String getSource_date_9006() {
		return source_date_9006;
	}
	public void setSource_date_9006(String source_date_9006) {
		this.source_date_9006 = source_date_9006;
	}
	public String getSource_time_9006() {
		return source_time_9006;
	}
	public void setSource_time_9006(String source_time_9006) {
		this.source_time_9006 = source_time_9006;
	}
	public String getSource_aba_number_9010() {
		return source_aba_number_9010;
	}
	public void setSource_aba_number_9010(String source_aba_number_9010) {
		this.source_aba_number_9010 = StringUtils.padLeft(source_aba_number_9010,10,"0").substring(0, 10);
	}
	public String getSource_branch_number_9004() {
		return source_branch_number_9004;
	}
	public void setSource_branch_number_9004(String source_branch_number_9004) {
		this.source_branch_number_9004 = StringUtils.padLeft(source_branch_number_9004,4,"0").substring(0, 4);
	}
	public String getSource_business_date_9006() {
		return source_business_date_9006;
	}
	public void setSource_business_date_9006(String source_business_date_9006) {
		this.source_business_date_9006 = source_business_date_9006;
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
