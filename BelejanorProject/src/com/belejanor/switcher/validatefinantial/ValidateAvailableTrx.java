package com.belejanor.switcher.validatefinantial;

import java.util.concurrent.Callable;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.GeneralUtils;

public class ValidateAvailableTrx implements Callable<wIso8583>{
	
	private Logger log;
	private wIso8583 iso;
	
	public ValidateAvailableTrx(wIso8583 iso){
		this.log = new Logger();
		this.iso = iso;
	}

	public wIso8583 ValidateNumberAvailableTrx(wIso8583 iso){
		
		try {
			
			if(iso.getWsTransactionConfig().getTrxNroPermission() == -1 &&
			    iso.getWsTransactionConfig().getTrxCupoMax() == -1){
				iso.setISO_039_ResponseCode("000");
				
			}else {
				if(iso.getISO_000_Message_Type().startsWith("12")){
					
					IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
					iso = sql.getValCupoTrx(iso);
					
				}else {
					iso.setISO_039_ResponseCode("000");
				}
			}
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo ValidateAvailableTrx::ValidateNumberAvailableTrx ", TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR FINANTIAL VALIDATION ", e, false));
		}
		return iso;
	}

	@Override
	public wIso8583 call() throws Exception {
		
		return ValidateNumberAvailableTrx(this.iso);
	}
	
	
	
}
