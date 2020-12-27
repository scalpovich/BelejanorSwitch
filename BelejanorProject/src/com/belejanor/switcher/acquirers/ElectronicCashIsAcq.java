package com.belejanor.switcher.acquirers;

import org.apache.commons.lang3.time.StopWatch;

import com.belejanor.switcher.cscoreswitch.EndTransaction;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.electroniccash.DTORequestCredit;
import com.belejanor.switcher.electroniccash.DTORequestDebit;
import com.belejanor.switcher.electroniccash.DTORequestIsValidAccount;
import com.belejanor.switcher.electroniccash.DTORequestRevert;
import com.belejanor.switcher.electroniccashres.DTOResponseCredit;
import com.belejanor.switcher.electroniccashres.DTOResponseDebit;
import com.belejanor.switcher.electroniccashres.DTOResponseIsValidAccount;
import com.belejanor.switcher.electroniccashres.DTOResponseRevert;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.StringUtils;

public class ElectronicCashIsAcq {

	private Logger log;
	
	public ElectronicCashIsAcq(){
		this.log = new Logger();
	}
	
	public DTOResponseCredit Credit(DTORequestCredit credit, String IP) {
		
		log.WriteLog(credit, TypeLog.bceacq, TypeWriteLog.file);
		DTOResponseCredit dtoResponseCredit = null;
		Iso8583 iso = new Iso8583(credit);
		if(iso.getISO_039_ResponseCode().equals("000")){
			Iso8583 isoResponse = new Iso8583();
			csProcess processor = new csProcess();
			isoResponse = processor.ProcessTransactionMain(iso, IP);
			if(isoResponse != null){
				dtoResponseCredit = new DTOResponseCredit();
				if(isoResponse.getISO_039_ResponseCode().equals("000"))
					dtoResponseCredit.setOperationResult("1");
				else
					dtoResponseCredit.setOperationResult("2");
				dtoResponseCredit.setErrorCode(getHomologaError(isoResponse));
				dtoResponseCredit.setMessage(isoResponse.getISO_039p_ResponseDetail());
				dtoResponseCredit.setTransactionSequenceId(Long.parseLong(isoResponse.getISO_011_SysAuditNumber()));
				dtoResponseCredit.setExternalTransactionId(StringUtils.IsNullOrEmpty(isoResponse.getISO_038_AutorizationNumber()) ? "000000" 
											:  isoResponse.getISO_038_AutorizationNumber());
			}
		}
		else{
			
			dtoResponseCredit = new DTOResponseCredit();
			dtoResponseCredit.setOperationResult("2");
			dtoResponseCredit.setErrorCode(getHomologaError(iso));
			dtoResponseCredit.setMessage(iso.getISO_039p_ResponseDetail());
			dtoResponseCredit.setTransactionSequenceId(credit.getTransactionSequenceId());
			dtoResponseCredit.setExternalTransactionId(StringUtils.IsNullOrEmpty(iso.getISO_038_AutorizationNumber()) ? "000000" 
										:  iso.getISO_038_AutorizationNumber());
		}
		
		log.WriteLog(dtoResponseCredit, TypeLog.bceacq, TypeWriteLog.file);
		return dtoResponseCredit;
	}

