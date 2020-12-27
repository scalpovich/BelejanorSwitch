package com.belejanor.switcher.acquirers;

import java.util.Arrays;

import com.belejanor.switcher.authorizations.FitIsAut;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;
import com.belejanor.switcher.validatefinantial.ValidateWalletBimo;

public class ProsupplyIsAcq {

	private Logger log;
	
	public ProsupplyIsAcq() {
		log = new Logger();
	}
	
	public wIso8583 AtmNormalCreditDebitJNP(wIso8583 iso){
		
		try {
			
			iso.setISO_037_RetrievalReferenceNumber(StringUtils.IsNullOrEmpty(iso.getISO_037_RetrievalReferenceNumber())
					?iso.getISO_011_SysAuditNumber():iso.getISO_037_RetrievalReferenceNumber());
			FitIsAut auth = new FitIsAut();
			iso = auth.DepositoReciclador(iso); 
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils
					.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo ProsupplyIsAcq::AtmNormalCreditDebit ", 
					TypeMonitor.error, e);
		}
		return iso;
	}
	
	public wIso8583 AtmNormalCreditDebit(wIso8583 iso){
		
		try {
			
			iso.setISO_037_RetrievalReferenceNumber(StringUtils.IsNullOrEmpty(iso.getISO_037_RetrievalReferenceNumber())
					?iso.getISO_011_SysAuditNumber():iso.getISO_037_RetrievalReferenceNumber());
			FitIsAut auth = new FitIsAut();
			iso = auth.ExecuteStandarTransaction(iso);
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils
					.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo ProsupplyIsAcq::AtmNormalCreditDebit ", 
					TypeMonitor.error, e);
		}
		return iso;
	}
	
    public wIso8583 isValidAssosiationMovilBimoAutExe(wIso8583 iso){
		
		wIso8583 isoClone = null;
		try {
	
			isoClone = iso.cloneWiso(iso);
			isoClone.setISO_BitMap("0000000000000000");
			isoClone.setISO_023_CardSeq(iso.getISO_002_PAN());
			ValidateWalletBimo val = new ValidateWalletBimo();
			isoClone = val.validateBimoWallet(isoClone);
			
			if(isoClone.getISO_039_ResponseCode().equals("000")){
			
				iso.setISO_102_AccountID_1(Arrays.asList(isoClone.getISO_044_AddRespData().split("\\|")).get(5));
				iso.setISO_034_PANExt(Arrays.asList(isoClone.getISO_044_AddRespData().split("\\|")).get(3));
				//iso.setISO_120_ExtendedData(isoClone.getISO_002_PAN());
			}
			
			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			iso.setWsTempAut(isoClone.getWsTempAut());
			iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo ProsupplyIsAcq::isValidAssosiationMovilBimoAutExe ", 
				     TypeMonitor.error, e);
		}
		return iso;
	}
    
    public wIso8583 AtmBimoDebit(wIso8583 iso){
    	
    	try {
			
			iso.setISO_037_RetrievalReferenceNumber(StringUtils.IsNullOrEmpty(iso.getISO_037_RetrievalReferenceNumber())
					?iso.getISO_011_SysAuditNumber():iso.getISO_037_RetrievalReferenceNumber());
			FitIsAut auth = new FitIsAut();
			iso = auth.ExecuteStandarTransaction(iso);
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils
					.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo ProsupplyIsAcq::AtmBimoDebit ", 
					TypeMonitor.error, e);
		}
		return iso;
    }
    
    public wIso8583 DisplayNamesATM(wIso8583 iso) {
    	
    	FitIsAut aut = null;
    	try {
			
    		aut = new FitIsAut();
    		iso = aut.ExecuteQValidateCtaTrx(iso);
    		if(iso.getISO_039_ResponseCode().equals("000")) {
    			
    			if(!StringUtils.IsNullOrEmpty(iso.getISO_120_ExtendedData())) {
    				
    				String[] nombres = iso.getISO_120_ExtendedData().split("(\\s)");    				
					if(nombres.length >= 3)
						iso.setISO_120_ExtendedData(nombres[0] + " " + nombres[2]);
    				
    			}else {
    				
    				iso.setISO_039_ResponseCode("902");
    				iso.setISO_039p_ResponseDetail("NO SE PUDO RECUPERAR NOMBRES COMPLETOS DE LA PERSONA");
    			}
    		}
    		
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo ProsupplyIsAcq::DisplayNamesATM ", 
					TypeMonitor.error, e);
		}
    	return iso;
    }
}
