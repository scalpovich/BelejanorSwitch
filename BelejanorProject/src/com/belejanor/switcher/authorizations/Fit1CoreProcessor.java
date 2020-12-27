package com.belejanor.switcher.authorizations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.cscoreswitch.EngineCallableProcessor;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.fit1struct.DetailFit1;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.ActionCodeTable;
import com.belejanor.switcher.memcached.Iso8583_Retrieve;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.parser.Fit1Parser;
import com.belejanor.switcher.parser.Fit1Parser.DataReturnParser;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;
import com.belejanor.switcher.storeandforward.AdminProcessStoreAndForward;
import com.belejanor.switcher.tcpclient.SocketClient;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;
import com.fitbank.common.exception.FitbankException;
import com.fitbank.middleware.jnp.Fit1Client;

public class Fit1CoreProcessor {

	private wIso8583 iso;
	private Logger log;
	
	public Fit1CoreProcessor(){
		this.log = new Logger();
	}
	public Fit1CoreProcessor(wIso8583 iso){
		this();
		this.iso = iso;
	}
	public wIso8583 getIso() {
		return iso;
	}
	public void setIso(wIso8583 iso) {
		this.iso = iso;
	}
	public Logger getLog() {
		return log;
	}
	public void setLog(Logger log) {
		this.log = log;
	}
	
