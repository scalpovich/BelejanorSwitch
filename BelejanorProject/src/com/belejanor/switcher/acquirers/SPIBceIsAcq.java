package com.belejanor.switcher.acquirers;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.belejanor.switcher.authorizations.FitIsAut;
import com.belejanor.switcher.authorizations.SPIBceIsAut;
import com.belejanor.switcher.cscoreswitch.EngineCallableProcessor;
import com.belejanor.switcher.cscoreswitch.EngineTaskVoidProcessor;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.TransactionConfiguration;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.parser.BceSPIParser;
import com.belejanor.switcher.snp.spi.SnpSPIOrdenante;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.sqlservices.IsoSqlMaintenance;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.SerializationExecutorString;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;

public class SPIBceIsAcq {
	
	private Logger log;
	private wIso8583 iso;
	
	public SPIBceIsAcq(){
		
		log = new Logger();
	}
	
	public wIso8583 getIso() {
		return iso;
	}

	public void setIso(wIso8583 iso) {
		this.iso = iso;
	}

	/*METODO QUE RECIBE LAS TRANSACCIONES ASINCRONAS DE BCE A PARTIR DE LA TRANSACCION 880056*/
	public wIso8583 ReturnDateForConfirmationAsync(wIso8583 iso){
		
		try {
			log.WriteLogMonitor("**** Entro Metodo ReturnDateForConfirmationAsync Iso090: " + iso.getISO_090_OriginalData() , TypeMonitor.monitor, null);
			iso.getTickAut().reset();
			iso.getTickAut().start();
			iso.setISO_013_LocalDate(new Date());
			iso.setWsIso_LogStatus(2);
			if(iso.getISO_090_OriginalData().equals("RJCT")) {
				
				iso.setISO_039_ResponseCode("100");
				iso.setISO_039p_ResponseDetail("SE RECIBE " + iso.getISO_023_CardSeq() +" TRANSACCION(ES) POR PARTE DEL BCE, CON ERROR. REVISAR LOGS. SE PROCESA LAS TRXS. ");
			}else {
			
				iso.setISO_039_ResponseCode("000");
				iso.setISO_039p_ResponseDetail("SE RECIBE " + iso.getISO_023_CardSeq() + " TRANSACCION(ES) EXITOSA(S) DEL BCE");
			}
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					//processResponsesFromBCE(iso);
					ProcessParallelReceiveResponsesFromBCE(iso);
				}
			});
			t.start();
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::ReturnDateForConfirmationAsync ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
    public wIso8583 ReturnRCVDForConfirmationReversoAsyc(wIso8583 iso){
		
		try {
			iso.getTickAut().reset();
			iso.getTickAut().start();
			iso.setISO_013_LocalDate(new Date());
			iso.setWsIso_LogStatus(2);
			/*if(iso.getISO_090_OriginalData().equals("RJCT")) {
				
				iso.setISO_039_ResponseCode("100");
				iso.setISO_039p_ResponseDetail("SE RECIBE " + iso.getISO_023_CardSeq() +" TRANSACCION(ES) POR PARTE DEL BCE, PARA REVERSO ");
			}else {*/
			
				iso.setISO_039_ResponseCode("000");
				iso.setISO_039p_ResponseDetail("SE RECIBE " + iso.getISO_023_CardSeq() + " TRANSACCION(ES) PARA *** REVERSAR *** POR PARTE DEL BCE");
			//}
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					ProcessParallelReceiveResponsesFromBCE(iso);
				}
			});
			t.start();
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::ReturnDateForConfirmationAsync ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
	@SuppressWarnings("unused")
	private void processResponsesFromBCE(wIso8583 iso){
		
		DocumentReverso rev = null;
		DocumentRespuesta doc = null;
		BceSPIParser parser = null;
		List<SnpSPIOrdenante> listSpi = new CopyOnWriteArrayList<>(); 
		IsoRetrievalTransaction sql = null;	
		final ContainerSPIOrd container = new ContainerSPIOrd();
		
		try {
			parser = new BceSPIParser();
			
			doc = new DocumentRespuesta();
			rev = new DocumentReverso();
			String trama = iso.getISO_115_ExtendedData();
			
			if(iso.getISO_055_EMV().equals("REVERSO")) {
			
				doc = (DocumentRespuesta) SerializationObject.StringToObject(trama, DocumentRespuesta.class);
				listSpi = parser.parseRespuestasSNP_SPI_BCE_Async(doc);
				
			}else {
				
				rev = (DocumentReverso) SerializationObject.StringToObject(trama, DocumentReverso.class);
				listSpi = parser.parseReversosSNP_SPI_BCE_Async(rev);
			}
			
			//Aqui el ExecutorService
			for (SnpSPIOrdenante snpLst : listSpi) {
				
				sql = new IsoRetrievalTransaction();
				SnpSPIOrdenante snpQuery = (SnpSPIOrdenante) snpLst.clone();
				snpQuery = sql.RetrieveTrxSPI_Ord(snpQuery);
				log.WriteLogMonitor("**** CONFIRMACIONES ERROR: " + snpQuery.getError_code_prop(), TypeMonitor.monitor, null);
				if(snpQuery.getError_code_prop().equals("000")) {
					
					final String isoMessage = snpQuery.getIso_message();
					container.setMessageIso(isoMessage);
					container.setSnpClase(snpQuery);
					log.WriteLogMonitor("**** Mensaje Confirmacion " + snpLst.getStatus_bce(), TypeMonitor.monitor, null);
					switch (snpLst.getStatus_bce()) {
					
					case "RVRS":
					case "RJCT":
						log.WriteLogMonitor("**** Entro Reverso por RJCT " + snpLst.getStatus_bce(), TypeMonitor.monitor, null);
						/*Reverso de Transaccion*/
						Thread t = new Thread(new Runnable() {
							
							@Override
							public void run() {
								
								ReverseTransactionForResponseBCE(container, snpLst);
							}
						});
						
						t.start();
						break;
						
					case "ACCP"://Ultima Instancia
					case "ACSP"://Aceptada minorista
					case "ACTC"://Validada
					case "ACSC"://Aceptada mayorista
					case "ACWC"://Aceptada con fecha posterior (programada, para lo cual se debera enviar el tag IntrBkSttlmDt)
						log.WriteLogMonitor("**** Entro por RESPUESTA: " + snpLst.getStatus_bce(), TypeMonitor.monitor, null);
						
						
						Thread x = new Thread(new Runnable() {
							public void run() {
							
								UpdateTrxSpiOrdenante(container, snpLst);
							}
						});
						x.start();
						
						break;
	
					default:
						break;
					}
				}
			}
			
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::processResponsesFromBCE ", TypeMonitor.error, e);
		}
	}
	
	private void ProcessParallelReceiveResponsesFromBCE(wIso8583 iso) {
		
		log.WriteLogMonitor("**** Entra al Metodo ProcessParallelReceiveResponsesFromBCE " , TypeMonitor.monitor, null);
		ProcessorResponsesFromBCE processor = null;
		DocumentRespuesta doc = null;
		DocumentReverso rev = null;
		BceSPIParser parser = null;
		List<SnpSPIOrdenante> listSpi = new CopyOnWriteArrayList<>(); 
		try {
			
			parser = new BceSPIParser();
			doc = new DocumentRespuesta();
			rev = new DocumentReverso();
			String trama = iso.getISO_115_ExtendedData();
			
			if(iso.getISO_055_EMV().equals("REVERSO")) {
				
				rev = (DocumentReverso) SerializationObject.StringToObject(trama, DocumentReverso.class);
				listSpi = parser.parseReversosSNP_SPI_BCE_Async(rev);
				
			}else {
				
				doc = (DocumentRespuesta) SerializationObject.StringToObject(trama, DocumentRespuesta.class);
				listSpi = parser.parseRespuestasSNP_SPI_BCE_Async(doc);
			}
			
			EngineTaskVoidProcessor tasker = new EngineTaskVoidProcessor(MemoryGlobal.UrlNumberThreadsExecutorSPI);
			for (SnpSPIOrdenante snp : listSpi) {
				
				processor = new ProcessorResponsesFromBCE(snp);
				tasker.add(processor);
			}
			tasker.go();
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::processResponsesFromBCE ", TypeMonitor.error, e);
		}
	}
	
	protected void ReverseTransactionForResponseBCE(ContainerSPIOrd container, SnpSPIOrdenante snpres) {
		
		csProcess processor = null;
		Iso8583 iso8583 = null;
		Date fechaActual = new Date();
		try {
			log.WriteLogMonitor("**** ^^^^ YA EN METODO REVERSO ^^^^ ", TypeMonitor.monitor, null);
			iso8583 = (Iso8583) SerializationObject.StringToObject(container.getMessageIso()
					.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", StringUtils.Empty()),
					Iso8583.class);
			/*Cambio a Reverso y fechas*/
			iso8583.setISO_000_Message_Type("1400");
			@SuppressWarnings("deprecation")
			int now = fechaActual.getDay();
			@SuppressWarnings("deprecation")
			int a = iso8583.getISO_007_TransDatetime().getDay();
			@SuppressWarnings("deprecation")
			int b = iso8583.getISO_012_LocalDatetime().getDay();
			
			if(a < now) 	
				iso8583.setISO_007_TransDatetime(new Date());
			if(b < now)
				iso8583.setISO_012_LocalDatetime(new Date());
			
			/*Pongo el detalle del error en la trama a reversar*/
			iso8583.setISO_124_ExtendedData(snpres.getCod_error_auth() + " - " + snpres.getDes_error_auth());
			/*Pongo el mensaje de respuesta del BCE*/
			iso8583.setISO_037_RetrievalReferenceNumber(snpres.getMsgid_last_be());
			/*Pongo la Fecha y hora de Respuesta*/
			iso8583.setISO_013_LocalDate(snpres.getCredttm());
			processor = new csProcess();
			iso8583 = processor.ProcessTransactionMain(iso8583, "127.0.0.1");
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::ReverseTransactionForResponseBCE ", TypeMonitor.error, e);
		}
	}
	
	protected void UpdateTrxSpiOrdenante(ContainerSPIOrd container, SnpSPIOrdenante snpres) {
		
		SnpSPIOrdenante snpOriginal = null;
		IsoSqlMaintenance sql = null;
		
		try {
			snpOriginal = new SnpSPIOrdenante();
			snpOriginal = container.getSnpClase();
			
			/*Igualo los campos de la respuesta a la original*/
			snpOriginal.setStatus_bce(snpres.getStatus_bce());
			snpOriginal.setDate_last_bce(snpres.getCredttm());
			if(snpOriginal.isOrdenante())
				snpOriginal.setMsgid_last_be(snpres.getMsgid_last_be());
			else {
			
				snpOriginal.setMsgid_last_be(snpres.getMsgid());
				snpOriginal.setMsgid(snpres.getMsgid_last_be());
			}
				
				
			switch (snpres.getStatus_bce()) {
			
			//Aqui talvez poner el motivo de un REVERSO TECNICO ojo
			case "ACCP"://Ultima Instancia
				snpOriginal.setStatus_reason_bce("CONFIRMACION TOTAL TRANSACCION RECEPTORA");
				break;
			case "ACTC":
				snpOriginal.setStatus_reason_bce("VALIDACION OK RECIBIDA POR EL BCE");
				break;
			case "ACSP":
				snpOriginal.setStatus_reason_bce("CONFIRMACION EXITOSA RECIBIDA POR EL BCE MINORISTA ORDENANTE");
				break;
			case "ACWC":
				snpOriginal.setStatus_reason_bce("CONFIRMACION EXITOSA RECIBIDA POR EL BCE PROGRAMADA POSTERIOR");
				break;
			case "ACSC"://Aceptada con fecha posterior (programada, para lo cual se debera enviar el tag IntrBkSttlmDt)
				snpOriginal.setStatus_reason_bce("CONFIRMACION EXITOSA RECIBIDA POR EL BCE MAYORISTA ORDENANTE");
				break;
			default:
				break;
			}
			
			sql = new IsoSqlMaintenance();
			int responseSQL = sql.UpdateSPIOrdenante(snpOriginal);
			log.WriteLogMonitor("UPDATE SNP " + snpOriginal.getMsgid() + " SQLCode: " + responseSQL, TypeMonitor.monitor, null);
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::UpdateTrxSpiOrdenante ", TypeMonitor.error, e);
		}
	}
	
	//Hilos Metodo que 1ro se llama desde BRIDGE
	public void processTrxSpiReceptor(List<Iso8583> isoList) {
		
		ContainerIsoList cont = new ContainerIsoList();
		Date tiempoInicio = new Date();
		
		try {
			
			Thread tmay = new Thread(() -> {
			   
				cont.setListMayorista(isoList.stream()
						.filter(p -> p.getISO_BitMap().equalsIgnoreCase("HIGH"))
						.peek(Objects::requireNonNull)
						.collect(Collectors.toList()));
			});
			
			Thread tmin = new Thread(() -> {
				   
				cont.setListMinorista(isoList.stream()
						.filter(p -> p.getISO_BitMap().equalsIgnoreCase("LOW"))
						.peek(Objects::requireNonNull)
						.collect(Collectors.toList()));
			});
			
			tmay.start();
			tmin.start();
			tmay.join();
			tmin.join();
			
			List<Iso8583> isoListMayoristas = cont.getListMayorista();
			List<Iso8583> isoListMinoristas = cont.getListMinorista();
			
		    log.WriteLogMonitor("============= INICIA SERIALIZACION: "+ FormatUtils.DateToString(new Date(), "YYYY-MM-DD-HH:mm:ss") +" ===============", TypeMonitor.monitor, null);
			
		    Thread SerialMayo = new Thread(() -> {
				
				SerializationIsos(isoListMayoristas);
			});
		    
		    Thread SerialMino = new Thread(() -> {
				
				SerializationIsos(isoListMinoristas);
			});
		    
		    SerialMayo.start();
		    SerialMino.start();
		    SerialMayo.join();
		    SerialMino.join();
		    		
		    log.WriteLogMonitor("============= TERMINA SERIALIZACION: "+ FormatUtils.DateToString(new Date(), "YYYY-MM-DD-HH:mm:ss") +" ===============", TypeMonitor.monitor, null);
		    
		    Ref<ContainerIsoList> containerMayoristas = new Ref<>();
		    Ref<ContainerIsoList> containerMinoristas = new Ref<>();
		    
			Thread tprocMay = new Thread(() -> {
				
				ReceptorProcessingTransactions(isoListMayoristas, 0, containerMayoristas);
			});
			
			Thread tprocMin = new Thread(() -> {
				
				ReceptorProcessingTransactions(isoListMinoristas, 1, containerMinoristas);
			});
			
			if(isoListMayoristas != null && isoListMayoristas.size() > 0) {
				
				tprocMay.start();
				tprocMay.join();
			}
			if(isoListMinoristas != null && isoListMinoristas.size() > 0) {
				
				tprocMin.start();
				tprocMin.join();
			}
			
			/*List<Iso8583> soListResponseMayoristas = containerMayoristas.get().getListMayorista();
			List<Iso8583> soListResponseMinoristas = containerMinoristas.get().getListMinorista();
			
			cont.setListMayorista(soListResponseMayoristas);
			cont.setListMinorista(soListResponseMinoristas);*/
			
			List<Iso8583> soListResponseMayoristas = new CopyOnWriteArrayList<>();
			if(containerMayoristas.get() != null) {
			
				if(containerMayoristas.get().getListMayorista() != null) {
					soListResponseMayoristas = containerMayoristas.get().getListMayorista();
					cont.setListMayorista(soListResponseMayoristas);
				}
			}
			
			List<Iso8583> soListResponseMinoristas = new CopyOnWriteArrayList<>();
			if(containerMinoristas.get() != null) {
				
				if(containerMinoristas.get().getListMinorista() != null) {
					soListResponseMinoristas = containerMinoristas.get().getListMinorista();
					cont.setListMinorista(soListResponseMinoristas);
				}
			}
			
			//System.out.println("Ej. Mayorista: " + soListResponseMayoristas.get(0).getISO_039_ResponseCode() + " ---- " + soListResponseMayoristas.get(0).getISO_039p_ResponseDetail());
			//System.out.println("Ej. Minorista: " + soListResponseMinoristas.get(0).getISO_039_ResponseCode() + " ---- " + soListResponseMinoristas.get(0).getISO_039p_ResponseDetail());
			
			log.WriteLogMonitor("\n**********************************************************************\n"
					          + "============= TERMINO PROCESAMIENTO SPI RECEPTOR ===============\n"
					          + "============= TRX. RECIBIDAS: "+ isoList.size() + " ===============================\n"
					          + "============= TIEMPO INICIO : "+ FormatUtils.DateToString(tiempoInicio, "YYYY-MM-DD-HH:mm:ss") +" ===============\n"
					          + "============= TIEMPO FINAL  : "+ FormatUtils.DateToString(new Date(), "YYYY-MM-DD-HH:mm:ss") +" ===============\n"
					          + "**********************************************************************\n"
					, TypeMonitor.monitor, null);
			
			Ref<ContainerIsoList> containerResponses = new Ref<ContainerIsoList>(cont);
			PrepareTransactionConfirmToBCE(containerResponses);
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::processTrxSpiReceptor ", TypeMonitor.error, e);
		}
	}
	
	private void PrepareTransactionConfirmToBCE(final Ref<ContainerIsoList> cont) {
		
		ContainerIsoListResponses contResponses = new ContainerIsoListResponses();
		try {
			
			Thread tmayOk = new Thread(() -> {
			
				contResponses.setListMayoristaOk(cont.get().getListMayorista().stream()
							  .filter(p -> p.getISO_039_ResponseCode().equals("000"))
						      .peek(Objects::requireNonNull)
							  .collect(Collectors.toList()));
			});
			Thread tmayErr = new Thread(() -> {
			contResponses.setListMayoristaErr(cont.get().getListMayorista().stream()
							  .filter(p -> (!p.getISO_039_ResponseCode().equals("000")))
						      .peek(Objects::requireNonNull)
							  .collect(Collectors.toList()));
			});
			Thread tminOk = new Thread(() -> {
			contResponses.setListMinoristaOK(cont.get().getListMinorista().stream()
							  .filter(p -> p.getISO_039_ResponseCode().equals("000"))
						      .peek(Objects::requireNonNull)
							  .collect(Collectors.toList()));
			});
			Thread tminErr = new Thread(() -> {
			contResponses.setListMinoristaERR(cont.get().getListMinorista().stream()
							  .filter(p -> !(p.getISO_039_ResponseCode().equals("000")))
						      .peek(Objects::requireNonNull)
							  .collect(Collectors.toList()));
			});
			
			tmayOk.start(); tmayErr.start(); tminOk.start(); tminErr.start();
			tmayOk.join(); tmayErr.join(); tminOk.join(); tminErr.join();
			
			SendTransactionsResponsesToBCE(new Ref<ContainerIsoListResponses>(contResponses));
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::SendTransactionConfirmToBCE ", TypeMonitor.error, e);
		}
	}
	
	/*METODO QUE ENVIA LAS TRANSACCIONES AL BCE (NOTIFICACIONES CUANDO ES RECEPTOR LA INSTITUCION FINANCIERA)*/
	
	private void SendTransactionsResponsesToBCE(final Ref<ContainerIsoListResponses> cont) {
		
		Iso8583 isoCentinelaMayOk = null; wIso8583 wIsoCentinelaMayOk = null;
		Iso8583 isoCentinelaMayErr = null; wIso8583 wIsoCentinelaMayErr = null;
		Iso8583 isoCentinelaMinOk = null; wIso8583 wIsoCentinelaMinOk = null;
		Iso8583 isoCentinelaMinErr = null; wIso8583 wIsoCentinelaMinErr = null;
		SPIBceIsAut autorizator = null;
		csProcess processor = null;
		try {
			
			isoCentinelaMayOk = new Iso8583();
			isoCentinelaMayOk.setISO_000_Message_Type("1200");
			isoCentinelaMayOk.setISO_003_ProcessingCode("880056");
			isoCentinelaMayOk.setISO_018_MerchantType("0005");
			isoCentinelaMayOk.setISO_024_NetworkId("555777");
			isoCentinelaMayOk.setISO_032_ACQInsID(MemoryGlobal.UrlSpiCodeSwitch_BCE);
			isoCentinelaMayOk.setISO_033_FWDInsID(MemoryGlobal.UrlSpiCodeEfi_BCE);
			
			
			if(cont.get().getListMayoristaOk().size() > 0) {
				
				
				isoCentinelaMayOk.setISO_BitMap("HIGH");
				isoCentinelaMayOk.setISO_011_SysAuditNumber(cont.get().getListMayoristaOk().get(0)
						                                   .getISO_037_RetrievalReferenceNumber());
				isoCentinelaMayOk.setISO_041_CardAcceptorID("SPI MAYORISTAS OK");
				isoCentinelaMayOk.setISO_055_EMV("ACSC");
				isoCentinelaMayOk.setISO_006_BillAmount(cont.get().getListMayoristaOk().get(0)
														.getISO_006_BillAmount());
				isoCentinelaMayOk.setISO_002_PAN("TRX.:" + cont.get().getListMayoristaOk().size());
				isoCentinelaMayOk.setISO_008_BillFeeAmount(cont.get().getListMayoristaOk().get(0)
														   .getISO_008_BillFeeAmount());
				
				wIsoCentinelaMayOk = new wIso8583(isoCentinelaMayOk, "127.0.0.1");
				
				autorizator = new SPIBceIsAut();
				wIsoCentinelaMayOk = autorizator.SendRequestSPINotifications(wIsoCentinelaMayOk, cont.get().getListMayoristaOk());
				isoCentinelaMayOk = new Iso8583(wIsoCentinelaMayOk);
				//Listo para ejecutar la Trx csProcess
				processor = new csProcess();
			    isoCentinelaMayOk = processor.ProcessTransactionMain(isoCentinelaMayOk, "127.0.0.1"); 
			}
			
			if(cont.get().getListMayoristaErr().size() > 0) {
				
				
				isoCentinelaMayErr = (Iso8583) isoCentinelaMayOk.clone();
				isoCentinelaMayErr.setISO_BitMap("HIGH");
				isoCentinelaMayErr.setISO_000_Message_Type("1200");
				isoCentinelaMayErr.setISO_002_PAN("TRX.: " + cont.get().getListMayoristaErr().size());
				isoCentinelaMayErr.setISO_012_LocalDatetime(new Date());
				isoCentinelaMayErr.setISO_011_SysAuditNumber(cont.get().getListMayoristaErr().get(0)
                        .getISO_037_RetrievalReferenceNumber());
				isoCentinelaMayErr.setISO_041_CardAcceptorID("SPI MAYORISTAS ERROR");
				isoCentinelaMayErr.setISO_055_EMV("RJCT");
				isoCentinelaMayErr.setISO_006_BillAmount(cont.get().getListMayoristaErr().get(0)
						.getISO_006_BillAmount());
				isoCentinelaMayErr.setISO_008_BillFeeAmount(cont.get().getListMayoristaErr().get(0)
						   .getISO_008_BillFeeAmount());
				wIsoCentinelaMayErr = new wIso8583(isoCentinelaMayErr, "127.0.0.1");
				
				autorizator = new SPIBceIsAut();
				wIsoCentinelaMayErr = autorizator.SendRequestSPINotifications(wIsoCentinelaMayErr, cont.get().getListMayoristaErr());
				isoCentinelaMayErr = new Iso8583(wIsoCentinelaMayErr);
				//Listo para ejecutar la Trx csProcess
				processor = new csProcess();
				isoCentinelaMayErr = processor.ProcessTransactionMain(isoCentinelaMayErr, "127.0.0.1"); 
			}
			
			if(cont.get().getListMinoristaOK().size() > 0) {
				
				
				isoCentinelaMinOk = (Iso8583) isoCentinelaMayOk.clone();
				isoCentinelaMinOk.setISO_BitMap("LOW");
				isoCentinelaMinOk.setISO_000_Message_Type("1200");
				isoCentinelaMinOk.setISO_002_PAN("TRX.: " + cont.get().getListMinoristaOK().size());
				isoCentinelaMinOk.setISO_012_LocalDatetime(new Date());
				isoCentinelaMinOk.setISO_011_SysAuditNumber(cont.get().getListMinoristaOK().get(0)
                        .getISO_037_RetrievalReferenceNumber());
				isoCentinelaMinOk.setISO_041_CardAcceptorID("SPI MINORISTAS OK");
				isoCentinelaMinOk.setISO_055_EMV("ACSP");
				isoCentinelaMinOk.setISO_006_BillAmount(cont.get().getListMinoristaOK().get(0)
						.getISO_006_BillAmount());
				isoCentinelaMinOk.setISO_008_BillFeeAmount(cont.get().getListMinoristaOK().get(0)
						   .getISO_008_BillFeeAmount());
				
				wIsoCentinelaMinOk = new wIso8583(isoCentinelaMinOk, "127.0.0.1");
				
				autorizator = new SPIBceIsAut();
				wIsoCentinelaMinOk = autorizator.SendRequestSPINotifications(wIsoCentinelaMinOk, cont.get().getListMinoristaOK());
				isoCentinelaMinOk = new Iso8583(wIsoCentinelaMinOk);
				//Listo para ejecutar la Trx csProcess
				processor = new csProcess();
				isoCentinelaMinOk = processor.ProcessTransactionMain(isoCentinelaMinOk, "127.0.0.1"); 
			}
			
			if(cont.get().getListMinoristaERR().size() > 0) {
				
				isoCentinelaMinErr = (Iso8583) isoCentinelaMayOk.clone();
				isoCentinelaMinErr.setISO_BitMap("LOW");
				isoCentinelaMinErr.setISO_000_Message_Type("1200");
				isoCentinelaMinErr.setISO_002_PAN("TRX.: " + cont.get().getListMinoristaERR().size());
				isoCentinelaMinErr.setISO_012_LocalDatetime(new Date());
				isoCentinelaMinErr.setISO_011_SysAuditNumber(cont.get().getListMinoristaERR().get(0)
                        .getISO_037_RetrievalReferenceNumber());
				isoCentinelaMinErr.setISO_041_CardAcceptorID("SPI MINORISTAS ERROR");
				isoCentinelaMinErr.setISO_055_EMV("RJCT");
				isoCentinelaMinErr.setISO_006_BillAmount(cont.get().getListMinoristaERR().get(0)
						.getISO_006_BillAmount());
				isoCentinelaMinErr.setISO_008_BillFeeAmount(cont.get().getListMinoristaERR().get(0)
						   .getISO_008_BillFeeAmount());
				
				wIsoCentinelaMinErr = new wIso8583(isoCentinelaMinErr, "127.0.0.1");
				
				autorizator = new SPIBceIsAut();
				wIsoCentinelaMinErr = autorizator.SendRequestSPINotifications(wIsoCentinelaMinErr, cont.get().getListMinoristaERR());
				isoCentinelaMinErr = new Iso8583(wIsoCentinelaMinErr);
				//Listo para ejecutar la Trx csProcess
				processor = new csProcess();
				isoCentinelaMinErr = processor.ProcessTransactionMain(isoCentinelaMinErr, "127.0.0.1"); 
			}
			
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::SendTransactionsResponsesToBCE ", TypeMonitor.error, e);
		}
	}
	
	/*METODO PRINCIPAL QUE EJECUTA TODO SPI RECEPTOR REFERENCIA AQUI*/ 
	private void ReceptorProcessingTransactions(List<Iso8583> isoList, int flag, Ref<ContainerIsoList> refContainer) {
		
		try {
			
			ContainerIsoList container = new ContainerIsoList();
			
			String IP = StringUtils.Empty();
			EngineCallableProcessor<Iso8583> engine = new EngineCallableProcessor<>(MemoryGlobal.UrlNumberThreadsExecutorSPI);
			for (Iso8583 iso : isoList) {
				IP = iso.getISO_036_Track3();
				csProcess proc = new csProcess(iso, IP);
				engine.add(proc);
			}
			List<Iso8583> listIsoProc = engine.goProcess();
			
			if(flag == 0)//Mayoristas
				container.setListMayorista(listIsoProc);
			else
				container.setListMinorista(listIsoProc);
			refContainer.set(container);
			
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::ReceptorProcessingTransactions ", TypeMonitor.error, e);
		}
	}

	private void SerializationIsos(List<Iso8583> isoList) {
				
		try {
						
			EngineCallableProcessor<String> engine = new EngineCallableProcessor<>(MemoryGlobal.UrlNumberThreadsExecutorSPI);
			for (Iso8583 iso : isoList) {
				
				SerializationExecutorString serial = new SerializationExecutorString(Iso8583.class, iso);
				engine.add(serial);
			}
			
			List<String> listIsoProc = engine.goProcess();
			
			
			for (Iso8583 obj : isoList) {
				
				String a = listIsoProc.stream().
						  filter(p -> p.contains(obj.getISO_011_SysAuditNumber()))
						 .findFirst().orElseGet(()-> null);
				
				obj.setISO_115_ExtendedData(a);
			}
				
			
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::SerializationIsos ", TypeMonitor.error, e);
		}
	}
	
	public wIso8583 CreditoSpiAccount(wIso8583 iso) {
		
		FitIsAut aut = null;
		wIso8583 isoCredit = null;
		try {
			
			/*Thread.sleep(790);
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");*/
			
			//ExecuteDebitCreditFit1
			aut = new FitIsAut();
			isoCredit =  (wIso8583) iso.clone();
			isoCredit.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			isoCredit.setISO_102_AccountID_1(iso.getISO_103_AccountID_2());
			isoCredit.setISO_123_ExtendedData("ACREDITACION SPI PRUEBAS");
			
			isoCredit = aut.ExecuteDebitCreditFit1(isoCredit);
			
			iso.setISO_039_ResponseCode(isoCredit.getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(isoCredit.getISO_039p_ResponseDetail());
			iso.setWsIso_LogStatus(isoCredit.getWsIso_LogStatus());
			iso.setWsTempAut(isoCredit.getWsTempAut());
			
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::CreditoSpiAccount ", TypeMonitor.error, e);
		}
		return iso;
	}
	
    public wIso8583 CreditoSpiAccountFit3(wIso8583 iso) {
		
		FitIsAut aut = null;
		wIso8583 isoCredit = null;
		try {
			

			aut = new FitIsAut();
			isoCredit =  (wIso8583) iso.clone();
			isoCredit.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			isoCredit = aut.addIsoAdditionalRows(new Ref<wIso8583>(isoCredit));
			
			//isoCredit.setISO_102_AccountID_1(iso.getISO_103_AccountID_2());
			isoCredit.setISO_102_AccountID_1(isoCredit.getISO_052_PinBlock());
			isoCredit.setISO_123_ExtendedData("ACREDITACION SPI");
			isoCredit = aut.ExecuteStandarTransaction(isoCredit);
			iso.setISO_039_ResponseCode(isoCredit.getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(isoCredit.getISO_039p_ResponseDetail());
			iso.setWsIso_LogStatus(isoCredit.getWsIso_LogStatus());
			iso.setWsTempAut(isoCredit.getWsTempAut());
			iso.setISO_044_AddRespData(isoCredit.getISO_044_AddRespData());
			iso.setWsISO_TranDatetimeResponse(isoCredit.getWsISO_TranDatetimeResponse());
			
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::CreditoSpiAccountFit3 ", TypeMonitor.error, e);
		}
		return iso;
	}
	
	public wIso8583 BloqueoSpiAccount(wIso8583 iso) {
		try {
			
			Thread.sleep(590);
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::BloqueoSpiAccount ", TypeMonitor.error, e);
		}
		return iso;
	}
	
	public wIso8583 ValidateTransactionTransferSPI(wIso8583 iso) {
    
		IsoRetrievalTransaction sql = null;
		try {
			
			wIso8583 isoClone = (wIso8583) iso.clone();
			sql = new IsoRetrievalTransaction();
			isoClone = sql.RetrieveValidationSNPSPI(isoClone);
			if(isoClone.getISO_039_ResponseCode().equals("000")) {
				/*Nueva validacion, cuando el cliente tiene mas de una cuenta con el mismo producto*/
				
				String[] ctas = iso.getISO_103_AccountID_2().split("\\|");
				for (int i = 0; i < ctas.length; i++) {
					
					if(ctas[i].equals(iso.getISO_103_AccountID_2()) && 
							isoClone.getISO_123_ExtendedData().equals(iso.getISO_123_ExtendedData())){
						
						iso.setISO_039_ResponseCode("000");
						iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
						break;
						
					}else {
						
						iso.setISO_039_ResponseCode("100");
						iso.setISO_039p_ResponseDetail("LOS DATOS DEL RECEPTOR NO COINCIDEN CON LOS REGISTRADOS EN EL SISTEMA");
						iso.setISO_124_ExtendedData("BE01-La identificacion del cliente final no corresponde con la asociada al numero de cuenta.");
					}
				}
					
			}else {
				
				iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo SPIBceIsAcq::ValidateTransactionTransferSPI ", TypeMonitor.error, e);
		}
		return iso;
	}
	public static void gc() {
	     Object obj = new Object();
	     @SuppressWarnings("rawtypes")
		WeakReference ref = new WeakReference<Object>(obj);
	     obj = null;
	     while(ref.get() != null) {
	       System.gc();
	     }
	   }


}
class ContainerSPIOrd{
	
	private String messageIso;
	private SnpSPIOrdenante snpClase;
	
	public ContainerSPIOrd() {
	
		this.messageIso = StringUtils.Empty();
		snpClase = new SnpSPIOrdenante();
	}

	public String getMessageIso() {
		return messageIso;
	}

	public void setMessageIso(String messageIso) {
		this.messageIso = messageIso;
	}

	public SnpSPIOrdenante getSnpClase() {
		return snpClase;
	}

	public void setSnpClase(SnpSPIOrdenante snpClase) {
		this.snpClase = snpClase;
	}
	
	
	
}
class ContainerIsoList{
	
	private List<Iso8583> listMayorista;
	private List<Iso8583> listMinorista;
	
	public ContainerIsoList() {
	
		listMayorista = new CopyOnWriteArrayList<>();
		listMinorista = new CopyOnWriteArrayList<>();
	}
	
	public List<Iso8583> getListMayorista() {
		return listMayorista;
	}
	public void setListMayorista(List<Iso8583> listMayorista) {
		this.listMayorista = listMayorista;
	}
	public List<Iso8583> getListMinorista() {
		return listMinorista;
	}
	public void setListMinorista(List<Iso8583> listMinorista) {
		this.listMinorista = listMinorista;
	}
}
class ContainerIsoListResponses{
	
	private List<Iso8583> listMayoristaOk;
	private List<Iso8583> listMayoristaErr;
	private List<Iso8583> listMinoristaOK;
	private List<Iso8583> listMinoristaERR;
	
	public ContainerIsoListResponses() {
	
		listMayoristaOk = new CopyOnWriteArrayList<>();
		listMayoristaErr = new CopyOnWriteArrayList<>();
		listMinoristaOK = new CopyOnWriteArrayList<>();
		listMinoristaERR = new CopyOnWriteArrayList<>();
	}

	public List<Iso8583> getListMayoristaOk() {
		return listMayoristaOk;
	}

	public void setListMayoristaOk(List<Iso8583> listMayoristaOk) {
		this.listMayoristaOk = listMayoristaOk;
	}

	public List<Iso8583> getListMayoristaErr() {
		return listMayoristaErr;
	}

	public void setListMayoristaErr(List<Iso8583> listMayoristaErr) {
		this.listMayoristaErr = listMayoristaErr;
	}

	public List<Iso8583> getListMinoristaOK() {
		return listMinoristaOK;
	}

	public void setListMinoristaOK(List<Iso8583> listMinoristaOK) {
		this.listMinoristaOK = listMinoristaOK;
	}

	public List<Iso8583> getListMinoristaERR() {
		return listMinoristaERR;
	}

	public void setListMinoristaERR(List<Iso8583> listMinoristaERR) {
		this.listMinoristaERR = listMinoristaERR;
	}
	
}
class ProcessorResponsesFromBCE implements Runnable{

	private SnpSPIOrdenante snp;
	private Logger log;
	
	public ProcessorResponsesFromBCE(SnpSPIOrdenante snp) {
		this.snp = snp;
		log = new Logger();
	}
	
	@Override
	public void run() {
		
		Process(this.snp);
		
	}
	
	private void Process(SnpSPIOrdenante snp) {
		
		IsoRetrievalTransaction sql = null;	
		final ContainerSPIOrd container = new ContainerSPIOrd();
		final SPIBceIsAcq spiAcq = new SPIBceIsAcq();
		try {
			
				sql = new IsoRetrievalTransaction();
				SnpSPIOrdenante snpQuery = (SnpSPIOrdenante) snp.clone();
				snpQuery = sql.RetrieveTrxSPI_Ord(snpQuery);
				log.WriteLogMonitor("**** CONFIRMACIONES <<QUERY>> ERROR: " + snpQuery.getError_code_prop(), TypeMonitor.monitor, null);
				if(snpQuery.getError_code_prop().equals("000")) {
					
					final String isoMessage = snpQuery.getIso_message();
					container.setMessageIso(isoMessage);
					container.setSnpClase(snpQuery);
					log.WriteLogMonitor("**** Mensaje Confirmacion " + snp.getStatus_bce(), TypeMonitor.monitor, null);
					switch (snp.getStatus_bce()) {
					case "RVRS":
					case "RJCT":
						log.WriteLogMonitor("**** Entro Reverso por RJCT " + snp.getStatus_bce(), TypeMonitor.monitor, null);
						/*Reverso de Transaccion*/
                        spiAcq.ReverseTransactionForResponseBCE(container, snp);
						break;
						
					case "ACCP"://Ultima Instancia
					case "ACSP"://Aceptada minorista
					case "ACTC"://Validada
					case "ACSC"://Aceptada mayorista
					case "ACWC"://Aceptada con fecha posterior (programada, para lo cual se debera enviar el tag IntrBkSttlmDt)
						log.WriteLogMonitor("**** Entro por RESPUESTA: " + snp.getStatus_bce(), TypeMonitor.monitor, null);
                        spiAcq.UpdateTrxSpiOrdenante(container, snp);
						break;
					default:
						break;
					}
				}
				
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo SPIBceIsAcq$ProcessorResponsesFromBCE::Process ", TypeMonitor.error, e);
		}
	}
}
