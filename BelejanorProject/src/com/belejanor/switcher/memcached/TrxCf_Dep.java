package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;

public class TrxCf_Dep extends PrincipalTrx {

	private String tab_name;
	private String tab_alias;
	private String join_cri_name;
	private String join_cri_alias;
	private String alias_desde;
	private String alias_hacia;
	private String desde;
	private String hacia;
	private String val;
	
	public TrxCf_Dep() {
		super();
		
	}
	public TrxCf_Dep(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk) {
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
	public String getJoin_cri_name() {
		return join_cri_name;
	}
	public void setJoin_cri_name(String join_cri_name) {
		this.join_cri_name = join_cri_name;
	}
	public String getJoin_cri_alias() {
		return join_cri_alias;
	}
	public void setJoin_cri_alias(String join_cri_alias) {
		this.join_cri_alias = join_cri_alias;
	}
	public String getAlias_desde() {
		return alias_desde;
	}
	public void setAlias_desde(String alias_desde) {
		this.alias_desde = alias_desde;
	}
	public String getAlias_hacia() {
		return alias_hacia;
	}
	public void setAlias_hacia(String alias_hacia) {
		this.alias_hacia = alias_hacia;
	}
	public String getDesde() {
		return desde;
	}
	public void setDesde(String desde) {
		this.desde = desde;
	}
	public String getHacia() {
		return hacia;
	}
	public void setHacia(String hacia) {
		this.hacia = hacia;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	
	public List<TrxCf_Dep> getTrxCf_DepListObject(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk){
		List<TrxCf_Dep> lista = null;
		Logger log = null;
		try {
			
			lista =   MemoryGlobal.ListTrxCf_Dep.stream().
					filter(p -> p.getSubsystem_pk().equals(subsystem_pk)
							 && p.getTransaction_pk().equals(transaction_pk)
							 && p.getVersion_pk().equals(version_pk)
							 && p.getTip_pk().equals(tip_pk))
					.peek(Objects::requireNonNull)
					.collect(Collectors.toList());
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo TrxCf_Dep::getTrxCf_DepListObject ", TypeMonitor.error, e);
		}
		return lista;
	}
	
	public Runnable getDataDep(){
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				
				Logger log;
				Connection conn = null;
				try {
					
					conn = DBCPDataSource.getConnection();
					String query = "SELECT * FROM TRXCF_DEP";		
					DataSetMemoryLoader<TrxCf_Dep> loader = 
				    new DataSetMemoryLoader<TrxCf_Dep>
					(conn, TrxCf_Dep.class, query);
					MemoryGlobal.ListTrxCf_Dep = loader.LoadDataClass();
					
				} catch (SQLException e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TrxCf_Dep::getDataDep() ", TypeMonitor.error, e);
					
				} catch (Exception e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TrxCf_Dep::getDataDep() ", TypeMonitor.error, e);
					
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
