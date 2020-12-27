package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.StringUtils;


@SuppressWarnings("serial")
@XmlType(propOrder={"pin_verify_flag_X001", "track_II_valid_flag_X001", "red_target_phone_9010",
					"red_appl_type_source_9001","red_flg_red_pd_X002", "red_filler_X_003",
					"track_2_data_X040","card_appl_code_X002"})
public class CardRelatedGroup implements Serializable{

	private String pin_verify_flag_X001;
	private String track_II_valid_flag_X001;
	private String red_target_phone_9010;
	private String red_appl_type_source_9001;
	private String red_flg_red_pd_X002;
	private String red_filler_X_003;
	private String track_2_data_X040;
	private String card_appl_code_X002;
	
	
	public CardRelatedGroup() {
		
		this.pin_verify_flag_X001 = FormatUtils.filledWhitSpaces(1);
		this.track_II_valid_flag_X001 = FormatUtils.filledWhitSpaces(1);
		this.red_target_phone_9010 = FormatUtils.filledWhitZeroes(10);
		this.red_appl_type_source_9001 = FormatUtils.filledWhitZeroes(1);
		this.red_flg_red_pd_X002 = FormatUtils.filledWhitSpaces(2);
		this.red_filler_X_003 = FormatUtils.filledWhitSpaces(3);
		this.track_2_data_X040 = FormatUtils.filledWhitSpaces(40);
		this.card_appl_code_X002 = FormatUtils.filledWhitSpaces(2);
	}
	public String getPin_verify_flag_X001() {
		return pin_verify_flag_X001;
	}
	public void setPin_verify_flag_X001(String pin_verify_flag_X001) {
		this.pin_verify_flag_X001 = pin_verify_flag_X001;
	}
	public String getTrack_II_valid_flag_X001() {
		return track_II_valid_flag_X001;
	}
	public void setTrack_II_valid_flag_X001(String track_II_valid_flag_X001) {
		this.track_II_valid_flag_X001 = track_II_valid_flag_X001;
	}
	public String getRed_target_phone_9010() {
		return red_target_phone_9010;
	}
	public void setRed_target_phone_9010(String red_target_phone_9010) {
		this.red_target_phone_9010 = red_target_phone_9010;
	}
	public String getRed_appl_type_source_9001() {
		return red_appl_type_source_9001;
	}
	public void setRed_appl_type_source_9001(String red_appl_type_source_9001) {
		this.red_appl_type_source_9001 = red_appl_type_source_9001;
	}
	public String getRed_flg_red_pd_X002() {
		return red_flg_red_pd_X002;
	}
	public void setRed_flg_red_pd_X002(String red_flg_red_pd_X002) {
		this.red_flg_red_pd_X002 = red_flg_red_pd_X002;
	}
	public String getRed_filler_X_003() {
		return red_filler_X_003;
	}
	public void setRed_filler_X_003(String red_filler_X_003) {
		this.red_filler_X_003 = red_filler_X_003;
	}
	public String getTrack_2_data_X040() {
		return track_2_data_X040;
	}
	public void setTrack_2_data_X040(String track_2_data_X040) {
		this.track_2_data_X040 = StringUtils.padRight(track_2_data_X040,40," ").substring(0, 40);
	}
	public String getCard_appl_code_X002() {
		return card_appl_code_X002;
	}
	public void setCard_appl_code_X002(String card_appl_code_X002) {
		this.card_appl_code_X002 = card_appl_code_X002;
	}
	

	
}
