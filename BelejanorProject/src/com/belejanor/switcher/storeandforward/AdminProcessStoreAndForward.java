package com.belejanor.switcher.storeandforward;

import com.belejanor.switcher.cscoreswitch.ContainerIsoQueue;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.Config;
import com.belejanor.switcher.queueadmin.Queue;
import com.belejanor.switcher.queueadmin.typeMessage;
import com.belejanor.switcher.sqlservices.IsoSqlMaintenance;
import com.belejanor.switcher.sqlservices.typeBDD;
import com.belejanor.switcher.utils.GeneralUtils;

public class AdminProcessStoreAndForward {
	
	private Logger log;
	
	public AdminProcessStoreAndForward(){
		this.log = new Logger();
	}

	/*Proceso por el cual se añade por primera vez un 
	 * mensaje wIso8583 a la cola de SF
	 * Este es llamando por FitCoreProcessor (Si el autorizador fue FitBank), 
	 * caso contrario será enviado por cualquier clase autorizadora */
	public void SendQueueStoreAndForwardClass(wIso8583 iso){
		Queue queue = null;
		try {
			
			if(iso.getWsTransactionConfig().getStore_Forward_Num() != 0){
				
				queue = new Queue();
				if(iso.getWsTransactionConfig().getStore_Forward_Num() != -1){
					iso.setWsISO_SFRetryCounts(iso.getWsISO_SFRetryCounts() - 1);
				}
				/*Añade segundos a la fecha actual*/
				iso.getWsTransactionConfig()
				.setTimeProcessingStoreAndForward(
				GeneralUtils.addSeccondsDate(iso.getWsTransactionConfig()
						.getStore_Forward_Time()));
				iso.setWsISO_SF_Count(iso.getWsISO_SF_Count() + 1);
				ContainerIsoQueue<wIso8583> cont = new ContainerIsoQueue<wIso8583>(iso, null);
				queue.SendMessage(typeMessage.storeAndForwardType, cont, 1, iso.getWsTransactionConfig()
						.getStore_Forward_Time());
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo AdminProcessStoreAndForward::AdminProcessStoreAndForward ", 
					TypeMonitor.error, e);
		}
	}
	/*Proceso que recibe un mensaje de SF y lo procesa nuevamente
	 *Tener en cuenta que si el mensaje es un reverso 1400 y el mismo no existia
	 *se procesa como Transaccion ISO Normal (insertando en la BDD)*/
	public void ProcessStoreAndForwardClass(wIso8583 iso){
		
		try {
			log.WriteLogMonitor("************* EJECUTANDO STORE AND FORWARD ************", TypeMonitor.monitor, null);
			if(iso != null){
				
				iso.setWsISO_FlagStoreForward(false);
				if((iso.getWsISO_SFRetryCounts() > 0 && iso.getWsISO_SFRetryCounts() 
				   < iso.getWsTransactionConfig().getStore_Forward_Num())
				   || (iso.getWsTransactionConfig().getStore_Forward_Num() == -1)){
					/*Inserta si es un mensaje 1400*/
					if(iso.getWsTransactionConfig().getStore_Forward_Type().equals("1400")
					   && iso.getWsISO_SF_Count() <= 1){
						//cambio a 1400 el mensaje
						iso.setISO_000_Message_Type(iso.getWsTransactionConfig().getStore_Forward_Type());
						csProcess processor = new csProcess();
						Iso8583 iso8583 = new Iso8583(iso);
						iso8583.setISO_023_CardSeq("SF");
						iso8583 = processor.ProcessTransactionMain(iso8583, iso.getWs_IP());
						
					}else {
						
						csProcess processor = new csProcess();
						iso = processor.ExecStoreAndForwardMain(iso);
					}
					
					/*Thread tLog = new Thread(new LoggerConfig().
							WriteMonitorStoreAndForwardAsycn(iso));
					tLog.start();*/
					
				}else {
					
					log.WriteLogMonitor("*******>>>>>>>>>>>>>>>>>>>>>>> EXPEDITE TRANSACTION STORE AND FORWARD ISO_011: ["+ iso.getISO_011_SysAuditNumber() +"]", TypeMonitor.monitor, null);
					//pendiente insercion a la BDD trx expedite;
					iso.setISO_124_ExtendedData("REINTENTOS EXPIRADOS POR STORE AND FORWARD [" + (iso.getWsISO_SF_Count() - 1) + "] REINTENTOS");
					iso.setWsISO_FlagStoreForward(true);
					Thread t = new Thread(new IsoSqlMaintenance().RunnableUpdateIso8583(iso, typeBDD.Sybase));
					t.start();
				}
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo AdminProcessStoreAndForward::ProcessStoreAndForwardClass ", 
					TypeMonitor.error, e);
		}
	}
	public void EvaluateStoreAndForwardCodes(wIso8583 iso, String code){
		
		try {
			
			Config conf = new Config();
			conf = conf.getConfigSystem("SF_" + iso.getISO_003_ProcessingCode() + "_" 
						+ iso.getISO_024_NetworkId() + "_" + iso.getISO_018_MerchantType());
			if(conf != null){
				if(conf.getCfg_Valor().contains(code))
					iso.setWsISO_FlagStoreForward(true);
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo AdminProcessStoreAndForward::EvaluateStoreAndForwardCodes ", 
					TypeMonitor.error, e);
		}
	}
	
	public Runnable AddTransactionToStoreAndForward(wIso8583 iso){
		
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				if(iso.getWsISO_FlagStoreForward())
					SendQueueStoreAndForwardClass(iso);
			}
		};
		return r;
	}
	public Runnable ProccessStoreAndForwardThreading(wIso8583 iso){
		
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				
				ProcessStoreAndForwardClass(iso);
			}
		};
		return r;
	}
	public Runnable EvaluateStoreAndForwardCodesThread(wIso8583 iso, String code){
		
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				
				EvaluateStoreAndForwardCodes(iso, code);
			}
		};
		
		return r;
	}
}
