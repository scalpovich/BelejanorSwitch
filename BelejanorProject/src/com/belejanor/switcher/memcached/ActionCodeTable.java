package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class ActionCodeTable extends Thread {

	private String act_codIso;
	private String act_desIso;
	private String act_codFit;
	private String act_desFit;
	public ActionCodeTable() {
		
	}
	public String getAct_codIso() {
		return act_codIso;
	}
	public void setAct_codIso(String act_codIso) {
		this.act_codIso = act_codIso;
	}
	public String getAct_desIso() {
		return act_desIso;
	}
	public void setAct_desIso(String act_desIso) {
		this.act_desIso = act_desIso;
	}
	public String getAct_codFit() {
		return act_codFit;
	}
	public void setAct_codFit(String act_codFit) {
		this.act_codFit = act_codFit;
	}
	public String getAct_desFit() {
		return act_desFit;
	}
	public void setAct_desFit(String act_desFit) {
		this.act_desFit = act_desFit;
	}
	
	@Override
	public String toString() {
		return "ActionCodeTable [act_codIso=" + act_codIso + ", act_desIso="
				+ act_desIso + ", act_codFit=" + act_codFit + ", act_desFit="
				+ act_desFit + "]";
	}
	public ActionCodeTable getCodErrorFromIsoList(String CodIso){
		Logger log = null;
		ActionCodeTable actionCode = null;
		try {
			
			actionCode =   MemoryGlobal.ListActionCodeTableMem.stream().
					 filter(p -> p.getAct_codIso().equals(CodIso))
				     .findFirst().get();			  			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo ActionCodeTable::getCodErrorFromIsoList ", TypeMonitor.error, e);
		}
		return actionCode;
	}
	public ActionCodeTable getCodErrorFromFitCodeList(String CodFit){
		ActionCodeTable actionCode = null;
		Logger log = null;
		try {
			
			actionCode =   MemoryGlobal.ListActionCodeTableMem.stream().
					filter(p -> p.getAct_codFit().equals(CodFit))
					.findFirst().orElseGet(()-> null);
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo ActionCodeTable::getCodErrorFromFitCodeList ", TypeMonitor.error, e);
		}
		return actionCode;
	}
	@Override
	public void run() {	
		
		Logger log;
		Connection conn = null;
		try {
			
			conn = DBCPDataSource.getConnection();
			String query = "SELECT * FROM ACTIONCODE_TABLE";		
			DataSetMemoryLoader<ActionCodeTable> loader = 
		    new DataSetMemoryLoader<ActionCodeTable>
			(conn, ActionCodeTable.class, query);
			MemoryGlobal.ListActionCodeTableMem = loader.LoadDataClass();
			
			log = new Logger();
			log.WriteLogMonitor("[MEM_CAHED]: Cargando CODIGOS DE ERROR SISTEMA...", TypeMonitor.monitor, null);
			
			
		} catch (SQLException e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo ActionCodeTable::run() ", TypeMonitor.error, e);
			
		}catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo ActionCodeTable::run() ", TypeMonitor.error, e);
			
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

