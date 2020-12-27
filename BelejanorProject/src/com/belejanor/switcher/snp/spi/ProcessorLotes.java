package com.belejanor.switcher.snp.spi;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.belejanor.switcher.authorizations.SPIBceIsAut;
import com.belejanor.switcher.cscoreswitch.EngineCallableProcessor;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.extetrnalprocess.ScheduleProcessor;
import com.belejanor.switcher.extetrnalprocess.SnpScheduledExecuteLot;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.parser.BceSPIParser;
import com.belejanor.switcher.sqlservices.IsoSqlMaintenance;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.SerializationObject;

public class ProcessorLotes extends Thread{
	
	private Logger log;
	
	public ProcessorLotes() {
		
		log = new Logger();
	}
	/*Metodo V1*/
	public void ExecuteProcessSNPLoptes() {
		
		try {
			
			SnpOrdLotes ord = new SnpOrdLotes();
			List<SnpOrdLotes> list = ord.retornaLotesDeamonWithout();
			ContainerIsoList contIsos = new ContainerIsoList();
			Ref<ContainerIsoList> refContainer = new Ref<>(contIsos);
			
			if(list != null) {
				
				if(list.size() > 0) {
					
					//MemoryGlobal.semaphoreIniLotesSpi = new CountDownLatch(1);
					
					Thread tParser = new Thread(() -> {
						
						ParseoSnpLoteToIso8583(list, refContainer);
					});
					
					tParser.start();
					tParser.join();
					
					List<Iso8583> isoList = refContainer.get().getIsoList();
					
					
					EngineCallableProcessor<Iso8583> engine = new EngineCallableProcessor<>(20);
					
					for (Iso8583 isos : isoList) {
						
						csProcess proc = new csProcess(isos, "127.0.0.1");
						engine.add(proc);
					}
					List<Iso8583> listIsoProc = engine.goProcess();
					//MemoryGlobal.semaphoreIniLotesSpi.countDown();
					log.WriteLogMonitor("============ TERMINO =============== " + listIsoProc.size() + "  TRANSACCIONES" , TypeMonitor.monitor, null);
					
				}
				
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo ProcessorLotes::ExecuteProcessSNPLoptes ", TypeMonitor.error, e);
		}
	}
	
	/*Metodo V2*/
	public void ExecuteProcessSNPLoptes_V2() {
		
		
		log.WriteLogMonitor("====> ENTRO POR VERSION 2" , TypeMonitor.monitor, null);
		try {
			
			SnpOrdLotes ord = new SnpOrdLotes();
			List<SnpOrdLotes> list = ord.retornaLotesDeamonWithout();
			ContainerIsoList contIsos = new ContainerIsoList();
			Ref<ContainerIsoList> refContainer = new Ref<>(contIsos);
			SPIBceIsAut spiAut = null;
			final IsoSqlMaintenance sql = new IsoSqlMaintenance();
			
			if(list != null) {
				
				if(list.size() > 0) {
					
					//MemoryGlobal.semaphoreIniLotesSpi = new CountDownLatch(1);
					Thread tParser = new Thread(() -> {
						
						ParseoSnpLoteToIso8583_V2(list, refContainer);
					});
					
					tParser.start();
					tParser.join();
					
					List<Iso8583> isoList = refContainer.get().getIsoList();
					
					/*Debitos Masivos*/
					ContainerIsoList contRes = new ContainerIsoList();
					Thread tProcess = new Thread(() -> {
						
						EngineCallableProcessor<Iso8583> engine = new EngineCallableProcessor<>(MemoryGlobal.UrlNumberThreadsExecutorSPI);
						
						log.WriteLogMonitor(".....Inicia Serializacion respaldo campo iso114....." , TypeMonitor.monitor, null);
						for (Iso8583 isos : isoList) {
							
							/*SERIALIZACION EN CAMPO 124*/
							String xmlIso = SerializationObject.ObjectToString(isos, Iso8583.class);
							isos.setISO_036_Track3(isos.getISO_114_ExtendedData()); //Respaldo campo 114 en 036
							isos.setISO_124_ExtendedData(isos.getISO_114_ExtendedData());
							isos.setISO_114_ExtendedData(xmlIso);
							csProcess proc = new csProcess(isos, "127.0.0.1");
							engine.add(proc);
						}
						List<Iso8583> listIsoProc = null;
						try {
							listIsoProc = engine.goProcess();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						contRes.setIsoList(listIsoProc);
					});
					tProcess.start();
					tProcess.join();
					/* FIN Debitos Masivos*/
					
					log.WriteLogMonitor("============ TERMINO V.2 =============== " + contRes.getIsoList().size() + "  TRANSACCIONES" , TypeMonitor.monitor, null);
					
					/*Selecccion de solo transacciones exitosas*/
					ContainerIsoListResultsDebit containerResults = filterTrxResultsDebits(contRes.getIsoList());
					log.WriteLogMonitor("\n ============ DEBITOS A CUENTA =============== \n" + 
					                    "TRX. EXITOSAS: " +  containerResults.getIsoListOk().size() + "\n" +
					                    "TRX. NO OK   : " +  containerResults.getIsoListNOK().size() + "\n" +
					                    "============================================= \n"
					                    , TypeMonitor.monitor, null);
					
					
					/*Se envia las respuestas al BCE*/
					log.WriteLogMonitor("============ ENVIANDO AL BCE: " + containerResults.getIsoListOk().size() + "  TRANSACCIONES....." , TypeMonitor.monitor, null);
					spiAut = new SPIBceIsAut();
					wIso8583 wiso = new wIso8583(isoList.get(0), "127.0.0.1");
					wiso = spiAut.SendRequestSPIOrdMasivo(containerResults.getIsoListOk(), wiso);
					log.WriteLogMonitor("\n ============ RESULTADO BCE: \n" + 
					                    "==== CODIGO     : " + wiso.getISO_039_ResponseCode() + "\n" + 
							            "==== DESCRIPCION: " + wiso.getISO_039p_ResponseDetail() + "\n" +
							            "==== REF. BCE   : " + wiso.getISO_123_ExtendedData()+ "\n"
							            , TypeMonitor.monitor, null);
					
					wIso8583 wisoRegisterRCVD = wiso;
					Thread tRcvd = new Thread(() -> {
						
						sql.RegisterRCVD_LotesSPI(wisoRegisterRCVD);
					});
					tRcvd.start();
					//tRcvd.join();
					//MemoryGlobal.semaphoreIniLotesSpi.countDown();ACTUALIZANDO RCVD LOTES
					
				}
				
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo ProcessorLotes::ExecuteProcessSNPLoptes ", TypeMonitor.error, e);
		}
	}
	
	
	
	protected ContainerIsoListResultsDebit filterTrxResultsDebits(List<Iso8583> isoList) {
		
		ContainerIsoListResultsDebit cont = null;
		try {
			
			cont = new ContainerIsoListResultsDebit();
			
			cont.setIsoListOk(isoList.stream()
						.filter(p -> p.getISO_039_ResponseCode().equals("000"))
						.peek(Objects::requireNonNull)
						.collect(Collectors.toList()));
			
			cont.setIsoListNOK(isoList.stream()
						.filter(p -> !(p.getISO_039_ResponseCode().equals("000")))
						.peek(Objects::requireNonNull)
						.collect(Collectors.toList()));
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo ProcessorLotes::filterTrxResultsDebits ", TypeMonitor.error, e);
		}
		return cont;
	}
	
	protected void ParseoSnpLoteToIso8583(List<SnpOrdLotes> snpList, Ref<ContainerIsoList> RefcontainerIso) {
		
		ContainerIsoList container = new ContainerIsoList();
		EngineCallableProcessor<Iso8583> engine = new EngineCallableProcessor<>(MemoryGlobal.UrlNumberThreadsExecutorSPI);
		try {
			
			for (SnpOrdLotes snp : snpList) {
				
				BceSPIParser parser = new BceSPIParser(snp);
				engine.add(parser);
			}
			
			List<Iso8583> listIsoProc = engine.goProcess();
			container.setIsoList(listIsoProc);
			RefcontainerIso.set(container);
			
		} catch (Exception e) {

			log.WriteLogMonitor("Error modulo ProcessorLotes::parseoSnpLoteToIso8583 ", TypeMonitor.error, e);
		}
	}
	
	protected void ParseoSnpLoteToIso8583_V2(List<SnpOrdLotes> snpList, Ref<ContainerIsoList> RefcontainerIso) {
		
		ContainerIsoList container = new ContainerIsoList();
		EngineCallableProcessor<Iso8583> engine = new EngineCallableProcessor<>(20);
		final String MsgIdLote = "SPI" + MemoryGlobal.UrlSpiCodeEfi_BCE + FormatUtils.DateToString(new Date(), "yyyyMMddHHmmss") + GeneralUtils.GetSecuencialNumeric(7);
		try {
			
			for (SnpOrdLotes snp : snpList) {
				
				BceSPIParser parser = new BceSPIParser(snp, MsgIdLote);
				engine.add(parser.callableLotesV2());
			}
			
			List<Iso8583> listIsoProc = engine.goProcess();
			container.setIsoList(listIsoProc);
			RefcontainerIso.set(container);
			
		} catch (Exception e) {

			log.WriteLogMonitor("Error modulo ProcessorLotes::ParseoSnpLoteToIso8583_V2 ", TypeMonitor.error, e);
		}
	}
	
    public void InitScheduleProcessorSnpLotes(){
		
    	SnpScheduledExecuteLot lotProcessor = new SnpScheduledExecuteLot();
		ScheduleProcessor schedule = new ScheduleProcessor();
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				schedule.ExecuteProcessPersistence(MemoryGlobal.serviceScheduleSnp, 
						lotProcessor.executeScheduleLotSnp(), 
							TimeUnit.SECONDS, MemoryGlobal.SnpScheduleLotesInterval);
			}
		});
		t.start();
	}

	@Override
	public void run() {
		
		InitScheduleProcessorSnpLotes();
	}

	
}
class ContainerIsoList{
	
	private List<Iso8583> isoList;
	
	public ContainerIsoList(List<Iso8583> isoList) {
		this();
		this.isoList = isoList;
	}
	
	public ContainerIsoList() {
		
		isoList = new CopyOnWriteArrayList<>();
	}
	
	public List<Iso8583> getIsoList() {
		return isoList;
	}

	public void setIsoList(List<Iso8583> isoList) {
		this.isoList = isoList;
	}
}
class ContainerIsoListResultsDebit{
	
	private List<Iso8583> isoListOk;
	private List<Iso8583> isoListNOK;
	
	public List<Iso8583> getIsoListOk() {
		return isoListOk;
	}
	public void setIsoListOk(List<Iso8583> isoListOk) {
		this.isoListOk = isoListOk;
	}
	public List<Iso8583> getIsoListNOK() {
		return isoListNOK;
	}
	public void setIsoListNOK(List<Iso8583> isoListNOK) {
		this.isoListNOK = isoListNOK;
	}

}

