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

public class TrxCf_Join_Cri extends PrincipalTrx {

	private String tab_name;
	private String tab_alias;
	private String join_cri_name;
	private String join_cri_alias;
	private String cond;
	private String val;
	private String ord;
	private String tipo;
	
	public TrxCf_Join_Cri() {
		super();
		
	}
	public TrxCf_Join_Cri(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk) {
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
	public String getCond() {
		return cond;
	}
	public void setCond(String cond) {
		this.cond = cond;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public String getOrd() {
		return ord;
	}
	public void setOrd(String ord) {
		this.ord = ord;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public List<TrxCf_Join_Cri> getTrxCf_JoinCriListObject(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk){
		List<TrxCf_Join_Cri> lista = null;
		Logger log = null;
		try {
			
			lista =   MemoryGlobal.ListTrxCf_JoinCriMem.stream().
					 filter(p -> p.getSubsystem_pk().equals(subsystem_pk)
							 && p.getTransaction_pk().equals(transaction_pk)
							 && p.getVersion_pk().equals(version_pk)
							 && p.getTip_pk().equals(tip_pk))
					.peek(Objects::requireNonNull)
					.collect(Collectors.toList());
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo TrxCf_Join_Cri::getTrxCf_JoinCriListObject ", TypeMonitor.error, e);
		}
		return lista;
	}
	
	public Runnable getDataJoinCri(){
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				Logger log;
				Connection conn = null;
				try {
					
					conn = DBCPDataSource.getConnection();
					String query = "SELECT * FROM TRXCF_JOIN_CRI";		
					DataSetMemoryLoader<TrxCf_Join_Cri> loader = 
				    new DataSetMemoryLoader<TrxCf_Join_Cri>
					(conn, TrxCf_Join_Cri.class, query);
					MemoryGlobal.ListTrxCf_JoinCriMem = loader.LoadDataClass();		
					
				} catch (SQLException e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TrxCf_Join_Cri::getDataJoinCri() ", TypeMonitor.error, e);
					
				} catch (Exception e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TrxCf_Join_Cri::getDataJoinCri() ", TypeMonitor.error, e);
					
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