	public DTOResponseRevert Revert(DTORequestRevert rever, String IP) {
		
		System.out.println("Entro DTOResponseRevert Revert....(3)");
		log.WriteLog(rever, TypeLog.bceacq, TypeWriteLog.file);
		DTOResponseRevert dtoResponseRevert = null;
		Iso8583 iso = new Iso8583(rever);
		if(iso.getISO_039_ResponseCode().equals("000")){
			
			System.out.println("Entro DTOResponseRevert Revert....(3.1)");
			/*** Retrieve Transaction ***/
			wIso8583 wiso = new wIso8583();
		
			wiso.setISO_000_Message_Type(iso.getISO_000_Message_Type());
			wiso.setISO_003_ProcessingCode(iso.getISO_003_ProcessingCode());
			wiso.setISO_011_SysAuditNumber(iso.getISO_011_SysAuditNumber());
			wiso.setISO_018_MerchantType(iso.getISO_018_MerchantType());
			wiso.setISO_024_NetworkId(iso.getISO_024_NetworkId());
			IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
			wiso = sql.RetrieveTransactionIso(wiso, 2);
			/*** End Retrieve Transaction ***/
			
			if(wiso.getISO_039_ResponseCode().equals("000")){
				
				iso.setISO_002_PAN(wiso.getISO_002_PAN());
				iso.setISO_003_ProcessingCode(wiso.getISO_003_ProcessingCode());
				iso.setISO_004_AmountTransaction(wiso.getISO_004_AmountTransaction());
				iso.setISO_102_AccountID_1(wiso.getISO_102_AccountID_1());
				iso.setISO_124_ExtendedData(wiso.getISO_124_ExtendedData());
				
				if(iso.getISO_039_ResponseCode().equals("000")){
					Iso8583 isoResponse = new Iso8583();
					csProcess processor = new csProcess();
					isoResponse = processor.ProcessTransactionMain(iso, IP);
					
					if(isoResponse != null){
						dtoResponseRevert = new DTOResponseRevert();
						if(isoResponse.getISO_039_ResponseCode().equals("000"))
							dtoResponseRevert.setOperationResult("1");
						else
							dtoResponseRevert.setOperationResult("2");
						dtoResponseRevert.setMessage(isoResponse.getISO_039p_ResponseDetail());
						dtoResponseRevert.setErrorCode(getHomologaError(isoResponse));
						dtoResponseRevert.setMessage(isoResponse.getISO_039p_ResponseDetail());
						dtoResponseRevert.setTransactionSequenceId(Long.parseLong(isoResponse.getISO_011_SysAuditNumber()));
						dtoResponseRevert.setExternalTransactionId(StringUtils.IsNullOrEmpty(isoResponse.getISO_038_AutorizationNumber()) ? "000000" 
											:  isoResponse.getISO_038_AutorizationNumber());
					}
				}
			}
			else{
				System.out.println("Entro DTOResponseRevert Revert....(3.2)");
				iso.setISO_039_ResponseCode("601");
				iso.setISO_039p_ResponseDetail("TRANSACCION A REVERSAR NO EXISTE");
				
				wiso.setISO_011_SysAuditNumber(iso.getISO_011_SysAuditNumber());
				//wiso.setWsTransactionConfig(new TransactionConfiguration());
				//wiso.getWsTransactionConfig().setIp(IP);
			    wiso.setTickAut(new StopWatch());
			    wiso.setTickBdd(new StopWatch());
			    wiso.setTickMidd(new StopWatch());
				
				
				dtoResponseRevert = new DTOResponseRevert();
				dtoResponseRevert.setOperationResult("2");
				dtoResponseRevert.setErrorCode(getHomologaError(iso));
				dtoResponseRevert.setMessage(iso.getISO_039p_ResponseDetail());
				dtoResponseRevert.setTransactionSequenceId(Long.parseLong(iso.getISO_011_SysAuditNumber()));
				dtoResponseRevert.setExternalTransactionId(StringUtils.IsNullOrEmpty(iso.getISO_038_AutorizationNumber()) ? "000000" 
											:  iso.getISO_038_AutorizationNumber());
				
				EndTransaction endTrx = new EndTransaction(wiso, iso, false);
				endTrx.start();
			}
		}else {
			System.out.println("Entro DTOResponseRevert Revert....(3.3)");
			dtoResponseRevert = new DTOResponseRevert();
			dtoResponseRevert.setOperationResult("2");
			dtoResponseRevert.setMessage(iso.getISO_039p_ResponseDetail());
			dtoResponseRevert.setErrorCode(getHomologaError(iso));
			dtoResponseRevert.setMessage(iso.getISO_039p_ResponseDetail());
			dtoResponseRevert.setTransactionSequenceId(0);
			dtoResponseRevert.setExternalTransactionId(StringUtils.IsNullOrEmpty(iso.getISO_038_AutorizationNumber()) ? "000000" 
								:  iso.getISO_038_AutorizationNumber());
		}
		
		log.WriteLog(dtoResponseRevert, TypeLog.bceacq, TypeWriteLog.file);
		return dtoResponseRevert;
	}

