package com.belejanor.switcher.authorizations;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.TransactionConfiguration;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;
import com.belejanor.switcher.memcached.Config;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.memcached.TransactionConfig;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;
import com.fitbank.middleware.bceisuatorizator.ConsumeBCEService;
import com.fitbank.middleware.bceisuatorizator.ProcessorTransactions_II;
import com.fitbank.middleware.bceisuatorizator.Response;

import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTORequestAccountAssociation;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTORequestAccountAssociationConfirm;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTORequestAccountDissociation;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTOResponseAccountAssociation;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTOResponseAccountAssociationConfirm;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTOResponseAccountDissociation;

public class BCEElectronicCashIsAut {
	
	private Logger log;
	
	public BCEElectronicCashIsAut() {
		
		this.log = new Logger();
	}

	public wIso8583 ExecuteAccountAsociation(wIso8583 iso){
		
		try {
			
			putCertificate();
			ConsumeBCEService con = new ConsumeBCEService(MemoryGlobal.urlBce,5000,iso.getWsTransactionConfig().getProccodeTimeOutValue(),
					                    MemoryGlobal.userBce,MemoryGlobal.pwdBce);
			
			DTORequestAccountAssociation rq = new DTORequestAccountAssociation();
			
			rq.setUser(MemoryGlobal.userBce);
			rq.setPassword(MemoryGlobal.pwdBce);
			String secuencial = GeneralUtils.GetSecuencial(20).toUpperCase();
			iso.setISO_037_RetrievalReferenceNumber(secuencial);
			rq.setUtfi(secuencial);
			rq.setLanguage(MemoryGlobal.idiomBce);
			rq.setName(iso.getISO_034_PANExt());
			rq.setDocument(iso.getISO_002_PAN());
			rq.setMsisdn(iso.getISO_114_ExtendedData());
			rq.setOperatorId(iso.getISO_115_ExtendedData());
			rq.setBank(iso.getISO_120_ExtendedData());
			rq.setBankCode(iso.getISO_121_ExtendedData());
			rq.setAccountNumber(iso.getISO_102_AccountID_1());
			rq.setAccountType("CA");
			rq.setEmail(iso.getISO_122_ExtendedData());
			log.WriteLog(rq, TypeLog.bceaut, TypeWriteLog.file);
			iso.getTickAut().start();
			DTOResponseAccountAssociation res = null;
			try {
				
				res = con.AccountAsociation(rq);
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
				
			
			iso.getTickAut().stop();
			
			if(con.isFlagError()){
				
				if(res != null){
					log.WriteLog(res, TypeLog.bceaut, TypeWriteLog.file);
					if(res.getResultCode() == 1){
						
						iso.setISO_123_ExtendedData(res.getUtfi());
						
						if(iso.getISO_037_RetrievalReferenceNumber().equalsIgnoreCase(res.getUtfi())){
							
							iso.setISO_039_ResponseCode("000");
							iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
							
						}else {
							
							iso.setISO_039_ResponseCode("804");
							iso.setISO_039p_ResponseDetail("TRANSACCION # SECUENCIAL ORIGINAL NO COINCIDE CON EL DE RESPUESTA BCE");
						}
					}
					else{
						iso.setISO_039_ResponseCode(StringUtils.padLeft
								   (String.valueOf(res.getResultCode()),3, " "));
						iso.setISO_039p_ResponseDetail(res.getResultText().toUpperCase());
						
					}
					iso.setISO_104_TranDescription(res.getResultCode() + "_" + res.getCodeErrorId() 
					+ "_" + res.getResultText().toUpperCase());
					iso.getISO_037_RetrievalReferenceNumber();
					iso.setWsIso_LogStatus(2);
				}
				else{
					
					log.WriteLog("Error: [Sec:]" + iso.getISO_037_RetrievalReferenceNumber() + " " + con.getDesError(), TypeLog.bceaut, TypeWriteLog.file);
					iso.setISO_039_ResponseCode("908");
				    iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS SE HA RECIBIDO UNA RESPUESTA NULLA POR EL BCE " + con.getDesError());
				}
			}
			else{
				
				log.WriteLog("Error: [Sec:]" + iso.getISO_037_RetrievalReferenceNumber() + " " + con.getDesError(), TypeLog.bceaut, TypeWriteLog.file);
				iso.setISO_039_ResponseCode("908");
				iso.setISO_039p_ResponseDetail(con.getDesError().toUpperCase());
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			log.WriteLogMonitor("Error modulo BCEElectronicCashIsAut::ExecuteAccountAsociation ", TypeMonitor.error, e);
			log.WriteLog("Error: [Sec:]" + iso.getISO_037_RetrievalReferenceNumber() + GeneralUtils.ExceptionToString(null, e, true), TypeLog.bceaut, TypeWriteLog.file);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR AL EJECUTAR METODO ASOCIACION DE CUENTA BCE ", e, true));
		}
		finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
			iso.setISO_120_ExtendedData(String.valueOf(iso.getWsIso_LogStatus()));
		}
		return iso;
	}
	
	public wIso8583 ExecuteAccountAsociationConfirm(wIso8583 iso){
		
		try {
			
			putCertificate();
			
			ConsumeBCEService con = new ConsumeBCEService(MemoryGlobal.urlBce,5000,iso.getWsTransactionConfig().getProccodeTimeOutValue(),
                    MemoryGlobal.userBce,MemoryGlobal.pwdBce);
			
			DTORequestAccountAssociationConfirm rq = new DTORequestAccountAssociationConfirm();
			
			rq.setUser(MemoryGlobal.userBce);
			rq.setPassword(MemoryGlobal.pwdBce);
			String secuencial = GeneralUtils.GetSecuencial(20).toUpperCase();
			iso.setISO_037_RetrievalReferenceNumber(secuencial);
			rq.setUtfi(secuencial);
			rq.setLanguage(MemoryGlobal.idiomBce);
			rq.setName(iso.getISO_034_PANExt());
			rq.setDocument(iso.getISO_002_PAN());
			rq.setMsisdn(iso.getISO_114_ExtendedData());
			rq.setOperatorId(iso.getISO_115_ExtendedData());
			rq.setBank(iso.getISO_120_ExtendedData());
			rq.setBankCode(iso.getISO_121_ExtendedData());
			rq.setAccountNumber(iso.getISO_102_AccountID_1());
			rq.setAccountType("CA");
			rq.setEmail(iso.getISO_122_ExtendedData());
			rq.setOtp(iso.getISO_052_PinBlock());
			log.WriteLog(rq, TypeLog.bceaut, TypeWriteLog.file);
			iso.getTickAut().start();
			DTOResponseAccountAssociationConfirm res = con.AccountAsociationConfirm(rq);
			iso.getTickAut().stop();
			
			if(con.isFlagError()){
				
				if(res != null){
					log.WriteLog(res, TypeLog.bceaut, TypeWriteLog.file);
					if(res.getResultCode() == 1){
						
						iso.setISO_123_ExtendedData(res.getUtfi());
						
						if(iso.getISO_037_RetrievalReferenceNumber().equalsIgnoreCase(res.getUtfi())){
							
							iso.setISO_039_ResponseCode("000");
							iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
							
						}else {
							
							iso.setISO_039_ResponseCode("804");
							iso.setISO_039p_ResponseDetail("TRANSACCION # SECUENCIAL ORIGINAL NO COINCIDE CON EL DE RESPUESTA BCE");
						}
					}
					else{
						iso.setISO_039_ResponseCode(StringUtils.padLeft
								   (String.valueOf(res.getResultCode()),3, " "));
						iso.setISO_039p_ResponseDetail(res.getResultText().toUpperCase());
						
					}
					iso.setISO_104_TranDescription(res.getResultCode() + "_" + res.getCodeErrorId() 
					+ "_" + res.getResultText().toUpperCase());
					iso.getISO_037_RetrievalReferenceNumber();
					iso.setWsIso_LogStatus(2);
					
				}
				else{
					
					log.WriteLog("Error: [Sec:]" + iso.getISO_037_RetrievalReferenceNumber() + " " + con.getDesError(), TypeLog.bceaut, TypeWriteLog.file);
					iso.setISO_039_ResponseCode("908");
				    iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS SE HA RECIBIDO UNA RESPUESTA NULLA POR EL BCE " + con.getDesError());
				}
			}
			else{
				
				log.WriteLog("Error: [Sec:]" + iso.getISO_037_RetrievalReferenceNumber() + " " + con.getDesError(), TypeLog.bceaut, TypeWriteLog.file);
				iso.setISO_039_ResponseCode("908");
				iso.setISO_039p_ResponseDetail(con.getDesError().toUpperCase());
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			log.WriteLogMonitor("Error modulo BCEElectronicCashIsAut::ExecuteAccountAsociationConfirm ", TypeMonitor.error, e);
			log.WriteLog("Error: [Sec:]" + iso.getISO_037_RetrievalReferenceNumber() + GeneralUtils.ExceptionToString(null, e, true), TypeLog.bceaut, TypeWriteLog.file);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR AL EJECUTAR METODO CONFIRMACION ASOCIACION DE CUENTA BCE ", e, true));
		}
		finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
			iso.setISO_120_ExtendedData(String.valueOf(iso.getWsIso_LogStatus()));
		}
		return iso;
	}
	
	public wIso8583 ExecuteAccountDissociation(wIso8583 iso){
		
		try {
			
			putCertificate();
			
			ConsumeBCEService con = new ConsumeBCEService(MemoryGlobal.urlBce,5000,iso.getWsTransactionConfig().getProccodeTimeOutValue(),
                    MemoryGlobal.userBce,MemoryGlobal.pwdBce);
			
			DTORequestAccountDissociation rq = new DTORequestAccountDissociation();
			
			rq.setUser(MemoryGlobal.userBce);
			rq.setPassword(MemoryGlobal.pwdBce);
			String secuencial = GeneralUtils.GetSecuencial(20).toUpperCase();
			iso.setISO_037_RetrievalReferenceNumber(secuencial);
			rq.setUtfi(secuencial);
			rq.setLanguage(MemoryGlobal.idiomBce);
			rq.setName(iso.getISO_034_PANExt());
			rq.setDocument(iso.getISO_002_PAN());
			rq.setMsisdn(iso.getISO_114_ExtendedData());
			rq.setOperatorId(iso.getISO_115_ExtendedData());
			rq.setBank(iso.getISO_120_ExtendedData());
			rq.setBankCode(iso.getISO_121_ExtendedData());
			rq.setAccountNumber(iso.getISO_102_AccountID_1());
			rq.setAccountType("CA");
			rq.setEmail(iso.getISO_122_ExtendedData());
			log.WriteLog(rq, TypeLog.bceaut, TypeWriteLog.file);
			iso.getTickAut().start();
			
			DTOResponseAccountDissociation res = con.AccountDissociation(rq);
			
			iso.getTickAut().stop();
			
			if(con.isFlagError()){
				
				if(res != null){
					log.WriteLog(res, TypeLog.bceaut, TypeWriteLog.file);
					if(res.getResultCode() == 1){
						
						iso.setISO_123_ExtendedData(res.getUtfi());
						
						if(iso.getISO_037_RetrievalReferenceNumber().equalsIgnoreCase(res.getUtfi())){
							
							iso.setISO_039_ResponseCode("000");
							iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
							
						}else {
							
							iso.setISO_039_ResponseCode("804");
							iso.setISO_039p_ResponseDetail("TRANSACCION # SECUENCIAL ORIGINAL NO COINCIDE CON EL DE RESPUESTA BCE");
						}
					}
					else{
						iso.setISO_039_ResponseCode(StringUtils.padLeft
								   (String.valueOf(res.getResultCode()),3, " "));
						iso.setISO_039p_ResponseDetail(res.getResultText().toUpperCase());
						
					}
					iso.setISO_104_TranDescription(res.getResultCode() + "_" + res.getCodeErrorId() 
					+ "_" + res.getResultText().toUpperCase());
					iso.getISO_037_RetrievalReferenceNumber();
					iso.setWsIso_LogStatus(2);
					
				}
				else{
					
					log.WriteLog("Error: [Sec:]" + iso.getISO_037_RetrievalReferenceNumber() + " " + con.getDesError(), TypeLog.bceaut, TypeWriteLog.file);
					iso.setISO_039_ResponseCode("908");
				    iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS SE HA RECIBIDO UNA RESPUESTA NULLA POR EL BCE " + con.getDesError());
				}
			}
			else{
				
				log.WriteLog("Error: [Sec:]" + iso.getISO_037_RetrievalReferenceNumber() + " " + con.getDesError(), TypeLog.bceaut, TypeWriteLog.file);
				iso.setISO_039_ResponseCode("908");
				iso.setISO_039p_ResponseDetail(con.getDesError().toUpperCase());
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			log.WriteLogMonitor("Error modulo BCEElectronicCashIsAut::ExecuteAccountAsociationConfirm ", TypeMonitor.error, e);
			log.WriteLog("Error: [Sec:]" + iso.getISO_037_RetrievalReferenceNumber() + GeneralUtils.ExceptionToString(null, e, true), TypeLog.bceaut, TypeWriteLog.file);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR AL EJECUTAR METODO CONFIRMACION ASOCIACION DE CUENTA BCE ", e, true));
		}
		finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
			iso.setISO_120_ExtendedData(String.valueOf(iso.getWsIso_LogStatus()));
		}
		return iso;
	}
	
	public wIso8583 ProcessTransactionsBCE_II(wIso8583 iso){
		
		try {
					putCertificate_II();
					
			 		ProcessorTransactions_II processor = new ProcessorTransactions_II(MemoryGlobal.urlBceAdmin, 
     				MemoryGlobal.ipBCEAdmin, MemoryGlobal.portBCEAdmin, iso.getISO_102_AccountID_1(), iso.getISO_052_PinBlock(), 
     				Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("-")).get(2),
     				Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("-")).get(3), 
     				(iso.getWsTransactionConfig().getProccodeTimeOutValue() - 5000), 
     				 iso.getWsTransactionConfig().getProccodeTimeOutValue());
			 		if(!StringUtils.IsNullOrEmpty(iso.getISO_120_ExtendedData())){
			 			processor.setOTP(iso.getISO_120_ExtendedData());
			 		}
			 		
			 		Date fechaReport = null;
			 		if(iso.getISO_124_ExtendedData().equalsIgnoreCase("S"))
			 			fechaReport = iso.getISO_013_LocalDate();
			 		
			 		String source = null;
			 		String target = null;
			 		if(!iso.getWsTransactionConfig().getProccodeParams().equalsIgnoreCase("IN")){
			 			
			 			source = iso.getISO_034_PANExt();
			 			target = iso.getISO_102_AccountID_1();
			 			
			 		}else {
						
			 			source = iso.getISO_102_AccountID_1();
			 			target = iso.getISO_034_PANExt();
					}
			 	
			 		iso.getTickAut().reset();
					iso.getTickAut().start();
			 		Response response = processor.ProcessHandlerTransaction(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("-")).get(0), 
			 				Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("-")).get(1), 
			 				iso.getISO_002_PAN(), source, target, 
			 				iso.getISO_004_AmountTransaction(), 
			 				iso.getISO_011_SysAuditNumber(), iso.getISO_052_PinBlock(), fechaReport);
			 		iso.getTickAut().stop();
			 		iso.setTickMidd(iso.getTickAut());
			 			
			 		log.WriteLog("Trama IN: \n" + processor.getXmlIn(), TypeLog.bceaut, TypeWriteLog.file);
			 		System.out.println(processor.getXmlIn());
			 		
			 		if(response.isError()){
			 			
			 			if(response.getResultCode().equals("1")){
			 				
			 				iso.setISO_039_ResponseCode("000");
			 				iso.setISO_039p_ResponseDetail(StringUtils.isNullOrEmpty(response.getResultText())
			 						?"TRANSACCION EXITOSA":response.getResultText());
			 				iso.setISO_044_AddRespData(response.getCodeErrorId() + "|" + response.getUtfi());
			 				if(response.getTrxValues() != null){
			 					
			 					iso.setISO_114_ExtendedData(SerializationObject.ObjectToXML((Serializable) response.getTrxValues()));
			 					iso.setISO_114_ExtendedData(iso.getISO_114_ExtendedData().
			 							replace("com.fitbank.middleware.bceisuatorizator.", ""));
			 				}
			 				if(iso.getISO_124_ExtendedData().contains("CONFIRM")){
			 					
			 					iso.setISO_038_AutorizationNumber(Arrays.asList(response.getResultText().split("\\:")).get(1).trim());
			 				}
			 				
			 			}else{
			 				
			 				String a = StringUtils.IsNumber(response.getCodeErrorId())? 
			 						   StringUtils.padLeft(response.getCodeErrorId(), 3, "0"):"304";
			 				if(a.equals("000"))
			 					a = "001";
			 				else if (a.equals("304")) {
								iso.setISO_044_AddRespData(response.getCodeErrorId());
								iso.setISO_039_ResponseCode(a);
							}else {
								iso.setISO_039_ResponseCode(a);
							}	
			 				iso.setISO_039p_ResponseDetail(response.getResultText().toUpperCase());
			 			}
			 			iso.setWsIso_LogStatus(2);
			 			log.WriteLog("Trama OUT: \n" + processor.getXmlResponse(), TypeLog.bceaut, TypeWriteLog.file);
			 			
			 		}else {
						
			 			iso.setISO_039_ResponseCode("909");
			 			iso.setISO_039p_ResponseDetail(response.getResultText());
			 			log.WriteLog("Trama OUT [Sec:] " + iso.getISO_011_SysAuditNumber() +" : \n"  + response.getResultText(), TypeLog.bceaut, TypeWriteLog.file);
					}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
 			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, true));
 			log.WriteLog("Trama OUT [Sec:] " + iso.getISO_011_SysAuditNumber() +" : \n"  + iso.getISO_039p_ResponseDetail(), TypeLog.bceaut, TypeWriteLog.file);
 			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
			iso.setWsTempTrx((iso.getTickMidd().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	protected void putCertificate(){
		  
		  if(MemoryGlobal.flagPutCert){
			  
		  	 String path = MemoryGlobal.currentPath;
		  	 log.WriteLogMonitor("Current Path [CertBCE 1] ===> " + path + MemoryGlobal.nameCertBceI, TypeMonitor.monitor, null);
		     System.setProperty("javax.net.ssl.keyStore", path + MemoryGlobal.nameCertBceI);
	         System.setProperty("javax.net.ssl.keyStorePassword", MemoryGlobal.pwdCertI);
	         System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
	      }
	 }
	 protected void putCertificate_II(){
		 
		 if(MemoryGlobal.flagPutCertII){
		 	String path = MemoryGlobal.currentPath;
		 	log.WriteLogMonitor("Current Path [CertBCE 2] ===> " + path + MemoryGlobal.nameCertBceII, TypeMonitor.monitor, null);
		    System.setProperty("javax.net.ssl.keyStore", path + MemoryGlobal.nameCertBceII);
	        System.setProperty("javax.net.ssl.keyStorePassword", MemoryGlobal.pwdCertII);
	        System.setProperty("com.sun.net.ssl.dhKeyExchangeFix", "true");
	        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "false");
	        System.setProperty("sun.security.ssl.allowLegacyHelloMessages", "false");
		 }
	 }

	@SuppressWarnings("unused")
	public wIso8583 ProcessAdminMethodsBCE(wIso8583 iso){
			
			if(iso.getWsTransactionConfig().getProccodeReverFlag() == 0){
				TransactionConfiguration respa = iso.getWsTransactionConfig();
				try {
					
					/**Validacion*/
					Config cfg = new Config();
					String IP = iso.getWs_IP();
					String procTrxCfg = iso.getWsTransactionConfig().getProccodeTransactionFit();
					int codValid = (Integer.parseInt(iso.getISO_003_ProcessingCode()) -1);
					TransactionConfiguration trx = new TransactionConfiguration(new 
							TransactionConfig(String.valueOf(codValid), Integer.parseInt(iso.getISO_024_NetworkId()), 
							iso.getISO_018_MerchantType(), -1));
					
					if(trx != null){
						
						iso.setWsTransactionConfig(trx);
						
					}else {
						
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail("NO SE HA PODIDO RECUPERAR TRX. CONFIGURATION PARA VALIDACION: " + String.valueOf(codValid));
						return iso;
					}
					iso.setISO_003_ProcessingCode(String.valueOf(codValid));
					iso.setISO_124_ExtendedData("VALIDACION-TRANSACCION");
					cfg = cfg.getConfigSystem(iso.getISO_041_CardAcceptorID() + "_" + iso.getISO_042_Card_Acc_ID_Code());
					if(cfg != null){
						
						//if(StringUtils.IsNullOrEmpty(iso.getISO_102_AccountID_1()))
					    iso.setISO_102_AccountID_1(Arrays.asList(cfg.getCfg_Valor().split("-")).get(1));
						iso.setISO_052_PinBlock(Arrays.asList(cfg.getCfg_Valor().split("-")).get(2));
						iso.setISO_043_CardAcceptorLoc(Arrays.asList(cfg.getCfg_Valor().split("-")).get(0));
						/*Seccion Fondeos*/
						if(iso.getISO_034_PANExt().contains("_")){
							
							cfg = cfg.getConfigSystem(iso.getISO_034_PANExt());
							if(cfg != null){
								
								iso.setISO_034_PANExt(Arrays.asList(cfg.getCfg_Valor().split("-")).get(1));
								
							}else {
								
								codValid = (Integer.parseInt(iso.getISO_003_ProcessingCode())  + 1);
								iso.setISO_039_ResponseCode("906");
								iso.setISO_039p_ResponseDetail("NO SE PUEDE RECUPERAR USUARIO-CUENTA OFICINA DESTINO PARA FONDEO: "
										+ "(" + iso.getISO_034_PANExt() + ")"); 
								
								return iso;
							}
						}
						/*Fin Fondeos*/
						
					}else{
						
						codValid = (Integer.parseInt(iso.getISO_003_ProcessingCode())  + 1);
						iso.setISO_039_ResponseCode("906");
						iso.setISO_039p_ResponseDetail("NO SE HA ENCONTRADO LA CONFIGURACION (USUARIO - CLAVE) PARA LA SUCURSAL " 
						+ iso.getISO_041_CardAcceptorID() + " OFICINA: " + iso.getISO_042_Card_Acc_ID_Code());
						return iso;
					}
					
					iso = ProcessTransactionsBCE_II(iso);
					
					if(iso.getISO_039_ResponseCode().equals("000")){
						
						/*Imprime la Validacion*/
						iso.getWsTransactionConfig().setIp(IP);
						Iso8583 isoPrint = new Iso8583(iso);
						LoggerConfig.WriteMonitor(iso, isoPrint);
						/*End Imprime la Validacion*/
						
						iso.setISO_124_ExtendedData("CONFIRMACION-TRANSACCION");
						codValid = (Integer.parseInt(iso.getISO_003_ProcessingCode())  + 1);
						iso.getWsTransactionConfig().setProccodeTransactionFit(procTrxCfg);
						iso.setISO_003_ProcessingCode(String.valueOf(codValid));
						iso = ProcessTransactionsBCE_II(iso);
						
					}else{
						
						codValid = (Integer.parseInt(iso.getISO_003_ProcessingCode()) + 1);
						iso.setISO_003_ProcessingCode(String.valueOf(codValid));
					}
					
				} catch (Exception e) {
					
					iso.setISO_039_ResponseCode("909");
					iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, true));
				}finally {
					
					if(iso.getISO_124_ExtendedData().equals("CONFIRMACION-TRANSACCION"))
						iso.setWsTransactionConfig(respa);
				}
			}else {
				
				iso = executeOnlyTransaction(iso);
			}
			return iso;
	}
	
	public wIso8583 executeOnlyTransaction(wIso8583 iso){
		
		Config cfg = new Config();
		iso.setISO_124_ExtendedData("CONFIRMACION-TRANSACCION");
		try {
			
			cfg = cfg.getConfigSystem(iso.getISO_041_CardAcceptorID() + "_" + iso.getISO_042_Card_Acc_ID_Code());
			if(cfg != null){
				
			    iso.setISO_102_AccountID_1(Arrays.asList(cfg.getCfg_Valor().split("-")).get(1));
				iso.setISO_052_PinBlock(Arrays.asList(cfg.getCfg_Valor().split("-")).get(2));
				iso.setISO_043_CardAcceptorLoc(Arrays.asList(cfg.getCfg_Valor().split("-")).get(0));
				/*Seccion Fondeos*/
				if(iso.getISO_034_PANExt().contains("_")){
					
					cfg = cfg.getConfigSystem(iso.getISO_034_PANExt());
					if(cfg != null){
						
						iso.setISO_034_PANExt(Arrays.asList(cfg.getCfg_Valor().split("-")).get(1));
						
					}else {
						
						iso.setISO_039_ResponseCode("906");
						iso.setISO_039p_ResponseDetail("NO SE PUEDE RECUPERAR USUARIO-CUENTA OFICINA DESTINO PARA FONDEO: "
								+ "(" + iso.getISO_034_PANExt() + ")"); 
						
						return iso;
					}
				}
				/*Fin Fondeos*/
				
			}else{
				
				iso.setISO_039_ResponseCode("906");
				iso.setISO_039p_ResponseDetail("NO SE HA ENCONTRADO LA CONFIGURACION (USUARIO - CLAVE) PARA LA SUCURSAL " 
				+ iso.getISO_041_CardAcceptorID() + " OFICINA: " + iso.getISO_042_Card_Acc_ID_Code());
				return iso;
			}
			
			iso = ProcessTransactionsBCE_II(iso);
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, true));
		}
		return iso;
	}
	
	public wIso8583 getBalanceReport(wIso8583 iso){
		
		try {
			
			putCertificate_II();
			
			Config cfg = new Config();
			
			cfg = cfg.getConfigSystem(iso.getISO_041_CardAcceptorID() + "_" + iso.getISO_042_Card_Acc_ID_Code());
			if(cfg != null){
				
				iso.setISO_102_AccountID_1(Arrays.asList(cfg.getCfg_Valor().split("-")).get(1));
				iso.setISO_052_PinBlock(Arrays.asList(cfg.getCfg_Valor().split("-")).get(2));
				iso.setISO_043_CardAcceptorLoc(Arrays.asList(cfg.getCfg_Valor().split("-")).get(0));
				
			}else{
			
				iso.setISO_039_ResponseCode("906");
				iso.setISO_039p_ResponseDetail("NO SE HA ENCONTRADO LA CONFIGURACION (USUARIO - CLAVE) PARA LA SUCURSAL " 
				+ iso.getISO_041_CardAcceptorID() + " OFICINA: " + iso.getISO_042_Card_Acc_ID_Code());
				return iso;
			}
			
			ProcessorTransactions_II processor = new ProcessorTransactions_II(MemoryGlobal.urlBceAdmin, 
				MemoryGlobal.ipBCEAdmin, MemoryGlobal.portBCEAdmin, iso.getISO_102_AccountID_1(), iso.getISO_052_PinBlock(), 
				Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("-")).get(2),
				Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("-")).get(3), 
				(iso.getWsTransactionConfig().getProccodeTimeOutValue() - 5000), 
				 iso.getWsTransactionConfig().getProccodeTimeOutValue());
	 		
	 		iso.getTickAut().reset();
			iso.getTickAut().start();
	 		Response response = processor.ProcessHandlerTransaction(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("-")).get(0), 
	 				Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("-")).get(1), 
	 				iso.getISO_002_PAN(), iso.getISO_102_AccountID_1(), iso.getISO_034_PANExt(), 
	 				iso.getISO_004_AmountTransaction(), 
	 				iso.getISO_011_SysAuditNumber(), iso.getISO_052_PinBlock(), null);
	 		iso.getTickAut().stop();
	 		
	 		log.WriteLog("Trama IN: \n" + processor.getXmlIn(), TypeLog.bceaut, TypeWriteLog.file);
	 		System.out.println(processor.getXmlIn());
	 		
	 		if(response.isError()){
	 			
	 			if(response.getResultCode().equals("1")){
	 				
	 				iso.setISO_039_ResponseCode("000");
	 				iso.setISO_039p_ResponseDetail(StringUtils.isNullOrEmpty(response.getResultText())
	 						?"TRANSACCION EXITOSA":response.getResultText());
	 				iso.setISO_044_AddRespData(response.getCodeErrorId() + "|" + response.getUtfi());
	 				if(response.getWallets() != null){
	 					
	 					iso.setISO_114_ExtendedData(SerializationObject.ObjectToXML((Serializable) response.getWallets().getWallet()));
	 					iso.setISO_114_ExtendedData(iso.getISO_114_ExtendedData().
	 							replace("com.fitbank.middleware.bceisuatorizator.", ""));
	 				}
	 			}else{
	 				
	 				String a = StringUtils.IsNumber(response.getCodeErrorId())? 
	 						   StringUtils.padLeft(response.getCodeErrorId(), 3, "0"):"304";
	 				if(a.equals("000"))
	 					a = "001";
	 				else if (a.equals("304")) {
						iso.setISO_044_AddRespData(response.getCodeErrorId());
						iso.setISO_039_ResponseCode(a);
					}else {
						iso.setISO_039_ResponseCode(a);
					}	
	 				iso.setISO_039p_ResponseDetail(response.getResultText().toUpperCase());
	 			}
	 			iso.setWsIso_LogStatus(2);
	 			log.WriteLog("Trama OUT: \n" + processor.getXmlResponse(), TypeLog.bceaut, TypeWriteLog.file);
	 			
	 		}else {
				
	 			iso.setISO_039_ResponseCode("909");
	 			iso.setISO_039p_ResponseDetail(response.getResultText());
	 			log.WriteLog("Trama OUT [Sec:] " + iso.getISO_011_SysAuditNumber() +" : \n"  + response.getResultText(), TypeLog.bceaut, TypeWriteLog.file);
			}
	 		
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, true));
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	public wIso8583 getDailyReport(wIso8583 iso){
		try {
			
			putCertificate_II();
			
			Config cfg = new Config();
			
			cfg = cfg.getConfigSystem(iso.getISO_041_CardAcceptorID() + "_" + iso.getISO_042_Card_Acc_ID_Code());
			if(cfg != null){
				
				
				iso.setISO_102_AccountID_1(Arrays.asList(cfg.getCfg_Valor().split("-")).get(1));
				iso.setISO_052_PinBlock(Arrays.asList(cfg.getCfg_Valor().split("-")).get(2));
				iso.setISO_043_CardAcceptorLoc(Arrays.asList(cfg.getCfg_Valor().split("-")).get(0));
				
			}else{
			
				iso.setISO_039_ResponseCode("906");
				iso.setISO_039p_ResponseDetail("NO SE HA ENCONTRADO LA CONFIGURACION (USUARIO - CLAVE) PARA LA SUCURSAL " 
				+ iso.getISO_041_CardAcceptorID() + " OFICINA: " + iso.getISO_042_Card_Acc_ID_Code());
				return iso;
			}
			
			ProcessorTransactions_II processor = new ProcessorTransactions_II(MemoryGlobal.urlBceAdmin, 
					MemoryGlobal.ipBCEAdmin, MemoryGlobal.portBCEAdmin, iso.getISO_102_AccountID_1(), iso.getISO_052_PinBlock(), 
					Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("-")).get(2),
					Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("-")).get(3), 
					(iso.getWsTransactionConfig().getProccodeTimeOutValue() - 5000), 
					 iso.getWsTransactionConfig().getProccodeTimeOutValue());
			Date fechaReport = null;
	 		if(iso.getISO_124_ExtendedData().equalsIgnoreCase("S"))
	 			fechaReport = iso.getISO_013_LocalDate();
			
	 		iso.getTickAut().reset();
			iso.getTickAut().start();
	 		Response response = processor.ProcessHandlerTransaction(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("-")).get(0), 
	 				Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("-")).get(1), 
	 				iso.getISO_002_PAN(), iso.getISO_102_AccountID_1(), iso.getISO_034_PANExt(), 
	 				iso.getISO_004_AmountTransaction(), 
	 				iso.getISO_011_SysAuditNumber(), iso.getISO_052_PinBlock(), fechaReport);
	 		iso.getTickAut().stop();
	 		iso.setTickMidd(iso.getTickAut());
	 		log.WriteLog("Trama IN: \n" + processor.getXmlIn(), TypeLog.bceaut, TypeWriteLog.file);
	 		System.out.println(processor.getXmlIn());
	 		
	 		if(response.isError()){
	 			
	 			if(response.getResultCode().equals("1")){
	 				
	 				iso.setISO_039_ResponseCode("000");
	 				iso.setISO_039p_ResponseDetail(StringUtils.isNullOrEmpty(response.getResultText())
	 						?"TRANSACCION EXITOSA":response.getResultText().toUpperCase());
	 				iso.setISO_044_AddRespData(response.getCodeErrorId() + "|" + response.getUtfi());
	 				
	 				if(response.getZipDailyReport() != null){
	 					
	 					iso.setISO_114_ExtendedData(response.getZipDailyReport());
	 				}
	 				
	 			}else{
	 				
	 				String a = StringUtils.IsNumber(response.getCodeErrorId())? 
	 						   StringUtils.padLeft(response.getCodeErrorId(), 3, "0"):"304";
	 				if(a.equals("000"))
	 					a = "001";
	 				else if (a.equals("304")) {
						iso.setISO_044_AddRespData(response.getCodeErrorId());
						iso.setISO_039_ResponseCode(a);
					}else {
						iso.setISO_039_ResponseCode(a);
					}	
	 				iso.setISO_039p_ResponseDetail(response.getResultText().toUpperCase());
	 			}
	 			iso.setWsIso_LogStatus(2);
	 			log.WriteLog("Trama OUT: \n" + processor.getXmlResponse(), TypeLog.bceaut, TypeWriteLog.file);
	 			
	 		}else {
				
	 			iso.setISO_039_ResponseCode("909");
	 			iso.setISO_039p_ResponseDetail(response.getResultText());
	 			log.WriteLog("Trama OUT [Sec:] " + iso.getISO_011_SysAuditNumber() +" : \n"  + response.getResultText(), TypeLog.bceaut, TypeWriteLog.file);
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, true));
		}finally {
		
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
	    }
		return iso;
	}
	
	
}

