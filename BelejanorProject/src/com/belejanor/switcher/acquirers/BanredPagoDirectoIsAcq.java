package com.belejanor.switcher.acquirers;

import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.authorizations.ProcessorCreditCardIsAut;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.FormatUtils;

public class BanredPagoDirectoIsAcq {

	private Logger log;
	
	public BanredPagoDirectoIsAcq() {
		log = new Logger();
	}
	
	public wIso8583 ProcessMessageControl(wIso8583 iso) {
		
		try {
			
			Thread.sleep(500);
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setWsIso_LogStatus(2);
			
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::ProcessMessageControl"
					, TypeMonitor.error, e);
		}
		return iso;
	}
	
    public wIso8583 ValidateAccountQuery(wIso8583 iso) {
		
		ProcessorCreditCardIsAut tc = null;
		IsoRetrievalTransaction sql = null;
		try {
			
			wIso8583 isoQ = (wIso8583) iso.clone(); 
			if(iso.getISO_003_ProcessingCode().substring(3, 5).equals("30")) { //Tarjeta de Credito
				
				tc = new ProcessorCreditCardIsAut();
				iso.getTickAut().reset();
				iso.getTickAut().start();
					isoQ = tc.QueryCreditCard(isoQ);
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
				
			}else {//Cuenta de Ahorros o Corriente
				
				sql = new IsoRetrievalTransaction();
				iso.getTickAut().reset();
				iso.getTickAut().start();
					isoQ = sql.validateAccountPagoDirectoBanred(isoQ);
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();	
				boolean flagAccountType = false;
				if(isoQ.getISO_039_ResponseCode().equals("000")) {
					
					String typeAccount = iso.getISO_003_ProcessingCode().substring(3, 5);
					
					if(typeAccount.equals("10")) {
						if(isoQ.getISO_123_ExtendedData().startsWith("4"))
							flagAccountType = true;
					}else {
						
						if(isoQ.getISO_123_ExtendedData().startsWith("9"))
							flagAccountType = true;
					}
					if(!flagAccountType) {
						
						isoQ.setISO_039_ResponseCode("114");
						isoQ.setISO_039p_ResponseDetail("EL TIPO DE CUENTA NO COINCIDE");
						
					}else {
						
						if(!isoQ.getISO_124_ExtendedData().equals("002")) {
						
							isoQ.setISO_039_ResponseCode("217");
							isoQ.setISO_039p_ResponseDetail("LA CUENTA NO ESTA ACTIVA");
							
						}else {
							
							//Validacion del cedula y cuenta Receptora
							if(!iso.getISO_023_CardSeq().equals(isoQ.getISO_122_ExtendedData())) {
								
								isoQ.setISO_039_ResponseCode("115");
								isoQ.setISO_039p_ResponseDetail("LA CEDULA RECEPTORA NO CORRESPONDE A LA REGISTRADA");
							}
						}
					}
					
					iso.setISO_013_LocalDate(FormatUtils.StringToDate("19801129","yyyyMMdd"));	
				}
			}
			
			iso.setISO_039_ResponseCode(isoQ.getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(isoQ.getISO_039p_ResponseDetail());
			iso.setWsIso_LogStatus(isoQ.getWsIso_LogStatus());
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo BanredPagoDirectoIsAut::ValidateAccountQuery ", 
				     TypeMonitor.error, e);
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
}