	public DetailFit1 ProcessingTransactionFit1Core(String nameMethod){
		
		
		DetailFit1 ResponseDetail = null;
		
		final com.fitbank.middleware.jnp.Fit1Client fitcli = new Fit1Client(MemoryGlobal.fit1ContextType, MemoryGlobal.fit1JnpUrl, 
				MemoryGlobal.fit1JnpContextFactory, null, null, null, null);;
		
		try {
			
			DataReturnParser responseXMLwIso = null;
			if(this.iso.getISO_000_Message_Type().startsWith("12")){
				
				Class<?> instanceClass = Fit1Parser.class;																			
				Object classInstance = instanceClass.getConstructor(new Class[]{}).newInstance();					
				Method methodToInvoke = instanceClass.getMethod("parse" + nameMethod, wIso8583.class);					
				responseXMLwIso = (DataReturnParser) methodToInvoke.invoke(classInstance, this.iso);
				
			}else{
				
				if(this.iso.getWsTransactionConfig().getProccodeReverFlag() < 300) {
					
					Fit1Parser parser = new Fit1Parser();
					responseXMLwIso = parser.parseReversoFit1(this.iso);
					
				}else {
					
					/*Aqui metodo que reversa mas de una transaccion*/
					this.iso = MultiplesReversosProcesador(this.iso);
					return null;
					
				}
			}
				
			if(responseXMLwIso.getCodError().equals("111")){
				
				
				iso.setISO_124_ExtendedData(responseXMLwIso.getcPersona() + "^" + responseXMLwIso.getIdentificador() + "^"
						+ responseXMLwIso.getNombresCuenta() + "^" + responseXMLwIso.getTipoIdentificacion() + 
						"^" + responseXMLwIso.getNumCuenta() + "^" + responseXMLwIso.getTipoPersona());
				
				String tramaRq = responseXMLwIso.getData();
				log.WriteOptimizeLog(tramaRq, TypeLog.debug, String.class, false, 0);
				
				final ContainerUCI1 container = new ContainerUCI1();
				container.setDet(tramaRq);
				container.setIso(iso);
				container.setFlag(false);
				
				final CountDownLatch semaphore = new CountDownLatch(1);
				iso.getTickAut().reset();
				iso.getTickAut().start();
				Thread tUci = new Thread(new Runnable() {
					
					@Override
					public void run() {
						
						try {
							
							if(MemoryGlobal.fit1FlagConnection.equalsIgnoreCase("L")){
								container.setDetResponse(fitcli.send(tramaRq));
							     semaphore.countDown();
							}
							else {
								
								SocketClient sock = 
								new SocketClient(MemoryGlobal.fit1IpConnectionRemote, 
								         MemoryGlobal.fit1PortConnectionRemote);
								
								String dataR = sock.SendSocketClient(tramaRq);
								
								if(sock.getDetailError().startsWith("OK")){
									
									container.setDetResponse(dataR);
									
								}else{
									
									iso.setISO_039_ResponseCode("908");
									iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS SOCKET NO RESPONDE (JNP_BRIDGE) " + sock.getDetailError());
									iso.setISO_104_TranDescription("ERROR AL RECUPERAR RESPUESTA UCI, JNP_BRIDGE");
									container.setFlag(true);
								}
								semaphore.countDown();	
							}
							
							if(iso.getTickAut().isStarted())
								iso.getTickAut().stop();
							
						} catch (InterruptedException e) {
							
							if(iso.getTickAut().isStarted())
								iso.getTickAut().stop();
							iso.setISO_039_ResponseCode("909");
							iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, false));
							iso.setISO_104_TranDescription("ERROR AL RECUPERAR RESPUESTA UCI, RUNNABLE INTERRUPTED EXCEPTION");

							log.WriteOptimizeLog("Error Procesos Sec: " + iso.getISO_011_SysAuditNumber() + " ---- " + 
									iso.getISO_039p_ResponseDetail(), TypeLog.debug, String.class, false, 0);
							container.setFlag(true);
							semaphore.countDown();
							
						} catch (FitbankException e) {
							
							if(iso.getTickAut().isStarted())
								iso.getTickAut().stop();
							iso.setISO_039_ResponseCode("909");
							iso.setISO_039p_ResponseDetail("ERROR, " + GeneralUtils.ExceptionToString(null, e, false));
							iso.setISO_104_TranDescription("ERROR AL RECUPERAR RESPUESTA UCI [FITBANK EXCEPTION]");
							log.WriteOptimizeLog("Error Procesos Sec: " + iso.getISO_011_SysAuditNumber() + " ---- " + 
									iso.getISO_039p_ResponseDetail(), TypeLog.debug, String.class, false, 0);
							container.setFlag(true);
							semaphore.countDown();
									
						} catch (Exception e) {
							if(iso.getTickAut().isStarted())
								iso.getTickAut().stop();
							iso.setISO_039_ResponseCode("909");
							iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS [GENERA:], " + GeneralUtils.ExceptionToString(null, e, false));
							iso.setISO_104_TranDescription("ERROR AL RECUPERAR RESPUESTA UCI GENERAL");
							log.WriteOptimizeLog("Error Procesos Sec: " + iso.getISO_011_SysAuditNumber() + " ---- " + 
									iso.getISO_039p_ResponseDetail(), TypeLog.debug, String.class, false, 0);
							
							container.setFlag(true);
							semaphore.countDown();
						}
					}
				}); 
				tUci.start();
				
				if(!semaphore.await(this.iso.getWsTransactionConfig().
						 getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)){
					
					this.iso.setISO_039_ResponseCode("907");
					this.iso.setISO_039p_ResponseDetail("TRANSACCION EXPIRADA, TIMEOUT FIT1");
					this.iso.setISO_104_TranDescription("HA EXPIRADO EL TIEMPO DE ESERA DE FITSWITCH HACIA FIT1 PARA ESTA OPERACION");
					this.iso.setWsIso_LogStatus(9);
					log.WriteOptimizeLog("Error Procesos Sec: " + iso.getISO_011_SysAuditNumber() + " ---- " + 
								iso.getISO_039p_ResponseDetail(), TypeLog.debug, String.class, false, 0);
					this.iso.setWsISO_FlagStoreForward(true);
					
				}else{
					 
					if(!container.isFlag()){
						
						String response = container.getDetResponse();
						log.WriteOptimizeLog(response, TypeLog.debug, String.class, false, 0);
						if(response.startsWith("<FITBANK>") || response.contains("<FITBANK>")){
							ResponseDetail = (DetailFit1) SerializationObject.StringToObject(response, DetailFit1.class);
							if(ResponseDetail.getRespuesta() != null){
								
								if(ResponseDetail.getRespuesta().getCodRespuesta().equals("0")){
									
									this.iso.setISO_039_ResponseCode("000");
									this.iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
									this.iso.setISO_104_TranDescription("LA TRANSACCION HA SIDO COMPLETADA SATISFACTORIAMENTE");
									String nroMensajeResponse = StringUtils.IsNullOrEmpty(ResponseDetail.getRespuesta().getNroMensajeRespuesta())
										  ?FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYYMMddHHmmss") + "_" + iso.getISO_011_SysAuditNumber()
										  :ResponseDetail.getRespuesta().getNroMensajeRespuesta();
								    this.iso.setISO_044_AddRespData(nroMensajeResponse);
									
								}else{
									
									Thread t = new Thread(new AdminProcessStoreAndForward().
											EvaluateStoreAndForwardCodesThread(iso, ResponseDetail.getRespuesta().getCodRespuesta().trim()));
									t.start();
									
									String [] errorParse =  ParseErrorsCodeIsoToFit(ResponseDetail.getRespuesta().getCodRespuesta().trim());
									this.iso.setISO_039_ResponseCode(errorParse[0]);
									this.iso.setISO_039p_ResponseDetail(ResponseDetail.getRespuesta().getCodRespuesta().trim() + " - " + 
																		ResponseDetail.getRespuesta().getDesRespuesta().toUpperCase());
									this.iso.setISO_104_TranDescription(errorParse[1]);
									this.iso.setISO_044_AddRespData(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "YYYYMMddHHmmss") 
											+ "_" + iso.getISO_011_SysAuditNumber());
								}
							}else{
								
								this.iso.setISO_039_ResponseCode("909");
								this.iso.setISO_039p_ResponseDetail("ERROR AL PROCESAR RESPUESTA DE FIT1");
								this.iso.setISO_104_TranDescription("HA OCURRIDO UN PROBLEMA AL DESEREALIZAR EL MENSAJE DE RESPUESTA DE FIT1");
								this.iso.setWsIso_LogStatus(2);
							}
							this.iso.setWsIso_LogStatus(2);
							
						}else{

							this.iso.setISO_039_ResponseCode("908");
							this.iso.setISO_039p_ResponseDetail(response);
							this.iso.setISO_104_TranDescription("HA OCURRIDO UN PROBLEMA EN EL MENSAJE DE RESPUESTA DE FIT1");
						}
						
					}
				}
				
			}else{
				
				this.iso.setISO_039_ResponseCode(responseXMLwIso.getCodError());
				this.iso.setISO_039p_ResponseDetail(responseXMLwIso.getDesError());
			}
			
		} catch (Exception e) {
			
			if(this.iso.getTickAut().isStarted())
				this.iso.getTickAut().stop();
			this.iso.setISO_039_ResponseCode("909");
			this.iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR MODULO AUTORIZACION ", e, false));
			log.WriteLogMonitor("Error modulo Fit1CoreProcessor::ProcessingTransactionFit1Core ", TypeMonitor.error, e);
			
		}finally {
			
			this.iso.setWsTempAut((this.iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return ResponseDetail;
	}
	private String[] ParseErrorsCodeIsoToFit(String errorFit){
		String[] data = new String[]{ "308","" };
		try {
			ActionCodeTable codTable = new ActionCodeTable();
			codTable = codTable.getCodErrorFromFitCodeList(errorFit);
			if(codTable != null){
				data[0] = codTable.getAct_codIso();
				data[1] = codTable.getAct_desIso();
			}
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo FitCoreProcessor::ParseErrorsCodeIsoToFit ", TypeMonitor.error, e);
			e.printStackTrace();
		}
		return data;
	}
	
	private wIso8583 MultiplesReversosProcesador(wIso8583 iso) {
		
		EngineCallableProcessor<Iso8583> engine = null;
		String IP = "127.0.0.1";
		try {
			
			List<Iso8583> isoList = getDataIsoTrxsRetrieve(iso);
			if(isoList != null) {
				
				if(isoList.size() > 0) {
				
					iso.getTickAut().reset();
					iso.getTickAut().start();
					
					/*Si es DEBITO mando el REVERSO con HILOS*/
					if(iso.getISO_003_ProcessingCode().startsWith("01")) {
						engine = new EngineCallableProcessor<>(5);
						
						
						for (Iso8583 Iso : isoList) {
							
							Iso.setISO_000_Message_Type("1400");
							csProcess proc = new csProcess(Iso, IP);
							engine.add(proc);
						}
						List<Iso8583> listResponse = engine.goProcess();
						
						Iso8583 iso8583 = listResponse.stream()
								  .filter(p -> !p.getISO_039_ResponseCode().equals("000"))
								  .findFirst().orElseGet(() -> null);
						
						if(iso8583 == null) {
							
							iso.setISO_039_ResponseCode("000");
							iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
							
						}else {
							
							iso.setISO_039_ResponseCode(iso8583.getISO_039_ResponseCode());
							iso.setISO_039p_ResponseDetail(iso8583.getISO_039p_ResponseDetail());
						}
				    /*Cuando es credito mando SIN HILOS pues la tabla SE BLOQUEA*/	
				   }else {
					   
					   Iso8583[] isoResponsesWithOutThreads = new Iso8583[isoList.size()];
					   int j = 0;
					   for (Iso8583 Iso : isoList) {
							
							Iso.setISO_000_Message_Type("1400");
							csProcess proc = new csProcess();
							isoResponsesWithOutThreads[j] = proc.ProcessTransactionMain(Iso, IP);
							j++;
					   }
					   
					   Iso8583 iso8583 = Arrays.stream(isoResponsesWithOutThreads)
								  .filter(p -> !p.getISO_039_ResponseCode().equals("000"))
								  .findFirst().orElseGet(() -> null);
					
					   if(iso8583 == null) {
							
							iso.setISO_039_ResponseCode("000");
							iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
							
						}else {
							
							iso.setISO_039_ResponseCode(iso8583.getISO_039_ResponseCode());
							iso.setISO_039p_ResponseDetail(iso8583.getISO_039p_ResponseDetail());
							
							/*Pendiente hacer algo si no contesta*/
						}
				   }
					if(iso.getTickAut().isStarted())
						iso.getTickAut().stop();
					
				}else {
					
					iso.setISO_039_ResponseCode("601");
					iso.setISO_039p_ResponseDetail("NO EXISTE TRANSACCION A REVERSAR");
				}
				
			}else {
				
				iso.setISO_039_ResponseCode("100");
				iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, NO SE PUEDE REVERSAR LA TRANSACCION");
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, NO SE PUEDE REVERSAR LA TRANSACCION");
			log.WriteLogMonitor("Error modulo FitCoreProcessor::MultiplesReversosProcesador ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
			iso.setWsIso_LogStatus(2);
		}
		return iso;
	}
	
	private List<Iso8583> getDataIsoTrxsRetrieve(wIso8583 iso){
	
		List<Iso8583> isoList = null;
		List<Iso8583_Retrieve> isoListRetrieve = null;
		try {
			
			String cabeceraQuery = "SELECT \r\n" + 
					"					wiso_000_message_type as ISO_000_Message_Type, wiso_bitmap as ISO_BitMap, wiso_002_pan as ISO_002_PAN,\r\n" + 
					"                   wiso_003_processingcode as ISO_003_ProcessingCode, wiso_004_amounttransaction as ISO_004_AmountTransaction, \r\n" + 
					"                   wiso_006_billamount as ISO_006_BillAmount, wiso_007_transdatetime as ISO_007_TransDatetime, \r\n" + 
					"					wiso_008_billfeeamount as ISO_008_BillFeeAmount, wiso_011_sysauditnumber as ISO_011_SysAuditNumber, \r\n" + 
					"                   wiso_012_localdatetime as ISO_012_LocalDatetime, wiso_013_localdate as ISO_013_LocalDate, \r\n" + 
					"                   wiso_015_settlementdatel as ISO_015_SettlementDatel, wiso_018_merchanttype as ISO_018_MerchantType, \r\n" + 
					"					wiso_019_acqcountrycode as ISO_019_AcqCountryCode, wiso_022_posentrymode as ISO_022_PosEntryMode, \r\n" + 
					"                   wiso_023_cardseq as ISO_023_CardSeq, wiso_024_networkid as ISO_024_NetworkId, \r\n" + 
					"                   wiso_028_tranfeeamount as ISO_028_TranFeeAmount,wiso_029_settlementfee as ISO_029_SettlementFee, \r\n" + 
					"					wiso_030_procfee as ISO_030_ProcFee,wiso_032_acqinsid as ISO_032_ACQInsID,\r\n" + 
					"                   wiso_033_fwdinsid as ISO_033_FWDInsID,wiso_034_panext as ISO_034_PANExt,\r\n" + 
					"                   wiso_035_track2 as ISO_035_Track2,wiso_036_track3 as ISO_036_Track3,\r\n" + 
					"                   wiso_037_retrievalreferencenro as \"ISO_037_RetrievalReference\",\r\n" + 
					"                   wiso_038_autorizationnumber as ISO_038_AutorizationNumber, wiso_039_responsecode as ISO_039_ResponseCode,\r\n" + 
					"                   wiso_039p_responsedetail as ISO_039p_ResponseDetail,wiso_041_cardacceptorid as ISO_041_CardAcceptorID, \r\n" + 
					"					wiso_042_card_acc_id_code as ISO_042_Card_Acc_ID_Code, wiso_043_cardacceptorloc as ISO_043_CardAcceptorLoc, \r\n" + 
					"                   wiso_044_addrespdata as ISO_044_AddRespData, wiso_049_trancurrcode as ISO_049_TranCurrCode,\r\n" + 
					"                   wiso_051_cardcurrcode as ISO_051_CardCurrCode,wiso_052_pinblock as ISO_052_PinBlock, \r\n" + 
					"					wiso_054_aditionalamounts as ISO_054_AditionalAmounts,wiso_055_emv as ISO_055_EMV,\r\n" + 
					"                   wiso_090_originaldata as ISO_090_OriginalData,wiso_102_accountid_1 as ISO_102_AccountID_1, \r\n" + 
					"					wiso_103_accountid_2 as ISO_103_AccountID_2, wiso_104_trandescription as ISO_104_TranDescription,\r\n" + 
					"                   wiso_114_extendeddata as ISO_114_ExtendedData, wiso_115_extendeddata as ISO_115_ExtendedData,\r\n" + 
					"                   wiso_120_extendeddata as ISO_120_ExtendedData,wiso_121_extendeddata as ISO_121_ExtendedData,\r\n" + 
					"                   wiso_122_extendeddata as ISO_122_ExtendedData,wiso_123_extendeddata as ISO_123_ExtendedData,\r\n" + 
					"                   wiso_124_extendeddata as ISO_124_ExtendedData\r\n" + 
					"					FROM ISO8583 ";
			
			int tipoReverso = iso.getWsTransactionConfig().getProccodeReverFlag(); 
			String queryTotal = StringUtils.Empty();
			switch (tipoReverso) {
			case 300:
				isoList = new ArrayList<>();
				
				queryTotal = cabeceraQuery + "where wiso_037_retrievalreferencenro = '"+ 
								 iso.getISO_037_RetrievalReferenceNumber() +"'\r\n" + 
						         "and wiso_102_accountid_1 = '"+ iso.getISO_102_AccountID_1() +"'"
						         + " and wiso_000_message_type ='1200'";	
				
				isoListRetrieve = new ArrayList<>();
				
				DataSetMemoryLoader<Iso8583_Retrieve> loader = 
			    new DataSetMemoryLoader<Iso8583_Retrieve>
				(MemoryGlobal.conn, Iso8583_Retrieve.class, queryTotal);
				isoListRetrieve = loader.LoadDataClass();	
				
				Iso8583 iso8583_ = null;
				if(isoListRetrieve != null) {
					for (Iso8583_Retrieve Iso : isoListRetrieve) {
						
						Iso.setISO_000_Message_Type("1400");
						System.out.println("000: " + Iso.getISO_000_Message_Type() + " 003: " + Iso.getISO_003_ProcessingCode() + " 004:" +
							Iso.getISO_004_AmountTransaction() + "  011: " + Iso.getISO_011_SysAuditNumber() + "  012:  " 
								+ Iso.getISO_012_LocalDatetime());
						iso8583_ = new Iso8583(Iso);
						isoList.add(iso8583_);
					}
				}
				
				break;

			default:
				break;
			}
			
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo FitCoreProcessor::MultiplesReversosProcesador ", TypeMonitor.error, e);
		}
		return isoList;
	}
	
}

class ContainerUCI1{
	
	private String det;
	private String detResponse;
	private wIso8583 iso;
	private boolean flag;
	
	public String getDet() {
		return det;
	}
	public void setDet(String det) {
		this.det = det;
	}
	public wIso8583 getIso() {
		return iso;
	}
	public void setIso(wIso8583 iso) {
		this.iso = iso;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public String getDetResponse() {
		return detResponse;
	}
	public void setDetResponse(String detResponse) {
		this.detResponse = detResponse;
	}
}

