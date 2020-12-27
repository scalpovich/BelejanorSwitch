package com.belejanor.switcher.authorizations;

import java.util.Date;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.FormatUtils.TypeTemp;

public class ProcessorCreditCardIsAut {

	private Logger log;
	
	public ProcessorCreditCardIsAut() {
		log = new Logger();
	}
	
	public wIso8583 QueryCreditCard(wIso8583 iso) {
		try {
			
			
			//Simulacion de pago minimo, pago total y fecha limite de pago de una tarjeta de credito
			iso.setISO_006_BillAmount(356.89); //Pago minimo
			iso.setISO_008_BillFeeAmount(1798.90); //Pago Total
			iso.setISO_013_LocalDate(FormatUtils.sumarRestarHorasFecha(new Date(), TypeTemp.dias, 5));
			Thread.sleep(1600);
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setWsIso_LogStatus(2);
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::QueryCreditCard"
					, TypeMonitor.error, e);
		}
		
		return iso;
	}
	
	public wIso8583 PayCreditCard(wIso8583 iso) {
		try {
			
			//Simulacion de pago exitoso de una tarjeta de credito
			Thread.sleep(700);
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setWsIso_LogStatus(2);
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::PayCreditCard"
					, TypeMonitor.error, e);
		}
		
		return iso;
	}
}
