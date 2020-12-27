package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class TransactionNotifications {

	private String proccode;
	private String canal_cod;
	private int net_id;
	private double ammountdebit;
	private String tran_process;
	private String active;
	private String isaffect;
	private String params1;
	private String params2;
	
	public TransactionNotifications() {
		
		super();
	}

	public String getProccode() {
		return proccode;
	}

	public void setProccode(String proccode) {
		this.proccode = proccode;
	}

	public String getCanal_cod() {
		return canal_cod;
	}
	
	public void setCanal_cod(String canal_cod) {
		this.canal_cod = canal_cod;
	}

	public int getNet_id() {
		return net_id;
	}

	public void setNet_id(int net_id) {
		this.net_id = net_id;
	}

	public double getAmmountdebit() {
		return ammountdebit;
	}

	public void setAmmountdebit(double ammountdebit) {
		this.ammountdebit = ammountdebit;
	}

	public String getTran_process() {
		return tran_process;
	}
	
	public void setTran_process(String tran_process) {
		this.tran_process = tran_process;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getIsaffect() {
		return isaffect;
	}

	public void setIsaffect(String isaffect) {
		this.isaffect = isaffect;
	}

	public String getParams1() {
		return params1;
	}

	public void setParams1(String params1) {
		this.params1 = params1;
	}

	public String getParams2() {
		return params2;
	}

	public void setParams2(String params2) {
		this.params2 = params2;
	}

	public List<TransactionNotifications> getTransactionNotificationsObject(String procCode, String canalCod, 
        int netId, double ammount){
		
		List<TransactionNotifications> lista = null;
		Logger log = null;
		try {
		
			lista =   MemoryGlobal.listTransactionNotifications.stream().
			filter(p -> p.getProccode().equals(procCode)
			&& p.getCanal_cod().equals(canalCod)
			&& p.getNet_id() == netId
			&& p.getAmmountdebit() == ammount)
			.peek(Objects::requireNonNull)
			.collect(Collectors.toList());

		} catch (Exception e) {

			log = new Logger();
			log.WriteLogMonitor("Error modulo TransactionCommands::getTransactionNotificationsObject ", TypeMonitor.error, e);
		}
		return lista;
	}

	public Runnable getDataTransactionNotifications(){
		Runnable runnable = new Runnable() {
		@Override
		public void run() {
					
			Logger log;
			Connection conn = null;
			try {
				
				conn = DBCPDataSource.getConnection();
				String query = "SELECT * FROM TRANSACTION_NOTIFICATION";		
				DataSetMemoryLoader<TransactionNotifications> loader = 
				new DataSetMemoryLoader<TransactionNotifications>
				(conn, TransactionNotifications.class, query);
				MemoryGlobal.listTransactionNotifications = loader.LoadDataClass();
				
				log = new Logger();
				log.WriteLogMonitor("[MEM_CAHED]: Cargando INFORMACION A NOTIFICAR...", TypeMonitor.monitor, null);
				
			} catch (SQLException e) {
				
				log = new Logger();
				log.WriteLogMonitor("Error modulo TransactionNotifications::getDataTransactionNotifications() ", TypeMonitor.error, e);
				
			} catch (Exception e) {
				
				log = new Logger();
				log.WriteLogMonitor("Error modulo TransactionNotifications::getDataTransactionNotifications() ", TypeMonitor.error, e);
				
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
