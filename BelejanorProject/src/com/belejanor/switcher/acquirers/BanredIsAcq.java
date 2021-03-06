package com.belejanor.switcher.acquirers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import com.belejanor.switcher.bimo.pacs.camt_998_202.AccountIdentification4Choice;
import com.belejanor.switcher.bimo.pacs.camt_998_202.AccountOrBusinessError2Choice;
import com.belejanor.switcher.bimo.pacs.camt_998_202.AccountOrOperationalError2Choice;
import com.belejanor.switcher.bimo.pacs.camt_998_202.AcctRp;
import com.belejanor.switcher.bimo.pacs.camt_998_202.ActiveOrHistoricCurrencyAndAmount;
import com.belejanor.switcher.bimo.pacs.camt_998_202.BalanceType8Choice;
import com.belejanor.switcher.bimo.pacs.camt_998_202.CashAccount23;
import com.belejanor.switcher.bimo.pacs.camt_998_202.CashBalance5;
import com.belejanor.switcher.bimo.pacs.camt_998_202.CreditDebitCode;
import com.belejanor.switcher.bimo.pacs.camt_998_202.GenericAccountIdentification1;
import com.belejanor.switcher.bimo.pacs.camt_998_202.PrtryData;
import com.belejanor.switcher.bimo.pacs.camt_998_202.StatusCode;
import com.belejanor.switcher.bimo.pacs.camt_998_202.SystemBalanceType1Code;
import com.belejanor.switcher.bimo.pacs.camt_998_212.AcctEnroll;
import com.belejanor.switcher.bimo.pacs.camt_998_212.BranchAndFinancialInstitutionIdentification5;
import com.belejanor.switcher.bimo.pacs.camt_998_212.GroupHeader;
import com.belejanor.switcher.bimo.pacs.camt_998_212.MjeData;
import com.belejanor.switcher.bimo.pacs.camt_998_212.OrigIdData;
import com.belejanor.switcher.bimo.pacs.camt_998_212.OriginalGroupInformation;
import com.belejanor.switcher.bimo.pacs.camt_998_212.Priority;
import com.belejanor.switcher.bimo.pacs.camt_998_212.ProprietaryMessageV02;
import com.belejanor.switcher.bimo.pacs.camt_998_212.Reason;
import com.belejanor.switcher.bimo.pacs.camt_998_212.RoRCod;
import com.belejanor.switcher.bimo.pacs.camt_998_212.ServiceData;
import com.belejanor.switcher.bimo.pacs.camt_998_212.SettlementInstruction4;
import com.belejanor.switcher.bimo.pacs.camt_998_212.SettlementMethod1Code;
import com.belejanor.switcher.bimo.pacs.camt_998_212.StatusReasonInformation;
import com.belejanor.switcher.bimo.pacs.camt_998_222.AcctUnEnroll;
import com.belejanor.switcher.bimo.pacs.camt_998_232.HeaderData;
import com.belejanor.switcher.bimo.pacs.camt_998_232.WithdrawNC;
import com.belejanor.switcher.bimo.pacs.pacs_002_022.Document;
import com.belejanor.switcher.bimo.pacs.pacs_002_022.FIToFIPaymentStatusReportV08;
import com.belejanor.switcher.bimo.pacs.pacs_002_022.OriginalGroupInformation3;
import com.belejanor.switcher.bimo.pacs.pacs_002_022.PaymentTransaction80;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.Config;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.StringUtils;

public class BanredIsAcq{

	private String Ip;
	private Logger log;
	private String codError;
	private String desError;
	
	public BanredIsAcq() {
		
		log = new Logger();
		codError = "000";
		desError = StringUtils.Empty();
	}
	
	public String getCodError() {
		return codError;
	}

	public void setCodError(String codError) {
		this.codError = codError;
	}

	public String getDesError() {
		return desError;
	}

	public void setDesError(String desError) {
		this.desError = desError;
	}
	
	public String getIp() {
		return Ip;
	}

	public void setIp(String ip) {
		Ip = ip;
	}