	public DTOResponseDebit Debit(DTORequestDebit debit, String IP) {
		
		log.WriteLog(debit, TypeLog.bceacq, TypeWriteLog.file);
		DTOResponseDebit dtoResponseDebit = null;
		Iso8583 iso = new Iso8583(debit);
		if(iso.getISO_039_ResponseCode().equals("000")){
			Iso8583 isoResponse = new Iso8583();
			csProcess processor = new csProcess();
			isoResponse = processor.ProcessTransactionMain(iso, IP);
			
			if(isoResponse != null){
				dtoResponseDebit = new DTOResponseDebit();
				if(isoResponse.getISO_039_ResponseCode().equals("000"))
					dtoResponseDebit.setOperationResult("1");
				else
					dtoResponseDebit.setOperationResult("2");
				dtoResponseDebit.setMessage(isoResponse.getISO_039p_ResponseDetail());
				dtoResponseDebit.setErrorCode(getHomologaError(isoResponse));
				dtoResponseDebit.setMessage(isoResponse.getISO_039p_ResponseDetail());
				dtoResponseDebit.setTransactionSequenceId(Long.parseLong(isoResponse.getISO_011_SysAuditNumber()));
				dtoResponseDebit.setExternalTransactionId(StringUtils.IsNullOrEmpty(isoResponse.getISO_038_AutorizationNumber()) ? "000000" 
									:  isoResponse.getISO_038_AutorizationNumber());
			}
		}
		else{
			
			dtoResponseDebit = new DTOResponseDebit();
			dtoResponseDebit.setOperationResult("2");
			dtoResponseDebit.setErrorCode(getHomologaError(iso));
			dtoResponseDebit.setMessage(iso.getISO_039p_ResponseDetail());
			dtoResponseDebit.setTransactionSequenceId(debit.getTransactionSequenceId());
			dtoResponseDebit.setExternalTransactionId(StringUtils.IsNullOrEmpty(iso.getISO_038_AutorizationNumber()) ? "000000" 
										:  iso.getISO_038_AutorizationNumber());
		}
		log.WriteLog(dtoResponseDebit, TypeLog.bceacq, TypeWriteLog.file);
		return dtoResponseDebit;
	}
	
	public DTOResponseIsValidAccount Valid(DTORequestIsValidAccount valid, String IP){
		
		log.WriteLog(valid, TypeLog.bceacq, TypeWriteLog.file);
		DTOResponseIsValidAccount dtoResponseValid = null;
		Iso8583 iso = new Iso8583(valid);
		if(iso.getISO_039_ResponseCode().equals("000")){
			Iso8583 isoResponse = new Iso8583();
			csProcess processor = new csProcess();
			isoResponse = processor.ProcessTransactionMain(iso, IP);
			
			if(isoResponse != null){
				dtoResponseValid = new DTOResponseIsValidAccount();
				if(isoResponse.getISO_039_ResponseCode().equals("000"))
					dtoResponseValid.setOperationResult("1");
				else
					dtoResponseValid.setOperationResult("2");
				dtoResponseValid.setMessage(isoResponse.getISO_039p_ResponseDetail());
				dtoResponseValid.setErrorCode(getHomologaError(isoResponse));
				dtoResponseValid.setMessage(isoResponse.getISO_039p_ResponseDetail());
				
			}
		}else {
			
			dtoResponseValid = new DTOResponseIsValidAccount();
			dtoResponseValid.setOperationResult("2");
			dtoResponseValid.setMessage(iso.getISO_039p_ResponseDetail());
			dtoResponseValid.setErrorCode(getHomologaError(iso));
			dtoResponseValid.setMessage(iso.getISO_039p_ResponseDetail());
		}
		
		log.WriteLog(dtoResponseValid, TypeLog.bceacq, TypeWriteLog.file);
		return dtoResponseValid;
	}
	
	protected String getHomologaError(Iso8583 iso){
		
		String error = null;
		try {
			
			switch (iso.getISO_039_ResponseCode()) {
			
			case "218":
			case "214":
				if(iso.getISO_003_ProcessingCode().equals("600001"))
					error = "41007";
				else
					error = "41000";
				break;
			case "116":
			case "216":
			case "507":
				error = "41002";
				break;
			case "120":
			case "123":
				error = "41003";
				break;
			case "215":
				error = "41004";
			case "217":
				error = "41008";
				break;
			case "114":
				error = "41001";
				break;
			case "601":
				error = "41005";
				break;
			case "606":
				error = "41006";
				break;
			case "602":
				error = "41006";
				break;
			default:
				error = "0";
				break;
			}
			
		} catch (Exception e) {
			
		}
		return error;
	}

}
