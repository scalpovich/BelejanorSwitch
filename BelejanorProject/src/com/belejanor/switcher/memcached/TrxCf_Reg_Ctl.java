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

public class TrxCf_Reg_Ctl extends PrincipalTrx {

	private String tab_Name;
	private String tab_Alias;
	private int reg_Nro;
	public TrxCf_Reg_Ctl() {
		super();
		
	}
	public TrxCf_Reg_Ctl(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk) {
		super(subsystem_pk, transaction_pk, version_pk, tip_pk);
		
	}
	
	public String getTab_Name() {
		return tab_Name;
	}
	public void setTab_Name(String tab_Name) {
		this.tab_Name = tab_Name;
	}
	public String getTab_Alias() {
		return tab_Alias;
	}
	public void setTab_Alias(String tab_Alias) {
		this.tab_Alias = tab_Alias;
	}
	public int getReg_Nro() {
		return reg_Nro;
	}
	public void setReg_Nro(int reg_Nro) {
		this.reg_Nro = reg_Nro;
	}
	public List<TrxCf_Reg_Ctl> getTrxCf_RegCtlListObject(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk){
		List<TrxCf_Reg_Ctl> lista = null;
		try {
			
			lista =   MemoryGlobal.ListTrxCf_RegCtlMem.stream().
					filter(p -> p.getSubsystem_pk().equals(subsystem_pk)
							 && p.getTransaction_pk().equals(transaction_pk)
							 && p.getVersion_pk().equals(version_pk)
							 && p.getTip_pk().equals(tip_pk))
					.peek(Objects::requireNonNull)
					.collect(Collectors.toList());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public Runnable getDataRegCtl(){
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				Logger log;
				Connection conn = null;
				try {
					
					conn = DBCPDataSource.getConnection();
					String query = "SELECT * FROM TRXCF_REG_CTL";		
					DataSetMemoryLoader<TrxCf_Reg_Ctl> loader = 
				    new DataSetMemoryLoader<TrxCf_Reg_Ctl>
					(conn, TrxCf_Reg_Ctl.class, query);
					MemoryGlobal.ListTrxCf_RegCtlMem = loader.LoadDataClass();	
					
				} catch (SQLException e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TrxCf_Reg_Ctl::getDataRegCtl() ", TypeMonitor.error, e);
					
				} catch (Exception e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TrxCf_Reg_Ctl::getDataRegCtl() ", TypeMonitor.error, e);
					
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
