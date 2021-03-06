package com.belejanor.switcher.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.BatchDay;
import com.belejanor.switcher.memcached.MemoryGlobal;


public class InitSchedule {

	Logger log;
	public InitSchedule() {
		
		log = new Logger();
	}
	
	public void chargeTask() {
		
		try {
			
			int count = 1;
			for (BatchDay batch : MemoryGlobal.ListBatchDayTableMem) {
				
				String messageClass = batch.getMethod_call();					
				List<String> aa = Arrays.asList(messageClass.split("\\."));
				String methodName = aa.get(aa.size() -1);
				String classname = messageClass.replace("." + methodName, "");
				
				Class<?> instanceClass = Class.forName(classname);																					
				
				@SuppressWarnings("unchecked")
				JobDetail job = JobBuilder.newJob((Class<? extends Job>) instanceClass).withIdentity("myjob" + String.valueOf(count)).build();

				String cron = batch.getSegundo() + " " + batch.getMinuto() + " " + batch.getHora() + " * * ?";
	            Trigger trigger = TriggerBuilder
	            		.newTrigger()
	            		.withIdentity("dummyTriggerName" + String.valueOf(count), "group1" + String.valueOf(count))
	            		.withSchedule(
	            			CronScheduleBuilder.cronSchedule(cron))
	            		.build();

	            SchedulerFactory schFactory = new StdSchedulerFactory();
	            Scheduler scheduler = schFactory.getScheduler(); 
	            scheduler.start();
	            scheduler.scheduleJob(job, trigger);

		        
		        log.WriteLogMonitor("[SCHEDULER]: Cargando TASK:... {" + batch.getTask_name() + "}" , TypeMonitor.monitor, null);
		        count++;
			}
			
			
		} catch (Exception e) {

			log.WriteLogMonitor("Error modulo InitSchedule::chargeTask", TypeMonitor.error, e);
		}
	}
	
	public Runnable runChargeTask() {
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				chargeTask();
			}
		};
		return run;
	}
}
