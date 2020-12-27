package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.StringUtils;


@SuppressWarnings("serial")
@XmlType(propOrder={"reqpdr_target_name_X035", "reqpdr_observations_X010", "reqpdr_source_id_X013",
					"reqpdr_city_X010","reqpdr_reference_X020", "reqpdr_target_id_X013",
					"filler_X006"})
public class UserRequestDefinedData implements Serializable{

	private String reqpdr_target_name_X035;
	private String reqpdr_observations_X010;
	private String reqpdr_source_id_X013;
	private String reqpdr_city_X010;
	private String reqpdr_reference_X020;
	private String reqpdr_target_id_X013;
	private String filler_X006;
	
	public UserRequestDefinedData() {

		this.reqpdr_target_name_X035 = FormatUtils.filledWhitSpaces(35);
		this.reqpdr_observations_X010 = FormatUtils.filledWhitSpaces(10);
		this.reqpdr_source_id_X013 = FormatUtils.filledWhitSpaces(13);
		this.reqpdr_city_X010 = FormatUtils.filledWhitSpaces(10);
		this.reqpdr_reference_X020 = FormatUtils.filledWhitSpaces(20);
		this.reqpdr_target_id_X013 = FormatUtils.filledWhitSpaces(13);
		this.filler_X006 = FormatUtils.filledWhitSpaces(6);
	}
	
	public String getReqpdr_target_name_X035() {
		return reqpdr_target_name_X035;
	}
	public void setReqpdr_target_name_X035(String reqpdr_target_name_X035) {
		this.reqpdr_target_name_X035 = StringUtils.padRight(reqpdr_target_name_X035,35," ").substring(0, 35);
	}
	public String getReqpdr_observations_X010() {
		return reqpdr_observations_X010;
	}
	public void setReqpdr_observations_X010(String reqpdr_observations_X010) {
		this.reqpdr_observations_X010 = StringUtils.padRight(reqpdr_observations_X010,10," ").substring(0, 10);
	}
	public String getReqpdr_source_id_X013() {
		return reqpdr_source_id_X013;
	}
	public void setReqpdr_source_id_X013(String reqpdr_source_id_X013) {
		this.reqpdr_source_id_X013 = StringUtils.padRight(reqpdr_source_id_X013,13," ").substring(0, 13);
	}
	public String getReqpdr_city_X010() {
		return reqpdr_city_X010;
	}
	public void setReqpdr_city_X010(String reqpdr_city_X010) {
		this.reqpdr_city_X010 = StringUtils.padRight(reqpdr_city_X010,10," ").substring(0, 10);
	}
	public String getReqpdr_reference_X020() {
		return reqpdr_reference_X020;
	}
	public void setReqpdr_reference_X020(String reqpdr_reference_X020) {
		this.reqpdr_reference_X020 = reqpdr_reference_X020;
	}
	public String getReqpdr_target_id_X013() {
		return reqpdr_target_id_X013;
	}
	public void setReqpdr_target_id_X013(String reqpdr_target_id_X013) {
		this.reqpdr_target_id_X013 = StringUtils.padRight(reqpdr_target_id_X013,13," ").substring(0, 13);
	}
	public String getFiller_X006() {
		return filler_X006;
	}
	public void setFiller_X006(String filler_X006) {
		this.filler_X006 = filler_X006;
	}
	
	
}
