package com.belejanor.switcher.memcached;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class TrxCf_Dep_Table extends PrincipalTrx{

	private String tab_name;
	private String tab_alias;
	private String alias_desde;
	private String desde;
	private String alias_hacia;
	private String hacia;
	private String value;
	
	public TrxCf_Dep_Table() {
		super();
	}
	
	public TrxCf_Dep_Table(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk) {
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

	public String getAlias_desde() {
		return alias_desde;
	}

	public void setAlias_desde(String alias_desde) {
		this.alias_desde = alias_desde;
	}

	public String getDesde() {
		return desde;
	}

	public void setDesde(String desde) {
		this.desde = desde;
	}

	public String getAlias_hacia() {
		return alias_hacia;
	}

	public void setAlias_hacia(String alias_hacia) {
		this.alias_hacia = alias_hacia;
	}

	public String getHacia() {
		return hacia;
	}

	public void setHacia(String hacia) {
		this.hacia = hacia;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<TrxCf_Dep_Table> getTrxCf_Cam_Table_ListObject(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk){
		List<TrxCf_Dep_Table> lista = null;
		Logger log = null;
		try {
			
			lista =   MemoryGlobal.ListTrxCf_DepTable.stream().
					filter(p -> p.getSubsystem_pk().equals(subsystem_pk)
							 && p.getTransaction_pk().equals(transaction_pk)
							 && p.getVersion_pk().equals(version_pk)
							 && p.getTip_pk().equals(tip_pk))
					.peek(Objects::requireNonNull)
					.collect(Collectors.toList());
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo TrxCf_Cam_Table::getTrxCf_Cam_Table_ListObject ", TypeMonitor.error, e);
		}
		return lista;
	}
	
	public Runnable getDataTableCam(){
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				String query = "SELECT * FROM TRXCF_DEP_TABLE";		
				DataSetMemoryLoader<TrxCf_Dep_Table> loader = 
			    new DataSetMemoryLoader<TrxCf_Dep_Table>
				(MemoryGlobal.conn, TrxCf_Dep_Table.class, query);
				MemoryGlobal.ListTrxCf_DepTable = loader.LoadDataClass();	
			}
		};	
	
	  return runnable;
	}
	
	
}
