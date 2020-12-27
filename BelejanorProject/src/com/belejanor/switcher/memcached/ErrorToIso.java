package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class ErrorToIso extends Thread{

	private String code_1;
	private String description_1;
	private String code_0;
	private String description_2;
	
	public ErrorToIso() {
		super();
	}
	
	public String getCode_1() {
		return code_1;
	}

	public void setCode_1(String code_1) {
		this.code_1 = code_1;
	}

	public String getDescription_1() {
		return description_1;
	}

	public void setDescription_1(String description_1) {
		this.description_1 = description_1;
	}

	public String getCode_0() {
		return code_0;
	}

	public void setCode_0(String code_0) {
		this.code_0 = code_0;
	}

	public String getDescription_2() {
		return description_2;
	}

	public void setDescription_2(String description_2) {
		this.description_2 = description_2;
	}

	public ErrorToIso getErrorsToIso(String error){
		ErrorToIso errors = null;
		Logger log= null;
		try {
			
			errors =   MemoryGlobal.ListErrorsToIso.stream().
					 filter(p -> p.getCode_1().equals(error))
					.findFirst().orElseGet(() -> null);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo ErrorToIso::getErrorsToIso ", TypeMonitor.error, e);
		}
		return errors;
	}
	public String getCodeErrorIso01(String error){
		String codeError = null;
		ErrorToIso errors = null;
		Logger log= null;
		try {
			
			errors =   MemoryGlobal.ListErrorsToIso.stream().
					 filter(p -> p.getCode_1().equals(error))
					.findFirst().orElseGet(() -> null);
			if(errors != null){
				
				codeError = errors.getCode_0() + "|" + 
				errors.getDescription_2().toUpperCase();
				
			}else{
				
				codeError = "12|TRANSACCION INVALIDA";
			}
			
		} catch (Exception e) {
			
			codeError = "96|ERROR EN PROCESOS";
			log = new Logger();
			log.WriteLogMonitor("Error modulo ErrorToIso::getErrorsToIso ", TypeMonitor.error, e);
		}
		return codeError;
	}
	
	@Override
	public void run() {	
		
		Logger log;
		Connection conn = null;
		
		try {
			
			conn = DBCPDataSource.getConnection();
			String query = "SELECT * FROM ERROR_ISO1_TO_ISO2_CODES";		
			DataSetMemoryLoader<ErrorToIso> loader = 
		    new DataSetMemoryLoader<ErrorToIso>
			(conn, ErrorToIso.class, query);
			MemoryGlobal.ListErrorsToIso = loader.LoadDataClass();	
			
			log = new Logger();
			log.WriteLogMonitor("[MEM_CAHED]: Cargando CODIGOS ISO8583 BINARIOS...", TypeMonitor.monitor, null);
			
		} catch (SQLException e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo ErrorToIso::run() ", TypeMonitor.error, e);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo ErrorToIso::run() ", TypeMonitor.error, e);
			
		} finally {
			
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
	}	
}
