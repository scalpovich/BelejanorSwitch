package com.belejanor.switcher.scheduler;

import java.util.Arrays;
import java.util.Calendar;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.BatchDay;
import com.belejanor.switcher.sqlservices.IsoSqlMaintenance;
import com.belejanor.switcher.utils.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class TaskBatchExcuteChrono implements Job{

	@Override
	public void execute(JobExecutionContext context) {
		
		
		Logger log = null;
		BatchDay batchDay = new BatchDay();
		IsoSqlMaintenance sql = null;
		String res = StringUtils.Empty();
		try {
			log = new Logger();
			log.WriteLogMonitor("<<INICIANDO PROCESO BATCH: TaskBatchExcuteChrono>>", TypeMonitor.monitor, null);
			batchDay = batchDay.getParamsBatch("M-");
			if(batchDay != null) {
				
				String proc = Arrays.asList(batchDay.getParams_call().split("\\-")).get(1);
				
				
				Calendar calendar = Calendar.getInstance(); 
				
				int ultimoDiaMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				int diaActual = calendar.get(Calendar.DAY_OF_MONTH);
				
				if(diaActual == ultimoDiaMes) {
					sql = new IsoSqlMaintenance();
					res = sql.executeBatchCobis(TypeBatch.MENSUAL, proc);
					log.WriteLogMonitor("<<FINAL PROCESO BATCH: TaskBatchExcuteChrono>>: " + res.substring(3), 
							TypeMonitor.error, null);
				}else {
					res = "000NO ES FIN DE MES... PROCESO NO EJECUTADO";
					log.WriteLogMonitor("<<FINAL PROCESO BATCH: TaskBatchExcuteChrono>>: NO ES FIN DE MES", 
							TypeMonitor.error, null);
				}
				
			}else {
				log.WriteLogMonitor("No se puede recuperar parametros BatchDay NULL", TypeMonitor.error, null);
			}
		    
			log.WriteLogMonitor("EJECUCION BATCH: TaskBatchExcuteChrono:  " + res.substring(3) , TypeMonitor.monitor, null);
			
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo TaskBatchExcuteChrono::run()", TypeMonitor.error, e);
			log.WriteLogMonitor("PROBLEMAS EJECUCION BATCH: TaskBatchExecuteDay:  " + e.getMessage() , TypeMonitor.monitor, null);
		}
	}

}
