package com.belejanor.switcher.scheduler;

import java.util.Arrays;
import java.util.TimerTask;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.BatchDay;
import com.belejanor.switcher.sqlservices.IsoSqlMaintenance;
import com.belejanor.switcher.utils.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class TaskBatchExecuteDay implements Job{

	@Override
	public void execute(JobExecutionContext context) {
		
		
		Logger log = null;
		BatchDay batchDay = new BatchDay();
		IsoSqlMaintenance sql = null;
		String res = StringUtils.Empty();
		try {
			log = new Logger();
			log.WriteLogMonitor("<<INICIANDO PROCESO BATCH: TaskBatchExecuteDay>>", TypeMonitor.monitor, null);
			batchDay = batchDay.getParamsBatch("D-");
			if(batchDay != null) {
				
				String proc = Arrays.asList(batchDay.getParams_call().split("\\-")).get(1);
				sql = new IsoSqlMaintenance();
				res = sql.executeBatchCobis(TypeBatch.DIARIO, proc);
				log.WriteLogMonitor("EJECUCION BATCH: TaskBatchExecuteDay: " + res.substring(3), TypeMonitor.monitor, null);
				
			}else {
				log.WriteLogMonitor("No se puede recuperar parametros BatchDay NULL", TypeMonitor.monitor, null);
			}
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo TaskBatchExecuteDay::run()", TypeMonitor.error, e);
			log.WriteLogMonitor("PROBLEMAS EJECUCION BATCH: TaskBatchExecuteDay:  " + e.getMessage() , TypeMonitor.error, null);
		}
	}

}
