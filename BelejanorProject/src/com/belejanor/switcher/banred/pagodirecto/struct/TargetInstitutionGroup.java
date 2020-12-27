package com.belejanor.switcher.banred.pagodirecto.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;

import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.StringUtils;

@SuppressWarnings("serial")
@XmlType(propOrder={"auth_fi_nbr_9004", "host_business_date_9006"})
public class TargetInstitutionGroup implements Serializable{

	private String auth_fi_nbr_9004;
	private String host_business_date_9006;
	
	
	public TargetInstitutionGroup() {
		
		this.auth_fi_nbr_9004 = FormatUtils.filledWhitZeroes(4);
		this.host_business_date_9006 = FormatUtils.filledWhitZeroes(6);
	}
	
	public String getAuth_fi_nbr_9004() {
		return auth_fi_nbr_9004;
	}
	public void setAuth_fi_nbr_9004(String auth_fi_nbr_9004) {
		this.auth_fi_nbr_9004 = StringUtils.padLeft(auth_fi_nbr_9004,4,"0").substring(0, 4);
	}
	public String getHost_business_date_9006() {
		return host_business_date_9006;
	}
	public void setHost_business_date_9006(String host_business_date_9006) {
		this.host_business_date_9006 = host_business_date_9006;
	}
	
}
