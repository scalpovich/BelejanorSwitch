package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class BatchDay extends Thread{

	private int hora;
	private int minuto;
	private int segundo;
	private int frecuencia;
	private String task_name;
	private String method_call;
	private String params_call;
	
    public BatchDay() {
		
	}
	
	
	public int getHora() {
		return hora;
	}



	public void setHora(int hora) {
		this.hora = hora;
	}



	public int getMinuto() {
		return minuto;
	}



	public void setMinuto(int minuto) {
		this.minuto = minuto;
	}



	public int getSegundo() {
		return segundo;
	}



	public void setSegundo(int segundo) {
		this.segundo = segundo;
	}



	public int getFrecuencia() {
		return frecuencia;
	}



	public void setFrecuencia(int frecuencia) {
		this.frecuencia = frecuencia;
	}



	public String getTask_name() {
		return task_name;
	}



	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}



	public String getMethod_call() {
		return method_call;
	}



	public void setMethod_call(String method_call) {
		this.method_call = method_call;
	}



	public String getParams_call() {
		return params_call;
	}



	public void setParams_call(String params_call) {
		this.params_call = params_call;
	}

	public BatchDay getParamsBatch(String param){
		BatchDay batch = null;
		Logger log = null;
		try {
			
			batch =   MemoryGlobal.ListBatchDayTableMem.stream().
					filter(p -> p.getParams_call().contains(param))
					.findFirst().orElseGet(()-> null);
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo BatchDay::getParamsBatch ", TypeMonitor.error, e);
		}
		return batch;
	}

	@Override
	public void run() {	
		
		Logger log;
		Connection conn = null;
		try {
			
			conn = DBCPDataSource.getConnection();
			String query = "SELECT * FROM BATCH_DAY";		
			DataSetMemoryLoader<BatchDay> loader = 
		    new DataSetMemoryLoader<BatchDay>
			(conn, BatchDay.class, query);
			MemoryGlobal.ListBatchDayTableMem = loader.LoadDataClass();
			
			log = new Logger();
			log.WriteLogMonitor("[MEM_CAHED]: Cargando TAREAS BATCH...", TypeMonitor.monitor, null);
			
			
		} catch (SQLException e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo BatchDay::run() ", TypeMonitor.error, e);
			
		}catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo BatchDay::run() ", TypeMonitor.error, e);
			
		}finally {
			
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
	}			
}
