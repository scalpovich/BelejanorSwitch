package com.belejanor.switcher.acquirers;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.authorizations.FitIsAut;
import com.belejanor.switcher.cscoreswitch.TransactionConfiguration;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;

public class CoonectaIsAcq {

	private Logger log;
	
	public CoonectaIsAcq() {
		
		log = new Logger();
	}
	
	public wIso8583 processRetiroATMs(wIso8583 iso){
		
		String fee = null; 
		wIso8583 isoRes = null;
		try {
			
			FitIsAut auth = new FitIsAut();
			iso = auth.RetiroAtmCoonecta(iso);
			
			/*Analisis de consulta en retiro*/
			fee = analizeFeeTransaction(iso);
			if(fee.equalsIgnoreCase("000")){
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					isoRes = (wIso8583) iso.clone();
					isoRes.setISO_039_ResponseCode("909");
					isoRes.setISO_039p_ResponseDetail("ERROR EN PROCESOS <**>");
					IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
					iso.getTickAut().reset();
					iso.getTickAut().start();
						isoRes = sql.Query_Savings_Account_SQL(isoRes);
					if(iso.getTickAut().isStarted())
						iso.getTickAut().stop();
					if(isoRes.getISO_039_ResponseCode().equals("000")){
						
						double sDisponible = Double.parseDouble(isoRes.getISO_121_ExtendedData());
						double sContable =  Double.parseDouble(isoRes.getISO_120_ExtendedData());
						iso.setISO_054_AditionalAmounts((iso.getISO_003_ProcessingCode().substring(2,4)
						    	.equals("10")?"10":"20") 
						    	+ "01" + (String.format("%.0f",iso.getISO_049_TranCurrCode())) 
						    	+ (sContable > 0 ?"C":"D") + String.format("%013.2f", sContable).replace(",", "").replace(".", "")
						    	+ (iso.getISO_003_ProcessingCode().substring(2,4).equals("10")?"10":"20") + "02" 
						    	+ (String.format("%.0f",iso.getISO_049_TranCurrCode()) + (sDisponible > 0 ?"C":"D") 
						    	+ String.format("%013.2f", sDisponible).replace(",", "").replace(".", "")));
						iso.setWsIso_LogStatus(2);
						iso.setISO_039_ResponseCode("000");
						iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
						
					}
					iso.setWsTempAut(iso.getWsTempAut() + isoRes.getWsTempAut());
				}
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils
					.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo CoonectaIsAcq::pro"
					+ "cessRetiroATMs ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	public wIso8583 processQuerySavingsAccount(wIso8583 iso){
		
		wIso8583 isoRes = null;
		wIso8583 isoResB = null;
		try {
			
			/*Proceso primero el debito por consulta*/
			isoResB = (wIso8583) iso.clone();
			isoResB.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			isoResB = processRetiroATMs(isoResB);
			
			/*Si es exitoso el debito por consulta
			 * Realizo el query para ver los saldos del Cliente*/
			if(isoResB.equals("000")){
			
				isoRes = (wIso8583) iso.clone();
				isoRes.setISO_039_ResponseCode("909");
				isoRes.setISO_039p_ResponseDetail("ERROR EN PROCESOS <**>");
				IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
				iso.getTickAut().reset();
				iso.getTickAut().start();
					isoRes = sql.Query_Savings_Account_SQL(isoRes);
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
				if(isoRes.getISO_039_ResponseCode().equals("000")){
					
					double sDisponible = Double.parseDouble(isoRes.getISO_121_ExtendedData());
					double sContable =  Double.parseDouble(isoRes.getISO_120_ExtendedData());
					iso.setISO_054_AditionalAmounts((iso.getISO_003_ProcessingCode().substring(2,4)
					    	.equals("10")?"10":"20") 
					    	+ "01" + (String.format("%.0f",iso.getISO_049_TranCurrCode())) 
					    	+ (sContable > 0 ?"C":"D") + String.format("%013.2f", sContable).replace(",", "").replace(".", "")
					    	+ (iso.getISO_003_ProcessingCode().substring(2,4).equals("10")?"10":"20") + "02" 
					    	+ (String.format("%.0f",iso.getISO_049_TranCurrCode()) + (sDisponible > 0 ?"C":"D") 
					    	+ String.format("%013.2f", sDisponible).replace(",", "").replace(".", "")));
					iso.setWsIso_LogStatus(2);
					iso.setISO_039_ResponseCode("000");
					iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
					
				}else{
					
					iso.setISO_039_ResponseCode(isoRes.getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(isoRes.getISO_039p_ResponseDetail());
					iso.setWsIso_LogStatus(isoRes.getWsIso_LogStatus());
				}
				
			}else{
				
				iso.setISO_039_ResponseCode(isoResB.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoResB.getISO_039p_ResponseDetail());
				iso.setWsIso_LogStatus(isoResB.getWsIso_LogStatus());
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error modulo CoonectaIsAcq::processQuerySavingsAccount ", 
					TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut(iso.getWsTempAut() + isoResB.getWsTempAut());
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
	private String analizeFeeTransaction(wIso8583 iso){
	    
    	String responseCode = null;
    	try {
			
    		String [] dataAdditional = iso.getISO_120_ExtendedData().trim().split("\\*");
    		String data = Arrays.stream(dataAdditional)
    				      .filter(p -> p.startsWith("S2C"))
    				      .findFirst().orElseGet(() -> null);
    		
    		if(data != null){
    			
    			data = data.replace("S2C", StringUtils.Empty());
    			int value = Integer.parseInt(data);
    			if(value > 0)
    				responseCode = "000";
    			else
    				responseCode = "001";	
    		}else
    			responseCode = "909ERROR EN PROCESOS, NO SE PUDO RECUPERAR COMISION DE LA TRANSACCION (NULL)";
    		
    		
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo CoonectaIsAcq::analizeFeeTransaction", 
					TypeMonitor.error, e);
			responseCode = "909ERROR EN PROCESOS, NO SE PUDO RECUPERAR COMISION DE LA TRANSACCION (EXCEPTION)";
		}
    	return responseCode;
    }
}
