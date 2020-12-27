package com.belejanor.switcher.authorizations;

import java.util.Arrays;
import java.util.List;

import com.belejanor.switcher.bimo.pacs.camt_998_211.Document;
import com.belejanor.switcher.bimo.pacs.camt_998_372.ListOfPayments;
import com.belejanor.switcher.bimo.processor.ClientProcessorBimo;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.parser.BimoParser;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.StringUtils;
import com.belejanor.switcher.validatefinantial.ValidateWalletBimo;

public class BanredIsAut implements ITranBIMOAut{

	private Logger log;
	
	public BanredIsAut() {
		log = new Logger();
	}
	
	@Override
	public wIso8583 EnrolamientoBimoAut(wIso8583 iso) {
		
		Document documentReq = null;
		ClientProcessorBimo procesessorBimo = null; 
		Object objRes = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			
			BimoParser parser = new BimoParser();
			documentReq = parser.parseEnrollAutBimo(iso);
			if(parser.getCodError().equals("000")){

				log.WriteOptimizeLog(documentReq, TypeLog.brdAut, Document.class, true);
				procesessorBimo = new ClientProcessorBimo();
				Ref<wIso8583> isoRef = new Ref<wIso8583>(iso);
				objRes = procesessorBimo.getResponseDataClientRest("camt.998.211", 
						com.belejanor.switcher.bimo.pacs.camt_998_211.Document.class, 
						com.belejanor.switcher.bimo.pacs.camt_998_212.Document.class,documentReq, isoRef);
				iso = isoRef.get();
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					if(objRes instanceof com.belejanor.switcher.bimo.genericerror.Document){
						
						com.belejanor.switcher.bimo.genericerror.Document genericError = 
								(com.belejanor.switcher.bimo.genericerror.Document)objRes;
						iso.setISO_039_ResponseCode(genericError.getTrx().getStsRsnInf().getRsn().getCode());
						iso.setISO_039p_ResponseDetail(genericError.getTrx().getStsRsnInf().getRsn().getAddtlInf().get(0));
						
						log.WriteOptimizeLog(genericError, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.genericerror.Document.class, true);
						
					}else if (objRes instanceof com.belejanor.switcher.bimo.pacs.camt_998_212.Document) {
						
						com.belejanor.switcher.bimo.pacs.camt_998_212.Document document =
								 (com.belejanor.switcher.bimo.pacs.camt_998_212.Document)objRes;
						iso.setISO_039_ResponseCode(document.getPrtryMsg().getAcctEnroll().getStsRsnInf()
								   .getRsn().getCode());
						iso.setISO_039p_ResponseDetail(document.getPrtryMsg().getAcctEnroll().getStsRsnInf()
								.getRsn().getAddtlInf().get(0));
					
						log.WriteOptimizeLog(document, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.pacs.camt_998_212.Document.class, true);
					}
				}else{
					
					log.WriteOptimizeLog("Error Servicio BIMO Secuencial: " + iso.getSecuencialCommandExecutor() 
					+ " -- " + iso.getISO_039p_ResponseDetail() , TypeLog.brdAut, 
					String.class, false);
				}
				
			}else{
				
				iso.setISO_039_ResponseCode(parser.getCodError());
				iso.setISO_039p_ResponseDetail(parser.getDesError());
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::@EnrolamientoBimoAut ", 
					     TypeMonitor.error, e);
		}
		return iso;
	}

	@Override
	public wIso8583 AdminBimoAut(wIso8583 iso) {
		com.belejanor.switcher.bimo.pacs.camt_998_311.Document documentReq = null;
		ClientProcessorBimo procesessorBimo = null; 
		Object objRes = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			BimoParser parser = new BimoParser();
			documentReq = parser.parseAdminWalletAutBimo(iso);
			
			if(parser.getCodError().equals("000")){
				
				log.WriteOptimizeLog(documentReq, TypeLog.brdAut, 
						com.belejanor.switcher.bimo.pacs.camt_998_311.Document.class, true);
				
				procesessorBimo = new ClientProcessorBimo();
				Ref<wIso8583> isoRef = new Ref<wIso8583>(iso);
				objRes = procesessorBimo.getResponseDataClientRest("camt.998.311", 
						com.belejanor.switcher.bimo.pacs.camt_998_311.Document.class, 
						com.belejanor.switcher.bimo.pacs.camt_998_312.Document.class,documentReq, isoRef);
				iso = isoRef.get();
				
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					if(objRes instanceof com.belejanor.switcher.bimo.genericerror.Document){
						
						com.belejanor.switcher.bimo.genericerror.Document genericError = 
								(com.belejanor.switcher.bimo.genericerror.Document)objRes;
						iso.setISO_039_ResponseCode(genericError.getTrx().getStsRsnInf().getRsn().getCode());
						iso.setISO_039p_ResponseDetail(genericError.getTrx().getStsRsnInf().getRsn().getAddtlInf().get(0));
						
						
						log.WriteOptimizeLog(genericError, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.genericerror.Document.class, true);
						
					}else if (objRes instanceof com.belejanor.switcher.bimo.pacs.camt_998_312.Document) {
						
						com.belejanor.switcher.bimo.pacs.camt_998_312.Document document =
								 (com.belejanor.switcher.bimo.pacs.camt_998_312.Document)objRes;
						iso.setISO_039_ResponseCode(document.getPrtryMsg().getBlckUser().getStsRsnInf()
								   .getRsn().getCode());
						iso.setISO_039p_ResponseDetail(document.getPrtryMsg().getBlckUser().getStsRsnInf()
								.getRsn().getAddtlInf().get(0));
						
						log.WriteOptimizeLog(document, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.pacs.camt_998_312.Document.class, true);
					}
				}else{
					
					log.WriteOptimizeLog("Error Servicio BIMO Secuencial: " + iso.getSecuencialCommandExecutor() 
					+ " -- " + iso.getISO_039p_ResponseDetail() , TypeLog.brdAut, 
					String.class, false);
	
				}
			}else{
				
				
				iso.setISO_039_ResponseCode(parser.getCodError());
				iso.setISO_039p_ResponseDetail(parser.getDesError());
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::@AdminFinancialBimoAut ", 
					     TypeMonitor.error, e);
		}
		
		return iso;
	}

	@Override
	public wIso8583 ConsultaMovilCtaBimoAut(wIso8583 iso) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public wIso8583 VerificarUsuarioBimoAut(wIso8583 iso) {
		
		com.belejanor.switcher.bimo.pacs.camt_998_421.Document documentReq = null;
		ClientProcessorBimo procesessorBimo = null; 
		Object objRes = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			BimoParser parser = new BimoParser();
			documentReq = parser.parseValidaUsuarioAutBimo(iso);
			
			if(parser.getCodError().equals("000")){
				
				
				log.WriteOptimizeLog(documentReq, TypeLog.brdAut, 
						    com.belejanor.switcher.bimo.pacs.camt_998_421.Document.class, true);
				procesessorBimo = new ClientProcessorBimo();
				Ref<wIso8583> isoRef = new Ref<wIso8583>(iso);
				objRes = procesessorBimo.getResponseDataClientRest("camt.998.421", 
						com.belejanor.switcher.bimo.pacs.camt_998_421.Document.class, 
						com.belejanor.switcher.pacs.camt_998_422.Document.class,documentReq, isoRef);
				iso = isoRef.get();
				
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					if(objRes instanceof com.belejanor.switcher.bimo.genericerror.Document){
						
						com.belejanor.switcher.bimo.genericerror.Document genericError = 
								(com.belejanor.switcher.bimo.genericerror.Document)objRes;
						iso.setISO_039_ResponseCode(genericError.getTrx().getStsRsnInf().getRsn().getCode());
						iso.setISO_039p_ResponseDetail(genericError.getTrx().getStsRsnInf().getRsn().getAddtlInf().get(0));
						
						log.WriteOptimizeLog(genericError, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.genericerror.Document.class, true);
						
					}else if (objRes instanceof com.belejanor.switcher.pacs.camt_998_422.Document) {
						
						com.belejanor.switcher.pacs.camt_998_422.Document document =
								 (com.belejanor.switcher.pacs.camt_998_422.Document)objRes;
						iso.setISO_039_ResponseCode(document.getPrtryMsg().getValField().getStsRsnInf()
								   .getRsn().getCode());
						iso.setISO_039p_ResponseDetail(document.getPrtryMsg().getValField().getStsRsnInf()
								.getRsn().getAddtlInf().get(0));
						
						
						log.WriteOptimizeLog(document, TypeLog.brdAut, 
								com.belejanor.switcher.pacs.camt_998_422.Document.class, true);
					}
				}else{
					
					log.WriteOptimizeLog("Error Servicio BIMO Secuencial: " + iso.getSecuencialCommandExecutor()  
					+ " -- " + iso.getISO_039p_ResponseDetail() , TypeLog.brdAut, 
					String.class, false);
				}
				
			}else{
				
				iso.setISO_039_ResponseCode(parser.getCodError());
				iso.setISO_039p_ResponseDetail(parser.getDesError());
			}
			
		} catch (Exception e) {
		
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::@VerificarUsuarioBimoAut ", 
					     TypeMonitor.error, e);
		}
		
		return iso;
	}

	@Override
	public wIso8583 RegenerarPasswordBimoAut(wIso8583 iso) {
		com.belejanor.switcher.pacs.camt_998_471.Document documentReq = null;
		ClientProcessorBimo procesessorBimo = null; 
		Object objRes = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			BimoParser parser = new BimoParser();
			documentReq = parser.parseReestablecePinAutBimo(iso);
			
			if(parser.getCodError().equals("000")){
				
				log.WriteOptimizeLog(documentReq, TypeLog.brdAut, 
						com.belejanor.switcher.pacs.camt_998_471.Document.class, true);
				
				procesessorBimo = new ClientProcessorBimo();
				Ref<wIso8583> isoRef = new Ref<wIso8583>(iso);
				objRes = procesessorBimo.getResponseDataClientRest("camt.998.471", 
						com.belejanor.switcher.pacs.camt_998_471.Document.class, 
						com.belejanor.switcher.bimo.pacs.camt_998_472.Document.class,documentReq, isoRef);
				iso = isoRef.get();
				
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					if(objRes instanceof com.belejanor.switcher.bimo.genericerror.Document){
						
						com.belejanor.switcher.bimo.genericerror.Document genericError = 
								(com.belejanor.switcher.bimo.genericerror.Document)objRes;
						iso.setISO_039_ResponseCode(genericError.getTrx().getStsRsnInf().getRsn().getCode());
						iso.setISO_039p_ResponseDetail(genericError.getTrx().getStsRsnInf().getRsn().getAddtlInf().get(0));
						
						
						log.WriteOptimizeLog(genericError, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.genericerror.Document.class, true);
						
					}else if (objRes instanceof com.belejanor.switcher.bimo.pacs.camt_998_472.Document) {
						
						com.belejanor.switcher.bimo.pacs.camt_998_472.Document document =
								 (com.belejanor.switcher.bimo.pacs.camt_998_472.Document)objRes;
						iso.setISO_039_ResponseCode(document.getPrtryMsg().getRegeneratePIN().getStsRsnInf()
								   .getRsn().getCode());
						iso.setISO_039p_ResponseDetail(document.getPrtryMsg().getRegeneratePIN().getStsRsnInf()
								.getRsn().getAddtlInf().get(0));
						
						log.WriteOptimizeLog(document, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.pacs.camt_998_472.Document.class, true);
					}
				}else{
					
					log.WriteOptimizeLog("Error Servicio BIMO Secuencial: " + iso.getSecuencialCommandExecutor() 
					+ " -- " + iso.getISO_039p_ResponseDetail() , TypeLog.brdAut, 
					String.class, false);
				}
			}else{
				
				iso.setISO_039_ResponseCode(parser.getCodError());
				iso.setISO_039p_ResponseDetail(parser.getDesError());
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::@RegenerarPasswordBimoAut ", 
					     TypeMonitor.error, e);
		}
		
		return iso;
	}

	@Override
	public wIso8583 AdminFinancialBimoAut(wIso8583 iso) {
		
		com.belejanor.switcher.bimo.pacs.camt_998_321.Document documentReq = null;
		ClientProcessorBimo procesessorBimo = null; 
		Object objRes = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			BimoParser parser = new BimoParser();
			documentReq = parser.parseTransferenciaBimo(iso);
			
			if(parser.getCodError().equals("000")){
				
				log.WriteOptimizeLog(documentReq, TypeLog.brdAut, 
						com.belejanor.switcher.bimo.pacs.camt_998_321.Document.class, true);
				
				procesessorBimo = new ClientProcessorBimo();
				Ref<wIso8583> isoRef = new Ref<wIso8583>(iso);
				objRes = procesessorBimo.getResponseDataClientRest("camt.998.321", 
						com.belejanor.switcher.bimo.pacs.camt_998_321.Document.class, 
						com.belejanor.switcher.bimo.pacs.camt_998_322.Document.class,documentReq, isoRef);
				iso = isoRef.get();
				
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					if(objRes instanceof com.belejanor.switcher.bimo.genericerror.Document){
						
						com.belejanor.switcher.bimo.genericerror.Document genericError = 
								(com.belejanor.switcher.bimo.genericerror.Document)objRes;
						iso.setISO_039_ResponseCode(genericError.getTrx().getStsRsnInf().getRsn().getCode());
						iso.setISO_039p_ResponseDetail(genericError.getTrx().getStsRsnInf().getRsn().getAddtlInf().get(0));
						
						
						log.WriteOptimizeLog(genericError, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.genericerror.Document.class, true);
						
					}else if (objRes instanceof com.belejanor.switcher.bimo.pacs.camt_998_322.Document) {
						
						com.belejanor.switcher.bimo.pacs.camt_998_322.Document document =
								 (com.belejanor.switcher.bimo.pacs.camt_998_322.Document)objRes;
						iso.setISO_039_ResponseCode(document.getPrtryMsg().getCdtTrfTxInf().getStsRsnInf()
								   .getRsn().getCode());
						iso.setISO_039p_ResponseDetail(document.getPrtryMsg().getCdtTrfTxInf().getStsRsnInf()
								.getRsn().getAddtlInf().get(0));
						
						log.WriteOptimizeLog(document, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.pacs.camt_998_322.Document.class, true);
					}
				}else{
					
					log.WriteOptimizeLog("Error Servicio BIMO Secuencial: " + iso.getSecuencialCommandExecutor()  
					+ " -- " + iso.getISO_039p_ResponseDetail() , TypeLog.brdAut, 
					String.class, false);
				}
			}else{
				
				iso.setISO_039_ResponseCode(parser.getCodError());
				iso.setISO_039p_ResponseDetail(parser.getDesError());
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::@AdminFinancialBimoAut ", 
					     TypeMonitor.error, e);
		}
		
		return iso;
	}

	@Override
	public wIso8583 SolicitudCobroBimoAut(wIso8583 iso) {
		
		com.belejanor.switcher.bimo.pacs.camt_998_381.Document documentReq = null;
		ClientProcessorBimo procesessorBimo = null; 
		Object objRes = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			BimoParser parser = new BimoParser();
			documentReq = parser.parseSolicitudCobroAutBimo(iso);
			
			if(parser.getCodError().equals("000")){
				
				log.WriteOptimizeLog(documentReq, TypeLog.brdAut, 
						com.belejanor.switcher.bimo.pacs.camt_998_381.Document.class, true);
				
				procesessorBimo = new ClientProcessorBimo();
				Ref<wIso8583> isoRef = new Ref<wIso8583>(iso);
				objRes = procesessorBimo.getResponseDataClientRest("camt.998.381", 
						com.belejanor.switcher.bimo.pacs.camt_998_381.Document.class, 
						com.belejanor.switcher.bimo.pacs.camt_998_382.Document.class,documentReq, isoRef);
				iso = isoRef.get();
				
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					if(objRes instanceof com.belejanor.switcher.bimo.genericerror.Document){
						
						com.belejanor.switcher.bimo.genericerror.Document genericError = 
								(com.belejanor.switcher.bimo.genericerror.Document)objRes;
						iso.setISO_039_ResponseCode(genericError.getTrx().getStsRsnInf().getRsn().getCode());
						iso.setISO_039p_ResponseDetail(genericError.getTrx().getStsRsnInf().getRsn().getAddtlInf().get(0));
						
						
						log.WriteOptimizeLog(genericError, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.genericerror.Document.class, true);
						
					}else if (objRes instanceof com.belejanor.switcher.bimo.pacs.camt_998_382.Document) {
						
						com.belejanor.switcher.bimo.pacs.camt_998_382.Document document =
								 (com.belejanor.switcher.bimo.pacs.camt_998_382.Document)objRes;
						iso.setISO_039_ResponseCode(document.getPrtryMsg().getCollectRq().getStsRsnInf()
								   .getRsn().getCode());
						iso.setISO_039p_ResponseDetail(document.getPrtryMsg().getCollectRq().getStsRsnInf()
								.getRsn().getAddtlInf().get(0));
						
						log.WriteOptimizeLog(document, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.pacs.camt_998_382.Document.class, true);
					}
				}else{
					
					log.WriteOptimizeLog("Error Servicio BIMO Secuencial: " + iso.getSecuencialCommandExecutor() 
					+ " -- " + iso.getISO_039p_ResponseDetail() , TypeLog.brdAut, 
					String.class, false);
				}
			}else{
				
				iso.setISO_039_ResponseCode(parser.getCodError());
				iso.setISO_039p_ResponseDetail(parser.getDesError());
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::@SolicitudCobroBimoAut ", 
					     TypeMonitor.error, e);
		}
		
		return iso;
	}

	@Override
	public wIso8583 RechazoSolicitudCobro(wIso8583 iso) {
		
		com.belejanor.switcher.bimo.pacs.camt_998_431.Document documentReq = null;
		ClientProcessorBimo procesessorBimo = null; 
		Object objRes = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			BimoParser parser = new BimoParser();
			documentReq = parser.parseRechazarSolicituAutBimo(iso);
			
			if(parser.getCodError().equals("000")){
				
				log.WriteOptimizeLog(documentReq, TypeLog.brdAut, 
						com.belejanor.switcher.bimo.pacs.camt_998_431.Document.class, true);
				
				procesessorBimo = new ClientProcessorBimo();
				Ref<wIso8583> isoRef = new Ref<wIso8583>(iso);
				objRes = procesessorBimo.getResponseDataClientRest("camt.998.431", 
						com.belejanor.switcher.bimo.pacs.camt_998_431.Document.class, 
						com.belejanor.switcher.bimo.pacs.camt_998_432.Document.class,documentReq, isoRef);
				iso = isoRef.get();
				
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					if(objRes instanceof com.belejanor.switcher.bimo.genericerror.Document){
						
						com.belejanor.switcher.bimo.genericerror.Document genericError = 
								(com.belejanor.switcher.bimo.genericerror.Document)objRes;
						iso.setISO_039_ResponseCode(genericError.getTrx().getStsRsnInf().getRsn().getCode());
						iso.setISO_039p_ResponseDetail(genericError.getTrx().getStsRsnInf().getRsn().getAddtlInf().get(0));
						
						
						log.WriteOptimizeLog(genericError, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.genericerror.Document.class, true);
						
					}else if (objRes instanceof com.belejanor.switcher.bimo.pacs.camt_998_432.Document) {
						
						com.belejanor.switcher.bimo.pacs.camt_998_432.Document document =
								 (com.belejanor.switcher.bimo.pacs.camt_998_432.Document)objRes;
						iso.setISO_039_ResponseCode(document.getPrtryMsg().getCollectRjct().getStsRsnInf()
								   .getRsn().getCode());
						iso.setISO_039p_ResponseDetail(document.getPrtryMsg().getCollectRjct().getStsRsnInf()
								.getRsn().getAddtlInf().get(0));
						
						log.WriteOptimizeLog(document, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.pacs.camt_998_432.Document.class, true);
					}
				}else{
					
					log.WriteOptimizeLog("Error Servicio BIMO Secuencial: " + iso.getSecuencialCommandExecutor() 
					+ " -- " + iso.getISO_039p_ResponseDetail() , TypeLog.brdAut, 
					String.class, false);
				}
			}else{
				
				iso.setISO_039_ResponseCode(parser.getCodError());
				iso.setISO_039p_ResponseDetail(parser.getDesError());
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::@RechazoSolicitudCobro ", 
					     TypeMonitor.error, e);
		}
		return iso;
	}

	@Override
	public wIso8583 VerificarTelefonoCtaBimoAut(wIso8583 iso) {
		com.belejanor.switcher.bimo.pacs.camt_998_441.Document documentReq = null;
		ClientProcessorBimo procesessorBimo = null; 
		Object objRes = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			BimoParser parser = new BimoParser();
			documentReq = parser.parseValidaTelefonoCtaAutBimo(iso);
			
			if(parser.getCodError().equals("000")){
				
				log.WriteOptimizeLog(documentReq, TypeLog.brdAut, 
						com.belejanor.switcher.bimo.pacs.camt_998_441.Document.class, true);
				
				procesessorBimo = new ClientProcessorBimo();
				Ref<wIso8583> isoRef = new Ref<wIso8583>(iso);
				objRes = procesessorBimo.getResponseDataClientRest("camt.998.441", 
						com.belejanor.switcher.bimo.pacs.camt_998_441.Document.class, 
						com.belejanor.switcher.bimo.pacs.camt_998_442.Document.class,documentReq, isoRef);
				iso = isoRef.get();
				
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					if(objRes instanceof com.belejanor.switcher.bimo.genericerror.Document){
						
						com.belejanor.switcher.bimo.genericerror.Document genericError = 
								(com.belejanor.switcher.bimo.genericerror.Document)objRes;
						iso.setISO_039_ResponseCode(genericError.getTrx().getStsRsnInf().getRsn().getCode());
						iso.setISO_039p_ResponseDetail(genericError.getTrx().getStsRsnInf().getRsn().getAddtlInf().get(0));
						
						
						log.WriteOptimizeLog(genericError, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.genericerror.Document.class, true);
						
					}else if (objRes instanceof com.belejanor.switcher.bimo.pacs.camt_998_442.Document) {
						
						com.belejanor.switcher.bimo.pacs.camt_998_442.Document document =
								 (com.belejanor.switcher.bimo.pacs.camt_998_442.Document)objRes;
						iso.setISO_039_ResponseCode(document.getPrtryMsg().getQryPhonebyAcc().getStsRsnInf()
								   .getRsn().getCode());
						iso.setISO_039p_ResponseDetail(document.getPrtryMsg().getQryPhonebyAcc().getStsRsnInf()
								.getRsn().getAddtlInf().get(0));
						
						log.WriteOptimizeLog(document, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.pacs.camt_998_442.Document.class, true);
					}
				}else{
					
					log.WriteOptimizeLog("Error Servicio BIMO Secuencial: " + iso.getSecuencialCommandExecutor()  
					+ " -- " + iso.getISO_039p_ResponseDetail() , TypeLog.brdAut, 
					String.class, false);
				}
			}else{
				
				iso.setISO_039_ResponseCode(parser.getCodError());
				iso.setISO_039p_ResponseDetail(parser.getDesError());
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::@VerificarTelefonoCtaBimoAut ", 
					     TypeMonitor.error, e);
		}
		
		return iso;
	}
	@Override
	public wIso8583 ListarPagosCobrosBimoAut(wIso8583 iso) {
		com.belejanor.switcher.bimo.pacs.camt_998_371.Document documentReq = null;
		ClientProcessorBimo procesessorBimo = null; 
		Object objRes = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			BimoParser parser = new BimoParser();
			documentReq = parser.parseListarPagosCobrosAutBimo(iso);
			
			if(parser.getCodError().equals("000")){
				
				log.WriteOptimizeLog(documentReq, TypeLog.brdAut, 
						com.belejanor.switcher.bimo.pacs.camt_998_371.Document.class, true);
				
				procesessorBimo = new ClientProcessorBimo();
				Ref<wIso8583> isoRef = new Ref<wIso8583>(iso);
				objRes = procesessorBimo.getResponseDataClientRest("camt.998.371", 
						com.belejanor.switcher.bimo.pacs.camt_998_371.Document.class, 
						com.belejanor.switcher.bimo.pacs.camt_998_372.Document.class,documentReq, isoRef);
				iso = isoRef.get();
				
				StringBuilder paymentsBuild = new StringBuilder();
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					if(objRes instanceof com.belejanor.switcher.bimo.genericerror.Document){
						
						com.belejanor.switcher.bimo.genericerror.Document genericError = 
								(com.belejanor.switcher.bimo.genericerror.Document)objRes;
						iso.setISO_039_ResponseCode(genericError.getTrx().getStsRsnInf().getRsn().getCode());
						iso.setISO_039p_ResponseDetail(genericError.getTrx().getStsRsnInf().getRsn().getAddtlInf().get(0));
						
						log.WriteOptimizeLog(genericError, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.genericerror.Document.class, true);
						
					}else if (objRes instanceof com.belejanor.switcher.bimo.pacs.camt_998_372.Document) {
						
						com.belejanor.switcher.bimo.pacs.camt_998_372.Document document =
								 (com.belejanor.switcher.bimo.pacs.camt_998_372.Document)objRes;
						iso.setISO_039_ResponseCode(document.getPrtryMsg().getQryPendPayments().getStsRsnInf()
								   .getRsn().getCode());
						iso.setISO_039p_ResponseDetail(document.getPrtryMsg().getQryPendPayments().getStsRsnInf()
								.getRsn().getAddtlInf().get(0));
						
						log.WriteOptimizeLog(document, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.pacs.camt_998_372.Document.class, true);
						
						if(iso.getISO_039_ResponseCode().equals("000")){
						
							 List<ListOfPayments> list = document.getPrtryMsg().getQryPendPayments().getPayments();
							 for (ListOfPayments lpys : list) {
								
								paymentsBuild.append(lpys.getEndToEnd() + "|" + lpys.getDate() + "|" + 
								                    lpys.getPhoneOrig() + "|" + 
													lpys.getNameOrig() + "|" + lpys.getPhoneDst() + "|" +
										            lpys.getNameDst() + "|" + lpys.getAmount().getCcy() + "|" +
													lpys.getAmount().getValue() + "|" + lpys.getReference() + "|" +
										            Arrays.asList(lpys.getAccountDst().split("\\|")).get(3)); 
								paymentsBuild.append("^");
							 }
							 iso.setISO_115_ExtendedData(StringUtils.trimEnd(paymentsBuild.toString(),"^"));
						}
					}
				}else{
					
					log.WriteOptimizeLog("Error Servicio BIMO Secuencial: " + iso.getSecuencialCommandExecutor()  
					+ " -- " + iso.getISO_039p_ResponseDetail() , TypeLog.brdAut, 
					String.class, false);
				}
				
			}else{
				
				iso.setISO_039_ResponseCode(parser.getCodError());
				iso.setISO_039p_ResponseDetail(parser.getDesError());
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::@ListarPagosCobrosBimoAut ", 
					     TypeMonitor.error, e);
		}
		return iso;
	}
	
	@Override
	public wIso8583 ValidacionOTP(wIso8583 iso) {
		
		com.belejanor.switcher.bimo.pacs.camt_998_461.Document documentReq = null;
		ClientProcessorBimo procesessorBimo = null; 
		Object objRes = null;
		try {
			
			FitIsAut fit = new FitIsAut();
			iso = fit.addIsoAdditionalRows(new Ref<wIso8583>(iso));
			BimoParser parser = new BimoParser();
			documentReq = parser.parsevalidarOTPAutBimo(iso);
			
			if(parser.getCodError().equals("000")){
				
				log.WriteOptimizeLog(documentReq, TypeLog.brdAut, 
						com.belejanor.switcher.bimo.pacs.camt_998_461.Document.class, true);
				
				procesessorBimo = new ClientProcessorBimo();
				Ref<wIso8583> isoRef = new Ref<wIso8583>(iso);
				objRes = procesessorBimo.getResponseDataClientRest("camt.998.461", 
						com.belejanor.switcher.bimo.pacs.camt_998_461.Document.class, 
						com.belejanor.switcher.bimo.pacs.camt_998_462.Document.class,documentReq, isoRef);
				iso = isoRef.get();
				
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					if(objRes instanceof com.belejanor.switcher.bimo.genericerror.Document){
						
						com.belejanor.switcher.bimo.genericerror.Document genericError = 
								(com.belejanor.switcher.bimo.genericerror.Document)objRes;
						iso.setISO_039_ResponseCode(genericError.getTrx().getStsRsnInf().getRsn().getCode());
						iso.setISO_039p_ResponseDetail(genericError.getTrx().getStsRsnInf().getRsn().getAddtlInf().get(0));
						
						
						log.WriteOptimizeLog(genericError, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.genericerror.Document.class, true);
						
					}else if (objRes instanceof com.belejanor.switcher.bimo.pacs.camt_998_462.Document) {
						
						com.belejanor.switcher.bimo.pacs.camt_998_462.Document document =
								 (com.belejanor.switcher.bimo.pacs.camt_998_462.Document)objRes;
						iso.setISO_039_ResponseCode(document.getPrtryMsg().getValidateOTP().getStsRsnInf()
								   .getRsn().getCode());
						iso.setISO_039p_ResponseDetail(document.getPrtryMsg().getValidateOTP().getStsRsnInf()
								.getRsn().getAddtlInf().get(0));
						
						//Recuperacion del CELULAR Y CUENTA A DEBITAR
						if(iso.getISO_039_ResponseCode().equals("000")){
							
							//Cuenta
							iso.setISO_102_AccountID_1(Arrays.asList(document.getPrtryMsg().getValidateOTP().getAccountId().split("\\|")).get(3));
							//Celular (Propio o Tercero)
							iso.setISO_124_ExtendedData(document.getHeader().getOrigId().getOtherId());
							//2018-06-11 Cambio Solicitado por Leonardo Jacome (se añade numero celular dueño de la Billetera, 
							//y su cedula de identidad)
							//Cedula
							iso.setISO_120_ExtendedData(document.getPrtryMsg().getValidateOTP().getPartyId());
							iso.setISO_121_ExtendedData(document.getPrtryMsg().getValidateOTP().getPartyPhone());
							
						}
						
						log.WriteOptimizeLog(document, TypeLog.brdAut, 
								com.belejanor.switcher.bimo.pacs.camt_998_462.Document.class, true);
					}
				}else{
					
					log.WriteOptimizeLog("Error Servicio BIMO Secuencial: " + iso.getSecuencialCommandExecutor() 
					+ " -- " + iso.getISO_039p_ResponseDetail() , TypeLog.brdAut, 
					String.class, false);
				}
				
			}else{
				
				iso.setISO_039_ResponseCode(parser.getCodError());
				iso.setISO_039p_ResponseDetail(parser.getDesError());
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::@ValidacionOTP ", 
					     TypeMonitor.error, e);
		}
		
		return iso;
	}
	
	/*------METODOS LLAMADOS DINAMICAMENTE----*/
	public wIso8583 VerificarUsuarioBimoAutExe(wIso8583 iso){
		
		iso = VerificarUsuarioBimoAut(iso);
		if(iso.getISO_039_ResponseCode().equals("500"))//Codigo Habilitante 050 (NO TIENE CUENTA BIMO)
			iso.setISO_039_ResponseCode("000");
		else if (iso.getISO_039_ResponseCode().equals("000")) {
			iso.setISO_039_ResponseCode("100");
			iso.setISO_039p_ResponseDetail("USAURIO YA TIENE UNA CUENTA BIMO");
		}
		return iso;
	}
	
	public wIso8583 VerificarTelefonoCtaBimoAutExe(wIso8583 iso){
		iso = VerificarTelefonoCtaBimoAut(iso);
		if(iso.getISO_039_ResponseCode().equals("500"))//Codigo Habilitante 050 (NO TIENE CUENTA BIMO)
			iso.setISO_039_ResponseCode("000");
		else if (iso.getISO_039_ResponseCode().equals("000")) {
			iso.setISO_039_ResponseCode("100");
			iso.setISO_039p_ResponseDetail("USAURIO YA TIENE UNA CUENTA BIMO");
		}
		return iso;
	}
	
	public wIso8583 isActiveCtaBimoNoFinantialAutExe(wIso8583 iso){
		
		wIso8583 isoClone = null;
		try {
			
			isoClone = iso.cloneWiso(iso);
			isoClone.setISO_BitMap("camt.998.211.A");
			ValidateWalletBimo val = new ValidateWalletBimo();
			isoClone = val.validateBimoWallet(isoClone);
			if(isoClone.getISO_039_ResponseCode().equals("100")){
				
				int index = 0;
				index = isoClone.getISO_039p_ResponseDetail().indexOf("ACTIVO");
				if(index > 0){
					
					iso.setISO_039_ResponseCode("000");
					iso.setISO_039p_ResponseDetail("CUENTA BIMO ACTIVA");
					
				}else{
					
					iso.setISO_039_ResponseCode("100");
					iso.setISO_039p_ResponseDetail("LA CUENTA SE ENCUENTRA INACTIVA");
				}
				
			}else{
				
				iso.setISO_039_ResponseCode("308");
				iso.setISO_039p_ResponseDetail("DATOS INCONSISTENTES, O ESTADO DE LA "
						+ "BILLETERA NO ES ACTIVO");
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::isActiveCtaBimoNoFinantialAutExe ", 
				     TypeMonitor.error, e);
		}
		return iso;
	}
	
	public wIso8583 isActiveCtaBimoFinantialAutExe(wIso8583 iso){
		try {
			
			ValidateWalletBimo val = new ValidateWalletBimo();
			iso = val.validateBimoWallet(iso);
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::isActiveCtaBimoFinantialAutExe ", 
				     TypeMonitor.error, e);
		}
		return iso;
	}
	
	public wIso8583 ValidacionOTPExe(wIso8583 iso) {
		try {
			
			
			wIso8583 isoClone = iso.cloneWiso(iso);
			isoClone.setISO_041_CardAcceptorID("RATM");
			isoClone.setISO_023_CardSeq(iso.getISO_002_PAN());
			isoClone.setISO_120_ExtendedData("USD");

			isoClone = ValidacionOTP(isoClone);
			
			if(isoClone.getISO_039_ResponseCode().equals("000")){
				
				iso.setISO_102_AccountID_1(isoClone.getISO_102_AccountID_1());
			}
			
			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			iso.setWsTempAut(isoClone.getWsTempAut());
			iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::ValidacionOTPExe ", 
				     TypeMonitor.error, e);
		}
		return iso;
	}
	
	public wIso8583 ValidacionBimoWalletInternExe(wIso8583 iso) {
		try {
			
			String [] dataInfoClient = iso.getISO_044_AddRespData().split("\\|");
			
			if(dataInfoClient.length > 1){
				String movilNro = Arrays.asList(iso.getISO_044_AddRespData().split("\\|")).get(2); 
				wIso8583 isoClone = iso.cloneWiso(iso);
				isoClone.setISO_023_CardSeq(movilNro);
	
				isoClone = VerificarUsuarioBimoAut(isoClone);
				
				switch (isoClone.getISO_039_ResponseCode()) {
				case  "000":
					dataInfoClient[6] = "1";
					break;
				case "501":
					dataInfoClient[6] = "0";
					break;
				case "500":
					dataInfoClient[6] = "-1";
					break;
				default:
					break;
				}
				
				String acum044 = StringUtils.Empty();
				for (int i = 0; i < dataInfoClient.length; i++)
					acum044 += dataInfoClient[i] + "|";
				
				iso.setISO_044_AddRespData(StringUtils.trimEnd(acum044, "|"));
				iso.setWsTempAut(isoClone.getWsTempAut());
				iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error modulo BanredIsAut::ValidacionOTPExe ", 
				     TypeMonitor.error, e);
		}
		return iso;
	}
	
}
