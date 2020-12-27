package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class User_Channel extends Thread{

	private String canal_cod;
	private String canal_term;
	private String user_fit;
	private String user_pass;
	private String uch_description;
	private String uch_direction;
	private String uch_sucursal;
	private String uch_oficina;
	public String getCanal_cod() {
		return canal_cod;
	}
	public void setCanal_cod(String canal_cod) {
		this.canal_cod = canal_cod;
	}
	public String getCanal_term() {
		return canal_term;
	}
	public void setCanal_term(String canal_term) {
		this.canal_term = canal_term;
	}
	public String getUser_fit() {
		return user_fit;
	}
	public void setUser_fit(String user_fit) {
		this.user_fit = user_fit;
	}
	public String getUser_pass() {
		return user_pass;
	}
	public void setUser_pass(String user_pass) {
		this.user_pass = user_pass;
	}
	public String getUch_description() {
		return uch_description;
	}
	public void setUch_description(String uch_description) {
		this.uch_description = uch_description;
	}
	public String getUch_direction() {
		return uch_direction;
	}
	public void setUch_direction(String uch_direction) {
		this.uch_direction = uch_direction;
	}
	public String getUch_sucursal() {
		return uch_sucursal;
	}
	public void setUch_sucursal(String uch_sucursal) {
		this.uch_sucursal = uch_sucursal;
	}
	public String getUch_oficina() {
		return uch_oficina;
	}
	public void setUch_oficina(String uch_oficina) {
		this.uch_oficina = uch_oficina;
	}
	
	public User_Channel() {
		super();
	}
	
	public User_Channel getUserChannel(String cod_canal, String cod_terminal41){
		User_Channel usrChannel = null;
		Logger log= null;
		try {
			
			usrChannel =   MemoryGlobal.ListUserChannel.stream().
					 filter(p -> p.getCanal_cod().equalsIgnoreCase(cod_canal) 
							&& p.getCanal_term().equalsIgnoreCase(cod_terminal41))
					.findFirst().orElseGet(() -> null);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo User_Channel::getUserChannel ", TypeMonitor.error, e);
		}
		return usrChannel;
	}
	
	@Override
	public void run() {	
		
		Logger log;
		Connection conn = null;
		try {
			
			conn = DBCPDataSource.getConnection();
			String query = "SELECT * FROM USER_CHANNEL";		
			DataSetMemoryLoader<User_Channel> loader = 
		    new DataSetMemoryLoader<User_Channel>
			(conn, User_Channel.class, query);
			MemoryGlobal.ListUserChannel = loader.LoadDataClass();
			
			log = new Logger();
			log.WriteLogMonitor("[MEM_CAHED]: Cargando INFORMACION DE CANALES TERMINALES (ATMS, POS)...", TypeMonitor.monitor, null);
			
		} catch (SQLException e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo User_Channel::run() ", TypeMonitor.error, e);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo User_Channel::run() ", TypeMonitor.error, e);
			
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
