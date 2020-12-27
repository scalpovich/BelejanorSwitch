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

public class TrxCf_DepCam extends PrincipalTrx {

	private String tab_name;
	private String tab_alias;
	private int reg_nro;
	private String cam_name;
	private String cam_alias;
	private String alias_desde;
	private String alias_hacia;
	private String desde;
	private String hacia;
	private String val;
	public TrxCf_DepCam() {
		super();
		
	}
	public TrxCf_DepCam(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk) {
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
	public int getReg_nro() {
		return reg_nro;
	}
	public void setReg_nro(int reg_nro) {
		this.reg_nro = reg_nro;
	}
	public String getCam_name() {
		return cam_name;
	}
	public void setCam_name(String cam_name) {
		this.cam_name = cam_name;
	}
	public String getCam_alias() {
		return cam_alias;
	}
	public void setCam_alias(String cam_alias) {
		this.cam_alias = cam_alias;
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
	
	public List<TrxCf_DepCam> getTrxCf_DepCamListObject(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk){
		List<TrxCf_DepCam> lista = null;
		Logger log = null;
		try {
			
			lista =   MemoryGlobal.ListTrxCf_DepCam.stream().
					filter(p -> p.getSubsystem_pk().equals(subsystem_pk)
							 && p.getTransaction_pk().equals(transaction_pk)
							 && p.getVersion_pk().equals(version_pk)
							 && p.getTip_pk().equals(tip_pk))
					.peek(Objects::requireNonNull)
					.collect(Collectors.toList());
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo TrxCf_DepCam::getTrxCf_DepCamListObject ", TypeMonitor.error, e);
		}
		return lista;
	}
	
	public Runnable getDataDepCam(){
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				
				Logger log;
				Connection conn = null;
				try {
					
					conn = DBCPDataSource.getConnection();
					String query = "SELECT * FROM TRXCF_DEP_CAM";		
					DataSetMemoryLoader<TrxCf_DepCam> loader = 
				    new DataSetMemoryLoader<TrxCf_DepCam>
					(conn, TrxCf_DepCam.class, query);
					MemoryGlobal.ListTrxCf_DepCam = loader.LoadDataClass();		
					
				} catch (SQLException e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TrxCf_DepCam::getDataDepCam() ", TypeMonitor.error, e);
					
				} catch (Exception e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TrxCf_DepCam::getDataDepCam() ", TypeMonitor.error, e);
					
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
