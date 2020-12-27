package com.belejanor.switcher.acquirers;

import com.belejanor.switcher.authorizations.CredencialIsAut;
import com.belejanor.switcher.authorizations.FitIsAut;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;

public class FitIsAcq {
	
	
	private Logger log;

	public FitIsAcq(){
		
		log = new Logger();
	}
	
	public FitIsAcq(String IsoText){
		this();
	}
	
	public wIso8583 QueryTC(wIso8583 iso){
		
		try {
			
			CredencialIsAut cred = new CredencialIsAut();
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			iso = cred.QueryTC(iso);
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", 
					  e, true));
			log.WriteLogMonitor("Error modulo FitIsAcq::QueryTC ", TypeMonitor.error, e);
		}
		
		return iso;
	}
}
