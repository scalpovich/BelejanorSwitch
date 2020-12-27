package com.belejanor.switcher.logger;

import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;


public class Logger{

	public void WriteLog(final Object mensaje, TypeLog typeLog, TypeWriteLog typeWriteLog){
		LoggerConfig log = new LoggerConfig(mensaje, typeLog, typeWriteLog);
		log.start();
	}
	public void WriteLogMonitor(final String message, TypeMonitor type, Exception ex){
		LoggerConfig log = new LoggerConfig();
		Thread tr = new Thread(log.WriteMonitorRun(message, type, ex));
		tr.start();
	}
	public void WriteOptimizeLog(final Object mensaje, TypeLog typeLog, Class<?> classe, boolean Serializable){
		LoggerConfig log = new LoggerConfig(mensaje,classe,typeLog, Serializable);
		Thread tr = new Thread(log.WriteOptimizeLogging());
		tr.start();
	}
	public void WriteOptimizeLog(final Object mensaje, TypeLog typeLog, Class<?> classe, boolean Serializable, int idaVuelta){
		LoggerConfig log = new LoggerConfig(mensaje,classe,typeLog, Serializable, idaVuelta);
		Thread tr = new Thread(log.WriteOptimizeLogging());
		tr.start();
	}
}
