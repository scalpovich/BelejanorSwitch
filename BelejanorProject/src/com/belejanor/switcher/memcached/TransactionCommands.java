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

public class TransactionCommands {

	private String proccode;
	private String canal_cod;
	private int net_id;
	private double ammountdebit;
	private String command;
	private int activo;
	private int orden;
	private int paralelo;
	private String retorno;
	private int securandom;
	
	public TransactionCommands(){
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

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getActivo() {
		return activo;
	}

	public void setActivo(int activo) {
		this.activo = activo;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public int getParalelo() {
		return paralelo;
	}

	public void setParalelo(int paralelo) {
		this.paralelo = paralelo;
	}

	public String getRetorno() {
		return retorno;
	}

	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}
	
	public int getSecurandom() {
		return securandom;
	}

	public void setSecurandom(int securandom) {
		this.securandom = securandom;
	}

	public List<TransactionCommands> getTransactionCommandsObject(String procCode, String canalCod, 
			                                      int netId, double ammount){
		List<TransactionCommands> lista = null;
		Logger log = null;
		try {
			
			lista =   MemoryGlobal.listTransactionCommands.stream().
					filter(p -> p.getProccode().equals(procCode)
							 && p.getCanal_cod().equals(canalCod)
							 && p.getNet_id() == netId
							 && p.getAmmountdebit() == ammount)
					.peek(Objects::requireNonNull)
					.collect(Collectors.toList());
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo TransactionCommands::getTransactionCommandsObject ", TypeMonitor.error, e);
		}
		return lista;
	}
	
	public Runnable getDataTransactionCommands(){
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				Logger log;
				Connection conn = null;
				try {
					
					conn = DBCPDataSource.getConnection();
					String query = "SELECT * FROM TRANSACTION_COMMANDS";		
					DataSetMemoryLoader<TransactionCommands> loader = 
				    new DataSetMemoryLoader<TransactionCommands>
					(conn, TransactionCommands.class, query);
					MemoryGlobal.listTransactionCommands = loader.LoadDataClass();	
					log = new Logger();
					log.WriteLogMonitor("[MEM_CAHED]: Cargando COMANDOS REFLEXIVOS...", TypeMonitor.monitor, null);
					
				} catch (SQLException e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TransactionCommands::getDataTransactionCommands() ", TypeMonitor.error, e);
					
				} catch (Exception e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TransactionCommands::getDataTransactionCommands() ", TypeMonitor.error, e);
					
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
