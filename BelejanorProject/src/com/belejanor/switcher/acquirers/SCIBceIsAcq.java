package com.belejanor.switcher.acquirers;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import com.belejanor.switcher.authorizations.FitIsAut;
import com.belejanor.switcher.authorizations.SCIBceIsAut;
import com.belejanor.switcher.cscoreswitch.EngineCallableProcessor;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.TransactionConfiguration;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.SerializationExecutorString;
import com.belejanor.switcher.utils.StringUtils;

public class SCIBceIsAcq {

	private Logger log;
	private wIso8583 iso;
	
	public SCIBceIsAcq(){
		
		log = new Logger();
	}
	
	public wIso8583 getIso() {
		return iso;
	}

	public void setIso(wIso8583 iso) {
		this.iso = iso;
	}
	
	//Hilos Metodo que 1ro se llama desde BRIDGE
		public void processTrxSciReceptor(List<Iso8583> isoList) {
			
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
				System.out.println("Ej. Minorista: " + soListResponseMinoristas.get(0).getISO_039_ResponseCode() + " ---- " + soListResponseMinoristas.get(0).getISO_039p_ResponseDetail());
				
				log.WriteLogMonitor("\n**********************************************************************\n"
						          + "============= TERMINO PROCESAMIENTO SCI RECEPTOR ===============\n"
						          + "============= TRX. RECIBIDAS: "+ isoList.size() + " ===============================\n"
						          + "============= TIEMPO INICIO : "+ FormatUtils.DateToString(tiempoInicio, "YYYY-MM-DD-HH:mm:ss") +" ===============\n"
						          + "============= TIEMPO FINAL  : "+ FormatUtils.DateToString(new Date(), "YYYY-MM-DD-HH:mm:ss") +" ===============\n"
						          + "**********************************************************************\n"
						, TypeMonitor.monitor, null);
				
				Ref<ContainerIsoList> containerResponses = new Ref<ContainerIsoList>(cont);
				PrepareTransactionConfirmToBCE(containerResponses);
				
			} catch (Exception e) {
				
				log.WriteLogMonitor("Error modulo SCIBceIsAcq::processTrxSciReceptor ", TypeMonitor.error, e);
			}
		}
		
		private void SerializationIsos(List<Iso8583> isoList) {
			
			try {
							
				EngineCallableProcessor<String> engine = new EngineCallableProcessor<>(MemoryGlobal.UrlNumberThreadsExecutorSCI);
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
		
		/*METODO PRINCIPAL QUE EJECUTA TODO SCI RECEPTOR REFERENCIA AQUI*/ 
		private void ReceptorProcessingTransactions(List<Iso8583> isoList, int flag, Ref<ContainerIsoList> refContainer) {
			
			try {
				
				ContainerIsoList container = new ContainerIsoList();
				
				String IP = StringUtils.Empty();
				EngineCallableProcessor<Iso8583> engine = new EngineCallableProcessor<>(MemoryGlobal.UrlNumberThreadsExecutorSCI);
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
			
				log.WriteLogMonitor("Error modulo SCIBceIsAcq::ReceptorProcessingTransactions ", TypeMonitor.error, e);
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
				log.WriteLogMonitor("Error modulo SCIBceIsAcq::SendTransactionConfirmToBCE ", TypeMonitor.error, e);
			}
		}
		
		/*METODO QUE ENVIA LAS TRANSACCIONES AL BCE (NOTIFICACIONES CUANDO ES RECEPTOR LA INSTITUCION FINANCIERA)*/
		private void SendTransactionsResponsesToBCE(final Ref<ContainerIsoListResponses> cont) {
			
			Iso8583 isoCentinelaMayOk = null; wIso8583 wIsoCentinelaMayOk = null;
			Iso8583 isoCentinelaMayErr = null; wIso8583 wIsoCentinelaMayErr = null;
			Iso8583 isoCentinelaMinOk = null; wIso8583 wIsoCentinelaMinOk = null;
			Iso8583 isoCentinelaMinErr = null; wIso8583 wIsoCentinelaMinErr = null;
			SCIBceIsAut autorizator = null;
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
					isoCentinelaMayOk.setISO_041_CardAcceptorID("SCI MAYORISTAS OK");
					isoCentinelaMayOk.setISO_055_EMV("ACSC");
					isoCentinelaMayOk.setISO_006_BillAmount(cont.get().getListMayoristaOk().get(0)
															.getISO_006_BillAmount());
					isoCentinelaMayOk.setISO_002_PAN("TRX.:" + cont.get().getListMayoristaOk().size());
					isoCentinelaMayOk.setISO_008_BillFeeAmount(cont.get().getListMayoristaOk().get(0)
															   .getISO_008_BillFeeAmount());
					
					wIsoCentinelaMayOk = new wIso8583(isoCentinelaMayOk, "127.0.0.1");
					
					autorizator = new SCIBceIsAut();
					wIsoCentinelaMayOk = autorizator.SendRequestSCINotifications(wIsoCentinelaMayOk, cont.get().getListMayoristaOk());
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
					isoCentinelaMayErr.setISO_041_CardAcceptorID("SCI MAYORISTAS ERROR");
					isoCentinelaMayErr.setISO_055_EMV("RJCT");
					isoCentinelaMayErr.setISO_006_BillAmount(cont.get().getListMayoristaErr().get(0)
							.getISO_006_BillAmount());
					isoCentinelaMayErr.setISO_008_BillFeeAmount(cont.get().getListMayoristaErr().get(0)
							   .getISO_008_BillFeeAmount());
					wIsoCentinelaMayErr = new wIso8583(isoCentinelaMayErr, "127.0.0.1");
					
					autorizator = new SCIBceIsAut();
					wIsoCentinelaMayErr = autorizator.SendRequestSCINotifications(wIsoCentinelaMayErr, cont.get().getListMayoristaErr());
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
					isoCentinelaMinOk.setISO_041_CardAcceptorID("SCI MINORISTAS OK");
					isoCentinelaMinOk.setISO_055_EMV("ACSP");
					isoCentinelaMinOk.setISO_006_BillAmount(cont.get().getListMinoristaOK().get(0)
							.getISO_006_BillAmount());
					isoCentinelaMinOk.setISO_008_BillFeeAmount(cont.get().getListMinoristaOK().get(0)
							   .getISO_008_BillFeeAmount());
					
					wIsoCentinelaMinOk = new wIso8583(isoCentinelaMinOk, "127.0.0.1");
					
					autorizator = new SCIBceIsAut();
					wIsoCentinelaMinOk = autorizator.SendRequestSCINotifications(wIsoCentinelaMinOk, cont.get().getListMinoristaOK());
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
					isoCentinelaMinErr.setISO_041_CardAcceptorID("SCI MINORISTAS ERROR");
					isoCentinelaMinErr.setISO_055_EMV("RJCT");
					isoCentinelaMinErr.setISO_006_BillAmount(cont.get().getListMinoristaERR().get(0)
							.getISO_006_BillAmount());
					isoCentinelaMinErr.setISO_008_BillFeeAmount(cont.get().getListMinoristaERR().get(0)
							   .getISO_008_BillFeeAmount());
					
					wIsoCentinelaMinErr = new wIso8583(isoCentinelaMinErr, "127.0.0.1");
					
					autorizator = new SCIBceIsAut();
					wIsoCentinelaMinErr = autorizator.SendRequestSCINotifications(wIsoCentinelaMinErr, cont.get().getListMinoristaERR());
					isoCentinelaMinErr = new Iso8583(wIsoCentinelaMinErr);
					//Listo para ejecutar la Trx csProcess
					processor = new csProcess();
					isoCentinelaMinErr = processor.ProcessTransactionMain(isoCentinelaMinErr, "127.0.0.1"); 
				}
				
			} catch (Exception e) {
			
				log.WriteLogMonitor("Error modulo SPIBceIsAcq::SendTransactionsResponsesToBCE ", TypeMonitor.error, e);
			}
		}
		
		public wIso8583 BloqueoSciAccount(wIso8583 iso) {
			try {
				
				Thread.sleep(590);
				iso.setISO_039_ResponseCode("000");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				
			} catch (Exception e) {
				
				log.WriteLogMonitor("Error modulo SCIBceIsAcq::BloqueoSciAccount ", TypeMonitor.error, e);
			}
			return iso;
		}
		
		public wIso8583 DebitoSciAccount(wIso8583 iso) {
			
			FitIsAut aut = null;
			wIso8583 isoCredit = null;
			try {
				
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
				
				log.WriteLogMonitor("Error modulo SCIBceIsAcq::DebitoSciAccount ", TypeMonitor.error, e);
			}
			return iso;
		}
		
		public wIso8583 ValidateTransactionTransferSCI(wIso8583 iso) {
			
			IsoRetrievalTransaction sql = null;
			try {
				
				wIso8583 isoClone = (wIso8583) iso.clone();
				sql = new IsoRetrievalTransaction();
				iso = sql.RetrieveValidationSNPSPI(isoClone);
				if(isoClone.getISO_039_ResponseCode().equals("000")) {
					
					if(isoClone.getISO_103_AccountID_2().equals(iso.getISO_103_AccountID_2()) && 
							isoClone.getISO_123_ExtendedData().equals(iso.getISO_123_ExtendedData())){
						
						iso.setISO_039_ResponseCode("000");
						iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
						
					}else {
						
						iso.setISO_039_ResponseCode("100");
						iso.setISO_039p_ResponseDetail("LOS DATOS DEL RECEPTOR NO COINCIDEN CON LOS REGISTRADOS EN EL SISTEMA");
						iso.setISO_124_ExtendedData("BE01-La identificacion del cliente final no corresponde con la asociada al numero de cuenta.");
					}
					
				}else {
					
					iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
				}
				
			} catch (Exception e) {
				
				log.WriteLogMonitor("Error modulo SCIBceIsAcq::ValidateTransactionTransferSCI ", TypeMonitor.error, e);
			}
			return iso;
		}
}
