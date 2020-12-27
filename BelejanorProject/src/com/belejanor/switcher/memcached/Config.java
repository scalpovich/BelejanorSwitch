package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class Config extends Thread{

	private String cfg_Id;
	private String cfg_Argumento;
	private String cfg_Valor;
	public String getCfg_Id() {
		return cfg_Id;
	}
	public void setCfg_Id(String cfg_Id) {
		this.cfg_Id = cfg_Id;
	}
	public String getCfg_Argumento() {
		return cfg_Argumento;
	}
	public void setCfg_Argumento(String cfg_Argumento) {
		this.cfg_Argumento = cfg_Argumento;
	}
	public String getCfg_Valor() {
		return cfg_Valor;
	}
	public void setCfg_Valor(String cfg_Valor) {
		this.cfg_Valor = cfg_Valor;
	}
	@Override
	public String toString() {
		return "Config [cfg_Id=" + cfg_Id + ", cfg_Argumento=" + cfg_Argumento
				+ ", cfg_Valor=" + cfg_Valor + "]";
	}
	public Config() {		
	}
	
	public Config getConfigSystem(String argumento){
		Config config = null;
		Logger log= null;
		try {
			
			config =   MemoryGlobal.ListConfigSystemMem.stream().
					 filter(p -> p.getCfg_Argumento().equals(argumento))
					.findFirst().orElseGet(() -> null);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo Config::getConfigSystem ", TypeMonitor.error, e);
		}
		return config;
	}
	
	@Override
	public void run() {	
		
		Logger log;
		Connection conn = null;
		try {
			
			conn = DBCPDataSource.getConnection();
			String query = "SELECT * FROM CONFIG";		
			DataSetMemoryLoader<Config> loader = 
		    new DataSetMemoryLoader<Config>
			(conn, Config.class, query);
			MemoryGlobal.ListConfigSystemMem = loader.LoadDataClass();	
			
			log = new Logger();
			log.WriteLogMonitor("[MEM_CAHED]: Cargando CONFIGURACIONES ADICIONALES...", TypeMonitor.monitor, null);
			
		} catch (SQLException e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo Config::run() ", TypeMonitor.error, e);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo Config::run() ", TypeMonitor.error, e);
			
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

