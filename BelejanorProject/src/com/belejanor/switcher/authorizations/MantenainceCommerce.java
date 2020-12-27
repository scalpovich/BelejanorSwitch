package com.belejanor.switcher.authorizations;

import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.GeneralUtils;

public class MantenainceCommerce {

	public wIso8583 processMantenainceCommerce(wIso8583 iso){
		
		try {
			
			IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
			iso.getTickAut().reset();
			iso.getTickAut().start();
			iso = sql.MantenainceCommerceSQL(iso);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));	
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
}
