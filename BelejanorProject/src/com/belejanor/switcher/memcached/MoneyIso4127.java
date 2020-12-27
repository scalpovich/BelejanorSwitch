package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class MoneyIso4127 extends Thread {

	private String cod_money;
	private String abrev_money;
	private String desc_money;
	
	public MoneyIso4127(){
		
	}

	public String getCod_money() {
		return cod_money;
	}

	public void setCod_money(String cod_money) {
		this.cod_money = cod_money;
	}

	public String getAbrev_money() {
		return abrev_money;
	}

	public void setAbrev_money(String abrev_money) {
		this.abrev_money = abrev_money;
	}

	public String getDesc_money() {
		return desc_money;
	}

	public void setDesc_money(String desc_money) {
		this.desc_money = desc_money;
	}
	
	public MoneyIso4127 getCodMoneyFromIsoList(String moneyAbrev){
		Logger log = null;
		MoneyIso4127 money = null;
		try {
			
			money =   MemoryGlobal.ListMoneyIsoMem.stream().
					 filter(p -> p.abrev_money.equals(moneyAbrev))
				     .findFirst().get();			  			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo MoneyIso4127::getCodMoneyFromIsoList ", TypeMonitor.error, e);
		}
		return money;
	}
	public static String getCodMoneyFromFitCodeMoney(String moneyAbrev){
		String money = null;
		Logger log = null;
		try {
			
			money =   MemoryGlobal.ListMoneyIsoMem.stream().
					  filter(p -> p.abrev_money.equals(moneyAbrev))
					  .findFirst().orElseGet(()-> null).getCod_money();
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo MoneyIso4127::getCodMoneyFromFitCodeMoney ", TypeMonitor.error, e);
		}
		return money;
	}
	
	public static String getAbrevMoneyFromCredencialCodeMoney(String codMoney){
		String money = null;
		Logger log = null;
		try {
			
			money =   MemoryGlobal.ListMoneyIsoMem.stream().
					  filter(p -> p.cod_money.equals(codMoney))
					  .findFirst().orElseGet(()-> null).getAbrev_money();
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo MoneyIso4127::getAbrevMoneyFromCredencialCodeMoney ", TypeMonitor.error, e);
		}
		return money;
	}
	
	@Override
	public void run() {	
		
		
		Logger log;
		Connection conn = null;
		try {
			
			conn = DBCPDataSource.getConnection();
			String query = "SELECT * FROM MONEY_ISO4127";		
			DataSetMemoryLoader<MoneyIso4127> loader = 
		    new DataSetMemoryLoader<MoneyIso4127>
			(conn, MoneyIso4127.class, query);
			MemoryGlobal.ListMoneyIsoMem = loader.LoadDataClass();
			
			log = new Logger();
			log.WriteLogMonitor("[MEM_CAHED]: Cargando MONEDAS TRANSACCIONALES ISO_4127...", TypeMonitor.monitor, null);
			
		} catch (SQLException e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo MoneyIso4127::run() ", TypeMonitor.error, e);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo MoneyIso4127::run() ", TypeMonitor.error, e);
			
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