	@SuppressWarnings("unused")
	public com.belejanor.switcher.bimo.pacs.camt_998_212.Document processEnrollPerson(
			com.belejanor.switcher.bimo.pacs.camt_998_211.Document document){
		
		Iso8583 iso = null;
		com.belejanor.switcher.bimo.pacs.camt_998_212.Document documentResponse = null;
		try {
			
			documentResponse = new com.belejanor.switcher.bimo.pacs.camt_998_212.Document();
			iso = new Iso8583(document);
			if(iso.getISO_039_ResponseCode().equals("004")){
				 
				 Ref<Iso8583> ref = new Ref<Iso8583>(iso);
				 changesEnroll(ref);
				 iso = ref.get();
				 if(iso.getISO_039_ResponseCode().equals("004")){
					 
					 csProcess processor = new csProcess();
					 iso = processor.ProcessTransactionMain(iso, this.Ip);
					 if(!iso.getISO_039_ResponseCode().equals("078")) {
					 //if(true) {
						 //iso.setISO_039_ResponseCode("998");
						 //iso.setISO_039p_ResponseDetail("ACERQUESE A UNA DE NUESTRAS OFICINAS MAS CERCANAS");
						 //documentResponse = ResponseEnroll(document, iso);
						 documentResponse = ResponseEnroll(document, iso);
					 }else{
						 
						 this.codError = "100";
						 this.desError = "MSGID YA UTILIZADO EN ESTE DIA";
					 }
						 
				 }else{
					 
					 this.codError = iso.getISO_039_ResponseCode();
						this.desError = truncateResponse(iso.getISO_039p_ResponseDetail());
				 }
				 
			}else{
				
				this.codError = iso.getISO_039_ResponseCode();
				this.desError = truncateResponse(iso.getISO_039p_ResponseDetail());
			}
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false);
			log.WriteLogMonitor("Error modulo BanredIsAcq::processEnrollPerson ", TypeMonitor.error, e);
		}
		if(documentResponse != null){
			log.WriteOptimizeLog(documentResponse, TypeLog.brdAcq, 
				 com.belejanor.switcher.bimo.pacs.camt_998_212.Document.class, true);
		}
		return documentResponse;
	}

	public com.belejanor.switcher.bimo.pacs.camt_998_222.Document processDesenrollPerson
	        (com.belejanor.switcher.bimo.pacs.camt_998_221.Document document){
		
		Iso8583 iso = null;
		com.belejanor.switcher.bimo.pacs.camt_998_222.Document documentResponse = null;
		try {
			
			iso = new Iso8583(document);
			if(iso.getISO_039_ResponseCode().equals("004")){
				 
				 csProcess processor = new csProcess();
				 iso = processor.ProcessTransactionMain(iso, this.Ip);
				 if(!iso.getISO_039_ResponseCode().equals("078"))
				 	documentResponse = ResponseDesEnroll(document, iso);
				 else{
					 
					 this.codError = "100";
					 this.desError = "MSGID YA UTILIZADO EN ESTE DIA";
				 }
			}else{
				
				this.codError = iso.getISO_039_ResponseCode();
				this.desError = truncateResponse(iso.getISO_039p_ResponseDetail());
			}
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAcq::processDesenrollPerson ", TypeMonitor.error, e);
		}
		if(documentResponse != null){
			log.WriteOptimizeLog(documentResponse, TypeLog.brdAcq, 
					 com.belejanor.switcher.bimo.pacs.camt_998_222.Document.class, true);
		}
		return documentResponse;
	}
	
	public com.belejanor.switcher.bimo.pacs.camt_998_202.Document processQueryBalance
    						(com.belejanor.switcher.bimo.pacs.camt_998_201.Document document){

		Iso8583 iso = null;
		com.belejanor.switcher.bimo.pacs.camt_998_202.Document documentResponse = null;
		try {
			
			iso = new Iso8583(document);
			if(iso.getISO_039_ResponseCode().equals("004")){
				 
				 csProcess processor = new csProcess();
				 iso = processor.ProcessTransactionMain(iso, this.Ip);
				 if(!iso.getISO_039_ResponseCode().equals("078"))
				 	documentResponse = ResponseQueryBalance(document, iso);
				 else{
					 
					 this.codError = "100";
					 this.desError = "MSGID YA UTILIZADO EN ESTE DIA";
				 }
					
			}else{
				
				this.codError = iso.getISO_039_ResponseCode();
				this.desError = truncateResponse(iso.getISO_039p_ResponseDetail());
			}
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAcq::processQueryBalance ", TypeMonitor.error, e);
		}
		if(documentResponse != null){
			log.WriteOptimizeLog(documentResponse, TypeLog.brdAcq, 
					 com.belejanor.switcher.bimo.pacs.camt_998_202.Document.class, true);
		}
		return documentResponse;
	}
	
	public com.belejanor.switcher.bimo.pacs.pacs_002_022.Document processCredit
									(com.belejanor.switcher.bimo.pacs.pacs_008_021.Document document){

		Iso8583 iso = null;
		com.belejanor.switcher.bimo.pacs.pacs_002_022.Document documentResponse = null;
		try {
			//Thread.sleep(20000); /*Pruebas*/
			iso = new Iso8583(document);
			if(iso.getISO_039_ResponseCode().equals("004")){
			
				csProcess processor = new csProcess();
				iso = processor.ProcessTransactionMain(iso, this.Ip);
				if(!iso.getISO_039_ResponseCode().equals("078"))
					documentResponse = ResponseCredit(document, iso);
				else{
				
					this.codError = "100";
					this.desError = "MSGID YA UTILIZADO EN ESTE DIA";
				}
			}else{
			
				this.codError = iso.getISO_039_ResponseCode();
				this.desError = truncateResponse(iso.getISO_039p_ResponseDetail());
			}
			
			} catch (Exception e) {
				
				this.codError = "070";
				this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
				log.WriteLogMonitor("Error modulo BanredIsAcq::processCredit ", TypeMonitor.error, e);
			}
			if(documentResponse != null){
				
				log.WriteOptimizeLog(documentResponse, TypeLog.brdAcq, 
				com.belejanor.switcher.bimo.pacs.pacs_002_022.Document.class, true);
			}
		
		return documentResponse;
    }
	
	public com.belejanor.switcher.bimo.pacs.pacs_002_042.Document processReverCredit
						(com.belejanor.switcher.bimo.pacs.pacs_007_041.Document document){
		
		Iso8583 iso = null;
		com.belejanor.switcher.bimo.pacs.pacs_002_042.Document documentResponse = null;
		try {
		
			iso = new Iso8583(document);
			if(iso.getISO_039_ResponseCode().equals("004")){
			
				csProcess processor = new csProcess();
				iso = processor.ProcessTransactionMain(iso, this.Ip);
				if(!iso.getISO_039_ResponseCode().equals("078"))
					documentResponse = ResponseReverCredit(document, iso);
				else{
				
					this.codError = "100";
					this.desError = "MSGID YA UTILIZADO EN ESTE DIA";
				}
			}else{
			
				this.codError = iso.getISO_039_ResponseCode();
				this.desError = truncateResponse(iso.getISO_039p_ResponseDetail());
			}
			
			} catch (Exception e) {
				
				this.codError = "070";
				this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
				log.WriteLogMonitor("Error modulo BanredIsAcq::processReverCredit ", TypeMonitor.error, e);
			}
			if(documentResponse != null){
				
				log.WriteOptimizeLog(documentResponse, TypeLog.brdAcq, 
				com.belejanor.switcher.bimo.pacs.pacs_002_042.Document.class, true);
			}
		
		return documentResponse;
	}
	
	public com.belejanor.switcher.bimo.pacs.pacs_002_072.Document processDebit
												(com.belejanor.switcher.bimo.pacs.pacs_008_071.Document document){

		
		Iso8583 iso = null;
		com.belejanor.switcher.bimo.pacs.pacs_002_072.Document documentResponse = null;
		try {
			//Thread.sleep(40000); /*Pruebas*/
			
			iso = new Iso8583(document);
			if(iso.getISO_039_ResponseCode().equals("004")){
			csProcess processor = new csProcess();
			iso = processor.ProcessTransactionMain(iso, this.Ip);
			
				if(!iso.getISO_039_ResponseCode().equals("078"))
					documentResponse = ResponseDebit(document, iso);
				else{
				
					this.codError = "100";
					this.desError = "MSGID YA UTILIZADO EN ESTE DIA";
				}
				
			}else{
			
				this.codError = iso.getISO_039_ResponseCode();
				this.desError = truncateResponse(iso.getISO_039p_ResponseDetail());
			}
		
		} catch (Exception e) {
		
			this.codError = "070";
			this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAcq::processDebit ", TypeMonitor.error, e);
		}
		if(documentResponse != null){
		
			log.WriteOptimizeLog(documentResponse, TypeLog.brdAcq, 
			com.belejanor.switcher.bimo.pacs.pacs_002_072.Document.class, true);
		}
		
		return documentResponse;
	}
	
	public com.belejanor.switcher.bimo.pacs.pacs_002_052.Document processReverDebit
				(com.belejanor.switcher.bimo.pacs.pacs_007_051.Document document){
		
		Iso8583 iso = null;
		com.belejanor.switcher.bimo.pacs.pacs_002_052.Document documentResponse = null;
		try {
		
			iso = new Iso8583(document);
			if(iso.getISO_039_ResponseCode().equals("004")){
			
				csProcess processor = new csProcess();
				iso = processor.ProcessTransactionMain(iso, this.Ip);
				if(!iso.getISO_039_ResponseCode().equals("078"))
					documentResponse = ResponseReverDebit(document, iso);
				else{
				
					this.codError = "100";
					this.desError = "MSGID YA UTILIZADO EN ESTE DIA";
				}
			}else{
			
				this.codError = iso.getISO_039_ResponseCode();
				this.desError = truncateResponse(iso.getISO_039p_ResponseDetail());
			}
			
			} catch (Exception e) {
				
				this.codError = "070";
				this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
				log.WriteLogMonitor("Error modulo BanredIsAcq::processReverCredit ", TypeMonitor.error, e);
			}
			if(documentResponse != null){
				
				log.WriteOptimizeLog(documentResponse, TypeLog.brdAcq, 
				com.belejanor.switcher.bimo.pacs.pacs_002_052.Document.class, true);
			}
		
		return documentResponse;
	}
	
	/*Generacion OTP Solo aplica a Cooperativas que generaran su propio OTP*/
	public com.belejanor.switcher.bimo.pacs.camt_998_232.Document processGenerateOTP
	                             (com.belejanor.switcher.bimo.pacs.camt_998_231.Document document){

		Iso8583 iso = null;
		com.belejanor.switcher.bimo.pacs.camt_998_232.Document documentResponse = null;
		try {
		
			//iso = new Iso8583(document);
			iso = new Iso8583();
			iso.setISO_039_ResponseCode("057");
			iso.setISO_039p_ResponseDetail("TRANSACCION NO SOPORTADA");
			//if(iso.getISO_039_ResponseCode().equals("004")){
			
				//Temporalmente se desactiva para Cooperativas que no soportan Generacion de OTP
				/*csProcess processor = new csProcess();
				iso = processor.ProcessTransactionMain(iso, this.Ip);
				if(!iso.getISO_039_ResponseCode().equals("078"))*/
				
				documentResponse = ResponseGeneraOTP(document, iso);
				
			//}else{
			
			//	this.codError = "100";
			//	this.desError = "MSGID YA UTILIZADO EN ESTE DIA";
			//}
			
		
		}catch (Exception e) {
		
			this.codError = "070";
			this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAcq::processGenerateOTP ", TypeMonitor.error, e);	
		}
		if(documentResponse != null){
			log.WriteOptimizeLog(documentResponse, TypeLog.brdAcq, 
					com.belejanor.switcher.bimo.pacs.camt_998_232.Document.class, true);
		}  
		return documentResponse;
    }
	

	/*ZONA RESPUESTAS*/
	private com.belejanor.switcher.bimo.pacs.camt_998_212.Document ResponseEnroll
	                            (com.belejanor.switcher.bimo.pacs.camt_998_211.Document document, 
	                             Iso8583 iso){
		com.belejanor.switcher.bimo.pacs.camt_998_212.Document docResponse = null;
		String IdMessage = MemoryGlobal.IdBIMOEfi + FormatUtils.DateToString(new Date(), "yyyyMMddHHmmss");
		XMLGregorianCalendar date = FormatUtils.DateToXMLGregorian(new Date());
		try {
			
			docResponse = new com.belejanor.switcher.bimo.pacs.camt_998_212.Document();
			com.belejanor.switcher.bimo.pacs.camt_998_212.HeaderData hd = new 
					com.belejanor.switcher.bimo.pacs.camt_998_212.HeaderData();
			ProprietaryMessageV02 propMsg = new ProprietaryMessageV02();
			AcctEnroll accEnroll = new AcctEnroll();
			/*Header Data*/
			OrigIdData orgHd = new OrigIdData();
			orgHd.setChannel(document.getHeader().getOrigId().getChannel());
			orgHd.setApp(document.getHeader().getOrigId().getApp());
			ServiceData servOrgHd = new ServiceData();
			servOrgHd.setIdServ("Enrollment RS");
			servOrgHd.setVersServ(document.getHeader().getOrigId().getService().getVersServ());
			orgHd.setService(servOrgHd);
			orgHd.setOtherId(document.getHeader().getOrigId().getOtherId());
			hd.setSender(document.getHeader().getReceiver()); //Al reves ahora el que recibe es el que envia y viceversa
			hd.setReceiver(document.getHeader().getSender()); 
			MjeData  mjdata = new MjeData();
			mjdata.setType("camt.998.212");
			mjdata.setRoR(RoRCod.RESP);
			mjdata.setIdMge(IdMessage);
			mjdata.setOpeDate(date);
			hd.setMge(mjdata);
			hd.setOrigId(orgHd);
			hd.setPssblDplct(document.getHeader().isPssblDplct());
			Priority prty = null;
			switch (document.getHeader().getPrty()) {
			case URGT:
				prty = Priority.URGT;
				break;
			case HIGH:
				prty = Priority.HIGH;
				break;
			case NORM:
				prty = Priority.NORM;
				break;
			case LOWW:
				prty = Priority.LOWW;
				break;
			default:
				break;
			}
			hd.setPrty(prty);
			GroupHeader grHd = new GroupHeader();
			grHd.setMsgId(IdMessage);
			grHd.setCreDtTm(date);
			grHd.setNbOfTxs(document.getPrtryMsg().getGrpHdr().getNbOfTxs());
			SettlementMethod1Code sttMeth = null;
			switch (document.getPrtryMsg().getGrpHdr().getSttInf().getSttlmMtd()) {
			case INDA:
				sttMeth = SettlementMethod1Code.INDA;
				break;
			case INGA:
				sttMeth = SettlementMethod1Code.INGA;
				break;
			case CLRG:
				sttMeth = SettlementMethod1Code.CLRG;
				break;
			case COVE:
				sttMeth = SettlementMethod1Code.COVE;
				break;
			default:
				break;
			}
			SettlementInstruction4 sttIns4 = new SettlementInstruction4();
			sttIns4.setSttlmMtd(sttMeth);
			grHd.setSttInf(sttIns4);
			
			BranchAndFinancialInstitutionIdentification5 eff = new BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.camt_998_212.ZFinancialInstitutionIdentification efi = new 
					com.belejanor.switcher.bimo.pacs.camt_998_212.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_212.ZGenericFinancialIdentification zgen = new 
					com.belejanor.switcher.bimo.pacs.camt_998_212.ZGenericFinancialIdentification();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			efi.setOthr(zgen);
			eff.setFinInstnId(efi);
			
			
			BranchAndFinancialInstitutionIdentification5 eff2 = new BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.camt_998_212.ZFinancialInstitutionIdentification efi2 = new 
					com.belejanor.switcher.bimo.pacs.camt_998_212.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_212.ZGenericFinancialIdentification zgen2 = new 
					com.belejanor.switcher.bimo.pacs.camt_998_212.ZGenericFinancialIdentification();
			zgen2.setId(MemoryGlobal.IdBIMOBanred);
			efi2.setOthr(zgen2);
			eff2.setFinInstnId(efi2);
			grHd.setInstdAgt(eff);
			grHd.setInstgAgt(eff2);			
			propMsg.setGrpHdr(grHd);
			
			OriginalGroupInformation orgId = new OriginalGroupInformation();
			orgId.setOrgnlMsgId(document.getPrtryMsg().getGrpHdr().getMsgId());
			orgId.setOrgnlCreDtTm(document.getPrtryMsg().getGrpHdr().getCreDtTm());
			orgId.setOrgnlMsgNmId("camt.998.211");
			
			accEnroll.setOrgnlGrpInf(orgId);
			
			if(iso.getISO_039_ResponseCode().equals("000")){
				accEnroll.setTxSts(com.belejanor.switcher.bimo.pacs.camt_998_212.StatusCode.OK);
				accEnroll.setAccountId(iso.getISO_124_ExtendedData());
			}
			else{
				accEnroll.setTxSts(com.belejanor.switcher.bimo.pacs.camt_998_212.StatusCode.ERR);
				accEnroll.setAccountId(document.getPrtryMsg().getAcctEnroll().getAcctInfo());
			}
			com.belejanor.switcher.bimo.pacs.camt_998_212.StatusReasonInformation stsInfo = new
					StatusReasonInformation();
			com.belejanor.switcher.bimo.pacs.camt_998_212.Reason rsn = new Reason();
			rsn.setCode(codeHomolog(iso));
			List<String> lstatusDetail =  new ArrayList<>();
			lstatusDetail.add(truncateResponse(iso.getISO_039p_ResponseDetail()
					.replace("<**>", StringUtils.Empty())
					.replace("<*>", StringUtils.Empty())));
			rsn.setAddtlInf(lstatusDetail);
			stsInfo.setRsn(rsn);
			
			accEnroll.setStsRsnInf(stsInfo);
			
			propMsg.setGrpHdr(grHd);
			propMsg.setAcctEnroll(accEnroll);
			
			docResponse.setHeader(hd);
			docResponse.setPrtryMsg(propMsg);
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESO", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAcq::ResponseEnroll ", TypeMonitor.error, e);
		}
		
		return docResponse;
	}
	
	private com.belejanor.switcher.bimo.pacs.camt_998_222.Document ResponseDesEnroll
					(com.belejanor.switcher.bimo.pacs.camt_998_221.Document document, Iso8583 iso){
		
		com.belejanor.switcher.bimo.pacs.camt_998_222.Document docResponse = null;
		String IdMessage = MemoryGlobal.IdBIMOEfi + FormatUtils.DateToString(new Date(), "yyyyMMddHHmmss");
		XMLGregorianCalendar date = FormatUtils.DateToXMLGregorian(new Date());
		try {
		
		docResponse = new com.belejanor.switcher.bimo.pacs.camt_998_222.Document();
		com.belejanor.switcher.bimo.pacs.camt_998_222.HeaderData hd = new 
		com.belejanor.switcher.bimo.pacs.camt_998_222.HeaderData();
		com.belejanor.switcher.bimo.pacs.camt_998_222.ProprietaryMessageV02 propMsg = new 
				com.belejanor.switcher.bimo.pacs.camt_998_222.ProprietaryMessageV02();
		
		com.belejanor.switcher.bimo.pacs.camt_998_222.AcctUnEnroll accEnroll = new AcctUnEnroll();
		/*Header Data*/
		com.belejanor.switcher.bimo.pacs.camt_998_222.OrigIdData orgHd = new 
				com.belejanor.switcher.bimo.pacs.camt_998_222.OrigIdData();
		orgHd.setChannel(document.getHeader().getOrigId().getChannel());
		orgHd.setApp(document.getHeader().getOrigId().getApp());
		com.belejanor.switcher.bimo.pacs.camt_998_222.ServiceData servOrgHd = new 
				com.belejanor.switcher.bimo.pacs.camt_998_222.ServiceData();
		servOrgHd.setIdServ("UnEnrollment RS");
		servOrgHd.setVersServ(document.getHeader().getOrigId().getService().getVersServ());
		orgHd.setService(servOrgHd);
		orgHd.setOtherId(document.getHeader().getOrigId().getOtherId());
		hd.setSender(document.getHeader().getReceiver()); //Al reves ahora el que recibe es el que envia y viceversa
		hd.setReceiver(document.getHeader().getSender()); 
		com.belejanor.switcher.bimo.pacs.camt_998_222.MjeData  mjdata = new 
				com.belejanor.switcher.bimo.pacs.camt_998_222.MjeData();
		mjdata.setType("camt.998.222");
		mjdata.setRoR(com.belejanor.switcher.bimo.pacs.camt_998_222.RoRCod.RESP);
		mjdata.setIdMge(IdMessage);
		mjdata.setOpeDate(date);
		hd.setMge(mjdata);
		hd.setOrigId(orgHd);
		hd.setPssblDplct(document.getHeader().isPssblDplct());
		com.belejanor.switcher.bimo.pacs.camt_998_222.Priority prty = null;
		switch (document.getHeader().getPrty()) {
		case URGT:
			prty = com.belejanor.switcher.bimo.pacs.camt_998_222.Priority.URGT;
		break;
		case HIGH:
			prty = com.belejanor.switcher.bimo.pacs.camt_998_222.Priority.HIGH;
		break;
		case NORM:
			prty = com.belejanor.switcher.bimo.pacs.camt_998_222.Priority.NORM;
		break;
			case LOWW:
		prty = com.belejanor.switcher.bimo.pacs.camt_998_222.Priority.LOWW;
			break;
		default:
			break;
		}
		hd.setPrty(prty);
		com.belejanor.switcher.bimo.pacs.camt_998_222.GroupHeader grHd = 
				new com.belejanor.switcher.bimo.pacs.camt_998_222.GroupHeader();
		grHd.setMsgId(IdMessage);
		grHd.setCreDtTm(date);
		grHd.setNbOfTxs(document.getPrtryMsg().getGrpHdr().getNbOfTxs());
		com.belejanor.switcher.bimo.pacs.camt_998_222.SettlementMethod1Code sttMeth = null;
		
		switch (document.getPrtryMsg().getGrpHdr().getSttInf().getSttlmMtd()) {
		case INDA:
			sttMeth = com.belejanor.switcher.bimo.pacs.camt_998_222.SettlementMethod1Code.INDA;
		break;
		case INGA:
			sttMeth = com.belejanor.switcher.bimo.pacs.camt_998_222.SettlementMethod1Code.INGA;
		break;
		case CLRG:
			sttMeth = com.belejanor.switcher.bimo.pacs.camt_998_222.SettlementMethod1Code.CLRG;
		break;
		case COVE:
			sttMeth = com.belejanor.switcher.bimo.pacs.camt_998_222.SettlementMethod1Code.COVE;
		break;
		
		default:
		break;
		}
		com.belejanor.switcher.bimo.pacs.camt_998_222.SettlementInstruction4 sttIns4 = 
				new com.belejanor.switcher.bimo.pacs.camt_998_222.SettlementInstruction4();
		sttIns4.setSttlmMtd(sttMeth);
		grHd.setSttInf(sttIns4);
		
		com.belejanor.switcher.bimo.pacs.camt_998_222.BranchAndFinancialInstitutionIdentification5 eff = 
				new com.belejanor.switcher.bimo.pacs.camt_998_222.BranchAndFinancialInstitutionIdentification5();
		com.belejanor.switcher.bimo.pacs.camt_998_222.ZFinancialInstitutionIdentification efi = new 
				com.belejanor.switcher.bimo.pacs.camt_998_222.ZFinancialInstitutionIdentification();
		com.belejanor.switcher.bimo.pacs.camt_998_222.ZGenericFinancialIdentification zgen = new 
				com.belejanor.switcher.bimo.pacs.camt_998_222.ZGenericFinancialIdentification();
		zgen.setId(MemoryGlobal.IdBIMOEfi);
		efi.setOthr(zgen);
		eff.setFinInstnId(efi);
		
		
		com.belejanor.switcher.bimo.pacs.camt_998_222.BranchAndFinancialInstitutionIdentification5 eff2 = 
				new com.belejanor.switcher.bimo.pacs.camt_998_222.BranchAndFinancialInstitutionIdentification5();
		com.belejanor.switcher.bimo.pacs.camt_998_222.ZFinancialInstitutionIdentification efi2 = new 
				com.belejanor.switcher.bimo.pacs.camt_998_222.ZFinancialInstitutionIdentification();
		com.belejanor.switcher.bimo.pacs.camt_998_222.ZGenericFinancialIdentification zgen2 = new 
				com.belejanor.switcher.bimo.pacs.camt_998_222.ZGenericFinancialIdentification();
		zgen2.setId(MemoryGlobal.IdBIMOBanred);
		efi2.setOthr(zgen2);
		eff2.setFinInstnId(efi2);
		
		
		grHd.setInstdAgt(eff);
		grHd.setInstgAgt(eff2);			
		propMsg.setGrpHdr(grHd);
		
		com.belejanor.switcher.bimo.pacs.camt_998_222.OriginalGroupInformation orgId = new 
					com.belejanor.switcher.bimo.pacs.camt_998_222.OriginalGroupInformation();
		orgId.setOrgnlMsgId(document.getPrtryMsg().getGrpHdr().getMsgId());
		orgId.setOrgnlCreDtTm(document.getPrtryMsg().getGrpHdr().getCreDtTm());
		orgId.setOrgnlMsgNmId("camt.998.221");
		
		accEnroll.setOrgnlGrpInf(orgId);
		
		if(iso.getISO_039_ResponseCode().equals("000"))
			
			accEnroll.setTxSts(com.belejanor.switcher.bimo.pacs.camt_998_222.StatusCode.OK);
		else
			accEnroll.setTxSts(com.belejanor.switcher.bimo.pacs.camt_998_222.StatusCode.ERR);
			
		com.belejanor.switcher.bimo.pacs.camt_998_222.StatusReasonInformation stsInfo = new
				com.belejanor.switcher.bimo.pacs.camt_998_222.StatusReasonInformation();
		com.belejanor.switcher.bimo.pacs.camt_998_222.Reason rsn = new 
				com.belejanor.switcher.bimo.pacs.camt_998_222.Reason();
		rsn.setCode(codeHomolog(iso));
		List<String> lstatusDetail =  new ArrayList<>();
		lstatusDetail.add(truncateResponse(iso.getISO_039p_ResponseDetail()
				.replace("<**>", StringUtils.Empty())
				.replace("<*>", StringUtils.Empty())));
		rsn.setAddtlInf(lstatusDetail);
		stsInfo.setRsn(rsn);
		
		accEnroll.setStsRsnInf(stsInfo);
		
		propMsg.setGrpHdr(grHd);
		propMsg.setAcctUnEnroll(accEnroll);
		
		docResponse.setHeader(hd);
		docResponse.setPrtryMsg(propMsg);
		
		} catch (Exception e) {
		
		this.codError = "070";
		this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESO", e, false));
		log.WriteLogMonitor("Error modulo BanredIsAcq::ResponseEnroll ", TypeMonitor.error, e);
		}
		
		return docResponse;
	}
	
	private com.belejanor.switcher.bimo.pacs.camt_998_202.Document ResponseQueryBalance
			(com.belejanor.switcher.bimo.pacs.camt_998_201.Document document, Iso8583 iso){
		
		com.belejanor.switcher.bimo.pacs.camt_998_202.Document docResponse = null;
		String IdMessage = MemoryGlobal.IdBIMOEfi + FormatUtils.DateToString(new Date(), "yyyyMMddHHmmss");
		XMLGregorianCalendar date = FormatUtils.DateToXMLGregorian(new Date());
		try {
		
		docResponse = new com.belejanor.switcher.bimo.pacs.camt_998_202.Document();
		com.belejanor.switcher.bimo.pacs.camt_998_202.HeaderData hd = new 
		com.belejanor.switcher.bimo.pacs.camt_998_202.HeaderData();
		com.belejanor.switcher.bimo.pacs.camt_998_202.ProprietaryMessageV02 propMsg = new 
				com.belejanor.switcher.bimo.pacs.camt_998_202.ProprietaryMessageV02();
		
		/*Header Data*/
		com.belejanor.switcher.bimo.pacs.camt_998_202.OrigIdData orgHd = new 
				com.belejanor.switcher.bimo.pacs.camt_998_202.OrigIdData();
		orgHd.setChannel(document.getHeader().getOrigId().getChannel());
		orgHd.setApp(document.getHeader().getOrigId().getApp());
		com.belejanor.switcher.bimo.pacs.camt_998_202.ServiceData servOrgHd = new 
				com.belejanor.switcher.bimo.pacs.camt_998_202.ServiceData();
		servOrgHd.setIdServ("Consulta Saldo RS");
		servOrgHd.setVersServ(document.getHeader().getOrigId().getService().getVersServ());
		orgHd.setService(servOrgHd);
		orgHd.setOtherId(document.getHeader().getOrigId().getOtherId());
		hd.setSender(document.getHeader().getReceiver()); //Al reves ahora el que recibe es el que envia y viceversa
		hd.setReceiver(document.getHeader().getSender()); 
		com.belejanor.switcher.bimo.pacs.camt_998_202.MjeData  mjdata = new 
				com.belejanor.switcher.bimo.pacs.camt_998_202.MjeData();
		mjdata.setType("camt.998.202");
		mjdata.setRoR(com.belejanor.switcher.bimo.pacs.camt_998_202.RoRCod.RESP);
		mjdata.setIdMge(IdMessage);
		mjdata.setOpeDate(date);
		hd.setMge(mjdata);
		hd.setOrigId(orgHd);
		hd.setPssblDplct(document.getHeader().isPssblDplct());
		com.belejanor.switcher.bimo.pacs.camt_998_202.Priority prty = null;
		switch (document.getHeader().getPrty()) {
		case URGT:
			prty = com.belejanor.switcher.bimo.pacs.camt_998_202.Priority.URGT;
		break;
		case HIGH:
			prty = com.belejanor.switcher.bimo.pacs.camt_998_202.Priority.HIGH;
		break;
		case NORM:
			prty = com.belejanor.switcher.bimo.pacs.camt_998_202.Priority.NORM;
		break;
			case LOWW:
		prty = com.belejanor.switcher.bimo.pacs.camt_998_202.Priority.LOWW;
			break;
		default:
			break;
		}
		hd.setPrty(prty);
		com.belejanor.switcher.bimo.pacs.camt_998_202.GroupHeader grHd = 
				new com.belejanor.switcher.bimo.pacs.camt_998_202.GroupHeader();
		grHd.setMsgId(IdMessage);
		grHd.setCreDtTm(date);
		grHd.setNbOfTxs(document.getPrtryMsg().getGrpHdr().getNbOfTxs());
		com.belejanor.switcher.bimo.pacs.camt_998_202.SettlementMethod1Code sttMeth = null;
		
		switch (document.getPrtryMsg().getGrpHdr().getSttInf().getSttlmMtd()) {
		case INDA:
			sttMeth = com.belejanor.switcher.bimo.pacs.camt_998_202.SettlementMethod1Code.INDA;
		break;
		case INGA:
			sttMeth = com.belejanor.switcher.bimo.pacs.camt_998_202.SettlementMethod1Code.INGA;
		break;
		case CLRG:
			sttMeth = com.belejanor.switcher.bimo.pacs.camt_998_202.SettlementMethod1Code.CLRG;
		break;
		case COVE:
			sttMeth = com.belejanor.switcher.bimo.pacs.camt_998_202.SettlementMethod1Code.COVE;
		break;
		
		default:
		break;
		}
		
		PrtryData prtData = new PrtryData();
		
		com.belejanor.switcher.bimo.pacs.camt_998_202.SettlementInstruction4 sttIns4 = 
				new com.belejanor.switcher.bimo.pacs.camt_998_202.SettlementInstruction4();
		sttIns4.setSttlmMtd(sttMeth);
		grHd.setSttInf(sttIns4);
		
		com.belejanor.switcher.bimo.pacs.camt_998_202.BranchAndFinancialInstitutionIdentification5 eff = 
				new com.belejanor.switcher.bimo.pacs.camt_998_202.BranchAndFinancialInstitutionIdentification5();
		com.belejanor.switcher.bimo.pacs.camt_998_202.ZFinancialInstitutionIdentification efi = new 
				com.belejanor.switcher.bimo.pacs.camt_998_202.ZFinancialInstitutionIdentification();
		com.belejanor.switcher.bimo.pacs.camt_998_202.ZGenericFinancialIdentification zgen = new 
				com.belejanor.switcher.bimo.pacs.camt_998_202.ZGenericFinancialIdentification();
		zgen.setId(MemoryGlobal.IdBIMOEfi);
		efi.setOthr(zgen);
		eff.setFinInstnId(efi);
		
		
		com.belejanor.switcher.bimo.pacs.camt_998_202.BranchAndFinancialInstitutionIdentification5 eff2 = 
				new com.belejanor.switcher.bimo.pacs.camt_998_202.BranchAndFinancialInstitutionIdentification5();
		com.belejanor.switcher.bimo.pacs.camt_998_202.ZFinancialInstitutionIdentification efi2 = new 
				com.belejanor.switcher.bimo.pacs.camt_998_202.ZFinancialInstitutionIdentification();
		com.belejanor.switcher.bimo.pacs.camt_998_202.ZGenericFinancialIdentification zgen2 = new 
				com.belejanor.switcher.bimo.pacs.camt_998_202.ZGenericFinancialIdentification();
		zgen2.setId(MemoryGlobal.IdBIMOBanred);
		efi2.setOthr(zgen2);
		eff2.setFinInstnId(efi2);
		
		
		grHd.setInstdAgt(eff);
		grHd.setInstgAgt(eff2);			
		propMsg.setGrpHdr(grHd);
		
		com.belejanor.switcher.bimo.pacs.camt_998_202.OriginalGroupInformation orgId = new 
					com.belejanor.switcher.bimo.pacs.camt_998_202.OriginalGroupInformation();
		orgId.setOrgnlMsgId(document.getPrtryMsg().getGrpHdr().getMsgId());
		orgId.setOrgnlCreDtTm(document.getPrtryMsg().getGrpHdr().getCreDtTm());
		orgId.setOrgnlMsgNmId("camt.998.201");
		
		
		if(iso.getISO_039_ResponseCode().equals("000"))
			prtData.setTxSts(StatusCode.OK);
		else
			prtData.setTxSts(StatusCode.ERR);
		
	
		com.belejanor.switcher.bimo.pacs.camt_998_202.StatusReasonInformation stsInfo = new
				com.belejanor.switcher.bimo.pacs.camt_998_202.StatusReasonInformation();
		com.belejanor.switcher.bimo.pacs.camt_998_202.Reason rsn = new 
				com.belejanor.switcher.bimo.pacs.camt_998_202.Reason();
		rsn.setCode(codeHomolog(iso));
		List<String> lstatusDetail =  new ArrayList<>();
		lstatusDetail.add(truncateResponse(iso.getISO_039p_ResponseDetail()
				.replace("<**>", StringUtils.Empty())
				.replace("<*>", StringUtils.Empty())));
		rsn.setAddtlInf(lstatusDetail);
		stsInfo.setRsn(rsn);
		
		AccountOrOperationalError2Choice accerr = new AccountOrOperationalError2Choice();
		AcctRp accrp = new AcctRp();
		AccountIdentification4Choice accId = new AccountIdentification4Choice();
		GenericAccountIdentification1 gnAcc = new GenericAccountIdentification1();
		AccountOrBusinessError2Choice accErrBuss = new AccountOrBusinessError2Choice();
		List<CashAccount23> lstCash = new ArrayList<>();
		CashAccount23 cashAcc = new CashAccount23();
		CashBalance5 cashBalance = new CashBalance5();
		
		
		gnAcc.setId(iso.getISO_124_ExtendedData()); //AccountInfoOriginal
		accId.setOthr(gnAcc);
		accrp.setAcctId(accId);
		
		if(!StringUtils.IsNullOrEmpty(iso.getISO_044_AddRespData()))
			cashAcc.setNm(Arrays.asList(iso.getISO_044_AddRespData().split("\\|")).get(3)); //Obtengo el nombre
		else
			cashAcc.setNm("BENEFICIARIO - " + iso.getISO_023_CardSeq());
		if(iso.getISO_003_ProcessingCode().equals("311000"))
			cashAcc.setPrDescr("AHORROS");
		else
			cashAcc.setPrDescr("CORRIENTE");
		
		ActiveOrHistoricCurrencyAndAmount amm = new ActiveOrHistoricCurrencyAndAmount();
		amm.setCcy("USD");
		
		double saldo = !StringUtils.IsNullOrEmpty(iso.getISO_054_AditionalAmounts())?Double.parseDouble(iso.getISO_054_AditionalAmounts()):0;
		amm.setValue(BigDecimal.valueOf(saldo));
		cashBalance.setAmt(amm);
		if(iso.getISO_104_TranDescription().equals("C"))
			cashBalance.setCdtDbtInd(CreditDebitCode.CRDT);
		else
			cashBalance.setCdtDbtInd(CreditDebitCode.DBIT);
		BalanceType8Choice balance8 = new BalanceType8Choice();
		balance8.setCd(SystemBalanceType1Code.SALDO);
		cashBalance.setTp(balance8);
		cashBalance.setValDt(FormatUtils.getDateXMLGregorianSimple(FormatUtils
				.DateToString(new Date(), "yyyy-MM-dd")));
		
		cashAcc.setMulBal(cashBalance);
		
		lstCash.add(cashAcc);
		
		accErrBuss.setPhone(iso.getISO_023_CardSeq());
		accErrBuss.setAcct(lstCash);
		accrp.setAcctOrErr(accErrBuss);
		accrp.setAcctId(accId);
		accerr.setAcctRpt(accrp);
		
		
		prtData.setOrgnlGrpInf(orgId);
		prtData.setStsRsnInf(stsInfo);
		prtData.setRptOrErr(accerr);
		
		propMsg.setGrpHdr(grHd);
		propMsg.setPrtryData(prtData);
		
		docResponse.setHeader(hd);
		docResponse.setPrtryMsg(propMsg);
		
		} catch (Exception e) {
		
			this.codError = "070";
			this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESO", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAcq::ResponseEnroll ", TypeMonitor.error, e);
		}
		
		return docResponse;
	}
	
	private com.belejanor.switcher.bimo.pacs.pacs_002_022.Document ResponseCredit
    					(com.belejanor.switcher.bimo.pacs.pacs_008_021.Document document, Iso8583 iso){

		com.belejanor.switcher.bimo.pacs.pacs_002_022.Document docresponse = null;
		
		try {

			String IdMessage = MemoryGlobal.IdBIMOEfi + FormatUtils.DateToString(new Date(), "yyyyMMddHHmmss");
			XMLGregorianCalendar date = FormatUtils.DateToXMLGregorian(new Date());
			
			docresponse = new Document();
			com.belejanor.switcher.bimo.pacs.pacs_002_022.HeaderData hd = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_022.HeaderData();
			
			/*Header Data*/
			com.belejanor.switcher.bimo.pacs.pacs_002_022.OrigIdData orgHd = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_022.OrigIdData();
			orgHd.setChannel(document.getHeader().getOrigId().getChannel());
			orgHd.setApp(document.getHeader().getOrigId().getApp());
			com.belejanor.switcher.bimo.pacs.pacs_002_022.ServiceData servOrgHd = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_022.ServiceData();
			servOrgHd.setIdServ("Credito RS");
			servOrgHd.setVersServ(document.getHeader().getOrigId().getService().getVersServ());
			orgHd.setService(servOrgHd);
			orgHd.setOtherId(document.getHeader().getOrigId().getOtherId());
			hd.setSender(document.getHeader().getReceiver()); //Al reves ahora el que recibe es el que envia y viceversa
			hd.setReceiver(document.getHeader().getSender()); 
			com.belejanor.switcher.bimo.pacs.pacs_002_022.MjeData  mjdata = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_022.MjeData();
			mjdata.setType("pacs.002.022");
			mjdata.setRoR(com.belejanor.switcher.bimo.pacs.pacs_002_022.RoRCod.RESP);
			mjdata.setIdMge(IdMessage);
			mjdata.setOpeDate(date);
			hd.setMge(mjdata);
			hd.setOrigId(orgHd);
			hd.setPssblDplct(document.getHeader().isPssblDplct());
			com.belejanor.switcher.bimo.pacs.pacs_002_022.Priority prty = null;
			switch (document.getHeader().getPrty()) {
			case URGT:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_022.Priority.URGT;
			break;
			case HIGH:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_022.Priority.HIGH;
			break;
			case NORM:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_022.Priority.NORM;
			break;
			case LOWW:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_022.Priority.LOWW;
			break;
				default:
				break;
		    }
			hd.setPrty(prty);
			com.belejanor.switcher.bimo.pacs.pacs_002_022.GroupHeader grHd = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_022.GroupHeader();
			grHd.setMsgId(IdMessage);
			grHd.setCreDtTm(date);
			grHd.setNbOfTxs(document.getFIToFICstmrCdtTrf().getGrpHdr().getNbOfTxs());
			com.belejanor.switcher.bimo.pacs.pacs_002_022.SettlementMethod1Code sttMeth = null;
			
			switch (document.getFIToFICstmrCdtTrf().getGrpHdr().getSttInf().getSttlmMtd()) {
			case INDA:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_022.SettlementMethod1Code.INDA;
			break;
			case INGA:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_022.SettlementMethod1Code.INGA;
			break;
			case CLRG:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_022.SettlementMethod1Code.CLRG;
			break;
			case COVE:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_022.SettlementMethod1Code.COVE;
			break;
			
			default:
			break;
			}
			
			/*FIToFICstmrCdtTrf*/
			FIToFIPaymentStatusReportV08 prtData = new FIToFIPaymentStatusReportV08();
			com.belejanor.switcher.bimo.pacs.pacs_002_022.SettlementInstruction4 sttIns4 = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_022.SettlementInstruction4();
			sttIns4.setSttlmMtd(sttMeth);
			grHd.setSttInf(sttIns4);
			
			com.belejanor.switcher.bimo.pacs.pacs_002_022.BranchAndFinancialInstitutionIdentification5 eff = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_022.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.pacs_002_022.FinancialInstitutionIdentification8 efi = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_022.FinancialInstitutionIdentification8();
			com.belejanor.switcher.bimo.pacs.pacs_002_022.GenericFinancialIdentification1 zgen = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_022.GenericFinancialIdentification1();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			efi.setOthr(zgen);
			eff.setFinInstnId(efi);
			
			
			com.belejanor.switcher.bimo.pacs.pacs_002_022.BranchAndFinancialInstitutionIdentification5 eff2 = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_022.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.pacs_002_022.FinancialInstitutionIdentification8 efi2 = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_022.FinancialInstitutionIdentification8();
			com.belejanor.switcher.bimo.pacs.pacs_002_022.GenericFinancialIdentification1 zgen2 = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_022.GenericFinancialIdentification1();
			zgen2.setId(document.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()
					                    .getFinInstnId().getOthr().getId()); //ojo EFI Acq
			efi2.setOthr(zgen2);
			eff2.setFinInstnId(efi2);
			
			
			grHd.setInstdAgt(eff);
			grHd.setInstgAgt(eff2);			
			prtData.setGrpHdr(grHd);
			
			//TxInfAndSts
			com.belejanor.switcher.bimo.pacs.pacs_002_022.PaymentTransaction80 txInfoSt = new PaymentTransaction80();
			OriginalGroupInformation3 original = new OriginalGroupInformation3();
			original.setOrgnlMsgId(document.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId());
			original.setOrgnlMsgNmId("pacs.008.021");
			original.setOrgnlCreDtTm(document.getFIToFICstmrCdtTrf().getGrpHdr().getCreDtTm());
			txInfoSt.setOrgnlGrpInf(original);
			
			txInfoSt.setOrgnlEndToEndId(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)
																	.getPmtId().getEndToEndId());
			
			txInfoSt.setOrgnlTxId(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)
																	.getPmtId().getTxId());
		
			if(iso.getISO_039_ResponseCode().equals("000"))
				txInfoSt.setTxSts(com.belejanor.switcher.bimo.pacs.pacs_002_022.StatusTypeCode.OK);
			else
				txInfoSt.setTxSts(com.belejanor.switcher.bimo.pacs.pacs_002_022.StatusTypeCode.ERR);
			
			com.belejanor.switcher.bimo.pacs.pacs_002_022.StatusReasonInformation9 statusInfo = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_022.StatusReasonInformation9();
			com.belejanor.switcher.bimo.pacs.pacs_002_022.StatusReason6Choice rzon = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_022.StatusReason6Choice();
			rzon.setPrtry(codeHomolog(iso));
			//
			statusInfo.setRsn(rzon);
			statusInfo.setAddtlInf(truncateResponse(iso.getISO_039p_ResponseDetail()
					.replace("<**>", StringUtils.Empty())
					.replace("<*>", StringUtils.Empty())));

			txInfoSt.setStsRsnInf(statusInfo);
			
			txInfoSt.setAccptncDtTm(date);
			
			String msgError = StringUtils.Empty();
			String confCode = StringUtils.Empty();
			
			if(iso.getISO_039_ResponseCode().equals("000")) {
				//msgError = iso.getISO_044_AddRespData().replaceAll("[^a-zA-Z0-9]", StringUtils.Empty());
				msgError = MemoryGlobal.IdBIMOEfi + "-" + MemoryGlobal.nameCoop;
				confCode = iso.getISO_019_AcqCountryCode();
			}
			else {
				msgError = MemoryGlobal.IdBIMOEfi  + GeneralUtils.GetSecuencial(6);
				confCode = GeneralUtils.GetSecuencial(6);
			}
			
			txInfoSt.setAcctSvcrRef(msgError);
			com.belejanor.switcher.bimo.pacs.pacs_002_022.SupplementaryData1 supp = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_022.SupplementaryData1();
			com.belejanor.switcher.bimo.pacs.pacs_002_022.SupplementaryDataEnvelope1 spenv = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_022.SupplementaryDataEnvelope1();
			com.belejanor.switcher.bimo.pacs.pacs_002_022.ContentsV03 contents = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_022.ContentsV03();
			contents.setConfCode(confCode);
			spenv.setCnts(contents);
			supp.setEnvlp(spenv);
			txInfoSt.setSplmtryData(supp);
			
			prtData.setTxInfAndSts(txInfoSt);
			docresponse.setFIToFIPmtStsRpt(prtData);
			docresponse.setHeader(hd);
			
		} catch (Exception e) {
		
			this.codError = "070";
			this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESO", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAcq::ResponseEnroll ", TypeMonitor.error, e);
		}
		return docresponse;
	}
	
	private com.belejanor.switcher.bimo.pacs.pacs_002_042.Document ResponseReverCredit
			(com.belejanor.switcher.bimo.pacs.pacs_007_041.Document document, Iso8583 iso){
		
        com.belejanor.switcher.bimo.pacs.pacs_002_042.Document docresponse = null;
		
		try {

			String IdMessage = MemoryGlobal.IdBIMOEfi + FormatUtils.DateToString(new Date(), "yyyyMMddHHmmss");
			XMLGregorianCalendar date = FormatUtils.DateToXMLGregorian(new Date());
			
			docresponse = new com.belejanor.switcher.bimo.pacs.pacs_002_042.Document();
			com.belejanor.switcher.bimo.pacs.pacs_002_042.HeaderData hd = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_042.HeaderData();
			
			/*Header Data*/
			com.belejanor.switcher.bimo.pacs.pacs_002_042.OrigIdData orgHd = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_042.OrigIdData();
			orgHd.setChannel(document.getHeader().getOrigId().getChannel());
			orgHd.setApp(document.getHeader().getOrigId().getApp());
			com.belejanor.switcher.bimo.pacs.pacs_002_042.ServiceData servOrgHd = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_042.ServiceData();
			servOrgHd.setIdServ("Reverso Credito RS");
			servOrgHd.setVersServ(document.getHeader().getOrigId().getService().getVersServ());
			orgHd.setService(servOrgHd);
			orgHd.setOtherId(document.getHeader().getOrigId().getOtherId());
			hd.setSender(document.getHeader().getReceiver()); //Al reves ahora el que recibe es el que envia y viceversa
			hd.setReceiver(document.getHeader().getSender()); 
			com.belejanor.switcher.bimo.pacs.pacs_002_042.MjeData  mjdata = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_042.MjeData();
			mjdata.setType("pacs.002.042");
			mjdata.setRoR(com.belejanor.switcher.bimo.pacs.pacs_002_042.RoRCod.RESP);
			mjdata.setIdMge(IdMessage);
			mjdata.setOpeDate(date);
			hd.setMge(mjdata);
			hd.setOrigId(orgHd);
			hd.setPssblDplct(document.getHeader().isPssblDplct());
			com.belejanor.switcher.bimo.pacs.pacs_002_042.Priority prty = null;
			switch (document.getHeader().getPrty()) {
			case URGT:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_042.Priority.URGT;
			break;
			case HIGH:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_042.Priority.HIGH;
			break;
			case NORM:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_042.Priority.NORM;
			break;
			case LOWW:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_042.Priority.LOWW;
			break;
				default:
				break;
		    }
			hd.setPrty(prty);
			com.belejanor.switcher.bimo.pacs.pacs_002_042.GroupHeader grHd = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_042.GroupHeader();
			grHd.setMsgId(IdMessage);
			grHd.setCreDtTm(date);
			grHd.setNbOfTxs(document.getFIToFIPmtRvsl().getGrpHdr().getNbOfTxs());
			com.belejanor.switcher.bimo.pacs.pacs_002_042.SettlementMethod1Code sttMeth = null;
			
			switch (document.getFIToFIPmtRvsl().getGrpHdr().getSttInf().getSttlmMtd()) {
			case INDA:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_042.SettlementMethod1Code.INDA;
			break;
			case INGA:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_042.SettlementMethod1Code.INGA;
			break;
			case CLRG:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_042.SettlementMethod1Code.CLRG;
			break;
			case COVE:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_042.SettlementMethod1Code.COVE;
			break;
			
			default:
			break;
			}
			
			/*FIToFICstmrCdtTrf*/
			com.belejanor.switcher.bimo.pacs.pacs_002_042.FIToFIPaymentStatusReportV08 prtData = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_042.FIToFIPaymentStatusReportV08();
			com.belejanor.switcher.bimo.pacs.pacs_002_042.SettlementInstruction4 sttIns4 = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_042.SettlementInstruction4();
			sttIns4.setSttlmMtd(sttMeth);
			grHd.setSttInf(sttIns4);
			
			com.belejanor.switcher.bimo.pacs.pacs_002_042.BranchAndFinancialInstitutionIdentification5 eff = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_042.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.pacs_002_042.FinancialInstitutionIdentification8 efi = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_042.FinancialInstitutionIdentification8();
			com.belejanor.switcher.bimo.pacs.pacs_002_042.GenericFinancialIdentification1 zgen = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_042.GenericFinancialIdentification1();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			efi.setOthr(zgen);
			eff.setFinInstnId(efi);
			
			
			com.belejanor.switcher.bimo.pacs.pacs_002_042.BranchAndFinancialInstitutionIdentification5 eff2 = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_042.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.pacs_002_042.FinancialInstitutionIdentification8 efi2 = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_042.FinancialInstitutionIdentification8();
			com.belejanor.switcher.bimo.pacs.pacs_002_042.GenericFinancialIdentification1 zgen2 = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_042.GenericFinancialIdentification1();
			zgen2.setId(MemoryGlobal.IdBIMOBanred); //ojo BCE Acq !!!!
			efi2.setOthr(zgen2);
			eff2.setFinInstnId(efi2);
			
			
			grHd.setInstdAgt(eff);
			grHd.setInstgAgt(eff2);			
			prtData.setGrpHdr(grHd);
			
			//TxInfAndSts
			com.belejanor.switcher.bimo.pacs.pacs_002_042.PaymentTransaction80 txInfoSt = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_042.PaymentTransaction80();
			com.belejanor.switcher.bimo.pacs.pacs_002_042.OriginalGroupInformation3 original = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_042.OriginalGroupInformation3();
			original.setOrgnlMsgId(document.getFIToFIPmtRvsl().getGrpHdr().getMsgId());
			original.setOrgnlMsgNmId("pacs.008.021");
			original.setOrgnlCreDtTm(document.getFIToFIPmtRvsl().getGrpHdr().getCreDtTm());
			txInfoSt.setOrgnlGrpInf(original);
			
			txInfoSt.setOrgnlEndToEndId(document.getFIToFIPmtRvsl().getTxInf()
																	.getOrgnlEndToEndId());
			
			txInfoSt.setOrgnlTxId(document.getFIToFIPmtRvsl().getTxInf()
																	.getOrgnlTxId());
		
			if(iso.getISO_039_ResponseCode().equals("000"))
				txInfoSt.setTxSts(com.belejanor.switcher.bimo.pacs.pacs_002_042.StatusTypeCode.OK);
			else
				txInfoSt.setTxSts(com.belejanor.switcher.bimo.pacs.pacs_002_042.StatusTypeCode.ERR);
			
			com.belejanor.switcher.bimo.pacs.pacs_002_042.StatusReasonInformation9 statusInfo = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_042.StatusReasonInformation9();
			com.belejanor.switcher.bimo.pacs.pacs_002_042.StatusReason6Choice rzon = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_042.StatusReason6Choice();
			rzon.setPrtry(codeHomolog(iso));
			statusInfo.setRsn(rzon);
			statusInfo.setAddtlInf(truncateResponse(iso.getISO_039p_ResponseDetail()
					.replace("<**>", StringUtils.Empty())
					.replace("<*>", StringUtils.Empty())));

			txInfoSt.setStsRsnInf(statusInfo);
			
			txInfoSt.setAccptncDtTm(date);
			if(iso.getISO_039_ResponseCode().equals("000")){
				
				//txInfoSt.setAcctSvcrRef(iso.getISO_044_AddRespData().replaceAll("[^a-zA-Z0-9]", StringUtils.Empty()));
				txInfoSt.setAccptncDtTm(date);
			}else {
				
				//txInfoSt.setAcctSvcrRef(GeneralUtils.GetSecuencialNumeric(8));
				txInfoSt.setAccptncDtTm(date);
			}
			txInfoSt.setAcctSvcrRef(MemoryGlobal.IdBIMOEfi + "-" + MemoryGlobal.nameCoop);
			
			prtData.setTxInfAndSts(txInfoSt);
			docresponse.setFIToFIPmtStsRpt(prtData);
			docresponse.setHeader(hd);
			
		} catch (Exception e) {
		
			this.codError = "070";
			this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESO", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAcq::ResponseEnroll ", TypeMonitor.error, e);
		}
		return docresponse;
	}
	
	private com.belejanor.switcher.bimo.pacs.pacs_002_072.Document ResponseDebit
				(com.belejanor.switcher.bimo.pacs.pacs_008_071.Document document, Iso8583 iso){
		
        com.belejanor.switcher.bimo.pacs.pacs_002_072.Document docresponse = null;
		
		try {

			String IdMessage = MemoryGlobal.IdBIMOEfi + FormatUtils.DateToString(new Date(), "yyyyMMddHHmmss");
			XMLGregorianCalendar date = FormatUtils.DateToXMLGregorian(new Date());
			
			docresponse = new com.belejanor.switcher.bimo.pacs.pacs_002_072.Document();
			com.belejanor.switcher.bimo.pacs.pacs_002_072.HeaderData hd = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_072.HeaderData();
			
			/*Header Data*/
			com.belejanor.switcher.bimo.pacs.pacs_002_072.OrigIdData orgHd = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_072.OrigIdData();
			orgHd.setChannel(document.getHeader().getOrigId().getChannel());
			orgHd.setApp(document.getHeader().getOrigId().getApp());
			com.belejanor.switcher.bimo.pacs.pacs_002_072.ServiceData servOrgHd = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_072.ServiceData();
			servOrgHd.setIdServ("Debito RS");
			servOrgHd.setVersServ(document.getHeader().getOrigId().getService().getVersServ());
			orgHd.setService(servOrgHd);
			orgHd.setOtherId(document.getHeader().getOrigId().getOtherId());
			hd.setSender(document.getHeader().getReceiver()); //Al reves ahora el que recibe es el que envia y viceversa
			hd.setReceiver(document.getHeader().getSender()); 
			com.belejanor.switcher.bimo.pacs.pacs_002_072.MjeData  mjdata = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_072.MjeData();
			mjdata.setType("pacs.002.072");
			mjdata.setRoR(com.belejanor.switcher.bimo.pacs.pacs_002_072.RoRCod.RESP);
			mjdata.setIdMge(IdMessage);
			mjdata.setOpeDate(date);
			hd.setMge(mjdata);
			hd.setOrigId(orgHd);
			hd.setPssblDplct(document.getHeader().isPssblDplct());
			com.belejanor.switcher.bimo.pacs.pacs_002_072.Priority prty = null;
			switch (document.getHeader().getPrty()) {
			case URGT:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_072.Priority.URGT;
			break;
			case HIGH:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_072.Priority.HIGH;
			break;
			case NORM:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_072.Priority.NORM;
			break;
			case LOWW:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_072.Priority.LOWW;
			break;
				default:
				break;
		    }
			hd.setPrty(prty);
			com.belejanor.switcher.bimo.pacs.pacs_002_072.GroupHeader grHd = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_072.GroupHeader();
			grHd.setMsgId(IdMessage);
			grHd.setCreDtTm(date);
			grHd.setNbOfTxs(document.getFIToFICstmrCdtTrf().getGrpHdr().getNbOfTxs());
			com.belejanor.switcher.bimo.pacs.pacs_002_072.SettlementMethod1Code sttMeth = null;
			
			switch (document.getFIToFICstmrCdtTrf().getGrpHdr().getSttInf().getSttlmMtd()) {
			case INDA:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_072.SettlementMethod1Code.INDA;
			break;
			case INGA:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_072.SettlementMethod1Code.INGA;
			break;
			case CLRG:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_072.SettlementMethod1Code.CLRG;
			break;
			case COVE:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_072.SettlementMethod1Code.COVE;
			break;
			
			default:
			break;
			}
			
			/*FIToFICstmrCdtTrf*/
			com.belejanor.switcher.bimo.pacs.pacs_002_072.FIToFIPaymentStatusReportV08 prtData = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_072.FIToFIPaymentStatusReportV08();
			com.belejanor.switcher.bimo.pacs.pacs_002_072.SettlementInstruction4 sttIns4 = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_072.SettlementInstruction4();
			sttIns4.setSttlmMtd(sttMeth);
			grHd.setSttInf(sttIns4);
			
			com.belejanor.switcher.bimo.pacs.pacs_002_072.BranchAndFinancialInstitutionIdentification5 eff = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_072.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.pacs_002_072.FinancialInstitutionIdentification8 efi = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_072.FinancialInstitutionIdentification8();
			com.belejanor.switcher.bimo.pacs.pacs_002_072.GenericFinancialIdentification1 zgen = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_072.GenericFinancialIdentification1();
			zgen.setId(MemoryGlobal.IdBIMOBanred);
			efi.setOthr(zgen);
			eff.setFinInstnId(efi);
			
			
			com.belejanor.switcher.bimo.pacs.pacs_002_072.BranchAndFinancialInstitutionIdentification5 eff2 = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_072.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.pacs_002_072.FinancialInstitutionIdentification8 efi2 = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_072.FinancialInstitutionIdentification8();
			com.belejanor.switcher.bimo.pacs.pacs_002_072.GenericFinancialIdentification1 zgen2 = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_072.GenericFinancialIdentification1();
			zgen2.setId(MemoryGlobal.IdBIMOEfi); //ojo el DebAcctor
			efi2.setOthr(zgen2);
			eff2.setFinInstnId(efi2);
			
			
			grHd.setInstdAgt(eff);
			grHd.setInstgAgt(eff2);			
			prtData.setGrpHdr(grHd);
			
			//TxInfAndSts
			com.belejanor.switcher.bimo.pacs.pacs_002_072.PaymentTransaction80 txInfoSt = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_072.PaymentTransaction80();
			com.belejanor.switcher.bimo.pacs.pacs_002_072.OriginalGroupInformation3 original = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_072.OriginalGroupInformation3();
			original.setOrgnlMsgId(document.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId());
			original.setOrgnlMsgNmId("pacs.008.071");
			original.setOrgnlCreDtTm(document.getFIToFICstmrCdtTrf().getGrpHdr().getCreDtTm());
			txInfoSt.setOrgnlGrpInf(original);
			
			txInfoSt.setOrgnlEndToEndId(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)
																	.getPmtId().getEndToEndId());
			
			txInfoSt.setOrgnlTxId(document.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)
																	.getPmtId().getTxId());
		
			if(iso.getISO_039_ResponseCode().equals("000"))
				txInfoSt.setTxSts(com.belejanor.switcher.bimo.pacs.pacs_002_072.StatusTypeCode.OK);
			else
				txInfoSt.setTxSts(com.belejanor.switcher.bimo.pacs.pacs_002_072.StatusTypeCode.ERR);
			
			com.belejanor.switcher.bimo.pacs.pacs_002_072.StatusReasonInformation9 statusInfo = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_072.StatusReasonInformation9();
			com.belejanor.switcher.bimo.pacs.pacs_002_072.StatusReason6Choice rzon = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_072.StatusReason6Choice();
			
			
			/*aqui JC*/
			//com.fitbank.middleware.bimo.pacs.pacs_002_072.prt
			
			rzon.setPrtry(codeHomolog(iso));
			log.WriteLogMonitor("ESCRIBIENDO ERROR: " + codeHomolog(iso), TypeMonitor.monitor, null);
			
			statusInfo.setRsn(rzon);
			statusInfo.setAddtlInf(truncateResponse(iso.getISO_039p_ResponseDetail()
					.replace("<**>", StringUtils.Empty())
					.replace("<*>", StringUtils.Empty())));

			txInfoSt.setStsRsnInf(statusInfo);
			
			txInfoSt.setAccptncDtTm(date);
			String RefIsError = StringUtils.Empty();
			String confCode = StringUtils.Empty();
			if(iso.getISO_039_ResponseCode().equals("000")) {
				//RefIsError = iso.getISO_044_AddRespData().replaceAll("[^a-zA-Z0-9]", StringUtils.Empty());
				RefIsError = MemoryGlobal.IdBIMOEfi + "-" + MemoryGlobal.nameCoop;
				confCode = iso.getISO_019_AcqCountryCode();
			}else {
				RefIsError = MemoryGlobal.IdBIMOEfi + GeneralUtils.GetSecuencial(6);
				confCode = GeneralUtils.GetSecuencial(6);
			}

			txInfoSt.setAcctSvcrRef(RefIsError);
			com.belejanor.switcher.bimo.pacs.pacs_002_072.SupplementaryData1 supp = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_072.SupplementaryData1();
			com.belejanor.switcher.bimo.pacs.pacs_002_072.SupplementaryDataEnvelope1 spenv = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_072.SupplementaryDataEnvelope1();
			com.belejanor.switcher.bimo.pacs.pacs_002_072.ContentsV03 contents = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_072.ContentsV03();
			contents.setConfCode(confCode);
			
			spenv.setCnts(contents);
			supp.setEnvlp(spenv);
			txInfoSt.setSplmtryData(supp);
			
			prtData.setTxInfAndSts(txInfoSt);
			docresponse.setFIToFIPmtStsRpt(prtData);
			docresponse.setHeader(hd);
			
		} catch (Exception e) {
		
			this.codError = "070";
			this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESO", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAcq::ResponseEnroll ", TypeMonitor.error, e);
		}
		return docresponse;
	}
	
	private com.belejanor.switcher.bimo.pacs.pacs_002_052.Document ResponseReverDebit
				(com.belejanor.switcher.bimo.pacs.pacs_007_051.Document document, Iso8583 iso){
		
		com.belejanor.switcher.bimo.pacs.pacs_002_052.Document docresponse = null;
		
		try {

			String IdMessage = MemoryGlobal.IdBIMOEfi + FormatUtils.DateToString(new Date(), "yyyyMMddHHmmss");
			XMLGregorianCalendar date = FormatUtils.DateToXMLGregorian(new Date());
			
			docresponse = new com.belejanor.switcher.bimo.pacs.pacs_002_052.Document();
			com.belejanor.switcher.bimo.pacs.pacs_002_052.HeaderData hd = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_052.HeaderData();
			
			/*Header Data*/
			com.belejanor.switcher.bimo.pacs.pacs_002_052.OrigIdData orgHd = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_052.OrigIdData();
			orgHd.setChannel(document.getHeader().getOrigId().getChannel());
			orgHd.setApp(document.getHeader().getOrigId().getApp());
			com.belejanor.switcher.bimo.pacs.pacs_002_052.ServiceData servOrgHd = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_052.ServiceData();
			servOrgHd.setIdServ("Reverso Debito RS");
			servOrgHd.setVersServ(document.getHeader().getOrigId().getService().getVersServ());
			orgHd.setService(servOrgHd);
			orgHd.setOtherId(document.getHeader().getOrigId().getOtherId());
			hd.setSender(document.getHeader().getReceiver()); //Al reves ahora el que recibe es el que envia y viceversa
			hd.setReceiver(document.getHeader().getSender()); 
			com.belejanor.switcher.bimo.pacs.pacs_002_052.MjeData  mjdata = new 
				com.belejanor.switcher.bimo.pacs.pacs_002_052.MjeData();
			mjdata.setType("pacs.002.052");
			mjdata.setRoR(com.belejanor.switcher.bimo.pacs.pacs_002_052.RoRCod.RESP);
			mjdata.setIdMge(IdMessage);
			mjdata.setOpeDate(date);
			hd.setMge(mjdata);
			hd.setOrigId(orgHd);
			hd.setPssblDplct(document.getHeader().isPssblDplct());
			com.belejanor.switcher.bimo.pacs.pacs_002_052.Priority prty = null;
			switch (document.getHeader().getPrty()) {
			case URGT:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_052.Priority.URGT;
			break;
			case HIGH:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_052.Priority.HIGH;
			break;
			case NORM:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_052.Priority.NORM;
			break;
			case LOWW:
				prty = com.belejanor.switcher.bimo.pacs.pacs_002_052.Priority.LOWW;
			break;
				default:
				break;
		    }
			hd.setPrty(prty);
			com.belejanor.switcher.bimo.pacs.pacs_002_052.GroupHeader grHd = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_052.GroupHeader();
			grHd.setMsgId(IdMessage);
			grHd.setCreDtTm(date);
			grHd.setNbOfTxs(document.getFIToFIPmtRvsl().getGrpHdr().getNbOfTxs());
			com.belejanor.switcher.bimo.pacs.pacs_002_052.SettlementMethod1Code sttMeth = null;
			
			switch (document.getFIToFIPmtRvsl().getGrpHdr().getSttInf().getSttlmMtd()) {
			case INDA:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_052.SettlementMethod1Code.INDA;
			break;
			case INGA:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_052.SettlementMethod1Code.INGA;
			break;
			case CLRG:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_052.SettlementMethod1Code.CLRG;
			break;
			case COVE:
				sttMeth = com.belejanor.switcher.bimo.pacs.pacs_002_052.SettlementMethod1Code.COVE;
			break;
			
			default:
			break;
			}
			
			/*FIToFICstmrCdtTrf*/
			com.belejanor.switcher.bimo.pacs.pacs_002_052.FIToFIPaymentStatusReportV08 prtData = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_052.FIToFIPaymentStatusReportV08();
			com.belejanor.switcher.bimo.pacs.pacs_002_052.SettlementInstruction4 sttIns4 = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_052.SettlementInstruction4();
			sttIns4.setSttlmMtd(sttMeth);
			grHd.setSttInf(sttIns4);
			
			com.belejanor.switcher.bimo.pacs.pacs_002_052.BranchAndFinancialInstitutionIdentification5 eff = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_052.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.pacs_002_052.FinancialInstitutionIdentification8 efi = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_052.FinancialInstitutionIdentification8();
			com.belejanor.switcher.bimo.pacs.pacs_002_052.GenericFinancialIdentification1 zgen = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_052.GenericFinancialIdentification1();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			efi.setOthr(zgen);
			eff.setFinInstnId(efi);
			
			
			com.belejanor.switcher.bimo.pacs.pacs_002_052.BranchAndFinancialInstitutionIdentification5 eff2 = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_052.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.pacs_002_052.FinancialInstitutionIdentification8 efi2 = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_052.FinancialInstitutionIdentification8();
			com.belejanor.switcher.bimo.pacs.pacs_002_052.GenericFinancialIdentification1 zgen2 = new 
					com.belejanor.switcher.bimo.pacs.pacs_002_052.GenericFinancialIdentification1();
			zgen2.setId(MemoryGlobal.IdBIMOBanred); //ojo BCE Acq !!!!
			efi2.setOthr(zgen2);
			eff2.setFinInstnId(efi2);
			
			
			grHd.setInstdAgt(eff);
			grHd.setInstgAgt(eff2);			
			prtData.setGrpHdr(grHd);
			
			//TxInfAndSts
			com.belejanor.switcher.bimo.pacs.pacs_002_052.PaymentTransaction80 txInfoSt = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_052.PaymentTransaction80();
			com.belejanor.switcher.bimo.pacs.pacs_002_052.OriginalGroupInformation3 original = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_052.OriginalGroupInformation3();
			original.setOrgnlMsgId(document.getFIToFIPmtRvsl().getGrpHdr().getMsgId());
			original.setOrgnlMsgNmId("pacs.007.051");
			original.setOrgnlCreDtTm(document.getFIToFIPmtRvsl().getGrpHdr().getCreDtTm());
			txInfoSt.setOrgnlGrpInf(original);
			
			txInfoSt.setOrgnlEndToEndId(document.getFIToFIPmtRvsl().getTxInf()
																	.getOrgnlEndToEndId());
			
			txInfoSt.setOrgnlTxId(document.getFIToFIPmtRvsl().getTxInf()
																	.getOrgnlTxId());
		
			if(iso.getISO_039_ResponseCode().equals("000"))
				txInfoSt.setTxSts(com.belejanor.switcher.bimo.pacs.pacs_002_052.StatusTypeCode.OK);
			else
				txInfoSt.setTxSts(com.belejanor.switcher.bimo.pacs.pacs_002_052.StatusTypeCode.ERR);

			com.belejanor.switcher.bimo.pacs.pacs_002_052.StatusReasonInformation9 statusInfo = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_052.StatusReasonInformation9();
			com.belejanor.switcher.bimo.pacs.pacs_002_052.StatusReason6Choice rzon = 
					new com.belejanor.switcher.bimo.pacs.pacs_002_052.StatusReason6Choice();
			rzon.setPrtry(codeHomolog(iso));
			statusInfo.setRsn(rzon);
			statusInfo.setAddtlInf(truncateResponse(iso.getISO_039p_ResponseDetail()
					.replace("<**>", StringUtils.Empty())
					.replace("<*>", StringUtils.Empty())));

			txInfoSt.setStsRsnInf(statusInfo);
			
			txInfoSt.setAccptncDtTm(date);
			if(iso.getISO_039_ResponseCode().equals("000")){
				
				//txInfoSt.setAcctSvcrRef(iso.getISO_044_AddRespData().replaceAll("[^a-zA-Z0-9]", StringUtils.Empty()));
				txInfoSt.setAccptncDtTm(date);
			}else {
				
				//txInfoSt.setAcctSvcrRef(GeneralUtils.GetSecuencialNumeric(8));
				txInfoSt.setAccptncDtTm(date);
			}
			txInfoSt.setAcctSvcrRef(MemoryGlobal.IdBIMOEfi + "-" + MemoryGlobal.nameCoop);
			
			prtData.setTxInfAndSts(txInfoSt);
			docresponse.setFIToFIPmtStsRpt(prtData);
			docresponse.setHeader(hd);
			
			
		} catch (Exception e) {
		
			this.codError = "070";
			this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESO", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAcq::ResponseReverDebit ", TypeMonitor.error, e);
		}
		return docresponse;

	}
	
	private com.belejanor.switcher.bimo.pacs.camt_998_232.Document ResponseGeneraOTP
	        (com.belejanor.switcher.bimo.pacs.camt_998_231.Document document, Iso8583 iso){
		
		com.belejanor.switcher.bimo.pacs.camt_998_232.Document docrespose = null;
	    try {
			
	    	String IdMessage = MemoryGlobal.IdBIMOEfi + FormatUtils.DateToString(new Date(), "yyyyMMddHHmmss");
	    	XMLGregorianCalendar date = FormatUtils.DateToXMLGregorian(new Date());
	    	
	    	docrespose = new com.belejanor.switcher.bimo.pacs.camt_998_232.Document();
	    	/*Header*/
	    	com.belejanor.switcher.bimo.pacs.camt_998_232.HeaderData hd = new HeaderData();
	    	com.belejanor.switcher.bimo.pacs.camt_998_232.OrigIdData org = new 
	    			com.belejanor.switcher.bimo.pacs.camt_998_232.OrigIdData();
	    	org.setChannel(document.getHeader().getOrigId().getChannel());
	    	org.setApp(document.getHeader().getOrigId().getApp());
	    	org.setOtherId(document.getHeader().getOrigId().getOtherId());
	    	
	    	com.belejanor.switcher.bimo.pacs.camt_998_232.ServiceData service = new 
	    			com.belejanor.switcher.bimo.pacs.camt_998_232.ServiceData();
	    	service.setIdServ("Retiro Sin Tarjeta");
	    	service.setVersServ("1.0");
	    	
	    	org.setService(service);
	    	hd.setOrigId(org);
	    	
	    	hd.setSender(document.getHeader().getReceiver());
	    	hd.setReceiver(document.getHeader().getSender());
	    	
	    	com.belejanor.switcher.bimo.pacs.camt_998_232.MjeData mge = 
	    			 new com.belejanor.switcher.bimo.pacs.camt_998_232.MjeData();
	    	mge.setIdMge(IdMessage);
	    	mge.setType("camt.998.232");
	    	mge.setRoR(com.belejanor.switcher.bimo.pacs.camt_998_232.RoRCod.RESP);
	    	mge.setOpeDate(date);
	    	
	    	hd.setPssblDplct(document.getHeader().isPssblDplct());
	    	com.belejanor.switcher.bimo.pacs.camt_998_232.Priority prty = null;
			switch (document.getHeader().getPrty()) {
			case URGT:
				prty = com.belejanor.switcher.bimo.pacs.camt_998_232.Priority.URGT;
			break;
			case HIGH:
				prty = com.belejanor.switcher.bimo.pacs.camt_998_232.Priority.HIGH;
			break;
			case NORM:
				prty = com.belejanor.switcher.bimo.pacs.camt_998_232.Priority.NORM;
			break;
			case LOWW:
				prty = com.belejanor.switcher.bimo.pacs.camt_998_232.Priority.LOWW;
			break;
				default:
				break;
		    }
			hd.setPrty(prty);
			hd.setMge(mge);
			
			com.belejanor.switcher.bimo.pacs.camt_998_232.ProprietaryMessageV02 pmsg = 
					new com.belejanor.switcher.bimo.pacs.camt_998_232.ProprietaryMessageV02();
			
			com.belejanor.switcher.bimo.pacs.camt_998_232.GroupHeader grpHeader = 
					new com.belejanor.switcher.bimo.pacs.camt_998_232.GroupHeader();
			grpHeader.setMsgId(IdMessage);
			grpHeader.setCreDtTm(date);
			grpHeader.setNbOfTxs(document.getPrtryMsg().getGrpHdr().getNbOfTxs());
			
			
			com.belejanor.switcher.bimo.pacs.camt_998_232.SettlementMethod1Code sttMeth = null;
	
			switch (document.getPrtryMsg().getGrpHdr().getSttInf().getSttlmMtd()) {
			case INDA:
				sttMeth = com.belejanor.switcher.bimo.pacs.camt_998_232.SettlementMethod1Code.INDA;
				break;
			case INGA:
				sttMeth = com.belejanor.switcher.bimo.pacs.camt_998_232.SettlementMethod1Code.INGA;
				break;
			case CLRG:
				sttMeth = com.belejanor.switcher.bimo.pacs.camt_998_232.SettlementMethod1Code.CLRG;
				break;
			case COVE:
				sttMeth = com.belejanor.switcher.bimo.pacs.camt_998_232.SettlementMethod1Code.COVE;
				break;
			default:
				break;
			}
			
			com.belejanor.switcher.bimo.pacs.camt_998_232.SettlementInstruction4 settIns4 = new 
					com.belejanor.switcher.bimo.pacs.camt_998_232.SettlementInstruction4();
			settIns4.setSttlmMtd(sttMeth);
			grpHeader.setSttInf(settIns4);
			
			com.belejanor.switcher.bimo.pacs.camt_998_232.BranchAndFinancialInstitutionIdentification5 eff = 
					new com.belejanor.switcher.bimo.pacs.camt_998_232.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.camt_998_232.ZFinancialInstitutionIdentification efi = new 
					com.belejanor.switcher.bimo.pacs.camt_998_232.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_232.ZGenericFinancialIdentification zgen = new 
					com.belejanor.switcher.bimo.pacs.camt_998_232.ZGenericFinancialIdentification();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			efi.setOthr(zgen);
			eff.setFinInstnId(efi);
			
			
			com.belejanor.switcher.bimo.pacs.camt_998_232.BranchAndFinancialInstitutionIdentification5 eff2 = 
					new com.belejanor.switcher.bimo.pacs.camt_998_232.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.camt_998_232.ZFinancialInstitutionIdentification efi2 = new 
					com.belejanor.switcher.bimo.pacs.camt_998_232.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_232.ZGenericFinancialIdentification zgen2 = new 
					com.belejanor.switcher.bimo.pacs.camt_998_232.ZGenericFinancialIdentification();
			zgen2.setId(MemoryGlobal.IdBIMOBanred);
			efi2.setOthr(zgen2);
			eff2.setFinInstnId(efi2);
			grpHeader.setInstdAgt(eff);
			grpHeader.setInstgAgt(eff2);			
			pmsg.setGrpHdr(grpHeader);
			
			com.belejanor.switcher.bimo.pacs.camt_998_232.WithdrawNC withNc = new WithdrawNC();
			com.belejanor.switcher.bimo.pacs.camt_998_232.OriginalGroupInformation orgGrp = 
					new com.belejanor.switcher.bimo.pacs.camt_998_232.OriginalGroupInformation();
			
			orgGrp.setOrgnlMsgId(document.getPrtryMsg().getGrpHdr().getMsgId());
			orgGrp.setOrgnlMsgNmId("camt.998.231");
			orgGrp.setOrgnlCreDtTm(document.getPrtryMsg().getGrpHdr().getCreDtTm());
			
			withNc.setOrgnlGrpInf(orgGrp);
			com.belejanor.switcher.bimo.pacs.camt_998_232.StatusReasonInformation statRif = 
					new com.belejanor.switcher.bimo.pacs.camt_998_232.StatusReasonInformation();
		
			
			com.belejanor.switcher.bimo.pacs.camt_998_232.Reason rsn = 
					new com.belejanor.switcher.bimo.pacs.camt_998_232.Reason();
			
			if(!iso.getISO_039_ResponseCode().equals("000")) 	
				withNc.setTxSts(com.belejanor.switcher.bimo.pacs.camt_998_232.StatusCode.ERR);
				
			rsn.setCode(iso.getISO_039_ResponseCode());
			String descripcion = iso.getISO_039p_ResponseDetail();
			List<String> descrp = new ArrayList<>();
			descrp.add(descripcion);
			rsn.setAddtlInf(descrp);
			statRif.setRsn(rsn);
			withNc.setStsRsnInf(statRif);
			
			pmsg.setWithdrawNC(withNc);
			
			docrespose.setHeader(hd);
			docrespose.setPrtryMsg(pmsg);
	    	
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = truncateResponse(GeneralUtils.ExceptionToString("ERROR EN PROCESO", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAcq::ResponseGeneraOTP ", TypeMonitor.error, e);
		}
	    return docrespose;
	}
	
	private String codeHomolog(Iso8583 iso){
		String data = null;
		try {
			switch (iso.getISO_039_ResponseCode()) {
			case "000":
				/*if(iso.getISO_000_Message_Type().startsWith("14")) ACERQ
					data = "010";
				else*/
					data = "000";
				break;
				
			case "500":
					data = "518";
				break;
			case "003": //Creacion Cliente Listas de Control
				data = "063";
			break;
			case "004": //Creacion Cliente Error interno
				data = "998";
			break;
			case "001"://Cobis
			case "601": /*Transaccion original no existe*/
				if(!iso.getISO_000_Message_Type().startsWith("12"))
					data = "200";
				else 
					data = "058";
				break;
			case "308":
			case "905":
				data = "100";
				break;
			case "002": //Cobis	
			case "606": /*Transaccion ya reversada*/
				data = "010";
				break;
			case "078":
				data = "054";
				break;
			case "116":
				data = "058";
				break;
			case "904":
				data = "602";
				break;
			case "120":
				data = "060";
				break;
			case "217":
			case "214":
				data = "056";
				break;
			case "215":
				data = "510";
				break;
			case "050":
				data = "050";
				break;
			case "123":
				data = "061";
				break;
			case "907":
				if(iso.getISO_003_ProcessingCode().startsWith("0"))
					data = "161"; /*TimeOut al Debito*/
				else
					data = "162"; /*TimeOut al Credito*/
				break;
			case "701":
					data = "056";
					break;
			case "702":
				   data = "065";
				   break;
			case "999":
			case "909":
			case "908":
				if(iso.getISO_039p_ResponseDetail().equalsIgnoreCase("no Cumple con el Monto Minimo"))
					data = "058";	
				else	
				    data = "070";
				break;
			default:
				data = iso.getISO_039_ResponseCode();
				break;
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BanredIsAcq::codeHomolog ", TypeMonitor.error, e);
		}
		return data;
	}
	
	private void changesEnroll(Ref<Iso8583> isoRef){
		
		double versionFit = 0;
		Iso8583 iso = isoRef.get();
		try {
			Config cnf = new Config();
			cnf = cnf.getConfigSystem("FIT_VERSION");
			if(cnf != null){
				
				versionFit = Double.parseDouble(cnf.getCfg_Valor());
				//Nacionalidad
				//iso.setISO_019_AcqCountryCode(iso.getISO_019_AcqCountryCode().substring(0,2));
				iso.setISO_019_AcqCountryCode(iso.getISO_019_AcqCountryCode().equals("ECUATORIANA")?"239":"239");
				//Tipo Documento persona
				iso.setISO_022_PosEntryMode(iso.getISO_022_PosEntryMode().equalsIgnoreCase("CC") ? "CC":"CC");
				//Tipo Persona
				iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData().equalsIgnoreCase("PN") ? "NAT":"JUR");
				//Genero
				if(Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(1).equalsIgnoreCase("HOMBRE"))
					iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|HOMBRE|","|MASCULINO|"));
				else if(Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(1).equalsIgnoreCase("MUJER"))
					iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|MUJER|", "|FEMENINO|"));
				
				//Estado Civil
				if(versionFit >= 2){
					
					switch (Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(3)) {
					case "SOLTERA":
					case "SOLTERO":
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|SOLTERO|", "|1|")
								.replace("|SOLTERA|", "|1|"));
						break;
					case "CASADA":
					case "CASADO":
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|CASADO|", "|2|")
								  .replace("|CASADA|", "|2|"));
						break;
					case "DIVORCIADA":
					case "DIVORCIADO":
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|DIVORCIADO|", "|3|")
								 .replace("|DIVORCIADA|", "|3|"));
						break;
					case "VIUDA":
					case "VIUDO":
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|VIUDO|", "|4|")
							     .replace("|VIUDA|", "|4|"));
						break;
					case "EN UNION DE HECHO":
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|EN UNION DE HECHO|", "|5|"));
						break;
					case "NO REGISTRA":
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|NO REGISTRA|", "|6|"));
						break;
					default:
						break;
					}
					
				}else{
					
					switch (Arrays.asList(iso.getISO_123_ExtendedData().split("\\|")).get(3)) {
					case "SOLTERA":
					case "SOLTERO":
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|SOLTERO|", "|1|")
								.replace("|SOLTERA|", "|1|"));
						break;
					case "CASADA":
					case "CASADO":
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|CASADO|", "|2|")
								.replace("|CASADA|", "|2|"));
						break;
					case "DIVORCIADA":
					case "DIVORCIADO":
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|DIVORCIADO|", "|3|")
								.replace("|DIVORCIADA|", "|3|"));
						break;
					case "VIUDA":
					case "VIUDO":
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|VIUDO|", "|4|")
								.replace("|VIUDA|", "|4|"));
						break;
					case "EN UNION DE HECHO":
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|EN UNION DE HECHO|", "|5|"));
						break;
					case "NO REGISTRA":
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().replace("|NO REGISTRA|", "|1|"));
						break;
					default:
						break;
					}
				}
			}else{
				
				iso.setISO_039_ResponseCode("100");
				iso.setISO_039p_ResponseDetail("NO SE PUDO RECUPERAR CONFIGURACION PARA HOMOLOGACIONES INTERNAS");
			}
			
			String [] desgloseNombres = iso.getISO_034_PANExt().split(" ");
			String acum = StringUtils.Empty();
			for (int i = 0; i < 4; i++) {
				
				try {
					acum += desgloseNombres[i] + "|";
				} catch (Exception e) {
					acum += " |";	
				}	
			}
			acum = StringUtils.trimEnd(acum, "|");
			iso.setISO_055_EMV(acum);
			
			iso.setISO_090_OriginalData(Arrays.asList(iso.getISO_055_EMV().split("\\|")).get(0) + " " 
			+ Arrays.asList(iso.getISO_055_EMV().split("\\|")).get(1) + "|" + 
			Arrays.asList(iso.getISO_055_EMV().split("\\|")).get(2) + " " + 
			Arrays.asList(iso.getISO_055_EMV().split("\\|")).get(3));
					
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, PROBLEMAS AL HOMOLOGAR");
			log.WriteLogMonitor("Error modulo BanredIsAcq::changesEnroll ", TypeMonitor.error, e);
		}
	}
	
	private String truncateResponse(String desError){
		
		try {
			
			if(desError.length() <= 105){
				
				return desError;
			}else{
				
				return desError.substring(0, 105);
			}
			
		} catch (Exception e) {
			
			return desError;
		}
	}
}
