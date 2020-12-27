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

public class TrxCf_Table extends PrincipalTrx {

	private String tab_name;
	private String tab_alias;
	private String readonly;
	private String distinct;
	private String blq;
	private String mpg;
	private String ract;
	private String npg;
	private String nrg;
	private String financial;
	
	public TrxCf_Table() {
		super();
	}
	public TrxCf_Table(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk) {
		super(subsystem_pk, transaction_pk, version_pk, tip_pk);
		
	}
	public String getTab_name() {
		return tab_name;
	}
	public void setTab_name(String tab_name) {
		this.tab_name = tab_name;
	}
	public String getTab_alias() {
		return tab_alias;
	}
	public void setTab_alias(String tab_alias) {
		this.tab_alias = tab_alias;
	}
	public String getReadonly() {
		return readonly;
	}
	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}
	public String getDistinct() {
		return distinct;
	}
	public void setDistinct(String distinct) {
		this.distinct = distinct;
	}
	public String getBlq() {
		return blq;
	}
	public void setBlq(String blq) {
		this.blq = blq;
	}
	public String getMpg() {
		return mpg;
	}
	public void setMpg(String mpg) {
		this.mpg = mpg;
	}
	public String getRact() {
		return ract;
	}
	public void setRact(String ract) {
		this.ract = ract;
	}
	public String getNpg() {
		return npg;
	}
	public void setNpg(String npg) {
		this.npg = npg;
	}
	public String getNrg() {
		return nrg;
	}
	public void setNrg(String nrg) {
		this.nrg = nrg;
	}
	public String getFinancial() {
		return financial;
	}
	public void setFinancial(String financial) {
		this.financial = financial;
	}
	
	public Runnable getDataTable(){
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				Logger log;
				Connection conn = null;
				try {
					
					conn = DBCPDataSource.getConnection();
					String query = "SELECT * FROM TRXCF_TABLE";		
					DataSetMemoryLoader<TrxCf_Table> loader = 
				    new DataSetMemoryLoader<TrxCf_Table>
					(conn, TrxCf_Table.class, query);
					MemoryGlobal.ListTrxCf_TableMem = loader.LoadDataClass();	
					
				} catch (SQLException e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TrxCf_Table::getDataTable() ", TypeMonitor.error, e);
					
				} catch (Exception e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TrxCf_Table::getDataTable() ", TypeMonitor.error, e);
					
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
	
    public List<TrxCf_Table> getTrxCf_TableListObject(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk){
		List<TrxCf_Table> lista = null;
		Logger log = null;
		try {
			
			lista =   MemoryGlobal.ListTrxCf_TableMem.stream().
					filter(p -> p.getSubsystem_pk().equals(subsystem_pk)
							 && p.getTransaction_pk().equals(transaction_pk)
							 && p.getVersion_pk().equals(version_pk)
							 && p.getTip_pk().equals(tip_pk))
					.peek(Objects::requireNonNull)
					.collect(Collectors.toList());
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo TrxCf_Table::getTrxCf_TableListObject ", TypeMonitor.error, e);
		}
		return lista;
	}
	
	
}
