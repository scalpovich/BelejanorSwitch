package com.belejanor.switcher.cscoreswitch;

import java.util.Arrays;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.IsoSqlMaintenance;
import com.belejanor.switcher.sqlservices.typeBDD;
import com.belejanor.switcher.storeandforward.AdminProcessStoreAndForward;
import com.belejanor.switcher.utils.StringUtils;
import com.belejanor.switcher.cscoreswitch.Iso8583;

public class EndTransaction extends Thread {

	private wIso8583 iso;
	private Iso8583 iso8583;
	private Logger log = null;
	private boolean flagContinueTrx = false;
	
	public EndTransaction(wIso8583 iso, Iso8583 iso8583, boolean flagContinue){
		this.iso = iso;
		this.iso8583 = iso8583;
		this.log = new Logger();
		this.flagContinueTrx = flagContinue;
	}
	
	/*public void EndTransactionSwitch(wIso8583 iso, Iso8583 iso8583){		
		try {
						
			IsoSqlMaintenance sql = new IsoSqlMaintenance();
			LoggerConfig logger = new LoggerConfig();
			
			EngineTaskProccesor task = new EngineTaskProccesor(10);
			if(this.flagContinueTrx)
				task.add(sql.RunnableUpdateIso8583(iso));
			if(iso.getWsTransactionConfig() != null){
				if(iso.getWsTransactionConfig().getIsLoged() == 1){
					task.add(logger.WriteLogRunnable(iso8583, 
							TypeLog.report, TypeWriteLog.file));
				}
			}
			task.add(logger.WriteMonitorRun(iso, iso8583));
		    task.go();
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo EndTransaction::EndTransactionSwitch ", TypeMonitor.error, e);
		}
	}*/
	
	public void EndTransactionSwitch(wIso8583 iso, Iso8583 iso8583){		
		
		try {
			
			IsoSqlMaintenance sql = new IsoSqlMaintenance();
			LoggerConfig logger = new LoggerConfig();
			
			EngineTaskProccesor task = new EngineTaskProccesor(10);
			if(this.flagContinueTrx)
				task.add(sql.RunnableUpdateIso8583(iso,typeBDD.Sybase));
			if(iso.getWsTransactionConfig() != null){
				
				if(iso.getWsTransactionConfig().getIsLoged() == 1){
					//task.add(logger.WriteLogRunnable(iso8583, 
					//		TypeLog.report, TypeWriteLog.file));
					log.WriteOptimizeLog(iso, TypeLog.report, Iso8583.class, true,1);
					/*ojo comente para que no escriba log iso8583*/
				}
			}
			String flagSf = StringUtils.IsNullOrEmpty(iso.getISO_023_CardSeq())?"":iso.getISO_023_CardSeq();
			if(flagSf.equals("SF")){
				
				iso.setWsIsoSF_CountVisualizer(iso.getWsIsoSF_CountVisualizer() + 1);
				task.add(logger.WriteMonitorStoreAndForwardAsycn(iso));
			}
			else
				task.add(logger.WriteMonitorRun(iso, iso8583));
			task.add(new AdminProcessStoreAndForward().AddTransactionToStoreAndForward(iso));
			
			
			/*Proceso que comprueba si la transaccion tiene transacciones hijas*/
			if(iso.getWsTransactionConfig() != null) {
				if(!StringUtils.IsNullOrEmpty(iso.getWsTransactionConfig().getIsNotif())) {
					if(iso.getWsTransactionConfig().getIsNotif().equalsIgnoreCase("Y")) {
						/*Recursive si es false ejecuta comandos asociados, si es true NO 
						 * pero cambia la trx en ISOBITMAP lo pone NONE */
						if(!iso8583.getISO_BitMap().equalsIgnoreCase("NONE")) {
							/*Evaluacion si se envia por exitosas o por error*/
							/*Esos codigos de error se guardan en la tabla TRANSACTION_CONFIGURATION campo Notif_Mail*/
							String[] ErrorCodesValids = iso.getWsTransactionConfig().getNotif_Mail().split("\\,");
							if(Arrays.asList(ErrorCodesValids).contains(iso8583.getISO_039_ResponseCode())) {
								EngineProcessorTransactionsBackGround processorEngine = new EngineProcessorTransactionsBackGround(iso);
								task.add(processorEngine.runProcessorBackGround());
							}
						}
					}
				}	
			}
			/* FIN Proceso que comprueba si la transaccion tiene transacciones hijas*/
		    task.go();
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo EndTransaction::EndTransactionSwitch ", TypeMonitor.error, e);
		}
	}
	
	@Override
	public void run() {		
		EndTransactionSwitch(this.iso, this.iso8583);
	}
	
	
}
