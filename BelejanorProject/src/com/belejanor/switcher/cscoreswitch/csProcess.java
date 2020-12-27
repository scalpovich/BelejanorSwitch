package com.belejanor.switcher.cscoreswitch;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.sqlservices.IsoSqlMaintenance;
import com.belejanor.switcher.sqlservices.typeBDD;
import com.belejanor.switcher.storeandforward.AdminProcessStoreAndForward;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;
import com.belejanor.switcher.validatefinantial.RulesValidation;

public class csProcess implements Callable<Iso8583>{
	
	private Logger log;
	private StopWatch timmerMidd = null;
	private boolean continueTrx;
	private Iso8583 iso;
	private String IP;
	
	
	public csProcess(){
		this.log = new Logger();
		this.continueTrx = false;
	}
	public csProcess(Iso8583 iso, String IP){
		this();
		this.IP = IP;
		this.iso = iso;
	}
	public Iso8583 getIso() {
		return iso;
	}

	public void setIso(Iso8583 iso) {
		this.iso = iso;
	}
	
	@SuppressWarnings("null")
	public Iso8583 ProcessTransactionMain(Iso8583 iso, String Ip) {
		
		timmerMidd = new StopWatch();
		int responseSql = 0;
		wIso8583 wiso = null;
		IsoSqlMaintenance sql = null;
		timmerMidd.start();
		if(MemoryGlobal.flagSystemReady){
			try {				
			    //log.WriteLog(iso, TypeLog.report, TypeWriteLog.file);	
				
				log.WriteOptimizeLog(iso, TypeLog.report, Iso8583.class, true, 0);
				wiso = new wIso8583(iso, Ip);			
				if(wiso.getISO_039_ResponseCode().equals("000")){
					if(wiso.getWsTransactionConfig().getIsSaved() == 1){
						
						sql = new IsoSqlMaintenance();
						responseSql = sql.InsertIso8583(wiso, com.belejanor.switcher.sqlservices.typeBDD.Sybase);
						if(responseSql == 0){
							this.continueTrx = true;
						    wiso.getWsTransactionConfig().setInserted(true);
						}
					}
					if(responseSql == 0){
						
						RulesValidation validator = new RulesValidation();
						wiso = validator.ValidateProcessor(wiso);
						
						if(wiso.getISO_039_ResponseCode().equals("000")){
							
							String messageClass = wiso.getWsTransactionConfig().getMessage_Class();					
							List<String> aa = Arrays.asList(messageClass.split("\\."));
							String methodName = aa.get(aa.size() -1);
							String classname = messageClass.replace("." + methodName, "");
							
							Class<?> instanceClass = Class.forName(classname);																			
							Object classInstance = instanceClass.getConstructor(new Class[]{}).newInstance();					
							Method methodToInvoke = instanceClass.getMethod(methodName, wIso8583.class);					
							wiso = (wIso8583) methodToInvoke.invoke(classInstance, wiso);
						}	
						
						iso = ParseISO8583toWiso(wiso);
						
						if(iso == null){
							iso.setISO_039_ResponseCode("900");
							iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS INTERNOS (ERROR PARSE ISO8583)");
						}
	
				
					}
					else{
						if(responseSql == 2601){ //1 en oracle
							
							if(iso.getISO_000_Message_Type().startsWith("12")){
								
								iso.setISO_039_ResponseCode("078");
								iso.setISO_039p_ResponseDetail("TRANSACCION DUPLICADA <**>");
								
							}else{
								
								iso.setISO_039_ResponseCode("606");
								iso.setISO_039p_ResponseDetail("REVERSO YA EFECTUADO <**>");
							}
						}
						else{
							iso.setISO_039_ResponseCode("901");
							iso.setISO_039p_ResponseDetail("ERROR EN <<MODULO>> DE BDD SW_ORACLE CODE: " + responseSql + 
														  " DESERROR: " + sql.getDescriptionSqlError());
						}
					}	
				}
				else{
					
					iso.setISO_039_ResponseCode(wiso.getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(wiso.getISO_039p_ResponseDetail());
					
				}
					
				
			} 
			catch (Exception e) {
				
				log.WriteLogMonitor("Error modulo csProcess::ProcessTransactionMain ", TypeMonitor.error, e);
				iso.setISO_039_ResponseCode("909");
				iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
			}	
			finally{
				
				if(iso.getISO_000_Message_Type().startsWith("12") || iso.getISO_000_Message_Type().startsWith("14"))
					iso.setISO_000_Message_Type(iso.getISO_000_Message_Type().replace("00", "10"));
				else if (iso.getISO_000_Message_Type().startsWith("18")) {
					iso.setISO_000_Message_Type("1810");
				}
					
				timmerMidd.stop();
				wiso.setTickMidd(timmerMidd);
				wiso.setWsTempTrx((timmerMidd.getTime(TimeUnit.MILLISECONDS)/1000.0));

			}
			//Modifique aqui....2018/04.02
			EndTransaction endTrx = new EndTransaction(wiso, iso, this.continueTrx);
			endTrx.start();
		
		}
		else{
			
			iso.setISO_000_Message_Type(iso.getISO_000_Message_Type().replace("00", "10"));
			iso.setISO_039_ResponseCode("901");
			iso.setISO_039p_ResponseDetail("ERROR AL INICIALIZAR PROCESOS A MEMORIA... GET MEMORY_CACHED NULL");
		}

		return iso;
		
	}
	
	private Iso8583 ParseISO8583toWiso(wIso8583 iso){
		Iso8583 ISO = null;
		try {	
			
			String nroAuth = StringUtils.IsNullOrEmpty(iso.getISO_038_AutorizationNumber())?StringUtils.Empty():iso.getISO_038_AutorizationNumber();
			if( StringUtils.IsNullOrEmpty(nroAuth) 
					&& iso.getWsIso_LogStatus() == 2 && iso.getISO_039_ResponseCode().equals("000"))
				 iso.setISO_038_AutorizationNumber(GeneralUtils.GetSecuencial(6).toUpperCase());
			ISO = new Iso8583(iso);	
			return ISO;
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo csProcess::ParseISO8583toWiso ", TypeMonitor.error, e);
			return null;
		}						
	}
	
	public wIso8583 ExecStoreAndForwardMain(wIso8583 iso){
		
		timmerMidd = new StopWatch();
		try {
			
			iso.setTickAut(new StopWatch());
			iso.setTickBdd(new StopWatch());
			iso.setTickMidd(new StopWatch());
			
			String messageClass = iso.getWsTransactionConfig().getMessage_Class();					
			List<String> aa = Arrays.asList(messageClass.split("\\."));
			String methodName = aa.get(aa.size() -1);
			String classname = messageClass.replace("." + methodName, "");
			
			Class<?> instanceClass = Class.forName(classname);																			
			Object classInstance = instanceClass.getConstructor(new Class[]{}).newInstance();					
			Method methodToInvoke = instanceClass.getMethod(methodName, wIso8583.class);	
			
			timmerMidd.reset();
			timmerMidd.start();
			iso = (wIso8583) methodToInvoke.invoke(classInstance, iso);
			timmerMidd.stop();
			iso.setTickMidd(timmerMidd);
			
			if(iso.getWsISO_FlagStoreForward()){
				
				iso.setWsIsoSF_CountVisualizer(iso.getWsIsoSF_CountVisualizer() + 1);
				Thread t = new Thread(new AdminProcessStoreAndForward()
						       .AddTransactionToStoreAndForward(iso));
				t.start();
			}else {
				
				//Actualizacion del SF
				if(iso.getWsISO_SFRetryCounts() != -1)
					iso.setISO_124_ExtendedData("TRX. INFINITA CONTESTADA POR REINTENTOS DE STORE AND FORWARD");
				else
					iso.setISO_124_ExtendedData("TRX. CONTESTADA POR REINTENTOS STORE AND FORWARD");
				Thread t = new Thread(new IsoSqlMaintenance()
						.RunnableUpdateIso8583(iso,typeBDD.Sybase));
				t.start();
			}
		} catch (Exception e) {
			
			if(timmerMidd.isStarted())
				timmerMidd.stop();
			log.WriteLogMonitor("Error modulo csProcess::ExecStoreAndForwardMain ", TypeMonitor.error, e);
			e.printStackTrace();
			
		}finally {
			
			iso.setWsTempTrx((timmerMidd.getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;

	}
	
	@Override
	public Iso8583 call() throws Exception {
		
		return ProcessTransactionMain(this.iso, this.IP);
	}
}
