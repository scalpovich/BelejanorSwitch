package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class MailSmsTypes {

	private int type_code;
	private String type_sender;
	private String type_text_message;
	private String type_iso_corresponse;
	
	public MailSmsTypes() {
		
		super();
	}

	public int getType_code() {
		return type_code;
	}

	public void setType_code(int type_code) {
		this.type_code = type_code;
	}

	public String getType_sender() {
		return type_sender;
	}

	public void setType_sender(String type_sender) {
		this.type_sender = type_sender;
	}

	public String getType_text_message() {
		return type_text_message;
	}

	public void setType_text_message(String type_text_message) {
		this.type_text_message = type_text_message;
	}

	public String getType_iso_corresponse() {
		return type_iso_corresponse;
	}

	public void setType_iso_corresponse(String type_iso_corresponse) {
		this.type_iso_corresponse = type_iso_corresponse;
	}
	
	public MailSmsTypes getMailSmsTypes(int codType, String typeSender){
		MailSmsTypes smsMail = null;
		Logger log= null;
		try {
			
			smsMail =   MemoryGlobal.listMailSms.stream().
					 filter(p -> p.type_code == codType && 
					 p.type_sender .equalsIgnoreCase(typeSender))
					.findFirst().orElseGet(() -> null);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo ErrorToIso::getMailSmsTypes ", TypeMonitor.error, e);
		}
		return smsMail;
	}
	
	public Runnable getDataMailSmsTypes(){
		Runnable runnable = new Runnable() {
		@Override
		public void run() {
		
			Logger log;
			Connection conn = null;
			
			try {
				
				conn = DBCPDataSource.getConnection();
				String query = "SELECT * FROM MAIL_SMS_TYPES";		
				DataSetMemoryLoader<MailSmsTypes> loader = 
				new DataSetMemoryLoader<MailSmsTypes>
				(conn, MailSmsTypes.class, query);
				MemoryGlobal.listMailSms = loader.LoadDataClass();	
				
				log = new Logger();
				log.WriteLogMonitor("[MEM_CAHED]: Cargando CONFIGURACION DE NOTIFICACION (SMS, MAIL)...", TypeMonitor.monitor, null);
				
			} catch (Exception e) {
				
				log = new Logger();
				log.WriteLogMonitor("Error modulo MailSmsTypes::getDataMailSmsTypes() ", TypeMonitor.error, e);
				
			} finally {
				
				try {
					if(conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	};	
	
	    return runnable;
	}

	
}
